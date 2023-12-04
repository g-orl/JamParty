package fr.eurecom.jamparty.ui.home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class HomeViewModel extends ViewModel {

    private final MutableLiveData<Boolean> inRoom = new MutableLiveData<>();
    private final MutableLiveData<String> roomName = new MutableLiveData<>();

    public HomeViewModel() {

        inRoom.setValue(false);
        roomName.setValue("None");
    }

    public LiveData<Boolean> getInRoom() {return inRoom; }

    public void setInRoom(boolean val){ this.inRoom.postValue(val);}

    public LiveData<String> getRoomName() {return roomName; }
    public void setRoomName(String val){ this.roomName.postValue(val);}
}