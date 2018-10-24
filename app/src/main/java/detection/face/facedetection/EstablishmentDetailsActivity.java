package detection.face.facedetection;

import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
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

import cz.msebera.android.httpclient.Header;

public class EstablishmentDetailsActivity extends AppCompatActivity {
    LinearLayout scrollView = null;
    int length = 0;
    TextView estNameLayout = null;
    String lon;
    String lat;
    String id = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_establishment_details);
        Bundle establishmentName = getIntent().getExtras();
        estNameLayout = findViewById(R.id.est_name_product);
        estNameLayout.setText(establishmentName.getString("estName"));
        getEstDetails(establishmentName.getString("estName"));
        id = establishmentName.getString("id");
        getProduct();
    }

    public void getProduct() {
        System.out.println(id + " aaaaa");
        RequestParams rp = new RequestParams();
        rp.add("pass", "get_product");
        rp.add("for_process", "all_active");
        rp.add("id",id);

        Utility.getByUrl(Constant.GET_PRODUCT_BY_ESTABLISHMENT, rp, new JsonHttpResponseHandler() {
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
                    scrollView = findViewById(R.id.product_list);
                    LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
                    JSONObject object = new JSONObject(response.toString());
                    String status = object.getString("status");
                    if ("success".equals(status)) {
                        JSONArray data = new JSONArray(object.getString("data"));
                        length = data.length();
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
            }
        });

    }

    public void getEstDetails(String estName) {
        RequestParams rp = new RequestParams();
        rp.add("pass", "get_all_est_user");
        rp.add("key", estName);

        Utility.getByUrl(Constant.GET_REGISTERED_EST, rp, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                // If the response is JSONObject instead of expected JSONArray
                try {
                    System.out.println(response + " aaaaaaaaaaaaaaaaaaaaaa");
                    JSONArray data = new JSONArray(response.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    System.out.println(response + " aaaaaaaaaaaaaaaaaaaaaa");
                    JSONObject object = new JSONObject(response.toString());
                    String status = object.getString("status");
                    if ("success".equals(status)) {
                        JSONArray data = new JSONArray(object.getString("data"));
                        length = data.length();
                        String array = data.getString(0);
                        JSONObject datas = new JSONObject(array);
                        int estStatus = datas.getInt("est_status");
                        if (estStatus == 1) {
                            lon = datas.getString("location_longitude");
                            lat = datas.getString("location_latitude");
                            System.out.println(lat+ " aaaaaa " + lon);
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
        System.out.println(lat+ " aaaaaa " + lon);
        Intent map = new Intent(getApplicationContext(),MapsActivity.class);
        map.putExtras(b);
        startActivity(map);
    }
}
