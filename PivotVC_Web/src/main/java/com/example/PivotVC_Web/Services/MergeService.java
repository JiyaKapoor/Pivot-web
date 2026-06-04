package com.example.PivotVC_Web.Services;

import com.example.PivotVC_Web.Entities.*;
import com.example.PivotVC_Web.Repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
@Service
public class MergeService {
    @Autowired
    UserRepository userRepository;
    @Autowired
    private TreeService treeService;
    @Autowired
    SupabaseStorageService supabaseStorageService;
    @Autowired
    private CommitRepository commitRepository;
    @Autowired
    private GitObjectRepository gitObjectRepository;
    @Autowired
    private TreeNodeRepository treeNodeRepository;
    @Autowired
    private RepoRepository repoRepository;
    @Autowired
    private BranchRepository branchRepository;
    public HashSet<String> collectAncestors(String shaA) {
        HashSet<String> parentCommits = new HashSet<>();
        Queue<String> q = new LinkedList<>();
        q.add(shaA);
        while (!q.isEmpty()) {
            String currCommit = q.poll();
            if (currCommit == null || parentCommits.contains(currCommit)) continue;
            parentCommits.add(currCommit);
            Commit commit = commitRepository.findBySha(currCommit);
            if (commit == null) continue;
            if (commit.getParentSha() != null) q.add(commit.getParentSha());
            if (commit.getSecondParentSha() != null) q.add(commit.getSecondParentSha());
        }
        return parentCommits;
    }
    public String findCommonAncestor(String shaA, String shaB) {
        HashSet<String> ancestors = collectAncestors(shaA);
        HashSet<String> visited = new HashSet<>();
        Queue<String> q = new LinkedList<>();
        q.add(shaB);
        while (!q.isEmpty()) {
            String currSha = q.poll();
            if (currSha == null || visited.contains(currSha)) continue;
            visited.add(currSha);
            if (ancestors.contains(currSha)) return currSha;
            Commit commit = commitRepository.findBySha(currSha);
            if (commit == null) continue;
            if (commit.getParentSha() != null) q.add(commit.getParentSha());
            if (commit.getSecondParentSha() != null) q.add(commit.getSecondParentSha());
        }
        return null;
    }
    //now we need to implement three-way merge
    public String threeWayMerge(String branchA, String branchB, Long repoId,Long userId) throws IOException {
        GitRepository repo=repoRepository.findById(repoId).orElseThrow();
        String shaA=branchRepository.findByRepoAndName(repo,branchA).getHeadCommitSha();
        String shaB=branchRepository.findByRepoAndName(repo,branchB).getHeadCommitSha();
        String baseSha = findCommonAncestor(shaA, shaB);
        if (shaA.equals(baseSha)) return shaB;
        if (shaB.equals(baseSha)) return shaA;
        // get flat maps
        HashMap<String, String> baseMap   = new HashMap<>();
        HashMap<String, String> sourceMap = new HashMap<>();
        HashMap<String, String> targetMap = new HashMap<>();
        if (baseSha != null) treeService.flattenTree(treeService.buildTree(baseSha, repoId).getSha(), "", baseMap);
        treeService.flattenTree(treeService.buildTree(shaA, repoId).getSha(), "", sourceMap);
        treeService.flattenTree(treeService.buildTree(shaB, repoId).getSha(), "", targetMap);

        Set<String> allPaths = new HashSet<>();
        allPaths.addAll(baseMap.keySet());
        allPaths.addAll(sourceMap.keySet());
        allPaths.addAll(targetMap.keySet());

        ShaComparisonResult result = compareBySha(allPaths, baseMap, sourceMap, targetMap);

        // final merged files: path -> byte[]
        Map<String, byte[]> mergedFiles = new HashMap<>();

        // resolved files - fetch content directly
        for (Map.Entry<String, String> e : result.resolved.entrySet()) {
            String blobPath = "repos/" + repoId + "/blob/" + e.getValue();
            mergedFiles.put(e.getKey(), supabaseStorageService.download(blobPath));
        }

        // line level merge files
        for (String path : result.needsLineMerge) {
            byte[] merged = lineLevelMerge(
                    path,
                    baseMap.get(path),
                    sourceMap.get(path),
                    targetMap.get(path),
                    repoId
            );
            mergedFiles.put(path, merged);
        }

        // deleted files are simply not added to mergedFiles

        // now persist and return new commit sha
        return persistMergeResult(mergedFiles, shaA, shaB, repoId,userId,branchB);
    }
    private ShaComparisonResult compareBySha(Set<String> allPaths,
                                             Map<String, String> baseMap,
                                             Map<String, String> sourceMap,
                                             Map<String, String> targetMap) {
        Map<String, String> resolved = new HashMap<>();
        Map<String, String> deleted = new HashMap<>();
        Set<String> needsLineMerge = new HashSet<>();

        for (String path : allPaths) {
            String base   = baseMap.get(path);
            String source = sourceMap.get(path);
            String target = targetMap.get(path);

            // both sides same → no conflict
            if (Objects.equals(source, target)) {
                if (source == null) deleted.put(path, null);
                else resolved.put(path, source);

                // file didn't exist in base (new file added)
            } else if (base == null) {
                if (source == null) resolved.put(path, target);       // only target added
                else if (target == null) resolved.put(path, source);  // only source added
                else needsLineMerge.add(path);                        // both added differently

                // only target changed (source untouched)
            } else if (Objects.equals(base, source)) {
                if (target == null) deleted.put(path, null);
                else resolved.put(path, target);

                // only source changed (target untouched)
            } else if (Objects.equals(base, target)) {
                if (source == null) deleted.put(path, null);
                else resolved.put(path, source);

                // both sides modified differently → line merge
            } else {
                needsLineMerge.add(path);
            }
        }

        return new ShaComparisonResult(resolved, deleted, needsLineMerge);
    }
    private byte[] lineLevelMerge(
            String path,
            String baseSha,
            String sourceSha,
            String targetSha,
            Long repoId) throws IOException {

        List<String> base   = readLines(baseSha, repoId);
        List<String> source = readLines(sourceSha, repoId);
        List<String> target = readLines(targetSha, repoId);

        StringBuilder merged = new StringBuilder();

        int max = Math.max(base.size(), Math.max(source.size(), target.size()));

        for (int i = 0; i < max; i++) {
            String b = getLine(base, i);
            String s = getLine(source, i);
            String t = getLine(target, i);

            if (Objects.equals(s, t)) {
                merged.append(s == null ? "" : s).append("\n");
            } else if (Objects.equals(b, s)) {
                merged.append(t == null ? "" : t).append("\n");
            } else if (Objects.equals(b, t)) {
                merged.append(s == null ? "" : s).append("\n");
            } else {
                merged.append("<<<<<<< SOURCE\n");
                if (s != null) merged.append(s).append("\n");
                merged.append("=======\n");
                if (t != null) merged.append(t).append("\n");
                merged.append(">>>>>>> TARGET\n");
            }
        }

        return merged.toString().getBytes(StandardCharsets.UTF_8);
    }

