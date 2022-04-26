package ru.tmkstd.cardgamedurakonline;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;


public class GameEngineActivity extends AppCompatActivity{

    final boolean isRobotTest = true;
    private SoundPool mSoundPool;
    private int step, mix, take;
    GameEngine gameEngine;
    RobotsAdapter robotsAdapter;
//    RobotClass robotClass;
    SharedPreferences save;
    SharedPreferences.Editor editor;
    DownloadAllRes downloadAllRes;

    boolean startGame, gameMaster;
    boolean hodit,otbivaet;
    String myCards,hoditCard, otbivaetCard, oldMyCards;
    String opponent;

    boolean modGame;
    int colorField, numCards,koziri;
//    String name;

    RelativeLayout layout;
    int x,y;
    int maxGameX,maxGameY;
    int timer;
//    int coin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Делаю так чтобы клавиши громкости изменяли громкость аудио потока Медиа(игры)
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
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
            final View decorView = getWindow().getDecorView();
            decorView.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener(){

                @Override
                public void onSystemUiVisibilityChange(int visibility) {
                    if((visibility &View.SYSTEM_UI_FLAG_FULLSCREEN) == 0){
                        decorView
                                .setSystemUiVisibility(
                                        View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                                                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                                                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                                                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                                                | View.SYSTEM_UI_FLAG_FULLSCREEN
                                                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                                );
                    }
                }
            });
        }
        save = getSharedPreferences("Save", MODE_PRIVATE);

