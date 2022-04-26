package ru.tmkstd.cardgamedurakonline.Fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.ArrayMap;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ru.tmkstd.cardgamedurakonline.GameEngineActivity;
import ru.tmkstd.cardgamedurakonline.MainActivity;
import ru.tmkstd.cardgamedurakonline.Notification.Client;
import ru.tmkstd.cardgamedurakonline.Notification.Data;
import ru.tmkstd.cardgamedurakonline.Notification.MyResponse;
import ru.tmkstd.cardgamedurakonline.Notification.Sender;
import ru.tmkstd.cardgamedurakonline.R;


public class FriendsFragment extends Fragment {
    DatabaseReference ref;
    public ArrayList<String> friendsUsers;
    ArrayList<ValueEventListener> listeners;
    ArrayMap<String,String> allUsers;
    LinearLayout linearLayout;
    Context context;
    StorageReference storageRef;
    ValueEventListener statusFriendsListener, gameListener;
    float scale;
    String myKey;
    SharedPreferences save;
    boolean modeGame;
    int numberCards;

    APIService apiService;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_friends, container, false);
        linearLayout = rootView.findViewById(R.id.linLay);
        startFriends();
        return rootView;
    }

    public FriendsFragment(Context con, ArrayList<String> users, ArrayMap<String,String > names,String MK) {
        friendsUsers = users;
        allUsers = names;
        context = con;
        storageRef = FirebaseStorage.getInstance().getReference();
        scale = context.getResources().getDisplayMetrics().density;
        listeners = new ArrayList<>();

        save = con.getSharedPreferences("Save", con.MODE_PRIVATE);

        modeGame = save.getBoolean("modGame",true);
        numberCards = save.getInt("numCards",36);

        ref = FirebaseDatabase.getInstance().getReference();
        myKey = MK;
        if(Build.VERSION.SDK_INT >= 21) {
            apiService = Client.getClient("https://fcm.googleapis.com/").create(APIService.class);
        }
    }
    void addListener(String key) {

        ref.child("Social").child(key).child("Friends").child(myKey).setValue("1");
        gameListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue(String.class)!=null) {
//                    String key2 = new String(key);
                    if (dataSnapshot.getValue(String.class).equals("No")) {
                        ref.child("Social").child(key).child("Friends").child(myKey).removeEventListener(gameListener);
                        Toast.makeText(context,"Пользователь отказался", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(context, MainActivity.class) ;
//                        overridePendingTransition(0,0);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }
                    if (dataSnapshot.getValue(String.class).equals("No1")) {
                        ref.child("Social").child(key).child("Friends").child(myKey).removeEventListener(gameListener);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };

        ref.child("Social").child(key).child("Friends").child(myKey).addValueEventListener(gameListener);
    }

    void addStatusUsers(TextView status, ImageView statImage,String userKey) {
        statusFriendsListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d("listenerF","xxxxx");
                if(dataSnapshot.getValue(Boolean.class)!=null) {
                    if(dataSnapshot.getValue(Boolean.class)) {
                        status.setText("Online");
                        statImage.setBackgroundResource(R.drawable.status_on);
                    }
                    else {
                        status.setText("Offline");
                        statImage.setBackgroundResource(R.drawable.status_off);
                    }
                }
                else {
                    status.setText("Offline");
                    statImage.setBackgroundResource(R.drawable.status_off);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        listeners.add(statusFriendsListener);

        ref.child("Social").child(userKey).child("Status").addValueEventListener(statusFriendsListener);
    }
    public void addUsers(ArrayList<String> users,int state) {
        if(state!=0) {
            deleteListeners();
            friendsUsers = users;
            startFriends();
        }
        friendsUsers = users;
    }
    public void deleteListeners() {

        for(String key: friendsUsers) {
            if(friendsUsers.indexOf(key)<listeners.size()) {
                ref.child("Social").child(key).child("Status").removeEventListener(listeners.get(friendsUsers.indexOf(key)));
            }
        }
    }
    public void addAllUsers(ArrayMap<String,String> users) {
        allUsers = users;
    }
    private void startFriends() {
        Log.d("Stranno","6");
        linearLayout.removeAllViews();
        listeners.clear();
        for(String userKay: friendsUsers) {
            linearLayout.addView(createUserFriendCard(userKay));
        }
        Log.d("Stranno","7");
    }
    private View createUserFriendCard(String key){
//        FrameLayout conteiner = new FrameLayout(context);
//        FrameLayout.LayoutParams paramsConteiner = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//        conteiner.setPadding(0,0,0,(int)(8*scale));
////        conteiner.setOrientation(LinearLayout.HORIZONTAL);
//        conteiner.setLayoutParams(paramsConteiner);

        RelativeLayout relativeLayout = new RelativeLayout(context);
        RelativeLayout.LayoutParams relativeParam = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        relativeLayout.setBackgroundResource(R.drawable.background_with_shadow);
        relativeParam.setMargins(0,(int)(10*scale),0,0);
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

        TextView status = new TextView(context);
        RelativeLayout.LayoutParams paramsStatus = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        paramsStatus.addRule(RelativeLayout.RIGHT_OF,avatar.getId());
        paramsStatus.addRule(RelativeLayout.BELOW,name.getId());
        paramsStatus.leftMargin = (int) (30*scale);
        status.setId(View.generateViewId());
        status.setTextSize(TypedValue.COMPLEX_UNIT_SP,20);
        status.setTextColor(Color.argb(255, 0, 0, 0));

        ImageView statusImage = new ImageView(context);
        RelativeLayout.LayoutParams paramsStatusImage = new RelativeLayout.LayoutParams((int) (15*scale),(int) (15*scale));
        paramsStatusImage.leftMargin = (int) (12*scale);
        paramsStatusImage.bottomMargin = (int) (4*scale);
        paramsStatusImage.addRule(RelativeLayout.RIGHT_OF,avatar.getId());
        paramsStatusImage.addRule(RelativeLayout.ALIGN_BOTTOM,status.getId());


        addStatusUsers(status,statusImage,key);
        relativeLayout.addView(status,paramsStatus);
        relativeLayout.addView(statusImage,paramsStatusImage);

        ImageView menuImage = new ImageView(context);
        RelativeLayout.LayoutParams paramsMenuImage =  new RelativeLayout.LayoutParams((int) (4*scale),(int) (20*scale));
        paramsMenuImage.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        paramsMenuImage.rightMargin = (int) (15*scale);
        paramsMenuImage.topMargin = (int) (15*scale);
        menuImage.setBackgroundResource(R.drawable.menu);
        relativeLayout.addView(menuImage,paramsMenuImage);
//        conteiner.addView(relativeLayout);
//        String opponKey = new String(key);
        relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder quitDialog = new AlertDialog.Builder(context);
                quitDialog.setTitle("Игрок \""+allUsers.get(key)+"\"");
                quitDialog.setPositiveButton("Удалить", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        AlertDialog.Builder quitDialog2 = new AlertDialog.Builder(context);
                        quitDialog2.setTitle("Удалить из друзей?");
                        quitDialog2.setPositiveButton("Удалить", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    ref.child("Social").child(myKey).child("Friends").child(key).removeValue();
                                    ref.child("Social").child(key).child("Friends").child(myKey).removeValue();
                                    linearLayout.removeView(relativeLayout);
                                }
                            });
                        quitDialog2.setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            });
                        quitDialog2.show();
                    }
                });
                quitDialog.setNegativeButton("Написать", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(context,"Ожидается в след. обновлениях", Toast.LENGTH_SHORT).show();
                    }
                });
                quitDialog.setNeutralButton("Играть",new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(apiService!=null) {
                            ref.child("Social").child(key).child("Token").addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.getValue(String.class) != null) {
                                        //Сначало мой ID потом кому ID
                                        Data data = new Data(myKey, allUsers.get(myKey), "Game" + modeGame + numberCards, key);
//                                    Data data = new Data(myKey,allUsers.get(myKey),"Game",key);
                                        //myToken?????
//        String token ="dWJpiCJhwqs:APA91bF4Q3NQHbxI_Y7PnmnvL7FziU8_6fvjC2BaRFEmNAY-VJ4Cg39zrW4oF6R0Rz9ZwpXbi6oGQrM0omvC2h5eMbUIuFHUeM0ZRI0T2Nl4P8b-1Nsz_5NytrbyTO5vMzeGvY7GWV5V";
                                        //opponToken
                                        String token = dataSnapshot.getValue(String.class);
                                        Sender sender = new Sender(data, token);
                                        apiService.sendNotification(sender)
                                                .enqueue(new Callback<MyResponse>() {
                                                    @Override
                                                    public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                                                        Log.d("apiSerInFrFrag", "1 |" + response.code() + "| 2 |" + response.body().success + "|");
                                                        if (response.code() == 200) {
                                                            if (response.body().success != 1) {

                                                            }
                                                        }
                                                        //Тут загружаеться игра поле
                                                    }

                                                    @Override
                                                    public void onFailure(Call<MyResponse> call, Throwable t) {

                                                    }

                                                });
                                        addListener(key);
                                        Toast.makeText(context, "Приглашение отправлено", Toast.LENGTH_SHORT).show();

                                        Intent intent = new Intent(context, GameEngineActivity.class);
                                        intent.putExtra("friend", true);
                                        intent.putExtra("friendId", key);
                                        intent.putExtra("gameMaster", true);
                                        intent.putExtra("mode", modeGame);
                                        intent.putExtra("number", numberCards);
//                                                            overridePendingTransition(0, 0);
//                                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                        startActivity(intent);

                                    } else {
                                        Toast.makeText(context, "Оппонент не обновил приложение", Toast.LENGTH_SHORT).show();
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                        }
                        else {
                            Toast.makeText(context,"Доступно на Android 5 и выше", Toast.LENGTH_LONG).show();
                        }

                    }
                });
                quitDialog.show();

            }
        });
        storageRef.child(key).getBytes(512*512).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                    @Override
                    public void onSuccess(byte[] bytes) {
                        try {
                            Bitmap src = BitmapFactory.decodeByteArray(bytes,0,bytes.length);
                            Drawable drawable= new BitmapDrawable(context.getResources(),src);
                            avatar.setBackground(drawable);
//                            Resources res = getResources();
//                            Bitmap src = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
//                            RoundedBitmapDrawable drawable = RoundedBitmapDrawableFactory.create(res, src);
//                            drawable.setCircular(true);
//                            drawable.setCornerRadius(25);
//                            avatar.setBackground(drawable);
                        } catch (Exception e) {

                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        avatar.setBackgroundResource(R.drawable.avatar_two);
                    }
                });
        return relativeLayout;
    }
}
