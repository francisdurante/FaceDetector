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
import android.widget.TextView;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class EstablishmentListActivity extends AppCompatActivity {
    int lenght;
    LinearLayout scrollView;
    TextView searchKey;
    Button searchButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_establishment_list);
        getWindow().setBackgroundDrawableResource(R.drawable.background_image);
        searchKey = findViewById(R.id.search_box);
        searchButton = findViewById(R.id.search_est_button);
        searchButton.setText(Constant.SEARCH);
        getEstRegistered("","");
    }

    public void getEstRegistered(String est_key,String category_key) {
        RequestParams rp = new RequestParams();
        rp.add("pass", "get_all_est_user");
        if(!"".equals(est_key)){
            rp.add("key", est_key);
        }if(!"".equals(category_key)){
            rp.add("filter",category_key);
        }
        searchButton.setText(Constant.LOADING);
        searchButton.setEnabled(false);
        Utility.getByUrl(Constant.GET_REGISTERED_EST, rp, new JsonHttpResponseHandler() {
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
                                imageView.setPadding(20,10,10,20);
                                Picasso.with(getApplicationContext())
                                        .load(picUrl(datas.getString("est_front_store")))
                                        .into(imageView);
                                imageView.setOnClickListener(new ClickEstablishment(datas.getString("establishment_name"),datas.getString("establishment_user_id")));

                                TextView estName = view.findViewById(R.id.est_name);
                                estName.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.common_google_signin_btn_text_dark_default));
                                estName.setText(datas.getString("establishment_name"));
                                estName.setOnClickListener(new ClickEstablishment(datas.getString("establishment_name"),datas.getString("establishment_user_id")));

                                TextView estAddress = view.findViewById(R.id.est_address);
                                estAddress.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.common_google_signin_btn_text_dark_default));
                                estAddress.setText(datas.getString("address"));

                                RatingBar ratingBar = view.findViewById(R.id.rating_bar);
                                ratingBar.setIsIndicator(true);
                                ratingBar.setNumStars(5);
                                ratingBar.setRating(3);

                                scrollView.addView(view);
                            }
                            searchButton.setText(Constant.SEARCH);
                            searchButton.setEnabled(true);
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

    public void searchButton(View v){
        scrollView.removeAllViews();
        getEstRegistered(searchKey.getText().toString(),"");
    }
}
