package fr.eurecom.jamparty.objects;

import android.os.Parcel;
import android.os.Parcelable;

public class Suggestion extends Song implements Parcelable {
    private int votesDown;
    private String userId;

    public Suggestion() {
    }


    public Suggestion(String name, String author, String uri, String imageUrl, String userId) {
        super(name, author, uri, imageUrl);
        this.userId = userId;
        this.votesDown = 0;
    }

    public int getVotesDown() {
        return votesDown;
    }

    public void setVotesDown(int votesDown) {
        this.votesDown = votesDown;
    }

    public void downvote() { votesDown++; }

    public void upvote() { votesDown--; }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(votesDown);
        dest.writeString(userId);
    }

    public static final Parcelable.Creator<Suggestion> CREATOR = new Parcelable.Creator<Suggestion>() {
        @Override
        public Suggestion createFromParcel(Parcel in) {
            return new Suggestion(in);
        }

        @Override
        public Suggestion[] newArray(int size) {
            return new Suggestion[size];
        }
    };

    private Suggestion(Parcel in) {
        votesDown = in.readInt();
        userId = in.readString();
    }
}
