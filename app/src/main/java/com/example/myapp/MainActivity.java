package com.example.myapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import java.io.IOException;
import java.util.ArrayList;

import static android.Manifest.permission.CAMERA;

public class MainActivity extends AppCompatActivity {

    private String[] neededPermissions = new String[]{CAMERA};
    private SurfaceHolder surfaceHolder;
    private CameraSource cameraSource;
    private SurfaceView surfaceView;
    private BarcodeDetector detector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        detector = new BarcodeDetector.Builder(getApplicationContext())
                .setBarcodeFormats(Barcode.QR_CODE).build();
        surfaceView = findViewById(R.id.surfaceView);
        if (!detector.isOperational()) {
            Log.d("tag111", "onCreate: Detector Initialisation failed ");
        }

        boolean result = checkPermission();
        if (result){

            setViewVisibility(R.id.surfaceView);

            setupSurfaceHolder();
        }

    }


    private boolean checkPermission(){
        ArrayList<String> permissionsNotGranted = new ArrayList<>();
        for (String permission:neededPermissions){
            if(ContextCompat.checkSelfPermission(this,permission)!= PackageManager.PERMISSION_GRANTED){
                permissionsNotGranted.add(permission);
            }
        }

        if(!permissionsNotGranted.isEmpty()){
            boolean shouldShowAlert = false;
            for(String permission:permissionsNotGranted){
                shouldShowAlert = ActivityCompat.shouldShowRequestPermissionRationale(this,permission);
            }

            if(shouldShowAlert){
                showPermissionAlert(permissionsNotGranted.toArray(new String[0]));
            }
            else{
                requestPermissions(permissionsNotGranted.toArray(new String[0]));
            }

            return false;
        }

        return true;
    }

    private void showPermissionAlert(final String[] permissions){
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
        alertBuilder.setCancelable(true);
        alertBuilder.setTitle("Permission Required");
        alertBuilder.setMessage("Camera permission required to move forward.");
        alertBuilder.setPositiveButton(android.R.string.yes,new DialogInterface.OnClickListener(){
            public void onClick(DialogInterface dialog, int which){
                requestPermissions(permissions);
            }
        });

        AlertDialog alert = alertBuilder.create();
        alert.show();
    }

    private void requestPermissions(String[] permissions){
        ActivityCompat.requestPermissions(MainActivity.this,permissions,1001);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults){
        if(requestCode==1001){
            for(int result:grantResults){
                if(result==PackageManager.PERMISSION_DENIED){
                    Toast.makeText(MainActivity.this,"This Permission is required",Toast.LENGTH_LONG).show();
                    checkPermission();
                    return;
                }
            }

            /*Code after permission granted*/
        }

        super.onRequestPermissionsResult(requestCode,permissions,grantResults);
    }

    private void setViewVisibility(int id) {

        View view = findViewById(id);

        if (view != null) {

            view.setVisibility(View.VISIBLE);
        }
    }

        private void setupSurfaceHolder () {
            cameraSource = new CameraSource.Builder(this, detector)
                    .setFacing(CameraSource.CAMERA_FACING_BACK)
                    .setRequestedFps(1.0f)
                    .build();

            surfaceHolder = surfaceView.getHolder();
            surfaceHolder.addCallback(new SurfaceHolder.Callback() {
                @Override
                public void surfaceCreated(SurfaceHolder holder) {
                    try {
                        cameraSource.start(surfaceHolder);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

                }

                @Override
                public void surfaceDestroyed(SurfaceHolder holder) {
                    cameraSource.stop();
                }
            });
        }



    //All the above stuff was to check if appropriate permissions have been granted

    }
