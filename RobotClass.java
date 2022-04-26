package ru.tmkstd.cardgamedurakonline;


//import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.util.TypedValue;
import android.widget.LinearLayout;
import android.widget.TextView;
//import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class RobotClass {
    String[] robotsNames;
    int[][] robotsParams;
    FirebaseAuth mAuth;
    DatabaseReference myRef;
    FirebaseUser user;
    String IDrobotsName;
    int robotID = 0;
    int numsGames;
    long robotTimerStartGame = 0;
    final String anims;
    String tempSmile;

    boolean modGame;
//    boolean robotSet;
    int numCards;
//    int timerOnStart;
    ValueEventListener
//        waitRoomListener,
        waitOpponentBeforeGame,opponentListener,robotGameDataListener, exitGameListener, nameOpponListener, animListener;
    Timer timer;
    TimerTask timerTask;
    Timer timerAnim;
    TimerTask timerTaskAnim;
    LinearLayout parentLa;
    TextView sostoan;
    TextView time,countPlayers;
    Handler handler;
    Date currentDate;
    DateFormat timeFormat, timeRobot;

    String opponentId;
    RobotStartGame robotStartGame;
    public RobotClass(LinearLayout la){
        parentLa = la;
        anims = "abdf";
        currentDate = new Date();
        timeFormat = new SimpleDateFormat("yy-MM-dd HH:mm:ss", Locale.getDefault());
        timeRobot = new SimpleDateFormat("HH", Locale.getDefault());
        handler = new Handler(Looper.getMainLooper());
        sostoan = new TextView(parentLa.getContext());
        time = new TextView(parentLa.getContext());
        countPlayers = new TextView(parentLa.getContext());

//        robotsNames = new String[25];
//        robotsNames[0] = "-00000000000"; // это котмэн
//
//        robotsNames[1] = "-00000000001";
//        robotsNames[2] = "-00000000002";
//        robotsNames[3] = "-00000000003";
//        robotsNames[4] = "-00000000004";
//        robotsNames[5] = "-00000000005";
//        robotsNames[6] = "-00000000006";
//        robotsNames[7] = "-00000000007";
//        robotsNames[8] = "-00000000008";
//        robotsNames[9] = "-00000000009";
//        robotsNames[10] = "-00000000010";
//        robotsNames[11] = "-00000000011";
//        robotsNames[12] = "-00000000012";
//
//        robotsNames[13] = "-00000000013";
//        robotsNames[14] = "-00000000014";
//        robotsNames[15] = "-00000000015";
//        robotsNames[16] = "-00000000016";
//        robotsNames[17] = "-00000000017";
//        robotsNames[18] = "-00000000018";
//        robotsNames[19] = "-00000000019";
//        robotsNames[20] = "-00000000020";
//        robotsNames[21] = "-00000000021";
//        robotsNames[22] = "-00000000022";
//        robotsNames[23] = "-00000000023";
//        robotsNames[24] = "-00000000024";
        //1 робот или нет(имитация), хотя наверно это лишнее


        //0 вероятность перевода,
        //1 вероятность перевода козыря,
        //2 вероятность брать карту без умысла,
        //3 вероятность бить козырем,
        //4 вероятность бить по однму или сначала проверить может ли отбить
        //5 веоятность подкидывать всё после беру или не подкидывает ничего
        //6 подкидывает ли козыри
        //7 Новое вероятность в первом ходу сразу походить двумя
        //8 Сразу отобьёт двумя
        //8 диопазон времени отбивания пдкидывания
        //8 кол-во игр которыйе играет(диапазон)
//        Ещё надо напихать рандома во времени с начала вглючения
        robotsParams = new int[][]
                {
                        {100,50, 0,100,70, 90,90},
//                        {100,100,0,100,100,100,100},
                        { 80,20, 3, 66,90, 69,40},
                        { 80,20, 3, 70,90, 52,40},
                        { 80,20, 3, 99,90, 32,40},
                        { 80,20, 3, 75,90, 69,40},
                        { 80,20, 3, 53,95, 40,40},
                        { 80,20, 3, 55,90, 67,40},
                        { 80,20, 3, 57,90, 44,40},
                        { 80,20, 5, 77,95, 84,40},
                        { 80,20, 3, 70,90, 49,40},
                        { 80,20, 3, 70,90, 49,40},
                        { 80,20, 3, 70,90, 49,40},
                        { 80,20, 3, 70,90, 49,40}
//                        { 80,20, 3, 70,90, 49,40},
//                        { 80,20, 3, 70,90, 49,40},
//                        { 80,20, 3, 70,90, 49,40},
//                        { 80,20, 3, 70,90, 49,40},
//                        { 80,20, 3, 70,90, 49,40},
//                        { 80,20, 3, 70,90, 49,40},
//                        { 80,20, 3, 70,90, 49,40},
//                        { 80,20, 3, 70,90, 49,40},
//                        { 80,20, 3, 70,90, 49,40},
//                        { 80,20, 3, 70,90, 49,40},
//                        { 80,20, 3, 70,90, 49,40},
//                        { 80,20, 3, 70,90, 49,40}
                };
//        robotsParams[0] = new int[]{0,100,50,0,100};

        sostoan.setTextSize(TypedValue.COMPLEX_UNIT_PX, 45);
        sostoan.setTextColor(Color.argb(255,0,0,0));
        sostoan.setPadding(15,15,15,0);
        sostoan.setText("hello");
        time.setTextSize(TypedValue.COMPLEX_UNIT_PX, 45);
        time.setTextColor(Color.argb(255,0,0,0));
        time.setPadding(15,15,15,0);
        countPlayers.setTextSize(TypedValue.COMPLEX_UNIT_PX, 45);
        countPlayers.setTextColor(Color.argb(255,0,0,0));
        countPlayers.setPadding(15,15,15,0);
        countPlayers.setText("0");
        parentLa.addView(sostoan);
        parentLa.addView(time);
        parentLa.addView(countPlayers);
        myRef = FirebaseDatabase.getInstance().getReference();
        user = mAuth.getInstance().getCurrentUser();
        InitListener();

    }

    protected void createLich(String[] names) {
        robotsNames = names;
    }
    protected void startGame() {
        Log.d("StartGame","ssssssa");
        robotTimerStartGame = System.currentTimeMillis();
        robotStartGame = new RobotStartGame(IDrobotsName,opponentId,myRef,modGame,robotsParams[robotID],numsGames);
        myRef.child("RobotGame").child(opponentId).addListenerForSingleValueEvent(robotGameDataListener);
        time.setText("Time: " + timeFormat.format(currentDate));
        countPlayers.setText(Integer.parseInt(countPlayers.getText().toString()) + 1 + "");
        sostoan.setText("Status: Game");


    }
    void deleteAll(){
        robotTimerStartGame = 0;
        if(opponentId!= null && IDrobotsName!=null){
            myRef.child("GameRoom").child(opponentId + IDrobotsName).child(opponentId).removeEventListener(opponentListener);
            myRef.child("GameRoom").child(opponentId + IDrobotsName).child(opponentId).removeEventListener(exitGameListener);
            myRef.child("GameRoom").child(opponentId + IDrobotsName).child(IDrobotsName).setValue("Exit");
            myRef.child("GameRoom").child(opponentId + IDrobotsName).child("anim"+true).removeEventListener(animListener);
            if(timerAnim != null){
                timerAnim.cancel();
                timerAnim = null;
            }
        }
        currentDate = new Date();
        time.setText("Time: " + timeFormat.format(currentDate));
        sostoan.setText("Status: Wait");
        opponentId = null;
        if(robotStartGame !=null) {
            robotStartGame.exit();
            robotStartGame = null;
        }
        if (timer != null) {
            timer.cancel();
            timer = null;
            timerTask = null;
        }
//        myRef.child("WaitRoom").removeEventListener(waitRoomListener);

//        database = null;
//        myRef = null;
//        user = null;
    }
    protected void InitListener() {
        nameOpponListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue(String.class)!=null) {
                    TextView name = new TextView(parentLa.getContext());
                    name.setTextSize(TypedValue.COMPLEX_UNIT_PX, 30);
                    name.setTextColor(Color.argb(255,0,0,0));
                    name.setPadding(15,10,15,0);
                    name.setText(dataSnapshot.getValue(String.class)+" <|> " + String.valueOf(modGame).toUpperCase().charAt(0)+numCards);
                    parentLa.addView(name);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        robotGameDataListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//тут пишет ошибку null for argument in child()
                try {
                    myRef.child("Names").child(opponentId).addListenerForSingleValueEvent(nameOpponListener);
                } catch (Exception e) {
                    long timeTemp = System.currentTimeMillis();
                    myRef.child("Errors").child(String.valueOf(timeTemp).concat("1")).setValue(opponentId);
                    myRef.child("Errors").child(String.valueOf(timeTemp).concat("2")).setValue(e);
                }
                if(dataSnapshot.getValue(String.class)!=null) {
                    String temp = dataSnapshot.getValue(String.class);
//                    Log.d("dataLog", dataSnapshot.getValue(String.class));
//                    Log.d("dataLog", temp.indexOf(' ') + " " + temp.substring(0,temp.indexOf(' ')) + " " + Integer.parseInt(temp.substring(0,temp.indexOf(' '))));
                    int num = Integer.parseInt(temp.substring(0,temp.indexOf(' '))) + 1;
                    myRef.child("RobotGame").child(opponentId).setValue(num+ " " + timeFormat.format(currentDate));
                }
                else {
                    myRef.child("RobotGame").child(opponentId).setValue(1 + " " + timeFormat.format(currentDate));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        exitGameListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue(String.class)!=null) {
                    //тут ещё впендюрить, когда в RobotField человек свернул игру и не заходил в неё,
                    // то он проиграл, и Game=false, там в таимере обявляю победителя и сдесь ставлю
                    // Exit типо if(equals.(Exit) && !game) deleteAll(); exit() без таймера.
                    // А вообще надо будет это переделать и оставить таймер на игру 30сек в роботфилд
                    // а здесь просто смотреть ушёл или нет(хотя можно совсем не смотреть, а сделать if(equals(exit)&&Game!)то выход
                    // а в филд если выход по таймеру то записать в оппонента (exitTimer) и сделать if(exitTimer) то выход.



                    if(dataSnapshot.getValue(String.class).equals("Exit")
                            && (robotStartGame==null
                                ||robotStartGame.robotGameField==null
                                ||robotStartGame.robotGameField.robotGameAI.endGame)){
                        deleteAll();
                        return;
                    }
                    // сделать реагировать на Exit и сделать таймер минуту(на всякий случай) и сделать выход когда время выйдет
                    // пока робот неного глючит и иногда не выходит
                    if(dataSnapshot.getValue(String.class).equals("Exit")) {
                        if(timer!=null){
                            timer.cancel();
                            timer = null;
                            timerTask= null;
                        }
                        timer = new Timer();
                        timerTask = new TimerTask() {
                            @Override
                            public void run() {
//                            myRef.child("GameRoom").child(opponentId + IDrobotsName).child(opponentId).removeEventListener(opponentListener);
//                            myRef.child("GameRoom").child(opponentId + IDrobotsName).child(IDrobotsName).setValue("Exit");
//                            opponentId = null;
//                            startWork(timerOnStart);
                                handler.post(
                                        new Runnable()
                                        {
                                            @Override
                                            public void run()
                                            {
                                                //в принципе можно сделать здесь типо таймер 30 сек
                                                //т.к. если Exit 30 сек то он проиграл,
                                                //Хотя правильно будет сделать 30 сек в таймер листенер.Т.к. человек может откл интернет и не выйти
                                                deleteAll();
                                            }
                                        }
                                );
                            }
                        };
                        timer.schedule(timerTask,60000);
                    }
                    if (dataSnapshot.getValue(String.class).equals("ExitTimer")) {
                        deleteAll();
                    }
                    // пока так. Потому что я не сделал ExitTimer
                    if (dataSnapshot.getValue(String.class).equals("Game") && robotStartGame!=null && robotStartGame.robotGameField!=null && !robotStartGame.robotGameField.robotGameAI.endGame) {
                        if(timer!=null){
                            timer.cancel();
                            timer = null;
                            timerTask= null;
                        }
                    }
//                    if (dataSnapshot.getValue(String.class).equals("Exit")) {
//                        if(timer!=null){
//                            timer.cancel();
//                            timer = null;
//                            timerTask= null;
//                        }
//                        timer = new Timer();
//                        timerTask = new TimerTask() {
//                            @Override
//                            public void run() {
////                            myRef.child("GameRoom").child(opponentId + IDrobotsName).child(opponentId).removeEventListener(opponentListener);
////                            myRef.child("GameRoom").child(opponentId + IDrobotsName).child(IDrobotsName).setValue("Exit");
////                            opponentId = null;
////                            startWork(timerOnStart);
//                                handler.post(
//                                        new Runnable()
//                                        {
//                                            @Override
//                                            public void run()
//                                            {
//                                                //в принципе можно сделать здесь типо таймер 30 сек
//                                                //т.к. если Exit 30 сек то он проиграл,
//                                                //Хотя правильно будет сделать 30 сек в таймер листенер.Т.к. человек может откл интернет и не выйти
//                                                deleteAll();
//                                            }
//                                        }
//                                );
//                            }
//                        };
//                        timer.schedule(timerTask,35000);
//                    }
//                    if(dataSnapshot.getValue(String.class).equals("Game")){
//                        if(timer!=null) {
//                            timer.cancel();
//                            timer = null;
//                            timerTask = null;
//                        }
//                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        opponentListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue(String.class)==null){
                    if(timer!=null) {
                        timer.cancel();
                        timer = null;
                        timerTask = null;
                    }
                    timer = new Timer();
                    timerTask = new TimerTask() {
                        @Override
                        public void run() {
//                            myRef.child("GameRoom").child(opponentId + IDrobotsName).child(opponentId).removeEventListener(opponentListener);
//                            myRef.child("GameRoom").child(opponentId + IDrobotsName).child(IDrobotsName).setValue("Exit");
//                            opponentId = null;
//                            startWork(timerOnStart);
                            handler.post(
                                    new Runnable()
                                    {
                                        @Override
                                        public void run()
                                        {
                                            deleteAll();
                                        }
                                    }
                            );
                        }
                    };
                    timer.schedule(timerTask,15000);
                }
                else if(dataSnapshot.getValue(String.class).equals("Ready")){
                    if(timer!=null) {
                        timer.cancel();
                        timer = null;
                        timerTask = null;
                    }
                    myRef.child("GameRoom").child(opponentId + IDrobotsName).child(IDrobotsName).setValue("Ready");//ДУБЛЬ3 на этом месте может быть Wait так что надо быть аккуратным и продублировать гдето ещё
                    timer = new Timer();
                    timerTask = new TimerTask() {
                        @Override
                        public void run() {
//                            myRef.child("GameRoom").child(opponentId + IDrobotsName).child(opponentId).removeEventListener(opponentListener);
//                            myRef.child("GameRoom").child(opponentId + IDrobotsName).child(IDrobotsName).setValue("Exit");
//                            opponentId = null;
//                            deleteAll();
//                            startWork(timerOnStart);
                            handler.post(
                                    new Runnable()
                                    {
                                        @Override
                                        public void run()
                                        {
                                            deleteAll();
                                        }
                                    }
                            );
                        }
                    };
                    timer.schedule(timerTask,15000);
                }
                else if (dataSnapshot.getValue(String.class).equals("Game")){
                    if(timer!=null){
                        timer.cancel();
                        timer = null;
                        timerTask= null;
                    }
                    myRef.child("GameRoom").child(opponentId + IDrobotsName).child(IDrobotsName).setValue("Ready"); //ДУБЛЬ2 на этом месте может быть Wait так что надо быть аккуратным и продублировать гдето ещё
                    myRef.child("GameRoom").child(opponentId + IDrobotsName).child(opponentId).removeEventListener(opponentListener);

                    timer = new Timer();
                    timerTask = new TimerTask() {
                        @Override
                        public void run() {
                            handler.post(
                                    new Runnable()
                                    {
                                        @Override
                                        public void run()
                                        {
                                            myRef.child("GameRoom").child(opponentId + IDrobotsName).child(IDrobotsName).setValue("Game");
                                            myRef.child("GameRoom").child(opponentId + IDrobotsName).child("anim"+true).addValueEventListener(animListener);
                                            startGame();
                                        }
                                    }
                            );
                        }
                    };
                    timer.schedule(timerTask,(int)(Math.random()*3000));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        animListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue(String.class)!=null) {
                    tempSmile = dataSnapshot.getValue(String.class);
                    if(timerAnim != null){
                        timerAnim.cancel();
                        timerAnim = null;
                    }
                    timerAnim = new Timer();
                    timerTaskAnim = new TimerTask() {
                        @Override
                        public void run() {
                            if(tempSmile.indexOf('e')!=-1 || tempSmile.indexOf('c')!=-1) {
                                myRef.child("GameRoom").child(opponentId + IDrobotsName).child("anim"+false).setValue(String.valueOf('c'));
                            }
                            if(Math.random()>0.3f) {
                                myRef.child("GameRoom").child(opponentId + IDrobotsName).child("anim"+false).setValue(String.valueOf(anims.charAt((int)(Math.random()*anims.length()))));
                            }
                        }
                    };
                   timerAnim.schedule(timerTaskAnim,1000+(int)(Math.random()*2000));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        waitOpponentBeforeGame = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue(String.class)!=null&&dataSnapshot.getValue(String.class).length()<10){
////                    opponentId = dataSnapshot.getKey();
//                    if(dataSnapshot.getValue(String.class).indexOf("true")>=0){
//                        modGame = true;
//                    }
//                    else {
//                        modGame = false;
//                    }
//                    if(dataSnapshot.getValue(String.class).indexOf("24")>=0){
//                        numCards = 24;
//                    }
//                    else if(dataSnapshot.getValue(String.class).indexOf("36")>=0){
//                        numCards = 36;
//                    }
//                    else numCards = 52;
                    myRef.child("WaitRoom").child(opponentId).setValue(IDrobotsName);
//                    try {
//                        Thread.sleep(1000); //Приостанавливает поток на 1 секунду
//                    } catch (Exception e) {
//
//                    }
                    if(timer != null) {
                        timer.cancel();
                        timer = null;
                    }
                    timer = new Timer();
                    timerTask = new TimerTask() {
                        @Override
                        public void run() {
                            myRef.child("GameRoom").child(opponentId + IDrobotsName).child(IDrobotsName).setValue("Ready"); //ДУБЛЬ1 на этом месте может быть Wait так что надо быть аккуратным и продублировать гдето ещё
                            myRef.child("GameRoom").child(opponentId + IDrobotsName).child(opponentId).addValueEventListener(opponentListener);
                            myRef.child("GameRoom").child(opponentId + IDrobotsName).child(opponentId).addValueEventListener(exitGameListener);//включаю здесь ExitListener т.к. оппонент может выйти раньше
                        }
                    };
                    timer.schedule(timerTask,500);
//                    myRef.child("GameRoom").child(opponentId + IDrobotsName).child(opponentId).addValueEventListener(exitGameListener);
                }
                else {
//                    startWork(timerOnStart);
                    opponentId = null;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
    }
    protected void whiteFlagAndEndGame(){
        TextView RRR = new TextView(parentLa.getContext());
        RRR.setTextSize(TypedValue.COMPLEX_UNIT_PX, 30);
        RRR.setTextColor(Color.argb(255,0,0,0));
        RRR.setPadding(15,10,15,0);
        if(robotStartGame!=null&& robotStartGame.robotGameField!=null&&!robotStartGame.robotGameField.robotGameAI.endGame) {
            myRef.child("GameRoom").child(opponentId + IDrobotsName).child("Winner").setValue("trueflag");
            RRR.setText("-Объед-е- Flag");
        }
        else {
            RRR.setText("-Объед-е- Wait");
        }
        parentLa.addView(RRR);
        if(timer!=null) {
            timer.cancel();
            timer = null;
            timerTask = null;
        }
        timer = new Timer();
        timerTask = new TimerTask() {
            @Override
            public void run() {
//                Log.d("testTimer","2 :"+timerOnStart);
//                Log.d("waitRoom","test1 " + opponentId+ "  " + oppId);
                handler.post(
                        new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                deleteAll();
                            }
                        }
                );
            }
        };
