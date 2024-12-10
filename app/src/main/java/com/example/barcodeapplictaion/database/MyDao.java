package com.example.barcodeapplictaion.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface MyDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(ScannedEntity scannedEntity);

    @Query("DELETE FROM scanned_table")
    void deleteAllScannedItems();

    @Query("SELECT * FROM scanned_table ORDER BY timestamp ASC")
    LiveData<List<ScannedEntity>> getAllScannedItems();

    @Query("SELECT * FROM scanned_table ORDER BY timestamp ASC")
    List<ScannedEntity> getAllScannedList();

    @Query("DELETE FROM scanned_table WHERE content LIKE :id")
    void deleteScannedItem(String id);

    /*@Query("UPDATE word_table SET word = :text WHERE word = :text")
    void updateWord(String text);
*/
    @Update
    void updateWord(ScannedEntity scannedEntity);


    /*CREATED ITEM*/
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertCreatedItem(CreatedEntity createdEntity);

    @Query("DELETE FROM created_table")
    void deleteAllCreatedItems();

    @Query("SELECT * FROM created_table ORDER BY timestamp ASC")
    LiveData<List<CreatedEntity>> getAllCreatedItems();

    @Query("DELETE FROM created_table WHERE content LIKE :id")
    void deleteCreatedItem(String id);

    @Update
    void updateCreatedItem(CreatedEntity createdEntity);


    /*FAV ITEM*/
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertFavItem(MyEntity myEntity);

    @Query("DELETE FROM fav_table")
    void deleteAllFavItems();

    @Query("SELECT * FROM fav_table ORDER BY timestamp ASC")
    List<MyEntity> getFavItems();

    @Query("SELECT * FROM fav_table ORDER BY timestamp ASC")
    LiveData<List<MyEntity>> getAllFavItems();

    @Query("SELECT * FROM fav_table WHERE content LIKE :content")
    List<MyEntity> checkFavByContentAndImage(String content);

    @Query("DELETE FROM fav_table WHERE content LIKE :content")
    void deleteFavItem(String content);

    @Update
    void updateFavItem(MyEntity myEntity);
}