    private List<String> readLines(String sha, Long repoId) {
        if (sha == null) return new ArrayList<>();
        String blobPath = "repos/" + repoId + "/blob/" + sha;
        byte[] content  = supabaseStorageService.download(blobPath);
        return Arrays.asList(new String(content, StandardCharsets.UTF_8).split("\n"));
    }

    private String getLine(List<String> lines, int idx) {
        return idx < lines.size() ? lines.get(idx) : null;
    }
    private String persistMergeResult(Map<String, byte[]> mergedFiles,
                                      String shaA, String shaB, Long repoId,Long userId,String targetBranchName){
        // Step 1: upload each file as a blob if it doesn't exist
        Set<TreeEntry> entries = new HashSet<>();
        for (Map.Entry<String, byte[]> e : mergedFiles.entrySet()) {
            String path    = e.getKey();
            byte[] content = e.getValue();

            String blobSha  = ComputeSha.computeBlobSha(content);
            String blobPath = "repos/" + repoId + "/blob/" + blobSha;

            if (!gitObjectRepository.existsBySha(blobSha)) {
                GitObject blobObject = new GitObject(blobSha, repoId,
                        GitObject.ObjectType.BLOB, blobPath, (long) content.length);
                gitObjectRepository.save(blobObject);
                supabaseStorageService.upload(blobPath, content);
            }

            entries.add(new TreeEntry(path, EntryType.BLOB, blobSha));
        }

        // Step 2: build and save new tree
        String treeSha = ComputeSha.computeBlobSha(
                entries.stream()
                        .map(TreeEntry::toString)
                        .sorted()
                        .collect(Collectors.joining("\n"))
                        .getBytes(StandardCharsets.UTF_8)
        );
        TreeNode tree   = new TreeNode(repoId, entries.stream().toList());
        tree.setSha(treeSha);
        treeNodeRepository.save(tree);

        String treePath = "repos/" + repoId + "/tree/" + treeSha;
        GitObject treeObject = new GitObject(treeSha, repoId,
                GitObject.ObjectType.TREE, treePath, 0L);
        if (!gitObjectRepository.existsBySha(treeSha)) {
            gitObjectRepository.save(treeObject);
        }
        User author=userRepository.findById(userId).orElseThrow();
        GitRepository repository=repoRepository.findById(repoId).orElseThrow();
        // Step 3: create merge commit with two parents
        String message    = "Merge commit";
        String commitSha  = ComputeSha.computeBlobSha((treeSha + shaA + shaB + message).getBytes());
        Commit mergeCommit = new Commit();
        mergeCommit.setSha(commitSha);
        mergeCommit.setTreeSha(treeSha);
        mergeCommit.setParentSha(shaA);
        mergeCommit.setSecondParentSha(shaB);
        mergeCommit.setMessage(message);
        mergeCommit.setRepo(repository);
        mergeCommit.setAuthor(author);
        commitRepository.save(mergeCommit);
        Branch targetBranch = branchRepository.findByRepoAndName(repository,targetBranchName);
        targetBranch.setHeadCommitSha(commitSha);
        branchRepository.save(targetBranch);

        // Step 5: upload commit object to Supabase so file viewer works
        String commitContent = treeSha + "\n" + shaA + "\n" + "Merge commit\n" + author.getUsername() + "\n" + LocalDateTime.now();
        supabaseStorageService.upload("repos/" + repoId + "/commit/" + commitSha, commitContent.getBytes(StandardCharsets.UTF_8));

        // Step 6: upload tree object to Supabase
        StringBuilder sb = new StringBuilder();
        entries.stream()
                .sorted(Comparator.comparing(TreeEntry::getName))
                .forEach(entry -> sb.append("BLOB ").append(entry.getSha()).append(" ").append(entry.getName()).append("\n"));
        supabaseStorageService.upload("repos/" + repoId + "/trees/" + treeSha, sb.toString().getBytes(StandardCharsets.UTF_8));
        return commitSha;
    }
}
