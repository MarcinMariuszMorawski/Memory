package pl.lodz.uni.math.morawski.marcin.memory;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.Arrays;
import java.util.Collections;

public final class GameActivity extends AppCompatActivity {

    static final int REQUEST_IMAGE_CAPTURE = 1;

    private MemoryButton[] buttonsArray;
    private BitmapDrawable[] photosArray;
    private int photosLeftToTake;
    private int photosCount;
    private int photosInitCounter=0;
    private int buttonsCount;
    private MemoryButton latelyClickedButton = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        Intent intent = getIntent();
        final int columns = intent.getIntExtra(MenuActivity.EXTRA_MESSAGE_COLUMNS,0);
        final int rows = intent.getIntExtra(MenuActivity.EXTRA_MESSAGE_ROWS,0);
        final int height = intent.getIntExtra(MenuActivity.EXTRA_MESSAGE_HEIGHT,0);
        final int width = intent.getIntExtra(MenuActivity.EXTRA_MESSAGE_WIDTH,0);

        if(columns<2 || rows<2) {
            finish();
        }
        else{
            buttonsCount=columns*rows;
            buttonsArray = new MemoryButton[buttonsCount];

            photosCount=columns*rows;
            photosArray = new BitmapDrawable[photosCount];

            photosLeftToTake = photosCount/2;

            addButtonsToScreen(columns,rows,height,width);
            dispatchTakePictureIntent();
        }
    }
    private void addButtonsToScreen(int numberOfButtonsPerRow,  int numberOfRows, int height, int width){
        int buttonSize = Math.min(height,width) / Math.max(numberOfButtonsPerRow,numberOfRows);
        final LinearLayout buttonsLayout = findViewById(R.id.buttonsLayout);
        int counter=0;

        for(int i=0;i<numberOfRows;i++) {
            LinearLayout newLine = new LinearLayout(this);
            newLine.setOrientation(LinearLayout.HORIZONTAL);
            for (int j = 0; j < numberOfButtonsPerRow; j++) {
                MemoryButton button = new MemoryButton(this);
                button.setWidth(buttonSize);
                button.setHeight(buttonSize);
                button.setOnClickListener(onMemoryButtonListener);
                newLine.addView(button);
                buttonsArray[counter] = button;
                counter++;
            }
            buttonsLayout.addView(newLine);
        }
    }
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, 1);
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            photosLeftToTake--;

            photosArray[photosInitCounter]=new BitmapDrawable(getResources(), imageBitmap);
            photosArray[photosInitCounter+1]=photosArray[photosInitCounter];
            photosInitCounter=photosInitCounter+2;

            if(photosLeftToTake>0) {
                dispatchTakePictureIntent();
            }else{
                afterCompletedPhotos();
            }
        }
        else{
            dispatchTakePictureIntent();
        }
    }
    private void afterCompletedPhotos() {
        ShufflePicturesForButtons();

    }
    private void ShufflePicturesForButtons() {
        Collections.shuffle(Arrays.asList(photosArray));

        for(int i=0;i<photosCount;i++){
            buttonsArray[i].setFront(photosArray[i]);
        }
    }
    private View.OnClickListener onMemoryButtonListener = new View.OnClickListener() {

        @Override
        public void onClick(final View v) {
           final  MemoryButton clickedButton = (MemoryButton) v;

            if(latelyClickedButton==null){
                if(clickedButton.revertToFront()) {
                    latelyClickedButton=clickedButton;
                }
            }else{
                if(clickedButton.revertToFront()) {

                    if(latelyClickedButton.getFront().equals(clickedButton.getFront())){
                        latelyClickedButton.match();
                        clickedButton.match();
                        latelyClickedButton=null;
                        checkIfGameEnds();
                    }
                    else{
                        final Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                latelyClickedButton.revertToBack();
                                clickedButton.revertToBack();
                                latelyClickedButton=null;
                                //isBusy = false;
                            }
                        }, 1000);


                    }


                }
            }
        }

        private void checkIfGameEnds() {

            int countOfMatched = 0;
            for(MemoryButton button : buttonsArray){
                if(button.isMatched())
                {
                    countOfMatched++;
                }
            }

            if(countOfMatched==buttonsCount) {
                Toast.makeText(getBaseContext(), "You won the game!",
                        Toast.LENGTH_LONG).show();
            }

        }
    };
}
