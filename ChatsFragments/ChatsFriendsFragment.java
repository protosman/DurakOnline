package ru.tmkstd.cardgamedurakonline.ChatsFragments;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;

import ru.tmkstd.cardgamedurakonline.R;


public class ChatsFriendsFragment extends Fragment {

    LinearLayout mainContainer;
    DatabaseReference ref;
    StorageReference storageRef;
    String myKeyId;
    float scale;
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_chats_friends, container, false);
        mainContainer = rootView.findViewById(R.id.fragment_chats_friends_container);
        startFriends();
        return rootView;
    }

    public ChatsFriendsFragment(DatabaseReference ref,StorageReference storageRef, String myId, float scale) {

        this.storageRef = storageRef;
        this.ref = ref;
        myKeyId=myId;
        this.scale=scale;
    }

    public void startFriends() {

        if(myKeyId!=null) {
            ref.child("Social").child(myKeyId).child("Friends").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    mainContainer.removeAllViews();
                    if(dataSnapshot == null || dataSnapshot.getChildrenCount() == 0) {
//                    if(true) {
                        addClearFriendsBanner();
                        return;
                    }
                    if(dataSnapshot!=null) {
                        for(DataSnapshot friendKey : dataSnapshot.getChildren()){
                            addPiople(friendKey.getKey());
//                            addPiople(friendKey.getKey());
//                            friendsContainer.addView(createUserFriendCard(friendKey.getKey()));
//                            friendsContainer.addView(createUserFriendCard(friendKey.getKey()));
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }


    private void addPiople(String idFriend) {

//        idFriend = "0";//проверка на неизвестный id(просто ничего не происходит), просто надо сделать проверку string на null
        RelativeLayout container = new RelativeLayout(getContext());
        RelativeLayout.LayoutParams paramsContainer = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        paramsContainer.setMargins(0,(int)(10*scale),0,0);
        container.setBackgroundResource(R.drawable.background_with_shadow);
        container.setLayoutParams(paramsContainer);

        ImageView avatar = new ImageView(getContext());
        RelativeLayout.LayoutParams paramsAvatar = new RelativeLayout.LayoutParams((int)(80*scale),(int)(80*scale));
        paramsAvatar.leftMargin =(int) (4*scale);
        paramsAvatar.topMargin = (int) (4*scale);
        paramsAvatar.bottomMargin = (int) (8*scale);
        avatar.setBackgroundResource(R.drawable.avatar_two);
        avatar.setId(View.generateViewId());
        container.addView(avatar,paramsAvatar);

        TextView name = new TextView(getContext());
        RelativeLayout.LayoutParams paramsName = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        paramsName.leftMargin = (int) (15*scale);
        paramsName.topMargin = (int) (8*scale);
        paramsName.addRule(RelativeLayout.RIGHT_OF,avatar.getId());
        name.setText("Игрок");
        name.setId(View.generateViewId());
        name.setTextSize(TypedValue.COMPLEX_UNIT_SP, 30);
        name.setTextColor(Color.argb(255, 0, 0, 0));
        container.addView(name,paramsName);

        TextView status = new TextView(getContext());
        RelativeLayout.LayoutParams paramsStatus = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        paramsStatus.addRule(RelativeLayout.CENTER_HORIZONTAL);
        paramsStatus.addRule(RelativeLayout.ALIGN_BOTTOM,avatar.getId());
        paramsStatus.bottomMargin=(int)(10*scale);
        status.setText("Открыть диалог");
        status.setId(View.generateViewId());
        status.setTextSize(TypedValue.COMPLEX_UNIT_SP,15);
        status.setLayoutParams(paramsStatus);
        container.addView(status);

        ImageView sendImage = new ImageView(getContext());
        RelativeLayout.LayoutParams paramsSendImage = new RelativeLayout.LayoutParams((int)(40*scale), (int)(40*scale));
        paramsSendImage.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        paramsSendImage.addRule(RelativeLayout.CENTER_VERTICAL);
        paramsSendImage.rightMargin = (int)(10*scale);
        sendImage.setBackgroundResource(R.drawable.send_icon);
        container.addView(sendImage,paramsSendImage);

        mainContainer.addView(container);
        ref.child("Names").child(idFriend).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue(String.class)!=null)
                name.setText(dataSnapshot.getValue(String.class));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                name.setText("Игрок");
            }
        });

        storageRef.child(idFriend).getBytes(512*512).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                try {
                    Bitmap src = BitmapFactory.decodeByteArray(bytes,0,bytes.length);
                    Drawable drawable= new BitmapDrawable(getContext().getResources(),src);
                    avatar.setBackground(drawable);
                } catch (Exception e) {

                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                avatar.setBackgroundResource(R.drawable.avatar_two);
            }
        });

    }
    private void addClearFriendsBanner() {

        TextView status = new TextView(getContext());
        LinearLayout.LayoutParams paramsStatus = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        paramsStatus.gravity = Gravity.CENTER;
        paramsStatus.topMargin = (int)(10*scale);
//        RelativeLayout.LayoutParams paramsStatus = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
//        paramsStatus.addRule(RelativeLayout.CENTER_HORIZONTAL);
//        paramsStatus.addRule(RelativeLayout.ALIGN_BOTTOM,avatar.getId());
//        paramsStatus.bottomMargin=(int)(10*scale);
        status.setText("Ваш список пуст");
        status.setId(View.generateViewId());
        status.setGravity(Gravity.CENTER);
        status.setTextSize(TypedValue.COMPLEX_UNIT_SP,30);
//        status.setLayoutParams(paramsStatus);
        mainContainer.addView(status,paramsStatus);

    }

}
