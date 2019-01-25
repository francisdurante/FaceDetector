package detection.face.facedetection;

import android.app.Service;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import android.content.Context;
import android.content.Intent;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;

import android.os.IBinder;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Toast;

public class CameraService extends Service {
    static int x=0;
    //Camera variables
    //a surface holder
    private SurfaceHolder sHolder;
    //a variable to control the camera
    private Camera mCamera;
    //the camera parameters
    private Camera.Parameters parameters;
    /** Called when the activity is first created. */

    @Override
    public void onCreate()
    {
        super.onCreate();

    }
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        SurfaceView sv = new SurfaceView(getApplicationContext());
        try {
            mCamera.setPreviewDisplay(sv.getHolder());
            parameters = mCamera.getParameters();

            //set camera parameters
            mCamera.setParameters(parameters);
            mCamera.startPreview();
            mCamera.takePicture(null, null, mCall);

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        //Get a surface
        sHolder = sv.getHolder();
        //tells Android that this surface will have its data constantly replaced
        sHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        return super.onStartCommand(intent, flags, startId);
    }


    Camera.PictureCallback mCall = new Camera.PictureCallback()
    {

        public void onPictureTaken(byte[] data, Camera camera)
        {
            //decode the data obtained by the camera into a Bitmap

            FileOutputStream outStream = null;
            try{
                x++;
                outStream = new FileOutputStream("/sdcard/Image"+x+".jpg");
                outStream.write(data);
                outStream.close();
                mCamera.release();
                Toast.makeText(getApplicationContext(), "picture clicked", Toast.LENGTH_LONG).show();
            } catch (FileNotFoundException e){
                Log.d("CAMERA", e.getMessage());
            } catch (IOException e){
                Log.d("CAMERA", e.getMessage());
            }

        }
    };

    private boolean hasFrontCamera() {
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        int numberOfCameras = Camera.getNumberOfCameras();
        for (int i = 0; i < numberOfCameras; i++) {
            Camera.getCameraInfo(i, cameraInfo);
            if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                return true;
            }
        }
        return false;
    }
}
