package ru.tmkstd.cardgamedurakonline;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.ConsumeParams;
import com.android.billingclient.api.ConsumeResponseListener;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.SkuDetails;
import com.android.billingclient.api.SkuDetailsParams;
import com.android.billingclient.api.SkuDetailsResponseListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
//import com.google.android.gms.ads.reward.RewardItem;
//import com.google.android.gms.ads.reward.RewardedVideoAd;
//import com.google.android.gms.ads.reward.RewardedVideoAdListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity
//        implements RewardedVideoAdListener
{
//    private RewardedVideoAd mRewardedVideoAd;

    ImageView avatar;
    ImageView startGame, settings,rules, shareApp, friendsList
            , homeBtn, acceptName, shop, chats
            ;

    ImageView plusImage, underCoin;
    TextView
            counterCoin,
//            counterCoinShadow,
            countNewFriend,maxLen;
    EditText name;
    int maxX,maxY;
//    int btnWidth, btnHeight;
//    int newBtnWidth,newBtnHeight, marginMy;
    boolean firstGame, modGame;
    int colorField, numCards;
    long coin = -1;
    long countAllGames = 0;
    boolean versionStart = false;
    RelativeLayout layout;
    FirebaseStorage storage;
    StorageReference storageRef;
    FirebaseDatabase database;
    DatabaseReference myRef;
    FirebaseAuth mAuth;
    FirebaseUser user;
    String myId;
    ValueEventListener  NickNameListener,
//            TimerReclamaListener,
            numberOfReqFriend;
    Bitmap bitmap;

    SharedPreferences save;
    SharedPreferences.Editor editor;
    AdView mAdView;


    private BillingClient mBillingClient;

    int numDialog = -1;

    String[] dialogs = {
            "Теперь у Вас на 50 монет больше.",
            "Поздавляю, Вы получили 150 монет.",
            "Поздавляю, на Ваш счёт зачисленно 250 монет.",
            "Джекпот! +777 монет на Ваш счёт.",
            "Вам преобрели статус UNREAL\n+999999999 монет.",
            "Наша жизнь стала слаще :)",
            "Теперь нам будет проще вставать по утрам :)"
    };
//    String[] dialogs = {
//            "Теперь у Вас на 50 монет больше.",
//            "Поздавляю у Вас на 120 монет больше.",
//            "Поздавляю, теперь у Вас на 300 монет больше.",
//            "Вам дан статус UNREAL",
//            "Спасибо Вам за донат!"
//    };



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
        setContentView(R.layout.activity_main);

        mAdView = findViewById(R.id.adView);
        mAdView.loadAd(new AdRequest.Builder().build());
//        Toast.makeText(this,"33333333333333333333", Toast.LENGTH_SHORT).show();
        Log.d("LLL1","MainActivity onCreate()");
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference();
        user = mAuth.getInstance().getCurrentUser();
        myId = user.getUid();
//        myId = "-00000000000"; // это котмэн
//        myId = "-00000000001";
//        myId = "-00000000002";
//        myId = "-00000000003";
//        myId = "-00000000004";
//        myId = "-00000000005";
//        myId = "-00000000006";
//        myId = "-00000000007";
//        myId = "-00000000008";
//        myId = "-00000000009";
//        myId = "-00000000010";
//        myId = "-00000000011";
//        myId = "-00000000012";

//        myId = "-00000000013";
//        myId = "-00000000014";
//        myId = "-00000000015";
//        myId = "-00000000016";
//        myId = "-00000000017";
//        myId = "-00000000018";
//        myId = "-00000000019";
//        myId = "-00000000020";
//        myId = "-00000000021";
//        myId = "-00000000022";
//        myId = "-00000000023";
//        myId = "-00000000024";
//        myRef.child("Coins").child(myId).setValue(1000000);
        save = getSharedPreferences("Save", MODE_PRIVATE);

        firstGame = save.getBoolean("firstGame",true);
        modGame = save.getBoolean("modGame",true);
        colorField = save.getInt("colorField",0);
        numCards = save.getInt("numCards",36);
        maxX = save.getInt("MaxX",720);
        maxY = save.getInt("MaxY",720);
        Log.d("MainMaxXY",maxX + " " + maxY);
        coin = save.getLong("coin",0);
        versionStart = save.getBoolean("version",false);
        if(getIntent().getIntExtra("coin",1)==0) {// это при выходе из GameEngineActivity coin<10
            Toast.makeText(MainActivity.this, "Не достаточно монет.", Toast.LENGTH_SHORT).show();
        }

        homeBtn= findViewById(R.id.btn_menu_home);
        underCoin= findViewById(R.id.btn_menu_count_coin);
        rules= findViewById(R.id.btn_menu_rules);
        shareApp= findViewById(R.id.btn_menu_share);
        plusImage= findViewById(R.id.btn_menu_add_coins);
        shop = findViewById(R.id.btn_menu_shop);
//        chats = findViewById(R.id.btn_menu_chats);
        friendsList= findViewById(R.id.btn_menu_friends);
        settings= findViewById(R.id.btn_menu_settings);
        startGame = findViewById(R.id.btn_menu_start);
        counterCoin = findViewById(R.id.count_coin);
//        counterCoinShadow =findViewById(R.id.count_coin_shadow);
        name = findViewById(R.id.nameUser);
        maxLen = findViewById(R.id.max_len_menu_name);
        avatar = findViewById(R.id.avatar);
        acceptName = findViewById(R.id.btn_menu_accept);
        countNewFriend = findViewById(R.id.count_menu_new_friends);
//        counterCoinShadow.getPaint().setMaskFilter(new BlurMaskFilter(1, BlurMaskFilter.Blur.NORMAL));
//        counterCoin.setAlpha(1);
//        counterCoin.getPaint().setShader(new LinearGradient(0,counterCoin.getTextSize()/2,0,counterCoin.getTextSize()/6+counterCoin.getTextSize(), Color.rgb(236,240,45),Color.rgb(204,97,24), Shader.TileMode.CLAMP));
//        Log.d("countCoin",counterCoin.getY()+" " + counterCoin.getHeight()+ " " + counterCoin.getTextSize());

        layout = findViewById(R.id.mainLayout);
        switch (colorField){
            case 0: layout.setBackgroundResource(R.drawable.fon_one); break;
            case 1: layout.setBackgroundResource(R.drawable.fon_two); break;
            case 2: layout.setBackgroundResource(R.drawable.fon_three); break;
            case 3: layout.setBackgroundResource(R.drawable.fon_four); break;
            default:layout.setBackgroundResource(R.drawable.fon_one); break;
        }

        setStatusOnline();
        getAndSaveToken();
        Date currentDate = new Date();
        DateFormat timeFormat = new SimpleDateFormat("yy-MM-dd HH:mm:ss", Locale.getDefault());
        myRef.child("Social").child(myId).child("LastStart").setValue(timeFormat.format(currentDate));
        InitListener();
        firstStart();

//        MobileAds.initialize(this, "ca-app-pub-9488118969901249~8597050177");
//        mRewardedVideoAd = MobileAds.getRewardedVideoAdInstance(this);
//        mRewardedVideoAd.setRewardedVideoAdListener(this);


//        loadRewardedVideoAd();
    }

    private void getAndSaveToken() {
        FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
            @Override
            public void onComplete(@NonNull Task<InstanceIdResult> task) {

                if(task.isSuccessful()) {
                    String token = task.getResult().getToken();
                    saveToken(token);
                }
            }
        });
    }


    private void saveToken(String token) {
        myRef.child("Social").child(myId).child("Token").setValue(token);
    }

