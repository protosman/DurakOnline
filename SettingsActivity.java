package ru.tmkstd.cardgamedurakonline;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;

public class SettingsActivity extends AppCompatActivity {
    int kolodaWidth,fonBtnWidth;
    ImageView startGame;
    int btnWidth, btnHeight,btn2Width,btn2Height;
    ImageView kolodaSmall, kolodaMedium, kolodaLarge;
    ImageView fon0btn, fon1btn, fon2btn, fon3btn;
    ImageView flip,trans, back, logout;
    ImageView icon_ok_1,icon_ok_2,icon_ok_3;
    RelativeLayout layout;
    int x,y;
    long coin;
    boolean versionStart;
    AdView mAdView;

    boolean modGame;
    int colorField, numCards;
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
        setContentView(R.layout.activity_settings);
        mAdView = findViewById(R.id.adView);
        mAdView.loadAd(new AdRequest.Builder().build());
        layout = findViewById(R.id.settingsLayout);
        save = getSharedPreferences("Save", MODE_PRIVATE);

        modGame = save.getBoolean("modGame",true);
        colorField = save.getInt("colorField",0);
        numCards = save.getInt("numCards",36);
        x = save.getInt("MaxX",720);
        y = save.getInt("MaxY",720);
        coin = save.getLong("coin",0);
        versionStart = save.getBoolean("version",false);
        Log.d("SETTINGACT",coin+"");

        switch (colorField){
            case 0: layout.setBackgroundResource(R.drawable.fon_one); break;
            case 1: layout.setBackgroundResource(R.drawable.fon_two); break;
            case 2: layout.setBackgroundResource(R.drawable.fon_three); break;
            case 3: layout.setBackgroundResource(R.drawable.fon_four); break;
            default:layout.setBackgroundResource(R.drawable.fon_one); break;
        }

        fonBtnWidth = x/5;
        kolodaWidth = x/5;
        btn2Width = x*22/50;
        btn2Height = btn2Width*10/25;
        btnWidth = x/2;
        btnHeight = btnWidth/25*10;



        back = findViewById(R.id.settings_back);
        back.setOnClickListener(v -> onBackPressed());
        back.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction()==MotionEvent.ACTION_DOWN){
                    back.setX(-back.getHeight()*0.06f);
                    back.setY(-back.getHeight()*0.06f);
                    back.setScaleY(0.9f);
                    back.setScaleX(0.9f);
                }
                if(event.getAction()==MotionEvent.ACTION_UP) {
                    back.setX(0);
                    back.setY(0);
                    back.setScaleY(1);
                    back.setScaleX(1);

                }
                return false;
            }
        });

        logout = findViewById(R.id.btn_settings_logout);
        logout.setOnClickListener(v -> logoutClick());
        logout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction()==MotionEvent.ACTION_DOWN) {
                    logout.setX(logout.getX()+logout.getWidth()*0.06f);
                    logout.setY(logout.getY()-logout.getHeight()*0.06f);
                    logout.setScaleX(0.9f);
                    logout.setScaleY(0.9f);
                }
                if(event.getAction()==MotionEvent.ACTION_UP) {
                    logout.setScaleX(1);
                    logout.setScaleY(1);
                    logout.setX(logout.getX()-logout.getWidth()*0.06f);
                    logout.setY(logout.getY()+logout.getHeight()*0.06f);
                }
                return false;
            }
        });

        flip = findViewById(R.id.btn_settings_flip);
        flip.setOnClickListener(v -> flipClick());

        trans = findViewById(R.id.btn_settings_transferable);
        trans.setOnClickListener(v -> transClick());

        icon_ok_1 = findViewById(R.id.btn_ok1);
        icon_ok_2 = findViewById(R.id.btn_ok2);
        icon_ok_3 = findViewById(R.id.btn_ok3);

        fon0btn = findViewById(R.id.btn_fon_green);
        fon0btn.setOnClickListener(v -> fon0btnClick());

        fon1btn = findViewById(R.id.btn_fon_blue);
        fon1btn.setOnClickListener(v -> fon1btnClick());

        fon2btn = findViewById(R.id.btn_fon_purple);
        fon2btn.setOnClickListener(v -> fon2btnClick());

        fon3btn = findViewById(R.id.btn_fon_red);
        fon3btn.setOnClickListener(v -> fon3btnClick());

        kolodaSmall = findViewById(R.id.btn_num_koloda_s);
        kolodaSmall.setOnClickListener(v -> kolodaSmallClick());


        kolodaMedium = findViewById(R.id.btn_num_koloda_m);
        kolodaMedium.setOnClickListener(v -> kolodaMediumClick());

        kolodaLarge = findViewById(R.id.btn_num_koloda_l);
        kolodaLarge.setOnClickListener(v -> kolodaLargeClick());

        startGame = findViewById(R.id.btn_settings_start);
        startGame.setOnClickListener(v -> startGameClick());
        startGame.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction()==MotionEvent.ACTION_DOWN){
                    startGame.setScaleY(0.9f);
                    startGame.setScaleX(0.9f);
                    startGame.setRotation(-5);
                }
                if(event.getAction()==MotionEvent.ACTION_UP) {
                    startGame.setScaleY(1);
                    startGame.setScaleX(1);
                    startGame.setRotation(0);

                }
                return false;
            }
        });


        switch (colorField){
            case 1:
                fon1btn.setScaleX(0.8f);
                fon1btn.setScaleY(0.8f);
//                fon1btn.setRotation(-5);
                break;
            case 2:
                fon2btn.setScaleX(0.8f);
                fon2btn.setScaleY(0.8f);
//                fon2btn.setRotation(-5);
                break;
            case 3:
                fon3btn.setScaleX(0.8f);
                fon3btn.setScaleY(0.8f);
//                fon3btn.setRotation(-5);
                break;
            default:
                fon0btn.setScaleX(0.8f);
                fon0btn.setScaleY(0.8f);
//                fon0btn.setRotation(-5);
                break;
        }
        switch (numCards){
            case 24:
                kolodaSmall.setScaleX(0.8f);
                kolodaSmall.setScaleY(0.8f);
//                kolodaSmall.setRotation(-5);
                break;
            case 36:
                kolodaMedium.setScaleX(0.8f);
                kolodaMedium.setScaleY(0.8f);
//                kolodaMedium.setRotation(-5);
                break;
            default:
                kolodaLarge.setScaleX(0.8f);
                kolodaLarge.setScaleY(0.8f);
//                kolodaLarge.setRotation(-5);
                break;
        }
        if(modGame) {

            trans.setScaleX(0.9f);
            trans.setScaleY(0.9f);
//            trans.setRotation(-5);
        }
        else {
            flip.setScaleX(0.9f);
            flip.setScaleY(0.9f);
//            flip.setRotation(-5);
        }
    }

