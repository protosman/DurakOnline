package ru.tmkstd.cardgamedurakonline;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import androidx.appcompat.app.AppCompatActivity;

public class LoadActivity extends AppCompatActivity {
    int maxX,maxY;
    int maxGameX,maxGameY;
//    DownloadAllRes downloadAllRes;
    Intent intent;
    Thread loadingThread;
//    Handler handler;
    boolean startGame;

    RelativeLayout layout;

    SharedPreferences save;
    SharedPreferences.Editor editor;
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
        setContentView(R.layout.activity_load);
        layout = findViewById(R.id.loadLayout);
        SharedPreferences save = getSharedPreferences("Save", MODE_PRIVATE);
        switch (save.getInt("colorField", 0)){
            case 0: layout.setBackgroundResource(R.drawable.fon_one); break;
            case 1: layout.setBackgroundResource(R.drawable.fon_two); break;
            case 2: layout.setBackgroundResource(R.drawable.fon_three); break;
            case 3: layout.setBackgroundResource(R.drawable.fon_four); break;
            default:layout.setBackgroundResource(R.drawable.fon_one); break;
        }
        maxX = save.getInt("MaxX",720);
        maxY = save.getInt("MaxY",720);
        maxGameX = save.getInt("maxGameX",720);
        maxGameY = save.getInt("maxGameY",720);
        startGame = save.getBoolean("StartGame",false);

//        handler = new Handler(Looper.getMainLooper());
        startActiv();
    }

    public void startActiv() {
        loadingThread = new Thread(new Runnable() {
            @Override
            public void run() {
                ((App) getApplication()).loadingRes(maxGameX,maxGameY);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (startGame){
                            intent = new Intent(LoadActivity.this, GameEngineActivity.class);
                        }
                        else {
                            intent = new Intent(LoadActivity.this, MainActivity.class);
                        }

                        overridePendingTransition(0,0);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }
                });
//                handler.post(
//                        new Runnable()
//                        {
//                            @Override
//                            public void run()
//                            {
//                                if (startGame){
//                                    intent = new Intent(LoadActivity.this, GameEngineActivity.class);
//                                }
//                                else {
//                                    intent = new Intent(LoadActivity.this, MainActivity.class);
//                                }
//
//                                overridePendingTransition(0,0);
//                                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
//                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                                startActivity(intent);
//                            }
//                        }
//                );
            }
        });
    }

    @Override
    public void onPause() {
        Log.e("LoadActivity  ", "onPause()");
        save = getSharedPreferences("Save", MODE_PRIVATE);
        editor = save.edit();
//        editor.putString("name",name.getText().toString());
        editor.putInt("activ",0);
        editor.commit();
        try{
            loadingThread.join();
        } catch (Exception e) {

        }
        super.onPause();
    }
    public  void onResume(){
        super.onResume();
        loadingThread.start();
    }
}
