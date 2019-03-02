package detection.face.facedetection.establishment.Api;

import android.content.Context;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class Api {
    private AsyncHttpClient client;
    private Context mContext;

    public Api(){
        client = new AsyncHttpClient();
    }

    private final String BASE_URL = "http://api.twitter.com/1/";

    public void get(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.get(getAbsoluteUrl(url), params, responseHandler);
    }

    public void post(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.post(getAbsoluteUrl(url), params, responseHandler);
    }

    public void getByUrl(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.get(url, params, responseHandler);
    }

    public void postByUrl(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.post(url, params, responseHandler);
    }
    public void postWIthImage(String url,RequestParams params, AsyncHttpResponseHandler responseHandler){
        client.put(url, params, responseHandler);
    }
    private String getAbsoluteUrl(String relativeUrl) {
        return BASE_URL + relativeUrl;
    }

}
