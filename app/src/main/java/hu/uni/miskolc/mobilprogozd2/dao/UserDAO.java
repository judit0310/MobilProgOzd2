package hu.uni.miskolc.mobilprogozd2.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

import hu.uni.miskolc.mobilprogozd2.model.User;

@Dao
public interface UserDAO {
    @Query("SELECT * from user")
    List<User> getAll();

    @Query("SELECT * from user WHERE last_name=:lastName and first_name=:firstName")
    List<User> findByName(String lastName, String firstName);

    @Query("SELECT * from user WHERE username=:username")
    User findByUsername(String username);

    @Insert
    void insert(User user);

    @Delete
    void delete(User user);
}
