package fr.eurecom.jamparty.objects;

import android.os.Parcel;
import android.os.Parcelable;

public class Song implements Parcelable {
    private String name;
    private String author;
    private String uri;
    private String image_url;

    public Song(String name, String author, String uri, String image_url) {
        this.name = name;
        this.author = author;
        this.uri = uri;
        this.image_url = image_url;
    }

    public Song() {
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

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(author);
        dest.writeString(uri);
        dest.writeString(image_url);
    }

    public static final Parcelable.Creator<Song> CREATOR = new Parcelable.Creator() {
        @Override
        public Song createFromParcel(Parcel in) {
            return new Song(in);
        }

        @Override
        public Song[] newArray(int size) {
            return new Song[size];
        }
    };

    private Song(Parcel in) {
        name = in.readString();
        author = in.readString();
        uri = in.readString();
        image_url = in.readString();
    }
}
