package tr.edu.yildiz.mecdeddinharrad;

import java.util.List;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

@Dao
public interface UserDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    public void insert(User user);

    @Update
    public void update(User user);

    @Query("SELECT * FROM user")
    public List<User> getAll();

    @Query("SELECT * FROM user WHERE uname LIKE :uname")
    public User findByUsername(String uname);

    @Query("SELECT * FROM user WHERE uname LIKE :uname AND pword LIKE :pword")
    public User checkUserLogin(String uname, String pword);

}
