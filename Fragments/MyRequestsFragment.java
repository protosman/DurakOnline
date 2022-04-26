package ru.tmkstd.cardgamedurakonline.Fragments;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;
import androidx.fragment.app.Fragment;

import android.util.ArrayMap;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

import ru.tmkstd.cardgamedurakonline.R;

public class MyRequestsFragment extends Fragment {
    ArrayList<String> myReqUsers;
    ArrayMap<String,String> allUsers;
    LinearLayout linearLayout;
    Context context;
    StorageReference storageRef;
    DatabaseReference ref;
    float scale;
    String myKey;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_my_requests, container, false);
        linearLayout = rootView.findViewById(R.id.linMyReq);
        startMyReqs();
        return rootView;
    }
    public MyRequestsFragment(Context con, ArrayList<String> users, ArrayMap<String,String > names,String MK) {
        myReqUsers = users;
        allUsers = names;
        context = con;
        scale = context.getResources().getDisplayMetrics().density;
        storageRef = FirebaseStorage.getInstance().getReference();
        ref = FirebaseDatabase.getInstance().getReference();
        myKey = MK;
    }
    public void addUsers(ArrayList<String> users, int state) {
        myReqUsers = users;
        if(state!=3) {
            startMyReqs();
        }
    }
    public void addAllUsers(ArrayMap<String,String> users) {
        allUsers = users;
    }
    void startMyReqs() {
        if(linearLayout != null) {
            linearLayout.removeAllViews();
            for (String userKay : myReqUsers) {
                linearLayout.addView(createMyReqUserCard(userKay));
            }
        }

    }

    private View createMyReqUserCard(String key){
        FrameLayout conteiner = new FrameLayout(context);
        FrameLayout.LayoutParams paramsConteiner = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        conteiner.setPadding(0,0,0,(int)(8*scale));
//        conteiner.setOrientation(LinearLayout.HORIZONTAL);
        conteiner.setLayoutParams(paramsConteiner);

        RelativeLayout relativeLayout = new RelativeLayout(context);
        RelativeLayout.LayoutParams relativeParam = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        relativeLayout.setBackgroundResource(R.drawable.background_with_shadow);
        relativeLayout.setLayoutParams(relativeParam);

        ImageView avatar = new ImageView(context);
        RelativeLayout.LayoutParams paramsAvatar = new RelativeLayout.LayoutParams((int)(70*scale),(int)(70*scale));
        paramsAvatar.leftMargin =(int) (8*scale);
        paramsAvatar.topMargin = (int) (4*scale);
        paramsAvatar.bottomMargin = (int) (10*scale);
        avatar.setBackgroundResource(R.drawable.avatar_two);
        avatar.setId(View.generateViewId());
        relativeLayout.addView(avatar,paramsAvatar);

        TextView name = new TextView(context);
        RelativeLayout.LayoutParams paramsName = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        paramsName.addRule(RelativeLayout.RIGHT_OF,avatar.getId());
        paramsName.leftMargin = (int) (20*scale);
        paramsName.topMargin = (int) (8*scale);
        name.setText(allUsers.get(key));
        name.setId(View.generateViewId());
        name.setTextSize(TypedValue.COMPLEX_UNIT_SP, 30);
        name.setTextColor(Color.argb(255, 0, 0, 0));
        relativeLayout.addView(name,paramsName);

        Button btnRequestInFriend = new Button(context);
        RelativeLayout.LayoutParams paramsbtnRequestInFriend =  new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        paramsbtnRequestInFriend.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        paramsbtnRequestInFriend.addRule(RelativeLayout.BELOW, name.getId());
        paramsbtnRequestInFriend.rightMargin = (int) (4*scale);
        paramsbtnRequestInFriend.bottomMargin = (int)(4*scale);
        btnRequestInFriend.setText("Отменить запрос");
        btnRequestInFriend.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        btnRequestInFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ref.child("Social").child(myKey).child("RequestsOut").child(key).removeValue();
                ref.child("Social").child(key).child("RequestsIn").child(myKey).removeValue();
                linearLayout.removeView(conteiner);
            }
        });


        relativeLayout.addView(btnRequestInFriend,paramsbtnRequestInFriend);

        conteiner.addView(relativeLayout);
//
        storageRef.child(key).getBytes(512*512).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                try {
                    Bitmap src = BitmapFactory.decodeByteArray(bytes,0,bytes.length);
                    Drawable drawable= new BitmapDrawable(context.getResources(),src);
                    avatar.setBackground(drawable);
//                    Resources res = context.getResources();
//                    Bitmap src = BitmapFactory.decodeByteArray(bytes,0,bytes.length);
//                    RoundedBitmapDrawable drawable = RoundedBitmapDrawableFactory.create(res,src);
//                    drawable.setCircular(true);
//                    drawable.setCornerRadius(25);
//                    avatar.setBackground(drawable);

                } catch (Exception e){

                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                avatar.setBackgroundResource(R.drawable.avatar_two);
            }
        });
        return conteiner;
    }
}
