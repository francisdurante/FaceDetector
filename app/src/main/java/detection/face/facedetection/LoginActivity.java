package detection.face.facedetection;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.os.AsyncTask;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.microsoft.projectoxford.face.FaceServiceClient;
import com.microsoft.projectoxford.face.FaceServiceRestClient;
import com.microsoft.projectoxford.face.contract.Face;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import cz.msebera.android.httpclient.Header;

public class LoginActivity extends AppCompatActivity {
    EditText un = null;
    EditText pass = null;
    SharedPreferences spf;
    Button loginButton = null;
    private SurfaceView sv;
    private final FaceServiceClient faceServiceClient =
            new FaceServiceRestClient(Constant.API_ENDPOINT, Constant.SUBSCRRIPTION_KEY);
    static String emotion_result = "";
    static String age_result = "";
    ProgressDialog detectionProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            int hasCameraPermission = checkSelfPermission(Manifest.permission.CAMERA);
            int hasStoragePermission = checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);

            List<String> permissions = new ArrayList<String>();

            if (hasCameraPermission != PackageManager.PERMISSION_GRANTED) {
                permissions.add(Manifest.permission.CAMERA);

            }if(hasStoragePermission != PackageManager.PERMISSION_GRANTED){
                permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            }
            if (!permissions.isEmpty()) {
                requestPermissions(permissions.toArray(new String[permissions.size()]), 111);
            }

            detectionProgressDialog = new ProgressDialog(this);
        }
        if(!"".equals(getString("account_id")) &&
                !"".equals(getString("first_name")) &&
                !"".equals(getString("last_name"))){
            String name = "<b>" + getString("first_name") +" " + getString("last_name") + "</b>";
            CharSequence fullName = Html.fromHtml(name);
            showLoginDialogBox("Currently logged in : " + fullName + "\nDo you want to continue?");
        }
        un = findViewById(R.id.username_login);
        pass = findViewById(R.id.password_login);
        loginButton = findViewById(R.id.login_button);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 111: {
                for (int i = 0; i < permissions.length; i++) {
                    if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                        System.out.println("Permissions --> " + "Permission Granted: " + permissions[i]);


                    } else if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                        System.out.println("Permissions --> " + "Permission Denied: " + permissions[i]);

                    }
                }
            }
            break;
            default: {
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            }
        }
    }

    public void login(View v) {
        loginButton.setEnabled(false);
        loginButton.setText(Constant.LOGGING_IN);
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
                            GlobalVO.setPreferredFood(datas.getString("preferred_food"));
                            save("first_name",GlobalVO.getFirstName());
                            save("last_name",GlobalVO.getLastname());
                            save("account_id",GlobalVO.getAccounId());
                            Toast.makeText(getApplicationContext(),"Please wait while getting some information",Toast.LENGTH_LONG).show();
                            takePhoto();
                        }else{
                            loginButton.setEnabled(true);
                            loginButton.setText(Constant.LOG_IN);
                            Toast.makeText(getApplicationContext(),Constant.INACTIVE_USER,Toast.LENGTH_SHORT).show();
                        }
                    }else{
                        loginButton.setEnabled(true);
                        loginButton.setText(Constant.LOG_IN);
                        Toast.makeText(getApplicationContext(),Constant.LOGIN_FAIL,Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    loginButton.setEnabled(true);
                    loginButton.setText(Constant.LOG_IN);
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
    public void showLoginDialogBox(String message){
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Login");
        dialog.setMessage(message);
        dialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                takePhoto();
            }
        });
        dialog.setNegativeButton("LOG OUT", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                save("first_name","");
                save("last_name","");
                save("account_id","");
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void takePhoto() {
        final String[] array = getResources().getStringArray(R.array.trivia_smiling);
        final String randomStr = array[new Random().nextInt(array.length)];
        detectionProgressDialog.setTitle("While Logging in...");
        detectionProgressDialog.setMessage(randomStr);
        detectionProgressDialog.show();
        Camera myCamera = Camera.open(Camera.CameraInfo.CAMERA_FACING_FRONT);
        Camera.Parameters parameters = myCamera.getParameters();
        myCamera.setDisplayOrientation(90);
        parameters.setPictureFormat(ImageFormat.JPEG);
        parameters.setPictureFormat(PixelFormat.JPEG);
        myCamera.setParameters(parameters);
        SurfaceView mview = new SurfaceView(this);
        try {
            myCamera.setPreviewDisplay(mview.getHolder());
            myCamera.startPreview();
            myCamera.takePicture(null, null, photoCallback);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    Camera.PictureCallback photoCallback=new Camera.PictureCallback() {
        public void onPictureTaken(byte[] data, Camera camera) {
            Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
            Matrix m = new Matrix();
            m.postRotate(270);
            bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), m, true);
//            detectAndFrame(bitmap);
            if (bitmap != null) {
                File file = new File(Environment.getExternalStorageDirectory() + "/Android/data/"+getPackageName()+"/secret_photo/");
                if (!file.exists()) {
                    file.mkdirs();
                }

                file = new File(Environment.getExternalStorageDirectory() + "/Android/data/"+getPackageName()+"/secret_photo/", System.currentTimeMillis() + ".jpg");


                try {
                    FileOutputStream fileOutputStream = new FileOutputStream(file);
                    detectAndFrame(bitmap);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 50, fileOutputStream);
                    fileOutputStream.flush();
                    fileOutputStream.close();

                } catch (IOException e) {
                    e.printStackTrace();
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
            }
        }
    };

    private void detectAndFrame(final Bitmap imageBitmap) {

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
        ByteArrayInputStream inputStream =
                new ByteArrayInputStream(outputStream.toByteArray());
        @SuppressLint("StaticFieldLeak") AsyncTask<InputStream, String, Face[]> detectTask =
                new AsyncTask<InputStream, String, Face[]>() {
                    String exceptionMessage = "";
                    @Override
                    protected Face[] doInBackground(InputStream... params) {
                        try {
                            publishProgress("Detecting...");
                            Face[] result = faceServiceClient.detect(
                                    params[0],
                                    true,         // returnFaceId
                                    false,        // returnFaceLandmarks
//                                     returnFaceAttributes:
                                    new FaceServiceClient.FaceAttributeType[] {
                                            FaceServiceClient.FaceAttributeType.Age,
                                            FaceServiceClient.FaceAttributeType.Gender,
                                            FaceServiceClient.FaceAttributeType.Smile,
                                            FaceServiceClient.FaceAttributeType.Emotion}

                            );
                            if (result == null){
                                publishProgress(
                                        "Detection Finished. Nothing detected");
                                return null;
                            }
                            return result;
                        } catch (Exception e) {
                            exceptionMessage = String.format(
                                    "Detection failed: %s", e.getMessage());
                            return null;
                        }
                    }

                    @Override
                    protected void onPreExecute() {
                    }
                    @Override
                    protected void onProgressUpdate(String... progress) {
                    }
                    @Override
                    protected void onPostExecute(Face[] result) {
                        if(!exceptionMessage.equals("")){
                            showError(exceptionMessage);
                        }
                        if (result == null) return;
                        drawFaceRectanglesOnBitmap(imageBitmap, result);
                        imageBitmap.recycle();
                    }
                };

        detectTask.execute(inputStream);
    }

    private void showError(String message) {
        new android.app.AlertDialog.Builder(this)
                .setTitle("Error")
                .setMessage(message)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }})
                .create().show();
    }

    private Bitmap drawFaceRectanglesOnBitmap(
            Bitmap originalBitmap, Face[] faces) {
        Bitmap bitmap = originalBitmap.copy(Bitmap.Config.ARGB_8888, true);
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.RED);
        paint.setStrokeWidth(5);
        if (faces != null) {
            for (Face face : faces) {
                Double[] emotion = {
                        face.faceAttributes.emotion.anger,
                        face.faceAttributes.emotion.happiness,
                        face.faceAttributes.emotion.sadness,};
//                smile.setText("EMOTION : " + getEmotion(emotion));
                emotion_result = getEmotion(emotion);
                age_result = getAgeRange(face.faceAttributes.age);
                save("INITIAL_EMOTION",emotion_result);
                save("INITIAL_AGE",age_result);
            }
        }
        new CountDownTimer(3000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                Intent redirect = new Intent(getApplicationContext(),EstablishmentListActivity.class);
                detectionProgressDialog.dismiss();
                startActivity(redirect);
                finish();
            }
        }.start();

        return bitmap;
    }
    public static String getEmotion(Double[] emotions){
        String emotion = "SAD";
        int largest = 0;
        for(int x = 1; x < emotions.length; x++){
            if(emotions[x] > emotions[largest]) largest = x;
        }
        if(largest == 0){
            emotion = "IRRITATE";
        }else if(largest == 1){
            emotion = "HAPPY";
        }
        return emotion;
    }
    public static String getAgeRange(double age) {
        double resultAge = age;
        String ageRange = "CHILDREN";
        if (resultAge < 25) {
            ageRange = "YOUTH";
        }
        if (resultAge < 59) {
            ageRange = "ADULT";
        } else if (resultAge > 59) {
            ageRange = "SENIOR";
        }
        return ageRange;
    }
}
