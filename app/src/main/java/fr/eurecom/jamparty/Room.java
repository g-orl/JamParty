package fr.eurecom.jamparty;

public class Room {

    private String id;
    private String name;
    private String hash;
    private User owner;

    public Room(String id, String name, String hash, User owner) {
        this.id = id;
        this.name = name;
        this.hash = hash;
        this.owner = owner;
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

    public User getOwner() {
        return owner;
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

    public void setOwnerId(User owner) {
        this.owner = owner;
    }

}
