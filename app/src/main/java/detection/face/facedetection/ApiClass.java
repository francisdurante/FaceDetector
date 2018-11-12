package detection.face.facedetection;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Handler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class ApiClass extends AsyncTask<Context,Void,String> {
    static SharedPreferences spf;
    String url = "";
    Handler h = new Handler();
    public String result = "";
    public String registrationKey = "user_registration";
    public int requestCode = 0;

    public ApiClass(String url,String firstName, String lastName, String username, String email,String password,String age,String pFood,int requestCode) {
        this.url = url +
                "?username=" + username +
                "&email=" + email +
                "&pass=" + registrationKey +
                "&first_name=" + firstName +
                "&last_name=" + lastName +
                "&password=" + password +
                "&preferred_food=" + pFood +
                "&age=" + age;
        this.requestCode = requestCode;
    }

    public ApiClass(String url) {
        this.url = url;
    }

    @Override
    protected String doInBackground(Context... context) {
        try {
            URL ipUrl = new URL(url);
            HttpURLConnection httpURLConnection = (HttpURLConnection) ipUrl.openConnection();
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.setDoInput(true);
            httpURLConnection.connect();

            InputStream is = httpURLConnection.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            String line = "";
            while ((line = reader.readLine()) != null) {
                result += line;
            }
            httpURLConnection.disconnect();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    protected void onPostExecute(String result) {
        switch(requestCode){
            case Constant.REGISTRATION:
                GlobalVO.setRegistrationResponse(result);
                break;
        }
    }
}
