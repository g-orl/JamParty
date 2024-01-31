package fr.eurecom.jamparty.objects;

import android.util.Log;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.TimerTask;

import fr.eurecom.jamparty.MainActivity;
import fr.eurecom.jamparty.SpotifyApiPostTask;

public class SongTimer extends TimerTask {
    Room room;
    Suggestion suggestion;
    public SongTimer(Room room, Suggestion suggestion) {
        this.room = room;
        this.suggestion = suggestion;
    }
    @Override
    public void run() {
        // need to add the song to spotify if no dislikes are added
        if(room.getOwnerId().compareTo(MainActivity.USER_ID) == 0){
            if(suggestion.getVotesDown() == 0){
                // song did not get downvoted so can add to the spotify queue
                room.addPlayedSong(suggestion);
                new SpotifyApiPostTask(res -> {}).execute(URLEncoder.encode(suggestion.getUri(), StandardCharsets.UTF_8));
            }
            // owner also removes the song from the suggestion queue and puts it in the played songs if it was added
            room.removeFromQueue(suggestion);
            // MainActivity.ROOMS_REF.child(room.getId()).setValue(room);
            room.pushSongsToDb();
            Log.i("TIMER", "Completed timer for " + suggestion.getName()+" | "+suggestion.getAuthor());
        }
    }
}
