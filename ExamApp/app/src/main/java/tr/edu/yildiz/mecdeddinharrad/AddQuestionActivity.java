package tr.edu.yildiz.mecdeddinharrad;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.VideoView;

import java.io.IOException;
import java.util.UUID;
import java.util.ArrayList;
import java.util.List;

public class AddQuestionActivity extends AppCompatActivity {

    EditText description, ans1, ans2, ans3, ans4, ans5;
    Spinner mediaTypeSpinner, correctAnswerSpinner;
    Button chooseFileBtn;
    String mediaUri = null;
    QuestionDao questionDao;
    LinearLayout mainLinearLayout;
    String currentUsername;
    String mediaType = null;
    MediaPlayer mediaPlayer;
    Button play, pause, stop;
    boolean mediaChosen = false;
    private final static int REQUEST_CODE = 1;
    private Integer questionId = null;
    private Question question = null;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_question);
        play = (Button) findViewById(R.id.addQuestion_play);
        pause = (Button) findViewById(R.id.addQuestion_pause);
        stop = (Button) findViewById(R.id.addQuestion_stop);
        currentUsername = getIntent().getStringExtra("uname");
        mainLinearLayout = (LinearLayout) findViewById(R.id.addQuestion_mainLinearLayout);
        description = (EditText) findViewById(R.id.addQuestion_descriptionEditText);
        ans1 = (EditText) findViewById(R.id.addQuestion_answerEditText);
        ans2 = (EditText) findViewById(R.id.addQuestion_answerEditText2);
        ans3 = (EditText) findViewById(R.id.addQuestion_answerEditText3);
        ans4 = (EditText) findViewById(R.id.addQuestion_answerEditText4);
        ans5 = (EditText) findViewById(R.id.addQuestion_answerEditText5);
        mediaTypeSpinner = (Spinner) findViewById(R.id.addQuestion_mediaTypeSpinner);
        chooseFileBtn = (Button) findViewById(R.id.addQuestion_chooseFile);
        chooseFileBtn.setEnabled(false);
        mediaTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                String mediaType = mediaTypeSpinner.getSelectedItem().toString();
                chooseFileBtn.setEnabled(Utils.mediaTypes.contains(mediaType));
                mediaChosen = Utils.mediaTypes.contains(mediaType);
                if (!mediaChosen){
                    mediaUri = null;
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                chooseFileBtn.setEnabled(false);
                mediaChosen = false;
            }
        });
        correctAnswerSpinner = (Spinner) findViewById(R.id.addQuestion_correctSpinner);
        ExamDB db = ExamDB.getInstance(this.getApplicationContext());
        questionDao = db.questionDao();
        setAudioBtnListeners();
        if (getIntent().hasExtra("questionId")){
            questionId = getIntent().getIntExtra("questionId", -1);
            populateFields();
        }
    }

    public void onSaveClick(View view){
        if (isValid()) {
            List<String> answers = new ArrayList<String>(5);
            answers.add(ans1.getText().toString());
            answers.add(ans2.getText().toString());
            answers.add(ans3.getText().toString());
            answers.add(ans4.getText().toString());
            answers.add(ans5.getText().toString());
            try {
                if (mediaChosen && mediaUri != null) {
                    mediaType = mediaTypeSpinner.getSelectedItem().toString();
                    String folder = "media/" + mediaType;
                    String fileName = currentUsername + "_" + UUID.randomUUID().toString().replace("-", "");
                    mediaUri = Utils.copyFile(getApplicationContext(), Uri.parse(mediaUri), folder, fileName).toString();
                }
            } catch (IOException e) {
                Log.d("Exception: ", e.toString());
            }
            if (question == null) {
                question = new Question(
                        answers, description.getText().toString(),
                        currentUsername,
                        Integer.parseInt(correctAnswerSpinner.getSelectedItem().toString()),
                        mediaUri,
                        mediaType
                );
                questionDao.insert(question);
                Toast.makeText(getApplicationContext(), "Question was added successfully", Toast.LENGTH_SHORT).show();
            }
            else{
                question.setDescription(description.getText().toString());
                question.setAnswer(answers);
                question.setCorrectAnswer(Integer.parseInt(correctAnswerSpinner.getSelectedItem().toString()));
                question.setMediaUri(mediaUri);
                if (mediaUri != null){
                    question.setMediaType(mediaType);
                }
                else{
                    question.setMediaType(null);
                }
                questionDao.insert(question);
                Toast.makeText(getApplicationContext(), "Changes were saved successfully", Toast.LENGTH_SHORT).show();
            }
            finish();
        }
    }

    public void onChooseFileClick(View view){
        String mediaType = mediaTypeSpinner.getSelectedItem().toString();
        Intent intent = new Intent();
        intent.setType(mediaType + "/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(
                Intent.createChooser(intent, "Pick a media file"), REQUEST_CODE
        );
    }

    @Override
    protected void onActivityResult(int requestCode, int resultData, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultData, data);
        if (requestCode == REQUEST_CODE && resultData == RESULT_OK && data != null) {
            Uri mediaData = data.getData();
            mediaUri = mediaData.toString();
            mediaChosen = true;
            String mediaType = mediaTypeSpinner.getSelectedItem().toString();
            cleanLastViews();
            populateMedia(mediaType, mediaData);
        }
        else {
            mediaUri = null;
            mediaChosen = false;
            cleanLastViews();
        }
    }

    private boolean isValid(){
        boolean flag = true;
        String emptyError = "This field cannot be empty!";
        if (Utils.isEmpty(description)){
           description.setError(emptyError);
            flag = false;
        }
        if (Utils.isEmpty(ans1)){
            ans1.setError(emptyError);
            flag = false;
        }
        if (Utils.isEmpty(ans2)){
            ans2.setError(emptyError);
            flag = false;
        }
        if (Utils.isEmpty(ans3)){
            ans3.setError(emptyError);
            flag = false;
        }
        if (Utils.isEmpty(ans4)){
            ans4.setError(emptyError);
            flag = false;
        }
        if (Utils.isEmpty(ans5)){
            ans5.setError(emptyError);
            flag = false;
        }
        return flag;
    }
    @Override
    protected void onPause(){
        super.onPause();
        if (mediaPlayer != null && mediaPlayer.isPlaying()){
            mediaPlayer.pause();
        }
        resetAudioButtons();

    }

    @Override
    protected void onDestroy(){
        if (mediaPlayer != null) {
            mediaPlayer.release();
        }
        super.onDestroy();
    }

    private void resetAudioButtons(){
        play.setEnabled(true);
        pause.setEnabled(false);
        stop.setEnabled(false);
    }

    private void cleanLastViews(){
        View lastView = mainLinearLayout.getChildAt(mainLinearLayout.getChildCount() - 2);
        if (lastView instanceof ImageView || lastView instanceof VideoView) {
            mainLinearLayout.removeViewAt(mainLinearLayout.getChildCount() - 2);
        }
        else if (lastView instanceof LinearLayout) {
            if (mediaPlayer != null) {
                mediaPlayer.release();
                mediaPlayer = null;
            }
            lastView.setVisibility(View.GONE);
            resetAudioButtons();
        }
    }

    private void setAudioBtnListeners(){
        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaPlayer.start();
                pause.setEnabled(true);
                stop.setEnabled(true);
                play.setEnabled(false);
            }
        });
        pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaPlayer.pause();
                pause.setEnabled(false);
                stop.setEnabled(true);
                play.setEnabled(true);
            }
        });
        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pause.setEnabled(false);
                play.setEnabled(true);
                stop.setEnabled(false);
                mediaPlayer.pause();
                mediaPlayer.seekTo(0);
            }
        });
    }

    private void populateFields(){
        question = questionDao.getQuestionById(questionId);
        description.setText(question.getDescription());
        ans1.setText(question.getAnswer().get(0));
        ans2.setText(question.getAnswer().get(1));
        ans3.setText(question.getAnswer().get(2));
        ans4.setText(question.getAnswer().get(3));
        ans5.setText(question.getAnswer().get(4));
        correctAnswerSpinner.setSelection(question.getCorrectAnswer()-1);
        if (question.getMediaType() != null && question.getMediaUri() != null){
            populateMedia(question.getMediaType(), Uri.parse(question.getMediaUri()));
        }
        mediaType = question.getMediaType();
        mediaTypeSpinner.setSelection(Utils.mediaTypes.indexOf(mediaType)+1);
        mediaUri = question.getMediaUri();
    }
    private void populateMedia(String mediaType, Uri mediaData){
        if (mediaType.equals("image")){
            ImageView view = new ImageView(getApplicationContext());
            view.setScaleType(ImageView.ScaleType.FIT_CENTER);
            mainLinearLayout.addView(view, mainLinearLayout.getChildCount() - 1);
            view.requestLayout();
            view.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT)
            );
            view.setImageURI(mediaData);
        }
        else if(mediaType.equals("video")){
            MyVideoView view = new MyVideoView(getApplicationContext());
            view.setVideoSize();
            mainLinearLayout.addView(view, mainLinearLayout.getChildCount() - 1);
            view.requestLayout();
            view.setClickable(true);
            view.setOnClickListener(v -> {
                if (view.isPlaying()){
                    view.pause();
                }
                else{
                    view.start();
                }

            });
            view.setVideoURI(mediaData);
            view.start();
        }
        else if(mediaType.equals("audio")){
            LinearLayout layout = findViewById(R.id.addQuestion_audioLayout);
            layout.setVisibility(View.VISIBLE);
            resetAudioButtons();
            mediaPlayer = MediaPlayer.create(getApplicationContext(), mediaData);
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    resetAudioButtons();
                }
            });
        }
    }

}