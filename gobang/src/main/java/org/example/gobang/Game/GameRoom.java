package org.example.gobang.Game;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import org.example.gobang.GobangApplication;
import org.example.gobang.model.*;
import org.example.gobang.service.UserService;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.UUID;

@Data
public class GameRoom {
    //唯一标识 UUID
    private String roomId;

    private User player1;
    private User player2;
    private int blackUser;

    private ObjectMapper mapper = new ObjectMapper();
    //@Autowired
    private OnlineUser onlineUser;
    //@Autowired
    private RoomManager roomManager;
    //@Autowired
    private UserService userService;
    //0表示未落子
    //1表示player1
    //2表示player2
    private int[][] board = new int[15][15];

    public GameRoom() {
        this.roomId = UUID.randomUUID().toString();//随机唯一id
        //手动注入
        onlineUser = GobangApplication.applicationContext.getBean(OnlineUser.class);
        roomManager = GobangApplication.applicationContext.getBean(RoomManager.class);
        userService = GobangApplication.applicationContext.getBean(UserService.class);
    }

    //落子请求
    public void putChess(String payload) throws IOException {
        GameRequest request = mapper.readValue(payload, GameRequest.class);
        GameResponse response = new GameResponse();
        int chess = (request.getPlayerId() == player1.getUserId()) ? 1 : 2;
        int row = request.getRow();
        int col = request.getCol();
        if (board[row][col] != 0) {
            System.out.println("当前位置无法落子！");
            return;
        }
        board[row][col] = chess;
        printBoard();
        //胜负判定
        int winner = checkWin(row, col, chess);
        //返回
        response.setMessage("putChess");
        response.setPlayerId(request.getPlayerId());
        response.setRow(row);
        response.setCol(col);
        response.setWinner(winner);

        WebSocketSession session1 = onlineUser.getFromGameRoom(player1.getUserId());
        WebSocketSession session2 = onlineUser.getFromGameRoom(player2.getUserId());
        //掉线处理
        if (session1 == null) {
            response.setWinner(player2.getUserId());
            System.out.println("玩家" + player1.getUsername() + "掉线！");
        }
        if (session2 == null) {
            response.setWinner(player1.getUserId());
            System.out.println("玩家" + player2.getUsername() + "掉线！");
        }
        String json = mapper.writeValueAsString(response);
        if (session1 != null) {
            session1.sendMessage(new TextMessage(json));
        }
        if (session2 != null) {
            session2.sendMessage(new TextMessage(json));
        }
        if (response.getWinner() != 0) {
            System.out.println("游戏结束！获胜方是" + userService.getUserNameByUserId(response.getWinner()));
            int winnerId = response.getWinner();
            int loserId = response.getWinner() == player1.getUserId() ? player2.getUserId() : player1.getUserId();
            userService.updateWinner(winnerId);
            userService.updateLoser(loserId);
            roomManager.remove(roomId, player1.getUserId(), player2.getUserId());
        }
    }

    private void printBoard() {
        System.out.println("===============");
        System.out.println("RoomId:" + roomId + " [棋盘信息]");
        for (int i = 0; i < 15; i++) {
            for (int j = 0; j < 15; j++) {
                System.out.print(board[i][j] + "  ");
            }
            System.out.println();
        }
        System.out.println("===============");
    }

    //判断
    private int checkWin(int row, int col, int chess) {
        //检查行
        for (int c = col - 4; c <= col; c++) {
            try {
                if (board[row][c] == chess
                        && board[row][c + 1] == chess
                        && board[row][c + 2] == chess
                        && board[row][c + 3] == chess
                        && board[row][c + 4] == chess) {
                    return chess == 1 ? player1.getUserId() : player2.getUserId();
                }
            } catch (ArrayIndexOutOfBoundsException e) {
                continue;
            }
        }

        //检查列
        for (int r = row - 4; r <= row; r++) {
            try {
                if (board[r][col] == chess
                        && board[r + 1][col] == chess
                        && board[r + 2][col] == chess
                        && board[r + 3][col] == chess
                        && board[r + 4][col] == chess) {
                    return chess == 1 ? player1.getUserId() : player2.getUserId();
                }
            } catch (ArrayIndexOutOfBoundsException e) {
                continue;
            }
        }

        //检查主对角线
        //同步变化
        for (int r = row - 4, c = col - 4; r <= row && c <= col; r++, c++) {
            try {
                if (board[r][c] == chess
                        && board[r + 1][c + 1] == chess
                        && board[r + 2][c + 2] == chess
                        && board[r + 3][c + 3] == chess
                        && board[r + 4][c + 4] == chess) {
                    return chess == 1 ? player1.getUserId() : player2.getUserId();
                }
            } catch (ArrayIndexOutOfBoundsException e) {
                continue;
            }
        }

        //检查次对角线
        for (int r = row - 4, c = col + 4; r <= row && c >= col; r++, c--) {
            try {
                if (board[r][c] == chess
                        && board[r + 1][c - 1] == chess
                        && board[r + 2][c - 2] == chess
                        && board[r + 3][c - 3] == chess
                        && board[r + 4][c - 4] == chess) {
                    return chess == 1 ? player1.getUserId() : player2.getUserId();
                }
            } catch (ArrayIndexOutOfBoundsException e) {
                continue;
            }
        }
        //胜负未分
        return 0;
    }
}