//    private void loadRewardedVideoAd() {
//
////        mRewardedVideoAd.loadAd("ca-app-pub-9488118969901249/4778537745",
////                new AdRequest.Builder().build());
////       Тестовая
//       mRewardedVideoAd.loadAd("ca-app-pub-3940256099942544/5224354917",
//                new AdRequest.Builder().build());
//    }
    void InitListener() {
        numberOfReqFriend = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot != null){
                    if (dataSnapshot.getChildrenCount() > 0) {
                        countNewFriend.setText("+" + (int) dataSnapshot.getChildrenCount());
                        countNewFriend.setVisibility(View.VISIBLE);
                    }
                    else {
                        countNewFriend.setVisibility(View.INVISIBLE);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };

        myRef.child("Social").child(myId).child("RequestsIn").addValueEventListener(numberOfReqFriend);

//        TimerReclamaListener = new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
////                plusImage.setBackgroundResource(R.drawable.plus);
//                if(dataSnapshot.getValue(Long.class)==null || dataSnapshot.getValue(Long.class)+600000<System.currentTimeMillis()){
//                    AlertDialog.Builder quitDialog = new AlertDialog.Builder(MainActivity.this);
//                    quitDialog.setTitle("Посмотреть обьявление, чтобы получить 10 монет?");
//                    quitDialog.setPositiveButton("Таки ДА!", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            if (mRewardedVideoAd.isLoaded()) {
//                                mRewardedVideoAd.show();
//                            } else {
//                                Toast.makeText(MainActivity.this, "Обьявление не загружено.", Toast.LENGTH_SHORT).show();
//                            }
//                        }
//                    });
//                    quitDialog.setNegativeButton("Таки НИЗАШО!", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//
//                        }
//                    });
//                    quitDialog.show();
//                }
//                else{
//                    AlertDialog.Builder quitDialog = new AlertDialog.Builder(MainActivity.this);
//                    quitDialog.setTitle("Доступно через "+(600 -(System.currentTimeMillis()/1000-dataSnapshot.getValue(Long.class)/1000))+" сек.");
//                    quitDialog.setPositiveButton("Хорошо", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                        }
//                    });
//                    quitDialog.show();
//                }
//            }
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        };
        NickNameListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue(String.class)!= null){
                    name.setText(dataSnapshot.getValue(String.class));
                }
                else {
                    myRef.child("Names").child(myId).setValue("Игрок");
                    name.setText("Игрок");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
    }
    void clickName() {
        name.setCursorVisible(true);
        maxLen.setVisibility(View.VISIBLE);
        acceptName.setVisibility(View.VISIBLE);
    }
    void firstStart() {
        Log.d("MainActivity","firstStart 1");
        homeBtn.setOnClickListener(v -> homeClick());
        homeBtn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction()==MotionEvent.ACTION_DOWN) {
//                    homeBtn.setLayoutParams(new ViewGroup.LayoutParams(homeBtn.get));
                    homeBtn.setX(-homeBtn.getWidth()*0.06f);
                    homeBtn.setY(-homeBtn.getHeight()*0.06f);
                    homeBtn.setScaleX(0.9f);
                    homeBtn.setScaleY(0.9f);
                }
                if(event.getAction()==MotionEvent.ACTION_UP) {
                    homeBtn.setX(0);
                    homeBtn.setY(0);
                    homeBtn.setScaleX(1f);
                    homeBtn.setScaleY(1f);
                }
                return false;
            }
        });
        shareApp.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction()==MotionEvent.ACTION_DOWN) {
                    shareApp.setRotation(-5);
                    shareApp.setScaleX(0.9f);
                    shareApp.setScaleY(0.9f);
                }
                if(event.getAction()==MotionEvent.ACTION_UP) {
                    shareApp.setRotation(0);
                    shareApp.setScaleX(1f);
                    shareApp.setScaleY(1f);
                }
                return false;
            }
        });
        plusImage.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction()==MotionEvent.ACTION_DOWN) {
                    plusImage.setRotation(-5);
                    plusImage.setScaleX(0.9f);
                    plusImage.setScaleY(0.9f);
                }
                if(event.getAction()==MotionEvent.ACTION_UP) {
                    plusImage.setScaleX(1f);
                    plusImage.setScaleY(1f);
                    plusImage.setRotation(0);
                }
                return false;
            }
        });
        friendsList.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction()==MotionEvent.ACTION_DOWN) {
                    friendsList.setRotation(-5);
                    friendsList.setScaleX(0.9f);
                    friendsList.setScaleY(0.9f);
                }
                if(event.getAction()==MotionEvent.ACTION_UP) {
                    friendsList.setRotation(0);
                    friendsList.setScaleX(1f);
                    friendsList.setScaleY(1f);
                }
                return false;
            }
        });
        settings.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction()==MotionEvent.ACTION_DOWN) {
                    settings.setRotation(-5);
                    settings.setScaleX(0.9f);
                    settings.setScaleY(0.9f);
                }
                if(event.getAction()==MotionEvent.ACTION_UP) {
                    settings.setScaleX(1f);
                    settings.setScaleY(1f);
                    settings.setRotation(0);
                }
                return false;
            }
        });
        startGame.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction()==MotionEvent.ACTION_DOWN) {
                    startGame.setRotation(-5);
                    startGame.setScaleX(0.9f);
                    startGame.setScaleY(0.9f);
                }
                if(event.getAction()==MotionEvent.ACTION_UP) {
                    startGame.setScaleX(1f);
                    startGame.setScaleY(1f);
                    startGame.setRotation(0);
                }
                return false;
            }
        });

        refreshCoins();
