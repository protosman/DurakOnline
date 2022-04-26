package ru.tmkstd.cardgamedurakonline;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class RulesActivity extends AppCompatActivity {

    ImageView back;
    RelativeLayout layout;
//    int x;
    int colorField,fonBtnWidth;
    SharedPreferences save;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow()
                    .getDecorView()
                    .setSystemUiVisibility(
                            View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    );
        }
        setContentView(R.layout.activity_rules);
        layout = findViewById(R.id.rulesLayout);
        save = getSharedPreferences("Save", MODE_PRIVATE);

        colorField = save.getInt("colorField",0);


        switch (colorField){
            case 1: layout.setBackgroundResource(R.drawable.fon_two); break;
            case 2: layout.setBackgroundResource(R.drawable.fon_three); break;
            case 3: layout.setBackgroundResource(R.drawable.fon_four); break;
            default:layout.setBackgroundResource(R.drawable.fon_one); break;
        }
        back = findViewById(R.id.rules_back);
        back.setOnClickListener(v -> onBackPressed());
        back.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN) {
                    back.setX(-back.getWidth()*0.06f);
                    back.setY(-back.getHeight()*0.06f);
                    back.setScaleX(0.9f);
                    back.setScaleY(0.9f);
                }
                if(event.getAction() == MotionEvent.ACTION_UP) {
                    back.setX(0);
                    back.setY(0);
                    back.setScaleX(1);
                    back.setScaleY(1);
                }
                return false;
            }
        });

    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(RulesActivity.this, MainActivity.class);

        overridePendingTransition(0,0);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}
