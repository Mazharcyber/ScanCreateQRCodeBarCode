package com.example.barcodeapplictaion.database;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

public class MyViewModel extends AndroidViewModel {
    private MyRepository myRepository;
    private LiveData<List<ScannedEntity>> scannedList;
    private LiveData<List<CreatedEntity>> createdList;
    private LiveData<List<MyEntity>> favList;

    public MyViewModel(@NonNull Application application) {
        super(application);
        myRepository = new MyRepository(application);
        scannedList = myRepository.getScannedList();
        createdList = myRepository.getCreatedItemsList();
        favList = myRepository.getFavoriteList();
    }

    //SCANNED ITEMS
    public LiveData<List<ScannedEntity>> getScannedItemsList() {
        return scannedList;
    }

    public void insertScannedItem(ScannedEntity scannedEntity) {
        myRepository.insertScannedItem(scannedEntity);
    }

    public void deleteScannedItem(String id) {
        myRepository.deleteScannedItem(id);
    }

    /*public void updateText(ScannedEntity scannedEntity) {
        myRepository.updateWord(scannedEntity);
    }*/



    //CREATED ITEMS
    public LiveData<List<CreatedEntity>> getCreatedItemsList() {
        return createdList;
    }

    public void insertCreatedItem(CreatedEntity createdEntity) {
        myRepository.insertCreatedItem(createdEntity);
    }

    public void deleteCreatedItem(String id) {
        myRepository.deleteCreatedItem(id);
    }

    /*public void updateText(ScannedEntity scannedEntity) {
        myRepository.updateWord(scannedEntity);
    }*/



    //FAV ITEMS
    public LiveData<List<MyEntity>> getFavoriteList() {
        return favList;
    }

    public void insertFavoriteItem(MyEntity myEntity) {
        myRepository.insertFavoriteItem(myEntity);
    }

    public void deleteFavoriteItem(String id) {
        myRepository.deleteFavoriteItem(id);
    }

    /*public void updateText(ScannedEntity scannedEntity) {
        myRepository.updateWord(scannedEntity);
    }*/

}
