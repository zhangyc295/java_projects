package org.example.gobang.model;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import java.util.concurrent.ConcurrentHashMap;
//在线用户管理
@Component
public class OnlineUser {
    //大厅内玩家
    private ConcurrentHashMap<Integer, WebSocketSession> gameUsers = new ConcurrentHashMap<>();
    //游戏房间内玩家
    private ConcurrentHashMap<Integer, WebSocketSession> gamePlayers = new ConcurrentHashMap<>();

    public void enterGameCenter(Integer id, WebSocketSession session) {
        gameUsers.put(id, session);
    }

    public void exitGameCenter(Integer id) {
        gameUsers.remove(id);
    }

    public WebSocketSession getFromGameCenter(Integer id) {
        return gameUsers.get(id);
    }

    public void enterGameRoom(Integer id, WebSocketSession session) {
        gamePlayers.put(id, session);
    }

    public void exitGameRoom(Integer id) {
        gamePlayers.remove(id);
    }

    public WebSocketSession getFromGameRoom(Integer id) {
        return gamePlayers.get(id);
    }
}
