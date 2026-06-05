package com.example.PivotVC_Web.Entities;


import com.fasterxml.jackson.annotation.JsonProperty;

public class QuerySource {
    @JsonProperty("file_path")
    private String filePath;

    @JsonProperty("chunk_index")
    private int chunkIndex;

    public QuerySource() {}

    public String getFilePath() { return filePath; }
    public void setFilePath(String filePath) { this.filePath = filePath; }

    public int getChunkIndex() { return chunkIndex; }
    public void setChunkIndex(int chunkIndex) { this.chunkIndex = chunkIndex; }
}