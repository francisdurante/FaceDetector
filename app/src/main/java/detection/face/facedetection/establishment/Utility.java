package detection.face.facedetection.establishment;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.widget.Toast;

import detection.face.facedetection.R;

public class Utility {
    static ProgressDialog mDialog;
    static SharedPreferences spf;
    static AlertDialog.Builder dialog;
    public static void showToastMessageLong(String message, Context context){
        Toast.makeText(context,message,Toast.LENGTH_LONG).show();
    }
    public static void showToastMessageShort(String message, Context context){
        Toast.makeText(context,message,Toast.LENGTH_SHORT).show();
    }
    public static void showProgressBar(String Message,Context context){
        mDialog = new ProgressDialog(context);
        mDialog.setMessage(Message);
        mDialog.setCanceledOnTouchOutside(false);
        mDialog.show();
    }
    public static void hideProgressBar(){
        mDialog.dismiss();
    }
    public static void save(String key, String value,Context context) {
        spf = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor edit = spf.edit();
        edit.putString(key, value);
        edit.apply();
    }
    public static String getString(String key, Context context) {

        spf = PreferenceManager.getDefaultSharedPreferences(context);
        return spf.getString(key,"");
    }
    public static String picUrl(String path){
        String response = "";
        if(!"".equals(path)) {
            String[] pic = path.split("/");
            response = Constant.PUBLIC_IMAGE_PATH + pic[7];
        }else{
            response = "ERROR_IMAGE";
        }
        return response;
    }
    public static void showAlertDialogBox(String message,String title, Context context,String type){
        dialog = new AlertDialog.Builder(context);
        dialog.setTitle(title);
        dialog.setMessage(message);
        dialog.setCancelable(true);
        if("error".equalsIgnoreCase(type)) {
            dialog.setIcon(R.drawable.ic_error);
        }else if("success".equalsIgnoreCase(type)){
            dialog.setIcon(R.drawable.ic_success);
        }else if("warning".equalsIgnoreCase(type)){
            dialog.setIcon(R.drawable.ic_warning);
        }
        dialog.show();
    }

}
