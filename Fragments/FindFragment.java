package ru.tmkstd.cardgamedurakonline.Fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;
import androidx.fragment.app.Fragment;

import android.text.InputFilter;
import android.text.InputType;
import android.util.ArrayMap;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Collections;

import ru.tmkstd.cardgamedurakonline.R;

public class FindFragment extends Fragment {
    ArrayList<String> myReqUsers;
    ArrayList<String> friendsUsers;
    ArrayList<String> reqUsers;
    TextView randomUsers;
    ArrayMap<String,String> allUsers;
    LinearLayout linearLayout;
    EditText findNameText;
    ImageView btnRefresh, btnFind;
    Context context;
    StorageReference storageRef;
    float scale;
    DatabaseReference ref;
    String myKey;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_find, container, false);
//        listView = rootView.findViewById(R.id.listView);
//        ArrayAdapter<String> myAdapter = new MyAdapter(context,friendsUsers);
//        listView.setAdapter(myAdapter);
        linearLayout = rootView.findViewById(R.id.linLay);
        btnFind = rootView.findViewById(R.id.mark_find_find);
        btnRefresh = rootView.findViewById(R.id.mark_find_refresh);
        findNameText = rootView.findViewById(R.id.mark_find_edit_text);
        findNameText.clearFocus();
        findNameText.setCursorVisible(false);
        findNameText.setSingleLine(true);
        findNameText.setFilters(new InputFilter[] {new InputFilter.LengthFilter(10)});
        findNameText.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
        findNameText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if(event.getAction() == KeyEvent.ACTION_DOWN && keyCode==KeyEvent.KEYCODE_ENTER){
                    findNameText.setCursorVisible(false);
                    findNameText.clearFocus();

                    //Скрыть клавиатуру
//                getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
                    InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
////                    View view = context.getCurrentFocus();
////                    if (view == null) {
//                View view = new View(context);
////                    }
                    imm.hideSoftInputFromWindow(rootView.getWindowToken(), 0);

                    startFragmentView(findNameText.getText().toString());
                    return true;
                }
                return false;
            }
        });
//        findNameText.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                findNameText.setCursorVisible(true);
//                Log.d("fragmetnTEst","test1");
//            }
//        });
        findNameText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    findNameText.setCursorVisible(true);
                }
                else {
                    findNameText.setCursorVisible(false);
                    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && hasFocus)
                    {
                        getActivity().getWindow().getDecorView().setSystemUiVisibility(
                                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
                    }
                }
            }
        });
        btnFind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findNameText.setCursorVisible(false);
                findNameText.clearFocus();

                //Скрыть клавиатуру

//                getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
                InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
