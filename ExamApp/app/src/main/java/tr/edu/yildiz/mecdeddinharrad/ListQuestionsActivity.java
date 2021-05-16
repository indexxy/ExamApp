package tr.edu.yildiz.mecdeddinharrad;


import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.appbar.AppBarLayout;

import java.io.File;

public class ListQuestionsActivity extends AppCompatActivity{
    RecyclerAdapter recyclerAdapter;
    RecyclerView recyclerView;
    EditText diff, points, duration;
    QuestionDao questionDao;
    AppBarLayout appBarLayout;
    String currentUsername;
    boolean selectionMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_questions);
        diff = (EditText) findViewById(R.id.listQuestions_diffEditText);
        points = (EditText) findViewById(R.id.listQuestions_pointsEditText);
        duration = (EditText) findViewById(R.id.listQuestions_durationEditText);
        appBarLayout = (AppBarLayout)findViewById(R.id.appBarLayout);
        SharedPreferences prefs = getSharedPreferences(ExamSettingsActivity.PREFS_NAME, MODE_PRIVATE);
        diff.setText(String.valueOf(prefs.getInt("DIFF", 5)));
        points.setText(String.valueOf(prefs.getInt("POINTS", 10)));
        duration.setText(String.valueOf(prefs.getInt("DURATION", 60)));
        currentUsername = getIntent().getStringExtra("uname");
        selectionMode = getIntent().getBooleanExtra("selectionMode", false);
        questionDao = ExamDB.getInstance(getApplicationContext()).questionDao();
        recyclerAdapter = new RecyclerAdapter(selectionMode);
        recyclerAdapter.setQuestionList(questionDao.getQuestions(currentUsername));

        recyclerAdapter.setOnDeleteClick(new RecyclerAdapter.OnDeleteClickListener() {
            @Override
            public void onDeleteClick(int position) {

                AlertDialog.Builder builder1 = new AlertDialog.Builder(ListQuestionsActivity.this);
                builder1.setMessage("Are you sure you want to delete this question?");
                builder1.setCancelable(true);

                builder1.setPositiveButton(
                        "Delete",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Question question = recyclerAdapter.getQuestionList().get(position);
                                if (question.getMediaUri() != null) {
                                    File mediaFile = new File(question.getMediaUri());
                                    mediaFile.delete();
                                }
                                questionDao.delete(question);
                                recyclerAdapter.removeItem(position);
                                recyclerAdapter.notifyDataSetChanged();
                            }
                        });

                builder1.setNegativeButton(
                        "Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

                AlertDialog alert11 = builder1.create();
                alert11.show();
            }
        });
        recyclerAdapter.setOnEditClick(new RecyclerAdapter.OnEditClickListener(){
            @Override
            public void onEditClick(int position){
                Intent intent = new Intent(ListQuestionsActivity.this, AddQuestionActivity.class);
                intent.putExtra("questionId", recyclerAdapter.getQuestionList().get(position).getId());
                intent.putExtra("position", position);
                startActivityForResult(intent, 33);


            }
        });

        if (selectionMode){
            appBarLayout.setVisibility(View.VISIBLE);
        }

        recyclerView = findViewById(R.id.listQuestions_recyclerView);
        RecyclerView.LayoutManager lm = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(lm);
        recyclerView.setAdapter(recyclerAdapter);
    }


    protected void onActivityResult(int requestCode, int resultData, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultData, data);
        if (requestCode == 33 && resultData == RESULT_OK && data != null) {
            int position = data.getIntExtra("position", 0);
            if (recyclerAdapter.getQuestionList().get(position).getMediaUri() != null){
                recyclerView.getLayoutManager().findViewByPosition(position)
                        .findViewById(R.id.questionCard_showMedia).setVisibility(View.VISIBLE);
            }
            recyclerAdapter.setQuestionList(questionDao.getQuestions(currentUsername));
            recyclerAdapter.notifyDataSetChanged();
        }
    }

    @Override
    protected void onResume(){
        if (recyclerAdapter != null){
            recyclerAdapter.setQuestionList(questionDao.getQuestions(currentUsername));
            recyclerAdapter.notifyDataSetChanged();
        }
        super.onResume();
    }

    public void onShareClick(View view){
        if (!Utils.isEmpty(points) && !Utils.isEmpty(duration) && !Utils.isEmpty(diff)){
            int pointsValue = Integer.parseInt(points.getText().toString());
            int diffValue = Integer.parseInt(diff.getText().toString());
            int durationValue = Integer.parseInt(duration.getText().toString());
            if (diffValue > 5){
                diffValue = 5;
            }
            else if (diffValue < 2){
                diffValue = 2;
            }

            Intent intent = new Intent(android.content.Intent.ACTION_SEND);
            String messageData = Utils.constructExamString(
                    recyclerAdapter.getSelectedQuestions(),
                    durationValue, pointsValue, diffValue);

            intent.setType("text/plain");
            intent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Test");
            intent.putExtra(android.content.Intent.EXTRA_TEXT, messageData);
            startActivity(Intent.createChooser(intent, "Test"));
        }
        else{
            Toast toast = Toast.makeText(this, "Please fill in all fields!", Toast.LENGTH_SHORT);
            toast.show();
        }
    }

}