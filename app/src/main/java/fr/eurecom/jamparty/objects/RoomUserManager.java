package fr.eurecom.jamparty.objects;

import fr.eurecom.jamparty.objects.Room;
import fr.eurecom.jamparty.objects.User;

public class RoomUserManager {
    public static void userJoinRoom(User user, Room room, boolean owner) {
        if(user == null) throw new NullPointerException("user is null");
        if(room == null) throw new NullPointerException("room is null");
        if(room.isTerminated()) throw new RuntimeException("user can't join a terminated room");
        if(room.getNumParticipants() == room.getMaxParticipants()) throw new RuntimeException("user can't join a full room");
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
        if(user == null) throw new NullPointerException("user is null");
        if(room == null) throw new NullPointerException("room is null");
        String roomId = room.getId();
        String userId = user.getId();
        user.setOwnedRoomId(null);
        user.setCurrentRoomId(null);
        room.getUserIds().remove(userId);   // check if this works
        if(room.getOwnerId().equals(userId)) {
            // should happen only if he is the last user in the room
            // terminate room

            /* if(room.getNumParticipants() == 0) {

            } else {
                String newOwnerId = room.getUserIds().get(0);
                User newOwner = User.get(newOwnerId);
                if(newOwner == null) {
                    throw new RuntimeException("CAN'T REASSIGN OWNER");
                }
                room.setOwnerId(newOwnerId);

            }
            room.setOwnerId(null);
            room.reassignOwner(); */
        }
    }


}
