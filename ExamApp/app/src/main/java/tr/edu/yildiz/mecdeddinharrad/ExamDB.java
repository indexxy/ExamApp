package tr.edu.yildiz.mecdeddinharrad;

import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.Database;
import android.content.Context;
import androidx.room.TypeConverters;


@Database(entities = {User.class, Question.class}, version = 1)
@TypeConverters({Converters.class})
public abstract class ExamDB extends RoomDatabase {

    private static ExamDB instance;
    public abstract UserDao userDao();
    public abstract QuestionDao questionDao();


    public static synchronized  ExamDB getInstance(Context context){
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(), ExamDB.class,
                    "examDB.db").fallbackToDestructiveMigration().allowMainThreadQueries().build();
        }

        return instance;
    }
}
