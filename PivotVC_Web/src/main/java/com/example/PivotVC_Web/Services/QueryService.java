package com.example.PivotVC_Web.Services;

import com.example.PivotVC_Web.Entities.QueryResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class QueryService {

    public QueryResponse ask(Long repoId, String branchName, String question) {

        Map<String, Object> body = new HashMap<>();
        body.put("repo_id", repoId.toString());
        body.put("branch_name", branchName);
        body.put("question", question);

        RestTemplate restTemplate = new RestTemplate();

        ResponseEntity<QueryResponse> response = restTemplate.postForEntity(
                "http://localhost:8000/ask",
                body,
                QueryResponse.class
        );

        return response.getBody();
    }
}