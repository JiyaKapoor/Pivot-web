package com.example.PivotVC_Web.Services;

import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@Service
public class SupabaseStorageService {

    private final RestTemplate restTemplate = new RestTemplate();

    // ---- CONFIG (move to application.properties in real apps) ----
    private final String SUPABASE_URL = "https://djhsvigtouwtlzddfqbr.storage.supabase.co";
    private final String SUPABASE_API_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImRqaHN2aWd0b3V3dGx6ZGRmcWJyIiwicm9sZSI6InNlcnZpY2Vfcm9sZSIsImlhdCI6MTc4MDQ0MzYwMSwiZXhwIjoyMDk2MDE5NjAxfQ.ATe2ialPJF5CroIhwPpqSf9qHMILHWAsGVBHAJlxOvc";
    private final String BUCKET_NAME = "git-objects";

    public void upload(String path, byte[] fileBytes) {

        String url = SUPABASE_URL + "/storage/v1/object/" + BUCKET_NAME + "/" + path;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.set("Authorization", "Bearer " + SUPABASE_API_KEY);
        headers.set("apikey", SUPABASE_API_KEY);
        headers.set("x-upsert", "true"); // overwrite if exists

        HttpEntity<byte[]> request = new HttpEntity<>(fileBytes, headers);

        ResponseEntity<String> response =
                restTemplate.exchange(url, HttpMethod.POST, request, String.class);

        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new RuntimeException("Failed to upload to Supabase: " + response.getBody());
        }
    }

    public byte[] download(String path) {

        String url = SUPABASE_URL + "/storage/v1/object/" + BUCKET_NAME + "/" + path;

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + SUPABASE_API_KEY);
        headers.set("apikey", SUPABASE_API_KEY);

        HttpEntity<Void> request = new HttpEntity<>(headers);

        ResponseEntity<byte[]> response =
                restTemplate.exchange(url, HttpMethod.GET, request, byte[].class);

        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new RuntimeException("Failed to download from Supabase");
        }

        return response.getBody();
    }

    public void delete(String path) {

        String url = SUPABASE_URL + "/storage/v1/object/" + BUCKET_NAME + "/" + path;

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + SUPABASE_API_KEY);
        headers.set("apikey", SUPABASE_API_KEY);

        HttpEntity<Void> request = new HttpEntity<>(headers);

        ResponseEntity<String> response =
                restTemplate.exchange(url, HttpMethod.DELETE, request, String.class);

        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new RuntimeException("Failed to delete from Supabase");
        }
    }
}