//    void backClick() {
//        Intent intent = new Intent(SettingsActivity.this, MainActivity.class);
//        overridePendingTransition(0,0);
//        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
//        startActivity(intent);
//    }
    void logoutClick(){

        AlertDialog.Builder quitDialog = new AlertDialog.Builder(this);
        quitDialog.setTitle("Выйдти из аккаунта Google?");
        quitDialog.setPositiveButton("Да!", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestIdToken(getString(R.string.my_Id))
                        .requestEmail()
                        .build();

                GoogleSignInClient mGoogleSignInClient = GoogleSignIn.getClient(SettingsActivity.this, gso);

                FirebaseAuth.getInstance().signOut();
                mGoogleSignInClient.signOut()
                        .addOnCompleteListener(SettingsActivity.this, task -> {
                            logoutFun();

                        });
            }
        });
        quitDialog.setNegativeButton("Нет", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        quitDialog.show();



    }
    void logoutFun() {

        Intent intent = new Intent(SettingsActivity.this, LoginActivity.class);

        overridePendingTransition(0,0);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
    void startGameClick() {
//        startGame.setBackgroundResource(R.drawable.startgame_press);
        editor = save.edit();
        editor.putBoolean("firstGame",false);
        editor.commit();
        if(versionStart) {
            if (coin < 10) {
//                startGame.setBackgroundResource(R.drawable.startgame);
                Toast.makeText(SettingsActivity.this, "Недостаточно монет.", Toast.LENGTH_SHORT).show();
            } else {
                Intent intent = new Intent(SettingsActivity.this, GameEngineActivity.class);
                overridePendingTransition(0, 0);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
            }
        }
        else {
            AlertDialog.Builder quitDialog = new AlertDialog.Builder(this);
            quitDialog.setTitle("Пожалуйста обновите приложение");
            quitDialog.setPositiveButton("Google play!", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String urle = getPackageName();
                    try {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id="+urle)));
                    } catch (android.content.ActivityNotFoundException e) {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id="+urle)));
                    }
                }
            });
            quitDialog.setNegativeButton("Позже", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            quitDialog.show();
//            Toast.makeText(SettingsActivity.this, "Сервер не подключен. Попробуёте снова.", Toast.LENGTH_SHORT).show();
//            onBackPressed();
        }

    }
    void kolodaSmallClick() {
        numCards = 24;
        kolodaSmall.setScaleX(0.8f);
        kolodaSmall.setScaleY(0.8f);
        icon_ok_3.setX(kolodaSmall.getX());
        kolodaMedium.setScaleX(1);
        kolodaMedium.setScaleY(1);
        kolodaLarge.setScaleX(1);
        kolodaLarge.setScaleY(1);
    }
    void kolodaMediumClick() {
        numCards = 36;
        kolodaSmall.setScaleX(1);
        kolodaSmall.setScaleY(1);
        kolodaMedium.setScaleX(0.8f);
        kolodaMedium.setScaleY(0.8f);
        icon_ok_3.setX(kolodaMedium.getX());
        kolodaLarge.setScaleX(1);
        kolodaLarge.setScaleY(1);
    }
    void kolodaLargeClick() {
        numCards = 52;
        kolodaSmall.setScaleX(1);
        kolodaSmall.setScaleY(1);
        kolodaMedium.setScaleX(1);
        kolodaMedium.setScaleY(1);
        kolodaLarge.setScaleX(0.8f);
        kolodaLarge.setScaleY(0.8f);
        icon_ok_3.setX(kolodaLarge.getX());
    }
    void flipClick(){
        modGame = false;
        icon_ok_1.setX(flip.getX());
        trans.setScaleX(1);
        trans.setScaleY(1);
        flip.setScaleX(0.9f);
        flip.setScaleY(0.9f);
    }
    void transClick() {
        modGame = true;
        icon_ok_1.setX(trans.getX());
        trans.setScaleX(0.9f);
        trans.setScaleY(0.9f);
        flip.setScaleX(1);
        flip.setScaleY(1);

    }
    void fon0btnClick() {
        colorField = 0;
        fon0btn.setScaleX(0.8f);
        fon0btn.setScaleY(0.8f);
        icon_ok_2.setX(fon0btn.getX());
        fon1btn.setScaleY(1);
        fon1btn.setScaleX(1);
        fon2btn.setScaleY(1);
        fon2btn.setScaleX(1);
        fon3btn.setScaleY(1);
        fon3btn.setScaleX(1);
        layout.setBackgroundResource(R.drawable.fon_one);
    }
    void fon1btnClick() {
        colorField = 1;
        fon0btn.setScaleX(1);
        fon0btn.setScaleY(1);
        fon1btn.setScaleY(0.8f);
        fon1btn.setScaleX(0.8f);
        icon_ok_2.setX(fon1btn.getX());
        fon2btn.setScaleY(1);
        fon2btn.setScaleX(1);
        fon3btn.setScaleY(1);
        fon3btn.setScaleX(1);
        layout.setBackgroundResource(R.drawable.fon_two);

    }
    void fon2btnClick() {
        colorField = 2;
        fon0btn.setScaleX(1);
        fon0btn.setScaleY(1);
        fon1btn.setScaleY(1);
        fon1btn.setScaleX(1);
        fon2btn.setScaleY(0.8f);
        fon2btn.setScaleX(0.8f);
        icon_ok_2.setX(fon2btn.getX());
        fon3btn.setScaleY(1);
        fon3btn.setScaleX(1);
        layout.setBackgroundResource(R.drawable.fon_three);

    }
    void fon3btnClick() {
        colorField = 3;
        fon0btn.setScaleX(1);
        fon0btn.setScaleY(1);
        fon1btn.setScaleY(1);
        fon1btn.setScaleX(1);
        fon2btn.setScaleY(1);
        fon2btn.setScaleX(1);
        fon3btn.setScaleY(0.8f);
        fon3btn.setScaleX(0.8f);
        icon_ok_2.setX(fon3btn.getX());
        layout.setBackgroundResource(R.drawable.fon_four);

    }

    @Override
    public void onPause() {

        Log.e("SettingActivity  ", "onPause()");
        save = getSharedPreferences("Save", MODE_PRIVATE);
        editor = save.edit();
        editor.putBoolean("modGame",modGame);
        editor.putInt("colorField",colorField);
        editor.putInt("numCards",numCards);
        editor.commit();
        super.onPause();
    }

    @SuppressLint("NewApi")
    @Override
    public void onWindowFocusChanged(boolean hasFocus)
    {
        icon_ok_1.setVisibility(View.VISIBLE);
        icon_ok_2.setVisibility(View.VISIBLE);
        icon_ok_3.setVisibility(View.VISIBLE);
        if(modGame){
            icon_ok_1.setX(trans.getX());
        }
        else {
            icon_ok_1.setX(flip.getX());
        }
        switch (colorField){
            case 1:
                icon_ok_2.setX(fon1btn.getX());
                break;
            case 2:
                icon_ok_2.setX(fon2btn.getX());
                break;
            case 3:
                icon_ok_2.setX(fon3btn.getX());
                break;
            default:
                icon_ok_2.setX(fon0btn.getX());
                break;
        }
        switch (numCards){
            case 24:
                icon_ok_3.setX(kolodaSmall.getX());
                break;
            case 36:
                icon_ok_3.setX(kolodaMedium.getX());
                break;
            default:
                icon_ok_3.setX(kolodaLarge.getX());
                break;
        }
        super.onWindowFocusChanged(hasFocus);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && hasFocus)
        {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(SettingsActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        overridePendingTransition(0,0);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
    }

}
