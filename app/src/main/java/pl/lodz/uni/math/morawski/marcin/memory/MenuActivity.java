package pl.lodz.uni.math.morawski.marcin.memory;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MenuActivity extends AppCompatActivity {
    public static final String EXTRA_MESSAGE = "pl.lodz.uni.math.morawski.marcin.MenuActivity.MESSAGE";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        final Button buttonStart4x3 = findViewById(R.id.buttonStartGame4x3); //6
        final Button buttonStart4x4 = findViewById(R.id.buttonStartGame4x4); //8


        buttonStart4x3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MenuActivity.this,GameActivity.class);
                int photosCount = 6;
                intent.putExtra(EXTRA_MESSAGE,photosCount);
                startActivity(intent);
            }
        });

        buttonStart4x4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MenuActivity.this,GameActivity.class);
                int photosCount = 8;
                intent.putExtra(EXTRA_MESSAGE,photosCount);
                startActivity(intent);
            }
        });
    }
}