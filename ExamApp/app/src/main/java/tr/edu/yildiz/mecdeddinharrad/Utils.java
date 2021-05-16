package tr.edu.yildiz.mecdeddinharrad;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Patterns;
import android.widget.EditText;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.List;
import java.util.Arrays;
import java.util.Random;

public class Utils {

    public static List<String> mediaTypes = Arrays.asList(new String[]{"image", "video", "audio"});

    public static boolean isValidEmail(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }

    public static boolean isValidPhoneNumber(CharSequence target){
        return (!TextUtils.isEmpty(target) && Patterns.PHONE.matcher(target).matches());
    }

    public static boolean isValidPassword(String target){
        return target.length() >= 8;
    }

    public static String encryptSHA256(String msg){
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(msg.getBytes(StandardCharsets.UTF_8));
            return bytesToHex(hash);
        } catch(Exception e){
            return msg;
        }

    }

    public static String bytesToHex(byte[] hash) {
        StringBuilder hexString = new StringBuilder(2 * hash.length);
        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }

    public static boolean isEmpty(EditText etText) {
        return etText.getText().toString().trim().length() == 0;
    }

    public static Uri copyFile(Context context, Uri src, String folder, String fileName) throws IOException{
        InputStream in = context.getContentResolver().openInputStream(src);
        File subDirectory = new File(context.getFilesDir(), folder);
        if (!subDirectory.exists()){
            if (folder.contains("media/")){
                File media = new File(subDirectory.getParent());
                if (!media.exists()){
                    media.mkdir();
                }
            }
            subDirectory.mkdir();
        }
        File dst = new File(subDirectory, fileName);
        dst.createNewFile();
        try {
            OutputStream out = new FileOutputStream(dst, false);
            try {
                // Transfer bytes from in to out
                byte[] buf = new byte[1024];
                int len;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
            } finally {
                out.close();
            }
        } finally {
            in.close();
        }
        return Uri.fromFile(dst);
    }

    public static String constructExamString(List<Question> questionList, int duration, int points, int diff){
        String out = "Duration: " + duration + " mins.\n";
        out += "Points per question: " + points + ".\n";
        for(Question question: questionList){
            if (diff == 5)
                out += question.toString();
            else
                out += question.getQuestionAnswers(diff);
        }
        return out;
    }

    public static int[] randomIndices(int len) {
        int[] indices = new int[len];
        for (int i = 0; i < len; i++) {
            indices[i] = i;
        }
        for (int i = len - 1, j, t; i > 0; i--) {
            j = new Random().nextInt(i);
            t = indices[j];
            indices[j] = indices[i];
            indices[i] = t;
        }
        return indices;
    }
}
