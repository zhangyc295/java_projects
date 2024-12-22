package org.example.gobang.model;

import lombok.Data;

@Data
public class MatchResponse {
    private boolean success;
    private String reason;
    private String message;
}
