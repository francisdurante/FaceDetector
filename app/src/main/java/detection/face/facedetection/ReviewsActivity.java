package detection.face.facedetection;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import cz.msebera.android.httpclient.Header;

public class ReviewsActivity extends AppCompatActivity {

    LinearLayout scrollViewComment;
    SharedPreferences spf;
    String est_name;
    String est_id;
    String est_rate;
    String est_address_rating;
    TextView rate;
    TextView est_name_rate;
    TextView address_rate;
    TextView noComment;
    ProgressDialog mDialog;
    int length;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reviews);

        Bundle reviewNeedData = getIntent().getExtras();
        est_rate = reviewNeedData.getString("rate");
        est_address_rating = reviewNeedData.getString("address");
        est_name = reviewNeedData.getString("est_name");
        est_id = reviewNeedData.getString("est_id");

        est_name_rate = findViewById(R.id.est_name_rating);
        rate = findViewById(R.id.rating);
        address_rate = findViewById(R.id.est_address_rating);

        est_name_rate.setText(est_name);
        rate.setText(est_rate);
        address_rate.setText(est_address_rating);


        getAllComment(est_id);
    }

    private void getAllComment(String est_id){
        RequestParams rp = new RequestParams();
        rp.add("pass", "get_comment_by_est");
        rp.add("est_id", est_id);
        showProgressBar();
        Utility.getByUrl(Constant.GET_COMMENT_BY_EST, rp, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    scrollViewComment = findViewById(R.id.comment_list);
                    LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
                    JSONObject object = new JSONObject(response.toString());
                    String status = object.getString("status");
                    if ("success".equals(status)) {
                        JSONArray data = new JSONArray(object.getString("data"));
                        length = data.length();
                        if(length == 0) {
                            noComment = findViewById(R.id.no_comment);
                            noComment.setVisibility(View.VISIBLE);
                        }
                        for (int x = 0; x < length; x++) {
                            String array = data.getString(x);
                            JSONObject datas = new JSONObject(array);
                            View view = inflater.inflate(R.layout.est_comment_list, scrollViewComment, false);

                            TextView prodName = view.findViewById(R.id.user_commented);
                            prodName.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.common_google_signin_btn_text_dark_default));
                            prodName.setText(" "+datas.getString("username"));

                            TextView prodPrice = view.findViewById(R.id.comment);
                            prodPrice.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.common_google_signin_btn_text_dark_default));
                            prodPrice.setText(datas.getString("user_comment"));

                            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

                            String dateString = datas.getString("comment_date");
                            Date date = formatter.parse(dateString);
                            SimpleDateFormat formatOut = new SimpleDateFormat("MMMM dd, yyyy HH:mm");

                            TextView productCategory = view.findViewById(R.id.date_comment);
                            productCategory.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.common_google_signin_btn_text_dark_default));
                            productCategory.setText(formatOut.format(date));

                            scrollViewComment.addView(view);
                        }
                    }
                } catch (JSONException | ParseException e) {
                    e.printStackTrace();
                }

                hideProgressBar();
            }
        });
    }
    private void submitReview(String est_id,String Comment, String rate){
        RequestParams rp = new RequestParams();
        rp.add("pass", "submit_rate_comment");
        rp.add("est_id", est_id);
        rp.add("user_id", getString("account_id"));
        rp.add("comment", Comment);
        rp.add("rate", rate);

        Utility.getByUrl(Constant.SUBMIT_COMMENT_AND_RATE, rp, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    JSONObject object = new JSONObject(response.toString());
                    String status = object.getString("status");
                    if ("success".equals(status)) {
                        Toast.makeText(getApplicationContext(),"Thanks for Rating",Toast.LENGTH_LONG).show();
                        save("comment_"+est_name,"rated");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void rateMeLinkOnClick(View v) {
        if(getString("comment_"+est_name).equals("")) {
            final AlertDialog.Builder builder;

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

                builder = new AlertDialog.Builder(ReviewsActivity.this);

            } else {

                builder = new AlertDialog.Builder(ReviewsActivity.this);

            }

            builder.setTitle("Rate And Comment For " + est_name);

            builder.setMessage("Your rate and comment will help others.");
            final RatingBar rateBar = new RatingBar(getApplicationContext());
            rateBar.setNumStars(5);
            rateBar.setMax(5);
            rateBar.setStepSize(1f);
            final EditText input = new EditText(getApplicationContext());
            LinearLayout ll = new LinearLayout(ReviewsActivity.this);
            LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT, 1);
            ll.setGravity(Gravity.CENTER);
            ll.setOrientation(LinearLayout.VERTICAL);
            ll.addView(rateBar, param);
            ll.addView(input);
            builder.setView(ll);


            builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (input.getText().length() >= 50) {
                        Toast.makeText(getApplicationContext(), "Limit of 50 characters", Toast.LENGTH_LONG).show();
                    } else if (rateBar.getRating() == 0) {
                        Toast.makeText(getApplicationContext(), "Please rate minimum of 1 star", Toast.LENGTH_LONG).show();
                    } else {
                        submitReview(est_id, input.getText().toString(), Float.toString(rateBar.getRating()));
                    }
                }
            });
            AlertDialog a = builder.create();
            a.show();
        }else{
            Toast.makeText(getApplicationContext(),"You already rated "+est_name,Toast.LENGTH_LONG).show();
        }
    }

    public String getString(String key) {

        spf = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        return spf.getString(key,"");
    }
    public void save(String key, String value) {

        spf = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor edit = spf.edit();
        edit.putString(key, value);
        edit.apply();

    }
    public void showProgressBar(){
        mDialog = new ProgressDialog(ReviewsActivity.this);
        mDialog.setMessage(Constant.LOADING);
        mDialog.setCanceledOnTouchOutside(false);
        mDialog.show();
    }
    public void hideProgressBar(){
        mDialog.dismiss();
    }
}

