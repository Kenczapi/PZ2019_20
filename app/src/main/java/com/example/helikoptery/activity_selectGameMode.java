package com.example.helikoptery;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

public class activity_selectGameMode extends AppCompatActivity {

    AnimationDrawable animBluetooth;
    AnimationDrawable animWiFi;
    ImageButton imageButtonBluetooth;
    ImageButton imageButtonWifi;
    final int DELAYED_TIME = 140;
    Context context = this;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_select_game_mode);

        imageButtonBluetooth = findViewById(R.id.bBluetooth);
        imageButtonBluetooth.setBackgroundResource(R.drawable.animationbluetooth);
        animBluetooth = (AnimationDrawable) imageButtonBluetooth.getBackground();
        animBluetooth.start();

        imageButtonWifi = findViewById(R.id.bWiFi);
        imageButtonWifi.setBackgroundResource(R.drawable.animationwifi);
        animWiFi = (AnimationDrawable) imageButtonWifi.getBackground();
        animWiFi.start();
    }


    public void goToGameFieldPvp(View view) {
        final ImageButton imageButton = findViewById(R.id.bPvp);
        imageButton.setBackgroundResource(R.drawable.buttonpvpclick);
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
                        imageButton.setBackgroundResource(R.drawable.buttonpvp);
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
                        Intent intent = new Intent(context, activity_gameField.class);
                        startActivity(intent);
                    }
                });
            }
        }).start();
    }

    public void goToGameFieldPvc(View view) {
        final ImageButton imageButton = findViewById(R.id.bComputer);
        imageButton.setBackgroundResource(R.drawable.buttoncomputerclick);
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
                        imageButton.setBackgroundResource(R.drawable.buttoncomputer);
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
                        Intent intent = new Intent(context, activity_gameFieldBot.class);
                        startActivity(intent);
                    }
                });
            }
        }).start();


    }

    public void goToGameFieldWiFi(View view) {

        Intent intent = new Intent(context, activity_minesweeper.class);
        startActivity(intent);

        imageButtonWifi = findViewById(R.id.bWiFi);
        imageButtonWifi.setBackgroundResource(R.drawable.buttonwificlick);

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(DELAYED_TIME);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                try {
                    Thread.sleep(DELAYED_TIME);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(context, activity_minesweeper.class);
                        startActivity(intent);
                    }
                });
            }
        }).start();

    }

    public void goToGameFieldBluetooth(View view) {

        imageButtonBluetooth = findViewById(R.id.bBluetooth);
        imageButtonBluetooth.setBackgroundResource(R.drawable.buttonbluetoothclick);
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
                        imageButtonBluetooth.setBackgroundResource(R.drawable.animationbluetooth);
                        animBluetooth = (AnimationDrawable) imageButtonBluetooth.getBackground();
                        animBluetooth.start();
                    }
                });
            }
        }).start();

        // Sprawdzenie czy urządenie obsługuje Bluetooth
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if(bluetoothAdapter == null){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(R.string.accesBluetooth).show();
            return;
        }

        int REQUEST_ENABLE_BT = 1;
        //Sprawdza czy Bluetooth jest włączony, jeśli nie to wysyła zapytanie
        if (!bluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
        else {
            Intent intent = new Intent(this, activity_bluetooth.class);
            startActivity(intent);
        }


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            Intent intent = new Intent(this, activity_bluetooth.class);
            startActivity(intent);
            Toast.makeText(getApplicationContext(), "Bluetooth enabled", Toast.LENGTH_SHORT).show();
        }
    }
}
