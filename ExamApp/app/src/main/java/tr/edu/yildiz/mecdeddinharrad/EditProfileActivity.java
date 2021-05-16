package tr.edu.yildiz.mecdeddinharrad;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;

public class EditProfileActivity extends AppCompatActivity {

    private final static int GALLERY_REQUEST_CODE = 1;
    ImageView avatar;
    Button saveBtn;
    EditText oldPword, newPword, newPwordConfirm, fname, lname, email, phone, uname;
    UserDao userDao;
    String currentUserName, avatarUri;
    User currentUser;
    boolean avatarChosen = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        avatar = (ImageView) findViewById(R.id.editProfile_avatar);
        saveBtn = (Button) findViewById(R.id.editProfile_saveBtn);
        newPword = (EditText) findViewById(R.id.editProfile_newPwordEditText);
        newPwordConfirm = (EditText) findViewById(R.id.editProfile_newPwordConfirmEditText);
        uname = (EditText) findViewById(R.id.editProfile_unameEditText);
        uname.setEnabled(false);
        fname = (EditText) findViewById(R.id.editProfile_fnameEditText);
        lname = (EditText) findViewById(R.id.editProfile_lnameEditText);
        email = (EditText) findViewById(R.id.editProfile_emailEditText);
        phone = (EditText) findViewById(R.id.editProfile_phoneEditText);
        oldPword = (EditText) findViewById(R.id.editProfile_oldPwordEditText);
        currentUserName = getIntent().getStringExtra("uname");
        ExamDB db = ExamDB.getInstance(this.getApplicationContext());
        userDao = db.userDao();
        populateFields();
    }

    public void onSaveClick(View view){
        if (isValid()) {
            String oldHash = Utils.encryptSHA256(oldPword.getText().toString());
            if (!oldHash.equals(currentUser.getPword())){
                oldPword.setError("Password is incorrect!");
            }
            else{
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
                currentUser.setAvatarURI(avatarUri);
                currentUser.setEmail(email.getText().toString());
                currentUser.setFname(fname.getText().toString());
                currentUser.setLname(lname.getText().toString());
                currentUser.setPhoneNumber(phone.getText().toString());
                if (!Utils.isEmpty(newPword)) {
                    String newHash = Utils.encryptSHA256(newPword.getText().toString());
                    currentUser.setPword(newHash);
                }
                userDao.update(currentUser);
                Toast toast = Toast.makeText(getApplicationContext(), "Changes were saved successfully!", Toast.LENGTH_LONG);
                toast.show();
                finish();
            }
        }
    }

    public  void onCancelClick(View view){
        this.finish();
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
        if (Utils.isEmpty(oldPword)){
            oldPword.setError("You must enter your password to confirm the changes");
            flag = false;
        }
        if (!newPword.getText().toString().equals(newPwordConfirm.getText().toString())){
            newPwordConfirm.setError("Passwords don't match!");
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
        if(!Utils.isEmpty(newPword) && !Utils.isValidPassword(newPword.getText().toString())){
            newPword.setError("Password must contain 8 characters at least");
            flag = false;
        }
        return flag;
    }

    private void populateFields(){
        currentUser = userDao.findByUsername(currentUserName);
        avatarUri = currentUser.getAvatarURI();
        avatar.setImageURI(Uri.parse(avatarUri));
        uname.setText(currentUser.getUname());
        fname.setText(currentUser.getFname());
        lname.setText(currentUser.getLname());
        email.setText(currentUser.getEmail());
        phone.setText(currentUser.getPhoneNumber());
    }

}