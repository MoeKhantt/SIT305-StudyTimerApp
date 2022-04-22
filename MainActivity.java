package com.example.studytimerapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.TextView;
import android.widget.ImageButton;
import android.widget.EditText;
import android.os.Handler;

import java.text.DecimalFormat;
import java.text.NumberFormat;

public class MainActivity extends AppCompatActivity {

    TextView last_time, timer, input_prompt;
    ImageButton play, pause, stop;
    EditText user_Input;
    long second;
    Handler handler;
    NumberFormat converter;
    SharedPreferences sharedPreferences;
    String lastTime = "lastTime";
    String runningTimer = "runningTimer";
    String TextKey = "TextKey";
    String firstRunKey = "firstRunKey";
    boolean timerRunning = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        last_time = findViewById(R.id.last_time);
        timer = findViewById(R.id.timer);
        input_prompt= findViewById(R.id.input_prompt);
        play = findViewById(R.id.play);
        pause = findViewById(R.id.pause);
        stop = findViewById(R.id.stop);
        user_Input = findViewById(R.id.user_Input);
        handler = new Handler();
        converter = new DecimalFormat("00");
        sharedPreferences = getSharedPreferences("com.example.studytimerapp", MODE_PRIVATE);

        boolean isRunning = false;

        previousRecord(sharedPreferences.getLong(lastTime, 0), sharedPreferences.getString(TextKey, ""));

        if(savedInstanceState != null && !savedInstanceState.getBoolean(firstRunKey, true))
        {
            second = savedInstanceState.getLong(lastTime, 0);
            isRunning = savedInstanceState.getBoolean(runningTimer, false);
        }

        timer.setText(timer_text(second));

        if(isRunning)
        {
            Play();
        }

        // btn listeners
        play.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                Play();
            }
        });

        pause.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                Pause();
            }
        });

        stop.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                Stop();
            }
        });
    }

    // Display text for previous task
    private void previousRecord(long lastSeconds, String lastTask)
    {
        last_time.setText("You spent " + timer_text(lastSeconds) + " on " +  lastTask + " last time");
    }

    // play timer
    private void Play()
    {
        // already running >> resume
        if(timerRunning)
        {
            return;
        }
        Timer.run();
        timerRunning = true;
    }

    // pause timer
    private void Pause()
    {
        PauseTime();
        timerRunning = false;
    }

    // stop timer
    private void Stop()
    {
        previousRecord(second, user_Input.getText().toString());
        PauseTime();
        SaveData();
        second = 0;
        timer.setText(timer_text(second));
        timerRunning = false;
    }

    private void SaveData()
    {
        SharedPreferences.Editor editor =  sharedPreferences.edit();
        editor.putLong(lastTime, second);
        editor.putString(TextKey, user_Input.getText().toString());
        editor.apply();
        timerRunning = false;
    }

    private void PauseTime()
    {
        handler.removeCallbacks(Timer);
    }

    private void IncreaseTime()
    {
        second++;
        timer.setText(timer_text(second));
    }

    private String timer_text(long time)
    {
        int hour = (int)time / 3600;
        int minute = ((int)time % 3600) / 60;
        int second = (int)time % 60;
        String timerText = converter.format(hour) + ":" + converter.format(minute) + ":" + converter.format(second);
        return timerText;
    }

    Runnable Timer = new Runnable() {
        @Override public void run() {
            try {
                IncreaseTime();
            }
            finally {
                handler.postDelayed(Timer, 1000);
            }
        }
    };

    @Override public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putLong(lastTime, second);
        savedInstanceState.putBoolean(runningTimer, timerRunning);
        savedInstanceState.putString(TextKey, user_Input.getText().toString());
        savedInstanceState.putBoolean(firstRunKey, false);
    }
}