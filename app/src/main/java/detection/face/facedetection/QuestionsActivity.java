package detection.face.facedetection;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.util.Random;

public class QuestionsActivity extends AppCompatActivity {
    Context mContext = this;
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
            final String randomStr = toGetEmotion[new Random().nextInt(toGetEmotion.length)];
            View view = inflater.inflate(R.layout.questions_place_layoutt, linearToPalceQuestions, false);
            TextView question = view.findViewById(R.id.questions);
            question.setText(x + 1+". " + randomStr);
            final RadioGroup radioGroup = view.findViewById(R.id.radio_group);
            radioGroup.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
            linearToPalceQuestions.addView(view);
        }
    }
}