////                    View view = context.getCurrentFocus();
////                    if (view == null) {
//                View view = new View(context);
////                    }
                imm.hideSoftInputFromWindow(rootView.getWindowToken(), 0);
                startFragmentView(findNameText.getText().toString());
            }
        });
        btnRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findNameText.setText("");
                startFragmentView(null);
            }
        });
        btnFind.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction()==MotionEvent.ACTION_DOWN) {
                    btnFind.setScaleX(0.9f);
                    btnFind.setScaleY(0.9f);
                    btnFind.setRotation(-5);
                }
                if(event.getAction()==MotionEvent.ACTION_UP) {
                    btnFind.setScaleX(1);
                    btnFind.setScaleY(1);
                    btnFind.setRotation(0);
                }
                return false;
            }
        });
        btnRefresh.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction()==MotionEvent.ACTION_DOWN) {
                    btnRefresh.setScaleX(0.9f);
                    btnRefresh.setScaleY(0.9f);
                    btnRefresh.setRotation(-5);
                }
                if(event.getAction()==MotionEvent.ACTION_UP) {
                    btnRefresh.setScaleX(1);
                    btnRefresh.setScaleY(1);
                    btnRefresh.setRotation(0);
                }
                return false;
            }
        });
        startFragmentView(null);

        return rootView;
    }
    public FindFragment(Context con, ArrayList<String> fri, ArrayList<String> req, ArrayList<String> myReq, ArrayMap<String,String> all, String myKey){
        context = con;
        friendsUsers = fri;
        reqUsers = req;
        myReqUsers = myReq;
        allUsers = all;
        storageRef = FirebaseStorage.getInstance().getReference();
        scale = context.getResources().getDisplayMetrics().density;
        this.myKey = myKey;
        ref = FirebaseDatabase.getInstance().getReference();

        randomUsers = new TextView(context);
        randomUsers.setTextSize(TypedValue.COMPLEX_UNIT_SP, 30);
        randomUsers.setTypeface(ResourcesCompat.getFont(context, R.font.balsamiq_sans_regular));
        randomUsers.setText("30 случайных игроков");
        randomUsers.setGravity(Gravity.CENTER_HORIZONTAL);
        randomUsers.setPadding(0,0,0,(int)(4*scale));

    }
    private void startFragmentView(String FM) {
        if(linearLayout!=null) {
            linearLayout.removeAllViews();
            if(FM == null || FM.length()==0) {
                linearLayout.addView(randomUsers);
                int i = 0;
                ArrayList<String> random = new ArrayList<>(allUsers.keySet());
                Collections.shuffle(random);
                for (String userKay : random) {
                    if(i>29) return;
                    else i++;

                    if (friendsUsers.contains(userKay)) {
                        linearLayout.addView(createUserFriendCard(userKay));
                    } else if (reqUsers.contains(userKay)) {
                        linearLayout.addView(createReqUserCard(userKay));

                    } else if (myReqUsers.contains(userKay)) {
                        linearLayout.addView(createMyReqUserCard(userKay));

                    } else if (userKay!=myKey){
                        linearLayout.addView(createAnyUserCard(userKay));
                    }
                    if (i!=0 && i%10==0) {
//                        FrameLayout conteiner = new FrameLayout(context);
//                        FrameLayout.LayoutParams paramsConteiner = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//                        conteiner.setPadding(0,0,0,(int)(8*scale));
////        conteiner.setOrientation(LinearLayout.HORIZONTAL);
//                        conteiner.setLayoutParams(paramsConteiner);
//
//                        FrameLayout conteiner2 = new FrameLayout(context);
//                        FrameLayout.LayoutParams paramsConteiner2 = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//                        conteiner2.setPadding(0,(int)(4*scale),0,(int)(8*scale));
////        conteiner.setOrientation(LinearLayout.HORIZONTAL);
//                        conteiner2.setLayoutParams(paramsConteiner2);
//                        conteiner2.setBackgroundResource(R.drawable.background_with_shadow);
//
//                        AdView adView = new AdView(context);
//                        adView.setAdSize(AdSize.BANNER);
//                        adView.setAdUnitId("ca-app-pub-9488118969901249/5483054210");
////                        adView.setAdUnitId("ca-app-pub-3940256099942544/6300978111");//Demo
//                        AdRequest adRequest = new AdRequest.Builder().build();
//                        adView.loadAd(adRequest);
//                        conteiner2.addView(adView);
//                        conteiner.addView(conteiner2);
//                        linearLayout.addView(conteiner);
                    }
                }
            }
            else {
                int i = 0;
                for(String userKay: allUsers.keySet()) {
//                    Toast.makeText(context, myKey + userKay, Toast.LENGTH_SHORT).show();
                    if(!userKay.equals(myKey)&&allUsers.get(userKay).toLowerCase().indexOf(FM.toLowerCase())>-1) {

                        if(i>29) return;
                        else i++;
                        if (friendsUsers.contains(userKay)) {
                            linearLayout.addView(createUserFriendCard(userKay));
                        } else if (reqUsers.contains(userKay)) {
                            linearLayout.addView(createReqUserCard(userKay));

                        } else if (myReqUsers.contains(userKay)) {
                            linearLayout.addView(createMyReqUserCard(userKay));

                        } else {
                            linearLayout.addView(createAnyUserCard(userKay));
                        }
                    }
                }

            }
        }
    }


