package tr.edu.yildiz.mecdeddinharrad;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface QuestionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public void insert(Question question);


    @Query("SELECT * FROM question WHERE owner LIKE :ownerUname ")
    public List<Question> getQuestions(String ownerUname);

    @Query("SELECT * FROM question WHERE id == :id")
    public Question getQuestionById(int id);

    @Delete
    public void delete(Question question);

}
