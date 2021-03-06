package ru.tmkstd.cardgamedurakonline;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.Timer;
import java.util.TimerTask;

public class RobotGameField {

    Timer timer,timerGame;
    TimerTask timerTask,timerTastGame;
    DatabaseReference myRefGameRoom, myRefCoins;
    String myID, opponentID;
    boolean hodit;
    boolean modGame;
    String myCards;
//    int lenOtbiv = 6,lenHodit = 6;
    boolean gameMaster;
    boolean opponentMayReturnCard = false, opponentReturnCard = false;
    int koziri;
    int numsGames;
    RobotGameAI robotGameAI;
    long timeGame;
    ValueEventListener
//            rasdachaPass,
            timerListener,
            winnerCoin, loserCoin;
    ValueEventListener returnCardListener, prosmotrLenKolod,prosmotrLenOpponent,prosmotrLenHodit,prosmotrLenOtbiv,prosmotrHodit, prosmotrOtbivaet
            ,prosmotrWin
            ,prosmotrPositionBito,prosmotrPositionPass,prosmotrPositionBery;
    public RobotGameField(boolean hodit, boolean modGam, String mCards, int koziri, DatabaseReference ref, DatabaseReference refC, String mID, String oID, int[] robotParams, int numsGame) {
        this.hodit = hodit;
        numsGames=numsGame;
        modGame = modGam;
        myCards = mCards;
        this.koziri = koziri;
        myRefGameRoom = ref;
        myRefCoins = refC.child("Coins");
        myID = mID;
        timeGame = 30;
        opponentID = oID;
        robotGameAI = new RobotGameAI(modGam,robotParams);
        robotGameAI.setMyCards(myCards);
        robotGameAI.setKoziri(koziri);
        robotGameAI.setHod(hodit);
        robotGameAI.sortingCard();
        InitListeners();

        myRefGameRoom.child("LenNoGM").setValue("6");
        myRefGameRoom.child("Koloda").addValueEventListener(prosmotrLenKolod);
        myRefGameRoom.child("Hodit").addValueEventListener(prosmotrHodit);
        myRefGameRoom.child("Otbivaet").addValueEventListener(prosmotrOtbivaet);
        myRefGameRoom.child("OtbivaetLen").addValueEventListener(prosmotrLenOtbiv);
        myRefGameRoom.child("HoditLen").addValueEventListener(prosmotrLenHodit);
        myRefGameRoom.child("LenGM").addValueEventListener(prosmotrLenOpponent);

//        myRefGameRoom.child("test").addValueEventListener(prosmotrPosition);
        myRefGameRoom.child("test").child("bito2").addValueEventListener(prosmotrPositionBito);
        myRefGameRoom.child("test").child("hodit2").addValueEventListener(prosmotrPositionPass);
        myRefGameRoom.child("test").child("otbivaet2").addValueEventListener(prosmotrPositionBery);
        myRefGameRoom.child("timer").addValueEventListener(timerListener);
        myRefGameRoom.child("Winner").addValueEventListener(prosmotrWin);
        myRefGameRoom.child("returnCard").addValueEventListener(returnCardListener);
        myRefGameRoom.keepSynced(true);
//        myRefGameRoom.child("timer").addValueEventListener(timerListener);

    }
    void InitListeners() {
        timerListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue(Long.class)!=null){
                    Log.d("timeGame",timeGame+"");
                    if(timeGame<dataSnapshot.getValue(Long.class))
                        timeGame = dataSnapshot.getValue(Long.class);
                    if(timeGame<2) {
                        if(robotGameAI.hodit
                                && robotGameAI.hoditCards.length()>0
                                && (robotGameAI.hoditCards.length()>robotGameAI.otbivaetCards.length() || robotGameAI.otbivaetCards.indexOf(' ')!=-1)){
                            myRefGameRoom.child("Winner").setValue("false");
                        }
                        if(!robotGameAI.hodit
                                && (robotGameAI.hoditCards.length()==0 || robotGameAI.iBery || (robotGameAI.hoditCards.length()==robotGameAI.otbivaetCards.length()&&robotGameAI.otbivaetCards.indexOf(' ')==-1))) {
                            myRefGameRoom.child("Winner").setValue("false");
                        }
                    }
                    else {
                        if (timerGame == null) {
                            timerGame = new Timer();
                            timerTastGame = new TimerTask() {
                                @Override
                                public void run() {
                                    timeGame -= 2;
                                    myRefGameRoom.child("timer").setValue(timeGame);
                                }
                            };
//                        timerGame.schedule(timerTastGame,2000,2000);
                            //?????? ???????? ????????????, ???????? ?????? ?????????????? ????????????????, ???? ???????????????????? ?? ?????????????? ???????????? ?? ?????????? ?????????????? ?????????????????? ?????? ????????????
                            if (timeGame < 1) {
                                timerGame.cancel();
//                            timerGame.purge();//???? ?????????? ???????? ???????????????? ?????????? ??????????
                            } else {
                                timerGame.schedule(timerTastGame, 2000, 2000);
                            }
                        }
                    }
//                    if(timerGame!=null) {
//                        timerGame.cancel();
//                        timerGame = null;
//                    }
//                    timerGame = new Timer();
//                    timerTastGame = new TimerTask() {
//                        @Override
//                        public void run() {
//                            timeGame-=2;
//                            myRefGameRoom.child("timer").setValue(timeGame);
//                        }
//                    };
//                    timerGame.schedule(timerTastGame,0,2000);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        returnCardListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            if (dataSnapshot.getValue(String.class) != null && dataSnapshot.getValue(String.class).indexOf('1')!=-1){
                //???????? ?????? ?????????????? ???? ?????????????? ??????????
//                myRefGameRoom.child("returnCard").setValue(Boolean.toString(opponentMayReturnCard));
                if(opponentMayReturnCard) {
                    if(timer != null) {
                        timer.cancel();
                        timer = null;
                    }
                    opponentMayReturnCard = false;
                    opponentReturnCard = true;
                    myRefGameRoom.child("returnCard").setValue(Boolean.toString(true));//?????????? ?????????????? ??????????????????????????, ?? ?????????? ???????????????????? ???? ????????????
                }
                else {
                    myRefGameRoom.child("returnCard").setValue(Boolean.toString(false));

                }
//                if(meNeedReturnCard &&"true".equals(dataSnapshot.getValue(String.class))) {
//                    returnCardFun();
//                }
//                else if(meNeedReturnCard &&"false".equals(dataSnapshot.getValue(String.class))) {
//                    meNeedReturnCard = false;
//                }
//                else {
//                    if(!meNeedReturnCard){
//                        myRef.child("returnCard").setValue(Boolean.toString(opponentMayReturnCard));
//                        if (opponentMayReturnCard) {
//                            opponentReturnCard = true;
//                            animOpponentCard = 0;
//                        }
//                    }
//                }
            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };
        winnerCoin = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                myRefCoins.child(myID).setValue(dataSnapshot.getValue(Long.class) + 9);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        loserCoin = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue(Long.class) > 10) {
                    myRefCoins.child(opponentID).setValue(dataSnapshot.getValue(Long.class) - 10);
                } else {
                    myRefCoins.child(opponentID).setValue(0l);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        prosmotrWin = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue(String.class)!=null){
                    Log.d("MyTagsSS","gameFieldWin  win? " + dataSnapshot.getValue(String.class));
                    if("trueflag".equals(dataSnapshot.getValue(String.class))){
//                        youWin = true;
                        if (timer != null) {
                            timer.cancel();
                            timer = null;
                        }
                        if(timerGame!=null) {
                            timerGame.cancel();
                            timerGame = null;
                        }
                        robotGameAI.endGame();
                        return;
                    }
                    if("falseflag".equals(dataSnapshot.getValue(String.class))){
//                        youWin = true;
                        if (timer != null) {
                            timer.cancel();
                            timer = null;
                        }
                        if(timerGame!=null) {
                            timerGame.cancel();
                            timerGame = null;
                        }
                        timer = new Timer();
                        timerTask = new TimerTask() {
                            @Override
                            public void run() {
                                if(numsGames<1) {
                                    myRefGameRoom.child(opponentID).removeValue();
                                    myRefGameRoom.child(opponentID).setValue("Exit");
                                    return;
                                }
                                myRefGameRoom.child("test").child("reset").child("w").setValue("yes");
                            }
                        };
                        timer.schedule(timerTask, 2000);
                        myRefCoins.child(myID).addListenerForSingleValueEvent(winnerCoin);
                        myRefCoins.child(opponentID).addListenerForSingleValueEvent(loserCoin);
                        robotGameAI.endGame();
                        return;
                    }
                    if("true".equals(dataSnapshot.getValue(String.class))){
                        if (timer != null) {
                            timer.cancel();
                            timer = null;
                        }
                        if(timerGame!=null) {
                            timerGame.cancel();
                            timerGame = null;
                        }
                        timer = new Timer();
                        timerTask = new TimerTask() {
                            @Override
                            public void run() {
                                if(numsGames<1) {
                                    myRefGameRoom.child(opponentID).removeValue();
                                    myRefGameRoom.child(opponentID).setValue("Exit");
                                    return;
                                }
                                myRefGameRoom.child("test").child("reset").child("l").setValue("yes");
                            }
                        };
                        timer.schedule(timerTask, 2000);
                        robotGameAI.endGame();
                        return;
                    }
                    if("false".equals(dataSnapshot.getValue(String.class))){
//                        youWin = true;
                        if (timer != null) {
                            timer.cancel();
                            timer = null;
                        }
                        if(timerGame!=null) {
                            timerGame.cancel();
                            timerGame = null;
                        }
                        timer = new Timer();
                        timerTask = new TimerTask() {
                            @Override
                            public void run() {
                                if(numsGames<1) {
                                    myRefGameRoom.child(opponentID).removeValue();
                                    myRefGameRoom.child(opponentID).setValue("Exit");
                                    return;
                                }
                                if(timeGame<1) {
                                    myRefGameRoom.child(opponentID).removeValue();
                                    myRefGameRoom.child(opponentID).setValue("ExitTimer");
                                }
                                else {
                                    myRefGameRoom.child("test").child("reset").child("w").setValue("yes");
                                }

                            }
                        };
                        timer.schedule(timerTask, 2000);
                        myRefCoins.child(myID).addListenerForSingleValueEvent(winnerCoin);
                        myRefCoins.child(opponentID).addListenerForSingleValueEvent(loserCoin);
                        robotGameAI.endGame();
                        return;
                    }
                    if("nichia".equals(dataSnapshot.getValue(String.class))) {
                        if (timer != null) {
                            timer.cancel();
                            timer = null;
                        }
                        if(timerGame!=null) {
                            timerGame.cancel();
                            timerGame = null;
                        }
                        timer = new Timer();
                        timerTask = new TimerTask() {
                            @Override
                            public void run() {
                                if(numsGames<1) {
                                    myRefGameRoom.child(opponentID).removeValue();
                                    myRefGameRoom.child(opponentID).setValue("Exit");
                                    return;
                                }
                                myRefGameRoom.child("test").child("reset").child("false").setValue("yes");
                            }
                        };
                        timer.schedule(timerTask, 2000);
//                        nichia = true;
//                        game = false;
                        robotGameAI.endGame();
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
//                if(
                        robotGameAI.setKolodaRandom(dataSnapshot.getValue(String.class));
//                        ) {
//                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        prosmotrLenOpponent = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue(String.class)!=null) {
                    robotGameAI.setLenOpponent(Integer.parseInt(dataSnapshot.getValue(String.class)));
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        prosmotrLenHodit = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue(String.class)!=null) {
//                    lenHodit = Integer.parseInt(dataSnapshot.getValue(String.class));
                    robotGameAI.setLenHodit(Integer.parseInt(dataSnapshot.getValue(String.class)));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        prosmotrLenOtbiv = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue(String.class)!=null) {
                    robotGameAI.setLenOtbivaet(Integer.parseInt(dataSnapshot.getValue(String.class)));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        prosmotrHodit = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d("PerevodDeb",robotGameAI.hoditCards+" " + dataSnapshot.getValue(String.class));
                if(dataSnapshot.getValue(String.class)!=null) opponentMayReturnCard = !robotGameAI.hodit;
                if(opponentReturnCard) {
                    robotGameAI.hoditCards = dataSnapshot.getValue(String.class);
                    if(timer != null) {
                        timer.cancel();
                        timer = null;
                    }
                    opponentReturnCard = false;
//                    return;//???????? ???????????? ?????????????????? ?????? ??????????, ?? ?????????? ?????????? ??????????????, ???? ???? ???? ????????????????????
                }
                switch (robotGameAI.setHoditCards(dataSnapshot.getValue(String.class))) {
                    case 0:
                        myRefGameRoom.child("Hodit").setValue(robotGameAI.hoditCards); //?????????????? ???????????? ???? ???????????????????????????? ???????????????? ?? ????????????????????????
                        break;
                    case 1:
                        myRefGameRoom.child("OtbivaetLen").setValue(Integer.toString(robotGameAI.myCards.length()));
                        myRefGameRoom.child("LenNoGM").setValue(Integer.toString(robotGameAI.myCards.length()));
                        if(timer != null) {
                            timer.cancel();
                            timer = null;
                        }
                        break;
                    default:break;
                }
                if(!robotGameAI.hodit && robotGameAI.hoditCards.length()>0 && !robotGameAI.iBery) {
                    operationOtbivaet();
                }

                //?????????? ???????? ?????????????? ???????????????? ???????? ???????? ???????? ?????????? ?? ????. (???????? ???????? ???????? ???????? ???????? ????????
            }
            @Override
            public void onCancelled( DatabaseError databaseError) {

            }
        };
        prosmotrOtbivaet = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue(String.class)!=null) opponentMayReturnCard = robotGameAI.hodit;
                if(opponentReturnCard) {
                    robotGameAI.otbivaetCards = dataSnapshot.getValue(String.class);
                    if(timer != null) {
                        timer.cancel();
                        timer = null;
                    }
                    opponentReturnCard = false;
                }
                robotGameAI.setOtbivaetCards(dataSnapshot.getValue(String.class));
