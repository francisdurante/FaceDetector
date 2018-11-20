package detection.face.facedetection;

import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

import cz.msebera.android.httpclient.Header;

public class FaceSuggestionActivity extends AppCompatActivity {

    LinearLayout scrollView;
    ScrollView scrollViewOther;
    int suggestionLenght = 0;
    int otherLenght = 0;
    Button bestSuggestion;
    Button otherSuggestion;
    String emotion;
    String age_param;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_face_suggestion);
        getWindow().setBackgroundDrawableResource(R.drawable.background_image);
        bestSuggestion = findViewById(R.id.best_suggestion);
        otherSuggestion = findViewById(R.id.other_suggestion);
        emotion = Objects.requireNonNull(getIntent().getExtras()).getString("emotion");
        age_param = Objects.requireNonNull(getIntent().getExtras()).getString("age");
        System.out.println(emotion + " " + age_param + " aaaaaaaaaaaaaaa");
        getFacialSuggestion(emotion,age_param);
    }

    public void getFacialSuggestion(String emotion,String age) {
        RequestParams rp = new RequestParams();
        rp.add("pass", "face_suggestion");
        rp.add("emotion", emotion);
        rp.add("age", age);


        Utility.getByUrl(Constant.GET_FACE_SUGGESTION, rp, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    scrollView = findViewById(R.id.list_face);
                    scrollViewOther = findViewById(R.id.scroll_view_face_suggestion_other);
                    LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
                    JSONObject object = new JSONObject(response.toString());
                    String status = object.getString("status");
                    if ("success".equals(status)) {
                        JSONArray data = new JSONArray(object.getString("suggestion"));
                        JSONArray other = new JSONArray(object.getString("other"));
                        JSONObject rate = new JSONObject(object.getString("rate"));
                        JSONObject rate_count = new JSONObject(object.getString("rate_count"));
                        suggestionLenght = data.length();
                        otherLenght = other.length();
                        if(suggestionLenght != 0) {
                            for (int x = 0; x < suggestionLenght; x++) {
                                String array = data.getString(x);
                                JSONObject datas = new JSONObject(array);
                                int estStatus = datas.getInt("est_status");
                                if (estStatus == 1) {
                                    View view = inflater.inflate(R.layout.activity_face_suggestion, scrollView, false);
                                    ImageView imageView = view.findViewById(R.id.est_pic);
                                    imageView.setPadding(20, 10, 10, 20);
                                    Picasso.with(getApplicationContext())
                                            .load(picUrl(datas.getString("est_front_store")))
                                            .into(imageView);
                                    imageView.setOnClickListener(new FaceSuggestionActivity.ClickEstablishment(datas.getString("establishment_name"), datas.getString("establishment_user_id")));

                                    TextView estName = view.findViewById(R.id.est_name_face);
                                    estName.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.common_google_signin_btn_text_dark_default));
                                    estName.setText(datas.getString("establishment_name"));
                                    estName.setOnClickListener(new FaceSuggestionActivity.ClickEstablishment(datas.getString("establishment_name"), datas.getString("establishment_user_id")));

                                    TextView estAddress = view.findViewById(R.id.est_address_face);
                                    estAddress.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.common_google_signin_btn_text_dark_default));
                                    estAddress.setText(datas.getString("address"));

                                    RatingBar ratingBar = view.findViewById(R.id.rating_bar_face);
                                    ratingBar.setIsIndicator(true);
                                    ratingBar.setNumStars(5);
                                    ratingBar.setStepSize(0.1f);
                                    ratingBar.setRating(Float.parseFloat(rate.getString(datas.getString("est_id"))));

                                    TextView rateTotalAndCount = view.findViewById(R.id.rate_total_and_count_face);
                                    rateTotalAndCount.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.common_google_signin_btn_text_dark_default));
                                    rateTotalAndCount.setText("Total Rate : " + Float.parseFloat(rate.getString(datas.getString("est_id"))) +
                                            " By: " + (Integer.parseInt(rate_count.getString(datas.getString("est_id"))) <= 1 ? Integer.parseInt(rate_count.getString(datas.getString("est_id"))) + " User" : Integer.parseInt(rate_count.getString(datas.getString("est_id"))) + " Users"));

                                    scrollView.addView(view);
                                }
                            }
                        }
                        if(otherLenght != 0) {
                            for (int i = 0; i < otherLenght; i++) {
                                String array = other.getString(i);
                                JSONObject datas = new JSONObject(array);
                                int estStatus = datas.getInt("est_status");
                                if (estStatus == 1) {
                                    View view = inflater.inflate(R.layout.facial_suggestion_content, scrollView, false);
                                    ImageView imageView = view.findViewById(R.id.est_pic_face);
                                    imageView.setPadding(20, 10, 10, 20);
                                    Picasso.with(getApplicationContext())
                                            .load(picUrl(datas.getString("est_front_store")))
                                            .into(imageView);
                                    imageView.setOnClickListener(new FaceSuggestionActivity.ClickEstablishment(datas.getString("establishment_name"), datas.getString("establishment_user_id")));

                                    TextView estName = view.findViewById(R.id.est_name_face);
                                    estName.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.common_google_signin_btn_text_dark_default));
                                    estName.setText(datas.getString("establishment_name"));
                                    estName.setOnClickListener(new FaceSuggestionActivity.ClickEstablishment(datas.getString("establishment_name"), datas.getString("establishment_user_id")));

                                    TextView estAddress = view.findViewById(R.id.est_address_face);
                                    estAddress.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.common_google_signin_btn_text_dark_default));
                                    estAddress.setText(datas.getString("address"));

                                    RatingBar ratingBar = view.findViewById(R.id.rating_bar_face);
                                    ratingBar.setIsIndicator(true);
                                    ratingBar.setNumStars(5);
                                    ratingBar.setStepSize(0.1f);
                                    ratingBar.setRating(Float.parseFloat(rate.getString(datas.getString("est_id"))));

                                    TextView rateTotalAndCount = view.findViewById(R.id.rate_total_and_count_face);
                                    rateTotalAndCount.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.common_google_signin_btn_text_dark_default));
                                    rateTotalAndCount.setText("Total Rate : " + Float.parseFloat(rate.getString(datas.getString("est_id"))) +
                                            " By: " + (Integer.parseInt(rate_count.getString(datas.getString("est_id"))) <= 1 ? Integer.parseInt(rate_count.getString(datas.getString("est_id"))) + " User" : Integer.parseInt(rate_count.getString(datas.getString("est_id"))) + " Users"));

                                    scrollView.addView(view);
                                }
                            }
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
    public String picUrl(String path){
        String[] pic = path.split("/");
        return Constant.PUBLIC_IMAGE_PATH + pic[7];
    }

    public class ClickEstablishment implements View.OnClickListener
    {

        String estName;
        String id;
        ClickEstablishment(String key,String id) {
            this.estName = key;
            this.id = id;
        }

        @Override
        public void onClick(View v)
        {
            Intent estProduct = new Intent(getApplicationContext(),EstablishmentDetailsActivity.class);
            Bundle establishmentname = new Bundle();
            establishmentname.putString("estName",estName);
            establishmentname.putString("id",id);
            estProduct.putExtras(establishmentname);
            startActivity(estProduct);
        }
    }
}
