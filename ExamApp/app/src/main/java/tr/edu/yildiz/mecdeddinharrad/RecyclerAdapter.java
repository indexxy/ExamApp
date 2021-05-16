package tr.edu.yildiz.mecdeddinharrad;

import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import androidx.cardview.widget.CardView;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.annotation.NonNull;

import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.VideoView;

import java.util.ArrayList;
import java.util.List;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.MyViewHolder>
{

    private List<Question> questionList = new ArrayList<Question>();
    private List<Question> selectedQuestionList = new ArrayList<Question>();

    private OnDeleteClickListener deleteClickListener;
    private OnEditClickListener editClickListener;
    private final boolean selectionMode;

    public RecyclerAdapter(boolean selectionMode){
        this.selectionMode = selectionMode;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.question_item, viewGroup, false);

        return new MyViewHolder(itemView, deleteClickListener, editClickListener);
    }

    public void setOnDeleteClick(OnDeleteClickListener listener) {
        this.deleteClickListener = listener;
    }

    public interface OnDeleteClickListener {
        void onDeleteClick(int position);
    }

    public void setOnEditClick(OnEditClickListener listener) {
        this.editClickListener = listener;
    }

    public interface OnEditClickListener {
        void onEditClick(int position);
    }


    public class MyViewHolder extends RecyclerView.ViewHolder{

        TextView description, ans1, ans2, ans3, ans4, ans5, correctAnswer;
        RelativeLayout cardLayout;
        CardView questionCard;
        Button showMedia, deleteBtn, editBtn;
        int position;

        public MyViewHolder(@NonNull View view, OnDeleteClickListener deleteListener, OnEditClickListener editListener){
            super(view);
            description = (TextView) view.findViewById(R.id.questionCard_descriptionView);
            ans1 = (TextView) view.findViewById(R.id.questionCard_answer1View);
            ans2 = (TextView) view.findViewById(R.id.questionCard_answer2View);
            ans3 = (TextView) view.findViewById(R.id.questionCard_answer3View);
            ans4 = (TextView) view.findViewById(R.id.questionCard_answer4View);
            ans5 = (TextView) view.findViewById(R.id.questionCard_answer5View);
            correctAnswer = (TextView) view.findViewById(R.id.questionCard_correctAnswer);
            showMedia = (Button) view.findViewById(R.id.questionCard_showMedia);
            deleteBtn = (Button) view.findViewById(R.id.questionCard_deleteBtn);
            editBtn = (Button) view.findViewById(R.id.questionCard_editBtn);
            cardLayout = (RelativeLayout) view.findViewById(R.id.questionCard_relativeLayout);
            questionCard = (CardView) view.findViewById(R.id.questionCard_cardView);

            editBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    editListener.onEditClick(position);
                }
            });
            deleteBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deleteListener.onDeleteClick(position);
                }
            });
        }
    }
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder viewHolder, int position) {
        Question currentQuestion = questionList.get(position);

        viewHolder.description.setText(currentQuestion.getDescription());
        viewHolder.position = position;
        viewHolder.ans1.setText(currentQuestion.getAnswer().get(0));
        viewHolder.ans2.setText(currentQuestion.getAnswer().get(1));
        viewHolder.ans3.setText(currentQuestion.getAnswer().get(2));
        viewHolder.ans4.setText(currentQuestion.getAnswer().get(3));
        viewHolder.ans5.setText(currentQuestion.getAnswer().get(4));
        String crrAns = "Correct answer: " + currentQuestion.getCorrectAnswer();
        viewHolder.correctAnswer.setText(crrAns);
        if (currentQuestion.getMediaUri() == null){
            viewHolder.showMedia.setVisibility(View.GONE);
        }
        viewHolder.showMedia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog mediaDialogue = new Dialog(v.getContext(), android.R.style.Theme_Black_NoTitleBar);
                mediaDialogue.setContentView(R.layout.media_fragment);
                if (currentQuestion.getMediaUri() != null){
                    if (currentQuestion.getMediaType().equals("audio")){
                        Button play = (Button) mediaDialogue.findViewById(R.id.mediaFragment_playBtn);
                        Button stop = (Button) mediaDialogue.findViewById(R.id.mediaFragment_stopBtn);
                        Button pause = (Button) mediaDialogue.findViewById(R.id.mediaFragment_pauseBtn);
                        play.setVisibility(View.VISIBLE);
                        stop.setVisibility(View.VISIBLE);
                        pause.setVisibility(View.VISIBLE);
                        resetAudioButtons(play, pause, stop);
                        MediaPlayer mediaPlayer = MediaPlayer.create(v.getContext(), Uri.parse(currentQuestion.getMediaUri()));
                        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                            @Override
                            public void onCompletion(MediaPlayer mp) {
                                resetAudioButtons(play, pause, stop);
                            }
                        });
                        setAudioBtnListeners(play, pause, stop, mediaPlayer);
                        mediaDialogue.setOnDismissListener(new DialogInterface.OnDismissListener() {
                            @Override
                            public void onDismiss(DialogInterface dialog) {
                                mediaPlayer.release();
                            }
                        });
                    }
                    else if (currentQuestion.getMediaType().equals("image")){
                        ImageView imageView = (ImageView) mediaDialogue.findViewById(R.id.mediaFragment_imageView);
                        imageView.setVisibility(View.VISIBLE);
                        imageView.setImageURI(Uri.parse(currentQuestion.getMediaUri()));
                    }
                    else{
                        VideoView videoView = (VideoView) mediaDialogue.findViewById(R.id.mediaFragment_videoView);
                        videoView.setVisibility(View.VISIBLE);
                        videoView.setClickable(true);
                        videoView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (videoView.isPlaying())
                                    videoView.pause();
                                else
                                    videoView.start();
                            }
                        });
                        videoView.setVideoURI(Uri.parse(currentQuestion.getMediaUri()));
                        videoView.start();
                    }
                }
                mediaDialogue.getWindow().setBackgroundDrawable(new ColorDrawable(Color.argb(100, 0, 0, 0)));
                mediaDialogue.setCancelable(true);
                mediaDialogue.setCanceledOnTouchOutside(true);
                mediaDialogue.show();

            }
        });

        if (selectionMode){
            viewHolder.editBtn.setVisibility(View.GONE);
            viewHolder.deleteBtn.setVisibility(View.GONE);
            viewHolder.questionCard.setClickable(true);
            viewHolder.questionCard.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (selectedQuestionList.contains(questionList.get(position))){
                        viewHolder.cardLayout.setBackgroundColor(Color.TRANSPARENT);
                        selectedQuestionList.remove(questionList.get(position));
                    }
                    else{
                        viewHolder.cardLayout.setBackgroundColor(ResourcesCompat.getColor(v.getResources(), R.color.selected, null));
                        selectedQuestionList.add(questionList.get(position));
                        setSelectedQuestions(selectedQuestionList);
                    }
                }
            });
        }
    }
    @Override
    public int getItemCount() {
        return questionList.size();
    }
    public void setQuestionList(List<Question> questions) {
        this.questionList = questions;
        notifyDataSetChanged();
    }

    public void removeItem(int position){
        questionList.remove(position);
        notifyDataSetChanged();
    }

    public List<Question> getQuestionList() {
        return questionList;
    }
    public List<Question> getSelectedQuestions() {
        return selectedQuestionList;
    }

    public void setSelectedQuestions(List<Question> selectedQuestions) {
        this.selectedQuestionList = selectedQuestions;
    }

    private void setAudioBtnListeners(Button play, Button pause, Button stop, MediaPlayer mediaPlayer){
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

    private void resetAudioButtons(Button play, Button pause, Button stop){
        play.setEnabled(true);
        pause.setEnabled(false);
        stop.setEnabled(false);
    }
}
