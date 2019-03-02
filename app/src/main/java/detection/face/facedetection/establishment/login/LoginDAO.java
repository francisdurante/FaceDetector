package detection.face.facedetection.establishment.login;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;
import detection.face.facedetection.establishment.Api.Api;

import static detection.face.facedetection.establishment.Constant.*;
import static detection.face.facedetection.establishment.Constant.LOGIN_API;

public class LoginDAO {

    public static void login(String username, String password, final LoginVO vo){
        Api api = new Api();
        RequestParams rp = new RequestParams();
        rp.add("username",username);
        rp.add("password",password);
        rp.add("for_log","desktop_app");
        rp.add("pass","for_login");
        api.getByUrl(LOGIN_API,rp,new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    JSONObject object = new JSONObject(response.toString());
                    String status = object.getString("status");
                    if("sucess".equals(status)) {
                        JSONObject data = new JSONObject(object.getString("data"));
                        int accountStatus = data.getInt("status");
                        if(accountStatus == 1){
                            vo.setLoginStatus(1); // success login
                            vo.setId(data.getString("id"));
                            vo.setEstUserId(data.getString("establishment_user_id"));
                            vo.setEstName(data.getString("establishment_name"));
                            vo.setLocationLat(data.getString("location_latitude"));
                            vo.setLocationLon(data.getString("location_longitude"));
                            vo.setEmotion(data.getString("good_for_emotion_of"));
                            vo.setAge(data.getString("good_at_of"));
                            vo.setEstTypeId(data.getInt("est_type_id"));
                            vo.setFrontStoreUrl(data.getString("est_front_store").equals("") ? "" : data.getString("est_front_store"));
                            vo.setEstAddress(data.getString("address"));
                            vo.setEstId(data.getString("est_id"));
                        }else{
                            vo.setLoginStatus(2); // deactivated
                        }
                    }else{
                        vo.setLoginStatus(3); // wrong password or account
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

}
