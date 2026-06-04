package com.example.PivotVC_Web.Entities;

import java.util.Map;
import java.util.Set;

public class ShaComparisonResult {
    public Map<String, String> resolved;
    public Map<String, String> deleted;
    public Set<String> needsLineMerge;

    public ShaComparisonResult(Map<String, String> resolved, Map<String, String> deleted, Set<String> needsLineMerge) {
        this.deleted=deleted;
        this.needsLineMerge=needsLineMerge;
        this.resolved=resolved;
    }
}