//?????? ?????????? ?????????? ???????????? ?????????? ?????? ???? ???? ??????????
                if(robotGameAI.otbivaetCards.length()==0){
                    return;//?????? ???????????? ?????????????????????????? ???????????? ???????? ???? ?????????????????? ???? ?????????????????? ?????? ???? ????????????
                }

                if (robotGameAI.hodit && robotGameAI.hoditCards.length()>0) {//?????? ?????? ?????? Null ???????? ?????? ?? ?? ?????????? ?? ?? ????????????????
                    operationHodit();
                }

                if(!robotGameAI.hodit && robotGameAI.hoditCards.length()> robotGameAI.otbivaetCards.length()) {
                    operationOtbivaet();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        prosmotrPositionBito = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                opponentMayReturnCard = false;
                if("bito".equals(dataSnapshot.getValue(String.class))) {
                    if(timer != null) {
                        timer.cancel();
                        timer = null;
                    }
                    switch (robotGameAI.bito()) {
                        case 0:
//                            ???????? ?????????? ??????????????
                            myRefGameRoom.child("Koloda").setValue(robotGameAI.kolodaRandom);
                            myRefGameRoom.child("HoditLen").setValue(Integer.toString(robotGameAI.myCards.length()));
                            //???????????? SetMyCards
                            myRefGameRoom.child("LenNoGM").setValue(Integer.toString(robotGameAI.myCards.length()));
                            myRefGameRoom.child("Otbivaet").removeValue();// ???????? ?????? ????????????????, ?? ???? ?????????????????????? ?? ?????????????? ?????????????????? ?????????? ?????????? ?????????????????? ?????????? ?? ??????-????
                            myRefGameRoom.child("Hodit").removeValue();//?????? ?????????????? ?????? ???????????????????? ?? ???????? ???????? ???????? ???? ?????????????? ???????? ???????????? ????????????
                            myRefGameRoom.child("test").removeValue();
//                            myRefGameRoom.child("Otbivaet").removeValue();
                            break;

                        case 98:
                            //?????????????????? ??????????
                            myRefGameRoom.child("Winner").setValue("nichia");
                            robotGameAI.endGame();
                            break;
                        case 99:
                            //?????????? ?????????? ???? ???????????? ??.??. ???????????????? ?? ???????????????????? ?? ??????????????????????????
                            //?? ?????????????? ???????? ??????????????????
                            break;
                        default: break;
                    }
                }
                else {
                    if(robotGameAI.hodit)
                        operationHodit();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        prosmotrPositionBery = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                opponentMayReturnCard = false;
                if("bery".equals(dataSnapshot.getValue(String.class))) {
                    //?????? ???????? ?????????????? ?????????????? ?????????????????? ?? ?????????? ?? hoditprosmotr ?????? ?????????????? ????????
                    //?????? ?????????? ?????????????? ?????? ???????? ???????????? ??????????????????????
                    if(timer != null) {
                        timer.cancel();
                        timer = null;
                    }
                    timer = new Timer();
                    timerTask = new TimerTask() {
                        @Override
                        public void run() {
                            switch (robotGameAI.bery()) {
                                //?????????????????? ????????
                                case 0:
                                    myRefGameRoom.child("HoditLen").setValue(Integer.toString(robotGameAI.myCards.length()));
                                    //???????????? SetMyCards
                                    myRefGameRoom.child("Koloda").setValue(robotGameAI.kolodaRandom);
                                    myRefGameRoom.child("LenNoGM").setValue(Integer.toString(robotGameAI.myCards.length()));
                                    myRefGameRoom.child("test").child("hodit2").setValue("pass");
                                    break;
                                //?????????????????? ??????????
                                case 1:
                                    myRefGameRoom.child("Hodit").setValue(robotGameAI.hoditCards);
                                    myRefGameRoom.child("HoditLen").setValue(Integer.toString(robotGameAI.myCards.length()));
                                    //???????????? SetMyCards
                                    myRefGameRoom.child("Koloda").setValue(robotGameAI.kolodaRandom);
                                    myRefGameRoom.child("LenNoGM").setValue(Integer.toString(robotGameAI.myCards.length()));
                                    myRefGameRoom.child("test").child("hodit2").setValue("pass");
                                    break;
                                case 98:
        //                            ?????????? ?? ????????????????

                                    myRefGameRoom.child("Winner").setValue(Boolean.toString(!gameMaster));
                                    robotGameAI.endGame();
                                    break;
                                case 99: //?? ?????????????? ???????? ????????????????????????
                                    //???????????? ???????? ?????? ?????????????????? ???????? ???? ??????????????????, ?????????? ?????? ?????????? ???????????????????? ?? ?? ??????????
                                    // ???????? ?????????? ?????????? ?? ???? ????????????
                                    // ???????? ?????????? ???????? ?? ???????? ???????? ?? ????????

                                    myRefGameRoom.child("Winner").setValue(Boolean.toString(gameMaster));
                                    robotGameAI.endGame();
                                    break;
                                default:break;
                            }
                        }
                    };
                    timer.schedule(timerTask,1000);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        prosmotrPositionPass = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                opponentMayReturnCard = false;
                if("pass".equals(dataSnapshot.getValue(String.class))) {
                    if(timer != null) {
                        timer.cancel();
                        timer = null;
                    }
//                    timer = new Timer();
//                    timerTask = new TimerTask() {
//                        @Override
//                        public void run() {
                            switch (robotGameAI.pass()){
                                case 0:
                                    //?? ?????? ???????? ?? ???????????????? ??????????????????
                                    myRefGameRoom.child("OtbivaetLen").setValue(Integer.toString(robotGameAI.myCards.length()));
                                    myRefGameRoom.child("LenNoGM").setValue(Integer.toString(robotGameAI.myCards.length()));

                                    myRefGameRoom.child("Otbivaet").removeValue();// ???????? ?????? ????????????????, ?? ???? ?????????????????????? ?? ?????????????? ?????????????????? ?????????? ?????????? ?????????????????? ?????????? ?? ??????-????
                                    myRefGameRoom.child("Hodit").removeValue();//?????? ?????????????? ?????? ???????????????????? ?? ???????? ???????? ???????? ???? ?????????????? ???????? ???????????? ????????????
                                    myRefGameRoom.child("test").removeValue();
                                    break;
                                case 1:
//                                    myRefGameRoom.child("Koloda").addListenerForSingleValueEvent(rasdachaPass);
                                    break;
                                case 99: //?? ?????????????? ???????? ????????????????????????
                                    // ???????? ?????????? ?????????? ?? ???? ????????????
                                    // ???????? ?????????? ???????? ?? ???????? ???????? ?? ????????
                                    break;
                                default:break;
                            }
//                        }
//                    };
//                    timer.schedule(timerTask,1000);
                }
                else {
                    if(robotGameAI.hodit)
                        operationHodit();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
//        rasdachaPass = new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                if(robotGameAI.rasdacha(dataSnapshot.getValue(String.class))) {
//// ???????? ?????????????? ?????????? ??????????
//                    myRefGameRoom.child("HoditLen").setValue(Integer.toString(robotGameAI.myCards.length()));
//                    //???????????? SetMyCards
//                    myRefGameRoom.child("Koloda").setValue(robotGameAI.kolodaRandom);
//                    myRefGameRoom.child("LenNoGM").setValue(Integer.toString(robotGameAI.myCards.length()));
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        };
    }
    void deleteListeners() {
        if(timer != null){
            timer.cancel();
            timer = null;
        }
        if(timerGame != null){
            timerGame.cancel();
            timerGame = null;
        }
        if(myRefGameRoom!=null){

            myRefGameRoom.child("Koloda").removeEventListener(prosmotrLenKolod);
            myRefGameRoom.child("Hodit").removeEventListener(prosmotrHodit);
            myRefGameRoom.child("Otbivaet").removeEventListener(prosmotrOtbivaet);
            myRefGameRoom.child("OtbivaetLen").removeEventListener(prosmotrLenOtbiv);
            myRefGameRoom.child("HoditLen").removeEventListener(prosmotrLenHodit);
            myRefGameRoom.child("LenGM").removeEventListener(prosmotrLenOpponent);

//            myRefGameRoom.child("test").removeEventListener(prosmotrPosition);
            myRefGameRoom.child("test").child("bito2").removeEventListener(prosmotrPositionBito);
            myRefGameRoom.child("test").child("hodit2").removeEventListener(prosmotrPositionPass);
            myRefGameRoom.child("test").child("otbivaet2").removeEventListener(prosmotrPositionBery);
            myRefGameRoom.child("timer").removeEventListener(timerListener);
            myRefGameRoom.child("Winner").removeEventListener(prosmotrWin);
            myRefGameRoom.child("returnCard").removeEventListener(returnCardListener);
            myRefGameRoom.keepSynced(false);
        }

    }
    void operationOtbivaet(){
        Log.d("operationOtbovaet","????????????");
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        Log.d("roboTestHod", "6");
        timer = new Timer();
        timerTask = new TimerTask() {
            @Override
            public void run() {
                Log.d("operationOtbovaet","??????????????????????????");
                Log.d("Gluuuk","OO ");
                switch (robotGameAI.robotOtbivaet()) {
                    case 0:
                        myRefGameRoom.child("Otbivaet").setValue(robotGameAI.otbivaetCards);
                        myRefGameRoom.child("OtbivaetLen").setValue(Integer.toString(robotGameAI.myCards.length()));
                        myRefGameRoom.child("LenNoGM").setValue(Integer.toString(robotGameAI.myCards.length()));
                        break;//?????????????? ??????????????????
                    case 1:
                        opponentMayReturnCard = false;
                        myRefGameRoom.child("Hodit").setValue(robotGameAI.hoditCards);
                        myRefGameRoom.child("HoditLen").setValue(Integer.toString(robotGameAI.myCards.length()));
                        myRefGameRoom.child("LenNoGM").setValue(Integer.toString(robotGameAI.myCards.length()));
                        return;//?????????????????? ?????????? ???? ??????????????????
                    case 2:
                        myRefGameRoom.child("test").child("otbivaet2").setValue("bery");
                        opponentMayReturnCard = false;
                        break;//????????
                    default:
                        break;//???????????? ???? ???????????? ?????????? ???????????? ?????? ????????
                }
//                            }
            }
        };
        timer.schedule(timerTask, 2000);

    }
    void operationHodit() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
            timer = new Timer();
            timerTask = new TimerTask() {
                @Override
                public void run() {
                    switch (robotGameAI.robotHodit()) {
//                    ?????????????? ?????? ???????????????? ?? ?????? ?????????? ?? ?????? ????????????????
                        case 0:
                            myRefGameRoom.child("Hodit").setValue(robotGameAI.hoditCards);
                            myRefGameRoom.child("HoditLen").setValue(Integer.toString(robotGameAI.myCards.length()));
                            myRefGameRoom.child("LenNoGM").setValue(Integer.toString(robotGameAI.myCards.length()));
                            Log.d("operationHodit","?????????????????? ?????? ?????? ????????????????????????");
                            break;//?????????????????? ?????? ?????? ????????????????????????
                        case 1:
//                ?????????????? ???????????? ????????????
//                ?????????? Bito
//                ?????????? ??????????????????????
//                ?????? ?????????????? ???????????? ?????? ?????? ?? ???????????? ???? ??????????????!!!!!!!
                            //???????????? ?????????? ???????????????????? ???????? ?? ???????????????? ?? ?? ???????? ???? ???????????? hodit na otbivaet
                            myRefGameRoom.child("Koloda").setValue(robotGameAI.kolodaRandom);
                            myRefGameRoom.child("OtbivaetLen").setValue(Integer.toString(robotGameAI.myCards.length()));
                            //???????? ???????????????? ???????? ??????
                            myRefGameRoom.child("test").child("bito2").setValue("bito");
                            opponentMayReturnCard = false;
                            myRefGameRoom.child("LenNoGM").setValue(Integer.toString(robotGameAI.myCards.length()));//???????? ?????????? ?????????? ?????????????? ????????????????
                            Log.d("operationHodit","???????????????? ??????o");
                            break;//?????????????????? ????????
                        case 2:
                            break;//?????????????? ?????????
                        case 99: //?? ?????????????? ??????????????????
                            Log.d("operationHodit","???????????????? ??????????????");
                            myRefGameRoom.child("Winner").setValue(Boolean.toString(gameMaster));
                            opponentMayReturnCard = false;
                            robotGameAI.endGame();
                            break;
                        default:
                            break;//???????????? ???? ????????????
                    }
                }
            };

            timer.schedule(timerTask, 2000);
    }
}
