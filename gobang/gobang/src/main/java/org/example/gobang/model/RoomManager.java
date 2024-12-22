package org.example.gobang.model;

import org.example.gobang.Game.GameRoom;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class RoomManager {
    // 房间id   房间
    private ConcurrentHashMap<String, GameRoom> rooms = new ConcurrentHashMap<>();
    // 用户id   房间id
    private ConcurrentHashMap<Integer, String> players = new ConcurrentHashMap<>();
    // 创建ConcurrentHashMap实例
    // 房间id -> 房间内所有用户id的集合
    private ConcurrentHashMap<String, Set<Integer>> roomUserMap = new ConcurrentHashMap<>();

    public void add(GameRoom room, Integer userId1, Integer userId2) {
        rooms.put(room.getRoomId(), room);
        players.put(userId1, room.getRoomId());
        players.put(userId2, room.getRoomId());
        roomUserMap.computeIfAbsent(room.getRoomId(), k -> new HashSet<>()).add(userId1);
        roomUserMap.computeIfAbsent(room.getRoomId(), k -> new HashSet<>()).add(userId2);
    }

    public void remove(String roomId, Integer userId1, Integer userId2) {
        rooms.remove(roomId);
        players.remove(userId1);
        players.remove(userId2);
        roomUserMap.computeIfPresent(roomId, (k, userSet) -> {
            userSet.remove(userId1);
            userSet.remove(userId2);
            // 如果用户集合为空，则移除该房间的条目
            if (userSet.isEmpty()) {
                return null; // 这将导致该条目从映射中移除
            }
            return userSet; // 否则返回修改后的集合
        });
    }

    //根据房间id查找房间
    public GameRoom getRoomByRoomId(String roomId) {
        return rooms.get(roomId);
    }

    //根据用户id查找房间
    public GameRoom getRoomByUserId(Integer userId) {
        String roomId = players.get(userId);
        if (roomId == null) {
            return null;
        }
        return rooms.get(roomId);
    }
    //根据用户id查找对手id
    public Integer getOpponentUserId(Integer userId) {
        // 从players映射中获取用户所在的房间ID
        String roomId = players.get(userId);
        if (roomId == null) {
            // 如果用户未关联到任何房间，则返回null
            return null;
        }

        // 从roomUserMap映射中获取该房间内的所有用户ID集合
        Set<Integer> userIdsInRoom = roomUserMap.get(roomId);
        if (userIdsInRoom == null || userIdsInRoom.size() <= 1) {
            // 如果房间内的用户集合为空或只包含一个用户，则返回null
            // 注意：理论上，这种情况不应发生，除非数据不一致
            return null;
        }

        // 遍历用户集合，找到与给定用户ID不同的另一用户ID
        for (Integer otherUserId : userIdsInRoom) {
            if (!otherUserId.equals(userId)) {
                return otherUserId;
            }
        }
        return null;
    }
}
