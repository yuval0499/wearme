package com.example.wearme.model;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface ItemDao {
    @Query("select * from Item")
    LiveData<List<Item>> getAllItems();

    @Query("select * from Item where ownBy = :currUser")
    List<Item> getCurrentUserItems(String currUser);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Item... items);

    @Query("Delete from Item where id = :id")
    void delete(String id);

    @Query("delete from Item")
    void deleteAll();

    @Query("select * from Item where id = :id")
    LiveData<Item> getItem(String id);
}