//        name = save.getString("name","Игрок");
        modGame = save.getBoolean("modGame",true);
        colorField = save.getInt("colorField",0);
        numCards = save.getInt("numCards",36);
        x = save.getInt("MaxX",720);
        y = save.getInt("MaxY",720);
        maxGameX = save.getInt("maxGameX",720);
        maxGameY = save.getInt("maxGameY",720);

        startGame = save.getBoolean("StartGame",false);
        gameMaster = save.getBoolean("gameMaster",false);
        opponent = save.getString("opponent","");
        hodit = save.getBoolean("hodit",false);
        otbivaet = save.getBoolean("otbivaet",false);
        myCards = save.getString("myCards","");
        oldMyCards = save.getString("oldMyCards","");
        hoditCard = save.getString("hoditCard","");
        otbivaetCard = save.getString("otbivaetCard","");
        koziri = save.getInt("koziri",-1);
        timer = save.getInt("timer",0);

        if (isRobotTest){
            setContentView(R.layout.activity_robot_game);
            robotsAdapter = new RobotsAdapter(findViewById(R.id.La0),findViewById(R.id.La1),findViewById(R.id.La2),findViewById(R.id.La3));
        }
        else {
            downloadAllRes = ((App) getApplication()).getDownloadAllRes();

            if(downloadAllRes == null) {
//                setContentView(R.layout.activity_empty);
//                layout = findViewById(R.id.emptyLayout);
                setContentView(R.layout.activity_load);
                layout = findViewById(R.id.loadLayout);
                switch (colorField){
                    case 0: layout.setBackgroundResource(R.drawable.fon_one); break;
                    case 1: layout.setBackgroundResource(R.drawable.fon_two); break;
                    case 2: layout.setBackgroundResource(R.drawable.fon_three); break;
                    case 3: layout.setBackgroundResource(R.drawable.fon_four); break;
                    default:layout.setBackgroundResource(R.drawable.fon_one); break;
                }
                new Thread(new Runnable() {
                    @Override
                    public void run() {
    //                    downloadAllRes = new DownloadAllRes(context,sizeDispX,sizeDispY);
//                        ((App) getApplication()).loadingRes(maxGameX,maxGameY);
//                        downloadAllRes = ((App) getApplication()).getDownloadAllRes();
                        // попробую так
                        downloadAllRes = new DownloadAllRes(GameEngineActivity.this,maxGameX,maxGameY);
//                        new Handler(Looper.getMainLooper()).post(
//                                new Runnable()
//                                {
//                                    @Override
//                                    public void run()
//                                    {
//                                        setView();
//                                    }
//                                }
//                        );
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                setView();
                                //не знаю зачем здесь resume();
                                //без этого не запускается поток игры
                                onResume();
//                                if(gameEngine!=null) {
//                                    gameEngine.resume();
//                                }
//                                layout.setBackgroundResource(R.drawable.fon_four);
//                                setContentView(R.layout.activity_load);
//                                gameEngine = new GameEngine(GameEngineActivity.this, maxGameX, maxGameY, colorField, modGame, numCards, downloadAllRes);
//                                Log.d("deeeeee","eeeeerrr");
//                                setContentView(gameEngine);
                            }
                        });
                    }
                }).start();
            }
            else {
                setView();
            }

        }
    }
    protected void setView(){
        if (getIntent().getBooleanExtra("friend", false)) {
            NotificationManager notificationManager =
                    (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            notificationManager.cancel(1);
            gameEngine = new GameEngine(GameEngineActivity.this, maxGameX, maxGameY, colorField,
                    getIntent().getBooleanExtra("mode", false),
                    getIntent().getIntExtra("number", 36),
                    downloadAllRes,
                    maxGameX*save.getFloat("counterCoinWidth",maxGameX/3));
            setContentView(gameEngine);
            gameEngine.openFriendGame(getIntent().getStringExtra("friendId"), getIntent().getBooleanExtra("gameMaster", false));
        }
        else {
            gameEngine = new GameEngine(GameEngineActivity.this, maxGameX, maxGameY, colorField, modGame, numCards, downloadAllRes,
                    maxGameX*save.getFloat("counterCoinWidth",maxGameX/3));
            Log.d("deeeeee","eeeeerrr");
            setContentView(gameEngine);
            if (startGame) {
                gameEngine.reopenGame(gameMaster, opponent, hodit, otbivaet, myCards, oldMyCards, hoditCard, otbivaetCard, koziri, timer);
            } else {
                gameEngine.openNewGame();
            }
        }

    }
    @SuppressLint("NewApi")
    @Override
    public void onWindowFocusChanged(boolean hasFocus)
    {
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
    public void onResume() {

        super.onResume();
        Log.e("GameView  ", "onResume()");
        if(gameEngine!=null) {
            gameEngine.resume();
            if(Build.VERSION.SDK_INT<21){
                createOldSoundPool();
            }
            else {
                createNewSoundPool();
            }
            gameEngine.startGameClass.setSoundPool(mSoundPool);
            mSoundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
                @Override
                public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                    if(gameEngine!=null && gameEngine.startGameClass!=null) {
                        if (mix == sampleId)
                            gameEngine.startGameClass.setSound(0, mix);
                        if (take == sampleId)
                            gameEngine.startGameClass.setSound(1, take);
                        if (step == sampleId)
                            gameEngine.startGameClass.setSound(2, step);
                    }
//                switch (sampleId) {
//                    case mix:
//                        gameEngine.startGameClass.setSound(0,mix);
//                        break;
//                    case take:
//                        gameEngine.startGameClass.setSound(1,take);
//                        break;
//                    case step:
//                        gameEngine.startGameClass.setSound(2,step);
//                        break;
//                }
                }
            });
            mix = mSoundPool.load(this,R.raw.first_random_kolod,1);
            take = mSoundPool.load(this,R.raw.take_card,1);
            step = mSoundPool.load(this,R.raw.step_card,1);
        }
    }
    @Override
    public void onPause() {
        super.onPause();
        if(!isRobotTest) {
            Log.e("GameView  ", "onPause()");
            if (gameEngine != null) {
                gameEngine.pause();
                save = getSharedPreferences("Save", MODE_PRIVATE);
                editor = save.edit();
                if (gameEngine.startGameClass.gameField != null) {
                    editor.putBoolean("StartGame", gameEngine.startGameClass.gameField.game);
                }
                if (gameEngine.findNewGame != null) {
                    if (gameEngine.findNewGame.coin > 0) {
                        editor.putLong("coin", gameEngine.findNewGame.realCoin);
                    }
                }
                if (gameEngine.startGameClass.gameField != null && gameEngine.startGame) {
                    editor.putBoolean("gameMaster", gameEngine.findNewGame.gameMaster);
                    editor.putString("opponent", gameEngine.findNewGame.opponent);
                    editor.putBoolean("hodit", gameEngine.startGameClass.gameField.hodit);
                    editor.putBoolean("otbivaet", gameEngine.startGameClass.gameField.otbivaet);
                    editor.putString("myCards", gameEngine.startGameClass.gameField.myCards);
                    editor.putString("oldMyCards", gameEngine.startGameClass.gameField.oldMyCards);
//                    oldMyCards = save.getString("oldMyCards","");
                    editor.putString("hoditCard", gameEngine.startGameClass.gameField.hoditCard);
                    editor.putString("otbivaetCard", gameEngine.startGameClass.gameField.otbivaetCard);
                    editor.putInt("koziri", gameEngine.startGameClass.gameField.koziri);
                    editor.putInt("timer", (int) (gameEngine.startGameClass.gameField.timer));
                }
                editor.putBoolean("friend", false);
                editor.commit();
            }
            if(mSoundPool !=null) mSoundPool.release();
            mSoundPool = null;
        }
    }
    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {
        if(gameEngine!=null) gameEngine.motion(motionEvent,(float)x,(float)y);
//        if(gameEngine!=null) gameEngine.motion(motionEvent);
//        if(mix>-1);
//        mSoundPool.play(mix,1,1,1,0,1);
        return true;
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void createNewSoundPool() {
        AudioAttributes attributes = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build();
        mSoundPool = new SoundPool.Builder()
                .setAudioAttributes(attributes)
                .build();
    }

    @SuppressWarnings("deprecation")
    private void createOldSoundPool() {
        mSoundPool = new SoundPool(3, AudioManager.STREAM_MUSIC,0);
    }
    @Override
    public void onBackPressed() {
        if(!isRobotTest) {
            if(gameEngine!=null) {
                if (gameEngine.startGameClass.gameField != null && gameEngine.startGameClass.gameField.game) {
                    gameEngine.flagClick();
                } else {
                    gameEngine.exitActivity();
                }
            }
            else {
                Intent intent = new Intent(GameEngineActivity.this, MainActivity.class);

                overridePendingTransition(0, 0);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);

            }
        }
        else {
            robotsAdapter.deleteAll();
            robotsAdapter = null;
            Intent intent = new Intent(GameEngineActivity.this, MainActivity.class);

            overridePendingTransition(0, 0);
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    }
    @Override
    public void onDestroy(){
        if(!isRobotTest) {
            if (gameEngine!=null&&gameEngine.startGameClass.gameField != null && gameEngine.startGameClass.gameField.myRef != null) {
                gameEngine.startGameClass.gameField.deleteListener();
            }
        }
        gameEngine = null;
        super.onDestroy();
    }
}
