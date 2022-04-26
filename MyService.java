package ru.tmkstd.cardgamedurakonline;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MyService extends Service {
    public MyService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if(intent==null||intent.getBooleanExtra("notifi",false)) {
//            Toast.makeText(this, "Don't panik but your time is up!!!!.",
//                    Toast.LENGTH_LONG).show();
            NotificationManager notificationManager =
                    (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            notificationManager.cancel(1);
            FirebaseUser fbUser = FirebaseAuth.getInstance().getCurrentUser();
            if(fbUser!=null) {
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
                ref.child("Social").child(fbUser.getUid()).child("Friends").child(intent.getStringExtra("friendId")).setValue("No");
            }

        }

        super.onDestroy();
        return START_NOT_STICKY ;
    }
}
