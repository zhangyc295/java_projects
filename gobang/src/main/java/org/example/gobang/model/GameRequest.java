package org.example.gobang.model;

import lombok.Data;
//落子请求
@Data
public class GameRequest {
    private String message;
    public Integer playerId;
    private Integer row;
    private Integer col;
}
