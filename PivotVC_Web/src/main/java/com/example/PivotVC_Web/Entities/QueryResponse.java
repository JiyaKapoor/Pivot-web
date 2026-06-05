package com.example.PivotVC_Web.Entities;



import java.util.List;

public class QueryResponse {
    private String answer;
    private List<QuerySource> sources;

    public QueryResponse() {}

    public String getAnswer() { return answer; }
    public void setAnswer(String answer) { this.answer = answer; }

    public List<QuerySource> getSources() { return sources; }
    public void setSources(List<QuerySource> sources) { this.sources = sources; }
}