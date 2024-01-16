package fr.eurecom.jamparty;

public class Suggestion extends Song{
    private int votesDown;
    private String userId;

    public Suggestion(String name, String author, String uri, String userId) {
        super(name, author, uri);
        this.userId = userId;
        this.votesDown = 0;
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
