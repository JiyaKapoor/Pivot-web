package com.example.PivotVC_Web.Services;

import com.example.PivotVC_Web.Entities.*;
import com.example.PivotVC_Web.Repository.CommitRepository;
import com.example.PivotVC_Web.Repository.TreeNodeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
@Service
public class TreeService {
    @Autowired
    private CommitRepository commitRepository;
    @Autowired
    private TreeNodeRepository treeNodeRepository;

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
}
