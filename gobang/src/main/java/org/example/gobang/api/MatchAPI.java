package org.example.gobang.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.gobang.Game.GameMatcher;
import org.example.gobang.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

//匹配功能
@Component
public class MatchAPI extends TextWebSocketHandler {
    private ObjectMapper mapper = new ObjectMapper();

    @Autowired
    private OnlineUser onlineUser;
    @Autowired
    private GameMatcher matcher;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        //玩家上线
        try {
            User user = (User) session.getAttributes().get("user");
            if (onlineUser.getFromGameCenter(user.getUserId()) != null ||
                    onlineUser.getFromGameRoom(user.getUserId()) != null) {
                //System.out.println(onlineUser.getFromGameCenter(user.getUserId()));
                //System.out.println(onlineUser.getFromGameRoom(user.getUserId()));
                MatchResponse matchResponse = new MatchResponse();
                matchResponse.setSuccess(true);
                matchResponse.setReason("禁止多次登录！");
                matchResponse.setMessage("repeatConnection");
                session.sendMessage(new TextMessage(mapper.writeValueAsString(matchResponse)));
                //session.close();
                return;
            }
            onlineUser.enterGameCenter(user.getUserId(), session);
            System.out.println("玩家" + user.getUsername() + "上线！");
        } catch (NullPointerException e) {
            e.printStackTrace();
            MatchResponse response = new MatchResponse();
            response.setSuccess(false);
            response.setReason("用户未登录！");
            session.sendMessage(new TextMessage(mapper.writeValueAsString(response)));
        }
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        User user = (User) session.getAttributes().get("user");
        String load = message.getPayload(); //json格式
        MatchRequest request = mapper.readValue(load, MatchRequest.class);
        MatchResponse response = new MatchResponse();
        if (request.getMessage().equals("startMatch")) {
            //将用户信息放入正在匹配列表
            matcher.enter(user);
            response.setSuccess(true);
            response.setMessage("startMatch");
        } else if (request.getMessage().equals("stopMatch")) {
            //将用户信息从匹配列表移除
            matcher.exit(user);
            response.setSuccess(true);
            response.setMessage("stopMatch");
        } else {
            response.setSuccess(false);
            response.setReason("匹配请求失败！");
        }
        session.sendMessage(new TextMessage(mapper.writeValueAsString(response)));
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        //玩家下线
        try {
            User user = (User) session.getAttributes().get("user");
            WebSocketSession socketSession = onlineUser.getFromGameCenter(user.getUserId());
            if (socketSession == session) {
                onlineUser.exitGameCenter(user.getUserId());
                System.out.println("玩家" + user.getUsername() + "连接异常！");
            }
            if (matcher.getQueue().contains(user)) {
                matcher.exit(user);
            }
        } catch (NullPointerException e) {
            System.out.println("[handleTransportError]用户未登录！");
//            e.printStackTrace();
//            MatchResponse response = new MatchResponse();
//            response.setSuccess(false);
//            response.setReason("尚未登录！");
//            session.sendMessage(new TextMessage(mapper.writeValueAsString(response)));
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        try {
            User user = (User) session.getAttributes().get("user");
            WebSocketSession socketSession = onlineUser.getFromGameCenter(user.getUserId());
            if (socketSession == session) {
                onlineUser.exitGameCenter(user.getUserId());
                System.out.println("玩家" + user.getUsername() + "下线！");
            }
            if (matcher.getQueue().contains(user)) {
                matcher.exit(user);
            }
        } catch (NullPointerException e) {
            System.out.println("[afterConnectionClosed]用户未登录！");
//            e.printStackTrace();
//            MatchResponse response = new MatchResponse();
//            response.setSuccess(false);
//            response.setReason("尚未登录！");
//            session.sendMessage(new TextMessage(mapper.writeValueAsString(response)));
        }
    }
}
