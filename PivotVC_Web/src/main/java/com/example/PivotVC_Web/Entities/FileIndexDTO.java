package com.example.PivotVC_Web.Entities;

public class FileIndexDTO {

    private String path;
    private String blobSha;
    private String content;

    public FileIndexDTO() {}

    public FileIndexDTO(String path, String blobSha, String content) {
        this.path = path;
        this.blobSha = blobSha;
        this.content = content;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getBlobSha() {
        return blobSha;
    }

    public void setBlobSha(String blobSha) {
        this.blobSha = blobSha;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}