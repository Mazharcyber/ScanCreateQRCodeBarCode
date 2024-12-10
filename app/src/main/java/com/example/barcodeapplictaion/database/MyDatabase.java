package com.example.barcodeapplictaion.database;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;


@Database(entities = {ScannedEntity.class, CreatedEntity.class, MyEntity.class}, version = 12, exportSchema = false)
public abstract class MyDatabase extends RoomDatabase {
    private static final String DATABASE_NAME = "my_database";

    public abstract MyDao myDao();
    private static MyDatabase mDB;

    /*static Migration migration = new Migration(2, 3) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE 'word_table' ADD COLUMN 'datetime' TEXT NOT NULL DEFAULT ''");
        }
    };*/

    public static MyDatabase getDatabase(Context context) {
        if (mDB == null) {
            synchronized (MyDatabase.class){
                if (mDB == null) {
                    mDB = Room.databaseBuilder(context.getApplicationContext(),
                            MyDatabase.class, DATABASE_NAME)
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return mDB;
    }
}
