package fr.eurecom.jamparty;

import java.util.ArrayList;
import java.util.LinkedList;

public class Room {

    private String id;      // id of the room
    private String name;    // name of the room
    private String hash;    // hash of the password to join the room
    private String ownerId; // id of the user currently owning the room
    private ArrayList<String> userIds;  // Contains the ids of the users that are currently inside the room
    private long creationTime;  // Date when the room was created
    private int maxParticipants;    // Max number of participants allowed in the room
    private boolean terminated; // Tells if the room is still active, i.e. more users can join
    private LinkedList<Song> queue;
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
        this.queue = new LinkedList<>();
        this.played = new ArrayList<>();
    }

    public Room() {

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

    public void addToQueue(Song song) { this.queue.push(song); }

    public LinkedList<Song> getQueue() { return this.queue; }

    public Song nextToPlay() {
        return this.queue.isEmpty() ? null : this.queue.get(0);
    }

    public Song playNext() {
        if (this.queue.isEmpty())
            return null;
        Song next = this.queue.removeFirst();
        this.played.add(next);
        return next;
    }

}