//        counterCoinShadow.setText(counterCoin.getText().toString());

        plusImage.setOnClickListener(v->plusImageClick());


        avatar.setOnClickListener(v->avatarClick());
        storageRef.child(myId).getBytes(1024*1024).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                bitmap = BitmapFactory.decodeByteArray(bytes,0,bytes.length);
//                RoundedBitmapDrawable drawable = RoundedBitmapDrawableFactory.create(getResources(),bitmap);
//                drawable.setCircular(true);
//                drawable.setCornerRadius(25);
//                avatar.setBackground(drawable);
////                bitmap = BitmapFactory.decodeByteArray(bytes,0,bytes.length);
//                Drawable drawable= new BitmapDrawable(getResources(),bitmap);
//
//                avatar.setBackground(drawable);
//                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
//                Bitmap mask = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(
//                        getResources(), R.drawable.avatar_mask),
//                        maxX/4, maxX/4,false);
//                bitmap = unionBitmap(bitmap, mask);
//                Drawable drawable= new BitmapDrawable(getResources(),bitmap);
//                bitmap.compress(Bitmap.CompressFormat.PNG,100,outputStream);
//
//
////                    bitmap.compress(Bitmap.CompressFormat.JPEG,100,bitmap);
//                byte[] dataBitmap = outputStream.toByteArray();
//
//                UploadTask uploadTask = storageRef.child(myId).putBytes(dataBitmap);
//                uploadTask.addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//
//                    }
//                }).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
//                    @Override
//                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
//
//                    }
//                });
                avatar.setImageBitmap(bitmap);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                avatar.setImageResource(R.drawable.avatar_two);
            }
        });

        name.clearFocus();
        name.setCursorVisible(false);
        name.setSingleLine(true);
        name.setFilters(new InputFilter[] {new InputFilter.LengthFilter(10)});
        name.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
        name.setText("Загрузка..");
        name.setOnClickListener(v-> clickName());
        name.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if(event.getAction() == KeyEvent.ACTION_DOWN && keyCode==KeyEvent.KEYCODE_ENTER){
                    maxLen.setVisibility(View.INVISIBLE);
                    acceptName.setVisibility(View.INVISIBLE);
                    name.setCursorVisible(false);
                    if(name.getText().toString().length()!=0){
                        myRef.child("Names").child(myId).setValue(name.getText().toString());
                        if(myId.equals("gHbzooLJkhVkQULluIg9hatYNdj2")&& name.getText().toString().equals("DeleteGame")){
                            deleteGamesFieldAll();//вроде как чеез enter? а вообще както через ж работает

                        }
                    }
                    else {
                        myRef.child("Names").child(myId).setValue(" ");
                    }
                    name.clearFocus();

                    //Скрыть клавиатуру
                    InputMethodManager imm = (InputMethodManager) MainActivity.this.getSystemService(Activity.INPUT_METHOD_SERVICE);
                    View view = MainActivity.this.getCurrentFocus();
                    if (view == null) {
                        view = new View(MainActivity.this);
                    }
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    return true;
                }
                return false;
            }
        });
        acceptName.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN) {
                    acceptName.setScaleX(0.9f);
                    acceptName.setScaleY(0.9f);
                    acceptName.setRotation(-4);
                }
                if(event.getAction() == MotionEvent.ACTION_UP) {
                    acceptName.setScaleX(1);
                    acceptName.setScaleY(1);
                    acceptName.setRotation(0);
                }
                return false;
            }
        });
        acceptName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                maxLen.setVisibility(View.INVISIBLE);
                acceptName.setVisibility(View.INVISIBLE);
                name.setCursorVisible(false);
                if(name.getText().toString().length()!=0){
                    myRef.child("Names").child(myId).setValue(name.getText().toString());
                }
                else {
                    myRef.child("Names").child(myId).setValue(" ");
                }
                name.clearFocus();

                //Скрыть клавиатуру
                InputMethodManager imm = (InputMethodManager) MainActivity.this.getSystemService(Activity.INPUT_METHOD_SERVICE);
                View view = MainActivity.this.getCurrentFocus();
                if (view == null) {
                    view = new View(MainActivity.this);
                }
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

            }
        });
        myRef.child("Names").child(myId).addListenerForSingleValueEvent(NickNameListener);

        shareApp.setOnClickListener(v -> shareAppClick());
        friendsList.setOnClickListener(v -> friendsListClick());
        rules.setOnClickListener(v -> rulesClick());
        rules.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction()==MotionEvent.ACTION_DOWN) {
                    rules.setScaleX(0.9f);
                    rules.setScaleY(0.9f);
                    rules.setRotation(-5);
                }
                if(event.getAction()==MotionEvent.ACTION_UP) {

                    rules.setScaleX(1f);
                    rules.setScaleY(1f);
                    rules.setRotation(0);
                }
                return false;
            }
        });
