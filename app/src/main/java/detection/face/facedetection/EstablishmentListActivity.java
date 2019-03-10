package detection.face.facedetection;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class EstablishmentListActivity extends AppCompatActivity {

    Context mContext = this;
    int lenght;
    LinearLayout scrollView;
    TextView searchKey;
    ImageButton searchButton;
    Spinner mSpinner;
    SharedPreferences spf;
    PopupMenu popupMenu;
    ProgressDialog mDialog;
    LinearLayout questionLinear;
    ArrayList<String> estNameDisplay;

    public void showPopup(View v) {
        popupMenu = new PopupMenu(this, v);
        MenuInflater inflater = popupMenu.getMenuInflater();
        inflater.inflate(R.menu.menu_main, popupMenu.getMenu());
        popupMenu.show();

        popupMenu.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case R.id.action_logout:
                   new AlertDialog.Builder(mContext)
                            .setPositiveButton("YES", (dialog, which) -> {
                                save("account_id","");
                                save("first_name","");
                                save("last_name","");
                                startActivity(new Intent(getApplicationContext(),LoginActivity.class));
                                finish();
                            })
                            .setMessage("Are you sure you want to log out?")
                            .setTitle("Log out")
                            .setNegativeButton("NO",((dialog, which) -> {
                               dialog.dismiss();
                            })).show();
                    break;
            }
            return false;
        });

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_establishment_list);
        questionLinear = findViewById(R.id.questions_place);

        getWindow().setBackgroundDrawableResource(R.drawable.background_image);
        searchKey = findViewById(R.id.search_box);
        searchButton = findViewById(R.id.search_est_button);
        mSpinner = findViewById(R.id.filter);
//        searchButton.setText(Constant.SEARCH);
        ArrayAdapter<String> mAdapter = new ArrayAdapter<String>(getApplicationContext(),
                android.R.layout.simple_list_item_1,getResources().getStringArray(R.array.filter));
        mAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinner.setAdapter(mAdapter);
        mSpinner.setOnItemSelectedListener(listener);
        getEstRegistered("","",GlobalVO.getPreferredFood());
        if(!"1".equals(getString("ANSWERED_SURVEY_" + getString("account_id") ))) {
            Utility.popupForQuestions(mContext, this);
        }
        Utility.getRandomTrivia(mContext,this);
    }

    public void getEstRegistered(String est_key,String category_key,String food) {
        RequestParams rp = new RequestParams();
        rp.add("pass", "get_all_est_user");
        if(!"".equals(est_key)){
            rp.add("key", est_key);
        }if(!"".equals(category_key)){
            rp.add("filter",category_key);
        }if(!"".equals(food)){
            rp.add("food",food);
        }
//        searchButton.setText(Constant.LOADING);
        searchButton.setEnabled(false);
        showProgressBar();
        Utility.getByUrl(Constant.GET_REGISTERED_EST, rp, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    boolean duplicate;
                    estNameDisplay = new ArrayList<String>();
                    scrollView = findViewById(R.id.list);
                    LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
                    JSONObject object = new JSONObject(response.toString());
                    String status = object.getString("status");
                    if ("success".equals(status)) {
                        JSONArray data = new JSONArray(object.getString("data"));
                        lenght = data.length();
                        for (int x = 0; x < lenght; x++) {
                            String array = data.getString(x);
                            JSONObject datas = new JSONObject(array);
                            if(x != 0 && estNameDisplay.contains(datas.getString("establishment_name"))){
                                duplicate = true;
                            }else {
                                estNameDisplay.add(datas.getString("establishment_name"));
                                duplicate = false;
                            }
                            int estStatus = datas.getInt("est_status");
                            if (estStatus == 1 && !duplicate) {
                                View view = inflater.inflate(R.layout.est_list_item, scrollView, false);
                                ImageView imageView = view.findViewById(R.id.est_pic);
                                imageView.setPadding(20, 10, 10, 20);
                                Picasso.with(getApplicationContext())
                                        .load(picUrl(datas.getString("est_front_store")))
                                        .error(R.drawable.default_image_thumbnail)
                                        .into(imageView);
                                imageView.setOnClickListener(new ClickEstablishment(datas.getString("establishment_name"), datas.getString("establishment_user_id"),String.format("%.1f", datas.getDouble("rate")),datas.getString("address")));

                                TextView estName = view.findViewById(R.id.est_name);
                                estName.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.common_google_signin_btn_text_dark_default));
                                estName.setText(datas.getString("establishment_name"));
                                estName.setOnClickListener(new ClickEstablishment(datas.getString("establishment_name"), datas.getString("establishment_user_id"),String.format("%.1f", datas.getDouble("rate")),datas.getString("address")));

                                TextView estAddress = view.findViewById(R.id.est_address);
                                estAddress.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.common_google_signin_btn_text_dark_default));
                                estAddress.setText(datas.getString("address"));

                                RatingBar ratingBar = view.findViewById(R.id.rating_bar);
                                ratingBar.setIsIndicator(true);
                                ratingBar.setNumStars(5);
                                ratingBar.setStepSize(0.1f);
                                ratingBar.setRating(datas.getLong("rate"));

                                TextView rateTotalAndCount = view.findViewById(R.id.rate_total_and_count);
                                rateTotalAndCount.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.common_google_signin_btn_text_dark_default));
                                double totalRateDouble = (datas.getDouble("rate"));
                                int totalRateCount = datas.getInt("rate_count");
                                rateTotalAndCount.setText("Total Rate : " + String.format("%.1f", totalRateDouble) +
                                        " By: " + (totalRateCount <= 1 ? datas.getInt("rate_count") + " User" : datas.getInt("rate_count") +" Users" ));

                                scrollView.addView(view);
                            }
