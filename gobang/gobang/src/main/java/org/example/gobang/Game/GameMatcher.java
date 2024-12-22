package org.example.gobang.Game;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import org.example.gobang.model.MatchResponse;
import org.example.gobang.model.OnlineUser;
import org.example.gobang.model.RoomManager;
import org.example.gobang.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;

@Component
@Data
public class GameMatcher {
    //匹配队列
    private Queue<User> queue = new LinkedList<>();
    @Autowired
    private OnlineUser onlineUser;
    @Autowired
    private RoomManager roomManager;

    private ObjectMapper mapper = new ObjectMapper();

    public void enter(User user) {
        synchronized (queue) {
            queue.offer(user);
            queue.notify();
        }
        System.out.println("玩家" + user.getUsername() + "进入匹配列表！");
    }

    public void exit(User user) {
        synchronized (queue) {
            queue.remove(user);
        }
        System.out.println("玩家" + user.getUsername() + "退出匹配列表！");
    }

    public GameMatcher() {
        Thread thread = new Thread() {
            @Override
            public void run() {
                while (true) {
                    synchronized (queue) {
                        try {
                            while (queue.size() < 2) {
                                queue.wait();
                            }
                            User user1 = queue.poll();
                            User user2 = queue.poll();
                            System.out.println("玩家1：" + user1.getUsername());
                            System.out.println("玩家2：" + user2.getUsername());
                            WebSocketSession session1 = onlineUser.getFromGameCenter(user1.getUserId());
                            WebSocketSession session2 = onlineUser.getFromGameCenter(user2.getUserId());
                            if (session1 == null) {
                                queue.offer(user2);
                                return;
                            }
                            if (session2 == null) {
                                queue.offer(user1);
                                return;
                            }
                            if (session1.equals(session2)) {
                                queue.offer(user1);
                                return;
                            }

                            //进行游戏
                            GameRoom gameRoom = new GameRoom();
                            roomManager.add(gameRoom, user1.getUserId(), user2.getUserId());
                            //返回响应

                            MatchResponse response1 = new MatchResponse();
                            response1.setSuccess(true);
                            response1.setMessage("matchSuccess");
                            session1.sendMessage(new TextMessage(mapper.writeValueAsString(response1)));
                            MatchResponse response2 = new MatchResponse();
                            response2.setSuccess(true);
                            response2.setMessage("matchSuccess");
                            session2.sendMessage(new TextMessage(mapper.writeValueAsString(response2)));
                            System.out.println("匹配成功！");
                        } catch (IOException | InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        };
        thread.start();
    }
}
