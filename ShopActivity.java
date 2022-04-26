package ru.tmkstd.cardgamedurakonline;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import ru.tmkstd.cardgamedurakonline.ShopFragments.CoinsFragment;
import ru.tmkstd.cardgamedurakonline.ShopFragments.DonutsFragment;
import ru.tmkstd.cardgamedurakonline.ShopFragments.SmilesFragment;


public class ShopActivity extends AppCompatActivity {

//    TextView shopName, shopNameShadow;
    ImageView back;
    ImageView markSmiles, markDonuts, markCoins;
    ImageView maskSmiles, maskDonuts, maskCoins;
    TextViewGradient coinCount;
    ValueEventListener coinsListener;
    long coin = -1;
    AdView mAdView;
    RelativeLayout layout;
    ViewPager viewPager;
    ShopActivity.ViewPagerAdapter viewPagerAdapter;
    SmilesFragment smilesFragment;
    DonutsFragment donutsFragment;
    CoinsFragment coinsFragment;
    float normalYMark;
    int divinePosY;
    DatabaseReference ref;
    String myKeyId;
    SharedPreferences.Editor editor;
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
//    SharedPreferences save;
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
        setContentView(R.layout.activity_shop);
        normalYMark=0;
        divinePosY = 15;

        markSmiles = findViewById(R.id.shop_mark_smiles);
        markDonuts = findViewById(R.id.shop_mark_donuts);
        markCoins = findViewById(R.id.shop_mark_coins);
        maskSmiles = findViewById(R.id.shop_mark_mask_smiles);
        maskDonuts = findViewById(R.id.shop_mark_mask_donuts);
        maskCoins = findViewById(R.id.shop_mark_mask_coins);


//        shopNameShadow = findViewById(R.id.shop_name_shadow);
//        shopNameShadow.getPaint().setMaskFilter(new BlurMaskFilter(1, BlurMaskFilter.Blur.NORMAL));
//        shopName = findViewById(R.id.shop_name);
//        shopName.setAlpha(1);
//        shopName.getPaint().setShader(new LinearGradient(0,shopName.getTextSize()/2+shopName.getTextSize()/6,0,shopName.getTextSize()/6+shopName.getTextSize()+shopName.getTextSize()/6, Color.rgb(236,240,45),Color.rgb(204,97,24), Shader.TileMode.CLAMP));

        mAdView = findViewById(R.id.adViewShop);
        mAdView.loadAd(new AdRequest.Builder().build());

        layout = findViewById(R.id.shopLayout);

        viewPager = findViewById(R.id.viewPagerShop);

//        save = getSharedPreferences("Save", MODE_PRIVATE);
        switch (getSharedPreferences("Save", MODE_PRIVATE).getInt("colorField",0)){
            case 0: layout.setBackgroundResource(R.drawable.fon_one); break;
            case 1: layout.setBackgroundResource(R.drawable.fon_two); break;
            case 2: layout.setBackgroundResource(R.drawable.fon_three); break;
            case 3: layout.setBackgroundResource(R.drawable.fon_four); break;
            default:layout.setBackgroundResource(R.drawable.fon_one); break;
        }

        editor = getSharedPreferences("Save", MODE_PRIVATE).edit();
        myKeyId = getSharedPreferences("Save", MODE_PRIVATE).getString("myKeyId",null);

        back = findViewById(R.id.shop_back);
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
        ref = FirebaseDatabase.getInstance().getReference();
        coinCount = findViewById(R.id.coinCountText);
        creatFragments();
        coinsListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                coinCount.setText(String.valueOf(dataSnapshot.getValue(Long.class)));
                if(dataSnapshot.getValue(Long.class)!=null) {
                    coin = dataSnapshot.getValue(Long.class);
                    smilesFragment.setCoins(coin);
                    coinsFragment.setCoins(coin);
                    refreshCoinsCount();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
// В самом конце подключаю слушатели вкладок
        markSmiles.setOnClickListener(v -> viewPager.setCurrentItem(0));
        markDonuts.setOnClickListener(v -> viewPager.setCurrentItem(1));
        markCoins.setOnClickListener(v -> viewPager.setCurrentItem(2));
//        connectBilling();
    }

