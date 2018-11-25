package detection.face.facedetection;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class EstablishmentDetailsActivity extends AppCompatActivity {
    LinearLayout scrollView = null;
    LinearLayout scrollViewComment;
    int length = 0;
    TextView estNameLayout = null;
    TextView noResultFound;
    String lon;
    String lat;
    String id = null;
    String est_id;
    SharedPreferences spf;
    String est_name;
    String rate;
    String address;
    ProgressDialog mDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_establishment_details);
        Bundle establishmentName = getIntent().getExtras();
        estNameLayout = findViewById(R.id.est_name_product);
        estNameLayout.setText(establishmentName.getString("estName"));
        getEstDetails(establishmentName.getString("estName"));

        est_name = establishmentName.getString("estName");
        id = establishmentName.getString("id");
        rate = establishmentName.getString("rate");
        address = establishmentName.getString("address");

        getProduct();
    }

    public void getProduct() {
        RequestParams rp = new RequestParams();
        rp.add("pass", "get_product");
        rp.add("for_process", "all_active");
        rp.add("id",id);
        showProgressBar();
        Utility.getByUrl(Constant.GET_PRODUCT_BY_ESTABLISHMENT, rp, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    scrollView = findViewById(R.id.product_list);
                    LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
                    JSONObject object = new JSONObject(response.toString());
                    String status = object.getString("status");
                    if ("success".equals(status)) {
                        JSONArray data = new JSONArray(object.getString("data"));
                        length = data.length();
                        if(length == 0) {
                            noResultFound = findViewById(R.id.no_result_found);
                            noResultFound.setVisibility(View.VISIBLE);
                        }

                        for (int x = 0; x < length; x++) {
                            String array = data.getString(x);
                            JSONObject datas = new JSONObject(array);
                            int itemName = datas.getInt("item_status");
                            if (itemName == 1) {
                                View view = inflater.inflate(R.layout.est_product_list,scrollView,false);
                                ImageView imageView = view.findViewById(R.id.prod_pic);
                                Picasso.with(getApplicationContext())
                                        .load(picUrl(datas.getString("path")))
                                        .into(imageView);

                                TextView prodName = view.findViewById(R.id.product_name);
                                prodName.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.common_google_signin_btn_text_dark_default));
                                prodName.setText(datas.getString("item_name"));

                                TextView prodPrice = view.findViewById(R.id.product_price);
                                prodPrice.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.common_google_signin_btn_text_dark_default));
                                prodPrice.setText("PHP "+datas.getString("price"));

                                TextView productCategory = view.findViewById(R.id.product_category);
                                productCategory.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.common_google_signin_btn_text_dark_default));
                                productCategory.setText(datas.getString("category_name"));

                                scrollView.addView(view);
                            }
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                hideProgressBar();
            }

        });

    }

    public void getEstDetails(String estName) {
        RequestParams rp = new RequestParams();
        rp.add("pass", "get_all_est_user");
        rp.add("key", estName);

        Utility.getByUrl(Constant.GET_REGISTERED_EST, rp, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    JSONObject object = new JSONObject(response.toString());
                    String status = object.getString("status");
                    if ("success".equals(status)) {
                        JSONArray data = new JSONArray(object.getString("data"));
                        length = data.length();
                        String array = data.getString(0);
                        JSONObject datas = new JSONObject(array);
                        int estStatus = datas.getInt("est_status");
                        if (estStatus == 1) {
                            est_id = datas.getString("est_id");
                            //getAllComment(est_id);
                            lon = datas.getString("location_longitude");
                            lat = datas.getString("location_latitude");
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

    public void map (View v){
        Bundle b = new Bundle();
        b.putString("lat",lat);
        b.putString("lon",lon);
        Intent map = new Intent(getApplicationContext(),MapsActivity.class);
        map.putExtras(b);
        startActivity(map);
    }

//    private void getAllComment(String est_id){
//        RequestParams rp = new RequestParams();
//        rp.add("pass", "get_comment_by_est");
//        rp.add("est_id", est_id);
//
//        Utility.getByUrl(Constant.GET_COMMENT_BY_EST, rp, new JsonHttpResponseHandler() {
//            @Override
//            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
//                try {
//                    scrollViewComment = findViewById(R.id.comment_list);
//                    LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
//                    JSONObject object = new JSONObject(response.toString());
//                    String status = object.getString("status");
//                    if ("success".equals(status)) {
//                        JSONArray data = new JSONArray(object.getString("data"));
//                        length = data.length();
//                        for (int x = 0; x < length; x++) {
//                            String array = data.getString(x);
//                            JSONObject datas = new JSONObject(array);
//                            View view = inflater.inflate(R.layout.est_comment_list, scrollViewComment, false);
//
//                            TextView prodName = view.findViewById(R.id.user_commented);
//                            prodName.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.common_google_signin_btn_text_dark_default));
//                            prodName.setText(datas.getString("username"));
//
//                            TextView prodPrice = view.findViewById(R.id.comment);
//                            prodPrice.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.common_google_signin_btn_text_dark_default));
//                            prodPrice.setText(datas.getString("user_comment"));
//
//                            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//
//                            String dateString = datas.getString("comment_date");
//                            Date date = formatter.parse(dateString);
//                            SimpleDateFormat formatOut = new SimpleDateFormat("MMMM dd, yyyy HH:mm");
//
//                            TextView productCategory = view.findViewById(R.id.date_comment);
//                            productCategory.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.common_google_signin_btn_text_dark_default));
//                            productCategory.setText(formatOut.format(date));
//
//                            scrollViewComment.addView(view);
//                        }
//                    }
//                } catch (JSONException | ParseException e) {
//                    e.printStackTrace();
//                }
//            }
//        });
//    }
//    private void submitReview(String est_id,String Comment, String rate){
//        RequestParams rp = new RequestParams();
//        rp.add("pass", "submit_rate_comment");
//        rp.add("est_id", est_id);
//        rp.add("user_id", getString("account_id"));
//        rp.add("comment", Comment);
//        rp.add("rate", rate);
//
//        Utility.getByUrl(Constant.SUBMIT_COMMENT_AND_RATE, rp, new JsonHttpResponseHandler() {
//            @Override
//            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
//                try {
//                    JSONObject object = new JSONObject(response.toString());
//                    String status = object.getString("status");
//                    if ("success".equals(status)) {
//                        Toast.makeText(getApplicationContext(),"Thanks for Rating",Toast.LENGTH_LONG).show();
//                        save("comment_"+est_name,"rated");
//                    }
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            }
//        });
//    }

//    public void rateMeLinkOnClick(View v) {
//        if(getString("comment_"+est_name).equals("")) {
//            final AlertDialog.Builder builder;
//
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//
//                builder = new AlertDialog.Builder(EstablishmentDetailsActivity.this);
//
//            } else {
//
//                builder = new AlertDialog.Builder(EstablishmentDetailsActivity.this);
//
//            }
//
//            builder.setTitle("Rate And Comment For " + est_name);
//
//            builder.setMessage("Your rate and comment will help others.");
//            final RatingBar rateBar = new RatingBar(getApplicationContext());
//            rateBar.setNumStars(5);
//            rateBar.setMax(5);
//            rateBar.setStepSize(1f);
//            final EditText input = new EditText(getApplicationContext());
//            LinearLayout ll = new LinearLayout(EstablishmentDetailsActivity.this);
//            LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(
//                    LinearLayout.LayoutParams.WRAP_CONTENT,
//                    LinearLayout.LayoutParams.WRAP_CONTENT, 1);
//            ll.setGravity(Gravity.CENTER);
//            ll.setOrientation(LinearLayout.VERTICAL);
//            ll.addView(rateBar, param);
//            ll.addView(input);
//            builder.setView(ll);
//
//
//            builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialog, int which) {
//                    if (input.getText().length() >= 50) {
//                        Toast.makeText(getApplicationContext(), "Limit of 50 characters", Toast.LENGTH_LONG).show();
//                    } else if (rateBar.getRating() == 0) {
//                        Toast.makeText(getApplicationContext(), "Please rate minimum of 1 star", Toast.LENGTH_LONG).show();
//                    } else {
//                        submitReview(est_id, input.getText().toString(), Float.toString(rateBar.getRating()));
//                    }
//                }
//            });
//            AlertDialog a = builder.create();
//            a.show();
//        }else{
//            Toast.makeText(getApplicationContext(),"You already rated "+est_name,Toast.LENGTH_LONG).show();
//        }
//    }

    public void showReviews(View view){
        Intent reviewActivity = new Intent(this,ReviewsActivity.class);
        Bundle reviewData = new Bundle();
        reviewData.putString("est_name",est_name);
        reviewData.putString("est_id",est_id);
        reviewData.putString("rate",rate);
        reviewData.putString("address",address);
        reviewActivity.putExtras(reviewData);
        startActivity(reviewActivity);
    }




    public String getString(String key) {
        spf = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        return spf.getString(key, "");
    }
    public void save(String key, String value) {

        spf = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor edit = spf.edit();
        edit.putString(key, value);
        edit.apply();

    }

    public void showProgressBar(){
        mDialog = new ProgressDialog(EstablishmentDetailsActivity.this);
        mDialog.setMessage(Constant.LOADING);
        mDialog.setCanceledOnTouchOutside(false);
        mDialog.show();
    }
    public void hideProgressBar(){
        mDialog.dismiss();
    }
}
