package com.horovitz.memorygame;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class MainComputerActivity extends AppCompatActivity {
    Button navigateButton;
    private long startTime;
    private long elapsedTime;
    private TextView timerTextView;
    private Handler handler = new Handler();
    private boolean isGameRunning = false;
    private Runnable timerRunnable = new Runnable() {
        @Override
        public void run() {
            long elapsedTime = System.currentTimeMillis() - startTime;
            timerTextView.setText("time: " + (elapsedTime / 1000));
            if (isGameRunning) {
                handler.postDelayed(this, 100);
            }
        }
    };

    private ImageButton[] buttons = new ImageButton[16];
    private ArrayList<Integer> images = new ArrayList<>();
    private int[] imageResources;

    private int firstChoice = -1;
    private int secondChoice = -1;
    private int firstChoiceIndex = -1;
    private int secondChoiceIndex = -1;
    private boolean[] isButtonFlipped = new boolean[16];
    private boolean[] isButtonMatched = new boolean[16];

    private TextView statusText;
    private Button resetButton;

    private int playerMatches = 0;
    private int computerMatches = 0;
    private int currentPlayer = 1; // 1 for player, 2 for computer
    private boolean computerPlaying = false;
    private boolean isComputerTurn = false;
    private Random random = new Random();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playwithcomputer);


            imageResources = new int[] {R.drawable.animal1, R.drawable.animal1, R.drawable.animal2, R.drawable.animal2,
                    R.drawable.animal3, R.drawable.animal3, R.drawable.animal4, R.drawable.animal4,
                    R.drawable.animal5, R.drawable.animal5, R.drawable.animal6, R.drawable.animal6,
                    R.drawable.animal7, R.drawable.animal7, R.drawable.animal8, R.drawable.animal8};


        buttons[0] = findViewById(R.id.button_1);
        buttons[1] = findViewById(R.id.button_2);
        buttons[2] = findViewById(R.id.button_3);
        buttons[3] = findViewById(R.id.button_4);
        buttons[4] = findViewById(R.id.button_5);
        buttons[5] = findViewById(R.id.button_6);
        buttons[6] = findViewById(R.id.button_7);
        buttons[7] = findViewById(R.id.button_8);
        buttons[8] = findViewById(R.id.button_9);
        buttons[9] = findViewById(R.id.button_10);
        buttons[10] = findViewById(R.id.button_11);
        buttons[11] = findViewById(R.id.button_12);
        buttons[12] = findViewById(R.id.button_13);
        buttons[13] = findViewById(R.id.button_14);
        buttons[14] = findViewById(R.id.button_15);
        buttons[15] = findViewById(R.id.button_16);
        timerTextView = findViewById(R.id.timerTextView);
        statusText = findViewById(R.id.statusText);
        resetButton = findViewById(R.id.resetButton);

        startNewGame();

        for (int i = 0; i < 16; i++) {
            final int index = i;
            buttons[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onButtonClick(index);
                }
            });
        }

        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startNewGame();
            }
        });
    }

    private void startNewGame() {
        for (int i = 0; i < 16; i++) {
            buttons[i].setImageResource(android.R.color.transparent);
            isButtonFlipped[i] = false;
            isButtonMatched[i] = false;
        }

        startTime = System.currentTimeMillis();
        isGameRunning = true;
        handler.postDelayed(timerRunnable, 100);

        images.clear();
        for (int i = 0; i < imageResources.length; i++) {
            images.add(imageResources[i]);
        }
        Collections.shuffle(images);

        firstChoice = -1;
        secondChoice = -1;
        firstChoiceIndex = -1;
        secondChoiceIndex = -1;

        currentPlayer = 1;  // Start with player
        statusText.setText("Your turn!");
    }

    private void onButtonClick(int index) {
        if (isButtonMatched[index] || isComputerTurn) {
            return;
        }

        buttons[index].setImageResource(images.get(index));
        isButtonFlipped[index] = true;

        if (firstChoice == -1) {
            firstChoice = images.get(index);
            firstChoiceIndex = index;
        } else {
            secondChoice = images.get(index);
            secondChoiceIndex = index;
            checkMatch();
        }
    }

    private void checkMatch() {
        if (firstChoice == secondChoice) {
            if (currentPlayer == 1) {
                playerMatches++;
                statusText.setText("You found a match!");
            } else {
                computerMatches++;
                statusText.setText("Computer found a match!");
            }
            isButtonMatched[firstChoiceIndex] = true;
            isButtonMatched[secondChoiceIndex] = true;
            resetChoices();
            switchPlayer();
        } else {
            statusText.setText("Try again later,");
            buttons[firstChoiceIndex].postDelayed(new Runnable() {
                @Override
                public void run() {
                    buttons[firstChoiceIndex].setImageResource(android.R.color.transparent);
                    buttons[secondChoiceIndex].setImageResource(android.R.color.transparent);
                    resetChoices();
                    switchPlayer();
                }
            }, 700);
        }
    }

    private void resetChoices() {
        firstChoice = -1;
        secondChoice = -1;
        firstChoiceIndex = -1;
        secondChoiceIndex = -1;
    }

    private void switchPlayer() {
        if (currentPlayer == 1) {
            currentPlayer = 2;  // Switch to computer
            statusText.setText(statusText.getText().toString()+" Computer's turn!");
            isComputerTurn = true;
            computerMove();
        } else {
            currentPlayer = 1;  // Switch to player
            statusText.setText(statusText.getText().toString()+" Your turn!");
            isComputerTurn = false;
        }
    }

    private void computerMove() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!isGameRunning) return;

                // Simulate computer turn with random moves (basic AI)
                int index1 = random.nextInt(16);
                int index2 = random.nextInt(16);
                while (index2 == index1 || isButtonMatched[index1] || isButtonMatched[index2]) {
                    index1 = random.nextInt(16);
                    index2 = random.nextInt(16);
                }

                buttons[index1].setImageResource(images.get(index1));
                buttons[index2].setImageResource(images.get(index2));

                if (images.get(index1).equals(images.get(index2))) {

                    isButtonMatched[index1] = true;
                    isButtonMatched[index2] = true;

                    computerMatches++;
                    statusText.setText("Computer found a match!");
                    switchPlayer();

                } else {
                    int finalIndex = index2;
                    int finalIndex1 = index1;
                    buttons[index1].postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            buttons[finalIndex1].setImageResource(android.R.color.transparent);
                            buttons[finalIndex].setImageResource(android.R.color.transparent);
                            statusText.setText("Computer did not find a match!");
                            switchPlayer();
                        }
                    }, 700);
                }
            }
        }, 1000);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_firstpage) {
            Intent intent = new Intent(MainComputerActivity.this, MainActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }
}
