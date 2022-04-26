package ru.tmkstd.cardgamedurakonline;

import android.content.Context;
import android.media.SoundPool;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.view.MotionEvent;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.Timer;
import java.util.TimerTask;

public class GameField {
    int take=-1, step=-1;
    SoundPool mSoundPool;
    boolean nextHod = false;
    boolean playning,connectInternet = false;
    long startTestConnect,endTestConnect;
    Thread threadTestConnect;

    String dataCardsHodit, dataCardOtbivaet;

    Timer timerWin;
    TimerTask timerTaskWin;
    Timer timerBito;
    TimerTask timerTaskBito;

    DatabaseReference myRef;
    ValueEventListener prosmotrHodit, prosmotrOtbivaet,
            prosmotrPositionBito, prosmotrPositionPass,prosmotrPositionBery,
//            rasdachaPass,
            prosmotrLenOtbiv,prosmotrLenHodit, prosmotrLenKolod, prosmotrWin,prosmotrLenOpponent,
            timerListener, returnCardListener, returnCardCoinListener;
    ValueEventListener winnerCoin, loserCoin;
    String myCards,kolodaSorting = "MIEAwsokgc840NJFBxtplhd951OKGCyuqmiea62PLHDzvrnjfb73", kolodaRandom;
    String oldMyCards;
    String hoditCard = "", otbivaetCard="", oldHoditCard;
    String tempHoditCard, tempOtbivaetCard;
    Card[] allCards, myCardsImg,stolImageCards;
    int maxX,maxY,sizeCard;
    int touchX, touchY,untouchX,untouchY;
    int lenOtbiv,lenHodit, lenKolod,lenOpponent, oldLenOpponent,animOpponentCard;
//    boolean resetListener;
    boolean hodit = false,otbivaet = false,
beryOpponent = false, singRasdachaBito = false,pressPassBool;
    boolean game = false, youWin = false, nichia = false, youLose = false, gameMaster;
    int[][] stol = new int[2][6];
    boolean beryyyyy = false;
    boolean returnCard, meNeedReturnCard, opponentMayReturnCard, meReturnCard, opponentReturnCard;
    boolean modGame;

//    boolean rubashkaGetOppon = false, lastGetOppon = false;
    int lastCardIndex =-1;
//    Bitmap endKoloda;

    String user,opponent;

    boolean bitoBool;
    boolean beryBool;

    boolean tempBito=false,tempPass=false,tempBery=false;

    boolean startGame;

    boolean reOpenGameBool;

    long timer = 30,proverckTimer = 30;

    boolean endGameTimerBool = false;
    long endGameTimer = 0;

    int koziri = -1;

    int passChatAnim = 0;
    int bitoChatAnim = 0;
    int flagChatAnim = 0;

    FirebaseDatabase database;
    DatabaseReference refCoin;

    Card[] rubashka;
    Context context;

    int indexTouchCards = -1, tempI = -1;
    int indexChtoBiot;

    GameField(Card[] rubashka, Context context){
        playning = true;
        this.context = context;
        nextHod = true;
        pressPassBool = false;
        stolImageCards = new Card[12];
        meNeedReturnCard = false;
        opponentMayReturnCard = false;
        returnCard = false;
        database = FirebaseDatabase.getInstance();
        refCoin = database.getReference();
        Log.d("MyTagsSS","Startgame gamefield create");
        this.rubashka = rubashka;
        startGame = false;
//        resetListener = false;
        initListener();
    }

