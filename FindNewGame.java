package ru.tmkstd.cardgamedurakonline;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class FindNewGame {
//    FirebaseAuth mAuth;
//    FirebaseDatabase database;
    DatabaseReference myRef;
    FirebaseUser user;
    boolean startGameBool, findGameBool,gameMaster,pressStart;
    String opponent;
    boolean opponentExit;
    boolean friendGame = false;
    Bitmap avatarOpponent, myNameBitmap;
    Bitmap avatarOpponentMenu, opponNameBitmap;
//    FirebaseStorage storage;
    StorageReference storageRef;
    int maxY, maxX;
    String nameOpponent="";
    String myName;
    boolean modGame;
    int numCards;
    long coin =-1;
    long tempCoin=0;
    long realCoin=0;
    boolean opponNull;
    int pressStartAnim;
    boolean pressStartAnimBool;
    int coinAddAnim = 0;
    String tempCoinSt="0";
    Context context;
    boolean playing;

    long firstTimer;
    boolean firstTimerBool;

    ValueEventListener findGameListener, waitGameListener, startGameListenerIsFind, startGameListenerIsWait, exitListener, nameOpponList, coinListener
//            ,myNameListener
                    ;

    public FindNewGame(Context context, int x, int y, boolean mod, int num){
        this.context = context;
        playing = true;
        opponNull = false;
        maxY = y;
        maxX = x;
//        name = n;
        modGame = mod;
        numCards = num;
        opponentExit = false;
        startGameBool = false;
        findGameBool = false;
        pressStart = false;
        pressStartAnim = 0;
        firstTimerBool = false;
        pressStartAnimBool = false;

        storageRef = FirebaseStorage.getInstance().getReference();
        myRef = FirebaseDatabase.getInstance().getReference();
        user = FirebaseAuth.getInstance().getCurrentUser();
        InitListener();
        myRef.child("Coins").child(user.getUid()).addValueEventListener(coinListener);
        myRef.child("Names").child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue(String.class)!= null) {
                    myName = dataSnapshot.getValue(String.class);
                    myNameBitmap = unionBitmap(myName);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


//        findGame();
    }

    private void InitListener() {
        coinListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(startGameBool){
                    realCoin = dataSnapshot.getValue(Long.class);
                    tempCoin = dataSnapshot.getValue(Long.class)-coin;
                    if(tempCoin>0) tempCoinSt = "+" + tempCoin;
                    else tempCoinSt = ""+tempCoin;
                    coinAddAnim = 70;
                } else {
                    coin = dataSnapshot.getValue(Long.class);
//                    coinSt = "" + coin;
                    realCoin = dataSnapshot.getValue(Long.class);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        nameOpponList = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                nameOpponent = dataSnapshot.getValue(String.class);
                opponNameBitmap = unionBitmap(nameOpponent);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        exitListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if ("Exit".equals(dataSnapshot.getValue(String.class))) {
                    if (!startGameBool){
                        if(playing)
                            refindFun();
                    }
                    else {
                        opponentExit = true;
                    }
                }
                if ("Game".equals(dataSnapshot.getValue(String.class))){
                    opponentExit = false;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        startGameListenerIsFind = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.child(opponent).getValue(String.class)== null) opponNull = true;
                if("Game".equals(dataSnapshot.child(opponent).getValue(String.class))) if(!pressStartAnimBool) {pressStartAnim = 100;pressStartAnimBool = true;}
                if("Wait".equals(dataSnapshot.child(user.getUid()).getValue(String.class))) {
                    myRef.child("GameRoom").child(opponent+user.getUid()).child(user.getUid()).setValue("Ready");
                }
                if ("Ready".equals(dataSnapshot.child(opponent).getValue(String.class))&& "Ready".equals(dataSnapshot.child(user.getUid()).getValue(String.class))){
//                    findGameBool = true;
                    opponNull = false;
                    if(friendGame) {
                        if(gameMaster){

                            myRef.child("GameRoom").child(user.getUid() + opponent).child(user.getUid()).setValue("Game");
                        }
                        else {
                            myRef.child("GameRoom").child(opponent+user.getUid()).child(user.getUid()).setValue("Game");
                        }
                    }
                }

                if ("Game".equals(dataSnapshot.child(opponent).getValue(String.class))&& "Game".equals(dataSnapshot.child(user.getUid()).getValue(String.class))) {
                    myRef.child("GameRoom").child(opponent + user.getUid()).removeEventListener(startGameListenerIsFind);
                    Log.d("TAGGSS","Старт игры тот кто искал ");
                    opponNull = false;
                    // Попробую обновить PressStart;
//                    pressStart = false;
                    if(!pressStartAnimBool) {pressStartAnim = 100;pressStartAnimBool = true;}
                    startGameBool = true;
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        startGameListenerIsWait = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.child(opponent).getValue(String.class)== null || "Wait".equals(dataSnapshot.child(opponent).getValue(String.class))) opponNull = true;
                if("Ready".equals(dataSnapshot.child(opponent).getValue(String.class))&& "Ready".equals(dataSnapshot.child(user.getUid()).getValue(String.class))) {
                    opponNull = false;
//                    nameOpponent = dataSnapshot.child(opponent+"name").getValue(String.class);

//                    findGameBool = true;
                    if(friendGame) {
                        if(gameMaster){

                            myRef.child("GameRoom").child(user.getUid() + opponent).child(user.getUid()).setValue("Game");
                        }
                        else {
                            myRef.child("GameRoom").child(opponent+user.getUid()).child(user.getUid()).setValue("Game");

                        }
                    }
                }
                if ("Game".equals(dataSnapshot.child(opponent).getValue(String.class))&& "Game".equals(dataSnapshot.child(user.getUid()).getValue(String.class))) {
                    opponNull = false;
                    // Попробую обновить PressStart;
//                    pressStart = false;

                    myRef.child("GameRoom").child(user.getUid() + opponent).removeEventListener(startGameListenerIsWait);
                    startGameBool = true;
                    if(!pressStartAnimBool) {pressStartAnim = 100;pressStartAnimBool = true;}
//                    return;

                }
                if("Game".equals(dataSnapshot.child(opponent).getValue(String.class))) if(!pressStartAnimBool) {pressStartAnim = 100;pressStartAnimBool = true;}
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        findGameListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                pressStartAnimBool = false;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Log.d("FindNewGame", " " + snapshot.getKey());
                    if (user.getUid().equals(snapshot.getKey())) {
                        Log.d("TAGGSS", "Я уже есть в очереди пробую поставить повторно, хотя нет");
                        getWait();
                        return;
                    }
                }
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    if(snapshot.getValue(String.class).length()<10&&(Boolean.toString(modGame)+Integer.toString(numCards)).equals(snapshot.getValue(String.class))) {
                        myRef.child("WaitRoom").removeEventListener(findGameListener);
                        opponent = snapshot.getKey();
                        Log.d("TAGGSS", " " + opponent);
                        myRef.child("WaitRoom").child(opponent).setValue(user.getUid());
                        stopFindGame();
                        findGameBool = true;
                        startGameIsFind();
                        return;
                    }
                }
                getWait();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        waitGameListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue(String.class)!=null && dataSnapshot.getValue(String.class).length()>10) {
                    stopFindGame();
                    findGameBool = true;
                    myRef.child("WaitRoom").child(user.getUid()).removeEventListener(waitGameListener);
                    opponent = dataSnapshot.getValue(String.class);
                    myRef.child("GameRoom").child(user.getUid()+opponent).child(opponent).setValue("Wait");// защита, если там стоит Exit то  тот кто ждал выходит автоматом
//                    myRef.child("GameRoom").child(user.getUid()+opponent).removeValue();

                    myRef.child("GameRoom").child(user.getUid()+opponent).child("gameOver").removeValue();
                    myRef.child("GameRoom").child(user.getUid()+opponent).child("timer").removeValue();
                    myRef.child("GameRoom").child(user.getUid()+opponent).child("Winner").removeValue();
                    myRef.child("GameRoom").child(user.getUid()+opponent).child("test").removeValue();
                    myRef.child("GameRoom").child(user.getUid()+opponent).child("Koloda").removeValue();
                    myRef.child("GameRoom").child(user.getUid()+opponent).child("Hodit").removeValue();
                    myRef.child("GameRoom").child(user.getUid()+opponent).child("Otbivaet").removeValue();
                    myRef.child("GameRoom").child(user.getUid()+opponent).child("Koziri").removeValue();
                    myRef.child("GameRoom").child(user.getUid()+opponent).child("Hod").removeValue();
                    myRef.child("WaitRoom").child(user.getUid()).removeValue();

                    downloadAvatarAndName();
                    Log.d("TAGGSS", "Нашёлся оппонент " + opponent);
                    myRef.child("GameRoom").child(user.getUid()+opponent).child(user.getUid()).setValue("Ready");
//                    myRef.child("GameRoom").child(user.getUid()+opponent).child(user.getUid()+"name").setValue(name);
//                    myRef.child("GameRoom").child(user.getUid()+opponent).child(user.getUid()+"online").onDisconnect().setValue("Disconect");
                    myRef.child("GameRoom").child(user.getUid()+opponent).addValueEventListener(startGameListenerIsWait);
                    myRef.child("GameRoom").child(user.getUid()+opponent).child(opponent).addValueEventListener(exitListener);
                    firstTimer = System.currentTimeMillis()/1000;
                    firstTimerBool = true;

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
    }
    protected void openFriendGame(String oppon, boolean gm) {
        opponent = oppon;
        findGameBool = true;
        startGameBool = false;
        firstTimerBool = false;
        friendGame = true;
        gameMaster = gm;
        if (gameMaster) {
            myRef.child("GameRoom").child(user.getUid() + opponent).child("gameOver").removeValue();
            myRef.child("GameRoom").child(user.getUid() + opponent).child("timer").removeValue();
            myRef.child("GameRoom").child(user.getUid() + opponent).child("Winner").removeValue();
            myRef.child("GameRoom").child(user.getUid() + opponent).child("test").removeValue();
            myRef.child("GameRoom").child(user.getUid() + opponent).child("Koloda").removeValue();
            myRef.child("GameRoom").child(user.getUid() + opponent).child("Hodit").removeValue();
            myRef.child("GameRoom").child(user.getUid() + opponent).child("Otbivaet").removeValue();
            myRef.child("GameRoom").child(user.getUid() + opponent).child("Koziri").removeValue();
            myRef.child("GameRoom").child(user.getUid() + opponent).child("Hod").removeValue();
            myRef.child("WaitRoom").child(user.getUid()).removeValue();
            Log.d("TAGGSS", "Нашёлся оппонент " + opponent);
            myRef.child("GameRoom").child(user.getUid() + opponent).child(opponent).setValue("Wait");
            myRef.child("GameRoom").child(user.getUid() + opponent).child(user.getUid()).setValue("Ready");
//            myRef.child("GameRoom").child(user.getUid() + opponent).child(opponent).setValue("Ready");
            myRef.child("GameRoom").child(user.getUid() + opponent).addValueEventListener(startGameListenerIsWait);
            myRef.child("GameRoom").child(user.getUid() + opponent).child(opponent).addValueEventListener(exitListener);
        }
        else {
            myRef.child("GameRoom").child(opponent+user.getUid()).addValueEventListener(startGameListenerIsFind);
            myRef.child("GameRoom").child(opponent+user.getUid()).child(opponent).addValueEventListener(exitListener);
            myRef.child("GameRoom").child(opponent+user.getUid()).child(user.getUid()).setValue("Ready");
            myRef.child("WaitRoom").child(user.getUid()).removeValue();
        }
        downloadAvatarAndName();
    }
    protected void reOpenGame(boolean GM, String Oppon) {
        gameMaster= GM;
        opponent= Oppon;
        findGameBool = true;
        startGameBool = true;
        firstTimerBool = false;
        if(gameMaster) {
            myRef.child("GameRoom").child(user.getUid() + opponent).child(user.getUid()).setValue("Game");
            myRef.child("GameRoom").child(user.getUid() + opponent).child(opponent).addValueEventListener(exitListener);
//            myRef.child("GameRoom").child(user.getUid()+opponent).child(opponent+"name").addListenerForSingleValueEvent(nameOpponList);
        } else {
            myRef.child("GameRoom").child(opponent+user.getUid()).child(user.getUid()).setValue("Game");
            myRef.child("GameRoom").child(opponent + user.getUid()).child(opponent).addValueEventListener(exitListener);
//            myRef.child("GameRoom").child(opponent + user.getUid()).child(opponent+"name").addListenerForSingleValueEvent(nameOpponList);
        }
        downloadAvatarAndName();
    }
    protected void findGame() {
        if(opponNameBitmap!=null) {
            opponNameBitmap.recycle();
            opponNameBitmap = null;
        }
        if(avatarOpponent != null) {
            avatarOpponent.recycle();
            avatarOpponent = null;
        }
        if(avatarOpponentMenu != null) {
            avatarOpponentMenu.recycle();
            avatarOpponentMenu = null;
        }
        pressStart = false;
        opponentExit = false;
        startGameBool = false;
        findGameBool = false;
        nameOpponent = "";
        firstTimerBool = false;
        avatarOpponent = null;
        avatarOpponentMenu = null;
        myRef.child("WaitRoom").addListenerForSingleValueEvent(findGameListener);
    }

    protected void startGame() {
        opponentExit = false;
        pressStart = true;
        if(gameMaster) {
            myRef.child("GameRoom").child(user.getUid() + opponent).child(user.getUid()).setValue("Game");
        }
        else {
            myRef.child("GameRoom").child(opponent+user.getUid()).child(user.getUid()).setValue("Game");
        }

    }
    protected void stopFindGame() {
        opponentExit = false;
        myRef.child("WaitRoom").child(user.getUid()).removeEventListener(waitGameListener);
    }

    protected void refindFun() {
        if (findGameBool && !startGameBool) {
            findGameBool = false;
            deleteGame();
            findGame();
        }
    }
    protected void exitFind() {
        deleteFind();
        deleteGame();
    }
    public void getWait() {
        gameMaster = true;

        Log.d("TAGGSS", "В ожидании никого нет, встал в очередь");
        //надо встать в очередь
        myRef.child("WaitRoom").child(user.getUid()).removeEventListener(waitGameListener);
        myRef.child("WaitRoom").child(user.getUid()).setValue(Boolean.toString(modGame)+Integer.toString(numCards));
        myRef.child("WaitRoom").child(user.getUid()).addValueEventListener(waitGameListener);
    }

    public void startGameIsFind() {
        gameMaster = false;
        // Две лишние строки это на всякий случай защита от дубликата
        myRef.child("WaitRoom").child(user.getUid()).removeEventListener(waitGameListener);
        myRef.child("WaitRoom").child(user.getUid()).removeValue();
        downloadAvatarAndName();

        try {
            Thread.sleep(1000); //Приостанавливает поток на 1 секунду
        } catch (Exception e) {

        }
        myRef.child("GameRoom").child(opponent+user.getUid()).child(user.getUid()).setValue("Ready");
//        myRef.child("GameRoom").child(opponent+user.getUid()).child(user.getUid()+"name").setValue(name);
//        myRef.child("GameRoom").child(opponent+user.getUid()).child(user.getUid()+"online").onDisconnect().setValue("Disconect");
        myRef.child("GameRoom").child(opponent+user.getUid()).addValueEventListener(startGameListenerIsFind);
        myRef.child("GameRoom").child(opponent+user.getUid()).child(opponent).addValueEventListener(exitListener);
        firstTimer = System.currentTimeMillis()/1000-1;
        firstTimerBool = true;
    }

    public void resume(){
        if (gameMaster){
            myRef.child("GameRoom").child(user.getUid() + opponent).child(opponent).addValueEventListener(exitListener);
//            myRef.child("GameRoom").child(user.getUid() + opponent).removeEventListener(startGameListenerIsWait);
            myRef.child("GameRoom").child(user.getUid()+opponent).child(user.getUid()).setValue("Game");
        }
        else  {
            myRef.child("GameRoom").child(opponent + user.getUid()).child(opponent).addValueEventListener(exitListener);
//            myRef.child("GameRoom").child(opponent + user.getUid()).removeEventListener(startGameListenerIsFind);
            myRef.child("GameRoom").child(opponent+user.getUid()).child(user.getUid()).setValue("Game");
        }
    }

    public void exit() {
        Log.d("testDeb","exit");
        if (gameMaster){
            myRef.child("GameRoom").child(user.getUid() + opponent).child(opponent).removeEventListener(exitListener);
            myRef.child("GameRoom").child(user.getUid() + opponent).removeEventListener(startGameListenerIsWait);
            myRef.child("GameRoom").child(user.getUid()+opponent).child(user.getUid()).setValue("Exit");
        }
        else  {
            myRef.child("GameRoom").child(opponent + user.getUid()).child(opponent).removeEventListener(exitListener);
            myRef.child("GameRoom").child(opponent + user.getUid()).removeEventListener(startGameListenerIsFind);
            myRef.child("GameRoom").child(opponent+user.getUid()).child(user.getUid()).setValue("Exit");
        }

    }
    public void deleteFind() {
        myRef.child("WaitRoom").child(user.getUid()).removeEventListener(waitGameListener);
        myRef.child("WaitRoom").child(user.getUid()).removeValue();
    }
    public void deleteGame() {
        if (gameMaster){
            myRef.child("GameRoom").child(user.getUid() + opponent).child(opponent).removeEventListener(exitListener);
            myRef.child("GameRoom").child(user.getUid() + opponent).removeEventListener(startGameListenerIsWait);
            myRef.child("GameRoom").child(user.getUid() + opponent).removeValue();
        }
        else  {
            myRef.child("GameRoom").child(opponent + user.getUid()).child(opponent).removeEventListener(exitListener);
            myRef.child("GameRoom").child(opponent + user.getUid()).removeEventListener(startGameListenerIsFind);
            myRef.child("GameRoom").child(opponent + user.getUid()).removeValue();
        }

    }
    void addOpponentInFriend() {
        myRef.child("Social").child(opponent).child("RequestsIn").child(user.getUid()).setValue(true);
        myRef.child("Social").child(user.getUid()).child("RequestsOut").child(opponent).setValue(true);

    }
    private Bitmap unionBitmap(String name){
        Paint paint = new Paint();
        if(name == null) name = "Игрок";
        paint.setTextSize(maxX/20);
        paint.setTypeface(ResourcesCompat.getFont(context, R.font.balsamiq_sans_regular));
        paint.setAntiAlias(true);
        Bitmap fonleft = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(
                context.getResources(), R.drawable.fon_name_one),
                maxX/40 , maxX/20,false);
        Bitmap center = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(
                context.getResources(), R.drawable.fon_name_two),
                (int)paint.measureText(name) , maxX/20,false);
        Bitmap fonright = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(
                context.getResources(), R.drawable.fon_name_one),
                -maxX/40 , maxX/20,false);
        Bitmap out = Bitmap.createBitmap(fonleft.getWidth()*2+center.getWidth(),maxX/20, Bitmap.Config.ARGB_8888);
        Canvas unionCan = new Canvas(out);
        unionCan.drawBitmap(fonleft,0,0,paint);
        unionCan.drawBitmap(center,fonleft.getWidth(),0,paint);
        unionCan.drawBitmap(fonright,fonleft.getWidth() + center.getWidth(),0,paint);
        unionCan.drawText(name,maxX/40,maxX/20-maxX/100,paint);
        fonleft.recycle();
        center.recycle();
        fonright.recycle();
        return out;
    }
    protected void deleteFriendGame() {
        if(gameMaster){
            myRef.child("Social").child(opponent).child("Friends").child(user.getUid()).setValue("No1");
        }
    }
    private void downloadAvatarAndName() {
        myRef.child("Names").child(opponent).addListenerForSingleValueEvent(nameOpponList);
        storageRef.child(opponent).getBytes(1024*1024).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                avatarOpponent = Bitmap.createScaledBitmap(BitmapFactory.decodeByteArray(bytes,0,bytes.length),
                        maxY/9, maxY/9,true);
                avatarOpponentMenu = Bitmap.createScaledBitmap(BitmapFactory.decodeByteArray(bytes,0,bytes.length),
                        maxY/8, maxY/8,true);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                avatarOpponent = null;
                avatarOpponentMenu = null;
            }
        });

    }
}
