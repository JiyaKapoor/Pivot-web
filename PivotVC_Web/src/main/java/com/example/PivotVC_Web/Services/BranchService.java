package com.example.PivotVC_Web.Services;

import com.example.PivotVC_Web.Entities.Branch;
import com.example.PivotVC_Web.Entities.GitRepository;
import com.example.PivotVC_Web.Entities.Head;
import com.example.PivotVC_Web.Entities.RefType;
import com.example.PivotVC_Web.Repository.BranchRepository;
import com.example.PivotVC_Web.Repository.HeadRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class BranchService {
    @Autowired
    HeadRepository headRepository;
    @Autowired
    BranchRepository branchRepository;
    public void createBranch(GitRepository repo,String branchName){
        //we need to find the current commit
        if(branchRepository.findByName(branchName)!=null){
            System.out.println("Branch already exists");
            return;
        }
        String currCommit;
        Head head=headRepository.findByRepoId(repo.getId());
        if(head.getRefType()== RefType.DETACHED){
            currCommit=head.getCommitSha();
        }
        else{
            Branch branch=branchRepository.findByName(head.getBranchName());
            currCommit=branch.getHeadCommitSha();
        }
        Branch branch=new Branch(repo,branchName,currCommit);
        branchRepository.save(branch);
    }
    public void checkOut(String branchName,GitRepository gitRepository){
        Branch branch=branchRepository.findByName(branchName);
        //we need to update headd
        Head head=headRepository.findByRepoId(gitRepository.getId());
        if(head.getRefType()==RefType.DETACHED){
            head.setRefType(RefType.BRANCH);
            head.setBranchName(branchName);
            headRepository.save(head);
        }
        else{
            head.setBranchName(branchName);
            headRepository.save(head);
        }
    }
    public List<Branch> listBranches(GitRepository gitRepository){
        List<Branch> branches=branchRepository.findByRepo(gitRepository);
        return branches;
    }
}
