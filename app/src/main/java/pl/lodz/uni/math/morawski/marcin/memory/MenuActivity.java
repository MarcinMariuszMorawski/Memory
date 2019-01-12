package pl.lodz.uni.math.morawski.marcin.memory;

import android.content.Intent;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public final class MenuActivity extends AppCompatActivity {
    public static final String EXTRA_MESSAGE_COLUMNS = "pl.lodz.uni.math.morawski.marcin.MenuActivity.EXTRA_MESSAGE_COLUMNS";
    public static final String EXTRA_MESSAGE_ROWS = "pl.lodz.uni.math.morawski.marcin.MenuActivity.EXTRA_MESSAGE_ROWS";
    public static final String EXTRA_MESSAGE_HEIGHT = "pl.lodz.uni.math.morawski.marcin.MenuActivity.EXTRA_MESSAGE_HEIGHT";
    public static final String EXTRA_MESSAGE_WIDTH = "pl.lodz.uni.math.morawski.marcin.MenuActivity.EXTRA_MESSAGE_WIDTH";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        final ConstraintLayout constraintLayout = findViewById(R.id.constraintLayout);
        constraintLayout.post(new Runnable() {
            @Override
            public void run() {
                final int HEIGHT = constraintLayout.getMeasuredHeight(); //
                final int WIDTH = constraintLayout.getMeasuredWidth();

                final Button buttonStart2x2 = findViewById(R.id.buttonStartGame2x2);
                final Button buttonStart3x2 = findViewById(R.id.buttonStartGame3x2);
                final Button buttonStart4x3 = findViewById(R.id.buttonStartGame4x3);


                buttonStart2x2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        sendIntent(2, 2, HEIGHT, WIDTH);
                    }
                });

                buttonStart3x2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        sendIntent(3, 2, HEIGHT, WIDTH);
                    }
                });

                buttonStart4x3.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        sendIntent(4, 3, HEIGHT, WIDTH);
                    }
                });
            }
        });
    }

    private void sendIntent(int columns, int rows, int height, int width) {
        Intent intent = new Intent(MenuActivity.this, GameActivity.class);

        intent.putExtra(EXTRA_MESSAGE_COLUMNS, columns);
        intent.putExtra(EXTRA_MESSAGE_ROWS, rows);
        intent.putExtra(EXTRA_MESSAGE_HEIGHT, height);
        intent.putExtra(EXTRA_MESSAGE_WIDTH, width);

        startActivity(intent);
    }
}