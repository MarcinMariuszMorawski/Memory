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

    private int numberOfColumns;
    private int numberOfRows;
    private int height;
    private int width;

    private int photosLeftToTake;
    private int photosCount;
    private int photosInitCounter=0;
    private int buttonsCount;
    boolean disabledButtons = false;

    private MemoryButton latelyClickedButton = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        loadIntenet();

        if(areIntentParametrsCorrect()) {
            initComponents();
            addButtonsToScreen();
            takePictureIntent();
        }
        else{
            finish();
        }
    }

    private  void loadIntenet()
    {
        Intent intent = getIntent();
        numberOfColumns = intent.getIntExtra(MenuActivity.EXTRA_MESSAGE_COLUMNS,0);
        numberOfRows = intent.getIntExtra(MenuActivity.EXTRA_MESSAGE_ROWS,0);
        height = intent.getIntExtra(MenuActivity.EXTRA_MESSAGE_HEIGHT,0);
        width = intent.getIntExtra(MenuActivity.EXTRA_MESSAGE_WIDTH,0);
    }

    private boolean areIntentParametrsCorrect() {
        if(numberOfColumns <2){
            return  false;
        }

        if(numberOfRows <2){
            return  false;
        }

        if(!(numberOfRows * numberOfColumns %2==0))
        {
            return false;
        }

        return true;

    }
    private void initComponents()
    {
        buttonsCount= numberOfColumns * numberOfRows;
        buttonsArray = new MemoryButton[buttonsCount];

        photosCount= numberOfColumns * numberOfRows;
        photosArray = new BitmapDrawable[photosCount];

        photosLeftToTake = photosCount/2;
    }

    private void addButtonsToScreen(){
        int buttonSize = Math.min(height,width) / Math.max(numberOfColumns, numberOfRows);
        final LinearLayout buttonsLayout = findViewById(R.id.buttonsLayout);
        int counter=0;

        for(int i = 0; i< numberOfRows; i++) {
            LinearLayout newLine = new LinearLayout(this);
            newLine.setOrientation(LinearLayout.HORIZONTAL);
            for (int j = 0; j < numberOfColumns; j++) {
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
            Bitmap photo = (Bitmap) extras.get("data");
            insertPhotoToArray(photo);
        }
        else{
            takePictureIntent();
        }
    }

    private void insertPhotoToArray(Bitmap photo){
        photosLeftToTake--;

        photosArray[photosInitCounter]=new BitmapDrawable(getResources(), photo);
        photosArray[photosInitCounter+1]=photosArray[photosInitCounter];
        photosInitCounter=photosInitCounter+2;

        if(photosLeftToTake>0) {
            takePictureIntent();
        }else{
            shufflePicturesForButtons();
        }
    }

    private void shufflePicturesForButtons() {
        Collections.shuffle(Arrays.asList(photosArray));

        for(int i=0;i<photosCount;i++){
            buttonsArray[i].setPhoto(photosArray[i]);
        }
    }

    private View.OnClickListener onMemoryButtonListener = new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                MemoryButton thisClickedButton = (MemoryButton) v;

                if(disabledButtons){
                    return;
                }

                if(latelyClickedButton==null){
                    if(thisClickedButton.tryRevertToFront()) {
                        latelyClickedButton=thisClickedButton;
                    }
                    return;
                }

                if(thisClickedButton.tryRevertToFront()) {
                    if (latelyClickedButton.getPhoto().equals(thisClickedButton.getPhoto())) {
                        pairFound(thisClickedButton);
                    } else{
                        pairNotFound(thisClickedButton);
                    }
                }
            }

            private void pairNotFound(final MemoryButton thisClickedButton) {
                disabledButtons = true;
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        latelyClickedButton.revertToBack();
                        thisClickedButton.revertToBack();
                        latelyClickedButton = null;
                        disabledButtons = false;
                    }
                }, 1000);
            }

            private void pairFound(MemoryButton thisClickedButton) {
                latelyClickedButton.match();
                thisClickedButton.match();
                latelyClickedButton = null;
                checkIfGameEnds();
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
