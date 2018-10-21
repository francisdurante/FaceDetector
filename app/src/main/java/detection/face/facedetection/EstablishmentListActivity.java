package detection.face.facedetection;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
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
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import cz.msebera.android.httpclient.Header;

public class EstablishmentListActivity extends AppCompatActivity {
    int lenght;
    LinearLayout scrollView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_establishment_list);
        getWindow().setBackgroundDrawableResource(R.drawable.background_image);

        getEstRegistered();
    }

    public void init() {
        scrollView = findViewById(R.id.list);
        LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
        for (int x = 0 ; x<lenght; x++){
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

    public void getEstRegistered() {
        RequestParams rp = new RequestParams();
        rp.add("pass", "get_all_est_user");

        Utility.getByUrl(Constant.GET_REGISTERED_EST, rp, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                // If the response is JSONObject instead of expected JSONArray
                try {
                    JSONArray data = new JSONArray(response.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    scrollView = findViewById(R.id.list);
                    LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
                    JSONObject object = new JSONObject(response.toString());
                    String status = object.getString("status");
                    if ("success".equals(status)) {
                        JSONArray data = new JSONArray(object.getString("data"));
                        lenght = data.length();
                        System.out.println(lenght + " aaaaaaaaa");
                        for (int x = 0; x < lenght; x++) {
                            String array = data.getString(x);
                            JSONObject datas = new JSONObject(array);
                            int estStatus = datas.getInt("est_status");
                            if (estStatus == 1) {
                                View view = inflater.inflate(R.layout.est_list_item,scrollView,false);
                                ImageView imageView = view.findViewById(R.id.est_pic);
                                Picasso.with(getApplicationContext())
                                        .load(picUrl(datas.getString("est_front_store")))
                                        .into(imageView);
                                TextView estName = view.findViewById(R.id.est_name);
                                estName.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.common_google_signin_btn_text_dark_default));
                                estName.setText(datas.getString("establishment_name"));

                                TextView estAddress = view.findViewById(R.id.est_address);
                                estAddress.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.common_google_signin_btn_text_dark_default));
                                estAddress.setText(datas.getString("address"));

                                RatingBar ratingBar = view.findViewById(R.id.rating_bar);
                                ratingBar.setIsIndicator(true);
                                ratingBar.setNumStars(5);
                                ratingBar.setRating(3);

                                scrollView.addView(view);
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
        return "https://darkened-career.000webhostapp.com/images/" + pic[7];
    }
}
