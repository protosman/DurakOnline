package ru.tmkstd.cardgamedurakonline;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.ArrayMap;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import ru.tmkstd.cardgamedurakonline.Fragments.FindFragment;
import ru.tmkstd.cardgamedurakonline.Fragments.FriendsFragment;
import ru.tmkstd.cardgamedurakonline.Fragments.MyRequestsFragment;
import ru.tmkstd.cardgamedurakonline.Fragments.RequestsFragment;

public class FriendsListActivity extends AppCompatActivity {
    ImageView back;
    ImageView friendsMark, requestsMark, findMark, myRequestsMark;
    ImageView friendsMarkMask, requestsMarkMask, findMarkMask, myRequestsMarkMask;
    TextViewGradient [] markTexts;
    TextView countNewFriends;
    float[] markTextPositionX;
    AdView mAdView;
    RelativeLayout linMain;
    ViewPager viewPager;
    ViewPagerAdapter viewPagerAdapter;
    SharedPreferences save;
    long coin;
    int seeStateFragment;
    int maxX, colorField;
    FriendsFragment friendsFragment;
    FindFragment findFragment;
    RequestsFragment requestsFragment;
    MyRequestsFragment myRequestsFragment;
    ArrayMap<String, String> allUsers;
    ArrayList<String> allFriends,allRequests, allMyRequests;
    ArrayList<ValueEventListener> listeners;
    boolean firstInit;
    FirebaseUser user;
    String myUserId;
    DatabaseReference ref;
    ValueEventListener allUserListener, friendsListener, requestsListener, myRequestsListener;
    float normalYMark;
    int divinePosY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow()
                    .getDecorView()
                    .setSystemUiVisibility(
                            View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    );
            final View decorView = getWindow().getDecorView();
            decorView.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener(){

                @Override
                public void onSystemUiVisibilityChange(int visibility) {
                    if((visibility &View.SYSTEM_UI_FLAG_FULLSCREEN) == 0){
                        decorView
                                .setSystemUiVisibility(
                                        View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                                                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                                                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                                                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                                                | View.SYSTEM_UI_FLAG_FULLSCREEN
                                                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                                );
                    }
                }
            });
        }
        setContentView(R.layout.activity_friend_list);
        mAdView = findViewById(R.id.adViewFriens);
        mAdView.loadAd(new AdRequest.Builder().build());

        countNewFriends = findViewById(R.id.count_friend_new_friends);

        linMain = findViewById(R.id.layoutFriend);
        viewPager = findViewById(R.id.viewPager);
        markTexts = new TextViewGradient[4];
        markTexts[0] = findViewById(R.id.friend_text_friends);
        markTexts[1] = findViewById(R.id.friend_text_finds);
        markTexts[2] = findViewById(R.id.friend_text_request_in);
        markTexts[3] = findViewById(R.id.friend_text_request_out);
        markTexts[0].setAlpha(1);
        markTexts[1].setAlpha(0);
        markTexts[2].setAlpha(0);
        markTexts[3].setAlpha(0);
        markTextPositionX = new float[4];
        markTextPositionX[0]=0f;
        allUsers = new ArrayMap<String, String>();

        save = getSharedPreferences("Save", MODE_PRIVATE);

        coin = save.getLong("coin",0);
        colorField = save.getInt("colorField",0);
        maxX = save.getInt("MaxX",720);
        switch (colorField){
            case 1: linMain.setBackgroundResource(R.drawable.fon_two); break;
            case 2: linMain.setBackgroundResource(R.drawable.fon_three); break;
            case 3: linMain.setBackgroundResource(R.drawable.fon_four); break;
            default:linMain.setBackgroundResource(R.drawable.fon_one); break;
        }


        user = FirebaseAuth.getInstance().getCurrentUser();
        myUserId = user.getUid();
        ref = FirebaseDatabase.getInstance().getReference();
        startUI();
        seeStateFragment=0;
        addMap();
    }
    void creatFragments() {
        viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        friendsFragment = new FriendsFragment(FriendsListActivity.this,allFriends,allUsers,myUserId);
        Log.d("erraaa3", allFriends.size()+" ");
        requestsFragment = new RequestsFragment(FriendsListActivity.this,allRequests,allUsers,myUserId);
        myRequestsFragment = new MyRequestsFragment(FriendsListActivity.this,allMyRequests,allUsers,myUserId);
        findFragment = new FindFragment(FriendsListActivity.this,allFriends,allRequests,allMyRequests,allUsers,myUserId);
        viewPagerAdapter.addFragment(friendsFragment);
        viewPagerAdapter.addFragment(findFragment);
        viewPagerAdapter.addFragment(requestsFragment);
        viewPagerAdapter.addFragment(myRequestsFragment);

        viewPager.setOffscreenPageLimit(3);
        viewPager.setAdapter(viewPagerAdapter);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                Log.d("onPageSelected"," " + position);
                if(seeStateFragment<position){
                    TextView textView = markTexts[seeStateFragment];
                    markTexts[seeStateFragment].animate()
                            .alpha(0f)
//                            .translationXBy(-200)
                            .setDuration(200)
                            .setListener(new AnimatorListenerAdapter() {
                                @Override
                                public void onAnimationEnd(Animator animation) {
                                    super.onAnimationEnd(animation);
                                    textView.setX(textView.getX()+200);
                                    textView.setVisibility(View.GONE);
                                }
                            });
                    markTexts[position].setAlpha(0f);
                    markTexts[position].setX(markTextPositionX[position]+200);
                    markTexts[position].setVisibility(View.VISIBLE);

                    markTexts[position].animate()
                            .translationXBy(-200)
                            .alpha(1f)
                            .setDuration(200)
                            .setListener(new AnimatorListenerAdapter() {
                                @Override
                                public void onAnimationEnd(Animator animation) {
                                    super.onAnimationEnd(animation);
                                    markTexts[position].setVisibility(View.VISIBLE);
                                    markTexts[position].setX(markTextPositionX[position]);
                                }
                            });
                }
                if(seeStateFragment>position){
                    TextView textView = markTexts[seeStateFragment];
                    markTexts[seeStateFragment].animate().cancel();
                    markTexts[seeStateFragment].animate()
                            .alpha(0f)
                            .translationXBy(200)
                            .setDuration(200)
                            .setListener(new AnimatorListenerAdapter() {
                                @Override
                                public void onAnimationEnd(Animator animation) {
                                    super.onAnimationEnd(animation);

                                    textView.setVisibility(View.GONE);
                                    textView.setX(textView.getX()-200);
                                }
                            });
                    markTexts[position].setAlpha(0f);
//                    markTexts[position].setX(markTextPositionX[position]-200);
                    markTexts[position].setVisibility(View.VISIBLE);
                    markTexts[position].setX(markTextPositionX[position]);
                    markTexts[position].animate()
//                            .translationXBy(200)
                            .alpha(1f)
                            .setDuration(200)
                            .setListener(new AnimatorListenerAdapter() {
                                @Override
                                public void onAnimationEnd(Animator animation) {
                                    super.onAnimationEnd(animation);
                                    markTexts[position].setVisibility(View.VISIBLE);
                                    markTexts[position].setX(markTextPositionX[position]);
                                }
                            });
                }
                seeStateFragment = position;
                InputMethodManager imm = (InputMethodManager) FriendsListActivity.this.getSystemService(Activity.INPUT_METHOD_SERVICE);
                View view = FriendsListActivity.this.getCurrentFocus();
                if (view == null) {
                    view = new View(FriendsListActivity.this);
                }
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                switch (position) {
                    case 1:
                        friendsMarkMask.setVisibility(View.INVISIBLE);
                        findMarkMask.setVisibility(View.VISIBLE);
                        requestsMarkMask.setVisibility(View.INVISIBLE);
                        myRequestsMarkMask.setVisibility(View.INVISIBLE);

                        friendsMark.setY(normalYMark);
                        findMark.setY(normalYMark + friendsMark.getHeight()/divinePosY);
                        requestsMark.setY(normalYMark);
                        myRequestsMark.setY(normalYMark);
                        break;
                    case 2:
                        friendsMarkMask.setVisibility(View.INVISIBLE);
                        findMarkMask.setVisibility(View.INVISIBLE);
                        requestsMarkMask.setVisibility(View.VISIBLE);
                        myRequestsMarkMask.setVisibility(View.INVISIBLE);
                        friendsMark.setY(normalYMark);
                        findMark.setY(normalYMark);
                        requestsMark.setY(normalYMark + friendsMark.getHeight()/divinePosY);
                        myRequestsMark.setY(normalYMark);
                        break;
                    case 3:
                        friendsMarkMask.setVisibility(View.INVISIBLE);
                        findMarkMask.setVisibility(View.INVISIBLE);
                        requestsMarkMask.setVisibility(View.INVISIBLE);
                        myRequestsMarkMask.setVisibility(View.VISIBLE);
                        friendsMark.setY(normalYMark);
                        findMark.setY(normalYMark);
                        requestsMark.setY(normalYMark);
                        myRequestsMark.setY(normalYMark + friendsMark.getHeight()/divinePosY);
                        break;
                    default:
                        friendsMarkMask.setVisibility(View.VISIBLE);
                        findMarkMask.setVisibility(View.INVISIBLE);
                        requestsMarkMask.setVisibility(View.INVISIBLE);
                        myRequestsMarkMask.setVisibility(View.INVISIBLE);
                        friendsMark.setY(normalYMark + friendsMark.getHeight()/divinePosY);
                        findMark.setY(normalYMark);
                        requestsMark.setY(normalYMark);
                        myRequestsMark.setY(normalYMark);
                        break;

                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {

        private ArrayList<Fragment> fragments;
        public ViewPagerAdapter(FragmentManager fm) {
            super(fm);
            this.fragments = new ArrayList<>();
        }

        @Override
        public Fragment getItem(int position) {

            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }


        public void addFragment(Fragment fragment) {
            fragments.add(fragment);
        }
    }
    private void startUI(){
        divinePosY = 15;
        normalYMark = 0;

        back = findViewById(R.id.friends_back);
        back.setOnClickListener(v -> onBackPressed());
        back.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN) {
                    back.setX(-back.getWidth()*0.06f);
                    back.setY(-back.getHeight()*0.06f);
                    back.setScaleX(0.9f);
                    back.setScaleY(0.9f);
                }
                if(event.getAction() == MotionEvent.ACTION_UP) {
                    back.setX(0);
                    back.setY(0);
                    back.setScaleX(1);
                    back.setScaleY(1);
                }
                return false;
            }
        });



        friendsMarkMask = findViewById(R.id.friends_mark_mask_friends);
        findMarkMask = findViewById(R.id.friends_mark_mask_sourchs);
        requestsMarkMask = findViewById(R.id.friends_mark_mask_requests_in);
        myRequestsMarkMask = findViewById(R.id.friends_mark_mask_requests_out);

        friendsMark = findViewById(R.id.mark_friends);
        friendsMark.setOnClickListener(v -> viewPager.setCurrentItem(0));

        findMark = findViewById(R.id.mark_sourchs);
        findMark.setOnClickListener(v -> viewPager.setCurrentItem(1));

        requestsMark = findViewById(R.id.mark_requests_in);
        requestsMark.setOnClickListener(v -> viewPager.setCurrentItem(2));

        myRequestsMark = findViewById(R.id.mark_requests_out);
        myRequestsMark.setOnClickListener(v -> viewPager.setCurrentItem(3));
    }

    private  void addMap() {

        InitListener();
        ref.child("Names").addValueEventListener(allUserListener);
    }
    private void InitListener() {
        firstInit = true;
        allFriends = new ArrayList<>();
        allRequests = new ArrayList<>();
        allMyRequests = new ArrayList<>();
        listeners = new ArrayList<>();
        allUserListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d("Rettttt","4");
                allUsers.clear();
                for(DataSnapshot snapshot: dataSnapshot.getChildren()) {
                    allUsers.put(snapshot.getKey(),snapshot.getValue(String.class));
                }
                if(firstInit) {
                    ref.child("Social").child(myUserId).child("Friends").addValueEventListener(friendsListener);
                }
                if(findFragment!=null) {
                    findFragment.addAllUsers(allUsers);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        listeners.add(allUserListener);
        friendsListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d("Rettttt","3");
                allFriends.clear();
                for(DataSnapshot snapshot: dataSnapshot.getChildren()) {
                    allFriends.add(snapshot.getKey());
                }
                if(friendsFragment!=null) {
//********************************************************тут косяк можно не делать аfriends= allFriends, т.к. я уже передал ссылку на обьект в конструкторе и теперь это один и тотже объект
                    friendsFragment.addUsers(allFriends, seeStateFragment);
                }
                if(findFragment!=null) {
                    findFragment.addFriends(allFriends);
                }
                if(firstInit) {
                    ref.child("Social").child(myUserId).child("RequestsIn").addValueEventListener(requestsListener);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        listeners.add(friendsListener);
        requestsListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                allRequests.clear();
                for(DataSnapshot snapshot: dataSnapshot.getChildren()) {
                    allRequests.add(snapshot.getKey());
                }
                if(dataSnapshot.getChildrenCount()>0) {
                    countNewFriends.setText("+"+dataSnapshot.getChildrenCount());
                    countNewFriends.setVisibility(View.VISIBLE);
                }
                else {
                    countNewFriends.setVisibility(View.GONE);
                }
                if(firstInit) {
                    ref.child("Social").child(myUserId).child("RequestsOut").addValueEventListener(myRequestsListener);
                }
                if(requestsFragment!= null) {
                    requestsFragment.addUsers(allRequests,seeStateFragment);
                }
                if(findFragment!=null) {
                    findFragment.addRequests(allRequests);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        listeners.add(requestsListener);
        myRequestsListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d("Rettttt","1");
                allMyRequests.clear();
                for(DataSnapshot snapshot: dataSnapshot.getChildren()) {
                    allMyRequests.add(snapshot.getKey());
                }
                if(findFragment!=null) {
                    findFragment.addMyRequests(allMyRequests);
                }
                if(myRequestsFragment!= null ) {
                    myRequestsFragment.addUsers(allMyRequests, seeStateFragment);
                }
                if(firstInit) {
                    firstInit = false;
                    creatFragments();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        listeners.add(myRequestsListener);

    }

    @Override
    public void onBackPressed() {
        deleteListener();
        Intent intent = new Intent(FriendsListActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        overridePendingTransition(0,0);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
    }
    void deleteListener() {
        if(friendsFragment!=null) {
            friendsFragment.deleteListeners();
        }

        if(listeners.size()>0)
        ref.child("Names").removeEventListener(listeners.get(0));
        if(listeners.size()>1)
        ref.child("Social").child(myUserId).child("Friends").removeEventListener(listeners.get(1));
        if(listeners.size()>2)
        ref.child("Social").child(myUserId).child("RequestsIn").removeEventListener(listeners.get(2));
        if(listeners.size()>3)
        ref.child("Social").child(myUserId).child("RequestsOut").removeEventListener(listeners.get(3));

    }

    @SuppressLint("NewApi")
    @Override
    public void onWindowFocusChanged(boolean hasFocus)
    {
        super.onWindowFocusChanged(hasFocus);
        if (markTextPositionX[0]==0) {
            markTextPositionX[0] = markTexts[0].getX();
            markTextPositionX[1] = markTexts[1].getX();
            markTextPositionX[2] = markTexts[2].getX();
            markTextPositionX[3] = markTexts[3].getX();
        }
        if(normalYMark == 0) {

            normalYMark = friendsMark.getY();
            friendsMark.setY(normalYMark + friendsMark.getHeight()/divinePosY);
            findMark.setY(normalYMark);
            requestsMark.setY(normalYMark);
            myRequestsMark.setY(normalYMark);
        }
//        Log.d("textTestFriend"," " + markTexts[0].getY() + " " + (markTexts[0].getY() + markTexts[0].getTextSize()/2) + " " + (markTexts[0].getY() +markTexts[0].getTextSize()/6+markTexts[0].getTextSize()));
//        markTexts[0].getPaint().setShader(new LinearGradient(0,markTexts[0].getTextSize()/15*10,0,markTexts[0].getTextSize() +markTexts[0].getTextSize()/3, Color.rgb(236,240,45),Color.rgb(204,97,24), Shader.TileMode.CLAMP));
////        markTexts[0].setShadowLayer(2,2,2,Color.rgb(100,115,50));
//        markTexts[1].getPaint().setShader(new LinearGradient(0,markTexts[0].getTextSize()/15*10,0,markTexts[0].getTextSize() +markTexts[0].getTextSize()/3, Color.rgb(236,240,45),Color.rgb(204,97,24), Shader.TileMode.CLAMP));
//        markTexts[2].getPaint().setShader(new LinearGradient(0,markTexts[0].getTextSize()/15*10,0,markTexts[0].getTextSize() +markTexts[0].getTextSize()/3, Color.rgb(236,240,45),Color.rgb(204,97,24), Shader.TileMode.CLAMP));
//        markTexts[3].getPaint().setShader(new LinearGradient(0,markTexts[0].getTextSize()/15*10,0,markTexts[0].getTextSize() +markTexts[0].getTextSize()/3, Color.rgb(236,240,45),Color.rgb(204,97,24), Shader.TileMode.CLAMP));
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && hasFocus)
        {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }
}
