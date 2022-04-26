package ru.tmkstd.cardgamedurakonline;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

import ru.tmkstd.cardgamedurakonline.ChatsFragments.ChatsChatsFragment;
import ru.tmkstd.cardgamedurakonline.ChatsFragments.ChatsFriendsFragment;


public class ChatsActivity extends AppCompatActivity {

//    LinearLayout friendsContainer;
    ImageView back;
    ImageView friendsMark, chatsMark;
    ImageView friendsMarkMask, chatsMarkMask;
    DatabaseReference ref;
    StorageReference storageRef;
    SharedPreferences save;
    String myKeyId;
    TextViewGradient [] markTexts;
    ViewPager viewPager;
    ChatsActivity.ViewPagerAdapter viewPagerAdapter;
    float[] markTextPositionX;
    int seeStateFragment;
    float normalYMark;
    int divinePosY;

    ChatsFriendsFragment chatsFriendsFragment;
    ChatsChatsFragment chatsChatsFragment;

    float scale;

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
        }
        setContentView(R.layout.activity_chats);
        divinePosY = 15;
        normalYMark = 0;
        back = findViewById(R.id.chats_back);
        back.setOnClickListener(v -> onBackPressed());
        back.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction()==MotionEvent.ACTION_DOWN){
                    back.setX(-back.getHeight()*0.06f);
                    back.setY(-back.getHeight()*0.06f);
                    back.setScaleY(0.9f);
                    back.setScaleX(0.9f);
                }
                if(event.getAction()==MotionEvent.ACTION_UP) {
                    back.setX(0);
                    back.setY(0);
                    back.setScaleY(1);
                    back.setScaleX(1);

                }
                return false;
            }
        });
        scale = this.getResources().getDisplayMetrics().density;
//        friendsContainer = findViewById(R.id.chat_friends_container);
        save = getSharedPreferences("Save", MODE_PRIVATE);
        myKeyId = save.getString("myKeyId",null);

        switch (save.getInt("colorField",0)){
            case 1: findViewById(R.id.chatsMainLayout).setBackgroundResource(R.drawable.fon_two); break;
            case 2: findViewById(R.id.chatsMainLayout).setBackgroundResource(R.drawable.fon_three); break;
            case 3: findViewById(R.id.chatsMainLayout).setBackgroundResource(R.drawable.fon_four); break;
            default:findViewById(R.id.chatsMainLayout).setBackgroundResource(R.drawable.fon_one); break;
        }

        storageRef = FirebaseStorage.getInstance().getReference();
        ref = FirebaseDatabase.getInstance().getReference();

        viewPager = findViewById(R.id.viewPager_chats);
        markTexts = new TextViewGradient[2];
        markTexts[0] = findViewById(R.id.chats_text_friends);
        markTexts[1] = findViewById(R.id.chats_text_chats);
        friendsMarkMask = findViewById(R.id.chats_mark_mask_friends);
        chatsMarkMask = findViewById(R.id.chats_mark_mask_chats);

        friendsMark = findViewById(R.id.mark_chats_friends);
        friendsMark.setOnClickListener(v -> viewPager.setCurrentItem(0));

        chatsMark = findViewById(R.id.mark_chats_chats);
        chatsMark.setOnClickListener(v -> viewPager.setCurrentItem(1));

        markTextPositionX = new float[2];
        seeStateFragment = 0;

        viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());

        chatsFriendsFragment = new ChatsFriendsFragment(ref,storageRef, myKeyId, scale);
        chatsChatsFragment = new ChatsChatsFragment();
        viewPagerAdapter.addFragment(chatsFriendsFragment);
        viewPagerAdapter.addFragment(chatsChatsFragment);

        viewPager.setOffscreenPageLimit(2);
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
                switch (position) {
                    case 1:
                        friendsMarkMask.setVisibility(View.INVISIBLE);
                        chatsMarkMask.setVisibility(View.VISIBLE);
                        friendsMark.setY(normalYMark + friendsMark.getHeight() / divinePosY);
                        chatsMark.setY(normalYMark);
                        break;
                    default:
                        friendsMarkMask.setVisibility(View.VISIBLE);
                        chatsMarkMask.setVisibility(View.INVISIBLE);
                        friendsMark.setY(normalYMark);
                        chatsMark.setY(normalYMark + friendsMark.getHeight() / divinePosY);
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
    @SuppressLint("NewApi")
    @Override
    public void onWindowFocusChanged(boolean hasFocus)
    {
        super.onWindowFocusChanged(hasFocus);
        if (markTextPositionX[0]==0) {
            markTextPositionX[0] = markTexts[0].getX();
            markTextPositionX[1] = markTexts[1].getX();
        }
        if (normalYMark==0) {
            normalYMark = friendsMark.getY();
//        viewPager.setCurrentItem(0);
            friendsMark.setY(normalYMark + friendsMark.getHeight() / divinePosY);
            chatsMark.setY(normalYMark);
        }
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
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(ChatsActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        overridePendingTransition(0,0);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
        finish();
    }

}
