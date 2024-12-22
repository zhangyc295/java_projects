package org.example.gobang.model;

import lombok.Data;

@Data
public class User {
    private Integer userId;
    private String username;
    private String password;
    private Integer score;
    private Integer totalGame;
    private Integer winGame;
}
