package fr.eurecom.jamparty.ui.fragments;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class RoomViewModel extends ViewModel {
    private final MutableLiveData<String> roomName = new MutableLiveData<>();

    public RoomViewModel() {

        roomName.setValue("None");
    }

    public LiveData<String> getRoomName() {return roomName; }
    public void setRoomName(String val){ this.roomName.postValue(val);}
}
