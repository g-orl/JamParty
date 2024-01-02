package fr.eurecom.jamparty;

import kotlin.NotImplementedError;

public class RoomUserManager {
    public static void userJoinRoom(User user, Room room, boolean owner) {
        if(user == null) throw new NullPointerException("user is null");
        if(room == null) throw new NullPointerException("room is null");
        String roomId = room.getId();
        String userId = user.getId();
        user.setCurrentRoomId(roomId);
        if(owner) {
            if(room.getOwnerId() != null) throw new IllegalStateException("you can't be owner of a room that is already owned");
            user.setOwnedRoomId(roomId);
            room.setOwnerId(userId);
        } else {
            user.setOwnedRoomId(null);
        }
        user.getRoomIdsHistory().add(roomId);
        room.getUserIds().add(userId);
    }

    public static void userExitRoom(User user, Room room) {
        throw new NotImplementedError();
    }
}
