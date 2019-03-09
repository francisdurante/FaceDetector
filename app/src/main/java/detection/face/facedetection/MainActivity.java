package detection.face.facedetection;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.microsoft.projectoxford.face.*;
import com.microsoft.projectoxford.face.contract.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

public class MainActivity extends Activity {
    private ImageView imageView;
    private final FaceServiceClient faceServiceClient =
            new FaceServiceRestClient(Constant.API_ENDPOINT, Constant.SUBSCRRIPTION_KEY);
    ProgressDialog detectionProgressDialog;
    TextView loggedIn;
    static TextView age;
    static TextView gender;
    static TextView smile;
    TextView resultSurvey;
    Context mContext = this;
    SharedPreferences spf;
    static String emotion_result = "";
    static String age_result = "";
    static String resultInQuestion;
    static int questionTrigger = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        age = findViewById(R.id.age);
        gender = findViewById(R.id.gender);
        smile = findViewById(R.id.smile);
        this.imageView = (ImageView) this.findViewById(R.id.image_result);
        detectionProgressDialog = new ProgressDialog(this);
        if (checkSelfPermission(Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.CAMERA},
                    Constant.MY_CAMERA_PERMISSION_CODE);
        } else {
            Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(cameraIntent, Constant.CAMERA_REQUEST);
        }
        if(null != getIntent().getExtras()) {
            questionTrigger = getIntent().getExtras().getInt("QUESTION_TRIGGER");
            resultInQuestion = getIntent().getExtras().getString("RESULT_QUESTION");
        }
        if(1 == questionTrigger) {
            save("ANSWERED_SURVEY_" + getString("account_id"),"1");
            resultSurvey = findViewById(R.id.result_survey);

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == Constant.MY_CAMERA_PERMISSION_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "camera permission granted", Toast.LENGTH_LONG).show();
                Intent cameraIntent = new
                        Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, Constant.CAMERA_REQUEST);
            } else {
                Toast.makeText(this, "camera permission denied", Toast.LENGTH_LONG).show();
            }

        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Constant.CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            imageView.setImageBitmap(photo);
            detectAndFrame(photo);
        }
    }



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
                            publishProgress(String.format(
                                    "Detection Finished. %d face(s) detected",
                                    result.length));
                            return result;
                        } catch (Exception e) {
                            exceptionMessage = String.format(
                                    "Detection failed: %s", e.getMessage());
                            return null;
                        }
                    }

                    @Override
                    protected void onPreExecute() {
                        detectionProgressDialog.show();
                    }
                    @Override
                    protected void onProgressUpdate(String... progress) {
                        detectionProgressDialog.setMessage(progress[0]);
                    }
                    @Override
                    protected void onPostExecute(Face[] result) {
                        detectionProgressDialog.dismiss();
                        if(!exceptionMessage.equals("")){
                            showError(exceptionMessage);
                        }
                        if (result == null) return;
                        ImageView imageView = findViewById(R.id.image_result);
                        imageView.setImageBitmap(
                                drawFaceRectanglesOnBitmap(imageBitmap, result,mContext));
                        imageBitmap.recycle();
                    }
                };

        detectTask.execute(inputStream);
    }

    private void showError(String message) {
        new AlertDialog.Builder(this)
                .setTitle("Error")
                .setMessage(message)
                .setPositiveButton("OK", (dialog, id) -> {
                })
                .create().show();
    }

    private Bitmap drawFaceRectanglesOnBitmap(
            Bitmap originalBitmap, Face[] faces,Context context) {
        Bitmap bitmap = originalBitmap.copy(Bitmap.Config.ARGB_8888, true);
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.RED);
        paint.setStrokeWidth(5);
        if (faces != null) {
            for (Face face : faces) {
                FaceRectangle faceRectangle = face.faceRectangle;
                canvas.drawRect(
                        faceRectangle.left,
                        faceRectangle.top,
                        faceRectangle.left + faceRectangle.width,
                        faceRectangle.top + faceRectangle.height,
                        paint);

                gender.setText("GENDER: " + face.faceAttributes.gender.toUpperCase());
                age.setText("AGE: " + getAgeRange(face.faceAttributes.age));
                Double[] emotion = {face.faceAttributes.emotion.anger,
                        face.faceAttributes.emotion.happiness,
                        face.faceAttributes.emotion.sadness,};
                if(questionTrigger != 1) {
                    smile.setText("EMOTION : " + getEmotion(emotion));
                    emotion_result = getEmotion(emotion);
                    age_result = getAgeRange(face.faceAttributes.age);
                }else {
                    smile.setText("");
                    age.setText("");
                    gender.setText("");
                    resultSurvey.setText("Based in our question and capture image you are " + face.faceAttributes.gender.toUpperCase() + " with age of " + getAgeRange(face.faceAttributes.age) + " and you are " + getEmotion(emotion));
                }
            }
        }
        return bitmap;
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
    public void logout(View v){
        save("first_name","");
        save("last_name","");
        save("account_id","");
        Intent login = new Intent(getApplicationContext(),LoginActivity.class);
        startActivity(login);
        finish();
    }

    public static String getEmotion(Double[] emotions){
        String emotion;
        int largest = 0;
        for(int x = 1; x < emotions.length; x++){
            if(emotions[x] > emotions[largest]) largest = x;
        }
        if(1 != questionTrigger) {
            emotion = "SAD";
            if(largest == 0){
                emotion = "IRRITATE";
            }else if(largest == 1){
                emotion = "HAPPY";
            }
        }else{
            emotion = "SAD";
            if(largest == 0){
                emotion = "IRRITATE";
            }else if(largest == 1){
                emotion = "HAPPY";
            }
        }
        return emotion;
    }

    public void reCapture(View v){
        if (checkSelfPermission(Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.CAMERA},
                    Constant.MY_CAMERA_PERMISSION_CODE);
        } else {
            Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(cameraIntent, Constant.CAMERA_REQUEST);
        }
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

    public void searchOnclick(View v){
        String emotion = emotion_result;
        String age = age_result;
        Intent mIntent = new Intent(mContext, FaceSuggestionActivity.class);
        Bundle mBundle = new Bundle();
        mBundle.putString("emotion", emotion);
        mBundle.putString("age", age);
        mIntent.putExtras(mBundle);
        startActivity(mIntent);

    }
}
