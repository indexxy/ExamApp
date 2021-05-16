package tr.edu.yildiz.mecdeddinharrad;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Entity(tableName = "question",
        foreignKeys = {@ForeignKey(entity = User.class, parentColumns = "uname", childColumns = "owner")}
)
public class Question {

    @PrimaryKey(autoGenerate = true)
    private Integer id;

    private List<String> answer;
    private String description;
    private int correctAnswer;
    private String mediaUri;
    private String mediaType;
    private final String owner;

    public Question(List<String> answer, String description, String owner, int correctAnswer, String mediaUri, String mediaType) {
        this.description = description;
        this.owner = owner;
        this.answer = answer;
        this.correctAnswer = correctAnswer;
        this.mediaUri = mediaUri;
        this.mediaType = mediaType;
    }

    public void setId(Integer id){
        this.id = id;
    }
    public Integer getId() {
        return id;
    }

    public void setDescription(String description){
        this.description = description;
    }

    public String getDescription(){
        return this.description;
    }

    public List<String> getAnswer() {
        return answer;
    }

    public int getCorrectAnswer() {
        return correctAnswer;
    }

    public String getMediaUri() {
        return mediaUri;
    }

    public void setAnswer(List<String> answer) {
        this.answer = answer;
    }

    public void setCorrectAnswer(int correctAnswer) {
        this.correctAnswer = correctAnswer;
    }

    public void setMediaUri(String mediaUri) {
        this.mediaUri = mediaUri;
    }

    public String getOwner(){
        return owner;
    }

    public String getMediaType(){
        return this.mediaType;
    }

    public void setMediaType(String mediaType){
        this.mediaType = mediaType;
    }

    @Override
    public String toString(){
        String out = "\n________\n";
        out += this.description + "\n\n";
        out += "1) " + answer.get(0);
        out += "\n2) " + answer.get(1);
        out += "\n3) " + answer.get(2);
        out += "\n4) " + answer.get(3);
        out += "\n5) " + answer.get(4);

        return out;
    }

    public String getQuestionAnswers(int n){
        List<String> finalAns = new ArrayList<String>(answer);
        finalAns.remove(correctAnswer);

        List<Integer> indices = new ArrayList<Integer>();
        for(int i: Utils.randomIndices(finalAns.size())){
            indices.add(i);
        }

        finalAns.add(answer.get(correctAnswer - 1));
        Collections.shuffle(finalAns);

        StringBuilder out = new StringBuilder("\n________\n");
        out.append(this.description).append("\n\n");
        for(int i = 0; i < n; i++){
            out.append((i + 1) +") ").append(finalAns.get(i) + "\n");
        }
        return out.toString();
    }

}
