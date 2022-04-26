package ru.tmkstd.cardgamedurakonline;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.AudioAttributes;
import android.media.RingtoneManager;
import android.os.Build;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class NotificationHelper {
    public static String CHANNEL_ID = "CardGameDurakOnline";
    public static String MY_MESSAGGE_FILTER = "ru.tmkstd.cardgamedurakonline.action";

    public static ValueEventListener gameListener;


    public static void displayNotification(Context context, String title, String body,String opponID) {

        if(title.indexOf("Game")!=-1 || title.equals("Game")) {
            Intent i = new Intent(context, MyService.class);
            i.putExtra("notifi",true);
            i.putExtra("friendId",opponID);
            PendingIntent noIntent = PendingIntent.getService(context,0,i,PendingIntent.FLAG_UPDATE_CURRENT);


            Intent intentYes = new Intent(context, GameEngineActivity.class);
            intentYes.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            if(title.length()>7) {
                if (title.indexOf("true") != -1) intentYes.putExtra("mode", true);
                else intentYes.putExtra("mode", false);
//                if (title.indexOf("false") != -1) intentYes.putExtra("mode", false);
                if (title.indexOf("24") != -1) intentYes.putExtra("number", 24);
                else if (title.indexOf("36") != -1) intentYes.putExtra("number", 36);
                else intentYes.putExtra("number", 52);
//                if (title.indexOf("52") != -1) intentYes.putExtra("mode", 52);
//                Log.d("nutifDeb",title);
            }
            intentYes.putExtra("friend",true);
            intentYes.putExtra("friendId",opponID);
            intentYes.putExtra("gameMaster",false);
            PendingIntent pendingIntentYes = PendingIntent.getActivity(context, 0, intentYes, PendingIntent.FLAG_UPDATE_CURRENT);

            FirebaseUser fbUser = FirebaseAuth.getInstance().getCurrentUser();
            if(fbUser!=null) {
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
                gameListener = new  ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.getValue(String.class)!= null) {
                            if(dataSnapshot.getValue(String.class).equals("No1")) {

                                NotificationManager notificationManager =
                                        (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
                                notificationManager.cancel(1);
                                Toast.makeText(context,"Пользователь ушёл", Toast.LENGTH_SHORT).show();
                                ref.child("Social").child(fbUser.getUid()).child("Friends").child(opponID).removeEventListener(gameListener);

                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                };
                ref.child("Social").child(fbUser.getUid()).child("Friends").child(opponID).addValueEventListener(gameListener);
            }

            NotificationCompat.Builder mBuilder =
                    new NotificationCompat.Builder(context, CHANNEL_ID)
                            .setSmallIcon(R.drawable.avatar_two)
                            .setLargeIcon(BitmapFactory.decodeResource(context.getResources(),R.mipmap.ic_launcher_round))
//                            .setContentIntent(contentIntent)
                            .setContentTitle("Дурак Онлайн")
                            .setContentText("Вам брошен вызов от: \""+body+"\"")
                            .setDefaults(Notification.DEFAULT_ALL)
                            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                            .setPriority(NotificationCompat.PRIORITY_MAX)
                            .addAction(R.drawable.add,"Принять",pendingIntentYes)
//                            .addAction(R.drawable.avatar_two,"Отказаться",actionIntent);
                            .addAction(R.drawable.cancel,"Отказаться",noIntent);

            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            createChannelIfNeeded(notificationManager, CHANNEL_ID);

            notificationManager.notify(1, mBuilder.build());
        }
    }

    static void createChannelIfNeeded(NotificationManager manager, String channel) {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(channel,channel, NotificationManager.IMPORTANCE_DEFAULT);
            notificationChannel.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION), new AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION_RINGTONE)
                    .build());
            manager.createNotificationChannel(notificationChannel);
        }
    }
}
