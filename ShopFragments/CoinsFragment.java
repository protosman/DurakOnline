package ru.tmkstd.cardgamedurakonline.ShopFragments;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.SkuDetails;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.gms.ads.rewarded.RewardedAdCallback;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import com.google.android.gms.ads.rewarded.RewardedAd;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import ru.tmkstd.cardgamedurakonline.R;
import ru.tmkstd.cardgamedurakonline.ShopActivity;

public class CoinsFragment extends Fragment
//        implements RewardedVideoAdListener
{
//    private RewardedVideoAd mRewardedVideoAd;

    private RewardedAd rewardedAd;

    LinearLayout containerCoins, empty;
    Button[] myBuyButton;
    ProgressBar[] myBuyCycles;
    private BillingClient mBillingClient;
    Context context;
    String myKeyId;
    DatabaseReference ref;
    long coins;
    ValueEventListener TimerReclamaListener;

//    private List<SkuDetails> mSkuDetailsMap = new ArrayList<>();
    private HashMap<String,SkuDetails> mSkuDetailsMap = new HashMap<>();
//    private String mSkuId = "toast_hello_world";
    String[] mSkusId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_shop_coins, container, false);
//        containerCoins = rootView.findViewById(R.id.container_shop_coins);
        myBuyButton[0] = rootView.findViewById(R.id.buyButton0);
        myBuyButton[1] = rootView.findViewById(R.id.buyButton1);
        myBuyButton[2] = rootView.findViewById(R.id.buyButton2);
        myBuyButton[3] = rootView.findViewById(R.id.buyButton3);
        myBuyButton[4] = rootView.findViewById(R.id.buyButton4);
        myBuyButton[5] = rootView.findViewById(R.id.buyButton5);
        myBuyCycles[0] = rootView.findViewById(R.id.buyCycle0);
        myBuyCycles[1] = rootView.findViewById(R.id.buyCycle1);
        myBuyCycles[2] = rootView.findViewById(R.id.buyCycle2);
        myBuyCycles[3] = rootView.findViewById(R.id.buyCycle3);
        myBuyCycles[4] = rootView.findViewById(R.id.buyCycle4);
        containerCoins = rootView.findViewById(R.id.container_shop_coins);
        empty = rootView.findViewById(R.id.container_shop_coins_empty);
        return rootView;
    }

    public CoinsFragment(Context context,String myKey, DatabaseReference ref) {
//        initBilling();
        this.context = context;
        myKeyId = myKey;
        this.ref = ref;
        myBuyButton = new Button[6];
        myBuyCycles = new ProgressBar[5];
        mSkusId = new String[5];
        mSkusId[0] = "coin50";
        mSkusId[1] = "coin150";
        mSkusId[2] = "coin250";
        mSkusId[3] = "coin777";
        mSkusId[4] = "coin99999";
        coins = -1L;//проверкатка на загрузку данных
        initListeners();

        rewardedAd = createAndLoadRewardedAd();//Создаю и загружаю обьявление


    }

    public void setCoins(long coins) {
        this.coins = coins;
    }
    void click(){
        if(coins > -1) {
            if(coins>9) {
                ref.child("ReclamaTimer").child(myKeyId).addListenerForSingleValueEvent(TimerReclamaListener);
            }
            else {
                AlertDialog.Builder quitDialog = new AlertDialog.Builder(context);
                quitDialog.setTitle("Посмотреть обьявление, чтобы получить 10 монет?");
                quitDialog.setPositiveButton("Таки ДА!", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        adsRewardPressYes();
                    }
                });
                quitDialog.setNegativeButton("Таки НИЗАШО!", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                quitDialog.show();
            }

        }
    }
    public void lookPurs(BillingClient billingClient, List<SkuDetails> skuDetailsList){
        mBillingClient = billingClient;
        for(SkuDetails skuDetails:skuDetailsList){
            mSkuDetailsMap.put(skuDetails.getSku(),skuDetails);
        }
        myBuyButton[0].setText("0,00 " + mSkuDetailsMap.get(mSkusId[0]).getPrice().substring(mSkuDetailsMap.get(mSkusId[0]).getPrice().length()-1));
        for(int i = 1; i < myBuyButton.length; i++) {
            myBuyButton[i].setText(mSkuDetailsMap.get(mSkusId[i-1]).getPrice());
        }
        if(myBuyButton[0]!=null) {
            myBuyButton[0].setOnClickListener(v -> click());
            myBuyButton[1].setOnClickListener(v -> launchBilling(mSkusId[0]));
            myBuyButton[2].setOnClickListener(v -> launchBilling(mSkusId[1]));
            myBuyButton[3].setOnClickListener(v -> launchBilling(mSkusId[2]));
            myBuyButton[4].setOnClickListener(v -> launchBilling(mSkusId[3]));
            myBuyButton[5].setOnClickListener(v -> launchBilling(mSkusId[4]));
            containerCoins.setVisibility(View.VISIBLE);
            empty.setVisibility(View.GONE);
        }
    }


    private void initListeners() {
        TimerReclamaListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                plusImage.setBackgroundResource(R.drawable.plus);
                if(dataSnapshot.getValue(Long.class)==null || dataSnapshot.getValue(Long.class)+600000<System.currentTimeMillis()){
                    AlertDialog.Builder quitDialog = new AlertDialog.Builder(context);
                    quitDialog.setTitle("Посмотреть обьявление, чтобы получить 10 монет?");
                    quitDialog.setPositiveButton("Таки ДА!", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            adsRewardPressYes();

                        }
                    });
                    quitDialog.setNegativeButton("Таки НИЗАШО!", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    quitDialog.show();
                }
                else{
                    AlertDialog.Builder quitDialog = new AlertDialog.Builder(context);
                    quitDialog.setTitle("Доступно через "+(600 -(System.currentTimeMillis()/1000-dataSnapshot.getValue(Long.class)/1000))+" сек.");
                    quitDialog.setPositiveButton("Хорошо", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });
                    quitDialog.show();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
    }
    public void pending(String SKU) {
        for (int i = 0;i<mSkusId.length;i++){
            if(mSkusId[i].equals(SKU)){
                myBuyButton[i+1].setVisibility(View.GONE);
                myBuyCycles[i].setVisibility(View.VISIBLE);
            }
        }
    }
    public void consume(String SKU) {
        for (int i = 0;i<mSkusId.length;i++){
            if(mSkusId[i].equals(SKU)){
                myBuyButton[i+1].setVisibility(View.VISIBLE);
                myBuyCycles[i].setVisibility(View.GONE);
            }
        }
    }
    public void launchBilling(String skuId) {
//        pending(skuId);
        if(mSkuDetailsMap.containsKey(skuId)) {
            BillingFlowParams billingFlowParams = BillingFlowParams.newBuilder()
                    .setSkuDetails(mSkuDetailsMap.get(skuId))
                    .build();
            mBillingClient.launchBillingFlow(getActivity(), billingFlowParams);
        }
    }

    public RewardedAd createAndLoadRewardedAd() {

//        RewardedAd rewardedAdTemp = new RewardedAd(context,"ca-app-pub-3940256099942544/5224354917");//тестовая
        RewardedAd rewardedAdTemp = new RewardedAd(context,"ca-app-pub-9488118969901249/7435606509");//Моя

        RewardedAdLoadCallback adLoadCallback = new RewardedAdLoadCallback() {
            @Override
            public void onRewardedAdLoaded() {
                // Ad successfully loaded.
            }

            @Override
            public void onRewardedAdFailedToLoad(LoadAdError adError) {
//// когда нет интернета просто показывает это сообщение. Надо обрабатывать ошибки
                // Ad failed to load.
            }
        };
        rewardedAdTemp.loadAd(new AdRequest.Builder().build(), adLoadCallback);
        return rewardedAdTemp;
    }

    protected void rewardedItems(RewardItem reward){
        Toast.makeText(context, "Поздравляю, 10 монет теперь ваши!!!", Toast.LENGTH_SHORT).show();
        coins = coins + 10;
//        plusImage.setBackgroundResource(R.drawable.plus);
        Date currentDate = new Date();
        DateFormat timeFormat = new SimpleDateFormat("yy-MM-dd HH:mm:ss", Locale.getDefault());
        ref.child("ReclamaTimerDay").child(timeFormat.format(currentDate)).setValue(myKeyId);
        ref.child("ReclamaTimer").child(myKeyId).setValue(System.currentTimeMillis());
        ref.child("Coins").child(myKeyId).setValue(coins);
    }
    protected void adsRewardPressYes(){
        if (rewardedAd.isLoaded()) {
            Activity activityContext = getActivity();
            RewardedAdCallback adCallback = new RewardedAdCallback() {
                @Override
                public void onRewardedAdOpened() {
                    // Ad opened.
                }

                @Override
                public void onRewardedAdClosed() {
                    // Ad closed.

                    rewardedAd = createAndLoadRewardedAd();//Загружаю обьявление когда предыдущее закрывается
//                                    да,да, это тупо но прошлый файл уже не действительный, надо создавать новый.
                }

                @Override
                public void onUserEarnedReward(@NonNull RewardItem reward) {
                    rewardedItems(reward);
                }

                @Override
                public void onRewardedAdFailedToShow(AdError adError) {
                    // Ad failed to display.
                }
            };
            rewardedAd.show(activityContext, adCallback);
        } else {
            Toast.makeText(context, "Обьявление не загружено.", Toast.LENGTH_SHORT).show();
        }

    }

}
