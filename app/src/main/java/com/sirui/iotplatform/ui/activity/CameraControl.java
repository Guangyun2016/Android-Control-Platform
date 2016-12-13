package com.sirui.iotplatform.ui.activity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;

import com.sirui.iotplatform.R;
import com.sirui.iotplatform.widget.CameraPreview;
import com.wx.android.common.util.SystemUtils;
import com.wx.android.common.util.ToastUtils;

public class CameraControl extends AppCompatActivity {

    private final static String TAG = "CameraControl";

    private final static int REQUEST_CAMERA_PERMISSION = 101;

    /**
     * Id of the camera to access. 0 is the first camera.
     */
    private static final int CAMERA_ID = 0;
    private Camera mCamera = null;
    private CameraPreview mPreview;

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission Granted
                Log.i(TAG, "Manifest.permission.CAMERA Permission Granted");
//                initFolders();
            } else {
                // Permission Denied
                Log.i(TAG, "Manifest.permission.CAMERA Permission Denied");
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_camera_control);

        /**
         * 6.0的sdk要请求权限
         */
        Log.i(TAG, "SDK Version--->" + String.valueOf(SystemUtils.getVersionSDK()));
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
        } else {
            initCamera();
        }

    }

    /** A safe way to get an instance of the Camera object. */
    public static android.hardware.Camera getCameraInstance(int cameraId) {
        android.hardware.Camera c = null;
        try {
            c = android.hardware.Camera.open(cameraId); // attempt to get a Camera instance
        } catch (Exception e) {
            // Camera is not available (in use or does not exist)
            Log.d(TAG, "Camera " + cameraId + " is not available: " + e.getMessage());
        }
        return c; // returns null if camera is unavailable
    }

    private void initCamera() {

        mCamera = getCameraInstance(CAMERA_ID);
        Camera.CameraInfo cameraInfo = null;

        if (mCamera != null) {
            // Get camera info only if the camera is available
            cameraInfo = new Camera.CameraInfo();
            Camera.getCameraInfo(CAMERA_ID, cameraInfo);
        }

        if (mCamera == null || cameraInfo == null) {
            // Camera is not available, display error message
            ToastUtils.showToast("Camera is not available.");
//            return null;
        }


        // Get the rotation of the screen to adjust the preview image accordingly.
        final int displayRotation = getWindowManager().getDefaultDisplay()
                .getRotation();

        // Create the Preview view and set it as the content of this Activity.
        mPreview = new CameraPreview(getApplicationContext(), mCamera, cameraInfo, displayRotation);
        FrameLayout preview = (FrameLayout) findViewById(R.id.camera_view);
        preview.addView(mPreview);

    }

    @Override
    public void onPause() {
        super.onPause();
        // Stop camera access
        releaseCamera();
    }

    private void releaseCamera() {
        if (mCamera != null) {
            mCamera.release();        // release the camera for other applications
            mCamera = null;
        }
    }


}
