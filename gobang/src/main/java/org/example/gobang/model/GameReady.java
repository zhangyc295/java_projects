package org.example.gobang.model;

import lombok.Data;
//连接至游戏房间
@Data
public class GameReady {
    private String message;
    private boolean success;
    private String reason;

    private String roomId;
    private Integer playerId;
    private Integer opponentId;
    private Integer isBlack;
}
