package pl.lodz.uni.math.morawski.marcin.memory;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.provider.MediaStore;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

public final class GameActivity extends AppCompatActivity {

    static final int REQUEST_IMAGE_CAPTURE = 1;

    private Button[] buttonsArray;
    private int photosLeftToTake;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        Intent intent = getIntent();
        final int columns = intent.getIntExtra(MenuActivity.EXTRA_MESSAGE_COLUMNS,4);
        final int rows = intent.getIntExtra(MenuActivity.EXTRA_MESSAGE_ROWS,4);
        final int height = intent.getIntExtra(MenuActivity.EXTRA_MESSAGE_HEIGHT,0);
        final int width = intent.getIntExtra(MenuActivity.EXTRA_MESSAGE_WIDTH,0);

        buttonsArray = new Button[columns*rows];
        photosLeftToTake = columns*rows/2;

        dispatchTakePictureIntent();
        addButtonsToScreen(columns,rows,height,width);
        initPicturesToButtons();
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
            buttonsArray[photosLeftToTake].setBackground(new BitmapDrawable(getResources(), imageBitmap));
            System.out.println(photosLeftToTake);
            if(photosLeftToTake>1)
            {
                photosLeftToTake--;
                dispatchTakePictureIntent();
            }
        }
        else{
            dispatchTakePictureIntent();
        }
    }
    private void addButtonsToScreen(int numberOfButtonsPerRow,  int numberOfRows, int height, int width){


        int buttonSize = Math.min(height,width) / Math.max(numberOfButtonsPerRow,numberOfRows);


        final LinearLayout buttonsLayout = findViewById(R.id.buttonsLayout);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                                                                         LinearLayout.LayoutParams.WRAP_CONTENT);
        int counter=0;
        for(int i=0;i<numberOfRows;i++) {
            LinearLayout newLine = new LinearLayout(this);
            newLine.setLayoutParams(params);
            newLine.setOrientation(LinearLayout.HORIZONTAL);

            for (int j = 0; j < numberOfButtonsPerRow; j++) {
                Button button = new Button(this);
                button.setWidth(buttonSize);
                button.setHeight(buttonSize);

               // button.setId(i);
                //button.setLayoutParams(params);
                newLine.addView(button);
                buttonsArray[counter] = button;
                counter++;
            }

            buttonsLayout.addView(newLine);
        }
    }

    private void initPicturesToButtons() {

    }

}
