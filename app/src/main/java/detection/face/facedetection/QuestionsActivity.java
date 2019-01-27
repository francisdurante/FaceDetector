package detection.face.facedetection;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Random;

public class QuestionsActivity extends AppCompatActivity {
    Context mContext = this;
    RadioGroup radioGroup;
    String oldValue = "";
    String newValue = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_questions);
        initQuestions();
    }

    public void initQuestions(){
        String[] toGetEmotion = null;
        if("SAD".equals(Utility.getString("INITIAL_EMOTION",mContext)) || "HAPPY".equals(Utility.getString("INITIAL_EMOTION",mContext))){
            toGetEmotion = getResources().getStringArray(R.array.questions_sad);
        }if("IRRITATE".equals(Utility.getString("INITIAL_EMOTION",mContext))){
            toGetEmotion = getResources().getStringArray(R.array.questions_irritate);
        }

        LayoutInflater inflater = LayoutInflater.from(mContext);
        LinearLayout linearToPalceQuestions = findViewById(R.id.questions_place);
        for(int x = 0; x < 5; x++){
            int position = new Random().nextInt(toGetEmotion.length);
            final String randomStr = toGetEmotion[position];
            View view = inflater.inflate(R.layout.questions_place_layoutt, linearToPalceQuestions, false);
            final TextView question = view.findViewById(R.id.questions);
            question.setText(x + 1+". " + randomStr);
            String questionsShown = question.getText().toString();
            question.setTag(x + 1);
            Utility.save("QUESTION_SHOWN_"+question.getTag(),questionsShown,mContext);
            radioGroup = view.findViewById(R.id.radio_group);
            radioGroup.setTag(x + 1);
            radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {
                    String value = ((RadioButton)findViewById(group.getCheckedRadioButtonId())).getText().toString();
                    Utility.save("ANSWER_QUESTION_"+group.getTag(),value,mContext);
                }
            });
            linearToPalceQuestions.addView(view);
        }
    }
    public void getAnswers(View v){
        Toast.makeText(mContext,"Thank you for your cooperation",Toast.LENGTH_LONG).show();
        for(int x = 0 ; x < 5; x++) {
            double points = 0.0;
            double totalPoints = 0.00;
            String answer = Utility.getString("ANSWER_QUESTION_" + x + 1, mContext);
            if ("Agree".equalsIgnoreCase(answer)) {
                points =+ 1.0; // + 1 point for sad
            }
            totalPoints = (points / 5) * 100;
            AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);
            dialog.setTitle("Result in your Answer");
            dialog.setMessage("Your total points " + totalPoints);
            dialog.show();
        }
        finish();
    }
    public void cancel(View v){
        startActivity(new Intent(mContext,EstablishmentListActivity.class));
        finish();
    }
}
