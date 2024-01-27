package fr.eurecom.jamparty.objects;

import androidx.annotation.Nullable;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import fr.eurecom.jamparty.MainActivity;

public class Room implements Parcelable {

    private String id;      // id of the room
    private String name;    // name of the room
    private String hash;    // hash of the password to join the room
    private @Nullable String ownerId; // id of the user currently owning the room
    private ArrayList<String> userIds;  // Contains the ids of the users that are currently inside the room
    private long creationTime;  // Date when the room was created
    private int maxParticipants;    // Max number of participants allowed in the room
    private boolean terminated; // Tells if the room is still active, i.e. more users can join
    private ArrayList<Suggestion> queue;
    private ArrayList<Song> played;


    public Room(String id, String name, String hash) {
        this.id = id;
        this.name = name;
        this.hash = hash;
        this.ownerId = null;
        this.userIds = new ArrayList<>();
        this.creationTime = System.currentTimeMillis();
        this.maxParticipants = 16;
        this.terminated = false;
        this.queue = new ArrayList<>();
        this.played = new ArrayList<>();
    }

    public Room() {
        this.queue = new ArrayList<>();
        this.played = new ArrayList<>();
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getHash() {
        return hash;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public ArrayList<String> getUserIds() {
        return userIds;
    }

    public long getCreationTime() {
        return creationTime;
    }

    public int getMaxParticipants() {
        return maxParticipants;
    }

    public boolean isTerminated() {
        return terminated;
    }

    public void addToQueue(Suggestion song) { this.queue.add(song); }

    public int getNumParticipants() { return userIds.size(); }

    public ArrayList<Suggestion> getQueue() { return this.queue; }

    public Suggestion nextToPlay() {
        return this.queue.isEmpty() ? null : this.queue.get(0);
    }

    public Suggestion playNext() {
        if (this.queue.isEmpty())
            return null;
        Suggestion next = this.queue.remove(0);
        this.played.add(next);
        return next;
    }

    public void setQueue(ArrayList<Suggestion> queue) { this.queue = new ArrayList<>(queue); }

    public void setPlayed(ArrayList<Song> played) { this.played = played; }

    public void pushSongsToDb() {
        FirebaseDatabase db = FirebaseDatabase.getInstance(MainActivity.DATABASE_URL);
        DatabaseReference roomsRef = db.getReference(MainActivity.ROOMS_TABLE);
        roomsRef.child(this.id+"/queue").setValue(this.queue);
        roomsRef.child(this.id+"/played").setValue(this.played);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(name);
        dest.writeString(hash);
        dest.writeString(ownerId);
        dest.writeStringList(userIds);
        dest.writeLong(creationTime);
        dest.writeInt(maxParticipants);
        dest.writeByte((byte) (terminated ? 1 : 0));
        dest.writeTypedList(queue);
        dest.writeTypedList(played);
    }

    public static final Parcelable.Creator<Room> CREATOR = new Parcelable.Creator<Room>() {
        @Override
        public Room createFromParcel(Parcel in) {
            return new Room(in);
        }

        @Override
        public Room[] newArray(int size) {
            return new Room[size];
        }
    };

    private Room(Parcel in) {
        id = in.readString();
        name = in.readString();
        hash = in.readString();
        ownerId = in.readString();
        userIds = in.createStringArrayList();
        creationTime = in.readLong();
        maxParticipants = in.readInt();
        terminated = in.readByte() != 0;
        queue = new ArrayList<>();
        in.readTypedList(queue, Suggestion.CREATOR);
        played = new ArrayList<>();
        in.readTypedList(played, Song.CREATOR);
    }

    public ArrayList<String> songsToAdd(ArrayList<String> songsUri){
        // return all songs in suggestions that are not in songsUri

        ArrayList<String> toAdd = new ArrayList<>();
            for(Suggestion suggestion: this.queue){
                if(!songsUri.contains(suggestion.getUri())){
                    toAdd.add(suggestion.getUri());
                }
            }
        return toAdd;
    }

}
