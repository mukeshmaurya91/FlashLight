package com.maurya91.flashlight;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.hardware.camera2.CameraManager;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;

public class MovementTrackerService extends Service {
    private boolean isFlashOn=false;
    private static Camera mCamera=null;

    public MovementTrackerService() {
    }

     CameraManager mCameraManager;
    String mCameraId;
    private  SensorManager mSensorManager;
    private  Sensor mAccelerometer;
    MySensorListener mListener=new MySensorListener();


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(">>>> ","onStartCommand.");
        return super.onStartCommand(intent,flags,startId);
    }

    @Override
    public void onCreate() {
        Log.d(">>>>>>>>","onCreate.");
        super.onCreate();
        if (!isFlashPresent()) {
            stopSelf();
            toast("Flash not present!");
        }

        mSensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mSensorManager.registerListener(mListener, mAccelerometer, SensorManager.SENSOR_DELAY_GAME);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mCameraManager= (CameraManager) getSystemService(Context.CAMERA_SERVICE);
            try {
                mCameraId=mCameraManager.getCameraIdList()[0];
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void toast(String s) {
        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
    }

    private boolean isFlashPresent(){
         return getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);
     }
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.d(">>>>>>>>>>","onBind.");
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mSensorManager==null)
            return;
        Log.d(">>>>>>>>>>","onDestroy.");
        mSensorManager.unregisterListener(mListener);

    }

    public void startFlashLite(){

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                mCameraManager.setTorchMode(mCameraId,true);
            }else{
                mCamera = Camera.open();
                Camera.Parameters parameters= mCamera.getParameters();
                parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                mCamera.setParameters(parameters);
                mCamera.lock();
                mCamera.startPreview();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    public void stopFlashLite(){
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                mCameraManager.setTorchMode(mCameraId,false);
            }else{
                   mCamera.stopPreview();
                   mCamera.unlock();
                   mCamera.release();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    class MySensorListener implements SensorEventListener{
        private static  final int SHAKE_THRESHOLD=500;
        private static final float SHAKE_GRAVITY=1.2f;
        private static  final int SHAKE_COUNT_RESET_TIME=1500;
        private long mTimeStamp;
        private int mShakeCount;

        @Override
        public void onSensorChanged(SensorEvent event) {


            float x=event.values[0];
            float y=event.values[1];
            float z=event.values[2];
            float gX=x/SensorManager.GRAVITY_EARTH;
            float gY=y/SensorManager.GRAVITY_EARTH;
            float gZ=z/SensorManager.GRAVITY_EARTH;
            long gForce= (long) Math.sqrt(gX * gX + gY * gY + gZ * gZ);
            if (gForce>SHAKE_GRAVITY){
                long curTime=System.currentTimeMillis();
                if (mTimeStamp+SHAKE_THRESHOLD>curTime) {
                    return;
                }
                if (mTimeStamp+SHAKE_COUNT_RESET_TIME<curTime) {
                    mShakeCount = 0;
                }
                mTimeStamp=curTime;
                mShakeCount++;
//                Log.d("Shake:::: ","Count::"+mShakeCount);
                if (mShakeCount==2){
                    mShakeCount=0;
                    shakeDetected();
                }


            }



//                 Log.d("X<><>",""+event.values[0]);
//            Log.d("Y<><>",""+event.values[1]);
//            Log.d("Z<><>",""+event.values[2]);
//            if((curTime-lastUpdate)>100){
//                long diffTime=curTime-lastUpdate;
//                lastUpdate=curTime;
//            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    }

    private void shakeDetected() {

        if (isFlashOn) {
            toast("SHAKE DETECTED! Turning OFF Flash ");
            stopFlashLite();
            isFlashOn=false;

        }
        else {
            toast("SHAKE DETECTED! Turning ON Flash ");
            startFlashLite();
            isFlashOn=true;
        }
    }
}
