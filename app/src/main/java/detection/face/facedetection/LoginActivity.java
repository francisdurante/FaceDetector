package detection.face.facedetection;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class LoginActivity extends AppCompatActivity {
    EditText un = null;
    EditText pass = null;
    SharedPreferences spf;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        if(!"".equals(getString("account_id")) &&
                !"".equals(getString("first_name")) &&
                !"".equals(getString("last_name"))){
            Intent redirect = new Intent(getApplicationContext(),MainActivity.class);
            startActivity(redirect);
            finish();
        }
        un = findViewById(R.id.username_login);
        pass = findViewById(R.id.password_login);
    }

    public void login(View v) {
        RequestParams rp = new RequestParams();
        rp.add("username", un.getText().toString());
        rp.add("password", pass.getText().toString());
        rp.add("for_log", "mobile_app");
        rp.add("pass", "for_login");

        Utility.getByUrl(Constant.LOGIN_URL, rp, new JsonHttpResponseHandler() {
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
                   JSONObject object = new JSONObject(response.toString());
                    String status = object.getString("status");
                    if("sucess".equals(status)) {
                        JSONArray data = new JSONArray(object.getString("data"));
                        String array = data.getString(0);
                        JSONObject datas = new JSONObject(array);
                        int accountStatus = datas.getInt("status");
                        if(accountStatus == 1){
                            GlobalVO.setFirstName(datas.getString("first_name"));
                            GlobalVO.setLastname(datas.getString("last_name"));
                            GlobalVO.setAccounId(datas.getString("id"));
                            save("first_name",GlobalVO.getFirstName());
                            save("last_name",GlobalVO.getLastname());
                            save("account_id",GlobalVO.getAccounId());
                            Intent estList = new Intent(getApplicationContext(),EstablishmentListActivity.class);
                            startActivity(estList);
                            finish();
                            Toast.makeText(getApplicationContext(),Constant.LOGIN_SUCCESS,Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(getApplicationContext(),Constant.INACTIVE_USER,Toast.LENGTH_SHORT).show();
                        }
                    }else{
                        Toast.makeText(getApplicationContext(),Constant.LOGIN_FAIL,Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    public void save(String key, String value) {

        spf = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor edit = spf.edit();
        edit.putString(key, value);
        edit.apply();

    }
    public String getString(String key) {

        spf = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        return spf.getString(key,"");
    }
    public void registration (View v){
        Intent registration = new Intent(getApplicationContext(),RegistrationActivity.class);
        startActivity(registration);
    }
}
