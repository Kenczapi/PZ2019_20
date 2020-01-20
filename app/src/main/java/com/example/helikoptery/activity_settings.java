package com.example.helikoptery;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.Switch;


public class activity_settings extends AppCompatActivity {


    Switch sSound;
    Switch sMusic;
    Switch sVibration;
    MediaPlayer music;
    Context context = this;

    public static final String SHARED_PREFS = "sharedPrefs";
    private boolean switchOnOf;
    private boolean switchOnOff;
    private boolean switchOnOfff;

    public static final String SSOUND = "sSound";
    public static final String SMUSIC = "sMusic";
    public static final String SVIBRATION= "sVibration";

    HomeWatcher mHomeWatcher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_settings);
        sSound = findViewById(R.id.soundSwitch);
        sMusic = findViewById(R.id.musicSwitch);
        sVibration = findViewById(R.id.vibrationSwitch);








        music = MediaPlayer.create(this, R.raw.music);

        if(sMusic.isChecked()){

            doBindService();
            Intent music = new Intent();
            music.setClass(this, MusicService.class);
            startService(music);
            saveData();
        }



        sMusic.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){

                    music = MediaPlayer.create(context, R.raw.music);
                    music.start();
                    saveData();
                }
                else {
                    music.stop();
                    music.release();

                    saveData();
                }
            }

        });
        //sMusic.setOnCheckedChangeListener();
        sSound.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    saveData();
                }
                else {
                    saveData();
                }
            }

        });
        sVibration.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    saveData();
                }
                else {
                    saveData();
                }
            }

        });


        mHomeWatcher = new HomeWatcher(this);
        mHomeWatcher.setOnHomePressedListener(new HomeWatcher.OnHomePressedListener() {
            @Override
            public void onHomePressed() {
                if (mServ != null) {
                    mServ.pauseMusic();
                }
            }
            @Override
            public void onHomeLongPressed() {
                if (mServ != null) {
                    mServ.pauseMusic();
                }
            }
        });
        mHomeWatcher.startWatch();


        loadData();
        updateViews();


    }
    private boolean mIsBound = false;
    private MusicService mServ;
    private ServiceConnection Scon =new ServiceConnection(){

        public void onServiceConnected(ComponentName name, IBinder
                binder) {
            mServ = ((MusicService.ServiceBinder)binder).getService();
        }

        public void onServiceDisconnected(ComponentName name) {
            mServ = null;
        }
    };

    void doBindService(){
        bindService(new Intent(this,MusicService.class),
                Scon,Context.BIND_AUTO_CREATE);
        mIsBound = true;
    }

    void doUnbindService()
    {
        if(mIsBound)
        {
            unbindService(Scon);
            mIsBound = false;
        }
    }
    @Override
    protected void onResume() {
        super.onResume();

        if (mServ != null) {
            mServ.resumeMusic();
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();


        doUnbindService();
        Intent music = new Intent();
        music.setClass(this,MusicService.class);
        stopService(music);

    }


    public void saveData() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putBoolean(SSOUND, sSound.isChecked());
        editor.putBoolean(SMUSIC, sMusic.isChecked());
        editor.putBoolean(SVIBRATION, sVibration.isChecked());

        editor.apply();


    }

    public void updateViews() {

        sSound.setChecked(switchOnOf);
        sMusic.setChecked(switchOnOff);
        sVibration.setChecked(switchOnOfff);
    }
    public void loadData() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        switchOnOf = sharedPreferences.getBoolean(SSOUND, false);
        switchOnOff = sharedPreferences.getBoolean(SMUSIC, false);
        switchOnOfff = sharedPreferences.getBoolean(SVIBRATION, false);


    }

    public void menuOnClick(View view) {
        Intent intent = new Intent(this, activity_menu.class);
        startActivity(intent);
    }
    @Override
    protected void onPause() {
        super.onPause();


        PowerManager pm = (PowerManager)
                getSystemService(Context.POWER_SERVICE);
        boolean isScreenOn = false;
        if (pm != null) {
            isScreenOn = pm.isScreenOn();
        }

        if (!isScreenOn) {
            if (mServ != null) {
                mServ.pauseMusic();
            }
        }
    }
}
