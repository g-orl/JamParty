package fr.eurecom.jamparty.objects;

import androidx.annotation.Nullable;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import fr.eurecom.jamparty.MainActivity;

public class User {

    private String id;  // id of the user
    private double latitude;    // last known latitude of the user
    private double longitude;   // last known longitude of the user
    @Nullable private String currentRoomId; // id of the room in which the user is at the moment
    @Nullable private String ownedRoomId;   // id of the room that the user owns at the moment
    private ArrayList<String> roomIdsHistory;   // complete history of all the rooms that have been joined by the user, with duplicates

    public User(String id, double latitude, double longitude) {
        this.id = id;
        this.latitude = latitude;
        this.longitude = longitude;
        this.currentRoomId = null;
        this.ownedRoomId = null;
        this.roomIdsHistory = new ArrayList<>();
    }

    public User() {
        this.roomIdsHistory = new ArrayList<>();
    }

    public String getId() {
        return id;
    }
    public void setId(String id) { this.id = id; }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) { this.latitude = latitude; }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) { this.longitude = longitude; }

    public String getCurrentRoomId() { return currentRoomId; }
    public void setCurrentRoomId(@Nullable String roomId) { this.currentRoomId = roomId; }

    public ArrayList<String> getRoomIdsHistory() { return roomIdsHistory; }
    public void setRoomIdsHistory(ArrayList<String> roomIdsHistory) {
        this.roomIdsHistory = roomIdsHistory;
    }
    public void addRoomToHistory(String roomId) {
        if (roomIdsHistory.isEmpty() || !roomIdsHistory.get(roomIdsHistory.size()-1).equals(roomId)) {
            roomIdsHistory.add(roomId);
        }
    }

    public String getOwnedRoomId() { return ownedRoomId; }
    public void setOwnedRoomId(@Nullable String roomId) {
        this.ownedRoomId = roomId;
    }

    public boolean equals(User other) {
        return id == other.id;
    }

    public void pushLocationToDb() {
        MainActivity.USERS_REF.child(id+"/latitude").setValue(latitude);
        MainActivity.USERS_REF.child(id+"/longitude").setValue(longitude);
    }

    public void pushToDb() {
        MainActivity.USERS_REF.child(id).setValue(this);
    }
}