//    public void findName(String FN){
//        startFragmentView(FN);
//    }

    private View createReqUserCard(String key){
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

        TextView waitRaport = new TextView(context);
        RelativeLayout.LayoutParams paramsWaitRaport =  new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        paramsWaitRaport.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        paramsWaitRaport.addRule(RelativeLayout.BELOW, name.getId());
        paramsWaitRaport.rightMargin = (int) (10*scale);
        paramsWaitRaport.bottomMargin = (int)(6*scale);
        waitRaport.setText("Ожидает ответа");
        relativeLayout.addView(waitRaport,paramsWaitRaport);
        conteiner.addView(relativeLayout);
//
        storageRef.child(key).getBytes(512*512).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                try {
                    Bitmap src = BitmapFactory.decodeByteArray(bytes,0,bytes.length);
                    Drawable drawable= new BitmapDrawable(context.getResources(),src);
                    avatar.setBackground(drawable);

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


        TextView sendReq = new TextView(context);
        RelativeLayout.LayoutParams paramsSendReq =  new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        paramsSendReq.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        paramsSendReq.addRule(RelativeLayout.BELOW, name.getId());
        paramsSendReq.rightMargin = (int) (10*scale);
        paramsSendReq.bottomMargin = (int)(6*scale);
        sendReq.setText("Запрос отправлен");
        relativeLayout.addView(sendReq,paramsSendReq);

        conteiner.addView(relativeLayout);
//
        storageRef.child(key).getBytes(512*512).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                try {
                    Bitmap src = BitmapFactory.decodeByteArray(bytes,0,bytes.length);
                    Drawable drawable= new BitmapDrawable(context.getResources(),src);
                    avatar.setBackground(drawable);

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
    private View createAnyUserCard(String key){
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


        TextView sendReq = new TextView(context);
        RelativeLayout.LayoutParams paramsSendReq =  new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        paramsSendReq.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        paramsSendReq.addRule(RelativeLayout.BELOW, name.getId());
        paramsSendReq.rightMargin = (int) (10*scale);
        paramsSendReq.bottomMargin = (int)(6*scale);
        sendReq.setText("Запрос отправлен");
        sendReq.setVisibility(View.GONE);
        relativeLayout.addView(sendReq,paramsSendReq);

        Button btnRequestInFriend = new Button(context);
        RelativeLayout.LayoutParams paramsbtnRequestInFriend =  new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        paramsbtnRequestInFriend.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        paramsbtnRequestInFriend.addRule(RelativeLayout.BELOW, name.getId());
        paramsbtnRequestInFriend.rightMargin = (int) (4*scale);
        paramsbtnRequestInFriend.bottomMargin = (int)(4*scale);
        btnRequestInFriend.setText("В друзья");
        btnRequestInFriend.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        btnRequestInFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnRequestInFriend.setVisibility(View.GONE);
                sendReq.setVisibility(View.VISIBLE);
                ref.child("Social").child(key).child("RequestsIn").child(myKey).setValue(true);
                ref.child("Social").child(myKey).child("RequestsOut").child(key).setValue(true);
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
    private View createUserFriendCard(String key){
        FrameLayout conteiner = new FrameLayout(context);
        FrameLayout.LayoutParams paramsConteiner = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        conteiner.setPadding(0,0,0,(int)(8*scale));
//        conteiner.setOrientation(LinearLayout.HORIZONTAL);
        conteiner.setLayoutParams(paramsConteiner);
//
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

        TextView addedFriend = new TextView(context);
        RelativeLayout.LayoutParams paramsAddedFriend =  new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        paramsAddedFriend.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        paramsAddedFriend.addRule(RelativeLayout.BELOW, name.getId());
        paramsAddedFriend.rightMargin = (int) (10*scale);
        paramsAddedFriend.bottomMargin = (int)(6*scale);
        addedFriend.setText("В друзьях");
        relativeLayout.addView(addedFriend,paramsAddedFriend);

        conteiner.addView(relativeLayout);
        storageRef.child(key).getBytes(512*512).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                try {
                    Bitmap src = BitmapFactory.decodeByteArray(bytes,0,bytes.length);
                    Drawable drawable= new BitmapDrawable(context.getResources(),src);
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
        return conteiner;
    }

    public  void addAllUsers(ArrayMap<String,String> users) {
        allUsers = users;
    }
    public void addFriends(ArrayList<String> fri) {
        friendsUsers = fri;
    }
    public void addRequests(ArrayList<String> req) {
        reqUsers = req;

    }
    public void addMyRequests(ArrayList<String> myReq) {
        myReqUsers = myReq;
    }

}
