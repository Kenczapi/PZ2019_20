package com.example.helikoptery;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

public class activity_menu extends AppCompatActivity {

    Context context;
    final int DELAYED_TIME = 140;
    HomeWatcher mHomeWatcher;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_menu);
        context = this;

        doBindService();
        Intent music = new Intent();
        music.setClass(this, MusicService.class);
        startService(music);

        //Start HomeWatcher
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
    }

    public void settingsOnClick(View view) {
        final Button button = findViewById(R.id.settingsButton);
        button.setBackgroundResource(R.drawable.button2click);
        button.setTextColor(Color.WHITE);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(DELAYED_TIME);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        button.setBackgroundResource(R.drawable.button2);
                        button.setTextColor(Color.BLACK);
                    }
                });
                try {
                    Thread.sleep(DELAYED_TIME);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(context, activity_settings.class);
                        startActivity(intent);
                    }
                });

            }
        }).start();
    }

    public void profileOnClick(View view) {
        final Button button = findViewById(R.id.profileButton);
        button.setBackgroundResource(R.drawable.button2click);
        button.setTextColor(Color.WHITE);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(DELAYED_TIME);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        button.setBackgroundResource(R.drawable.button2);
                        button.setTextColor(Color.BLACK);
                    }
                });
                try {
                    Thread.sleep(DELAYED_TIME);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(context, activity_profile.class);
                        startActivity(intent);
                    }
                });
            }
        }).start();
    }

    public void goToSelectMode(View view) {
        final Button button = findViewById(R.id.playButton);
        button.setBackgroundResource(R.drawable.buttonclick);
        button.setTextColor(Color.WHITE);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(DELAYED_TIME);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        button.setBackgroundResource(R.drawable.button);
                        button.setTextColor(Color.BLACK);
                    }
                });
                try {
                    Thread.sleep(DELAYED_TIME);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(context, activity_selectGameMode.class);
                        startActivity(intent);
                    }
                });
            }
        }).start();

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
    protected void onPause() {
        super.onPause();

        //Detect idle screen
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


    @Override
    protected void onDestroy() {
        super.onDestroy();

        //UNBIND music service
        doUnbindService();
        Intent music = new Intent();
        music.setClass(this,MusicService.class);
        stopService(music);

    }
}
