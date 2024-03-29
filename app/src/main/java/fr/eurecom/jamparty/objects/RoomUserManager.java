package fr.eurecom.jamparty.objects;

public class RoomUserManager {
    public static int OPERATION_OK = 0;
    public static int ROOM_FULL = 1;
    public static int ROOM_TERMINATED = 2;
    public static int userJoinRoom(User user, Room room, boolean owner) {
        if(user == null) throw new NullPointerException("user is null");
        if(room == null) throw new NullPointerException("room is null");
        if(room.isTerminated())
            return ROOM_TERMINATED;
        if(room.countParticipants() == room.getMaxParticipants())
            return ROOM_FULL;
        String roomId = room.getId();
        String userId = user.getId();
        user.setCurrentRoomId(roomId);
        if(owner) {
            if(room.getOwnerId() != null && !room.getOwnerId().equals(user.getId()))
                throw new IllegalStateException("you can't be owner of a room that is already owned");
            user.setOwnedRoomId(roomId);
            room.setOwnerId(userId);
        } else {
            user.setOwnedRoomId(null);
        }
        user.addRoomToHistory(roomId);
        room.addUser(userId);

        room.pushUsersToDb();
        user.pushToDb();
        return OPERATION_OK;
    }

    public static void userExitRoom(User user, Room room) {
        if(user == null) throw new NullPointerException("user is null");
        if(room == null) throw new NullPointerException("room is null");
        String roomId = room.getId();
        String userId = user.getId();
        user.setOwnedRoomId(null);
        user.setCurrentRoomId(null);
        room.getUserIds().remove(userId);   // check if this works
        if (RoomUserManager.userOwnsRoom(user, room)) {
            room.setTerminated(true);
            room.pushTerminatedToDb();
        }
        room.pushUsersToDb();
        user.pushToDb();
    }

    public static boolean userOwnsRoom(User user, Room room) {
        return user.getId().equals(room.getOwnerId());
    }


}
