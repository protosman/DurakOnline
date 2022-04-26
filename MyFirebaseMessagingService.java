package ru.tmkstd.cardgamedurakonline;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        String sented = remoteMessage.getData().get("sented");

        FirebaseUser fbUser = FirebaseAuth.getInstance().getCurrentUser();

        if(fbUser!=null && sented.equals(fbUser.getUid())) {
            sendNotification(remoteMessage);
        }
    }

    private void sendNotification(RemoteMessage remoteMessage) {
        String title = remoteMessage.getData().get("title");
        String text = remoteMessage.getData().get("body");
        String opponId = remoteMessage.getData().get("user");

//        RemoteMessage.Notification notification = remoteMessage.getNotification();

        NotificationHelper.displayNotification(getApplicationContext(),title,text,opponId);
    }

    @Override
    public void onNewToken(String token) {
        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user!=null)
        myRef.child("Social").child(user.getUid()).child("Token").setValue(token);
    }
}