//                            searchButton.setText(Constant.SEARCH);
                            searchButton.setEnabled(true);
                        }
                    }else{
//                        searchButton.setText(Constant.SEARCH);
                        searchButton.setEnabled(true);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                hideProgressBar();
            }
        });

    }
    public String picUrl(String path){
        String[] pic = path.split("/");
        try {
            return Constant.PUBLIC_IMAGE_PATH + pic[7];
        }catch(Exception ignore){
            return Constant.PUBLIC_IMAGE_PATH + pic[0];
        }
    }
    public class ClickEstablishment implements View.OnClickListener
    {
        String estName;
        String id;
        String rate;
        String address;
        ClickEstablishment(String key,String id,String rate, String address) {
            this.estName = key;
            this.id = id;
            this.rate = rate;
            this.address = address;
        }

        @Override
        public void onClick(View v)
        {
            Intent estProduct = new Intent(getApplicationContext(),EstablishmentDetailsActivity.class);
            Bundle establishmentname = new Bundle();
            establishmentname.putString("estName",estName);
            establishmentname.putString("id",id);
            establishmentname.putString("rate",rate);
            establishmentname.putString("address",address);
            estProduct.putExtras(establishmentname);
            startActivity(estProduct);
        }

    }

    public void searchButton(View v){
        scrollView.removeAllViews();
        if(mSpinner.getSelectedItem().toString().equals("Establishment")) {
            getEstRegistered(searchKey.getText().toString(), "", "");
        }else if(mSpinner.getSelectedItem().toString().equals("Food")){
            getEstRegistered("", "", searchKey.getText().toString());
        }
    }
    public void detectFace(View v){
        startActivity(new Intent(getApplicationContext(),MainActivity.class));
    }
    private AdapterView.OnItemSelectedListener listener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            ((TextView) parent.getChildAt(0)).setTextColor(0xFFFFFFFF);
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };
    public void save(String key, String value) {

        spf = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor edit = spf.edit();
        edit.putString(key, value);
        edit.apply();

    }
    public void bakeShopOnlick(View v){
        if(scrollView.getChildCount() != 0)
            scrollView.removeAllViews();
        getEstRegistered("","Bake Shop","");
    }
    public void burgerShopOnClick(View v){
        if(scrollView.getChildCount() != 0)
            scrollView.removeAllViews();
        getEstRegistered("","Burger Stall","");
    }
    public void coffeeShopOnClick(View v){
        if(scrollView.getChildCount() != 0)
            scrollView.removeAllViews();
        getEstRegistered("","Coffee Shop","");
    }
    public void iceCreamShopOnClick(View v){
        if(scrollView.getChildCount() != 0)
            scrollView.removeAllViews();
        getEstRegistered("","Dessert","");
    }
    public void pizzaShopOnlick(View v){
        if(scrollView.getChildCount() != 0)
            scrollView.removeAllViews();
        getEstRegistered("","Pizza House","");
    }
    public void restaurantOnClick(View v){
        if(scrollView.getChildCount() != 0)
            scrollView.removeAllViews();
        getEstRegistered("","Eatery","");
    }
    public void showProgressBar(){
        mDialog = new ProgressDialog(EstablishmentListActivity.this);
        mDialog.setMessage(Constant.SEARCHING);
        mDialog.setCanceledOnTouchOutside(false);
        mDialog.show();
    }
    public void hideProgressBar(){
        mDialog.dismiss();
    }
    public String getString(String key) {

        spf = PreferenceManager.getDefaultSharedPreferences(mContext);
        return spf.getString(key,"");
    }
}