    void reOpenGame(boolean gm,DatabaseReference ref) {
        playning = true;
        meNeedReturnCard = false;
        pressPassBool = false;
        opponentMayReturnCard = false;
        returnCard = false;
//        lastGetOppon= false;
//        rubashkaGetOppon = false;
        Log.d("MyTagsSS","GameField restart1");
        reOpenGameBool = true;
        gameMaster = gm;
        oldLenOpponent = 6;
        sizeCard = allCards[0].getWidth();
        initStol();
        myRef=ref;
        deleteListener();
        myRef.child("timer").addValueEventListener(timerListener);
        Log.d("MyTagsSS","GameField restart2");
        Log.d("GameFieldREst","GameField restart2");
        endGameTimerBool = false;
        endGameTimer = 0;
        startGame = true;
        game = true;

    }
    private void initStol (){
        for(int i = 0; i < 3; i++) {
            stol[1][i] = maxY - allCards[0].getHeight()*2-allCards[0].getHeight()*8/10;
            stol[1][i+3] = maxY - allCards[0].getHeight()*2;
        }
        // раздвигаю карты на растояние синус 8 градусов это 0.15 но можно добавить побольше и сделал на 0.2 * на scale
        stol[0][0] = stol [0][3] = (int)(maxX/2- sizeCard/2 - allCards[0].getHeight()*0.15 - allCards[0].getWidth()*0.6f);
        stol[0][1] = stol [0][4] = maxX/2- sizeCard/2;
        stol[0][2] = stol [0][5] = (int)(maxX/2- sizeCard/2 + allCards[0].getHeight()*0.15 + allCards[0].getWidth()*0.6f);
    }
    void startNewGame(boolean m, boolean mod) {
        connectInternet = true;
        playning = true;
        timer = 30;
        proverckTimer = 30;
        pressPassBool = false;
        Log.d("ProblemZZZ","gameField.startNewGame");
        meNeedReturnCard = false;
        opponentMayReturnCard = false;
        returnCard = false;
//        lastGetOppon= false;
//        rubashkaGetOppon = false;
        animOpponentCard=20;
        endGameTimerBool = false;
        endGameTimer = 0;
        Log.d("MyTagsSS","testtt H O" + hodit + " " + otbivaet);
        refreshTimer();
        passChatAnim = 0;
        bitoChatAnim = 0;
        hoditCard = "";
        otbivaetCard = "";
        lenOtbiv =6;
        lenHodit =6;
        lenOpponent = 6;
        oldLenOpponent = 6;
        lenKolod =24;
        gameMaster = m;
        youWin = false;
        nichia = false;
        youLose = false;
        sizeCard = allCards[0].getWidth();
        initStol();
        modGame = mod;
//        deleteListener();
        myRef.child("HoditLen").setValue("6");
        myRef.child("OtbivaetLen").setValue("6");
        myRef.child("returnCard").addValueEventListener(returnCardListener);
        myRef.child("timer").addValueEventListener(timerListener);
        myRef.child("Hodit").addValueEventListener(prosmotrHodit);
        myRef.child("Otbivaet").addValueEventListener(prosmotrOtbivaet);
//        myRef.child("test").addValueEventListener(prosmotrPosition);
        myRef.child("test").child("bito2").addValueEventListener(prosmotrPositionBito);
        myRef.child("test").child("hodit2").addValueEventListener(prosmotrPositionPass);
        myRef.child("test").child("otbivaet2").addValueEventListener(prosmotrPositionBery);
        myRef.child("Winner").addValueEventListener(prosmotrWin);
        myRef.child("Koloda").addValueEventListener(prosmotrLenKolod);
        myRef.child("OtbivaetLen").addValueEventListener(prosmotrLenOtbiv);
        myRef.child("HoditLen").addValueEventListener(prosmotrLenHodit);
        if(gameMaster) {
            myRef.child("LenGM").setValue("6");
            myRef.child("LenNoGM").addValueEventListener(prosmotrLenOpponent);
        }
        else {
            myRef.child("LenNoGM").setValue("6");
            myRef.child("LenGM").addValueEventListener(prosmotrLenOpponent);
        }
        Log.d("MyTagsSS","testtt H O1" + hodit + " " + otbivaet);
        myRef.keepSynced(true);
        startGame = true;
        game = true;
    }
    void meNeedReturnCardFun() {
        meNeedReturnCard = true;
        myRef.child("returnCard").setValue(Long.toString(System.currentTimeMillis()/1000));
    }
    void setBool(boolean h, boolean o){
        Log.d("MyTagsSS","testtt H O2" + hodit + " " + otbivaet);
        hodit = h;
        otbivaet = o;
        if(!gameMaster&& hodit!=otbivaet){
            myRef.child("Hod").setValue("2");
        }
        if(hodit) {
            refreshTimer();
        }

    }
    protected boolean setMyCards(String cards, boolean playSound) {
        Log.d("MyTagsSS","LogSetMyCards 1 " + cards);
        myCards = cards;
        sortingCard();
        myCardsImg = new Card[myCards.length()];
        for (int i = 0; i < myCards.length(); i++){
            myCardsImg[i] = allCards[kolodaSorting.indexOf(myCards.charAt(i))];
            if(maxX>myCardsImg[i].width+myCardsImg[i].width/2*(myCardsImg.length-1)) {
                myCardsImg[i].setNeedPosX(maxX/30+maxX / 2 - myCardsImg[i].width / 2 - myCardsImg[i].width / 4 * (myCardsImg.length - 1) + myCardsImg[i].width / 2 * i);
            }
            else {
                myCardsImg[i].setNeedPosX(maxX/23 +i*((maxX-maxX/23-maxX/23-myCardsImg[i].width/2)/(myCards.length()-1)));
            }
            myCardsImg[i].setNeedPosY(maxY-maxY*2/9);
        }
        if(gameMaster){myRef.child("LenGM").setValue(Integer.toString(myCards.length()));}
        else {myRef.child("LenNoGM").setValue(Integer.toString(myCards.length()));}

        if (game) {
            winTriger();
        }

        if(playSound && mSoundPool != null && take>-1){
            mSoundPool.play(take,1,1,1,0,1);
        }
        return true;
    }
    protected boolean proverkaOtbivaemCart() {
        //проверка может ли подкидывать тот кто ходит. зависит от кол-ва карт у того кто отбивает
        int opa=0;
        for (int i = 0; i < otbivaetCard.length();i++){
//            if(kolodaSorting.indexOf(otbivaetCard.charAt(i)) != -1){
//                opa++;
//            }
            if(otbivaetCard.charAt(i) == ' ') {
                opa++;
            }
        }
//        if(opa<lenOtbiv) return true;
        if(hoditCard.length()-otbivaetCard.length()+opa<lenOtbiv){// из-за этого ошибка, можно подкинуть 2 карты если у человека 1 карта
            return true;
        }
//        if(hoditCard.length()-opa<lenOtbiv){// из-за этого ошибка, можно подкинуть 2 карты если у человека 1 карта
//            return true;
//        }
            return false;

    }
    protected void motion(MotionEvent motionEvent,float x,float y) {
//    protected void motion(MotionEvent motionEvent) {
        if( (motionEvent.getAction() & MotionEvent.ACTION_MASK) == MotionEvent.ACTION_DOWN) {
            indexTouchCards = -1;
            tempI = -1;
        }
        if (nextHod&&myCardsImg!=null && (motionEvent.getAction() & MotionEvent.ACTION_MASK) == MotionEvent.ACTION_DOWN && game && !meNeedReturnCard && !opponentReturnCard){
            touchX = (int) (motionEvent.getX()/(x/maxX));
            touchY = (int) (motionEvent.getY()/(y/maxY));
            if (touchY > maxY-allCards[0].getHeight()) {
                for (int i = myCardsImg.length -1; i >-1 && indexTouchCards ==-1 ; i--) {
                    if (myCardsImg[i].touch(touchX, touchY)) {
                        indexTouchCards = i;
                        myCardsImg[i].setPosBtnX(touchX-myCardsImg[i].getWidth()/5);
                        myCardsImg[i].setPosBtnY(touchY-myCardsImg[i].getHeight());
                    }
                }
            }
        }
        if (nextHod && (motionEvent.getAction() & MotionEvent.ACTION_MASK) == MotionEvent.ACTION_MOVE && game && !meNeedReturnCard && !opponentReturnCard){
            if(indexTouchCards != -1 &&
                    indexTouchCards<myCardsImg.length // была ошибка то что индек массива не найден. Типо защита
                    && myCardsImg[indexTouchCards]!=null) {
                myCardsImg[indexTouchCards].setPosBtnX((int) (motionEvent.getX()/(x/maxX)-myCardsImg[indexTouchCards].getWidth()/2));
                myCardsImg[indexTouchCards].setPosBtnY((int) (motionEvent.getY()/(y/maxY) - myCardsImg[indexTouchCards].getHeight()+myCardsImg[indexTouchCards].getHeight()/8));
            }
        }
        if (nextHod && (motionEvent.getAction() & MotionEvent.ACTION_MASK) == MotionEvent.ACTION_UP && game && !meNeedReturnCard && !opponentReturnCard){
            untouchX = (int)(motionEvent.getX()/(x/maxX));
            untouchY = (int)(motionEvent.getY()/(y/maxY));
            if (indexTouchCards != -1) {
                tempI = indexTouchCards;
                indexTouchCards = -1;
                if (hodit && !otbivaet) {
                    if ((untouchY < maxY - allCards[0].getHeight()) && proverkaOtbivaemCart() && (hoditCard.length() == 0 || provercaPodkid(myCardsImg[tempI].nickName)) && hoditCard.length()<6) {
                        if(hoditCard == null){//хз просто тест, а то была ошибка в этом
                            hoditCard = ""+ myCardsImg[tempI].nickName;
                        }
                        else {
                            hoditCard = hoditCard + myCardsImg[tempI].nickName;
                        }
                        tempI = -1;//обнуление результата выбора пальца
                        returnCard=true;
                        opponentMayReturnCard = false;
                        Log.d("MyTagsSShod","prosmotrHod 1");
                        myRef.child("Hodit").setValue(hoditCard);
//                        refreshTimer();
                    }
                    else {
                        tempI = -1;//обнуление результата выбора пальца
                        setMyCards(myCards, false);
                    }
                }
                if (otbivaet && !hodit) {
                    Log.d("MyTagsSS","LogMotion 3 " + hoditCard);
                    if (hoditCard != null || hoditCard.length()>0) {
                        Log.d("MyTagsSS","LogMotion 4 " + otbivaetCard);
                        if (provercaOtbivaet()) {
//                            if (untouchY<stol[1][3]+allCards[0].getHeight()/2){
//                                if(untouchX<stol[0][1]+allCards[0].height*0.1) indexChtoBiot = 0;
//                                else if(untouchX<stol[0][2]+allCards[0].height*0.1) indexChtoBiot = 1;
//                                else indexChtoBiot = 2;
//                            }
//                            else {
//                                if(untouchX<stol[0][1]+allCards[0].height*0.1) indexChtoBiot = 3;
//                                else if(untouchX<stol[0][2]+allCards[0].height*0.1) indexChtoBiot = 4;
//                                else indexChtoBiot = 5;
//
//                            }
                            if (untouchY<stol[1][3]+allCards[0].getHeight()/2){
                                if(untouchX<stol[0][1]) indexChtoBiot = 0;
                                else if(untouchX<stol[0][2]) indexChtoBiot = 1;
                                else indexChtoBiot = 2;
                            }
                            else {
                                if(untouchX<stol[0][1]) indexChtoBiot = 3;
                                else if(untouchX<stol[0][2]) indexChtoBiot = 4;
                                else indexChtoBiot = 5;

                            }
                            if (hoditCard.length()!= 0 && otbivaetCard.length() == 0 && hoditCard.length()==indexChtoBiot && hoditCard.length()<lenHodit) {
                                if(funPerevod(myCardsImg[tempI].nickName)) {
                                    returnCard = false;
                                    opponentMayReturnCard = false;
//                                    Начало защиты от одновременного перевода и подкидования
                                    hodit = true;
                                    otbivaet = false;
                                    hoditCard =hoditCard + myCardsImg[tempI].nickName;
                                    tempI = -1;//обнуление результата выбора пальца
                                    myRef.child("Hodit").setValue(hoditCard);
//                                    refreshTimer();
                                    return;
                                }
                                else {// раньше этого не было и карта зависала. Хз может помоч или нет
                                    tempI = -1;//обнуление результата выбора пальца
                                    setMyCards(myCards, false);
                                }
                            }
                            //Сложный расчёт
                            if(hoditCard.length() > indexChtoBiot) {
                                if (otbivaetCard.length()< indexChtoBiot+1) {
                                    int indexHod = kolodaSorting.indexOf(hoditCard.charAt(indexChtoBiot));
                                    int indexOtbiv = kolodaSorting.indexOf(myCardsImg[tempI].nickName);
                                    Log.d("MyTagsSS","LogMotion 7 "+ indexHod + " " + indexOtbiv);
                                    if(funOtbivProverka(indexHod,indexOtbiv)){
                                        if(otbivaetCard.length() == indexChtoBiot) {
                                            myRef.child("Otbivaet").setValue(otbivaetCard + myCardsImg[tempI].nickName);
//                                            refreshTimer();
                                            returnCard = true;
                                            opponentMayReturnCard = false;
                                            tempI = -1;//обнуление результата выбора пальца
                                            return;
                                        }
                                        else {
                                            int tempLen = otbivaetCard.length();
                                            for(int i = 0; i < indexChtoBiot-tempLen;i++) {
                                                otbivaetCard = otbivaetCard + " ";
                                            }
                                            myRef.child("Otbivaet").setValue(otbivaetCard + myCardsImg[tempI].nickName);
//                                            refreshTimer();
                                            returnCard = true;
                                            opponentMayReturnCard = false;
                                            tempI = -1;//обнуление результата выбора пальца
                                            return;
                                        }
                                    }
                                    else {
                                        tempI = -1;//обнуление результата выбора пальца
                                        setMyCards(myCards, false);
                                    }
                                }
                                else {
                                    if (otbivaetCard.charAt(indexChtoBiot) == ' ') {
//                                    if (-1 == kolodaSorting.indexOf(otbivaetCard.charAt(indexChtoBiot))) {
                                        int indexHod = kolodaSorting.indexOf(hoditCard.charAt(indexChtoBiot));
                                        int indexOtbiv = kolodaSorting.indexOf(myCardsImg[tempI].nickName);
                                        if(funOtbivProverka(indexHod,indexOtbiv)){
                                            char[] c = otbivaetCard.toCharArray();
                                            c[indexChtoBiot] = myCardsImg[tempI].nickName;
                                            tempI = -1;//обнуление результата выбора пальца
                                            myRef.child("Otbivaet").setValue(new String(c));
//                                            refreshTimer();
                                            returnCard = true;
                                            opponentMayReturnCard = false;
                                            return;
                                        }
                                        else {
                                            tempI = -1;//обнуление результата выбора пальца
                                            setMyCards(myCards, false);
                                        }

                                    }
                                    else {
                                        tempI = -1;//обнуление результата выбора пальца
                                        setMyCards(myCards, false);
                                    }
                                }
                            }
                            else {
                                tempI = -1;//обнуление результата выбора пальца
                                setMyCards(myCards, false);
                            }
                        }
                        else {
                            tempI = -1;//обнуление результата выбора пальца
                            setMyCards(myCards, false);
                        }
                    }
                    else {
                        tempI = -1;//обнуление результата выбора пальца
                        setMyCards(myCards, false);
                    }
                }
            }
        }
    }
    private boolean funOtbivProverka(int h, int o){
        if (o % 13 > h % 13 && o/13==h/13) {lastCardIndex = o;return true;}
        if (o/13==koziri && h/13 != koziri) {lastCardIndex = o;return true;}
        return false;
    }
    private boolean funPerevod(char n) {
        if (!modGame) return false;
        if (kolodaSorting.indexOf(hoditCard.charAt(0))%13 == kolodaSorting.indexOf(n) % 13) {
            return true;
        }
        return false;
    }
    private void winTriger() {
        if(myCards!=null && hoditCard!=null && otbivaetCard!=null) {
            if (timerWin != null) {
                timerWin.cancel();
                timerWin = null;
            }
            timerWin = new Timer();
            timerTaskWin = new TimerTask() {
                @Override
                public void run() {
                    if (hodit && !otbivaet && game) {
                        if (myCards.length() == 0) {
                            if (beryOpponent) {
                                if (lenKolod == 0) {
                                    Log.d("setWin", "1");
                                    setWinMe();
                                }
                            }
                            if (lenOpponent > hoditCard.length() && lenHodit == 0) {
                                if (lenKolod == 0) {
                                    Log.d("setWin", "2");
                                    setWinMe();
                                }
                            }
                            if (bitoBool) {
                                if (lenKolod == 0 && myCards.length() == 0 && 0 == lenOtbiv) {
                                    nichia = true;
                                    myRef.child("Winner").setValue("nichia");
                                    Log.d("MyTagsSS", "Reshenie Win2Nisha");
                                    game = false;
                                }
                            }
                        }
                    }
                    if (otbivaet && !hodit && game) {
                        if (lenHodit == 0 && myCards.length() > nebitieCards() && lenKolod == 0) {
                            myRef.child("gameOver").setValue("gameOver");
                            myRef.child("Winner").setValue(Boolean.toString(!gameMaster));
                        }
                        if (myCards.length() == 0) {
                            if ((lenHodit + lenKolod <= 6 && lenKolod != 0) || (lenKolod == 0 && lenHodit != 0)) {
                                Log.d("setWin", "3");
                                setWinMe();
                            } else if (lenKolod == 0 && lenHodit == 0) {
                                nichia = true;
                                myRef.child("Winner").setValue("nichia");
                                Log.d("MyTagsSS", "Reshenie Win2Nisha");
                                game = false;

                            }
                        }
                    }
                }
            };

            timerWin.schedule(timerTaskWin, 1000);
        }
    }

