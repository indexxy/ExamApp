package tr.edu.yildiz.mecdeddinharrad;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.content.Intent;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity {
    UserDao userDao;
    EditText uname;
    EditText pword;
    int globalCounter = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        SharedPreferences prefs = getPreferences(MODE_PRIVATE);
        if (prefs.getBoolean("firstRun", true)){
            FirstRun.run(this);
            prefs.edit().putBoolean("firstRun", false).apply();
        }

        ExamDB db = ExamDB.getInstance(getApplicationContext());
        userDao = db.userDao();
        uname = (EditText) findViewById(R.id.login_unameEditTxt);
        pword = (EditText) findViewById(R.id.login_pwordEditText);
    }

    public void signupOnClick(View view){
        Intent i = new Intent(LoginActivity.this, SignupActivity.class);
        startActivity(i);
    }

    public void loginOnClick(View view){
        boolean valid = true;
        if (isEmpty(pword)){
            pword.setError("This field cannot be empty!");
            valid = false;
        }
        if (isEmpty(uname)){
            uname.setError("This field cannot be empty!");
            valid = false;
        }
        if (valid) {
            if (globalCounter < 3) {
                String hash = Utils.encryptSHA256(pword.getText().toString());
                User user = userDao.checkUserLogin(uname.getText().toString(), hash);
                if (user == null) {
                    Toast toast = Toast.makeText(getApplicationContext(), "Password is incorrect or username does not exist!", Toast.LENGTH_LONG);
                    toast.show();
                    globalCounter++;
                } else {
                    Intent intent = new Intent(LoginActivity.this, MenuActivity.class);
                    intent.putExtra("uname", user.getUname());
                    startActivity(intent);
                    globalCounter = 0;
                }
            }
            else{
                Intent i = new Intent(LoginActivity.this, SignupActivity.class);
                startActivity(i);
                globalCounter = 0;
            }
        }
    }

    private boolean isEmpty(EditText etText) {
        return etText.getText().toString().trim().length() == 0;
    }

}