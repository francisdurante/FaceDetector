package detection.face.facedetection;

import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.ScrollView;
import android.widget.TextView;

public class EstablishmentListActivity extends AppCompatActivity {

    LinearLayout scrollView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_establishment_list);
        getWindow().setBackgroundDrawableResource(R.drawable.background_image);

        init();
    }

    public void init() {
        scrollView = findViewById(R.id.list);
        LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
        for (int x = 0 ; x<5; x++){
            View view = inflater.inflate(R.layout.est_list_item,scrollView,false);
            ImageView imageView = view.findViewById(R.id.est_pic);
            imageView.setImageResource(R.drawable.eateryfinderlogo);

            TextView estName = view.findViewById(R.id.est_name);
            estName.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.common_google_signin_btn_text_dark_default));
            estName.setText("Francis Kainan");

            TextView estAddress = view.findViewById(R.id.est_address);
            estAddress.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.common_google_signin_btn_text_dark_default));
            estAddress.setText("ADDRESS ADDRESS ADDRESS ADDRESS ADDRESS ADDRESS ADDRESS");

            RatingBar ratingBar = view.findViewById(R.id.rating_bar);
            ratingBar.setIsIndicator(true);
            ratingBar.setNumStars(5);
            ratingBar.setRating(3);

            scrollView.addView(view);
        }
    }
}
