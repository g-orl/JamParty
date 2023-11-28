package fr.eurecom.jamparty.ui.home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class HomeViewModel extends ViewModel {

    private final MutableLiveData<Boolean> inRoom = new MutableLiveData<>();

    public HomeViewModel() {
        inRoom.setValue(false);
    }

    public LiveData<Boolean> getInRoom() {return inRoom; }

    public void setInRoom(boolean val){ this.inRoom.postValue(val);}
}