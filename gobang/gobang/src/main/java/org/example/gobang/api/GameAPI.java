package org.example.gobang.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.gobang.Game.GameRoom;
import org.example.gobang.mapper.UserMapper;
import org.example.gobang.model.GameReady;
import org.example.gobang.model.*;
import org.example.gobang.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.Random;

@Component
public class GameAPI extends TextWebSocketHandler {

    private ObjectMapper mapper = new ObjectMapper();
    @Autowired
    private RoomManager roomManager;
    @Autowired
    private OnlineUser onlineUser;
    @Autowired
    private UserService userService;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        //判断用户是否登录
        GameReady resp = new GameReady();
        User user = (User) session.getAttributes().get("user");
        if (user == null) {
            resp.setSuccess(false);
            resp.setReason("用户未登录！");
            session.sendMessage(new TextMessage(mapper.writeValueAsString(resp)));
            return;
        }
        //判断用户是否进入游戏房间
        GameRoom room = roomManager.getRoomByUserId(user.getUserId());
        if (room == null) {
            resp.setSuccess(false);
            resp.setReason("用户未匹配！");
            session.sendMessage(new TextMessage(mapper.writeValueAsString(resp)));
            return;
        }
        //判断用户是否双开
        if (onlineUser.getFromGameCenter(user.getUserId()) != null ||
                onlineUser.getFromGameRoom(user.getUserId()) != null) {
            resp.setSuccess(true);
            resp.setReason("禁止重复登录！");
            resp.setMessage("repeatConnection");
            session.sendMessage(new TextMessage(mapper.writeValueAsString(resp)));
            return;
        }
        onlineUser.enterGameRoom(user.getUserId(), session);
        System.out.println("玩家" + user.getUsername() + "进入游戏房间！");
        //对room对象加锁
        synchronized (room) {
            if (room.getPlayer1() == null) {
                room.setPlayer1(user);

                //先后手随机选择
                Random random = new Random();
                // 根据随机数是奇数还是偶数
                int result = random.nextInt(2);
                if (result == 0) {
                    room.setBlackUser(user.getUserId());
                }
                System.out.println("玩家1：" + user.getUsername() + "已经准备！");
                if (room.getBlackUser() != 0) {
                    System.out.println("先手是：" + userService.getUserNameByUserId(room.getBlackUser()));
                }
                return;
            }
            if (room.getPlayer2() == null) {
                room.setPlayer2(user);
                if (room.getBlackUser() == 0) {
                    room.setBlackUser(user.getUserId());
                }
                System.out.println("玩家2：" + user.getUsername() + "已经准备！");
                if (room.getBlackUser() != 0) {
                    System.out.println("先手是：" + userService.getUserNameByUserId(room.getBlackUser()));
                }
                notice(room, room.getPlayer1(), room.getPlayer2());
                notice(room, room.getPlayer2(), room.getPlayer1());
                return;
            }
        }
        //处理异常情况
        resp.setSuccess(false);
        resp.setReason("当前房间已满！");
        session.sendMessage(new TextMessage(mapper.writeValueAsString(resp)));
    }

    //通知玩家
    private void notice(GameRoom room, User player1, User player2) throws IOException {
        GameReady resp = new GameReady();
        resp.setSuccess(true);
        resp.setReason("");
        resp.setMessage("gameReady");
        resp.setRoomId(room.getRoomId());
        resp.setPlayerId(player1.getUserId());
        resp.setOpponentId(player2.getUserId());
        resp.setIsBlack(room.getBlackUser());
        WebSocketSession session = onlineUser.getFromGameRoom(player1.getUserId());
        session.sendMessage(new TextMessage(mapper.writeValueAsString(resp)));
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        GameReady resp = new GameReady();
        User user = (User) session.getAttributes().get("user");
        if (user == null) {
            resp.setSuccess(false);
            resp.setReason("用户未登录！");
            session.sendMessage(new TextMessage(mapper.writeValueAsString(resp)));
            return;
        }
        //得到GameRoom对象
        GameRoom room = roomManager.getRoomByUserId(user.getUserId());
        //处理请求
        room.putChess(message.getPayload());
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        User user = (User) session.getAttributes().get("user");
        if (user == null) {
            return;
        }
        WebSocketSession socketSession = onlineUser.getFromGameRoom(user.getUserId());
        if (socketSession == session) {
            onlineUser.exitGameRoom(user.getUserId());
            System.out.println("玩家" + user.getUsername() + "连接游戏房间异常！");
        }
        //掉线处理
        disConnect(user);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        User user = (User) session.getAttributes().get("user");
        if (user == null) {
            return;
        }
        WebSocketSession socketSession = onlineUser.getFromGameRoom(user.getUserId());
        if (socketSession == session) {
            onlineUser.exitGameRoom(user.getUserId());
            System.out.println("玩家" + user.getUsername() + "离开游戏房间！");
        }
        disConnect(user);
    }

    //对手掉线处理
    private void disConnect(User user) throws IOException {
        GameRoom room = roomManager.getRoomByUserId(user.getUserId());
        if (room == null) {
            //游戏房间不存在
            System.out.println("当前游戏房间不存在！");
            return;
        }
        //对手信息
        User opponent = (user == room.getPlayer1()) ? room.getPlayer2() : room.getPlayer1();
        WebSocketSession socketSession = onlineUser.getFromGameRoom(opponent.getUserId());
        if (socketSession == null) {
            System.out.println("对手已调线！");
            return;
        }
        GameResponse response = new GameResponse();
        response.setPlayerId(opponent.getUserId());
        response.setWinner(opponent.getUserId());
        response.setMessage("putChess");
        socketSession.sendMessage(new TextMessage(mapper.writeValueAsString(response)));

        int winnerId = opponent.getUserId();
        int loserId = user.getUserId();
        userService.updateWinner(winnerId);
        userService.updateLoser(loserId);

        //删除游戏房间资源
        //roomManager.remove(room.getRoomId(), room.getPlayer1().getUserId(), room.getPlayer2().getUserId());
        roomManager.remove(room.getRoomId(), winnerId, loserId);
    }
}
