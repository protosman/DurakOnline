package ru.tmkstd.cardgamedurakonline;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Shader;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.res.ResourcesCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class GameEngine extends SurfaceView implements Runnable {
    final int sizeX,sizeY;
    Matrix matrix;
    SurfaceHolder gameHolder;
    Canvas canvas;
    Bitmap groundFon, badConnect, myAvatar, topPanel, numFon, numCardImage, modGameImage
//            endKoloda
                    ;
    Paint paint, paintCoinCounter,paintAddCoinCounter,paintLenKoloda,paintTimer, paintTimerRed, paintFirstTimer,paintWinner,paintLosing;
    Paint mainBitmapPaint, secondBitmapPaint;
    Paint graySmilesPaint;
    boolean startGame = false;
    boolean resetListenersProverk = false;
    Thread gameThread = null;
    volatile boolean playing;
    boolean avatarClick = false;
    boolean emojiClick;
    int numEmojiClick;
    int animEmojiClick;
    int groupEmoji;
    int showEmoji;
    boolean paused = false;
    short fps;
    private long timeThisFrame;
    int touchX, touchY,untouchX,untouchY;
    //    String name;
    int numCards;
    boolean modGame;
    Card tempCard;
    Context context;
    FirebaseUser user;
    StorageReference storageRef;
    Handler handler;

    Path pathText;
    Paint paintText;

    boolean timerSecBool=true;
//    boolean proverkaOnline = true;
    boolean friendGame = false;
    long timerSecLong = 0;


    FindNewGame findNewGame;
    StartGame startGameClass;
    DownloadAllRes downloadAllRes;
    int colorField;
    boolean fingGameLogo;

    int widthTopPanel;
//    Rect mTextBoundRect;

    public GameEngine(Context context, int sizeDispX, int sizeDispY, int color,boolean mod,int num, DownloadAllRes res, float sizeWidthCoinCounter) {
        super(context);
        numEmojiClick = -1;
        animEmojiClick = 0;
        groupEmoji = 0;
        showEmoji = 0;
        handler = new Handler(Looper.getMainLooper());
        this.context = context;
        gameHolder = getHolder();
        modGame = mod;
        numCards = num;
        sizeY = sizeDispY;
        sizeX = sizeDispX;
        emojiClick = false;
        widthTopPanel = (int)sizeWidthCoinCounter;
//        sizeY = 600;
//        sizeX = 600;
        gameHolder.setFixedSize(sizeX,sizeY);

        matrix = new Matrix();
        mainBitmapPaint = new Paint();
        mainBitmapPaint.setAntiAlias(true);
        mainBitmapPaint.setFilterBitmap(true);
//        mainBitmapPaint.setShadowLayer(50,50,50,0xFF000000);
//        mainBitmapPaint.setDither(true);

        secondBitmapPaint = new Paint();
        secondBitmapPaint.setAntiAlias(true);
        secondBitmapPaint.setFilterBitmap(true);
//        secondBitmapPaint.setDither(true);

        paintWinner = new Paint();
        paintWinner.setColorFilter(new ColorMatrixColorFilter(new ColorMatrix(new float[] {
                0,0,0,0,0,
                0.6f,0,0,0,0,
                0,0,0,0,0,
                0,0,0,1,0
        })));
        paintLosing = new Paint();
        paintLosing.setColorFilter(new ColorMatrixColorFilter(new ColorMatrix(new float[] {
                0.7f,0,0,0,0,
                0,0,0,0,0,
                0,0,0,0,0,
                0,0,0,1,0
        })));
        graySmilesPaint = new Paint();
        graySmilesPaint.setColorFilter(new ColorMatrixColorFilter(new ColorMatrix(new float[]{
                0.3f,0.59f,0.11f,0,0,
                0.3f,0.59f,0.11f,0,0,
                0.3f,0.59f,0.11f,0,0,
                0,0,0,0.5f,0
        })));
//        graySmilesPaint.setAntiAlias(true);
//        graySmilesPaint.setFilterBitmap(true);
        paint = new Paint();
        paint.setColor(Color.argb(255, 0, 0, 0));
        paint.setTextSize(30);
        paint.setTypeface(ResourcesCompat.getFont(context, R.font.balsamiq_sans_regular));
//        paint.setAntiAlias(true);

        paintCoinCounter = new Paint();
//        paintCoinCounter.setTextSize(sizeX/15);
        paintCoinCounter.setTextSize(widthTopPanel*30/135);//оригинал размера
        paintCoinCounter.setTypeface(ResourcesCompat.getFont(context, R.font.balsamiq_sans_regular));
        paintCoinCounter.setShader(new LinearGradient(0,widthTopPanel*30/135/4,0,widthTopPanel*30/135-widthTopPanel*30/1350,  Color.rgb(236,240,45),Color.rgb(204,97,24), Shader.TileMode.CLAMP));

        paintCoinCounter.setAntiAlias(true);

        paintAddCoinCounter = new Paint();
        paintAddCoinCounter.setTextSize(sizeX/15);
        paintAddCoinCounter.setColor(Color.argb(255,204,97,24));
        paintAddCoinCounter.setTypeface(ResourcesCompat.getFont(context, R.font.balsamiq_sans_regular));
//        paintAddCoinCounter.setAntiAlias(true);

        paintLenKoloda = new Paint();
        paintLenKoloda.setTextSize(sizeX/15);
        paintLenKoloda.setTypeface(ResourcesCompat.getFont(context, R.font.balsamiq_sans_regular));
//        paintLenKoloda.setAntiAlias(true);
        paintTimer = new Paint();
        paintTimer.setTextSize(sizeX/10);
        paintTimer.setTypeface(ResourcesCompat.getFont(context, R.font.balsamiq_sans_regular));
//        paintTimer.setAntiAlias(true);
        paintTimerRed = new Paint();
        paintTimerRed.setTextSize(sizeX/10);
        paintTimerRed.setColor(Color.argb(255,255,0,0));
        paintTimerRed.setTypeface(ResourcesCompat.getFont(context, R.font.balsamiq_sans_regular));
//        paintTimerRed.setAntiAlias(true);
        colorField=color;
        switch (colorField){
            case 1:groundFon = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(
                    context.getResources(), R.drawable.fon_two),
                    sizeX, sizeY,false); break;
            case 2:groundFon = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(
                    context.getResources(), R.drawable.fon_three),
                    sizeX, sizeY,true); break;
            case 3:groundFon = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(
                    context.getResources(), R.drawable.fon_four),
                    sizeX, sizeY,false); break;
            default:groundFon = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(
                    context.getResources(), R.drawable.fon_one),
                    sizeX, sizeY,false); break;
        }

        if(numCards==24){
            numCardImage = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(
                    context.getResources(), R.drawable.num_one),
                    sizeY/20, sizeY/20,false);
        }
        else if(numCards == 36){
            numCardImage = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(
                    context.getResources(), R.drawable.num_two),
                    sizeY/20, sizeY/20,false);
        }
        else{
            numCardImage = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(
                    context.getResources(), R.drawable.num_three),
                    sizeY/20, sizeY/20,false);
        }
        if(modGame){
            modGameImage = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(
                    context.getResources(), R.drawable.mod_one),
                    sizeY/20, sizeY/20,false);
        }
        else {
            modGameImage = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(
                    context.getResources(), R.drawable.mod_two),
                    sizeY/20, sizeY/20,false);
        }

        topPanel = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(
                context.getResources(), R.drawable.btn_menu_count_coin),
                widthTopPanel , widthTopPanel/3,false);
        badConnect = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(
                context.getResources(), R.drawable.bad_internet_connect),
                sizeX, sizeX/6,false);
        numFon = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(
                context.getResources(), R.drawable.num_fon),
                sizeX/9, sizeX/9,false);

        storageRef = FirebaseStorage.getInstance().getReference();
        user = FirebaseAuth.getInstance().getCurrentUser();
        storageRef.child(user.getUid()).getBytes(1024*1024).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {

                myAvatar = Bitmap.createScaledBitmap(BitmapFactory.decodeByteArray(bytes,0,bytes.length),
                        sizeY/9, sizeY/9,true);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
            }
        });

        downloadAllRes = res;
        groundFon = unionBitmap(groundFon,null).copy(Bitmap.Config.RGB_565,false);
        downloadAllRes.resetPosition();
        findNewGame = new FindNewGame(context, sizeDispX, sizeDispY, mod, num);
        startGameClass = new StartGame(mod, num, downloadAllRes.rubashka,downloadAllRes.imageCard, context);
        fingGameLogo = true;
        pathText = new Path();
        pathText.reset();
        pathText.moveTo((sizeX-downloadAllRes.grandPanelGameField.getWidth())/2,(sizeY - downloadAllRes.grandPanelGameField.getHeight())/2);
        pathText.quadTo(sizeX/2,sizeY/2 -downloadAllRes.grandPanelGameField.getHeight()/2-downloadAllRes.grandPanelGameField.getHeight()/7 ,(sizeX+downloadAllRes.grandPanelGameField.getWidth())/2,(sizeY - downloadAllRes.grandPanelGameField.getHeight())/2);
        paintText = new Paint();
        paintText.setTextSize(downloadAllRes.grandFlame.getHeight()*10/33);
        paintText.setTypeface(ResourcesCompat.getFont(context, R.font.balsamiq_sans_regular));
        paintText.setAntiAlias(true);
        paintFirstTimer = new Paint();
        paintFirstTimer.setTextSize(downloadAllRes.resetBtn.getHeight()/3);
        paintFirstTimer.setTypeface(ResourcesCompat.getFont(context, R.font.balsamiq_sans_regular));

    }
    private Bitmap unionBitmap(Bitmap i, Bitmap j) {
        Canvas unionCanvas = new Canvas(i);
        unionCanvas.drawBitmap(numCardImage,sizeX - topPanel.getWidth(),topPanel.getHeight(),null);
        unionCanvas.drawBitmap(modGameImage,sizeX - topPanel.getWidth()  + numCardImage.getHeight()+ sizeX/50,topPanel.getHeight(),null);
        unionCanvas.drawBitmap(topPanel,sizeX - topPanel.getWidth(),0,null);
        numCardImage.recycle();
        modGameImage.recycle();
        topPanel.recycle();
        return i;
    }


    protected void reopenGame(boolean GM,String Oppon, boolean hodit, boolean otbivaet, String myCards, String oldMyCards,String hoditCard, String otbivaetCard, int koziri, int timer) {
        startGame = true;
        findNewGame.reOpenGame(GM,Oppon);
        startGameClass.reOpenGame(GM,Oppon,findNewGame.user.getUid(),sizeX,sizeY,hodit,otbivaet,myCards, oldMyCards,hoditCard,otbivaetCard, koziri,timer);
    }
    protected void openNewGame(){
        findNewGame.findGame();
    }
    protected void openFriendGame (String opponentId,boolean gm) {
        friendGame = true;
        findNewGame.openFriendGame(opponentId, gm);
    }
    @Override
    public void run() {
        long startFrameTime=0;
//        int i=0;
//        int maxms=0;
        while (playing) {
            startFrameTime = System.currentTimeMillis();
            if(!paused) {
                update();
            }
            try {
                draw();
            } catch (Exception e) {

            }
//            tempTime = System.currentTimeMillis();
//            if(tempTime-startFrameTime<34)
//                try {
//                    Log.d("testMatrix","THRRREEEEEEAD");
//                    Thread.sleep(33-(tempTime-startFrameTime));
//                } catch (Exception e) {
//
//                }

            timeThisFrame = System.currentTimeMillis() - startFrameTime;
//            if(maxms<timeThisFrame) {
//                maxms = timeThisFrame;
//                Log.d("testFastRun",String.valueOf(maxms));
//            }
            if(timeThisFrame > 0) {
                fps = (short) (1000 / timeThisFrame);
//                maxms = maxms + fps;
//                i++;
//                Log.d("testMatrix",String.valueOf(fps));
            }
//            if(i>30) {
//
////                fps = (short) (1000 / timeThisFrame);
//                Log.d("testMatrix",String.valueOf(maxms/i));
//                maxms = 0;
//                i = 0;
//            }

        }

    }
    public void draw() throws NullPointerException{
        if(gameHolder.getSurface().isValid()) {
            canvas = gameHolder.lockCanvas();
            if (downloadAllRes != null) {
                if(groundFon!=null) {
                    canvas.drawBitmap(groundFon,0,0,null);
                }
                canvas.drawBitmap(downloadAllRes.btnExit.getBitmap(),downloadAllRes.btnExit.getPosX(),downloadAllRes.btnExit.getPosY(),null);
                canvas.drawBitmap(downloadAllRes.btnFlag.getBitmap(),downloadAllRes.btnFlag.getPosX(),downloadAllRes.btnFlag.getPosY(),null);
                if(findNewGame.coinAddAnim >0){
                    if (findNewGame.coinAddAnim>15){
                        canvas.drawBitmap(downloadAllRes.coinAdd,sizeX - sizeX/15,sizeX/10,null);
                            canvas.drawText(findNewGame.tempCoinSt, sizeX - sizeX / 15 - paintAddCoinCounter.measureText(findNewGame.tempCoinSt), sizeX / 10 + sizeX / 15 - sizeX / 15 / 5, paintAddCoinCounter);
//
                    } else {
                        canvas.drawBitmap(downloadAllRes.coinAdd,sizeX - sizeX/15,sizeX/10 - sizeX/10/15*(15-findNewGame.coinAddAnim),null);
                        canvas.drawText(findNewGame.tempCoinSt, sizeX - sizeX / 15 - paintAddCoinCounter.measureText(findNewGame.tempCoinSt), sizeX / 10 + sizeX / 15 - sizeX / 15 / 5 - sizeX / 10 / 15 * (15 - findNewGame.coinAddAnim), paintAddCoinCounter);

                    }
                    if (findNewGame.coinAddAnim==1) {
                        findNewGame.coin = findNewGame.coin + findNewGame.tempCoin;
                        Log.d("testCoin",""+findNewGame.coin);
                    }
                    findNewGame.coinAddAnim--;
                }
//                canvas.drawBitmap(downloadAllRes.coin,sizeX-sizeX/10,0,null);
                if(startGameClass.gameField!= null &&startGameClass.gameField.startGame) {
                    switch (startGameClass.gameField.koziri) {
                        case 0:canvas.drawBitmap(downloadAllRes.mastBubi,sizeX-downloadAllRes.mastBubi.getWidth(),downloadAllRes.btnExit.getHeight()/2+widthTopPanel/3+downloadAllRes.rubashka[52].height/2-downloadAllRes.rubashka[52].width/2,null);break;
                        case 1:canvas.drawBitmap(downloadAllRes.mastTrefy,sizeX-downloadAllRes.mastBubi.getWidth(),downloadAllRes.btnExit.getHeight()/2+widthTopPanel/3+downloadAllRes.rubashka[52].height/2-downloadAllRes.rubashka[52].width/2,null);break;
                        case 2:canvas.drawBitmap(downloadAllRes.mastChervi,sizeX-downloadAllRes.mastBubi.getWidth(),downloadAllRes.btnExit.getHeight()/2+widthTopPanel/3+downloadAllRes.rubashka[52].height/2-downloadAllRes.rubashka[52].width/2,null);break;
                        case 3:canvas.drawBitmap(downloadAllRes.mastVini,sizeX-downloadAllRes.mastBubi.getWidth(),downloadAllRes.btnExit.getHeight()/2+widthTopPanel/3+downloadAllRes.rubashka[52].height/2-downloadAllRes.rubashka[52].width/2,null);break;
                        default:break;
                    }

                    if(startGameClass.gameField.kolodaRandom!=null) {
                        if (startGameClass.gameField.kolodaRandom.length()!=0) {
                            if(startGameClass.endKoloda != null) {
                                canvas.drawBitmap(
                                        startGameClass.endKoloda,
                                        sizeX - startGameClass.endKoloda.getWidth()/2,
                                        downloadAllRes.btnExit.getHeight()/2+widthTopPanel/3+downloadAllRes.rubashka[52].height/2-startGameClass.endKoloda.getHeight()/2,
                                        null);
                            }

                            if (startGameClass.gameField.kolodaRandom.length()>1){
                                canvas.drawBitmap(downloadAllRes.rubashka[52].normal,
                                        sizeX-downloadAllRes.rubashka[52].width/4,
                                        downloadAllRes.btnExit.getHeight()/2+widthTopPanel/3,
                                        null);
                                canvas.drawBitmap(numFon,
                                        sizeX-downloadAllRes.rubashka[52].width/4- sizeX/9/2,
                                        downloadAllRes.btnExit.getHeight()/2+widthTopPanel/3 +downloadAllRes.rubashka[52].height +sizeX/15 -(sizeX/15 - sizeX/15/3)/2 - sizeX/9/2,
                                        null);
                                canvas.drawText(String.valueOf(startGameClass.gameField.kolodaRandom.length()),
                                        sizeX - downloadAllRes.rubashka[52].width/4-paintLenKoloda.measureText(String.valueOf(startGameClass.gameField.kolodaRandom.length()))/2 ,
                                        downloadAllRes.btnExit.getHeight()/2+widthTopPanel/3 +downloadAllRes.rubashka[52].height +sizeX/15,
                                        paintLenKoloda);

                            }

                        }
                    }

                    if (startGameClass.gameField.myCardsImg != null) {
                        if (startGameClass.gameField.hoditCard.length()==0 ){
                            for(int i = 0; i<6;i++){
                                canvas.drawBitmap(downloadAllRes.whiteRamka,
                                        startGameClass.gameField.stol[0][i]+downloadAllRes.sizeCard/2-downloadAllRes.whiteRamka.getWidth()/2,
                                        startGameClass.gameField.stol[1][i]+downloadAllRes.imageCard[0].getHeight()/2-downloadAllRes.whiteRamka.getHeight()/2,
                                        null);
                            }
                        } else if (startGameClass.gameField.otbivaetCard.length()==0 && modGame &&startGameClass.gameField.otbivaet){
                            if(startGameClass.gameField.lenOpponent>startGameClass.gameField.hoditCard.length()&&startGameClass.gameField.hoditCard.length()<6) {
                                canvas.drawBitmap(downloadAllRes.whiteRamkaPerevod,
                                        startGameClass.gameField.stol[0][startGameClass.gameField.hoditCard.length()] + downloadAllRes.sizeCard / 2 - downloadAllRes.whiteRamka.getWidth() / 2,
                                        startGameClass.gameField.stol[1][startGameClass.gameField.hoditCard.length()] + downloadAllRes.imageCard[0].getHeight() / 2 - downloadAllRes.whiteRamka.getHeight() / 2,
                                        null);
                            }
                        }
                        for(int i = 0; i < 6; i++){
                            if(startGameClass.gameField.stolImageCards[i] != null) {
                                //Защита от NULL во время изменения;
                                tempCard = startGameClass.gameField.stolImageCards[i];
                                tempCard.update(fps);
                                matrix.reset();
                                matrix.setTranslate(tempCard.getPosX(), tempCard.getPosY());
                                matrix.postScale(0.6f,0.6f,
                                        tempCard.getCenterX(),
                                        tempCard.getCenterY());
                                matrix.postRotate(-3,
                                        tempCard.getCenterX(),
                                        tempCard.getCenterY());
                                canvas.drawBitmap(tempCard.getBitmap(),
                                        matrix,
                                        secondBitmapPaint);
                                if(tempCard.getPosX()<0-tempCard.getWidth()){
                                    startGameClass.gameField.stolImageCards[i].setPosBtnX(sizeX-startGameClass.gameField.stolImageCards[i].getHeight()/2);
                                    startGameClass.gameField.stolImageCards[i].setPosBtnY(downloadAllRes.sizeCard);
                                    startGameClass.gameField.stolImageCards[i] = null;
                                }
                                if(tempCard.getPosY()<0-tempCard.getHeight()){
                                    startGameClass.gameField.stolImageCards[i].setPosBtnX(sizeX-startGameClass.gameField.stolImageCards[i].getHeight()/2);
                                    startGameClass.gameField.stolImageCards[i].setPosBtnY(downloadAllRes.sizeCard);
                                    startGameClass.gameField.stolImageCards[i] = null;
                                }
                                if (tempCard.getPosY()>sizeY-tempCard.getHeight()*1.1){
                                    startGameClass.gameField.stolImageCards[i] = null;
                                }
                            }
                            if(startGameClass.gameField.stolImageCards[i+6] != null) {
                                //Защита от NULL во время изменения;
                                tempCard = startGameClass.gameField.stolImageCards[i+6];

                                tempCard.update(fps);
                                matrix.reset();
                                matrix.setTranslate(tempCard.getPosX(), tempCard.getPosY());
                                matrix.postScale(0.6f,0.6f,
                                        tempCard.getCenterX(),
                                        tempCard.getCenterY());
                                matrix.postRotate(5,
                                        tempCard.getCenterX(),
                                        tempCard.getCenterY());
                                canvas.drawBitmap(tempCard.getBitmap(),
                                        matrix,
                                        secondBitmapPaint);
                                if(tempCard.getPosX()<0-tempCard.getWidth()){
                                    startGameClass.gameField.stolImageCards[i+6].setPosBtnX(sizeX-startGameClass.gameField.stolImageCards[i+6].getHeight()/2);
                                    startGameClass.gameField.stolImageCards[i+6].setPosBtnY(downloadAllRes.sizeCard);
                                    startGameClass.gameField.stolImageCards[i+6] = null;
                                }
                                if(tempCard.getPosY()<0-tempCard.getHeight()){
                                    startGameClass.gameField.stolImageCards[i+6].setPosBtnX(sizeX-startGameClass.gameField.stolImageCards[i+6].getHeight()/2);
                                    startGameClass.gameField.stolImageCards[i+6].setPosBtnY(downloadAllRes.sizeCard);
                                    startGameClass.gameField.stolImageCards[i+6] = null;
                                }
                                if (tempCard.getPosY()>sizeY-tempCard.getHeight()*1.1){
                                    startGameClass.gameField.stolImageCards[i+6] = null;
                                }
                            }
                        }
                        if(startGameClass.gameField.myCardsImg.length>9) mainBitmapPaint.setFilterBitmap(false);
                        else mainBitmapPaint.setFilterBitmap(true);
                        for (int j = 0; j < startGameClass.gameField.myCardsImg.length; j++) {
                            if(startGameClass.gameField.myCardsImg!=null && j < startGameClass.gameField.myCardsImg.length && startGameClass.gameField.myCardsImg[j]!=null) {
                                //Защита от NULL во время изменения;
                                tempCard = startGameClass.gameField.myCardsImg[j];
                                tempCard.update(fps);
                                matrix.reset();
                                if(tempCard.getPosY()<sizeY-tempCard.getHeight()-1) {
                                    matrix.setScale(
                                            (float) (tempCard.getPosY() + (sizeY - tempCard.getHeight())) / ((sizeY - tempCard.getHeight()) * 2),
                                            (float) (tempCard.getPosY() + (sizeY - tempCard.getHeight())) / ((sizeY - tempCard.getHeight()) * 2),
                                            tempCard.getWidth()/2,
                                            tempCard.getHeight()/2);
                                }

                                matrix.postTranslate(tempCard.getPosX(),tempCard.getPosY());

                                if(tempCard.getPosY()>sizeY-sizeY*2/9-sizeY/17) {
                                    matrix.postRotate((14 * ((tempCard.getPosX() + tempCard.getWidth() / 2) / (float) sizeX) - 7)*((tempCard.getPosY() - (sizeY-sizeY*2/9 -sizeY/17))/((float)sizeY/17)),
                                            tempCard.getWidth() / 2+sizeX / 2 + sizeX/30,
                                            sizeY);
                                    canvas.drawBitmap(tempCard.getBitmap(),
                                            matrix,
                                            mainBitmapPaint);
                                }
                                else {
                                    canvas.drawBitmap(tempCard.getBitmap(),
                                            matrix,
                                            null);
                                }
                            }
                        }


                        for (int i = 0; i < startGameClass.gameField.lenOpponent;i++){
                            downloadAllRes.rubashka[i].update(fps);
                            matrix.reset();
                            matrix.setTranslate(
                                    downloadAllRes.rubashka[i].getPosX(),
                                    downloadAllRes.rubashka[i].getPosY());
                            if(downloadAllRes.rubashka[i].getPosX()<(sizeY/9-downloadAllRes.rubashka[i].getWidth())) {
                                matrix.postRotate(0 - 40 * ((float) (downloadAllRes.rubashka[i].getPosX()) / (sizeY / 9 - downloadAllRes.rubashka[i].getWidth())),
                                        sizeY / 18,
                                        downloadAllRes.rubashka[i].getPosY());
                                //раньше 15 - 30ж
                            }
                            else {
                                matrix.postRotate(-45,
                                        downloadAllRes.rubashka[i].getPosX(),
                                        downloadAllRes.rubashka[i].getPosY());

                            }
                            canvas.drawBitmap(downloadAllRes.rubashka[0].getBitmap(),
                                    matrix,
                                    null);
                        }

                        if (startGameClass.gameField.youWin) {
                            if(startGameClass.gameField.flagChatAnim>0){
                                startGameClass.gameField.flagChatAnim--;
                                if(startGameClass.gameField.flagChatAnim>15){
                                    canvas.drawBitmap(downloadAllRes.flag,(sizeX - downloadAllRes.flag.getWidth())/2,sizeY/9,null);
                                } else {
                                    canvas.drawBitmap(downloadAllRes.flag,(sizeX - downloadAllRes.flag.getWidth())/2,sizeY/9-(sizeY/5/15)*(15-startGameClass.gameField.flagChatAnim),null);
                                }
                            }
                        }
                        if (startGameClass.gameField.youLose) {
                            if(startGameClass.gameField.flagChatAnim>0){
                                startGameClass.gameField.flagChatAnim--;
                                if(startGameClass.gameField.flagChatAnim>15) {
                                    canvas.drawBitmap(downloadAllRes.flag, (sizeX - downloadAllRes.flag.getWidth()) / 2, sizeY - downloadAllRes.imageCard[0].getHeight() - downloadAllRes.flag.getHeight(), null);
                                } else {
                                    canvas.drawBitmap(downloadAllRes.flag, (sizeX - downloadAllRes.flag.getWidth()) / 2, sizeY - (downloadAllRes.imageCard[0].getHeight() + downloadAllRes.flag.getHeight())/15*startGameClass.gameField.flagChatAnim, null);
                                }
                            }
                        }


                        if(timerSecBool && startGameClass.gameField.game){
                            timerSecBool = false;
                            timerSecLong = System.currentTimeMillis();
                            if(startGameClass.gameField.timer>25) resetListenersProverk = true;
                            if(resetListenersProverk && startGameClass.gameField.timer==10){
                                resetListenersProverk = false;
                                startGameClass.gameField.resetListeners();
                            }
                        }
                        if (System.currentTimeMillis()-timerSecLong>999 && startGameClass.gameField.game) {
                            timerSecBool = true;
                            startGameClass.gameField.timer--;
                            if (startGameClass.gameField.hodit && !startGameClass.gameField.otbivaet) {
                                if(startGameClass.gameField.timer > 14){
                                    if (startGameClass.gameField.timer%5==0){
                                        startGameClass.gameField.myRef.child("timer").setValue(startGameClass.gameField.timer);
                                    }
                                }
                                else if(startGameClass.gameField.timer > 5) {
                                    if (startGameClass.gameField.timer%2==1){
                                        startGameClass.gameField.myRef.child("timer").setValue(startGameClass.gameField.timer);
                                    }
                                } else {
                                    startGameClass.gameField.myRef.child("timer").setValue(startGameClass.gameField.timer);
                                }
                                if ((startGameClass.gameField.hoditCard.length() == 0 || startGameClass.gameField.bitoBool || startGameClass.gameField.beryOpponent)
                                && !startGameClass.gameField.pressPassBool) {
                                    if (startGameClass.gameField.timer < 1) {
                                        startGameClass.gameField.game = false;
                                        if(!startGameClass.gameField.connectInternet) {
                                            exitActivity();
                                        }
                                    }
                                }
                                else {
                                    if (startGameClass.gameField.timer < 1) {
                                        if(startGameClass.gameField.connectInternet) {
//                                            startGameClass.gameField.seeGameEnd();
                                            startGameClass.gameField.setWinMe();
                                        }
                                        else {
                                            exitActivity();
                                        }
                                        startGameClass.gameField.game = false;
                                    }
                                }
                            }
                            if (startGameClass.gameField.otbivaet && !startGameClass.gameField.hodit) {
                                if(startGameClass.gameField.timer > 14){
                                    if (startGameClass.gameField.timer%5==0){
                                        startGameClass.gameField.myRef.child("timer").setValue(startGameClass.gameField.timer);
                                    }
                                }
                                else if(startGameClass.gameField.timer > 5) {
                                    if (startGameClass.gameField.timer%2==1){
                                        startGameClass.gameField.myRef.child("timer").setValue(startGameClass.gameField.timer);
                                    }
                                }
                                else {
                                    startGameClass.gameField.myRef.child("timer").setValue(startGameClass.gameField.timer);
                                }
                                if (startGameClass.gameField.hoditCard.length() == 0 || startGameClass.gameField.bitoBool || startGameClass.gameField.beryyyyy) {
                                    if (startGameClass.gameField.timer < 1) {
                                        if(startGameClass.gameField.connectInternet) {
//                                            startGameClass.gameField.seeGameEnd();
                                            startGameClass.gameField.setWinMe();
                                        }
                                        else {
                                            exitActivity();
                                        }
                                        startGameClass.gameField.game = false;
                                    }
                                } else {
                                    if (startGameClass.gameField.timer < 1) {
                                        startGameClass.gameField.game = false;
                                        if(!startGameClass.gameField.connectInternet) {
                                            exitActivity();
                                        }
                                    }
                                }
                            }
                        }

                        if(!startGameClass.gameField.connectInternet){
                            canvas.drawBitmap(badConnect, 0, (sizeY-badConnect.getHeight() - sizeY/9)/2, null);
                        }
                    }
                }
                if(findNewGame.myNameBitmap!= null) {
                    canvas.drawBitmap(findNewGame.myNameBitmap,sizeY/9,sizeY - findNewGame.myNameBitmap.getHeight(),null);
                }
                if(myAvatar!=null){
                    canvas.drawBitmap(myAvatar,0,sizeY-myAvatar.getHeight(),null);
                } else {
                    canvas.drawBitmap(downloadAllRes.avatar,0,sizeY-downloadAllRes.avatar.getHeight(),null);
                }
                if((findNewGame.firstTimerBool && !findNewGame.startGameBool) || (startGameClass.gameField!=null && startGameClass.gameField.endGameTimerBool) ){

                    canvas.drawBitmap(downloadAllRes.grandPanelGameField,
                            (sizeX-downloadAllRes.grandPanelGameField.getWidth())/2,
                            (sizeY - downloadAllRes.grandPanelGameField.getHeight())/2,
                            null);
                    if(findNewGame.pressStart) {
                        canvas.drawBitmap(downloadAllRes.wait_oppon_pass,
                                (sizeX-downloadAllRes.wait_oppon_pass.getWidth())/2,
                                sizeY/2 + downloadAllRes.wait_oppon_pass.getHeight()/2,
                                null);
                    }
                    canvas.drawBitmap(downloadAllRes.resetBtn.getBitmap(),
                            downloadAllRes.resetBtn.getPosX(),
                            downloadAllRes.resetBtn.getPosY(),
                            null);
                }
                // Сделал =true  здесь в Update()
//                        Функция когда кто-то Win или Lose Вкулючается счётчик. Если не нажал то выкидывает, а если нажал то новый поиск
                //Так надо из-за последовательности отрисовки
                // startGameBool надо для того чтобы избежать двойной отрисовки таймера.
                if(startGameClass.gameField!=null && startGameClass.gameField.endGameTimerBool && findNewGame.startGameBool){
                    if(startGameClass.gameField.youWin) {
                        canvas.drawBitmap(downloadAllRes.grandFlame,
                                (sizeX - downloadAllRes.grandFlame.getWidth()) / 2,
                                (sizeY - downloadAllRes.grandPanelGameField.getHeight() - downloadAllRes.grandFlame.getHeight()) / 2,
                                paintWinner);
                        canvas.drawTextOnPath("Вы победили",pathText,
//                                0,
                                (sizeX-paintText.measureText("Вы победили"))/2 - (sizeX - downloadAllRes.grandPanelGameField.getWidth()) / 2,
                                0,paintText);
                    }
                    if(startGameClass.gameField.youLose) {
                        canvas.drawBitmap(downloadAllRes.grandFlame,
                                (sizeX - downloadAllRes.grandFlame.getWidth()) / 2,
                                (sizeY - downloadAllRes.grandPanelGameField.getHeight() - downloadAllRes.grandFlame.getHeight()) / 2,
                                paintLosing);
//                        canvas.drawPath(pathText,paintText);
                        canvas.drawTextOnPath("Вы проиграли",pathText,
//                                0,
                                (sizeX-paintText.measureText("Вы проиграли"))/2 - (sizeX - downloadAllRes.grandPanelGameField.getWidth()) / 2,
                                0,paintText);
                    }
                    if(startGameClass.gameField.nichia) {
                        canvas.drawBitmap(downloadAllRes.grandFlame,
                                (sizeX - downloadAllRes.grandFlame.getWidth()) / 2,
                                (sizeY - downloadAllRes.grandPanelGameField.getHeight() - downloadAllRes.grandFlame.getHeight()) / 2,
                                null);
                        canvas.drawTextOnPath("Ничья",pathText,
//                                0,
                                (sizeX-paintText.measureText("Ничья"))/2 - (sizeX - downloadAllRes.grandPanelGameField.getWidth()) / 2,
                                0,paintText);
                    }
                    canvas.drawText("Выход через: "+String.valueOf(startGameClass.gameField.endGameTimer + 20 - System.currentTimeMillis()/1000),(sizeX-paintFirstTimer.measureText("Выход через: 20"))/2, sizeY/2-downloadAllRes.grandPanelGameField.getHeight()/5, paintFirstTimer);
                    if (startGameClass.gameField.endGameTimer + 20 - System.currentTimeMillis()/1000 < 1) {
                        if (!findNewGame.pressStart) {
                            findNewGame.exitFind();
                            startGameClass.gameField.deleteListener();
                            startGameClass.deleteListener();
                            Intent intent = new Intent(context, MainActivity.class);

                            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                            context.startActivity(intent);
                        }
                        else { // этого раньше не было, Дописываю !код 23:45
                            if(findNewGame.coin>9){
                                if(friendGame){
                                    findNewGame.exitFind();
                                    startGameClass.gameField.deleteListener();
                                    startGameClass.deleteListener();
                                    Intent intent = new Intent(context, MainActivity.class);

                                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                    context.startActivity(intent);

                                }
                                else {
                                    startGameClass.gameField.endGameTimer = System.currentTimeMillis() / 1000;// надо обнулить результат
                                    startGame = false;
                                    findNewGame.exit();
                                    findNewGame.startGameBool = false;
                                    findNewGame.refindFun();
                                    startGameClass.gameField.startGame = false;
                                }
                            }
//                                    else {//не знаю просто убрал
//                                        findNewGame.exitFind();
//                                        startGameClass.gameField.deleteListener();
//                                        startGameClass.deleteListener();
//                                        Intent intent = new Intent(context, MainActivity.class);
//
//                                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
//                                        context.startActivity(intent);
//                                    }
                        }
                    }

                }
                if(findNewGame.firstTimerBool && !findNewGame.startGameBool){

                    canvas.drawText("Выход через: "+String.valueOf(findNewGame.firstTimer + 20 - System.currentTimeMillis()/1000),(sizeX-paintFirstTimer.measureText("Выход через: 20"))/2, sizeY/2-downloadAllRes.grandPanelGameField.getHeight()/5, paintFirstTimer);
                    if (findNewGame.firstTimer + 20 - System.currentTimeMillis()/1000 < 1) {
                        if (findNewGame.pressStart) {
                            findNewGame.refindFun();
                        }
                        else {
                            findNewGame.exitFind();
                            Intent intent = new Intent(context, MainActivity.class);

                            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                            context.startActivity(intent);
                        }
                    }
                }
                if(findNewGame.findGameBool
//                        || findNewGame.firstTimerBool // странно нужна ли эта проверка
                ) {
                    if(findNewGame.opponNameBitmap!=null){
                        if(startGameClass.gameField == null || !startGameClass.gameField.game) {
                            canvas.drawBitmap(findNewGame.opponNameBitmap,
                                    0,
                                    sizeY / 9,
                                    null);
                        }
                        else {
                            canvas.drawBitmap(findNewGame.opponNameBitmap,
                                    0,
                                    sizeY / 9 + downloadAllRes.rubashka[0].getHeight(),
                                    null);
                        }
                    }
                    if (findNewGame.avatarOpponent != null) {
                        canvas.drawBitmap(findNewGame.avatarOpponent,
                                0,
                                0,
                                null);
                    } else {
                        canvas.drawBitmap(downloadAllRes.avatar,
                                0,
                                0,
                                null);
                    }
                    if(findNewGame.opponNull){
                        canvas.drawBitmap(downloadAllRes.avatarTimer,
                                0,
                                0,
                                null);
                        canvas.drawBitmap(downloadAllRes.connection,0,sizeY/36,null);
                    }
                    // Мне паказалось так проще
                    if (findNewGame.pressStartAnim > 0){
                        canvas.drawBitmap(downloadAllRes.ok,0,0,null);
                        findNewGame.pressStartAnim--;
                    }
                    if (startGameClass!=null && startGameClass.pressStartAnim>0) {
                        canvas.drawBitmap(downloadAllRes.ok,0,0,null);
                        startGameClass.pressStartAnim--;
                    }
                }


                if (fingGameLogo){
                    canvas.drawBitmap(downloadAllRes.findGame,
                            0,
                            sizeY-downloadAllRes.findGame.getHeight()-downloadAllRes.sizeCardHeight,
                            null);
                }
                if (startGameClass.gameField!= null &&startGameClass.gameField.startGame) {
                    if(startGameClass.gameField.hodit && startGameClass.gameField.hoditCard.length()==0 && startGameClass.gameField.game &&
                            downloadAllRes.passBtn.getPosX()==sizeX){
                        canvas.drawBitmap(downloadAllRes.yourTurn,
                                (sizeX-downloadAllRes.yourTurn.getWidth())/2,
//                                startGameClass.gameField.stol[1][3],
//                                sizeY/2-downloadAllRes.yourTurn.getHeight(),
                                sizeY-downloadAllRes.sizeCardHeight-downloadAllRes.yourTurn.getHeight(),
                                null);
                    }
                    if(startGameClass.gameField.bitoChatAnim>0) {
                        canvas.drawBitmap(downloadAllRes.bitoChat,
                                (sizeX-downloadAllRes.bitoChat.getWidth())/2,
                                0,
                                null);
                        startGameClass.gameField.bitoChatAnim--;
                    }
                    if(startGameClass.gameField.passChatAnim>0) {
                        canvas.drawBitmap(downloadAllRes.passChat,
                                (sizeX-downloadAllRes.passChat.getWidth())/2,
                                0,
                                null);
                        startGameClass.gameField.passChatAnim--;
                    }
                    if(startGameClass.gameField.hodit && startGameClass.gameField.beryOpponent){
                        canvas.drawBitmap(downloadAllRes.beryChat,
                                (sizeX-downloadAllRes.beryChat.getWidth())/2,
                                0,
                                null);

                    }
                    // Пока так, но посмотрим дальше.
                    if(startGameClass.gameField.beryyyyy){
                        canvas.drawBitmap(downloadAllRes.beryBtn.pressed,
                                downloadAllRes.beryBtn.getPosX(),
                                downloadAllRes.beryBtn.getPosY(),
                                null);
                    }
                    else {
                        canvas.drawBitmap(downloadAllRes.beryBtn.normal,
                                downloadAllRes.beryBtn.getPosX(),
                                downloadAllRes.beryBtn.getPosY(),
                                null);
                    }

                    if (findNewGame.coin >=50) {
                        canvas.drawBitmap(downloadAllRes.returnCardBtn.getBitmap(),
                                downloadAllRes.returnCardBtn.getPosX(),
                                downloadAllRes.returnCardBtn.getPosY(),
                                null);
                    }
                    else {
                        //думаю вообще не рисовать
                        canvas.drawBitmap(downloadAllRes.returnCardBtn.pressed,
                                downloadAllRes.returnCardBtn.getPosX(),
                                downloadAllRes.returnCardBtn.getPosY(),
                                null);
                    }

                    canvas.drawBitmap(downloadAllRes.bitoBtn.getBitmap(),
                            downloadAllRes.bitoBtn.getPosX(),
                            downloadAllRes.bitoBtn.getPosY(),
                            null);
                    canvas.drawBitmap(
                            startGameClass.gameField.pressPassBool ? downloadAllRes.passBtn.pressed : downloadAllRes.passBtn.normal,
                            downloadAllRes.passBtn.getPosX(),
                            downloadAllRes.passBtn.getPosY(),
                            null);
                    if(startGameClass.gameField.game) {
                        if(!startGameClass.gameField.nextHod) {
                            canvas.drawBitmap(downloadAllRes.wait_oppon_pass,
                                    (sizeX-downloadAllRes.wait_oppon_pass.getWidth())/2,
                                    sizeY-downloadAllRes.sizeCardHeight - downloadAllRes.wait_oppon_pass.getHeight(),
                                    null);
                        }
                        if (startGameClass.gameField.hodit && !startGameClass.gameField.otbivaet) {
//                            if(startGameClass.gameField.pressPassBool) {// Изменение на более универсальное сверху
//                                canvas.drawBitmap(downloadAllRes.wait_oppon_pass,
//                                        (sizeX-downloadAllRes.wait_oppon_pass.getWidth())/2,
//                                        sizeY-downloadAllRes.sizeCardHeight - downloadAllRes.wait_oppon_pass.getHeight(),
//                                        null);
//                            }
                            if ((startGameClass.gameField.hoditCard.length() == 0 || startGameClass.gameField.bitoBool || startGameClass.gameField.beryOpponent )
                                    && !startGameClass.gameField.pressPassBool) {
//                                myTimer = startGameClass.gameField.timer;
                                canvas.drawBitmap(downloadAllRes.avatarTimer,
                                        0,
                                        sizeY - sizeY/9,
                                        null);
                                if (startGameClass.gameField.timer > 9) {
                                    canvas.drawText(String.valueOf(startGameClass.gameField.timer),
                                            (sizeY / 9 - paintTimer.measureText(String.valueOf(startGameClass.gameField.timer))) / 2,
                                            sizeY - sizeY / 9 / 2 + (sizeX / 10 - sizeX / 10 / 5) / 2,
                                            paintTimer);

                                } else {
                                    canvas.drawText(String.valueOf(startGameClass.gameField.timer),
                                            (sizeY / 9 - paintTimer.measureText(String.valueOf(startGameClass.gameField.timer))) / 2,
                                            sizeY - sizeY / 9 / 2 + (sizeX / 10 - sizeX / 10 / 5) / 2,
                                            paintTimerRed);

                                }
                            } else {
                                canvas.drawBitmap(downloadAllRes.avatarTimer,
                                        0,
                                        0,
                                        null);
                                if (startGameClass.gameField.timer > 9) {
                                    canvas.drawText(String.valueOf(startGameClass.gameField.timer),
                                            (sizeY / 9 - paintTimer.measureText(String.valueOf(startGameClass.gameField.timer))) / 2,
                                            sizeY/18 + (sizeX / 10 - sizeX / 10 / 5) / 2,
                                            paintTimer);
                                } else {
                                    canvas.drawText(String.valueOf(startGameClass.gameField.timer),
                                            (sizeY / 9 - paintTimer.measureText(String.valueOf(startGameClass.gameField.timer))) / 2,
                                            sizeY/18 + (sizeX / 10 - sizeX / 10 / 5) / 2,
                                            paintTimerRed);
                                }
                            }
                        }
                        if (startGameClass.gameField.otbivaet && !startGameClass.gameField.hodit) {
                            if (startGameClass.gameField.hoditCard.length() == 0 || startGameClass.gameField.bitoBool || startGameClass.gameField.beryyyyy) {

                                canvas.drawBitmap(downloadAllRes.avatarTimer,
                                        0,
                                        0,
                                        null);
                                if (startGameClass.gameField.timer > 9) {
                                    canvas.drawText(String.valueOf(startGameClass.gameField.timer),
                                            (sizeY / 9 - paintTimer.measureText(String.valueOf(startGameClass.gameField.timer))) / 2,
                                            sizeY/18 + (sizeX / 10 - sizeX / 10 / 5) / 2,
                                            paintTimer);
                                } else {
                                    canvas.drawText(String.valueOf(startGameClass.gameField.timer),
                                            (sizeY / 9 - paintTimer.measureText(String.valueOf(startGameClass.gameField.timer))) / 2,
                                            sizeY/18 + (sizeX / 10 - sizeX / 10 / 5) / 2,
                                            paintTimerRed);
                                }
                            } else {
//                                myTimer = startGameClass.gameField.timer;
                                canvas.drawBitmap(downloadAllRes.avatarTimer,
                                        0,
                                        sizeY - sizeY/9,
                                        null);
                                if (startGameClass.gameField.timer > 9) {
                                    canvas.drawText(String.valueOf(startGameClass.gameField.timer),
                                            (sizeY / 9 - paintTimer.measureText(String.valueOf(startGameClass.gameField.timer))) / 2,
                                            sizeY - sizeY / 9 / 2 + (sizeX / 10 - sizeX / 10 / 5) / 2,
                                            paintTimer);

                                } else {
                                    canvas.drawText(String.valueOf(startGameClass.gameField.timer),
                                            (sizeY / 9 - paintTimer.measureText(String.valueOf(startGameClass.gameField.timer))) / 2,
                                            sizeY - sizeY / 9 / 2 + (sizeX / 10 - sizeX / 10 / 5) / 2,
                                            paintTimerRed);

                                }
                            }
                        }
                    }
                }
                if (findNewGame.coin>-1) {
                    if (findNewGame.coin < 1000)
                        canvas.drawText(String.valueOf(findNewGame.coin), sizeX - widthTopPanel*7/24 - paintCoinCounter.measureText(String.valueOf(findNewGame.coin)), widthTopPanel*30/135-widthTopPanel*30/1350, paintCoinCounter); // y = sizeX/10-sizeX/10/5
                    else if (findNewGame.coin < 10000)
                        canvas.drawText((int) (findNewGame.coin / 1000) + "," + findNewGame.coin %1000/100 + findNewGame.coin%100/10 + "K", sizeX - widthTopPanel*7/24 - paintCoinCounter.measureText("9,99K"), widthTopPanel*30/135-widthTopPanel*30/1350, paintCoinCounter);
                    else if (findNewGame.coin < 100000)
                        canvas.drawText((int) (findNewGame.coin / 1000) + "," + findNewGame.coin %1000/100 + "K" , sizeX - widthTopPanel*7/24 - paintCoinCounter.measureText("99,9K"), widthTopPanel*30/135-widthTopPanel*30/1350, paintCoinCounter);
                    else if (findNewGame.coin < 1000000)
                        canvas.drawText((int) (findNewGame.coin / 1000) + "K", sizeX - widthTopPanel*7/24 - paintCoinCounter.measureText("999K"), widthTopPanel*30/135-widthTopPanel*30/1350, paintCoinCounter);
                    else if (findNewGame.coin < 10000000)
                        canvas.drawText((int) (findNewGame.coin / 1000000) + "," + findNewGame.coin %1000000/100000 + findNewGame.coin %100000/10000+ "KK", sizeX - widthTopPanel*7/24 - paintCoinCounter.measureText("9,99KK"), widthTopPanel*30/135-widthTopPanel*30/1350, paintCoinCounter);
                    else
                        canvas.drawText("Oops!", sizeX - widthTopPanel*7/24 - paintCoinCounter.measureText("Oops!"), widthTopPanel*30/135-widthTopPanel*30/1350, paintCoinCounter);
                }


//                if(numEmojiClick>-1 && animEmojiClick>0) {//дубль
//                    canvas.drawBitmap(downloadAllRes.emojis[0][numEmojiClick].normal,0,sizeY - sizeY/9,null);
//                    animEmojiClick--;
//                }
                if(startGameClass!=null){
                    if(findNewGame.startGameBool) {
                        if (emojiClick) {
                            for(Card mark:downloadAllRes.emojisMark){
                                canvas.drawBitmap(
                                        mark.normal,
                                        mark.getPosX(),
                                        mark.getPosY(),
                                        null
                                );
                            }
                            canvas.drawBitmap(downloadAllRes.grandPanelGameField,(sizeX-downloadAllRes.grandPanelGameField.getWidth())/2,(sizeY-downloadAllRes.grandPanelGameField.getHeight())/2,null);
                            canvas.save();
                            canvas.scale(0.8f,0.8f,sizeX/2,sizeY/2);
                            for (int i = 0; i < 6; i++) {
                                if(groupEmoji>0&&((startGameClass.smilesCounts&1<<(i+(groupEmoji-1)*6))!=(1<<(i+(groupEmoji-1)*6)))){
                                    canvas.drawBitmap(downloadAllRes.emojis[groupEmoji][i].normal, downloadAllRes.emojis[groupEmoji][i].getPosX(), downloadAllRes.emojis[groupEmoji][i].getPosY(), graySmilesPaint);
                                }
                                else {
                                    canvas.drawBitmap(downloadAllRes.emojis[groupEmoji][i].normal, downloadAllRes.emojis[groupEmoji][i].getPosX(), downloadAllRes.emojis[groupEmoji][i].getPosY(), mainBitmapPaint);
                                }
                            }
                            canvas.restore();
                        } else {
                            canvas.drawBitmap(downloadAllRes.emojis[0][0].normal, sizeX - sizeY / 9, sizeY - sizeY / 9, null);
                        }
                        if (numEmojiClick > -1 && animEmojiClick > 0) {
                            canvas.drawBitmap(downloadAllRes.emojis[showEmoji][numEmojiClick].normal, 0, sizeY - sizeY / 9, null);
                            animEmojiClick--;
                        }
                    }
                    if(startGameClass.animEmojiClick>0 && startGameClass.numEmojiClick>-1){
                        canvas.drawBitmap(downloadAllRes.emojis[0][startGameClass.numEmojiClick].normal,0,0,null);
                        startGameClass.animEmojiClick--;
                    }
                    if(findNewGame.opponentExit) {
                        canvas.drawBitmap(downloadAllRes.icon_exit,
//                                sizeY/18 - downloadAllRes.icon_exit.getHeight()/2,sizeY/9-downloadAllRes.icon_exit.getHeight(),
                                sizeY/36, sizeY/18,
                                null);
                    }
                }
                if(startGameClass.gameField!=null && startGameClass.gameField.game&&avatarClick) {
                    canvas.drawBitmap(downloadAllRes.grandPanelGameField,(sizeX-downloadAllRes.grandPanelGameField.getWidth())/2,(sizeY-downloadAllRes.grandPanelGameField.getHeight())/2,null);
                    if(findNewGame.avatarOpponentMenu != null) {
                        canvas.drawBitmap(findNewGame.avatarOpponentMenu,(sizeX-downloadAllRes.grandPanelGameField.getWidth())/2 +downloadAllRes.grandPanelGameField.getWidth()/10,(sizeY-downloadAllRes.grandPanelGameField.getHeight())/2 + downloadAllRes.grandPanelGameField.getHeight()/10,null);
                    }
                    else {
                        canvas.drawBitmap(downloadAllRes.avatarOpponMenu,(sizeX-downloadAllRes.grandPanelGameField.getWidth())/2 +downloadAllRes.grandPanelGameField.getWidth()/10,(sizeY-downloadAllRes.grandPanelGameField.getHeight())/2 + downloadAllRes.grandPanelGameField.getHeight()/10,null);
                    }
                    if (findNewGame.opponNameBitmap != null){
//                        canvas.drawText(findNewGame.nameOpponent,  sizeX/2-paintTimer.measureText(findNewGame.nameOpponent)/2 ,(sizeY-downloadAllRes.menuAddFriend.getWidth())/2 +sizeX/9, paintTimer);
                        canvas.drawBitmap(findNewGame.opponNameBitmap,(sizeX + downloadAllRes.grandPanelGameField.getWidth())/2-downloadAllRes.grandPanelGameField.getWidth()/10-findNewGame.opponNameBitmap.getWidth(),(sizeY-downloadAllRes.grandPanelGameField.getHeight())/2 + downloadAllRes.grandPanelGameField.getHeight()/10,null);
                    }
                    canvas.drawBitmap(downloadAllRes.addFriendBtn.getBitmap(),downloadAllRes.addFriendBtn.getPosX(),downloadAllRes.addFriendBtn.getPosY(),null);
                }

            }

//            canvas.drawText("FPS: " + fps, 20 ,40, paint);
            gameHolder.unlockCanvasAndPost(canvas);
        }

    }

    public void update() {
        if (startGameClass.gameField != null && startGameClass.gameField.startGame){
//            fingGameLogo = false;
            if (startGameClass.gameField.youWin || startGameClass.gameField.youLose || startGameClass.gameField.nichia){
//                Таймер и счёт будет в Draw()
                if(!startGameClass.gameField.endGameTimerBool) {
                    startGameClass.gameField.endGameTimerBool = true;
                    startGameClass.gameField.endGameTimer = System.currentTimeMillis()/1000;
                }
//                // изменяю !код 23:45
                if(findNewGame.opponentExit && findNewGame.coin>9
                && findNewGame.coinAddAnim == 0//попробую после анимации, если будет работать не коректно, то можно сделать таймер в Field
                ){
                    if(friendGame) {
                        findNewGame.exitFind();
                        startGameClass.gameField.deleteListener();
                        startGameClass.deleteListener();
                        Intent intent = new Intent(context, MainActivity.class);

                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        context.startActivity(intent);
                    }
                    else {
                        startGameClass.gameField.endGameTimer = System.currentTimeMillis() / 1000;// надо обнулить результат
                        startGameClass.gameField.endGameTimerBool = false;
                        startGame = false;
                        findNewGame.exit();
                        findNewGame.startGameBool = false;
                        findNewGame.refindFun();
                        startGameClass.gameField.startGame = false;
                    }
                }
                if(findNewGame.coin<10){
//                            Выход когда недостаточно монет
                    findNewGame.exitFind();
                    Intent intent = new Intent(context, MainActivity.class);
                    intent.putExtra("coin",0);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    context.startActivity(intent);
                }
            }
        }
        fingGameLogo = !findNewGame.findGameBool;
        if(!findNewGame.findGameBool){
            friendGame = false;
            findNewGame.pressStart = false;
            findNewGame.friendGame = false;
//            fingGameLogo =true;
            avatarClick = false;
            downloadAllRes.resetBtn.setPosBtnX(sizeX);
            downloadAllRes.addFriendBtn.setPosBtnX(sizeX);
//            downloadAllRes.addFriendBtn.setPosBtnY(sizeY);
        }
        else if(!findNewGame.startGameBool){
            if(!findNewGame.friendGame && !findNewGame.pressStart) {
                downloadAllRes.resetBtn.setPosBtnX((sizeX - downloadAllRes.resetBtn.getWidth()) / 2);
            }
            else {
                downloadAllRes.resetBtn.setPosBtnX(sizeX);
            }
        }
        if(startGameClass.gameField!=null&& startGameClass.gameField.game){
            findNewGame.pressStart = false;//обновление нажатия
            downloadAllRes.btnFlag.setPosBtnX(sizeY/9 + sizeY/36);
//            downloadAllRes.btnFlag.setPosBtnY(0);
            downloadAllRes.btnExit.setPosBtnX(sizeX);
        }
        else{
            downloadAllRes.btnFlag.setPosBtnX(sizeX);
            downloadAllRes.btnExit.setPosBtnX(sizeY/9 + sizeY/36);
//            downloadAllRes.btnExit.setPosBtnY(0);
        }
        if(downloadAllRes.btnExit.tripped) {
            downloadAllRes.btnExit.tripped = false;
            exitActivity();
        }
        if(downloadAllRes.btnFlag.tripped) {
            downloadAllRes.btnFlag.tripped = false;
//            startGameClass.gameField.refreshPositionStol();
            flagClick();
        }
        if(findNewGame.findGameBool && findNewGame.startGameBool && !startGame){
            startGame= true;
            downloadAllRes.resetPosition();
            downloadAllRes.resetBtn.setPosBtnX(sizeX);
            startGameClass.StartGameFun(findNewGame.gameMaster,findNewGame.opponent,findNewGame.user.getUid(),downloadAllRes.imageCard,sizeX,sizeY);
        }
        if (startGameClass.resetGame){
            Log.d("ProblemZZZ","startGameClass.resetGame");
            findNewGame.pressStart = false;
            startGameClass.resetGame = false;
//            downloadAllRes.resetPosition();//перенёс наверх
            startGameClass.deleteGameField();
            Log.d("MyTagsSS","GameEngine Update resetgame ");
//            startGameClass.gameField.startGame = false;
            startGame=false;
        }
        if(startGame){
            if(downloadAllRes.addFriendBtn.tripped){
                findNewGame.addOpponentInFriend();
                downloadAllRes.addFriendBtn.tripped = false;
                avatarClick = false;
            }
            if(startGameClass.gameField.startGame){
//                startGameClass.gameField.update();
                if (startGameClass.gameField.game) {
                    downloadAllRes.resetBtn.setPosBtnX(sizeX);
                    if(startGameClass.gameField.returnCard){
                        downloadAllRes.returnCardBtn.setPosBtnX(0);
                    }
                    else {
                        downloadAllRes.returnCardBtn.setPosBtnX(sizeX);
                    }
                    if (startGameClass.gameField.hodit && !startGameClass.gameField.otbivaet) {
                        downloadAllRes.beryBtn.setPosBtnX(sizeX);
                        downloadAllRes.bitoBtn.setPosBtnX(startGameClass.gameField.bitoBool ?
                                sizeX - downloadAllRes.bitoBtn.getWidth() :
                                sizeX);
                        downloadAllRes.passBtn.setPosBtnX(startGameClass.gameField.beryOpponent ?
                                sizeX - downloadAllRes.bitoBtn.getWidth() :
                                sizeX);
                    }
                    else {
                        downloadAllRes.passBtn.setPosBtnX(sizeX);
                        downloadAllRes.bitoBtn.setPosBtnX(sizeX);
                        downloadAllRes.beryBtn.setPosBtnX(startGameClass.gameField.beryBool?
                                sizeX - downloadAllRes.bitoBtn.getWidth() :
                                sizeX);
                    }
                    if (downloadAllRes.returnCardBtn.tripped) {
                        if (findNewGame.coin >= 50) {
                            startGameClass.gameField.meNeedReturnCardFun();
                        }
                        downloadAllRes.returnCardBtn.tripped = false;
                    }
                    if (downloadAllRes.bitoBtn.tripped) {
                        downloadAllRes.bitoBtn.tripped = false;
                        startGameClass.gameField.pressBito();
                    }
                    if (downloadAllRes.passBtn.tripped) {
                        downloadAllRes.passBtn.tripped = false;
                        startGameClass.gameField.pressPass();
                    }
                    if (downloadAllRes.beryBtn.tripped) {
                        downloadAllRes.beryBtn.tripped = false;
                        startGameClass.gameField.pressBery();
                    }
                }
                else {
                    //обнуление всех кликов иначи блокируется в motionEvent()
                    avatarClick = false;
                    emojiClick = false;
                    if (downloadAllRes.resetBtn.tripped) {
                        Log.d("ProblemZZZ","downloadAllRes.resetBtn.tripped");
                        findNewGame.pressStart = true;
                        downloadAllRes.resetBtn.tripped = false;
                        if (startGameClass.gameField.youWin) {
//                            startGameClass.gameField.youWin = false; // изза этого проблемы с автовыходом при opponentExit
                            startGameClass.gameField.myRef.child("test").child("reset").child("w").setValue("yes");
                        }
                        if (startGameClass.gameField.youLose){
//                            startGameClass.gameField.youLose = false;// изза этого проблемы с автовыходом при opponentExit
                            startGameClass.gameField.myRef.child("test").child("reset").child("l").setValue("yes");
                        }
                        if (startGameClass.gameField.nichia){
//                            startGameClass.gameField.nichia = false;// изза этого проблемы с автовыходом при opponentExit
                            if (startGameClass.gameField.gameMaster) {
                                startGameClass.gameField.myRef.child("test").child("reset").child("true").setValue("yes");
                            }
                            else {
                                startGameClass.gameField.myRef.child("test").child("reset").child("false").setValue("yes");
                            }
                        }
                    }
                    if(!findNewGame.pressStart) {
                        downloadAllRes.resetBtn.setPosBtnX((sizeX - downloadAllRes.resetBtn.getWidth()) / 2);
                    }
                    else {
                        downloadAllRes.resetBtn.setPosBtnX(sizeX);
                    }
                    downloadAllRes.returnCardBtn.setPosBtnX(sizeX);
                    downloadAllRes.bitoBtn.setPosBtnX(sizeX);
                    downloadAllRes.passBtn.setPosBtnX(sizeX);
                    downloadAllRes.beryBtn.setPosBtnX(sizeX);
                }
            }
        }
        else {
            if (downloadAllRes.resetBtn.tripped) {
                findNewGame.pressStart = true;
                downloadAllRes.resetBtn.tripped = false;
                if (findNewGame.findGameBool && !findNewGame.startGameBool) {
                    fingGameLogo = false;
                    findNewGame.startGame();
                }
            }
        }
    }
    public void resume() {
        Log.e("Error joining thread  ", "GEresume");
        playing = true;
        gameThread = new Thread(this);
        gameThread.start();
        findNewGame.playing = true;
        if(findNewGame.startGameBool){
            findNewGame.resume();
        }
        if(startGameClass!= null && startGameClass.gameField!= null){
            startGameClass.gameField.playning = true;
        }
//        if(startGameClass!=null){
//            startGameClass.resume();
//        }

    }
    public void pause() {

        Log.d("testDeb","pause exit");
        if(startGameClass!= null && startGameClass.gameField!= null){
            startGameClass.gameField.playning = false;
        }
        if(!findNewGame.findGameBool){
            findNewGame.deleteFind();
        }
        else if(findNewGame.startGameBool) {
            if (findNewGame.opponentExit) {
                findNewGame.deleteGame();
            } else {
                findNewGame.exit();
            }
        }
        if(findNewGame.findGameBool && !findNewGame.startGameBool){
            findNewGame.exit();
        }
        playing = false;
        findNewGame.playing = false;
        try {
            gameThread.join();
        } catch (Exception e) {
            Log.e("Error joining thread  ", e.toString());
        }
        if(startGameClass!=null){
            startGameClass.pause();
        }

    }
