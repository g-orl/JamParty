package fr.eurecom.jamparty;

public class Song {
    private String name;
    private String author;
    private String uri;

    public Song(String name, String author, String uri) {
        this.name = name;
        this.author = author;
        this.uri = uri;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }
}
