package com.example.wearme.model;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.wearme.MyApplication;

@Database(entities = {Item.class}, version = 1)
abstract class AppLocalDBRepository extends RoomDatabase {
    public abstract ItemDao itemDao();
}

public class AppLocalDB {
    static public AppLocalDBRepository db =
            Room.databaseBuilder(MyApplication.context,
                    AppLocalDBRepository.class,
                    "dbFileName.db").fallbackToDestructiveMigration().build();
}
