package hu.uni.miskolc.mobilprogozd2.dao;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import hu.uni.miskolc.mobilprogozd2.model.User;

@Database(entities = {User.class},version= 1)
public abstract class UserDatabase extends RoomDatabase {
    public abstract UserDAO getUserDao();
}
