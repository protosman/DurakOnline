package ru.tmkstd.cardgamedurakonline;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

public class RobotStartGame {
    String myId,opponentId;
    DatabaseReference myRef, myRefGameRoom;
    ValueEventListener waitRasdacha,nachalnauaRasdacha, whatchKoziri, firstHod, resetList;
    int koziri;
    String myCards, kolodaRandom;
    int minKozir;
    String[] Cards;
    boolean modeGame;
    RobotGameField robotGameField;
    int[] robotParams;
    int numsGames, endsGames;
    public RobotStartGame(String userId, String oppId, DatabaseReference ref, boolean modGa, int[] params, int numsGame) {
        endsGames = 0;
        myId = userId;
        numsGames = numsGame;
        opponentId = oppId;
        myRef = ref;
        robotParams = params;
        myRefGameRoom = myRef.child("GameRoom").child(opponentId+myId);
        modeGame = modGa;
        minKozir = -1;
        Cards = new String[4];
        Cards[0] = "048cgkoswAEIM";
        Cards[1] = "159dhlptxBFJN";
        Cards[2] = "26aeimquyCGKO";
        Cards[3] = "37bfjnrvzDHLP";
        InitListener();
        startNewGame();
//      проихвести ресет гаме
        myRef.child("GameRoom").child(opponentId + myId).child("test").child("reset").addValueEventListener(resetList);
    }
    void exit() {

        //произвести выход из игры
        deleteListeners();
        if(robotGameField != null) robotGameField.deleteListeners();
        robotGameField = null;
    }
    void startNewGame() {
        myRef.child("GameRoom").child(opponentId + myId).child("Hod").addValueEventListener(waitRasdacha);
    }
    void deleteListeners() {
        if(opponentId != null && myId != null) {
            myRef.child("GameRoom").child(opponentId + myId).child("Koziri").removeEventListener(whatchKoziri);
            myRef.child("GameRoom").child(opponentId + myId).child("Hod").removeEventListener(waitRasdacha);
            myRef.child("GameRoom").child(opponentId + myId).child("test").child("reset").removeEventListener(resetList);
            myRef.child("GameRoom").child(opponentId + myId).child(opponentId).removeEventListener(firstHod);
            myRef.child("GameRoom").child(opponentId + myId).child("Koloda").removeEventListener(nachalnauaRasdacha);
        }
    }
    void InitListener (){
        resetList = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if("yes".equals(dataSnapshot.child("true").getValue(String.class))
                    && "yes".equals(dataSnapshot.child("false").getValue(String.class))) {
                        endsGames++;
//                    myRefGameRoom.child("test").child("reset").child("false").setValue("yes");
                        robotGameField.deleteListeners();
                        robotGameField = null;
//                    myRefGameRoom.child("test").child("reset").child("true").setValue("test"); //НЕ успеет оппонет оппомниться
                        startNewGame();
                }
                if("yes".equals(dataSnapshot.child("w").getValue(String.class))){
                    if("yes".equals(dataSnapshot.child("l").getValue(String.class))){

                        endsGames++;
//                        myRefGameRoom.child("test").child("reset").child("w").setValue("yes");
                        robotGameField.deleteListeners();
                        robotGameField = null;
//                    myRefGameRoom.child("test").child("reset").child("true").setValue("test");//НЕ успеет оппонет оппомниться
                        startNewGame();
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        firstHod = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue(String.class) != null && !"Game".equals(dataSnapshot.getValue(String.class)) && !"Exit".equals(dataSnapshot.getValue(String.class)) &&!"Ready".equals(dataSnapshot.getValue(String.class))) {
                    myRef.child("GameRoom").child(opponentId + myId).child(opponentId).removeEventListener(firstHod);
                    if(dataSnapshot.getValue(String.class).equals("0") && minKozir ==0){
                        //не у кого нет козыря ходит тот кто ждал игру
                        //Но у меня такого невозможно т.к. я всем даю козырь в начале

                    }
                    else if(Integer.parseInt(dataSnapshot.getValue(String.class))>minKozir){
                        //у него козырь меньше, значит он ходит(тут больше значит меньше) мдаааа
                        robotGameField = new RobotGameField(false,modeGame,myCards,koziri,myRefGameRoom,myRef,myId,opponentId,robotParams,numsGames-endsGames);
                    }
                    else {
                        robotGameField = new RobotGameField(true,modeGame,myCards,koziri,myRefGameRoom,myRef,myId,opponentId,robotParams,numsGames-endsGames);
                        //тут хожу я, т.к. у меня(робота) козырь меньше

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        whatchKoziri = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue(String.class)!=null){
                    koziri = Integer.parseInt(dataSnapshot.getValue(String.class));
                    myRef.child("GameRoom").child(opponentId + myId).child("Koziri").removeEventListener(whatchKoziri);
                    myRef.child("GameRoom").child(opponentId + myId).child("Koloda").addValueEventListener(nachalnauaRasdacha);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        waitRasdacha = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue(String.class)!=null) {
                    if(dataSnapshot.getValue(String.class).equals("1")){
                        myRef.child("GameRoom").child(opponentId + myId).child("Hod").removeEventListener(waitRasdacha);
                        myRef.child("GameRoom").child(opponentId + myId).child("Hod").setValue("2");
                        myRef.child("GameRoom").child(opponentId + myId).child("Koziri").addValueEventListener(whatchKoziri);

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        nachalnauaRasdacha = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue(String.class)!=null){
                    myRef.child("GameRoom").child(opponentId + myId).child("Koloda").removeEventListener(nachalnauaRasdacha);
                    kolodaRandom = dataSnapshot.getValue(String.class);
                    myCards = kolodaRandom.substring(kolodaRandom.length()-6);
                    kolodaRandom = kolodaRandom.substring(0,kolodaRandom.length()-6);
                    Log.d("BotWork","myCard=" + myCards+ " kolodaRandom=" + kolodaRandom+". LenMy=" + myCards.length()+" lenBeforeKolod="+ dataSnapshot.getValue(String.class)+" lenAfterKolod=" + kolodaRandom.length());
                    myRef.child("GameRoom").child(opponentId + myId).child("Koloda").setValue(kolodaRandom);
                    poiskMenchegoKozira();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };

    }
    void poiskMenchegoKozira() {
        minKozir = -1;
        int temp;
        for(int i =0; i<6; i++) {
            temp = Cards[koziri].indexOf(myCards.charAt(i));
            if(temp > -1) {
                if (temp>minKozir){
                    minKozir = temp;
                }
            }
        }
        minKozir++;
        myRef.child("GameRoom").child(opponentId+myId).child(myId).setValue(Integer.toString(minKozir));
        myRef.child("GameRoom").child(opponentId + myId).child(opponentId).addValueEventListener(firstHod);
    }
}
