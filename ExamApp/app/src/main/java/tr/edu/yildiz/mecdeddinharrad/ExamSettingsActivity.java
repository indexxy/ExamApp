package tr.edu.yildiz.mecdeddinharrad;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class ExamSettingsActivity extends AppCompatActivity {

    public static final String PREFS_NAME = "SettingsPrefs";
    SharedPreferences prefs;
    EditText points, duration, diff;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exam_settings);

        prefs = getApplicationContext().getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        points = (EditText) findViewById(R.id.examSettings_pointsEditText);
        duration = (EditText) findViewById(R.id.examSettings_durationEditText);
        diff = (EditText) findViewById(R.id.examSettings_diffEditText);
        points.setText(String.valueOf(prefs.getInt("POINTS", 10)));
        duration.setText(String.valueOf(prefs.getInt("DURATION", 60)));
        diff.setText(String.valueOf(prefs.getInt("DIFF", 5)));
    }

    public void onSaveClick(View view){
        prefs.edit().putInt("POINTS", Integer.parseInt(points.getText().toString())).apply();
        prefs.edit().putInt("DURATION", Integer.parseInt(duration.getText().toString())).apply();
        int diffValue = Integer.parseInt(diff.getText().toString());
        if (diffValue > 5){
            diffValue = 5;
        }
        else if (diffValue < 2){
            diffValue = 2;
        }
        prefs.edit().putInt("DIFF", diffValue).apply();
        finish();
    }
}