//        timer.schedule(timerTask,3000);// слишком долго
        timer.schedule(timerTask,1500);
    }
    protected void startWait(String oppId, String mode
//            , boolean robotSt
                             ,int numsGame
    ){
        deleteAll();//Здесь бнуляется таймеры
        opponentId = oppId;
        numsGames = numsGame;
//        robotSet = robotSt;
        if(mode.indexOf("true")>=0){
            modGame = true;
        }
        else {
            modGame = false;
        }
        if(mode.indexOf("24")>=0){
            numCards = 24;
        }
        else if(mode.indexOf("36")>=0){
            numCards = 36;
        }
        else numCards = 52;

        timer = new Timer();
        timerTask = new TimerTask() {
            @Override
            public void run() {
                Log.d("waitRoom","test1 " + opponentId+ "  " + oppId);
                handler.post(
                    new Runnable() {
                        @Override
                        public void run() {
                            robotID = 0;
                            if( robotsNames.length>1) {
                                if (modGame) {
                                    if (numCards == 24)
                                        robotID = (int) (Math.random() + 0.5);
                                    else if (numCards == 36)
                                        robotID = (int) (Math.random() + 2.5);
                                    else robotID = (int) (Math.random() + 4.5);
                                } else {
                                    if (numCards == 24)
                                        robotID = (int) (Math.random() + 6.5);
                                    else if (numCards == 36)
                                        robotID = (int) (Math.random() + 8.5);
                                    else robotID = (int) (Math.random() + 10.5);

                                }
                                // от 1 до 12 всего 12 личностей
                                currentDate = new Date();
                                robotID = (1 + (robotID + Integer.valueOf(timeRobot.format(currentDate)) % 12) % 12);
                                if (robotID > 13) robotID = 13;
                            }
                            IDrobotsName = robotsNames[robotID];
                            myRef.child("WaitRoom").child(opponentId).addListenerForSingleValueEvent(waitOpponentBeforeGame);
                        }
                    }
                );
            }
        };
        timer.schedule(timerTask,6000);
//        timer.schedule(timerTask,1000);

    }
}
