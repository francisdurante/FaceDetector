package detection.face.facedetection;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Random;

public class QuestionsActivity extends AppCompatActivity {
    Context mContext = this;
    RadioGroup radioGroup;
    SharedPreferences spf;
    Button next;
    int page = 1;
    String finalResultComputation = "";
    String finalResultEmotion = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_questions);
        initQuestions();
    }

    public void initQuestions() {
        questionToDisplay(page);
        new SecretCapture(mContext).execute();
    }
    public void getAnswers(){
        String[] results = new String[3];
        int points;
        for (int page = 0; page < 3; page++) {
            points = 0;
            for (int x = 0; x < 4; x++) {
                String position = Integer.toString(x + 1);
                String pageNumber = Integer.toString(page + 1);
                switch (getString("ANSWER_QUESTION_" + pageNumber + "_" + position, mContext)){
                    case "Strongly Agree" :
                        points = points + 5;
                        break;
                    case "Agree" :
                        points =points + 4;
                        break;
                    case "Neutral" :
                        points =points + 3;
                        break;
                    case "Disagree" :
                        points =points + 2;
                        break;
                    case "Strongly Disagree" :
                        points =points + 1;
                        break;
                }

                save("RESULT_PAGE_" + pageNumber,Integer.toString(points),mContext);
            }
            String pageNumber = Integer.toString(page + 1);
            results[page] = getString("RESULT_PAGE_" + pageNumber,mContext);
        }
        switch (getBiggerResultPage(results)){
            case 1 :
                finalResultEmotion = "HAPPY";
                break;
            case 2:
                finalResultEmotion = "SAD";
                break;
            case 3:
                finalResultEmotion = "IRRITATE";
        }


//        AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);
//        dialog.setTitle("Result in your Answers");
//        dialog.setMessage("Base in our survey we saw that you are " + finalResultEmotion + " with total points of " + finalResultComputation);
//        dialog.setPositiveButton("OK", (dialog1, which) -> onBackPressed());
//        dialog.show();
        Toast.makeText(mContext,"Please Use Facial Recognition",Toast.LENGTH_SHORT).show();
        startActivity(new Intent(mContext,MainActivity.class).putExtra("RESULT_QUESTION",finalResultEmotion).putExtra("QUESTION_TRIGGER",1));
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    public void cancel(View v){
        startActivity(new Intent(mContext,EstablishmentListActivity.class));
        finish();
    }
    public void save(String key, String value,Context context) {
        spf = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor edit = spf.edit();
        edit.putString(key, value);
        edit.apply();
    }
    public String getString(String key,Context context) {
        spf = PreferenceManager.getDefaultSharedPreferences(context);
        return spf.getString(key,"");
    }

    private void questionToDisplay(int page){
        String[] toGetEmotion = null;
        next = findViewById(R.id.submit_answer);
        if(page == 1){
            toGetEmotion = getResources().getStringArray(R.array.questions_happy);
            next.setText("NEXT");
            next.setOnClickListener(v -> {
                new SecretCapture(mContext).execute();
                if(radioGroup.getCheckedRadioButtonId() == -1){
                    Toast.makeText(mContext,"Please complete the survey",Toast.LENGTH_SHORT).show();
                }else{
                    questionToDisplay(2);
                    new SecretCapture(mContext).execute();
                }
            });

        }
        if(page == 2){
            toGetEmotion = getResources().getStringArray(R.array.questions_sad);
            next.setText("NEXT");
            next.setOnClickListener(v -> {
                if(radioGroup.getCheckedRadioButtonId() == -1){
                    Toast.makeText(mContext,"Please complete the survey",Toast.LENGTH_SHORT).show();
                }else{
                    questionToDisplay(3);
                }
            });
        }
        if(page == 3){
            toGetEmotion = getResources().getStringArray(R.array.questions_irritate);
            next.setText("SUBMIT");
            next.setOnClickListener(v -> {
                if(radioGroup.getCheckedRadioButtonId() == -1){
                    Toast.makeText(mContext,"Please complete the survey",Toast.LENGTH_SHORT).show();
                }else{
                    getAnswers();
                }
            });
        }

        LayoutInflater inflater = LayoutInflater.from(mContext);
        LinearLayout linearToPalceQuestions = findViewById(R.id.questions_place);
        linearToPalceQuestions.removeAllViews();
        for(int x = 0; x < 4; x++){
            try {
                int position = new Random().nextInt(toGetEmotion.length);
                final String randomStr = toGetEmotion[position];
                View view = inflater.inflate(R.layout.questions_place_layoutt, linearToPalceQuestions, false);
                final TextView question = view.findViewById(R.id.questions);
                if(page == 1)
                    question.setText((x + 1) + ". " + randomStr);
                if(page == 2)
                    question.setText((x + 5) + ". " + randomStr);
                if(page == 3)
                    question.setText((x + 9) + ". " + randomStr);
                String questionsShown = question.getText().toString();
                question.setTag(x + 1);
                save("QUESTION_SHOWN_" + question.getTag(), questionsShown, mContext);
                radioGroup = view.findViewById(R.id.radio_group);
                radioGroup.setTag(x + 1);
                radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
                    String value = ((RadioButton) findViewById(group.getCheckedRadioButtonId())).getText().toString();
                    save("ANSWER_QUESTION_" + page + "_" + group.getTag().toString(), value, mContext);
                });
                linearToPalceQuestions.addView(view);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    public int getBiggerResultPage(String[] points){
        int largest = 0;
        for (int x = 1 ; x < 3; x++){
            if(Double.parseDouble(points[x]) > Double.parseDouble(points[largest])){
                largest = x;
                finalResultComputation = points[largest];
            }else{
                double total = Integer.parseInt(points[largest]) / 4;
                finalResultComputation = Double.toString(total);
            }
        }
        return largest + 1;
    }


}
