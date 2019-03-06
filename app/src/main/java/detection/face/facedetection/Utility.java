package detection.face.facedetection;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.CountDownTimer;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import cz.msebera.android.httpclient.Header;

public class Utility {
    private static final String BASE_URL = "http://api.twitter.com/1/";
    private static AsyncHttpClient client = new AsyncHttpClient();
    static String statusReward = "";
    private static Context mContext;
    static SharedPreferences spf;

    public static void get(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.get(getAbsoluteUrl(url), params, responseHandler);
    }

    public static void post(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.post(getAbsoluteUrl(url), params, responseHandler);
    }

    public static void getByUrl(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.get(url, params, responseHandler);
    }

    public static void postByUrl(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.post(url, params, responseHandler);
    }

    private static String getAbsoluteUrl(String relativeUrl) {
        return BASE_URL + relativeUrl;
    }
    public static void login(String username, String password){
        RequestParams rp = new RequestParams();
        rp.add("username",username);
        rp.add("password",password);
        rp.add("for_log","mobile_app");
        rp.add("pass","for_login");

        getByUrl(Constant.LOGIN_URL,rp,new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                // If the response is JSONObject instead of expected JSONArray
                try {
                    JSONObject serverResp = new JSONObject(response.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                try {
                    JSONArray mJsonArray = new JSONArray(response.toString());
                    JSONObject mJsonObject = mJsonArray.getJSONObject(0);
                    int status = mJsonObject.getInt("status");
                    if(status == 1){
                        //active
                        JSONArray datas = mJsonObject.getJSONArray("data");
                        for (int i = 0; i < datas.length(); i++) {
                            JSONObject data = datas.getJSONObject(i);
                            GlobalVO.setFirstName(data.getString("first_name"));
                            GlobalVO.setLastname(data.getString("last_name"));
                        }
                    }else{
                        Toast.makeText(mContext,"User is InActive",Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public static void getRandomTrivia(final Context context , final Activity activity){
        final AlertDialog alertDialog = new AlertDialog.Builder(
                        context).create();
        final Handler mHandler = new Handler();
        new Thread(new Runnable() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                while (true) {
                    try {
                        Thread.sleep(1000*60*60);
                        mHandler.post(new Runnable() {

                            @Override
                            public void run() {
                                if(!alertDialog.isShowing()) {
                                    final String[] array = context.getResources().getStringArray(R.array.trivia);
                                    final String randomStr = array[new Random().nextInt(array.length)];
                                    alertDialog.setTitle("WE HAVE A FOOD TRIVIA FOR YOU");
                                    alertDialog.setMessage(randomStr);
                                    alertDialog.show();
                                }else{
                                    new CountDownTimer(3000, 1000) {
                                        @Override
                                        public void onTick(long millisUntilFinished) {

                                        }

                                        @Override
                                        public void onFinish() {
                                            alertDialog.dismiss();
                                        }
                                    }.start();
                                }
                            }
                        });
                    } catch (Exception e) {
                        // TODO: handle exception
                    }
                }
            }
        }).start();
    }

    public static void popupForQuestions(final Context context, final Activity activity){

        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
        alertDialog.setTitle("Questions");
        alertDialog.setMessage("Please Answer the following questions.");
        alertDialog.setPositiveButton("OK", (dialog, which) -> activity.startActivity(new Intent(context,QuestionsActivity.class)));
        alertDialog.show();
    }

    public static void save(String key, String value,Context context) {
        spf = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor edit = spf.edit();
        edit.putString(key, value);
        edit.apply();
    }
    public static String getString(String key,Context context) {
        spf = PreferenceManager.getDefaultSharedPreferences(context);
        return spf.getString(key,"");
    }
}
