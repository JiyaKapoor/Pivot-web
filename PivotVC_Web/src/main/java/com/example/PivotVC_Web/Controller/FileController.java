package com.example.PivotVC_Web.Controller;

import com.example.PivotVC_Web.Services.SupabaseStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import tools.jackson.databind.ObjectMapper;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
@CrossOrigin("*")
@RestController
@RequestMapping("/repo")

public class FileController{
    @Autowired
    private SupabaseStorageService storageService;
    @Autowired
    private ObjectMapper objectMapper;
    @GetMapping("/{repoId}/commits/{commitHash}/files/{filename}")
    public ResponseEntity<String> getFileAtCommit(
            @PathVariable String repoId,
            @PathVariable String commitHash,
            @PathVariable String filename) throws Exception {

        byte[] commitBytes = storageService.download("repos/" + repoId + "/commit/" + commitHash);
        String[] commitLines = new String(commitBytes, StandardCharsets.UTF_8).trim().split("\n");
        String treeHash = commitLines[0].trim();

        byte[] treeBytes = storageService.download("repos/" + repoId + "/trees/" + treeHash);
        String[] treeLines = new String(treeBytes, StandardCharsets.UTF_8).trim().split("\n");

        // Format: BLOB <blobHash> <filename>
        String blobHash = null;
        for (String line : treeLines) {
            String[] parts = line.trim().split("\\s+", 3);
            if (parts.length == 3 && parts[2].trim().equals(filename)) {
                blobHash = parts[1].trim();
                break;
            }
        }

        if (blobHash == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "File '" + filename + "' not found");
        }

        byte[] content = storageService.download("repos/" + repoId + "/blob/" + blobHash);
        return ResponseEntity.ok()
                .contentType(MediaType.TEXT_PLAIN)
                .body(new String(content, StandardCharsets.UTF_8));
    }

    @GetMapping("/{repoId}/commits/{commitHash}/files")
    public ResponseEntity<List<String>> listFilesAtCommit(
            @PathVariable String repoId,
            @PathVariable String commitHash) throws Exception {

        byte[] commitBytes = storageService.download("repos/" + repoId + "/commit/" + commitHash);
        String[] commitLines = new String(commitBytes, StandardCharsets.UTF_8).trim().split("\n");
        String treeHash = commitLines[0].trim();

        byte[] treeBytes = storageService.download("repos/" + repoId + "/trees/" + treeHash);
        String[] treeLines = new String(treeBytes, StandardCharsets.UTF_8).trim().split("\n");

        List<String> filenames = new ArrayList<>();
        for (String line : treeLines) {
            String[] parts = line.trim().split("\\s+", 3);
            if (parts.length == 3) {
                filenames.add(parts[2].trim());
            }
        }

        return ResponseEntity.ok(filenames);
    }
}
