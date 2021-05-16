package tr.edu.yildiz.mecdeddinharrad;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "user")
public class User {

    @PrimaryKey
    @NonNull
    private final String uname;

    private String fname, lname, pword, email, phoneNumber, avatarURI;

    public User(@NonNull String uname, String fname, String lname, String email, String pword, String phoneNumber, String avatarURI){
        this.uname = uname;
        this.pword = pword;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.fname = fname;
        this.lname = lname;
        this.avatarURI = avatarURI;
    }

    @NonNull
    public String getUname(){
        return this.uname;
    }

    public String getPword(){
        return this.pword;
    }

    public String getEmail(){
        return this.email;
    }

    public String getPhoneNumber(){
        return this.phoneNumber;
    }

    public String getFname(){
        return this.fname;
    }

    public String getLname(){
        return this.lname;
    }

    public String getAvatarURI(){
        return this.avatarURI;
    }

    public void setFname(String fname) {
        this.fname = fname;
    }

    public void setLname(String lname) {
        this.lname = lname;
    }

    public void setPword(String pword) {
        this.pword = pword;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setAvatarURI(String avatarURI) {
        this.avatarURI = avatarURI;
    }

}
