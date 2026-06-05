package com.example.PivotVC_Web.Services;

import com.example.PivotVC_Web.Entities.*;
import com.example.PivotVC_Web.Repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.*;
@Service
public class TreeService {
    @Autowired
    private CommitRepository commitRepository;
    @Autowired
    private TreeNodeRepository treeNodeRepository;
    @Autowired
    private GitObjectRepository gitObjectRepository;
    @Autowired
    private SupabaseStorageService supabaseStorageService;
    @Autowired
    private BranchRepository branchRepository;
    @Autowired
    private RepoRepository repoRepository;
    public Set<TreeEntry> buildTreeAdd(String currCommit,String filePath,String blobSha) {
        Commit commit=commitRepository.findBySha(currCommit);
        HashMap<String,String> map=new HashMap<>();
        if(commit!=null){
            String treeSha=commit.getTreeSha();
            flattenTree(treeSha,"",map);
        }
        Set<TreeEntry> entries=new HashSet<>();
        entries.add(new TreeEntry(filePath,EntryType.BLOB,blobSha));
        for(Map.Entry<String,String> e:map.entrySet()){
            entries.add(new TreeEntry(e.getKey(),EntryType.BLOB,e.getValue()));
        }
        return entries;
    }
    public TreeNode buildTree(String currCommit, Long repoId){
        //we need to fetch the tree at a particular commit
        Commit commit = commitRepository.findBySha(currCommit);
        if (commit == null) return null;
        return treeNodeRepository.findBySha(commit.getTreeSha());
    }
    public void flattenTree(String treeSha,String currPath,HashMap<String,String> map){
        TreeNode tree=treeNodeRepository.findBySha(treeSha);
        for(TreeEntry entry:tree.getEntries()){
            String fullPath=currPath.isEmpty()? entry.getName() : currPath+ "/"+entry.getName();
            if(entry.getType()== EntryType.BLOB){
                map.put(fullPath, entry.getSha());
            }
            else{
                flattenTree(entry.getSha(),fullPath,map);
            }
        }
    }
    public void flattenTreeWithContent(
            String treeSha,
            String currPath,
            Map<String,String> map,
            Long repoId
    ) {

        TreeNode tree = treeNodeRepository.findBySha(treeSha);

        if(tree == null) {
            return;
        }

        for(TreeEntry entry : tree.getEntries()) {

            String fullPath = currPath.isEmpty()
                    ? entry.getName()
                    : currPath + "/" + entry.getName();

            if(entry.getType() == EntryType.BLOB) {

                GitObject blob = gitObjectRepository.findByShaAndRepoId(entry.getSha(),repoId).orElseThrow();

                if(blob != null) {

                    byte[] data = supabaseStorageService.download(
                            blob.getStoragePath()
                    );

                    String content = new String(
                            data,
                            StandardCharsets.UTF_8
                    );

                    map.put(fullPath, content);
                }
            } else {

                flattenTreeWithContent(
                        entry.getSha(),
                        fullPath,
                        map,
                        repoId
                );
            }
        }
    }
    public void flattenTreeForIndexing(
            String treeSha,
            String currPath,
            List<FileIndexDTO> files,
            Long repoId
    ) {

        TreeNode tree = treeNodeRepository.findBySha(treeSha);

        if (tree == null) {
            return;
        }

        for (TreeEntry entry : tree.getEntries()) {

            String fullPath = currPath.isEmpty()
                    ? entry.getName()
                    : currPath + "/" + entry.getName();

            if (entry.getType() == EntryType.BLOB) {

                GitObject blob =
                        gitObjectRepository
                                .findByShaAndRepoId(entry.getSha(), repoId)
                                .orElse(null);

                if (blob == null) {
                    continue;
                }

                byte[] data =
                        supabaseStorageService.download(
                                blob.getStoragePath()
                        );

                String content =
                        new String(data, StandardCharsets.UTF_8);

                files.add(
                        new FileIndexDTO(
                                fullPath,
                                entry.getSha(),
                                content
                        )
                );

            } else {

                flattenTreeForIndexing(
                        entry.getSha(),
                        fullPath,
                        files,
                        repoId
                );
            }
        }
    }
    public Map<String,String> loadBranches(Long repoId,String branchName){
            GitRepository repo=repoRepository.findById(repoId).orElseThrow();
            Branch branch =
                    branchRepository.findByRepoAndName(
                            repo,
                            branchName
                    );

            Commit commit =
                    commitRepository.findBySha(
                            branch.getHeadCommitSha()
                    );

            Map<String,String> files = new HashMap<>();

            flattenTreeWithContent(
                    commit.getTreeSha(),
                    "",
                    files,
                    repoId
            );

            return files;
    }
    public List<FileIndexDTO> loadBranchFiles(
            Long repoId,
            String branchName
    ) {

        GitRepository repo =
                repoRepository.findById(repoId)
                        .orElseThrow();

        Branch branch =
                branchRepository.findByRepoAndName(
                        repo,
                        branchName
                );

        Commit commit =
                commitRepository.findBySha(
                        branch.getHeadCommitSha()
                );

        if (commit == null) {
            return Collections.emptyList();
        }

        List<FileIndexDTO> files = new ArrayList<>();

        flattenTreeForIndexing(
                commit.getTreeSha(),
                "",
                files,
                repoId
        );

        return files;
    }
}