    void creatFragments() {
        viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());

        smilesFragment = new SmilesFragment(this, myKeyId, ref);
        donutsFragment = new DonutsFragment();
        coinsFragment = new CoinsFragment(this, myKeyId, ref);

        viewPagerAdapter.addFragment(smilesFragment);
        viewPagerAdapter.addFragment(donutsFragment);
        viewPagerAdapter.addFragment(coinsFragment);

        viewPager.setOffscreenPageLimit(2);
        viewPager.setAdapter(viewPagerAdapter);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                Log.d("onPageSelected"," " + position);
                switch (position) {
                    case 1:
//                        markSmiles.setImageResource(R.drawable.b_mask_mark_shop);
//                        markDonuts.setImageDrawable(null);
//                        markCoins.setImageResource(R.drawable.b_mask_mark_shop);
                        markSmiles.setY(normalYMark);
                        markDonuts.setY(normalYMark+markSmiles.getHeight()/divinePosY);
                        markCoins.setY(normalYMark);

                        maskSmiles.setVisibility(View.INVISIBLE);
                        maskDonuts.setVisibility(View.VISIBLE);
                        maskCoins.setVisibility(View.INVISIBLE);
//                        friendsMark.setY(friendsMark.getHeight()/3);
//                        findMark.setY(0);
//                        requestsMark.setY(friendsMark.getHeight()/3);
//                        myRequestsMark.setY(friendsMark.getHeight()/3);
                        break;
                    case 2:
//                        markSmiles.setImageResource(R.drawable.b_mask_mark_shop);
//                        markDonuts.setImageResource(R.drawable.b_mask_mark_shop);
//                        markCoins.setImageDrawable(null);

                        markSmiles.setY(normalYMark);
                        markDonuts.setY(normalYMark);
                        markCoins.setY(normalYMark+markSmiles.getHeight()/divinePosY);
                        maskSmiles.setVisibility(View.INVISIBLE);
                        maskDonuts.setVisibility(View.INVISIBLE);
                        maskCoins.setVisibility(View.VISIBLE);
//                        friendsMark.setY(friendsMark.getHeight()/3);
//                        findMark.setY(friendsMark.getHeight()/3);
//                        requestsMark.setY(0);
//                        myRequestsMark.setY(friendsMark.getHeight()/3);
                        break;
                    default:
//                        markSmiles.setImageDrawable(null);
//                        markDonuts.setImageResource(R.drawable.b_mask_mark_shop);
//                        markCoins.setImageResource(R.drawable.b_mask_mark_shop);

                        markSmiles.setY(normalYMark+markSmiles.getHeight()/divinePosY);
                        markDonuts.setY(normalYMark);
                        markCoins.setY(normalYMark);
                        maskSmiles.setVisibility(View.VISIBLE);
                        maskDonuts.setVisibility(View.INVISIBLE);
                        maskCoins.setVisibility(View.INVISIBLE);
//                        friendsMark.setY(0);
//                        findMark.setY(friendsMark.getHeight()/3);
//                        requestsMark.setY(friendsMark.getHeight()/3);
//                        myRequestsMark.setY(friendsMark.getHeight()/3);
                        break;

                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
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
                                ref.child("Buys").child(myKeyId).child(purchase.getOrderId().replaceAll("[^\\w\\s]"," ")).setValue("PURCHASED");
                                handlePurchase(purchase);
                                break;
                            case Purchase.PurchaseState.PENDING:
                                ref.child("Buys").child(myKeyId).child(purchase.getOrderId().replaceAll("[^\\w\\s]"," ")).setValue("PENDING");
                                coinsFragment.pending(purchase.getSku());
                                donutsFragment.pending(purchase.getSku());
                        }

                    }
                } else if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.USER_CANCELED) {
                    // Handle an error caused by a user cancelling the purchase flow.
                    Log.d("purhEndFirst1Can","user_Canceled purchases NULL =" + (purchases == null));
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
                    List<String> skuList = new ArrayList<> ();
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
                                coinsFragment.lookPurs(mBillingClient,skuDetailsList);
                                donutsFragment.lookPurs(mBillingClient,skuDetailsList);
                            }
                            for(SkuDetails skuDetails:skuDetailsList){

                                Log.d("purhEndFirst2",skuDetails.getSku()+" billingResult2: "+billingResult2.getResponseCode());
                            }
                            Purchase.PurchasesResult result =  mBillingClient.queryPurchases(BillingClient.SkuType.INAPP);
                            List<Purchase> purchases = result.getPurchasesList();
                            for(Purchase purchase:purchases){
                                Log.d("purhEndResult","state:" + purchase.getPurchaseState() + " id: " + purchase.getOrderId()+" delId: " + purchase.getOrderId().replaceAll("[^\\w\\s]"," "));
//                                        if(purchase.getPurchaseState()==Purchase.PurchaseState.PURCHASED){
//                                            handlePurchase(purchase);
//                                        }
                                switch (purchase.getPurchaseState()) {

                                    case Purchase.PurchaseState.PURCHASED:
                                        ref.child("Buys").child(myKeyId).child(purchase.getOrderId().replaceAll("[^\\w\\s]"," ")).setValue("PURCHASED");
                                        handlePurchase(purchase);
                                        break;
                                    case Purchase.PurchaseState.PENDING:
                                        ref.child("Buys").child(myKeyId).child(purchase.getOrderId().replaceAll("[^\\w\\s]"," ")).setValue("PENDING");
                                        coinsFragment.pending(purchase.getSku());
                                        donutsFragment.pending(purchase.getSku());
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

                    coinsFragment.consume(purchase1.getSku());
                    donutsFragment.consume(purchase1.getSku());
                    editor.putLong("coin",coin);
                    editor.commit();
                    ref.child("Coins").child(myKeyId).setValue(coin);
                    refreshCoinsCount();
                    ref.child("Buys").child(myKeyId).child(purchase1.getOrderId().replaceAll("[^\\w\\s]"," ")).setValue("consume");
                    ShopActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            AlertDialog.Builder quitDialog = new AlertDialog.Builder(ShopActivity.this);
                            quitDialog.setTitle("Спасибо за покупку");  // заголовок

                            if(numDialog<0) {
                                quitDialog.setMessage("Error 0909"); // сообщение
                                ref.child("Buys").child(myKeyId).child(purchase1.getOrderId().replaceAll("[^\\w\\s]"," ")+"error").setValue("error 0909");

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
    public void onWindowFocusChanged(boolean hasFocus) {
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
        if (normalYMark==0) {
            normalYMark = markSmiles.getY();
            markSmiles.setY(normalYMark+markSmiles.getHeight()/divinePosY);
            markDonuts.setY(normalYMark);
            markCoins.setY(normalYMark);
            if (getIntent().getBooleanExtra("CoinReward", false)) {
                viewPager.setCurrentItem(2);
            }
            else {
            }
        }
    }
    @Override
    public void onResume() {
        super.onResume();
        connectBilling();
        ref.child("Coins").child(myKeyId).addValueEventListener(coinsListener);
    }
    @Override
    public void onPause(){
        super.onPause();
        mBillingClient.endConnection();
        ref.child("Coins").child(myKeyId).removeEventListener(coinsListener);
        if (coin >= 0) {
            editor.putLong("coin", coin);
        }
        editor.commit();
    }
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(ShopActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        overridePendingTransition(0,0);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
        finish();
    }

    private void refreshCoinsCount() {
        if (coin < 0)
            coinCount.setText("Загрузка");
        else if (coin < 1000)
            coinCount.setText("На счету : " + coin + "");
        else if (coin < 10000)
            coinCount.setText("На счету : " + (int) (coin / 1000) + "," + coin % 1000 / 100 + coin % 100 / 10 + "K");
        else if (coin < 100000)
            coinCount.setText("На счету : " + (int) (coin / 1000) + "," + coin % 1000 / 100 + "K");
        else if (coin < 1000000)
            coinCount.setText("На счету : " + (int) (coin / 1000) + "K");
        else if (coin < 10000000)
            coinCount.setText("На счету : " + (int) (coin / 1000000) + "," + coin % 1000000 / 100000 + coin % 100000 / 10000 + "KK");
        else
            coinCount.setText("Unreal!");

    }

}
