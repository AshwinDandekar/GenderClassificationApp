package com.gamer.rage.genderclassificationapp;


import android.content.Intent;
import android.hardware.Camera;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;


import static android.os.Environment.getExternalStorageDirectory;



public class CameraActivity extends AppCompatActivity implements SurfaceHolder.Callback {

    private SurfaceHolder mSurfaceHolder;
    private SurfaceHolder.Callback mSurfaceHolderCallback;
    private SurfaceView mSurfaceView;
    private Camera mCamera;
    private ImageButton button;
    private Camera.PictureCallback jpegImg;
    static final File mFileDir = getExternalStorageDirectory();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        button = (ImageButton) findViewById(R.id.imageButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takePicture();
            }
        });

        mSurfaceView = (SurfaceView)findViewById(R.id.surfaceView);
        mSurfaceHolder = mSurfaceView.getHolder();
        mSurfaceHolder.addCallback(this);
        mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        jpegImg = new Camera.PictureCallback() {
            @Override
            public void onPictureTaken(byte[] data, Camera camera) {
                FileOutputStream file_op = null;
                try {
                    File img = new File(mFileDir+"/DCIM/","selfie.jpg");
                    file_op = new FileOutputStream(img);
                    file_op.write(data);
                    Toast.makeText(getApplicationContext(), "Selfie Saved!", Toast.LENGTH_LONG).show();
                    file_op.close();
                }
                catch (IOException ioe) {
                    System.out.print(ioe.toString());
                }
                startpreview();
                callIntent();

            }
        };
    }

    public void callIntent() {
        Intent intent =  new Intent(this,GenderClassificationActivity.class);
        startActivity(intent);
    }
    public void takePicture() {
        mCamera.takePicture(null,null,null,jpegImg);
    }

    public void startpreview() {
        try {

            Camera.Parameters param;
            param = mCamera.getParameters();
            param.setRotation(270);
            Camera.Size s = param.getPictureSize();
            Toast.makeText(getApplicationContext(),"Current resolution "+s.height+"*"+s.width,Toast.LENGTH_LONG).show();
            mCamera.setParameters(param);
            mCamera.setPreviewDisplay(mSurfaceHolder);
            mCamera.setDisplayOrientation(90);
            mCamera.startPreview();
        }
        catch(Exception e) {

            System.out.print(e.toString());
        }
    }
    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Camera.CameraInfo mCameraInfo = new Camera.CameraInfo();
        int cameraCount = Camera.getNumberOfCameras();
        for(int i = 0 ; i < cameraCount ; i++) {
            Camera.getCameraInfo(i,mCameraInfo);
            if(mCameraInfo.facing==Camera.CameraInfo.CAMERA_FACING_FRONT){
                try{
                    mCamera = Camera.open(i);
                    startpreview();
                }
                catch(Exception e){
                    e.printStackTrace();

                }
            }
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        mCamera.stopPreview();
        mCamera.release();
        mCamera = null;
    }
}
