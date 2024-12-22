package org.example.gobang.model;

import lombok.Data;
//落子响应
@Data
public class GameResponse {
    private String message;
    private Integer playerId;
    private Integer winner;

    private Integer row;
    private Integer col;
}
