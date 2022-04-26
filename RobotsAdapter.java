package ru.tmkstd.cardgamedurakonline;

import android.util.Log;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class RobotsAdapter {
//    LinearLayout parentLayout1;
//    LinearLayout parentLayout2;
//    RobotClass robot1, robot2;
    RobotClass[] robots;
    DatabaseReference myRef;
    ValueEventListener waitRoomListener;
    double randomRobot;

    RobotsAdapter(LinearLayout la0, LinearLayout la1, LinearLayout la2, LinearLayout la3){
//        parentLayout1 = la1;
//        parentLayout2 = la2;
//        robot1 = new RobotClass(parentLayout1);
//        robot2 = new RobotClass(parentLayout2);
        robots = new RobotClass[4];
        robots[0] = new RobotClass(la0);
        robots[1] = new RobotClass(la1);
        robots[2] = new RobotClass(la2);
        robots[3] = new RobotClass(la3);
        robots[0].createLich(new String[]{"-00000000000",
                "-00000000001","-00000000002","-00000000003",
                "-00000000004","-00000000005","-00000000006",
                "-00000000007","-00000000008","-00000000009",
                "-00000000010", "-00000000011","-00000000012"});
        robots[1].createLich(new String[]{"-00000000000",
                "-00000000013","-00000000014","-00000000015",
                "-00000000016","-00000000017","-00000000018",
                "-00000000019","-00000000020","-00000000021",
                "-00000000022", "-00000000023","-00000000024"});
        // дополнительный робот с личностью
//        robots[1].createLich(new String[]{"-00000000000",
//                "-00000000013","-00000000014","-00000000015",
//                "-00000000016","-00000000017","-00000000018",
//                "-00000000019","-00000000020","-00000000021",
//                "-00000000022", "-00000000023","-00000000024"});
        robots[2].createLich(new String[]{"-00000000000"});
        robots[3].createLich(new String[]{"-00000000000"});
        myRef = FirebaseDatabase.getInstance().getReference();
        InitListener();
        deleteAll();//Обнуение всех счетчиков;
        startRobotsWork();
    }
    void InitListener() {
        waitRoomListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d("RandomRobot","first " + dataSnapshot.getChildrenCount());
                if(dataSnapshot.getChildrenCount()>0){
//                    Log.d("RandomRobot","2");
                    for(DataSnapshot snapshot: dataSnapshot.getChildren()) {
//                        Log.d("RandomRobot","3");
//                        gHbzooLJkhVkQULluIg9hatYNdj2 мой ID Аура
                        if(
//                            Проверка только для своих ID ( вроде, мой, нексус, и ещё хз)
//                            (snapshot.getKey().equals("gHbzooLJkhVkQULluIg9hatYNdj2") || snapshot.getKey().equals("0NdpuHqYmKT1r1kBjiWMvLWigR03") ||  snapshot.getKey().equals("jxNlGPvhWISNq0vWEJDMiICI3UI2")) &&
                                        snapshot.getValue(String.class).length()>3 && snapshot.getValue(String.class).length()<10){
                            //Сначала проверка роботов которые сейчас играют
                            if(robots[3].robotTimerStartGame+10000<System.currentTimeMillis() && robots[3].opponentId!=null && !snapshot.getKey().equals(robots[3].opponentId)) {
                                if(snapshot.getValue(String.class).indexOf(String.valueOf(robots[3].numCards))!=-1 && snapshot.getValue(String.class).indexOf(String.valueOf(robots[3].modGame))!=-1){
                                    robots[3].whiteFlagAndEndGame();
                                    return;
                                }
                            }
                            if(robots[2].robotTimerStartGame+10000<System.currentTimeMillis() && robots[2].opponentId!=null && !snapshot.getKey().equals(robots[2].opponentId)) {
                                if(snapshot.getValue(String.class).indexOf(String.valueOf(robots[2].numCards))!=-1 && snapshot.getValue(String.class).indexOf(String.valueOf(robots[2].modGame))!=-1){
                                    robots[2].whiteFlagAndEndGame();
                                    return;
                                }
                            }
                            //потом попадает на робота с личностями, где 50% личность принимает
                            if(robots[0].opponentId == null && Math.random()>0.5f) {
                                robots[0].startWait(snapshot.getKey(), snapshot.getValue(String.class),(int)(Math.random()*9)+(int)(Math.random()*2)+1);
//                                robots[0].startWait(snapshot.getKey(), snapshot.getValue(String.class),1);
                                return;
                            }
                            //новая личность 30%
                            if(robots[1].opponentId == null && Math.random()>0.7f) {
                                robots[1].startWait(snapshot.getKey(), snapshot.getValue(String.class),(int)(Math.random()*9)+(int)(Math.random()*2)+1);
                                return;
                            }
                            //и последнее перекидывает на безличных роботов
                            if(snapshot.getKey().equals(robots[3].opponentId)){
                                robots[3].startWait(snapshot.getKey(),snapshot.getValue(String.class),100);
                                return;
                            }
                            if(snapshot.getKey().equals(robots[2].opponentId)){
                                robots[2].startWait(snapshot.getKey(),snapshot.getValue(String.class),100);
                                return;
                            }
                            if (robots[3].opponentId == null && robots[2].opponentId == null) {
                                randomRobot = Math.random();
                                Log.d("RandomRobot",randomRobot + " " + (randomRobot<0.5));
                                if (randomRobot < 0.5f) {
                                    robots[3].startWait(snapshot.getKey(), snapshot.getValue(String.class),100);
                                    return;
                                }
                                else {
                                    robots[2].startWait(snapshot.getKey(), snapshot.getValue(String.class),100);
                                    return;
                                }
                            }
                            else {
                                if (robots[3].opponentId == null) {
                                    robots[3].startWait(snapshot.getKey(),snapshot.getValue(String.class),100);
                                    return;
                                }
                                else if (robots[2].opponentId == null) {
                                    robots[2].startWait(snapshot.getKey(),snapshot.getValue(String.class),100);
                                    return;
                                }
                            }
                            //на случай если игрока никто не взял, а личность свободна
                            if(robots[0].opponentId == null) {
                                robots[0].startWait(snapshot.getKey(), snapshot.getValue(String.class),(int)(Math.random()*9)+(int)(Math.random()*2)+1);
                                return;
                            }
                            if(robots[1].opponentId == null) {
                                robots[1].startWait(snapshot.getKey(), snapshot.getValue(String.class),(int)(Math.random()*9)+(int)(Math.random()*2)+1);
                                return;
                            }
                        }
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };

    }
    void startRobotsWork(){
        myRef.child("WaitRoom").addValueEventListener(waitRoomListener);
    }
    void deleteAll(){
        myRef.child("WaitRoom").removeEventListener(waitRoomListener);
//        robot1.deleteAll();
//        robot2.deleteAll();
        for(RobotClass robot: robots) {
            robot.deleteAll();
        }
    }
}
