package tr.edu.yildiz.mecdeddinharrad;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class FirstRun {

    public static void run(Context context){
        String baseUri = "android.resource://" + context.getPackageName();
        ExamDB db = ExamDB.getInstance(context);
        QuestionDao questionDao = db.questionDao();
        UserDao userDao = db.userDao();
        User user = new User("test", "Mecdeddin", "Harrad",
                "majd.kharrat@gmail.com", Utils.encryptSHA256("123456789"),
                "+905511685495", baseUri + "/drawable/sample_avatar2");

        List<String> answers = new ArrayList<String>(5);
        answers.add("Mathew Evans");
        answers.add("Thomas Edison");
        answers.add("Hiram Maxim");
        answers.add("Nikola Tesla");
        answers.add("Bill Gates");
        Question question1 = new Question(
            new ArrayList<String>(answers), "Who was the \"first\" inventor of the light bulb",
                "test", 2, baseUri + "/drawable/bulb", "image"
        );

        answers.clear();

        answers.add("O(1)");
        answers.add("O(nlogn)");
        answers.add("O(n^2)");
        answers.add("O(n)");
        answers.add("O(logn)");
        Question question2 = new Question(
                new ArrayList<String>(answers), "What is the space complexity of Merge Sort algorithm?",
                "test", 4, null, null
        );

        answers.clear();
        answers.add("O(1)");
        answers.add("O(n)");
        answers.add("O(n^2)");
        answers.add("O(nlogn)");
        answers.add("O(n^2logn)");
        Question question3 = new Question(
                new ArrayList<String>(answers), "What is the time complexity of Insertion Sort algorithm?",
                "test", 3, baseUri + "/raw/insertion_sort", "video"
        );

        answers.clear();
        answers.add("1967");
        answers.add("1959");
        answers.add("1963");
        answers.add("1971");
        answers.add("1954");
        Question question4 = new Question(
                new ArrayList<String>(answers), "When was the original \"Pink Panther\" movie released?",
                "test", 3, baseUri + "/raw/pink_panther", "audio"
        );
        try{
            userDao.insert(user);
            questionDao.insert(question1);
            questionDao.insert(question2);
            questionDao.insert(question3);
            questionDao.insert(question4);
        } catch(Exception e){
            Log.e("Exception: ", e.toString());
        }
    }
}