    protected int nebitieCards() {
        int num = 0;
        num = hoditCard.length()-otbivaetCard.length();
        for(int i =0;i<otbivaetCard.length();i++) {
//            if(kolodaSorting.indexOf(otbivaetCard.charAt(i))==-1){
//                num++;
//            }
            if(otbivaetCard.charAt(i)==' '){
                num++;
            }
        }
        return num;
    }
    protected void setWinMe() {
        Log.d("setwinme","1");
        myRef.child("Winner").setValue(Boolean.toString(gameMaster));
        youWin = true;
        game = false;

    }
    protected boolean beryBoolFun() {
        if (hoditCard==null && otbivaetCard==null) return false;
        if (hoditCard.length()==0) return false;
        if (otbivaetCard.length()!=hoditCard.length()) return true;
        if (otbivaetCard.indexOf(' ') != -1) return  true;
        return false;
    }
    protected boolean bitoFun() {
        if (hoditCard==null && otbivaetCard==null) return false;
        if((otbivaetCard.length() == hoditCard.length()) && hoditCard.length()!=0){
            if(otbivaetCard.indexOf(' ')!=-1) return false;
            if(otbivaetCard.length()== 6) {
                if(hodit && !otbivaet) {
//                    pressBito(); слишком быстрая автобита
                }
                return false;
            }
            return true;
        }
        return false;
    }
    private void initListener() {
        returnCardCoinListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d("listenerstart","returnCardCoinListener");
                refCoin.child("Coins").child(user).setValue(dataSnapshot.getValue(Long.class) - 50);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        };
        returnCardListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d("listenerstart","returnCardListener");
                if (dataSnapshot.getValue(String.class) != null){
                    if(meNeedReturnCard &&"true".equals(dataSnapshot.getValue(String.class))) {
                        returnCardFun();
                    }
                    else if(meNeedReturnCard &&"false".equals(dataSnapshot.getValue(String.class))) {
                        meNeedReturnCard = false;
                    }
                    else {
                        if(!meNeedReturnCard){
                            myRef.child("returnCard").setValue(Boolean.toString(opponentMayReturnCard));
                            if (opponentMayReturnCard) {
                                opponentReturnCard = true;
                                animOpponentCard = 0;
                            }
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        };
        winnerCoin = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d("listenerstart","winnerCoin");
                refCoin.child("Coins").child(user).setValue(dataSnapshot.getValue(Long.class) + 9);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        };
        loserCoin = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d("listenerstart","loserCoin");
                if(dataSnapshot.getValue(Long.class) > 10) {
                    refCoin.child("Coins").child(opponent).setValue(dataSnapshot.getValue(Long.class) - 10);
                } else {
                    refCoin.child("Coins").child(opponent).setValue(0l);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        };
        timerListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d("listenerstart","timerListener");
                if(dataSnapshot.getValue(Long.class)!= null){
                    proverckTimer = dataSnapshot.getValue(Long.class);
                    Log.d("TimerDeb","provercTimer "  + proverckTimer);
                    if(reOpenGameBool){
                        timer = proverckTimer;
//                        proverckTimer = timer;
                    }
//                    if(proverckTimer>15||proverckTimer%3==0) {
//                        testConnected();
//                    }
//                    if(proverckTimer < 10) {
                    if(proverckTimer < 26)
                        if(!(proverckTimer<10 && !connectInternet))
                            testConnected();
//                    }
                    if (proverckTimer>timer
//                            || dataSnapshot.getValue(Long.class)+2>=timer
                    ) {
                        timer = proverckTimer;
//                        proverckTimer = timer;
                    }
                    if(!playning){
                        timer = proverckTimer;
                        return;
                    }
                    if(proverckTimer+1<timer) {
                        if(playning && connectInternet) {
                            myRef.child("timer").setValue(timer);
                        }
                    }


                }
                if(reOpenGameBool) {
                    Log.d("hod timer", "3 "+ timer);
                    myRef.child("returnCard").addValueEventListener(returnCardListener);
                    myRef.child("Winner").addValueEventListener(prosmotrWin);
                    myRef.child("OtbivaetLen").addValueEventListener(prosmotrLenOtbiv);
                    myRef.child("HoditLen").addValueEventListener(prosmotrLenHodit);
                    if (gameMaster) {
                        myRef.child("LenNoGM").addValueEventListener(prosmotrLenOpponent);
                    } else {
                        myRef.child("LenGM").addValueEventListener(prosmotrLenOpponent);
                    }
                    myRef.child("Hodit").addValueEventListener(prosmotrHodit);
                    myRef.child("Otbivaet").addValueEventListener(prosmotrOtbivaet);
                    myRef.child("Koloda").addValueEventListener(prosmotrLenKolod);
//                    myRef.child("test").addValueEventListener(prosmotrPosition);
                    myRef.child("test").child("bito2").addValueEventListener(prosmotrPositionBito);
                    myRef.child("test").child("hodit2").addValueEventListener(prosmotrPositionPass);
                    myRef.child("test").child("otbivaet2").addValueEventListener(prosmotrPositionBery);
                    setMyCards(myCards, false);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        };
        prosmotrLenOpponent = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d("listenerstart","prosmotrLenOpponent");
                if (dataSnapshot.getValue(String.class)!=null) {
                    Log.d("lenOpponDebag",dataSnapshot.getValue(String.class) + " " +lenOpponent+ " " + animOpponentCard);
                    int tempLenOpponent = Integer.parseInt(dataSnapshot.getValue(String.class));
                    if (lenOpponent<tempLenOpponent) {
                        oldLenOpponent = lenOpponent;
                        if (animOpponentCard == 0) {
                            for(int i = oldLenOpponent; i < oldLenOpponent + (tempLenOpponent-oldLenOpponent);i++){
                                rubashka[i].setPosBtnY(0-rubashka[i].getHeight()/2);
                                rubashka[i].setPosBtnX(0);
                            }

                        }
                    }
                    Log.d("debSclae",""+ maxX + " " + maxX/3+ " " +rubashka[0].getWidth()/2+ " " + rubashka[0].getWidth()*43/100+ " " + (maxX/3-rubashka[0].getWidth()/2-rubashka[0].getWidth()*21/100));
                    for(int i = 0; i < tempLenOpponent;i++){
                        if(tempLenOpponent-1<(maxY/9-rubashka[i].getWidth())/(rubashka[i].getWidth()/2)) {
                            rubashka[i].setNeedPosX(maxY/18 - rubashka[i].getWidth()/2 - rubashka[i].getWidth()/4 * (tempLenOpponent-1) + i * rubashka[i].getWidth()/2);
                        }
                        else {
                            rubashka[i].setNeedPosX(i*(maxY/9-rubashka[i].getWidth())/(tempLenOpponent-1) );
                        }
                        rubashka[i].setNeedPosY(maxY/9-rubashka[i].getHeight()/3);
                    }
                    if(tempLenOpponent<lenOpponent){
                        for(int i = tempLenOpponent; i < tempLenOpponent+(lenOpponent-tempLenOpponent);i++){
                            rubashka[i].setPosBtnX(maxX-allCards[0].getHeight()/2);
                            rubashka[i].setPosBtnY(allCards[0].getWidth()/2);
                        }
                    }
                    lenOpponent = tempLenOpponent;
                }
                if (game) {
                    winTriger();
                }
                animOpponentCard=0;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        };
        prosmotrWin = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d("listenerstart","prosmotrWin");
                String resultWin = dataSnapshot.getValue(String.class);
                if(resultWin!=null){
                    Log.d("MyTagsSS","gameFieldWin  win? " + resultWin);
                    if("trueflag".equals(resultWin)){
                        myRef.child("gameOver").setValue("gameOver");
                        flagChatAnim = 70;
                        if(gameMaster){
                            youWin = true;
                            refCoin.child("Coins").child(user).addListenerForSingleValueEvent(winnerCoin);
                            refCoin.child("Coins").child(opponent).addListenerForSingleValueEvent(loserCoin);
                        }
                        else {
                            youLose = true;
                        }
                        game = false;
                        deleteListener();
                        return;

                    }
                    if("falseflag".equals(resultWin)){
                        myRef.child("gameOver").setValue("gameOver");
                        flagChatAnim = 70;
                        if(gameMaster){
                            youLose = true;
                        }
                        else {
                            youWin = true;
                            refCoin.child("Coins").child(user).addListenerForSingleValueEvent(winnerCoin);
                            refCoin.child("Coins").child(opponent).addListenerForSingleValueEvent(loserCoin);
                        }
                        game = false;
                        deleteListener();
                        return;
                    }
                    if("true".equals(resultWin)){
                        myRef.child("gameOver").setValue("gameOver");
                        if(gameMaster){
                            youWin = true;
                            refCoin.child("Coins").child(user).addListenerForSingleValueEvent(winnerCoin);
                            refCoin.child("Coins").child(opponent).addListenerForSingleValueEvent(loserCoin);
                        }
                        else {
                            youLose = true;
                        }
                        game = false;
                        deleteListener();
                        return;
                    }
                    if("false".equals(resultWin)){
                        myRef.child("gameOver").setValue("gameOver");
                        if(gameMaster){
                            youLose = true;
                        }
                        else {
                            youWin = true;
                            refCoin.child("Coins").child(user).addListenerForSingleValueEvent(winnerCoin);
                            refCoin.child("Coins").child(opponent).addListenerForSingleValueEvent(loserCoin);
                        }
                        game = false;
                        deleteListener();
                        return;
                    }
                    if("nichia".equals(resultWin)) {
                        myRef.child("gameOver").setValue("gameOver");
                        nichia = true;
                        game = false;
                        deleteListener();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        };
        prosmotrLenKolod = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d("listenerstart","prosmotrLenKolod");
                Log.d("MyTagsSS","GameField prosmotrLenKolod 1");
//                resetListener = false;
                if (dataSnapshot.getValue(String.class)!=null) {
                    kolodaRandom = dataSnapshot.getValue(String.class);
                    Log.d("MyTagsSS","GameField prosmotrLenKolod 2");
                }else {
                    kolodaRandom = "";
                }
                lenKolod =kolodaRandom.length();
//                if(endKoloda == null) {
//                    if(kolodaRandom.length()>0){
//                        Matrix matrix = new Matrix();
//                        matrix.postRotate(-90);
////                        Log.d("kooooooo",kolodaRandom+ " " + kolodaRandom.charAt(0)+ " " + kolodaSorting.indexOf(kolodaRandom.charAt(0)));
////                        Log.d("kooooooo2",kolodaSorting);
//                        endKoloda = Bitmap.createScaledBitmap(allCards[kolodaSorting.indexOf(kolodaRandom.charAt(0))].getBitmap(),
//                                (int)(allCards[0].width*0.6), (int)(allCards[0].height*0.6), false);
//                        endKoloda = Bitmap.createBitmap(endKoloda,0,0,endKoloda.getWidth(),endKoloda.getHeight(),matrix,false);
//                    }
//                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        };
        prosmotrLenHodit = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d("listenerstart","prosmotrLenHodit");
                if (dataSnapshot.getValue(String.class)!=null) {
                    lenHodit = Integer.parseInt(dataSnapshot.getValue(String.class));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {


                //setMyCards("1");
            }
        };
        prosmotrLenOtbiv = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d("listenerstart","prosmotrLenOtbiv");
                if (dataSnapshot.getValue(String.class)!=null) {
                    lenOtbiv = Integer.parseInt(dataSnapshot.getValue(String.class));
                    Log.d("LenOtbiv","List" + lenOtbiv+ " " );
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        };
        prosmotrPositionBito = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d("listenerstart","prosmotrPositionBito");
                if("bito".equals(dataSnapshot.getValue(String.class))!=tempBito) {
                    tempBito = "bito".equals(dataSnapshot.getValue(String.class));
                    refreshTimer();
                }
                else{
                    return;
                }
                if(tempBito) {
                    bitoBool = false;
                    beryBool = false;
                    returnCard = false;
                    opponentMayReturnCard = false;
                    animOpponentCard = 20;
                    if(!singRasdachaBito){
                        bitoChatAnim = 40;
                        Log.d("MyTagsSS","prosmotrPosition 1 " + myCards);
                        hodit = true;
                        otbivaet = false;
                        for(int i = 0; i < otbivaetCard.length();i++) {
                            myCards = myCards.replaceAll(String.valueOf(otbivaetCard.charAt(i)),"");
                            myCards = myCards.replaceAll(String.valueOf(hoditCard.charAt(i)),"");
                            Log.d("MyTagsSS","prosmotrPosition 2 ");
                        }
                        Log.d("MyTagsSS","prosmotrPosition 3 " + myCards);
                        rasdachaCart();
                        stolImageCardsBita();
                        myRef.child("HoditLen").setValue(Integer.toString(myCards.length()));
                        oldMyCards = myCards;
                        setMyCards(myCards, true);
                        hoditCard = "";
                        otbivaetCard = "";
                        myRef.child("Otbivaet").removeValue();// надо это оставить, а то сталкиваюсь с ошибкой зависание карты когда отбиваюсь одной и той-же
                        myRef.child("Hodit").removeValue();//это сделано для перезахода в игру надо чтбы на сервере были свежие данные
                        myRef.child("test").removeValue();
                    }
                    else {
                        singRasdachaBito = false;
                    }
                    Log.d("GameFieldREst","BitoListener hodit: " + hodit);
//                    refreshTimer();
                }
                else {
//                    hoditCard = "";
//                    otbivaetCard = "";
                    nextHod = true;
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        prosmotrPositionBery = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d("listenerstart","prosmotrPositionBery");
                if("bery".equals(dataSnapshot.getValue(String.class))!=tempBery) {
                    tempBery = "bery".equals(dataSnapshot.getValue(String.class));
                    refreshTimer();
                }
                else{
                    return;
                }
                if(tempBery) {
//                  ДУбль анимации
                    animOpponentCard = 20;
                    returnCard = false;
                    opponentMayReturnCard = false;
                    if(hodit) beryOpponent = true;
//                    refreshTimer();
                    if(game) {
//                        winTriger();
                        if (otbivaet && !hodit && game) {
                            if (lenHodit == 0 && lenKolod == 0) {
                                myRef.child("gameOver").setValue("gameOver");
                                myRef.child("Winner").setValue(Boolean.toString(!gameMaster));
                            }
                        }
                    }

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        prosmotrPositionPass = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d("listenerstart","prosmotrPositionPass");
                if("pass".equals(dataSnapshot.getValue(String.class))!=tempPass) {
                    tempPass = "pass".equals(dataSnapshot.getValue(String.class));
                    refreshTimer();
                }
                else{
                    return;
                }
                if(tempPass) {
                    bitoBool = false;
                    beryBool = false;
                    beryyyyy = false;
                    returnCard = false;
                    opponentMayReturnCard = false;
                    Log.d("MyTagsSS","LogMotionT 1 ");
                    if (otbivaet && !hodit) {
                        animOpponentCard = 20;
                        passChatAnim = 40;
                        beryyyyy = false;
                        nextHod = true;
                        Log.d("MyTagsSS","LogMotionT 2 ");
                        //passBool = true;
                        myCards = myCards + hoditCard + otbivaetCard;
                        oldMyCards = myCards;
                        myRef.child("OtbivaetLen").setValue(Integer.toString(myCards.length()));
                        setMyCards(myCards, true);
                        stolImageCardsBery();
                        Log.d("MyTagsSS","LogMotionT 3 ");
                        hoditCard = "";
                        otbivaetCard = "";
//                        oldMyCards = myCards;
//                        setMyCards(myCards);
                        myRef.child("Otbivaet").removeValue();// надо это оставить, а то сталкиваюсь с ошибкой зависание карты когда отбиваюсь одной и той-же
                        myRef.child("Hodit").removeValue();//это сделано для перезахода в игру надо чтбы на сервере были свежие данные
                        myRef.child("test").removeValue();//Это только тут. т.к. тот кто отбивает получает сигнал намного позже
                    }
//                    else {
////                        pressPass();
//
////                        myRef.child("Koloda").addListenerForSingleValueEvent(rasdachaPass);
//
//                    }
//                    refreshTimer();
                }
                else {
//                    hoditCard = "";
//                    otbivaetCard = "";
                    pressPassBool = false;
                    beryOpponent = false;
                    nextHod = true;
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        prosmotrHodit = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d("listenerstart","prosmotrHodit");
                dataCardsHodit = dataSnapshot.getValue(String.class);

                if (timerBito != null) {
                    timerBito.cancel();
                    timerBito = null;
                }
                if (reOpenGameBool){// сделать это до return
                    Log.d("MyTagsSS","prosmotrHod restart");
                    if(dataCardsHodit!=null){
                        if (hoditCard.length() < dataCardsHodit.length()) {
                            otbivaet = true;
                            hodit = false;
                            beryBool = beryBoolFun();
                            bitoBool = false;
                        }

                        hoditCard = dataCardsHodit;
                        for (int i = 0; i< hoditCard.length();i++){
                            stolImageCards[i] = allCards[kolodaSorting.indexOf(hoditCard.charAt(i))];
                            stolImageCards[i].setPosBtnX(stol[0][i]);
                            stolImageCards[i].setPosBtnY(stol[1][i]);
                        }
                        returnCard = false;
                    }
                    else {
//                        otbivaet = !otbivaet;//получается глюки после перезахода
//                        hodit = !hodit;
                        hoditCard = "";
                        otbivaetCard = "";
                    }
//                    refreshTimer();
                    return;
                }
                if(dataCardsHodit==null) {
                    dataCardsHodit = "";
                    return;//это сделано для перезахода в игру надо чтбы на сервере были свежие данные
                }
//                if(dataCardsHodit==null) dataCardsHodit = "";
                if(dataCardsHodit.equals(tempHoditCard)) return;
                if(mSoundPool != null && step>-1){
                    mSoundPool.play(step,1,1,1,0,1);
                }
                tempHoditCard = dataCardsHodit;
                refreshTimer();
//                if (reOpenGameBool){
//                    Log.d("MyTagsSS","prosmotrHod restart");
//                    if(dataCardsHodit!=null){
//                        if (hoditCard.length() < dataCardsHodit.length()) {
//                            otbivaet = true;
//                            hodit = false;
//                        }
//
//                        hoditCard = dataCardsHodit;
//                        for (int i = 0; i< hoditCard.length();i++){
//                            stolImageCards[i] = allCards[kolodaSorting.indexOf(hoditCard.charAt(i))];
//                            stolImageCards[i].setPosBtnX(stol[0][i]);
//                            stolImageCards[i].setPosBtnY(stol[1][i]);
//                        }
//                        returnCard = false;
//                    }
//                    else {
//                        hoditCard = "";
//                    }
////                    refreshTimer();
//                    return;
//                }
                if(meReturnCard){
                    meReturnCard = false;
                    bitoBool = bitoFun();
                    beryBool = beryBoolFun();
//                    refreshTimer();
                    return;
                }
                if(dataCardsHodit.length()>1 && hodit && !dataCardsHodit.equals(hoditCard) ) {
                    int error = -1;
                    for(int i = 1; i<hoditCard.length() && i < dataCardsHodit.length();i++){
                        if(dataCardsHodit.charAt(i)!=hoditCard.charAt(i)){
                            error = i-1;
                            break;
                        }
                    }
                    if (error != -1) {//это проверка на простои перевод, елси ошибок в несовпадении данных нет, значит они просто добавились
                        //Значить просто перевли карту
                        if (oldMyCards.indexOf(dataCardsHodit.charAt(error)) > -1) {
                            //значит я хожу и настаиваю на своём ходе
                            hodit = true;
                            otbivaet = false;
                            animOpponentCard = 0; //убрать анимацию у оппонента
                            myRef.child("Hodit").setValue(hoditCard);
//                            refreshTimer();
                            return;
                        }
                        else {
                            //Значит я переводил и вынужден вернуть карты
                            for (int i = error + 1; i < hoditCard.length(); i++) {
                                myCards = myCards + hoditCard.charAt(i);
                            }
                            hodit = false;
                            otbivaet = true;
                            myRef.child("OtbivaetLen").setValue(Integer.toString(myCards.length()));
                            setMyCards(myCards,false);
//                                    return;
                        }
                    }
                }
                if(hoditCard.length()!=0) oldHoditCard = hoditCard;
                else oldHoditCard = dataCardsHodit;
                hoditCard = dataCardsHodit;
                bitoBool = bitoFun();
                beryBool = beryBoolFun();
                if (otbivaet && !hodit) {
                    returnCard = false;
                    opponentMayReturnCard = true;
                }
                if (opponentReturnCard){
                    returnCard = false;
                    opponentMayReturnCard = false;
                    opponentReturnCard = false;
                    meReturnCard = false;
                    if (otbivaet && !hodit) {
                        stolImageCards[oldHoditCard.length() - 1].setNeedPosX(0);
                        stolImageCards[oldHoditCard.length() - 1].setNeedPosY(-allCards[0].getHeight() * 2);
                    }
//                    refreshTimer();
                    return;
                }
                if (hoditCard.length() != 0) {
                    for(int i = 0; i < hoditCard.length();i++) {
                        stolImageCards[i] = allCards[kolodaSorting.indexOf(hoditCard.charAt(i))];
                        if(stolImageCards[i].needPosX != stol[0][i] || stolImageCards[i].needPosY != stol[1][i]) {
                            if(oldMyCards.indexOf(hoditCard.charAt(i)) == -1){
                                stolImageCards[i].setPosBtnX(0);
                                stolImageCards[i].setPosBtnY(0 - allCards[0].height / 2);
                            }
                            stolImageCards[i].setNeedPosX(stol[0][i]);
                            stolImageCards[i].setNeedPosY(stol[1][i]);
                        }
                    }
                    if(oldMyCards.indexOf(hoditCard.charAt(hoditCard.length()-1)) == -1) {
                        if (hodit && !otbivaet) {
                            returnCard = false;
                            opponentMayReturnCard = false;
                        }
                        hodit = false;
                        otbivaet = true;
                        myRef.child("OtbivaetLen").setValue(Integer.toString(myCards.length()));

                    }
                    else {
//                                Если ты попал сюда и ты раньше отбивал. Значит ты перевёл карту
                        if (otbivaet && !hodit) {
                            returnCard = false;
                            opponentMayReturnCard = false;
                        }
                        hodit = true;
                        otbivaet = false;
//                                    вернуться
                        myCards = myCards.replaceAll(String.valueOf(hoditCard.charAt(hoditCard.length()-1)),"");
                        myRef.child("HoditLen").setValue(Integer.toString(myCards.length()));
                        setMyCards(myCards,false);

                    }

//                    refreshTimer();
                    if (game) {
                        winTriger();
                    }
                }
            }

            @Override
            public void onCancelled( DatabaseError databaseError) {

                ////setMyCards("1");
            }
        };
        prosmotrOtbivaet = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d("listenerstart","prosmotrOtbivaet");
                dataCardOtbivaet = dataSnapshot.getValue(String.class);

                if (timerBito != null) {
                    timerBito.cancel();
                    timerBito = null;
                }
                if(reOpenGameBool){ // сделать это до return
                    if (dataCardOtbivaet != null) {
                        otbivaetCard = dataCardOtbivaet;
                        for (int i = 0; i<otbivaetCard.length();i++) {
                            if (dataCardOtbivaet.charAt(i) != ' ') {
                                stolImageCards[i + 6] = allCards[kolodaSorting.indexOf(dataCardOtbivaet.charAt(i))];
                                stolImageCards[i + 6].setPosBtnX(stol[0][i] + sizeCard / 10);
                                stolImageCards[i + 6].setPosBtnY(stol[1][i] + sizeCard / 10);
                            }
                        }
                    }
                    else {
                        otbivaetCard = "";
                    }
                    beryBool = beryBoolFun();
                    bitoBool = bitoFun();
                    startGame = true;
                    reOpenGameBool = false;
                    returnCard = false;
                }
//                if(dataCardOtbivaet==null) dataCardOtbivaet = "";
                if(dataCardOtbivaet==null) {
                    tempOtbivaetCard = "";
                    return;
                }
                if(dataCardOtbivaet.equals(tempOtbivaetCard)) return;
                if(mSoundPool != null && step>-1){
                    mSoundPool.play(step,1,1,1,0,1);
                }
                refreshTimer();
                tempOtbivaetCard = dataCardOtbivaet;

                if(hodit && !otbivaet) {
                    returnCard = false;
                    opponentMayReturnCard = true;
                }
                if(meReturnCard) meReturnCard = false;
                if (opponentReturnCard){
                    returnCard = false;
                    opponentMayReturnCard = false;
                    opponentReturnCard = false;
                    meReturnCard = false;
                    if (!otbivaet && hodit) {
                        Log.d("testRetOtbiv","1 " + otbivaetCard.length() + " " + dataCardOtbivaet.length());
                        for(int i = 0; i< otbivaetCard.length();i++) {
                            Log.d("testRetOtbiv","2 "+ i + " |" + otbivaetCard.charAt(i) + "|" + dataCardOtbivaet.charAt(i)+ "| "+(otbivaetCard.charAt(i)!=dataCardOtbivaet.charAt(i)));
                            if(otbivaetCard.charAt(i)!=dataCardOtbivaet.charAt(i)) {
                                //если я бью, а тот кто отбивает вернул карту, то надо отобразить возврат карты ему
//                                stolImageCards[i + 6].setNeedPosX((maxX - allCards[0].getWidth()) / 2);
                                stolImageCards[i + 6].setNeedPosX(0);
                                stolImageCards[i + 6].setNeedPosY(-allCards[0].getHeight() * 2);

                                otbivaetCard = dataCardOtbivaet;
                                bitoBool = bitoFun();
//                                refreshTimer();
                                return;
                            }
                        }
                    }

                    beryBool = beryBoolFun();
//                    refreshTimer();
                    return;
                }
                if (dataCardOtbivaet.length()!=0){
                    for(int i = 0; i<dataCardOtbivaet.length();i++){
                        if (dataCardOtbivaet.charAt(i) != ' ') {
//                            if(myCards.indexOf(dataCardOtbivaet.charAt(i))!=-1) {
                                myCards = myCards.replaceAll(String.valueOf(dataCardOtbivaet.charAt(i)),"");
//                            }
                            stolImageCards[i + 6] = allCards[kolodaSorting.indexOf(dataCardOtbivaet.charAt(i))];

                            if(stolImageCards[i + 6].needPosX != (stol[0][i] + sizeCard / 10) || stolImageCards[i + 6].needPosY != (stol[1][i] + sizeCard / 10)) {
                                if (hodit && !otbivaet) {
                                    stolImageCards[i + 6].setPosBtnX(0);
                                    stolImageCards[i + 6].setPosBtnY(0 - allCards[0].height / 2);
                                }
                                stolImageCards[i + 6].setNeedPosX(stol[0][i] + sizeCard / 10);
                                stolImageCards[i + 6].setNeedPosY(stol[1][i] + sizeCard / 10);
                            }
                        }
                    }
                    if(!hodit && otbivaet)
                        myRef.child("OtbivaetLen").setValue(Integer.toString(myCards.length()));
                    setMyCards(myCards,false);

                    otbivaetCard = dataCardOtbivaet;
                    for(int i = otbivaetCard.length()-1; i>-1;i--){
                        if(otbivaetCard.charAt(i)==' '){
                            otbivaetCard = otbivaetCard.substring(0,otbivaetCard.length()-1);
                        }
                        else
                            break;
                    }
                    beryBool = beryBoolFun();
                    bitoBool = bitoFun();
//                    refreshTimer();
                    if (game) {
                        winTriger();
//                            автобита попытка 2
                        if(hodit&&!otbivaet ) {
                            if (timerBito != null) {
                                timerBito.cancel();
                                timerBito = null;
                            }
                            timerBito = new Timer();
                            timerTaskBito = new TimerTask() {
                                @Override
                                public void run() {
                                    if (hodit && !otbivaet) {//двойная проверка, а то вдруг за секунду что-то изменилось.
                                        if (((hoditCard.length() == 6||lenHodit == 0 ||lenOtbiv == 0) && hoditCard.length() == otbivaetCard.length() && otbivaetCard.indexOf(' ')==-1)
//                                        if (((hoditCard.length() == 6||lenHodit == 0) && hoditCard.length() == otbivaetCard.length() && otbivaetCard.indexOf(' ')==-1) ||lenOtbiv == 0 // было так и из-за эого пошла ошибка внизу
//                                                || (lenHodit == 0 && hoditCard.length() == otbivaetCard.length() && otbivaetCard.indexOf(' ')==-1)
                                        ) {
//                                            for (int i = 0; i < hoditCard.length(); i++) {// это есть в прес бито
//                                                myCards = myCards.replaceAll(String.valueOf(otbivaetCard.charAt(i)), "");
//                                                странно ^ здесь ^ ошибка. типо отбиваетКард лен 1, а здесь индекс 1(должен 0)
//                                                ошибка банальная. Т.е. ленОтбив пришёл = 0 а отбив карт нет вот и индекс не совпал
//                                                myCards = myCards.replaceAll(String.valueOf(hoditCard.charAt(i)), "");
//                                            }
                                            pressBito();
                                        }
                                    }
                                }
                            };
                            timerBito.schedule(timerTaskBito, 1500);
                        }
                    }
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {


                //setMyCards("1");
            }
        };
    }

    private int sortingCard() {
        String tempString="";
        for (int i = 0; i < koziri*13; i++) {
            if(myCards.indexOf(kolodaSorting.charAt(i)) != -1) {
                tempString = tempString + kolodaSorting.charAt(i);
            }
        }
        for (int i = (koziri+1)*13; i < kolodaSorting.length(); i++){
            if(myCards.indexOf(kolodaSorting.charAt(i)) != -1) {
                tempString = tempString + kolodaSorting.charAt(i);
            }
        }
        for (int i = koziri*13; i < (koziri+1)*13; i++){
            if(myCards.indexOf(kolodaSorting.charAt(i)) != -1) {
                tempString = tempString + kolodaSorting.charAt(i);
            }
        }
        myCards = tempString;
        return 0;
    }


    private boolean provercaOtbivaet() {
        if(hoditCard.length() == otbivaetCard.length() && otbivaetCard.indexOf(' ') == -1) return false;
        return true;
//        int tempIp = 0;
//        for (int i = 0; i < otbivaetCard.length(); i++) {
//            if (kolodaSorting.indexOf(otbivaetCard.charAt(i)) != -1) {
//                tempIp++;
//            }
//        }
//        if (tempIp < hoditCard.length()) return true;
//        return false;
    }

    private boolean provercaPodkid(char proverk) {
        for (int i = 0; i < hoditCard.length(); i++) {
            if (kolodaSorting.indexOf(hoditCard.charAt(i))%13 == kolodaSorting.indexOf(proverk) % 13) {
                return true;
            }
        }
        for (int i = 0; i < otbivaetCard.length(); i++) {
            Log.d("MyTagsSS","proverka " + kolodaSorting.indexOf(proverk) % 13 + " " + kolodaSorting.indexOf(otbivaetCard.charAt(i))%13);
            if (kolodaSorting.indexOf(proverk) % 13 == kolodaSorting.indexOf(otbivaetCard.charAt(i))%13) {
                return true;
            }
        }
        return false;
    }

    protected void stolImageCardsBita () {
        for(int i = 0;i<stolImageCards.length; i++){
            if(stolImageCards[i]!=null){
                stolImageCards[i].setNeedPosX(-stolImageCards[i].getWidth()*2);
                stolImageCards[i].setNeedPosY(stolImageCards[i].getPosY());// так надо, у меня там так расчитывается
            }
        }
    }
    protected void stolImageCardsPass () {
        for(int i = 0;i<stolImageCards.length; i++){
            if(stolImageCards[i]!=null){
                stolImageCards[i].setNeedPosX(-stolImageCards[i].getWidth());
                stolImageCards[i].setNeedPosY(0-stolImageCards[i].getHeight()*2);
            }
        }
    }
    protected void returnCardFun(){
        meReturnCard = true;
        refCoin.child("Coins").child(user).addListenerForSingleValueEvent(returnCardCoinListener);
        //Надо обнулить возврат карт
        if(otbivaet && !hodit){
            myCards = myCards+(kolodaSorting.charAt(lastCardIndex));
            otbivaetCard = otbivaetCard.substring(0,otbivaetCard.indexOf(kolodaSorting.charAt(lastCardIndex))) + " " +otbivaetCard.substring(otbivaetCard.indexOf(kolodaSorting.charAt(lastCardIndex))+1);

            myRef.child("Otbivaet").setValue(otbivaetCard);
            myRef.child("OtbivaetLen").setValue(Integer.toString(myCards.length()));
            setMyCards(myCards,false);
            lastCardIndex = -1;

        }
        else {
            myCards = myCards+hoditCard.charAt(hoditCard.length()-1);
            hoditCard = hoditCard.substring(0,hoditCard.length()-1);
            myRef.child("Hodit").setValue(hoditCard);
            myRef.child("HoditLen").setValue(Integer.toString(myCards.length()));
            setMyCards(myCards,false);
        }
        meNeedReturnCard = false;
        returnCard = false;
    }
    protected void stolImageCardsBery () {
        for(int i = 0;i<stolImageCards.length; i++){
            if(stolImageCards[i]!=null){
                stolImageCards[i]=null;
            }
        }
    }
//
    protected void pressBery() {
       if(!beryyyyy && beryBool&& !meReturnCard&&!opponentReturnCard) {
            opponentMayReturnCard=false;
            nextHod = false;
            beryyyyy = true;
            myRef.child("test").child("otbivaet2").setValue("bery");
       }
    }
    protected void pressPass() {
        if(!pressPassBool && !meReturnCard&&!opponentReturnCard) {
            opponentMayReturnCard=false;
            pressPassBool = true;
            nextHod = false;
            animOpponentCard = 0;
            stolImageCardsPass();
            for (int i = 0; i < hoditCard.length(); i++) {
                myCards = myCards.replaceAll(String.valueOf(hoditCard.charAt(i)), "");
            }
            for (int i = 0; i < otbivaetCard.length(); i++) {
                myCards = myCards.replaceAll(String.valueOf(otbivaetCard.charAt(i)), "");
            }
            beryyyyy = false;
//        kolodaRandom = dataSnapshot.getValue(String.class);
//        if(kolodaRandom!=null) {
//            if (myCards.length()<6) {
//                while (myCards.length() < 6 && kolodaRandom.length() > 0) {
//                    char i = kolodaRandom.charAt(kolodaRandom.length() - 1);
//                    myCards = myCards + i;
//                    kolodaRandom = kolodaRandom.replaceAll(String.valueOf(i), "");
//                }
//            }
//        }
//        myRef.child("Koloda").setValue(kolodaRandom);
            rasdachaCart();
//        if (!hodit && otbivaet) myRef.child("OtbivaetLen").setValue(Integer.toString(myCards.length()));
            if (hodit && !otbivaet)
                myRef.child("HoditLen").setValue(Integer.toString(myCards.length()));
            myRef.child("test").child("hodit2").setValue("pass");
            otbivaetCard = "";
            hoditCard = "";
            oldMyCards = myCards;
            setMyCards(myCards,true);
        }
    }
    protected void pressBito() {
        if(timerBito != null) {
            timerBito.cancel();
            timerBito = null;
        }
        if(!meReturnCard&&!opponentReturnCard) {
            opponentMayReturnCard=false;
            nextHod = false;
            bitoBool = false;
            singRasdachaBito = true;
            hodit = false;
            otbivaet = true;
            for (int i = 0; i < hoditCard.length(); i++) {
                myCards = myCards.replaceAll(String.valueOf(hoditCard.charAt(i)), "");
                myCards = myCards.replaceAll(String.valueOf(otbivaetCard.charAt(i)), "");
            }
            rasdachaCart();
            otbivaetCard = "";
            hoditCard = "";
            animOpponentCard = 20;//дубль проверка
            myRef.child("OtbivaetLen").setValue(Integer.toString(myCards.length()));
            myRef.child("test").child("bito2").setValue("bito");
            stolImageCardsBita();
            oldMyCards = myCards;
            setMyCards(myCards,true);
        }

        Log.d("GameFieldREst","pressBito hodit: " + hodit);
    }
    protected void rasdachaCart() {
        Log.d("MyTagsSS","rasdachaCart() 1 ");
        if(kolodaRandom!=null) {
            if (myCards.length()<6) {
                while (myCards.length() < 6 && kolodaRandom.length() > 0) {
                    char i = kolodaRandom.charAt(kolodaRandom.length() - 1);
                    myCards = myCards + i;
                    kolodaRandom = kolodaRandom.replaceAll(String.valueOf(i), "");
                }
            }
        }
        myRef.child("Koloda").setValue(kolodaRandom);
    }
    protected void deleteListener() {
        if(myRef != null) {
            myRef.keepSynced(false);
            myRef.child("returnCard").removeEventListener(returnCardListener);
            myRef.child("Hodit").removeEventListener(prosmotrHodit);
            myRef.child("Otbivaet").removeEventListener(prosmotrOtbivaet);
//        myRef.child("test").removeEventListener(prosmotrPosition);
            myRef.child("test").child("bito2").removeEventListener(prosmotrPositionBito);
            myRef.child("test").child("hodit2").removeEventListener(prosmotrPositionPass);
            myRef.child("test").child("otbivaet2").removeEventListener(prosmotrPositionBery);
            myRef.child("timer").removeEventListener(timerListener);
            myRef.child("Winner").removeEventListener(prosmotrWin);
            myRef.child("Koloda").removeEventListener(prosmotrLenKolod);
            myRef.child("OtbivaetLen").removeEventListener(prosmotrLenOtbiv);
            myRef.child("HoditLen").removeEventListener(prosmotrLenHodit);
            myRef.child("LenNoGM").removeEventListener(prosmotrLenOpponent);
            myRef.child("LenGM").removeEventListener(prosmotrLenOpponent);
        }
    }
    protected void refreshTimer() {
//        try {
//            if(isConnected()) {
//        testConnected();
        timer = 30;
        myRef.child("timer").setValue(30l);
//            }
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

    }
    protected void refreshPositionStol(){
        for(int i = 0; i < 6; i++) {
            if(stolImageCards[i]!=null) {
                stolImageCards[i].setNeedPosX(stol[0][i]);
                stolImageCards[i].setNeedPosY(stol[1][i]);
            }
            if(stolImageCards[i + 6]!=null) {
                stolImageCards[i+6].setNeedPosX(stol[0][i] + sizeCard/10);
                stolImageCards[i+6].setNeedPosY(stol[1][i] + sizeCard/10);

            }
        }
    }
    protected void resetListeners() {
//        resetListener = true;
        if(myRef!=null) {
            myRef.child("Hodit").removeEventListener(prosmotrHodit);
            myRef.child("Otbivaet").removeEventListener(prosmotrOtbivaet);
            myRef.child("test").child("bito2").removeEventListener(prosmotrPositionBito);
            myRef.child("test").child("hodit2").removeEventListener(prosmotrPositionPass);
            myRef.child("test").child("otbivaet2").removeEventListener(prosmotrPositionBery);
            myRef.child("timer").removeEventListener(timerListener);
            myRef.child("Winner").removeEventListener(prosmotrWin);
            myRef.child("Koloda").removeEventListener(prosmotrLenKolod);

            myRef.child("Hodit").addValueEventListener(prosmotrHodit);
            myRef.child("Otbivaet").addValueEventListener(prosmotrOtbivaet);
            myRef.child("test").child("bito2").addValueEventListener(prosmotrPositionBito);
            myRef.child("test").child("hodit2").addValueEventListener(prosmotrPositionPass);
            myRef.child("test").child("otbivaet2").addValueEventListener(prosmotrPositionBery);
            myRef.child("timer").addValueEventListener(timerListener);
            myRef.child("Winner").addValueEventListener(prosmotrWin);
            myRef.child("Koloda").addValueEventListener(prosmotrLenKolod);
        }
    }
    protected void testConnected() {
        Log.d("internetTest","1");
//        if(endTestConnect-startTestConnect<2000) connectInternet = true;
//        else
//        connectInternet = (endTestConnect-startTestConnect<2000);
        if (threadTestConnect == null || threadTestConnect.getState() == Thread.State.TERMINATED) {
            Log.d("internetTest","2");
            startTestConnect = System.currentTimeMillis();
//                threadTestConnect = new Thread(null, isConnected(), "testConnected");
            threadTestConnect = new Thread(new Runnable() {
                @Override
                public void run() {
//                    try {
                        isConnected();
//                    }catch (Exception e) {
//                        connectInternet = false;
//                    }
                }
            });
            threadTestConnect.start();
        }
        Log.d("internetTest",(threadTestConnect != null) + " " + (threadTestConnect.getState() != Thread.State.TERMINATED) + " " +((System.currentTimeMillis() - startTestConnect) > 2000));
//        if (threadTestConnect != null && threadTestConnect.getState() != Thread.State.TERMINATED && (System.currentTimeMillis() - startTestConnect) > 2000) {
//            connectInternet = false;
//            try {
//                threadTestConnect.join();
//            } catch (Exception e) {
//                Log.e("Error joining thread  ", e.toString());
//            }
//            Log.d("internetTest","3");
////            threadTestConnect.stop();
//        }

    }
    public void isConnected() {
//        public void isConnected() throws InterruptedException, IOException {
//        final String command = "ping -c 1 google.com";

        try {
//            int timeoutMs = 1500;
            Socket sock = new Socket();
            SocketAddress sockaddr = new InetSocketAddress("8.8.8.8", 53);

            sock.connect(sockaddr, 1000);
            sock.close();
            connectInternet = true;
        } catch (IOException e) {
            connectInternet = false;
        }
//        final String command = "ping -c 1 8.8.8.8";
//        Log.d("internetTest","4");
//        if(Runtime.getRuntime().exec(command).waitFor() == 0){
//            endTestConnect = System.currentTimeMillis();
//            connectInternet = (endTestConnect-startTestConnect<2000);
//            Log.d("internetTest","5" + connectInternet);
//            return true;
//        }
//        connectInternet = false;
//        Log.d("internetTest","6");
//        return false;

//        return Runtime.getRuntime().exec(command).waitFor() == 0;
//        final String command = "ping -c 1 8.8.8.8";
//        return Runtime.getRuntime().exec("ping -c 1 8.8.8.8").waitFor() == 0;
//        public static boolean isOnline(Context context)
//        {
//        cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
//        netInfo = cm.getActiveNetworkInfo();
//        if (netInfo != null && netInfo.isConnectedOrConnecting())
//        {
//            if(netInfo.getType() == ConnectivityManager.TYPE_WIFI){
////                    final String command = "ping -c 1 google.com";
//                return true;
////                return Runtime.getRuntime().exec("ping -c 1 8.8.8.8").waitFor() == 0;
//            }
//            return true;
//        }
//        return false;
    }

//    public void seeGameEnd() {
//        myRef.child("gameOver").addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                if("false".equals(dataSnapshot.getValue(String.class))) {
////                    setWinMe();
//                    Log.d("gameOverFalse","proverka");
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
//    }
    protected void setSound(int title, int data, SoundPool soundPool) {
        switch (title){
            case 0:
                break;
            case 1:
                take = data;
                if(mSoundPool==null) {
                    mSoundPool = soundPool;
                }
                break;
            default:
                step = data;
                if(mSoundPool==null) {
                    mSoundPool = soundPool;
                }
                break;
        }
    }
}
