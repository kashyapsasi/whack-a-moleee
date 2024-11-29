package com.example.myapplication;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Random;

public class MainActivity extends AppCompatActivity {

    // Views for intro screen
    private LinearLayout introLayout;
    private Button easyButton, mediumButton, hardButton;

    // Views for game screen
    private LinearLayout gameLayout;
    private GridLayout gridLayout;
    private TextView scoreTextView;
    private TextView bestScoreTextView;

    // Views for game over screen
    private LinearLayout gameOverLayout;
    private Button restartButton;

    private int score = 0;
    private int bestScore = 0;
    private Random random = new Random();
    private Handler handler = new Handler();
    private Runnable moleRunnable;
    private Runnable timerRunnable;
    private int gameSpeed = 1000; // Default speed for "Medium"
    private boolean isGameOver = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize intro screen views
        introLayout = findViewById(R.id.introLayout);
        easyButton = findViewById(R.id.easyButton);
        mediumButton = findViewById(R.id.mediumButton);
        hardButton = findViewById(R.id.hardButton);

        // Initialize game screen views
        gameLayout = findViewById(R.id.gameLayout);
        gridLayout = findViewById(R.id.gridLayout);
        scoreTextView = findViewById(R.id.scoreTextView);
        bestScoreTextView = findViewById(R.id.bestScoreTextView);

        // Initialize game over screen views
        gameOverLayout = findViewById(R.id.gameOverLayout);
        restartButton = findViewById(R.id.restartButton);

        // Hide the game layout and game over screen initially
        gameLayout.setVisibility(View.GONE);
        gameOverLayout.setVisibility(View.GONE);

        // Set up button listeners for difficulty selection
        easyButton.setOnClickListener(v -> startGame("Easy"));
        mediumButton.setOnClickListener(v -> startGame("Medium"));
        hardButton.setOnClickListener(v -> startGame("Hard"));

        // Initialize grid buttons and set onClickListener for mole hitting
        for (int i = 0; i < gridLayout.getChildCount(); i++) {
            Button button = (Button) gridLayout.getChildAt(i);
            button.setVisibility(View.INVISIBLE);
            button.setOnClickListener(v -> {
                if (isGameOver) return; // Prevent clicks when the game is over

                // If the button clicked is not the mole, game over
                if (v.getVisibility() != View.VISIBLE) {
                    gameOver("Mole Escaped!");
                } else {
                    score++;
                    scoreTextView.setText("Score: " + score);
                    if (score > bestScore) {
                        bestScore = score;
                        bestScoreTextView.setText("Best Score: " + bestScore);
                    }
                    v.setVisibility(View.INVISIBLE); // Hide mole after hit
                }
            });
        }

        // Restart button functionality
        restartButton.setOnClickListener(v -> {
            isGameOver = false;
            gameOverLayout.setVisibility(View.GONE);
            introLayout.setVisibility(View.VISIBLE);
            score = 0;
            scoreTextView.setText("Score: 0");
            handler.removeCallbacks(timerRunnable); // Stop any previous timer
        });
    }

    // Start the game based on selected difficulty
    private void startGame(String difficulty) {
        // Hide the intro screen and show the game layout
        introLayout.setVisibility(View.GONE);
        gameLayout.setVisibility(View.VISIBLE);

        // Adjust game speed and time limit based on the selected difficulty
        int timeLimit = 3000; // Default time limit for "Medium"
        switch (difficulty) {
            case "Easy":
                gameSpeed = 1500; // Slow speed for Easy
                timeLimit = 4000; // 4 seconds for Easy
                break;
            case "Medium":
                gameSpeed = 1000; // Normal speed for Medium
                timeLimit = 3000; // 3 seconds for Medium
                break;
            case "Hard":
                gameSpeed = 500; // Fast speed for Hard
                timeLimit = 1000; // 1 second for Hard
                break;
        }

        // Initialize score
        score = 0;
        scoreTextView.setText("Score: " + score);

        // Start the mole appearance thread
        startMoleAppearance();

        // Start the timer thread
        startTimer(timeLimit);
    }

    // Start mole appearance with the specified delay (gameSpeed)
    private void startMoleAppearance() {
        moleRunnable = new Runnable() {
            @Override
            public void run() {
                if (!isGameOver) {
                    showRandomMole();
                    handler.postDelayed(this, gameSpeed); // Repeat every gameSpeed
                }
            }
        };
        handler.post(moleRunnable);
    }

    // Show a random mole (button)
    private void showRandomMole() {
        // Hide all moles first
        for (int i = 0; i < gridLayout.getChildCount(); i++) {
            Button button = (Button) gridLayout.getChildAt(i);
            button.setVisibility(View.INVISIBLE);
            button.setBackgroundResource(R.drawable.mole); // Reset to mole image
        }

        // Randomly choose a button to show the mole
        int moleIndex = random.nextInt(gridLayout.getChildCount());
        gridLayout.getChildAt(moleIndex).setVisibility(View.VISIBLE);
    }

    // Start the timer with the specified duration
    private void startTimer(int timeLimit) {
        // Remove any previous timer callbacks to avoid overlapping
        handler.removeCallbacks(timerRunnable);

        // Timer to end the game after the specified time limit
        timerRunnable = new Runnable() {
            @Override
            public void run() {
                if (!isGameOver) {
                    gameOver("Time's Up! Mole Escaped");
                }
            }
        };
        handler.postDelayed(timerRunnable, timeLimit);
    }

    // Handle game over scenario
    private void gameOver(String message) {
        isGameOver = true;
        gameLayout.setVisibility(View.GONE);
        gameOverLayout.setVisibility(View.VISIBLE);
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    // Ensure handler is cleared when the activity is destroyed to avoid memory leaks
    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(moleRunnable); // Stop the mole appearance updates when activity is destroyed
        handler.removeCallbacks(timerRunnable); // Stop the timer updates when activity is destroyed
    }
}