//        chats.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                Intent intent = new Intent(MainActivity.this, ChatsActivity.class);
//                overridePendingTransition(0,0);
//                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
//                startActivity(intent);
//                finish();
//            }
//        });
        shop.setOnClickListener(v -> shopClick());
        shop.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction()==MotionEvent.ACTION_DOWN) {
                    shop.setScaleX(0.9f);
                    shop.setScaleY(0.9f);
                    shop.setRotation(-5);
                }
                if(event.getAction()==MotionEvent.ACTION_UP) {

                    shop.setScaleX(1f);
                    shop.setScaleY(1f);
                    shop.setRotation(0);
                }
                return false;
            }
        });

        settings.setOnClickListener(v -> settingsClick());
        startGame.setOnClickListener(v -> startGameClick());

    }

    private void homeClick() {
        onBackPressed();

    }

    void shareAppClick() {
            StringBuilder msg = new StringBuilder();
            msg.append("Давай сыграем в дурака? Мой Ник \"" + name.getText()+'"');
            msg.append("\n");
            msg.append("https://play.google.com/store/apps/details?id=ru.tmkstd.cardgamedurakonline");
            Intent shareIntent = new Intent();
            shareIntent.setAction(Intent.ACTION_SEND);
            shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, msg.toString());
            shareIntent.setType("text/plain");
            try {
                startActivity(Intent.createChooser(shareIntent, "Поделиться: "));
            } catch (ActivityNotFoundException e) {
                Toast.makeText(this,"Приложение недоступно", Toast.LENGTH_SHORT).show();
            }

    }
    void friendsListClick() {
        if(coin > -1) {
            if(versionStart) {
                Intent intent = new Intent(MainActivity.this, FriendsListActivity.class);
                overridePendingTransition(0, 0);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
            }
            else {
                AlertDialog.Builder quitDialog = new AlertDialog.Builder(this);
                quitDialog.setTitle("Пожалуйста обновите приложение");
                quitDialog.setPositiveButton("Google play!", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String urle = getPackageName();
                        try {
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id="+urle)));
                        } catch (android.content.ActivityNotFoundException e) {
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id="+urle)));
                        }
                    }
                });
                quitDialog.setNegativeButton("Позже", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                quitDialog.show();
            }
        }

    }
    void plusImageClick() {
//                            Отправляем в ShopActivity
        Intent intent = new Intent(MainActivity.this, ShopActivity.class);
        overridePendingTransition(0,0);
        intent.putExtra("CoinReward",true);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
//        if(coin > -1) {
//            if(coin>9) {
//                myRef.child("ReclamaTimer").child(myId).addListenerForSingleValueEvent(TimerReclamaListener);
//            }
//            else {
//                AlertDialog.Builder quitDialog = new AlertDialog.Builder(MainActivity.this);
//                quitDialog.setTitle("Посмотреть обьявление, чтобы получить 10 монет?");
//                quitDialog.setPositiveButton("Таки ДА!", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        if (mRewardedVideoAd.isLoaded()) {
//                            mRewardedVideoAd.show();
//                        } else {
//                            Toast.makeText(MainActivity.this, "Обьявление не загружено.", Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                });
//                quitDialog.setNegativeButton("Таки НИЗАШО!", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//
//                    }
//                });
//                quitDialog.show();
//            }
//
//        }
    }
    void avatarClick() {
        Log.d("LogMain","1");
        if(ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            Log.d("LogMain","2");
            //Запрос разрешения
            if(ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.READ_EXTERNAL_STORAGE)) {
                Log.d("LogMain","3");

                AlertDialog.Builder quitDialog = new AlertDialog.Builder(this);
                quitDialog.setTitle("Для загрузки Аватара требуется доступ к медиа.");
                quitDialog.setPositiveButton("Предоставить разрешение", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which){
                        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 102);
                    }
                });
                quitDialog.setNegativeButton("Отказ", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                quitDialog.show();
            }
            else {
                Log.d("LogMain","4");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 102);
            }
        } else {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            startActivityForResult(intent, 1);
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grandResults) {
        switch (requestCode) {
            case 102:{
                if (grandResults.length > 0) {
                    if (grandResults[0] == PackageManager.PERMISSION_GRANTED) {
                        Intent intent = new Intent(Intent.ACTION_PICK);
                        intent.setType("image/*");
                        startActivityForResult(intent, 1);
                    }
                }
                super.onRequestPermissionsResult(requestCode,permissions,grandResults);
                break;
            }
            default: break;
        }
    }
    private Bitmap unionBitmap(Bitmap i) {
        Paint maskPaint = new Paint();
        maskPaint.setAntiAlias(true);
        maskPaint.setXfermode(new PorterDuffXfermode((PorterDuff.Mode.DST_IN)));
//        maskPaint.setColor(Color.argb(0,0,0,0));
        Bitmap outPut = Bitmap.createBitmap(i.getWidth(),i.getHeight(), Bitmap.Config.ARGB_8888);
        Bitmap mask = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(
                this.getResources(), R.drawable.avatar_mask),
                avatar.getWidth(), avatar.getWidth(),false);
        Bitmap frame = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(
                this.getResources(), R.drawable.avatar_frame),
                i.getWidth(), i.getWidth(),false);
        Canvas unionCanvas = new Canvas(outPut);
        unionCanvas.drawBitmap(i,0,0,null);
        unionCanvas.drawBitmap(mask,0,0,maskPaint);
        unionCanvas.drawBitmap(frame,0,0,null);
        mask.recycle();
        mask = null;
        frame.recycle();
        frame = null;
        return outPut;
    }
    public Bitmap decodeSampledBitmapFromResource(Intent res, int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        try {
            BitmapFactory.decodeStream(getContentResolver().openInputStream(res.getData()),null, options);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        try {
            return BitmapFactory.decodeStream(getContentResolver().openInputStream(res.getData()),null, options);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }
    public int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;
        if (height > reqHeight || width > reqWidth) {
            final int halfHeight = height / 2;
            final int halfWidth = width / 2;
            while ((halfHeight / inSampleSize) >= reqHeight
                    && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }
    public void onActivityResult(int requestCode, int resultCode,Intent data){
        super.onActivityResult(requestCode,resultCode,data);
//        bitmap=null;
        if (resultCode == Activity.RESULT_OK){
            if(requestCode == 1){
                //попытка защиты от OutOfMemoryError
                bitmap = decodeSampledBitmapFromResource(data,avatar.getWidth(),avatar.getWidth());
                if (bitmap != null){
                    int width = bitmap.getWidth();
                    int height = bitmap.getHeight();
//                    Была ошибка, я указывал в конце конечную точкуб а надо было указать ширину и высоту
                    if (width>height){
                        bitmap = Bitmap.createBitmap(bitmap,(width-height)/2,0,height,height);
                    }
                    else if (width < height){
                        bitmap = Bitmap.createBitmap(bitmap,0,(height-width)/2,width,width);
                    }
//                    bitmap = Bitmap.createScaledBitmap(bitmap,
//                            maxX/4, maxX/4,false);
                    bitmap = Bitmap.createScaledBitmap(bitmap,
                            avatar.getWidth(), avatar.getWidth(),false);
//                    File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),"AvatarOnlineDurak.png");
//                    Drawable drawable= new BitmapDrawable(getResources(),bitmap);
//                    RoundedBitmapDrawable drawable = RoundedBitmapDrawableFactory.create(getResources(),bitmap);
//                    drawable.setCircular(true);
//                    drawable.setCornerRadius(25);
                    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                    bitmap = unionBitmap(bitmap);
//                    Drawable drawable= new BitmapDrawable(getResources(),bitmap);
                    bitmap.compress(Bitmap.CompressFormat.PNG,100,outputStream);


//                    bitmap.compress(Bitmap.CompressFormat.JPEG,100,bitmap);
                    byte[] dataBitmap = outputStream.toByteArray();

                    UploadTask uploadTask = storageRef.child(myId).putBytes(dataBitmap);
                    uploadTask.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                        }
                    }).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                        }
                    });
//                    avatar.setBackground(drawable);
                    avatar.setImageBitmap(bitmap);
//                    bitmap.recycle();
                }
            }
        }
    }
    void rulesClick() {
        Intent intent = new Intent(MainActivity.this, RulesActivity.class);
        overridePendingTransition(0,0);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
    }
    void shopClick() {
        Intent intent = new Intent(MainActivity.this, ShopActivity.class);
        overridePendingTransition(0,0);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
    }
    void settingsClick() {
        Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
        overridePendingTransition(0, 0);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
    }


    void setStatusOnline() {
        DatabaseReference myStatus = myRef.child("Social").child(myId).child("Status");
        myStatus.setValue(true);
        myStatus.onDisconnect().setValue(false);
    }
    void startGameClick() {
        if (versionStart) {
            if (coin > -1) {
                if (coin > 9) {
                    Intent intent;
                    if (firstGame) {
                        intent = new Intent(MainActivity.this, SettingsActivity.class);
                        intent.putExtra("coin", coin);
                    } else {
                        intent = new Intent(MainActivity.this, GameEngineActivity.class);
                    }
                    overridePendingTransition(0, 0);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    startActivity(intent);
                } else {
                    AlertDialog.Builder quitDialog = new AlertDialog.Builder(MainActivity.this);
                    quitDialog.setTitle("Недостаточно монет для ставки.");
                    quitDialog.setPositiveButton("Пополнить", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
//                            Отправляем в ShopActivity
                            Intent intent = new Intent(MainActivity.this, ShopActivity.class);
                            overridePendingTransition(0,0);
                            intent.putExtra("CoinReward",true);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                            startActivity(intent);
                        }
                    });
                    quitDialog.setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });
                    quitDialog.show();
                }
            }
        }
        else {
            AlertDialog.Builder quitDialog = new AlertDialog.Builder(this);
            quitDialog.setTitle("Пожалуйста обновите приложение");
            quitDialog.setPositiveButton("Google play!", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String urle = getPackageName();
                    try {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id="+urle)));
                    } catch (android.content.ActivityNotFoundException e) {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id="+urle)));
                    }
                }
            });
            quitDialog.setNegativeButton("Позже", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            quitDialog.show();

        }
    }
    @Override
    public void onBackPressed() {
    Log.d("testDeb","pressBack");
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);

    }

    @Override
    public void onResume() {
        Log.e("MainActivity  ", "onResume()");
        homeBtn.setScaleX(1f);
        homeBtn.setScaleY(1f);
        plusImage.setScaleX(1f);
        plusImage.setScaleY(1f);
        plusImage.setRotation(0);
        connectBilling();
        super.onResume();
    }

    @Override
    public void onPause() {
//        mRewardedVideoAd.pause(this);
        mBillingClient.endConnection();

        Log.e("MainActivity  ", "onPause()");
        save = getSharedPreferences("Save", MODE_PRIVATE);
        editor = save.edit();
        editor.putBoolean("firstGame",firstGame);
//        if(firstGame){
            editor.putFloat("counterCoinWidth",underCoin.getWidth()/(float)maxX);
//        }
        editor.putBoolean("modGame",modGame);
        editor.putInt("colorField",colorField);
        editor.putInt("numCards",numCards);
        editor.putLong("coin",coin);
        editor.commit();
        super.onPause();
    }
    @Override
    public void onDestroy() {
        myRef.child("Social").child(myId).child("RequestsIn").removeEventListener(numberOfReqFriend);
        super.onDestroy();
    }

    @SuppressLint("NewApi")
    @Override
    public void onWindowFocusChanged(boolean hasFocus)
    {
        super.onWindowFocusChanged(hasFocus);
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


    void refreshCoins(){

        if (coin < 0)counterCoin.setText("Загрузка");
        else if(coin<1000)counterCoin.setText(coin+"");
        else if(coin<10000)counterCoin.setText((int)(coin/1000)+","+coin%1000/100+coin%100/10+"K");
        else if(coin<100000)counterCoin.setText((int)(coin/1000)+","+coin%1000/100+"K");
        else if(coin<1000000)counterCoin.setText((int)(coin/1000)+"K");
        else if(coin<10000000)counterCoin.setText((int)(coin/1000000)+","+coin%1000000/100000+coin%100000/10000+"KK");
        else counterCoin.setText("Unreal!");
    }

    void connectBilling(){
        mBillingClient = BillingClient.newBuilder(this).setListener(new PurchasesUpdatedListener() {
            @Override
            public void onPurchasesUpdated(BillingResult billingResult, List<Purchase> purchases) {

                Log.d("purhEndFirst1",billingResult.getResponseCode()+ " purc:" + purchases);
                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK
                        && purchases != null) {
                    for (Purchase purchase : purchases) {
                        Log.d("purhEndFirst1",purchase.getPurchaseState()+"");
                        switch (purchase.getPurchaseState()) {
                            case Purchase.PurchaseState.PURCHASED:
                                myRef.child("Buys").child(myId).child(purchase.getOrderId().replaceAll("[^\\w\\s]"," ")).setValue("PURCHASED");
                                handlePurchase(purchase);
                                break;
                            case Purchase.PurchaseState.PENDING:
                                myRef.child("Buys").child(myId).child(purchase.getOrderId().replaceAll("[^\\w\\s]"," ")).setValue("PENDING");
//                                coinsFragment.pending(purchase.getSku());
                        }

                    }
                } else if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.USER_CANCELED) {
                    // Handle an error caused by a user cancelling the purchase flow.
                    Log.d("purhEndFirst1","user_Canceled");
                } else {
                    // Handle any other error codes.
                    Log.d("purhEndFirst1","ERROR");
//                    for (Purchase purchase : purchases) {
//                        Log.d("purhEndFirst1","ERROR"+ purchase.getPurchaseState()+"");
//                    }
                }
            }
        })
                .enablePendingPurchases()
                .build();

        mBillingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingSetupFinished(BillingResult billingResult) {
                if (billingResult.getResponseCode() ==  BillingClient.BillingResponseCode.OK) {
                    // The BillingClient is ready. You can query purchases here.
                    // BillingClient готов. Здесь вы можете запросить покупки.
                    List<String> skuList = new ArrayList<>();
//                    skuList.add("premium_upgrade");
//                    skuList.add("gas");
                    skuList.add("coin50");
                    skuList.add("coin150");
                    skuList.add("coin250");
                    skuList.add("coin777");
                    skuList.add("coin99999");
                    skuList.add("donut1");
                    skuList.add("donut2");
                    skuList.add("coffee1");
                    SkuDetailsParams.Builder params = SkuDetailsParams.newBuilder();
                    params.setSkusList(skuList).setType(BillingClient.SkuType.INAPP);
                    mBillingClient.querySkuDetailsAsync(params.build(), new SkuDetailsResponseListener() {
                        @Override
                        public void onSkuDetailsResponse(BillingResult billingResult2, List<SkuDetails> skuDetailsList) {
                            // Process the result.
                            // Обрабатываем результат.
                            if (billingResult2.getResponseCode() ==  BillingClient.BillingResponseCode.OK) {
                                Log.d("purhEndbillingResult2"," billingResult2: OK");
//                                coinsFragment.lookPurs(mBillingClient,skuDetailsList);
                            }
                            for(SkuDetails skuDetails:skuDetailsList){

                                Log.d("purhEndFirst2",skuDetails.getSku()+" billingResult2: "+billingResult2.getResponseCode());
                            }
                            Purchase.PurchasesResult result =  mBillingClient.queryPurchases(BillingClient.SkuType.INAPP);
                            List<Purchase> purchases = result.getPurchasesList();
                            for(Purchase purchase:purchases){
                                Log.d("purhEndResult","state:" + purchase.getPurchaseState());
//                                        if(purchase.getPurchaseState()==Purchase.PurchaseState.PURCHASED){
//                                            handlePurchase(purchase);
//                                        }
                                switch (purchase.getPurchaseState()) {
                                    case Purchase.PurchaseState.PURCHASED:
                                        myRef.child("Buys").child(myId).child(purchase.getOrderId().replaceAll("[^\\w\\s]"," ")).setValue("PURCHASED");
                                        handlePurchase(purchase);
                                        break;
                                    case Purchase.PurchaseState.PENDING:
                                        myRef.child("Buys").child(myId).child(purchase.getOrderId().replaceAll("[^\\w\\s]"," ")).setValue("PENDING");
//                                        coinsFragment.pending(purchase.getSku());
                                }
                            }
                        }
                    });
                }
            }
            @Override
            public void onBillingServiceDisconnected() {
            }
        });

    }
    void deleteGamesFieldAll(){
        Log.d("deleteGame","Start");
        Toast.makeText(getApplicationContext(), "Start", Toast.LENGTH_SHORT).show();
        countAllGames = 0;
        myRef.child("GameRoom").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot!=null) {
                    countAllGames = dataSnapshot.getChildrenCount();
                    Toast.makeText(getApplicationContext(), Long.toString(countAllGames), Toast.LENGTH_SHORT).show();
                    for(DataSnapshot snapshot: dataSnapshot.getChildren()) {
                        String key1,key2;
                        key1 = snapshot.getKey().substring(0,myId.length());
                        key2 = snapshot.getKey().substring(myId.length());
                        if (snapshot.hasChild(key1) && snapshot.hasChild(key2)) {
                            myRef.child("GameRoom").child(key1+key2).child(key1).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.getValue().toString().equals("Exit")){
                                        myRef.child("GameRoom").child(key1+key2).child(key2).addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                                if (dataSnapshot.getValue().toString().equals("Exit")){
                                                    myRef.child("GameRoom").child(key1+key2).removeValue();
                                                    countAllGames--;
                                                    Log.d("deleteGame",Long.toString(countAllGames));

                                                }

                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                            }
                                        });
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });

                        }
                        else {
                            myRef.child("GameRoom").child(key1+key2).removeValue();
                            countAllGames--;
                            Log.d("deleteGame",Long.toString(countAllGames));
                        }

                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    void handlePurchase(Purchase purchase) {
        // Покупка получена из BillingClient # queryPurchases или из вашего PurchasesUpdatedListener.//
        // Подтверждаем покупку.
        // Убедитесь, что права на этот токен покупки еще не предоставлены.
        // Предоставление прав пользователю.

        Purchase purchase1 = purchase;
        ConsumeParams consumeParams =
                ConsumeParams.newBuilder()
                        .setPurchaseToken(purchase1.getPurchaseToken())
                        .build();

        ConsumeResponseListener listener = new ConsumeResponseListener() {
            @Override
            public void onConsumeResponse(BillingResult billingResult, String purchaseToken) {
                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
//                    purchase1.getSku();
                    Log.d("purhEnd",purchase1.getSku()+ " " + purchase1.getOrderId());
                    // Handle the success of the consume operation.
                    // Обработка успеха операции потребления.
                    switch (purchase1.getSku()){
                        case "coin50":
                            coin += 50;
                            numDialog = 0;
                            break;
                        case "coin150":
                            coin += 150;
                            numDialog = 1;
                            break;
                        case "coin250":
                            coin += 250;
                            numDialog = 2;
                            break;
                        case "coin777":
                            coin += 777;
                            numDialog = 3;
                            break;
                        case "coin99999":
                            coin += 9999999999L;
                            numDialog = 4;
                            break;
                        case "donut1":
                            numDialog = 5;
                            break;
                        case "donut2":
                            numDialog = 5;
                            break;
                        case "coffee1":
                            numDialog = 6;
                            break;
                    }

//                    coinsFragment.consume(purchase1.getSku());
//                    editor.putLong("coin",coin);
//                    editor.commit();
                    myRef.child("Coins").child(myId).setValue(coin);
                    refreshCoins();
                    myRef.child("Buys").child(myId).child(purchase1.getOrderId().replaceAll("[^\\w\\s]"," ")).setValue("consume");
                    MainActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            android.app.AlertDialog.Builder quitDialog = new android.app.AlertDialog.Builder(MainActivity.this);
                            quitDialog.setTitle("Спасибо за покупку");  // заголовок

                            if(numDialog<0) {
                                quitDialog.setMessage("Error 0909"); // сообщение
                                myRef.child("Buys").child(myId).child(purchase1.getOrderId().replaceAll("[^\\w\\s]"," ")+"error").setValue("error 0909");

                            }
                            else {
                                quitDialog.setMessage(dialogs[numDialog]); // сообщение
                            }
                            quitDialog.setPositiveButton("ОК", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                }
                            });
                            quitDialog.show();
                        }
                    });

                }
                else {
                    Log.d("purhEnd","Потребление откланён " + purchase1.getSku());

                }
            }
        };

        mBillingClient.consumeAsync(consumeParams, listener);
    }

}
