package pl.lodz.uni.math.morawski.marcin.memory;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.Arrays;
import java.util.Collections;
import java.util.Objects;

public final class GameActivity extends AppCompatActivity {

    private static final int REQUEST_IMAGE_CAPTURE = 1;

    private MemoryButton[] buttonsArray;
    private BitmapDrawable[] photosArray;

    private int numberOfColumns;
    private int numberOfRows;
    private int screenHeight;
    private int screenWidth;

    private int photosLeftToTake;
    private int photosCount;
    private int photosInitCounter = 0;
    private int buttonsCount;

    private boolean disabledButtonsToClick = false;
    private MemoryButton recentlyClickedButton = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        loadIntent();

        if (areIntentParametersCorrect()) {
            initComponents();
            addButtonsToScreen();
            takePictureIntent();
        } else {
            finish();
        }
    }

    private void loadIntent() {
        Intent intent = getIntent();
        numberOfColumns = intent.getIntExtra(MenuActivity.EXTRA_MESSAGE_COLUMNS, 0);
        numberOfRows = intent.getIntExtra(MenuActivity.EXTRA_MESSAGE_ROWS, 0);
        screenHeight = intent.getIntExtra(MenuActivity.EXTRA_MESSAGE_HEIGHT, 0);
        screenWidth = intent.getIntExtra(MenuActivity.EXTRA_MESSAGE_WIDTH, 0);
    }

    private boolean areIntentParametersCorrect() {
        if (numberOfColumns < 2) {
            return false;
        }

        if (numberOfRows < 2) {
            return false;
        }

        return numberOfRows * numberOfColumns % 2 == 0;
    }

    private void initComponents() {
        buttonsCount = numberOfColumns * numberOfRows;
        buttonsArray = new MemoryButton[buttonsCount];


        photosCount = numberOfColumns * numberOfRows;
        photosArray = new BitmapDrawable[photosCount];

        photosLeftToTake = photosCount / 2;
    }

    private void addButtonsToScreen() {
        int buttonSize = Math.min(screenHeight, screenWidth) / Math.max(numberOfColumns, numberOfRows);
        final LinearLayout buttonsLayout = findViewById(R.id.buttonsLayout);

        for (int i = 0; i < numberOfRows; i++) {
            LinearLayout newLine = new LinearLayout(this);
            newLine.setOrientation(LinearLayout.HORIZONTAL);
            for (int j = 0; j < numberOfColumns; j++) {
                MemoryButton button = createButton(buttonSize, buttonSize);

                int index = i * numberOfColumns + j;
                buttonsArray[index] = button;

                newLine.addView(button);
            }
            buttonsLayout.addView(newLine);
        }
    }

    private MemoryButton createButton(int width, int height) {
        MemoryButton button = new MemoryButton(this);
        button.setWidth(width);
        button.setHeight(height);
        button.setOnClickListener(onMemoryButtonListener);
        return button;
    }

    private void takePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, 1);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap photo = (Bitmap) Objects.requireNonNull(extras).get("data");
            insertPhotoToArray(photo);
        } else {
            takePictureIntent();
        }
    }

    private void insertPhotoToArray(Bitmap photo) {
        photosLeftToTake--;

        photosArray[photosInitCounter] = new BitmapDrawable(getResources(), photo);
        photosArray[photosInitCounter + 1] = photosArray[photosInitCounter];
        photosInitCounter = photosInitCounter + 2;

        if (photosLeftToTake > 0) {
            takePictureIntent();
        } else {
            afterPhotosCreated();
        }
    }

    private void afterPhotosCreated() {
        shufflePicturesArray();
        setPicturesToButtons();
    }

    private void shufflePicturesArray() {
        Collections.shuffle(Arrays.asList(photosArray));
    }

    private void setPicturesToButtons() {
        for (int i = 0; i < photosCount; i++) {
            buttonsArray[i].setPhoto(photosArray[i]);
        }
    }

    private final View.OnClickListener onMemoryButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(final View v) {
            MemoryButton nowClickedButton = (MemoryButton) v;
            memoryButtonClicked(nowClickedButton);
        }
    };


    private void memoryButtonClicked(MemoryButton nowClickedButton) {

        if (disabledButtonsToClick) { // prevent clicking all buttons in same time
            return;
        }

        if (recentlyClickedButton == null) { // when any card is not reverted
            if (nowClickedButton.tryRevertToFront()) {
                recentlyClickedButton = nowClickedButton;
            }
            return;
        }

        if (nowClickedButton.tryRevertToFront()) { // when one card is reverted and we try to revert another one
            if (recentlyClickedButton.getPhoto().equals(nowClickedButton.getPhoto())) {
                pairFound(nowClickedButton);
            } else {
                pairNotFound(nowClickedButton);
            }
        }
    }

    private void pairNotFound(final MemoryButton thisClickedButton) {
        disabledButtonsToClick = true;
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                recentlyClickedButton.revertToBack();
                thisClickedButton.revertToBack();
                recentlyClickedButton = null;
                disabledButtonsToClick = false;
            }
        }, 1000);
    }

    private void pairFound(final MemoryButton thisClickedButton) {
        recentlyClickedButton.match();
        thisClickedButton.match();
        recentlyClickedButton = null;
        checkIfGameEnds();
    }

    private void checkIfGameEnds() {
        int countOfMatched = 0;
        for (MemoryButton button : buttonsArray) {
            if (button.isMatched()) {
                countOfMatched++;
            }
        }
        if (countOfMatched == buttonsCount) {
            endGame();
        }
    }

    private void endGame() {
        Toast.makeText(getBaseContext(), "You won!",
                Toast.LENGTH_LONG).show();
    }
}