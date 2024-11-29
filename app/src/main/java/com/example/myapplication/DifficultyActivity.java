package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class DifficultyActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_difficulty);

        Button easyButton = findViewById(R.id.easyButton);
        Button mediumButton = findViewById(R.id.mediumButton);
        Button hardButton = findViewById(R.id.hardButton);

        // Set up button click listeners
        easyButton.setOnClickListener(v -> startGame(1000)); // Easy: 1-second delay
        mediumButton.setOnClickListener(v -> startGame(700)); // Medium: 0.7-second delay
        hardButton.setOnClickListener(v -> startGame(500)); // Hard: 0.5-second delay
    }

    private void startGame(int delay) {
        Intent intent = new Intent(DifficultyActivity.this, MainActivity.class);
        intent.putExtra("DELAY", delay); // Pass delay to game activity
        startActivity(intent);
        finish();
    }
}
