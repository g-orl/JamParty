package fr.eurecom.jamparty;

public class Suggestion {
    private Song song;
    private int votesDown;
    private String userId;

    public Suggestion(Song song, String userId) {
        this.song = song;
        this.userId = userId;
        this.votesDown = 0;
    }

    public Song getSong() {
        return song;
    }

    public void setSong(Song song) {
        this.song = song;
    }

    public int getVotesDown() {
        return votesDown;
    }

    public void setVotesDown(int votesDown) {
        this.votesDown = votesDown;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
