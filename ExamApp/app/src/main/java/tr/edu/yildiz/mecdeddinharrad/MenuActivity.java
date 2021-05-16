package tr.edu.yildiz.mecdeddinharrad;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.view.View;

public class MenuActivity extends AppCompatActivity {

    Button editProfile, addQuestion, listQuestions, createExam, examSettings;
    String currentUserName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        editProfile = (Button) findViewById(R.id.menu_editProfileBtn);
        addQuestion = (Button) findViewById(R.id.menu_addQuestionBtn);
        listQuestions = (Button) findViewById(R.id.menu_listQuestionsBtn);
        createExam = (Button) findViewById(R.id.menu_createExamBtn);
        examSettings = (Button) findViewById(R.id.menu_examSettingsBtn);
        currentUserName = getIntent().getStringExtra("uname");
    }

    public void onEditProfileClick(View view){
        Intent intent = new Intent(MenuActivity.this, EditProfileActivity.class);
        intent.putExtra("uname", currentUserName);
        startActivity(intent);
    }

    public void onAddQuestionClick(View view){
        Intent intent = new Intent(MenuActivity.this, AddQuestionActivity.class);
        intent.putExtra("uname", currentUserName);
        startActivity(intent);
    }

    public void onListQuestionsClick(View view){
        Intent intent = new Intent(MenuActivity.this, ListQuestionsActivity.class);
        intent.putExtra("uname", currentUserName);
        startActivity(intent);
    }

    public void onCreateExamClick(View view){
        Intent intent = new Intent(MenuActivity.this, ListQuestionsActivity.class);
        intent.putExtra("uname", currentUserName);
        intent.putExtra("selectionMode", true);
        startActivity(intent);
    }

    public void onExamSettingsClick(View view){
        Intent intent = new Intent(MenuActivity.this, ExamSettingsActivity.class);
        intent.putExtra("uname", currentUserName);
        startActivity(intent);
    }
}