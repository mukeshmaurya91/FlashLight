package com.maurya91.flashlight;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    SwitchCompat mSwitchCompat;
    private static final String PREF_NAME="FLASH_LITE_PREF";
    private static final String IS_SERVICE_RUNNING_PREF="FLASH_LITE_PREF";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
          mSwitchCompat= (SwitchCompat) findViewById(R.id.switch_compat);
//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        TextView msgText=(TextView)findViewById(R.id.msg);

//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });
//        fab.hide();
        if (msgText!=null)
        if (isFlashPresent())
        {
            msgText.setText("This functionality does not work in your device.Flash light not present.");
        }else {
            msgText.setText("Turn On Service to track Shake Movement of the device.\n \n \n Tip: Shake device two times to ON/OFF the flash light. ");
        }
        final Intent intent=new Intent(this,MovementTrackerService.class);
        mSwitchCompat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    if (isFlashPresent()) {
                        startService(intent);
                        setPref(true);
                        toast("Service started.");
                    }else {
                        mSwitchCompat.setChecked(false);
                        toast("Sorry! We can't start Service. ");
                    }
                }
                else {
                    if (getPref()) {
                        stopService(intent);
                        setPref(false);
                        toast("Service stopped.");
                    }
                }

            }
        });
        if (getPref()){
            mSwitchCompat.setChecked(true);
        }

    }
    private void setPref(boolean b){
        SharedPreferences preferences = getSharedPreferences(PREF_NAME,MODE_PRIVATE);
        SharedPreferences.Editor editor=preferences.edit();
        editor.putBoolean(IS_SERVICE_RUNNING_PREF,b);
        editor.commit();
    }
    private boolean getPref(){
        SharedPreferences preferences = getSharedPreferences(PREF_NAME,MODE_PRIVATE);
        return  preferences.getBoolean(IS_SERVICE_RUNNING_PREF,false);
    }


     private void toast(String s){
         Toast.makeText(this,s,Toast.LENGTH_LONG).show();
     }

    private boolean isFlashPresent(){
        return getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);
    }
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_main, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }
}