//    public boolean isConnected() throws InterruptedException, IOException {
////        final String command = "ping -c 1 google.com";
////        return Runtime.getRuntime().exec(command).waitFor() == 0;
////        final String command = "ping -c 1 8.8.8.8";
////        return Runtime.getRuntime().exec("ping -c 1 8.8.8.8").waitFor() == 0;
////        public static boolean isOnline(Context context)
////        {
//        Log.d("Connect","start");
//            cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
//            netInfo = cm.getActiveNetworkInfo();
//            if (netInfo != null && netInfo.isConnectedOrConnecting())
//            {
//                if(netInfo.getType() == ConnectivityManager.TYPE_WIFI){
////                    final String command = "ping -c 1 google.com";
//                    Log.d("Connect","ret1");
//                    return true;
////                    return Runtime.getRuntime().exec("ping -c 1 8.8.8.8").waitFor() == 0;
//                }
//                Log.d("Connect","ret2");
//                return true;
//            }
//        Log.d("Connect","ret3");
//            return false;
//    }

//    public void motion(MotionEvent motionEvent){
    public void motion(MotionEvent motionEvent, float x, float y){
        if ((motionEvent.getAction() & MotionEvent.ACTION_MASK) == MotionEvent.ACTION_DOWN){

            touchX = (int) (motionEvent.getX()/(x/sizeX));
            touchY = (int) (motionEvent.getY()/(y/sizeY));
//            Log.d("TouchTest",touchX + " " + touchY);
            downloadAllRes.addFriendBtn.touch(touchX,touchY);
            if(!avatarClick) {
                downloadAllRes.returnCardBtn.touch(touchX, touchY);
                downloadAllRes.resetBtn.touch(touchX, touchY);
                downloadAllRes.bitoBtn.touch(touchX, touchY);
                downloadAllRes.beryBtn.touch(touchX, touchY);
                downloadAllRes.passBtn.touch(touchX, touchY);
                downloadAllRes.btnExit.touch(touchX,touchY);
                downloadAllRes.btnFlag.touch(touchX,touchY);
            }
            if(!emojiClick) {
//                numEmojiClick = -1;
//                animEmojiClick = 0;
                downloadAllRes.returnCardBtn.touch(touchX, touchY);
                downloadAllRes.resetBtn.touch(touchX, touchY);
                downloadAllRes.bitoBtn.touch(touchX, touchY);
                downloadAllRes.beryBtn.touch(touchX, touchY);
                downloadAllRes.passBtn.touch(touchX, touchY);
                downloadAllRes.btnExit.touch(touchX,touchY);
                downloadAllRes.btnFlag.touch(touchX,touchY);
            }
            else {
                for(int i = 0; i < 6; i++) {
                    if(downloadAllRes.emojis[groupEmoji][i].touch(touchX,touchY)) {
                        //Временная защита чтобы не кликнуть куда-нибудь ещё
                            return;
                    }
                }
//                for(Card mark:downloadAllRes.emojisMark){
//                    mark.touch(touchX,touchY);
//                }

            }
        }
        if ((motionEvent.getAction() & MotionEvent.ACTION_MASK) == MotionEvent.ACTION_UP) {
            untouchX = (int)(motionEvent.getX()/(x/sizeX));
            untouchY = (int)(motionEvent.getY()/(y/sizeY));
            if(emojiClick) {

//                for(int i = 0; i < 6; i++) {
//                    downloadAllRes.emojis[i].unt(touchX,touchY);
//                }
                for(int i = 0; i < 6; i++) {
                    if(downloadAllRes.emojis[groupEmoji][i].touch(touchX,touchY)) {
                        if(startGameClass!= null
                                && (groupEmoji>0&&((startGameClass.smilesCounts&1<<(i+(groupEmoji-1)*6))==(1<<(i+(groupEmoji-1)*6))))){
                            startGameClass.sendEmoji(downloadAllRes.emojis[groupEmoji][i].emojiName);
                            numEmojiClick = i;
                            if(fps>30) {
                                animEmojiClick = fps+20;
                            } else {
                                animEmojiClick = 60;
                            }
                            emojiClick=false;
                            showEmoji = groupEmoji;
                            return;
                        } else {
                            return;
                        }
                    }
                }
                for(int i = 0;i < downloadAllRes.emojisMark.length;i++){
                    if(downloadAllRes.emojisMark[i].touch(touchX,touchY)){
                        groupEmoji = i;
                    }
                }
//                if((touchX<sizeX/4 || untouchX>sizeX*3/4) || (touchY<sizeY/2-sizeX/4 || untouchY>sizeY/2+sizeX/4)) {
//                    emojiClick=false;
//                }
                if(
                        touchX<downloadAllRes.emojisMark[0].getPosX()
                        || touchX>(downloadAllRes.emojisMark[downloadAllRes.emojisMark.length-1].getPosX()+downloadAllRes.emojisMark[0].getWidth())
                        || touchY<(sizeY+downloadAllRes.grandPanelGameField.getHeight())/2
                        || touchY>downloadAllRes.emojisMark[0].getPosY()+downloadAllRes.emojisMark[0].getWidth()) {
                    emojiClick=false;
                }
            }
            else {
                if(findNewGame.startGameBool&&!avatarClick) {

                    if(startGameClass!=null && startGameClass.gameField!=null && startGameClass.gameField.game) {
                        if (touchX > sizeX - sizeY / 9 && touchY > sizeY - sizeY / 9) {
                            if (untouchX > sizeX - sizeY / 9 && untouchY > sizeY - sizeY / 9) {
                                emojiClick = true;
                            }
                            return;
                        }
                    }
                }
            }
            if(avatarClick) {
                downloadAllRes.addFriendBtn.unTouch(untouchX,untouchY);
                if((touchX<sizeX/4 || untouchX>sizeX*3/4) || (touchY<sizeY/2-sizeX/4 || untouchY>sizeY/2+sizeX/4)) {
                    avatarClick=false;
                    downloadAllRes.addFriendBtn.setPosBtnX(sizeX);
//                    downloadAllRes.addFriendBtn.setPosBtnY(sizeY);
                    downloadAllRes.returnCardBtn.unTouch(-1000, -1000);
                    downloadAllRes.resetBtn.unTouch(-1000, -1000);
                    downloadAllRes.bitoBtn.unTouch(-1000, -1000);
                    downloadAllRes.beryBtn.unTouch(-1000, -1000);
                    downloadAllRes.passBtn.unTouch(-1000, -1000);
                    downloadAllRes.btnExit.unTouch(-1000,-1000);
                    downloadAllRes.btnFlag.unTouch(-1000,-1000);
                }
            }
            else {
//                ВРЕМЕННО ПРИКРЫЛ ЭТОТ УЧАСТОК
                if(startGame){
                    if(touchX<sizeY/9 && untouchX<sizeY/9 && touchY<sizeY/9 && untouchY<sizeY/9){
                        downloadAllRes.addFriendBtn.setPosBtnX((sizeX + downloadAllRes.grandPanelGameField.getWidth())/2-downloadAllRes.grandPanelGameField.getWidth()/10-downloadAllRes.addFriendBtn.getWidth());
//                        downloadAllRes.addFriendBtn.setPosBtnY(sizeY/2+sizeX/3/2 + (downloadAllRes.menuAddFriend.getWidth()/2-sizeX/3/2 )/2 - downloadAllRes.addFriendBtn.getHeight()/2);
                        avatarClick = true;
                    }
                }
                downloadAllRes.addFriendBtn.unTouch(-1000, -1000);
                downloadAllRes.returnCardBtn.unTouch(untouchX, untouchY);
                downloadAllRes.resetBtn.unTouch(untouchX, untouchY);
                downloadAllRes.bitoBtn.unTouch(untouchX, untouchY);
                downloadAllRes.beryBtn.unTouch(untouchX, untouchY);
                downloadAllRes.passBtn.unTouch(untouchX, untouchY);
                downloadAllRes.btnExit.unTouch(untouchX,untouchY);
                downloadAllRes.btnFlag.unTouch(untouchX,untouchY);
            }
        }
        if ((!avatarClick || !emojiClick) && (touchX < sizeX - sizeY/9 ||  touchY < sizeY - sizeY/9)) {
//        if (!avatarClick && touchY < sizeY - sizeY/14) {
            if (startGameClass.gameField!=null&&startGameClass.gameField.startGame) {
                if (!startGameClass.gameField.beryyyyy) {
                    startGameClass.gameField.motion(motionEvent,x , y);
//                    startGameClass.gameField.motion(motionEvent);
                }
            }
        }
}
    protected void exitActivity() {
        Log.d("exitGameEngine","first");
        playing = false;
        if (friendGame) {
            findNewGame.deleteFriendGame();
        }
        deleteGameAndClasses();
        Intent intent = new Intent(context, MainActivity.class);

        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Log.d("exitGameEngine","second end");
        context.startActivity(intent);
    }
    private void deleteGameAndClasses() {
        if(startGameClass!=null){
//            startGameClass.deleteGameField();

            if(startGameClass.gameField!=null) {
                startGameClass.gameField.deleteListener();
                startGameClass.gameField.context = null;
//                gameField = null;// ошибка в методе Draw, там всё становиться NULL
            }
            startGameClass.deleteGameAndClasses();
//            startGameClass = null;
        }
    }
    protected void flagClick() {

        AlertDialog.Builder quitDialog = new AlertDialog.Builder(context);
        quitDialog.setTitle("Вы хотите сдаться?");
        quitDialog.setPositiveButton("Таки ДА!", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
//                    Функция сдаться
//                WhiteCastle();// не знаю, это всё лишнее
//                public void WhiteCastle(){
                    startGameClass.WhiteCastle();
//                }
            }
        });
        quitDialog.setNegativeButton("Таки НИЗАШО", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
//        runOnUiThread(new Runnable() {
//            public void run() {
//                Toast.makeText(activity, "Hello", Toast.LENGTH_SHORT).show();
//            }
//        });
        handler.post(
                new Runnable()
                {
                    @Override
                    public void run()
                    {
                        quitDialog.show();
                    }
                }
        );
//        quitDialog.show();
    }
}