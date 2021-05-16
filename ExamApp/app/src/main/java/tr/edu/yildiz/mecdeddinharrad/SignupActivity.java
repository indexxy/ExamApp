package tr.edu.yildiz.mecdeddinharrad;

import androidx.appcompat.app.AppCompatActivity;

import android.database.sqlite.SQLiteConstraintException;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.content.Intent;
import androidx.annotation.Nullable;
import android.widget.ImageView;
import android.widget.EditText;
import android.net.Uri;
import android.widget.Toast;

import java.io.IOException;

public class SignupActivity extends AppCompatActivity {
    private final static int GALLERY_REQUEST_CODE = 1;
    ImageView avatar;
    Button signUpBtn;
    EditText pword, pwordConfirm, uname, fname, lname, email, phone;
    UserDao userDao;
    String avatarUri;
    boolean avatarChosen = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        avatarUri = "android.resource://"  + getPackageName() +"/drawable/sample_avatar";
        avatar = (ImageView) findViewById(R.id.signup_avatar);
        signUpBtn = (Button) findViewById(R.id.signup_signupBtn);
        pword = (EditText) findViewById(R.id.signup_pwordEditText);
        pwordConfirm = (EditText) findViewById(R.id.signup_pwordConfirmEditText);
        uname = (EditText) findViewById(R.id.signup_unameEditText);
        fname = (EditText) findViewById(R.id.signup_fnameEditText);
        lname = (EditText) findViewById(R.id.signup_lnameEditText);
        email = (EditText) findViewById(R.id.signup_emailEditText);
        phone = (EditText) findViewById(R.id.signup_phoneEditText);
        ExamDB db = ExamDB.getInstance(this.getApplicationContext());
        userDao = db.userDao();
    }


    public void onAvatarClick(View view){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(
                Intent.createChooser(intent, "Pick an avatar"), GALLERY_REQUEST_CODE
        );
    }

    @Override
    protected void onActivityResult(int requestCode, int resultData, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultData, data);
        if (requestCode == GALLERY_REQUEST_CODE && resultData == RESULT_OK && data != null) {
            Uri imageData = data.getData();
            avatarUri = imageData.toString();
            avatar.setImageURI(imageData);
            avatarChosen = true;
        }
    }

    public void onSignUpClick(View view){
        if (isValid()) {
            String hashedPword = Utils.encryptSHA256(pword.getText().toString());
            try {
                if (avatarChosen) {
                    String fileName = uname.getText().toString();
                    avatarUri = Utils.copyFile(
                            getApplicationContext(), Uri.parse(avatarUri), "avatars", fileName
                    ).toString();
                }
            } catch (IOException e){
                Log.d("Exception :", e.toString());
            }
            finally {
                User user = new User(
                        uname.getText().toString(), fname.getText().toString(),
                        lname.getText().toString(), email.getText().toString(),
                        hashedPword, phone.getText().toString(), avatarUri
                );
                try {
                    userDao.insert(user);
                    Toast toast = Toast.makeText(this.getApplicationContext(), "You signed up successfully", Toast.LENGTH_SHORT);
                    toast.show();
                    this.finish();
                } catch (SQLiteConstraintException e) {
                    uname.setError("Username is already taken!");
                }
            }
        }
    }

    private boolean isValid(){
        String emptyError = "This field cannot be blank";
        boolean flag = true;
        if (Utils.isEmpty(fname)){
            fname.setError(emptyError);
            flag = false;
        }
        if (Utils.isEmpty(lname)){
            lname.setError(emptyError);
            flag = false;
        }
        if (Utils.isEmpty(uname)){
            uname.setError(emptyError);
            flag = false;
        }
        if (Utils.isEmpty(pword)){
            pword.setError(emptyError);
            flag = false;
        }
        if (!pword.getText().toString().equals(pwordConfirm.getText().toString())){
            pwordConfirm.setError("Passwords don't match!");
            flag = false;
        }
        if (!Utils.isValidEmail(email.getText().toString())){
            flag = false;
            email.setError("Please use a valid e-mail address");
        }
        if (!Utils.isValidPhoneNumber(phone.getText().toString())){
            flag = false;
            phone.setError("Please use a valid phone number");
        }
        if(!Utils.isValidPassword(pword.getText().toString())){
            pword.setError("Password must contain 8 characters at least");
            flag = false;
        }
        return flag;
    }

}