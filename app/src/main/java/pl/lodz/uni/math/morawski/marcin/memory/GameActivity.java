package pl.lodz.uni.math.morawski.marcin.memory;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TableLayout;

public class GameActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        createButtonsDynamically(5);
    }


    private void createButtonsDynamically(int n) {

        for (int i = 0; i < n; i++) {
            Button myButton = new Button(this);
            myButton.setText("Button :"+i);
            myButton.setId(i);
            final int id_ = myButton.getId();

            TableLayout layout = (TableLayout) findViewById(R.id.tableLayout);
            layout.addView(myButton);

            myButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {

                }
            });
        }}
}
