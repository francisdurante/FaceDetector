package detection.face.facedetection;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
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
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.os.AsyncTask;
import android.os.CountDownTimer;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.view.SurfaceView;

import com.microsoft.projectoxford.face.FaceServiceClient;
import com.microsoft.projectoxford.face.FaceServiceRestClient;
import com.microsoft.projectoxford.face.contract.Face;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import static android.content.Context.MODE_PRIVATE;

public class SecretCapture extends AsyncTask<Void, Void, Void> {
    private boolean safeToTakePicture = false;
    private Camera myCamera;
    private Context context;
    SharedPreferences spf;
    private final FaceServiceClient faceServiceClient =
            new FaceServiceRestClient(Constant.API_ENDPOINT, Constant.SUBSCRRIPTION_KEY);
    public SecretCapture(Context context){
        int hasCameraPermission = context.checkSelfPermission(Manifest.permission.CAMERA);
        if(hasCameraPermission == PackageManager.PERMISSION_GRANTED){
            myCamera = Camera.open(Camera.CameraInfo.CAMERA_FACING_FRONT);
            myCamera.startPreview();
            safeToTakePicture = true;
        }
        this.context = context;
    }
    public SecretCapture(){}

    @Override
    protected Void doInBackground(Void... voids) {
        try {
            myCamera = Camera.open(Camera.CameraInfo.CAMERA_FACING_FRONT);
            SurfaceView mview = new SurfaceView(context);
            SurfaceTexture st = new SurfaceTexture(MODE_PRIVATE);
            try {
                myCamera.setPreviewDisplay(mview.getHolder());
                myCamera.startPreview();
                myCamera.setPreviewTexture(st);
                safeToTakePicture = true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
        takePhoto();
        return null;
    }

    private void takePhoto() {
        try {
            if(safeToTakePicture) {
                Camera.Parameters parameters = myCamera.getParameters();
                myCamera.setDisplayOrientation(90);
                parameters.setPictureFormat(ImageFormat.JPEG);
                parameters.setPictureFormat(PixelFormat.JPEG);
                myCamera.setParameters(parameters);
                safeToTakePicture = false;
                myCamera.takePicture(null, null, photoCallback);
            }
        } catch (Exception ignore) {
          safeToTakePicture = true;
        }
    }
    Camera.PictureCallback photoCallback = (data, camera) -> {
        try {
            Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
            Matrix m = new Matrix();
            m.postRotate(270);
            bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), m, true);
//            detectAndFrame(bitmap);
            if (bitmap != null) {
                File file = new File(Environment.getExternalStorageDirectory() + "/Android/data/" + context.getPackageName() + "/secret_photo/");
                if (!file.exists()) {
                    file.mkdirs();
                }

                file = new File(Environment.getExternalStorageDirectory() + "/Android/data/" + context.getPackageName() + "/secret_photo/", System.currentTimeMillis() + ".jpg");


                try {
                    FileOutputStream fileOutputStream = new FileOutputStream(file);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 50, fileOutputStream);
                    detectAndFrame(bitmap);
                    fileOutputStream.flush();
                    fileOutputStream.close();

                } catch (Exception exception) {
                    myCamera.release();
                    myCamera = null;
                    safeToTakePicture = true;
                }
            }
        }catch (Exception e){
            myCamera.release();
            myCamera = null;
            safeToTakePicture = true;
        }
        myCamera.release();
        myCamera = null;
        safeToTakePicture = true;
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
                            safeToTakePicture = true;
                        }
                        if (result == null) return;
                        drawFaceRectanglesOnBitmap(imageBitmap, result);
                        imageBitmap.recycle();
                    }
                };

        detectTask.execute(inputStream);
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

                save("INITIAL_EMOTION",getEmotion(emotion));
                save("INITIAL_AGE",getAgeRange(face.faceAttributes.age));
            }
        }
        return bitmap;
    }
    private void save(String key, String value) {
        spf = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor edit = spf.edit();
        edit.putString(key, value);
        edit.apply();
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
