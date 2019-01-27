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
    SharedPreferences spf;
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
        }else{
            toGetEmotion = getResources().getStringArray(R.array.questions_sad);
        }

        LayoutInflater inflater = LayoutInflater.from(mContext);
        LinearLayout linearToPalceQuestions = findViewById(R.id.questions_place);
        for(int x = 0; x < 5; x++){
            try {
                int position = new Random().nextInt(toGetEmotion.length);
                final String randomStr = toGetEmotion[position];
                View view = inflater.inflate(R.layout.questions_place_layoutt, linearToPalceQuestions, false);
                final TextView question = view.findViewById(R.id.questions);
                question.setText(x + 1 + ". " + randomStr);
                String questionsShown = question.getText().toString();
                question.setTag(x + 1);
                save("QUESTION_SHOWN_" + question.getTag(), questionsShown, mContext);
                radioGroup = view.findViewById(R.id.radio_group);
                radioGroup.setTag(x + 1);
                radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup group, int checkedId) {
                        String value = ((RadioButton) findViewById(group.getCheckedRadioButtonId())).getText().toString();
                        save("ANSWER_QUESTION_" + group.getTag().toString(), value, mContext);
                        System.out.println(getString("ANSWER_QUESTION_1",mContext) + " " + value + " aaaaaaaaaaa");
                    }

                });
                linearToPalceQuestions.addView(view);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
    public void getAnswers(View v){
        Toast.makeText(mContext,"Thank you for your cooperation",Toast.LENGTH_LONG).show();
        float points = 0.0f;
        float totalPoints;
        for(int x = 0 ; x < 5; x++) {
            String position = Integer.toString(x + 1);
            if ("Agree".equalsIgnoreCase(getString("ANSWER_QUESTION_" + position, mContext))) {
                points =+ 1.0f; // + 1 point for sad
            }
        }
        totalPoints = (points / 5.00f) * 100f;
        if(totalPoints == 0.00){
            totalPoints = 100f;
        }else{
            totalPoints = 100f - totalPoints;
        }
        AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);
        dialog.setTitle("Result in your Answer");
        dialog.setMessage("Your total points of being " + (Utility.getString("INITIAL_EMOTION",mContext).equals("") ? "SAD" : Utility.getString("INITIAL_EMOTION",mContext))+ " is " + totalPoints);
        dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        dialog.show();
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
}
