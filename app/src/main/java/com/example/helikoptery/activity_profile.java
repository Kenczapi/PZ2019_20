package com.example.helikoptery;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.Random;

public class activity_profile extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_profile);

        Random random = new Random();
        StringBuilder nick = new StringBuilder("guest");
        for(int i = 0; i < 5; i++){
            nick.append(random.nextInt(10));
        }
        TextView text = findViewById(R.id.textViewNick);
        text.setText(nick.toString());

    }


    public void changeNick(View view) {
        EditText edittext = findViewById(R.id.editTextNick);
        String nick = edittext.getText().toString();
        TextView text = findViewById(R.id.textViewNick);
        text.setText(nick);

    }
}
