package ru.tmkstd.cardgamedurakonline;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.SoundPool;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class StartGame {
    String opponent, userId;
    String meCards = "";
    String tempString = "";
    int koziri;
    int minI;
    char randomCard;
    boolean gameMaster, resetGame;
    boolean hodit,otbivaet;
    int numberCards;
    boolean modGame;
    int numEmojiClick;
    int animEmojiClick;
    int pressStartAnim;
    boolean pressStartAnimBool;
    GameField gameField;
    ValueEventListener razdacha, firstHod, nachalnayaRazdacha,resetList;
    ValueEventListener timerListener, gameOverListener, winnerListener;
    ValueEventListener animListener, smilesListener;
    long smilesCounts;
    FirebaseDatabase database;
    DatabaseReference myRef;
    DatabaseReference myRef2;
    String koloda52, randomKoloda = "", kolodaPoVozr;
    String[] Cards = new String[4];
    Card[] rubashka,allCards;
    Context context;
    Bitmap endKoloda;
    SoundPool mSoundPool;

    int mix=-1, take=-1, step=-1;

    public StartGame(boolean mod, int num, Card[] rubashka, Card[] allCards, Context context) {

        smilesCounts = 0;
        numberCards = num;
        modGame = mod;
        numEmojiClick= -1;
        animEmojiClick = 0;
        this.allCards = allCards;
        this.rubashka = rubashka;
        this.context = context;
        Cards[0] = "048cgkoswAEIM";
        Cards[1] = "159dhlptxBFJN";
        Cards[2] = "26aeimquyCGKO";
        Cards[3] = "37bfjnrvzDHLP";
        kolodaPoVozr = "MIEAwsokgc840NJFBxtplhd951OKGCyuqmiea62PLHDzvrnjfb73";
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference();
//        gameField = new GameField(rubashka,context);
        koloda52 = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOP";
        pressStartAnim = 0;
        pressStartAnimBool = false;
        InitListener();
        InitListenerReopen();

/*
        bubi trefi jervi vini
        bubi kresti chervi piki
        0 tb 1 tk 2 tc 3 tp
        4 kb 5 kk 6 kc 7 kp
        8 qb 9 qk a qc b qp
        c jb d jk e jc f jp
        g 0b h 0k i 0c j 0p
        k 9b l 9k m 9c n 9p
        o 8b o 8k q 8c r 8p
        s 7b t 7k u 7c v 7p
        w 6b x 6k y 6c z 6p
        ! 5b @ 5k # 5c $ 5p
        % 4b ^ 4k & 4c * 4p
        ( 3b ) 3k _ 3c + 3p
        - 2b = 2k [ 2c ] 2p
         */
    }

    protected void  reOpenGame(boolean gm,String o, String u,int x,int y,boolean hodit,boolean otbivaet, String myCards, String oldMyCards, String hoditCard,String otbivaetCard, int koz, int timer){
        gameField = new GameField(rubashka,context);
        if(take > -1)
            setSound(1,take);
        if(step > -1)
            setSound(2,step);
        resetGame = false;
        gameMaster = gm;
        opponent = o;
        userId = u;
        meCards = myCards;
        this.hodit = hodit;
        this.otbivaet = otbivaet;
        pressStartAnimBool = false;
        if(gm){
            myRef2 = myRef.child("GameRoom").child(userId+opponent);

        }else {
            myRef2 = myRef.child("GameRoom").child(opponent+userId);
        }
        gameField.user = userId;
        gameField.opponent = opponent;
        gameField.koziri = koz;
        gameField.myCards = myCards;
        gameField.oldMyCards = oldMyCards;
        gameField.hodit = hodit;
        gameField.otbivaet = otbivaet;
        gameField.otbivaetCard = otbivaetCard;
        gameField.hoditCard = hoditCard;
        gameField.allCards = allCards;
        gameField.maxX = x;
        gameField.maxY = y;
        gameField.modGame = modGame;
        gameField.timer = timer;

        myRef2.child("test").child("reset").addValueEventListener(resetList);
        myRef2.child("anim"+!gameMaster).addValueEventListener(animListener);
        myRef2.child("gameOver").addListenerForSingleValueEvent(gameOverListener);
        myRef2.child("Koloda").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.getValue(String.class)!=null) {
                    randomKoloda = dataSnapshot.getValue(String.class);
                    if (randomKoloda.length() > 0) {
                        Matrix matrix = new Matrix();
                        matrix.postRotate(-90);
//                        Log.d("kooooooo",kolodaRandom+ " " + kolodaRandom.charAt(0)+ " " + kolodaSorting.indexOf(kolodaRandom.charAt(0)));
//                        Log.d("kooooooo2",kolodaSorting);
                        endKoloda = Bitmap.createScaledBitmap(allCards[kolodaPoVozr.indexOf(randomKoloda.charAt(0))].getBitmap(),
                                (int) (allCards[0].width * 0.6), (int) (allCards[0].height * 0.6), false);
                        endKoloda = Bitmap.createBitmap(endKoloda, 0, 0, endKoloda.getWidth(), endKoloda.getHeight(), matrix, false);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        resume();
    }
    protected void WhiteCastle() {
        myRef2.child("gameOver").setValue("gameOver");
        myRef2.child("Winner").setValue(!gameMaster+"flag");
    }
    void InitListenerReopen() {
        gameOverListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue(String.class) != null) {
                    if (!"gameOver".equals(dataSnapshot.getValue(String.class))) {
                        myRef2.child("timer").addListenerForSingleValueEvent(timerListener);
                    }
                    else {
                        myRef2.child("Winner").addListenerForSingleValueEvent(winnerListener);

                    }
                }
                else {
                    Toast.makeText(context, "Все Игроки покинули игру. Объявлена Ничья.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        timerListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue(Long.class)!=null){
                    gameField.timer = dataSnapshot.getValue(Long.class);
                        gameField.reOpenGame(gameMaster, myRef2);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        winnerListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
    }

    protected void StartGameFun(boolean gm, String o, String u,Card[] cards,int x,int y) {
        Log.d("ProblemZZZ","startGame.StartGameFun");
        if(mSoundPool != null && mix>-1){
            mSoundPool.play(mix,1,1,1,0,1);
        }
        Cards[0] = "048cgkoswAEIM";
        Cards[1] = "159dhlptxBFJN";
        Cards[2] = "26aeimquyCGKO";
        Cards[3] = "37bfjnrvzDHLP";
        pressStartAnimBool = false;
        gameField = new GameField(rubashka,context);
        if(take > -1)
            setSound(1,take);
        if(step > -1)
            setSound(2,step);
        koziri = -1;
        minI= -1;
        randomKoloda = "";
        meCards = "";
        Log.d("StartGameClass","StartGameFun");
        gameMaster = gm;
        opponent = o;
        userId = u;
        resetGame = false;
        koloda52 = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOP";

        gameField.user = userId;
        gameField.opponent = opponent;
        gameField.allCards = cards;
        gameField.maxX = x;
        gameField.maxY = y;
        if (gameMaster) {
            myRef2 = myRef.child("GameRoom").child(userId+opponent);
            gameField.myRef = myRef2;
//            gameField.deleteListener();
            myRef.child("GameRoom").child(userId+opponent).child("Koloda").setValue(firstRandomKoloda());

            myRef.child("GameRoom").child(userId+opponent).child("Koloda").addListenerForSingleValueEvent(razdacha);
            myRef.child("GameRoom").child(userId+opponent).child("Koloda").removeEventListener(razdacha);
        }
        else {
            myRef2 = myRef.child("GameRoom").child(opponent+userId);
            gameField.myRef = myRef2;
//            gameField.deleteListener();
            myRef.child("GameRoom").child(opponent+userId).addValueEventListener(nachalnayaRazdacha);
        }
        myRef2.child("gameOver").setValue("false");
//        myRef2.child("test").child("reset").addValueEventListener(resetList);

        resume();
    }


    private String firstRandomKoloda() {
        Cards[0] = "048cgkoswAEIM";
        Cards[1] = "159dhlptxBFJN";
        Cards[2] = "26aeimquyCGKO";
        Cards[3] = "37bfjnrvzDHLP";
        koloda52 = koloda52.substring(0,numberCards);
        while (randomKoloda.length()<numberCards) {
            randomKoloda = randomCards() + randomKoloda;
            Log.d("TAGGSS", " AnaAAAL ERROR" + randomKoloda);
        }
//        Log.d("FirstRandomKoloda",randomKoloda);
//        Log.d("FirstRandomKoloda1",Cards[0]);
//        Log.d("FirstRandomKoloda2",Cards[1]);
//        Log.d("FirstRandomKoloda3",Cards[2]);
//        Log.d("FirstRandomKoloda4",Cards[3]);
//        randomKoloda = "ba9876543210";
//        randomKoloda = "0abdefhijklmnopqr1235679xyztuv48cgws";
//        randomKoloda = "0abxyztuv48cgws";
//        ходы шесть коз дама коз -> бита ->6-7 7-8 8-9 9-10 10-в -> бита -> в-т -> Ничья
//        randomKoloda = "0c8xspkhwtolgd";
//        ходы шесть коз дама коз -> бита ->6-7 7-8 8-9 9-10 10-в в-т -> Ничья
//        randomKoloda = "c08xspkhwtolgd";


//        randomKoloda = "01234567wxabcdyz";// колода на которой проверить выйгрыш 02052020!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!1
        // ( переводить шстёрки, отбиваться должен с королями, потом из колоды берёт два туза
// и Баг он автоматом выигрывает
//        randomKoloda = "0123abcdyz4567wx";
        Cards[0] = "048cgkoswAEIM";
        Cards[1] = "159dhlptxBFJN";
        Cards[2] = "26aeimquyCGKO";
        Cards[3] = "37bfjnrvzDHLP";
        koziri=kolodaPoVozr.indexOf(randomKoloda.charAt(0))/13;
        Log.w("StartGame","poiskKoz Koz=" + koziri);
        if (koziri == -1) Log.w("StartGame ziri = -1", "Fuck" + randomKoloda.charAt(0)+ "-- ");
        myRef.child("GameRoom").child(userId+opponent).child("Koziri").setValue(String.valueOf(koziri));
        return randomKoloda;
    }


    private char randomCards() {
//        Log.d("RandomCardKolo","1");
        if(randomKoloda.length()<4){
//            Log.d("RandomCardKolo","2");
            randomCard=Cards[randomKoloda.length()].charAt((int)Math.round(Math.random()*(Cards[randomKoloda.length()].length()-(52-numberCards)/4-1)));
            Cards[randomKoloda.length()]=Cards[randomKoloda.length()].replaceAll(String.valueOf(randomCard),"");
        }
        else{
//            Log.d("RandomCardKolo","3");
            if(randomKoloda.length()>5 && randomKoloda.length()<10) {
//                Log.d("RandomCardKolo","3");
                randomCard = Cards[randomKoloda.length() % 4].charAt((int) Math.round(Math.random() * (Cards[randomKoloda.length() % 4].length() - (52 - numberCards) / 4 - 1)));
                Cards[randomKoloda.length() % 4] = Cards[randomKoloda.length() % 4].replaceAll(String.valueOf(randomCard), "");
            }
            else {
//                Log.d("RandomCardKolo","4");
                randomCard = koloda52.charAt((int) Math.round(Math.random() * (koloda52.length()-1)));
                if(randomKoloda.length()<10){
                    Cards[0] = Cards[0].replaceAll(String.valueOf(randomCard),"");
                    Cards[1] = Cards[1].replaceAll(String.valueOf(randomCard),"");
                    Cards[2] = Cards[2].replaceAll(String.valueOf(randomCard),"");
                    Cards[3] = Cards[3].replaceAll(String.valueOf(randomCard),"");

                }
            }
        }

        koloda52 = koloda52.replaceAll(String.valueOf(randomCard),"");
        return randomCard;
    }

    protected void InitListener() {
        smilesListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue(Long.class)!=null)
                    smilesCounts = dataSnapshot.getValue(Long.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        animListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue(String.class)!=null){
                    if("a".equals(dataSnapshot.getValue(String.class))) {
                        myRef2.child("anim"+!gameMaster).removeValue();
                        numEmojiClick= 0;
                        animEmojiClick = 100;
                    }
                    if("b".equals(dataSnapshot.getValue(String.class))) {
                        myRef2.child("anim"+!gameMaster).removeValue();
                        numEmojiClick= 1;
                        animEmojiClick = 100;

                    }
                    if("c".equals(dataSnapshot.getValue(String.class))) {
                        myRef2.child("anim"+!gameMaster).removeValue();
                        numEmojiClick= 2;
                        animEmojiClick = 100;

                    }
                    if("d".equals(dataSnapshot.getValue(String.class))) {
                        myRef2.child("anim"+!gameMaster).removeValue();
                        numEmojiClick= 3;
                        animEmojiClick = 100;

                    }
                    if("e".equals(dataSnapshot.getValue(String.class))) {
                        myRef2.child("anim"+!gameMaster).removeValue();
                        numEmojiClick= 4;
                        animEmojiClick = 100;

                    }
                    if("f".equals(dataSnapshot.getValue(String.class))) {
                        myRef2.child("anim"+!gameMaster).removeValue();
                        numEmojiClick= 5;
                        animEmojiClick = 100;

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        resetList = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(gameField!=null) {
                    if (gameField.youWin && "yes".equals(dataSnapshot.child("l").getValue(String.class))) {
                        if(!pressStartAnimBool) {
                            pressStartAnim = 100;
                            pressStartAnimBool = true;
                        }
                    }
                    if (gameField.youLose && "yes".equals(dataSnapshot.child("w").getValue(String.class))) {
                        if(!pressStartAnimBool) {
                            pressStartAnim = 100;
                            pressStartAnimBool = true;
                        }
                    }
                    if (gameField.nichia) {
                        if (gameField.gameMaster) {
                            if ("yes".equals(dataSnapshot.child("false").getValue(String.class))) {
                                if(!pressStartAnimBool) {
                                    pressStartAnim = 100;
                                    pressStartAnimBool = true;
                                }

                            }
                        } else {
                            if ("yes".equals(dataSnapshot.child("true").getValue(String.class))) {
                                if(!pressStartAnimBool) {
                                    pressStartAnim = 100;
                                    pressStartAnimBool = true;
                                }
                            }

                        }
                    }
                }

                if("yes".equals(dataSnapshot.child("true").getValue(String.class))) {
                    if ("yes".equals(dataSnapshot.child("false").getValue(String.class))) {
//                        gameField.timer = 30;
                        Log.d("timer=30","st 1 ");
                        myRef2.child("test").child("reset").removeEventListener(resetList);
                        myRef2.child("anim"+!gameMaster).removeEventListener(animListener);
//                        myRef2.child("test").child("reset").child("true").setValue("test");
                        if(gameField.gameMaster){
                            myRef2.removeValue();
                        }
                        resetGame = true;
                        Log.d("ResetList","Nichia");
                    }
                }
                if("yes".equals(dataSnapshot.child("w").getValue(String.class))){
                    if("yes".equals(dataSnapshot.child("l").getValue(String.class))){
//                        gameField.timer = 30;
                        Log.d("timer=30","st 2 ");
                        Log.d("ProblemZZZ","w l yes yes");
                        myRef2.child("test").child("reset").removeEventListener(resetList);
                        myRef2.child("anim"+!gameMaster).removeEventListener(animListener);
//                        myRef2.child("test").child("reset").child("w").setValue("test");
                        Log.d("ResetList","test 1");
                        if(gameField.gameMaster){
                            myRef2.removeValue();
                        }
                        resetGame = true;
                        Log.d("ResetList","test 2");
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        firstHod = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                tempString = dataSnapshot.getValue(String.class);
                if(tempString!=null) {
                    tempString = tempString.replaceAll("\\D","");
                    Log.d("firstHod",tempString);
                    if (tempString.length()>0) {
                        if (Integer.parseInt(tempString) < minI  && minI!=0) {
                            hodit = true;
                            otbivaet = false;
                            gameField.lenKolod = randomKoloda.length();
                            gameField.setBool(hodit,otbivaet);
                            if(gameMaster){
                                myRef.child("GameRoom").child(userId+opponent).child(opponent).removeEventListener(firstHod);
                                gameField.startGame = true;
                                gameField.startNewGame(true, modGame);
                            }
                            else {
                                myRef.child("GameRoom").child(opponent+userId).child(opponent).removeEventListener(firstHod);
                                gameField.startGame = true;
                                gameField.startNewGame(false, modGame);
                            }
                            myRef2.child("test").child("reset").addValueEventListener(resetList);
                            myRef2.child("anim"+!gameMaster).addValueEventListener(animListener);
                            return;
                        }
                        if (Integer.parseInt(tempString) > minI && Integer.parseInt(tempString)!=0) {
                            hodit = false;
                            otbivaet = true;
                            gameField.lenKolod = randomKoloda.length();

                            gameField.setBool(hodit,otbivaet);
                            if(gameMaster){
                                myRef.child("GameRoom").child(userId+opponent).child(opponent).removeEventListener(firstHod);
                                gameField.startNewGame(true, modGame);
                            }
                            else {
                                myRef.child("GameRoom").child(opponent+userId).child(opponent).removeEventListener(firstHod);
                                gameField.startNewGame(false, modGame);
                            }
                            myRef2.child("test").child("reset").addValueEventListener(resetList);
                            myRef2.child("anim"+!gameMaster).addValueEventListener(animListener);
                            return;
                        }
                        if (Integer.parseInt(tempString)==0 && minI==0) {
                            if(gameMaster) {
                                hodit = true;
                                otbivaet = false;
                            }
                            else {
                                hodit = false;
                                otbivaet = true;
                            }
                            gameField.lenKolod = randomKoloda.length();
                            gameField.setBool(hodit,otbivaet);
                            if(gameMaster){
                                myRef.child("GameRoom").child(userId+opponent).child(opponent).removeEventListener(firstHod);
                                gameField.startNewGame(true, modGame);
                            }
                            else {
                                myRef.child("GameRoom").child(opponent+userId).child(opponent).removeEventListener(firstHod);
                                gameField.startNewGame(false, modGame);
                            }
                            myRef2.child("test").child("reset").addValueEventListener(resetList);
                            myRef2.child("anim"+!gameMaster).addValueEventListener(animListener);
                            return;
                        }
                        if(minI==0&&Integer.parseInt(tempString)!=0){

                            hodit = false;
                            otbivaet = true;
                            gameField.lenKolod = randomKoloda.length();
                            gameField.setBool(hodit,otbivaet);
                            if(gameMaster){
                                myRef.child("GameRoom").child(userId+opponent).child(opponent).removeEventListener(firstHod);
                                gameField.startNewGame(true, modGame);
                            }
                            else {
                                myRef.child("GameRoom").child(opponent+userId).child(opponent).removeEventListener(firstHod);
                                gameField.startNewGame(false, modGame);
                            }
                            myRef2.child("test").child("reset").addValueEventListener(resetList);
                            myRef2.child("anim"+!gameMaster).addValueEventListener(animListener);
                            return;
                        }
                        if (minI!=0&&Integer.parseInt(tempString)==0){
                            hodit = true;
                            otbivaet = false;
                            gameField.lenKolod = randomKoloda.length();
                            gameField.setBool(hodit,otbivaet);
                            if(gameMaster){
                                myRef.child("GameRoom").child(userId+opponent).child(opponent).removeEventListener(firstHod);
                                gameField.startNewGame(true, modGame);
                            }
                            else {
                                myRef.child("GameRoom").child(opponent+userId).child(opponent).removeEventListener(firstHod);
                                gameField.startNewGame(false, modGame);
                            }
                            myRef2.child("test").child("reset").addValueEventListener(resetList);
                            myRef2.child("anim"+!gameMaster).addValueEventListener(animListener);
                            return;
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        nachalnayaRazdacha = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if("1".equals(dataSnapshot.child("Hod").getValue(String.class))) {
                    if(dataSnapshot.child("Koziri").getValue(String.class)!=null && !dataSnapshot.child("Koziri").getValue(String.class).equals("-1")) {
                        Log.d("ProblemZZZ","hod 1 - hod 2");
                        myRef.child("GameRoom").child(opponent + userId).removeEventListener(nachalnayaRazdacha);
                        koziri = Integer.parseInt(dataSnapshot.child("Koziri").getValue(String.class));
                        myRef.child("GameRoom").child(opponent + userId).child("Koloda").addListenerForSingleValueEvent(razdacha);
//                        myRef.child("GameRoom").child(opponent + userId).child("Koloda").removeEventListener(razdacha);
                        myRef.child("GameRoom").child(opponent + userId).child("Hod").setValue("2");
                    }

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        razdacha = new ValueEventListener() {
            @Override
            public void onDataChange( DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue(String.class)!=null) {
                    randomKoloda = dataSnapshot.getValue(String.class);
                    if(randomKoloda.length()>0){
                        Matrix matrix = new Matrix();
                        matrix.postRotate(-90);
//                        Log.d("kooooooo",kolodaRandom+ " " + kolodaRandom.charAt(0)+ " " + kolodaSorting.indexOf(kolodaRandom.charAt(0)));
//                        Log.d("kooooooo2",kolodaSorting);
                        endKoloda = Bitmap.createScaledBitmap(allCards[kolodaPoVozr.indexOf(randomKoloda.charAt(0))].getBitmap(),
                                (int)(allCards[0].width*0.6), (int)(allCards[0].height*0.6), true);
                        endKoloda = Bitmap.createBitmap(endKoloda,0,0,endKoloda.getWidth(),endKoloda.getHeight(),matrix,true);
                    }
                    while (meCards.length() < 6 && randomKoloda.length() > 0) {
                        char i = randomKoloda.charAt(randomKoloda.length() - 1);
                        meCards = meCards + i;
                        randomKoloda = randomKoloda.replaceAll(String.valueOf(i), "");

                        Log.d("TAGGSS", meCards + " AnaAAAL ERROR" + randomKoloda);
                    }
                    if (gameMaster)
                        myRef.child("GameRoom").child(userId + opponent).child("Koloda").setValue(randomKoloda);
                    else
                        myRef.child("GameRoom").child(opponent + userId).child("Koloda").setValue(randomKoloda);
                    poiskMenchegoKozira();
                    gameField.koziri = koziri;
                    gameField.kolodaRandom = randomKoloda;
                    gameField.setMyCards(meCards, false);
                    gameField.oldMyCards = meCards;
                    if (gameMaster) {
//                        myRef.child("GameRoom").child(userId + opponent).child("Koziri").setValue(String.valueOf(koziri));
                        myRef.child("GameRoom").child(userId + opponent).child("Hod").setValue("1");
                        myRef.child("GameRoom").child(userId+opponent).child(opponent).addValueEventListener(firstHod);
                    }
                    else {
                        myRef.child("GameRoom").child(opponent + userId).child(opponent).addValueEventListener(firstHod);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
    }
    private void poiskMenchegoKozira() {
        minI = -1;
        int temp;
        for(int i =0; i<6; i++) {
            temp = Cards[koziri].indexOf(meCards.charAt(i));
            Log.d("poiskKozir" ,i + " 3kozir "+meCards.charAt(i));
            if(temp > -1) {
                if (temp>minI){
                    minI = temp;
                }
            }
        }
        minI++;
        if(gameMaster) myRef.child("GameRoom").child(userId+opponent).child(userId).setValue(Integer.toString(minI));
        else myRef.child("GameRoom").child(opponent+userId).child(userId).setValue(Integer.toString(minI));

    }
    protected void deleteGameField(){
        if(gameField!=null) {
            gameField.deleteListener();
            gameField.context = null;
            gameField = null;
        }

    }
    protected void sendEmoji(String a){
        if(myRef2!=null){
            myRef2.child("anim"+gameMaster).setValue(a);
        }
    }

    protected void deleteGameAndClasses() {
        deleteListener();
    }
    void deleteListener() {
        context = null;
        if(myRef2!=null) {
            myRef2.child("test").child("reset").removeEventListener(resetList);
            myRef2.child("anim"+!gameMaster).removeEventListener(animListener);
        }
        if(myRef != null && opponent!=null && userId!=null) {
            myRef.child("GameRoom").child(opponent + userId).removeEventListener(nachalnayaRazdacha);
            myRef.child("GameRoom").child(userId + opponent).child(opponent).removeEventListener(firstHod);
            myRef.child("GameRoom").child(opponent + userId).child(opponent).removeEventListener(firstHod);
        }
    }
    protected void setSoundPool(SoundPool mSound) {
        mSoundPool = mSound;
    }
    protected void setSound(int title, int data) {
        switch (title){
            case 0 :
                mix = data;
                break;
            case 1:
                take = data;
                if(gameField!=null && gameField.take==-1) {
                    gameField.setSound(1, take, mSoundPool);
                }
                break;
            default:
                step = data;
                if(gameField!=null && gameField.step==-1)
                    gameField.setSound(2,step, mSoundPool);
                break;
        }
    }
    protected void pause(){
//        myRef.child("Smiles").child(userId).removeEventListener(smilesListener);
    }
    protected void resume(){
        myRef.child("Smiles").child(userId).addListenerForSingleValueEvent(smilesListener);
    }
}
