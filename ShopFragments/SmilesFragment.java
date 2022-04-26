package ru.tmkstd.cardgamedurakonline.ShopFragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import ru.tmkstd.cardgamedurakonline.R;


public class SmilesFragment extends Fragment {
    ImageView aa,ab,ac,ad,ae,af;
    ImageView ba,bb,bc,bd,be,bf;
    ImageView ca,cb,cc,cd,ce,cf;

    TextView[] price;
    TextView[] buy;
    ImageView[] coinImage;
    TextView[] countsSmiles;
    Context context;
    long statusEmojis;
    long coins;
    String myKeyId;

    DatabaseReference ref;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_shop_smiles, container, false);
//        listView = rootView.findViewById(R.id.listView);
//        ArrayAdapter<String> myAdapter = new MyAdapter(context,friendsUsers);
//        listView.setAdapter(myAdapter);
//        containerSmiles = rootView.findViewById(R.id.container_shop_smiles);
        aa = rootView.findViewById(R.id.aa);
        ab = rootView.findViewById(R.id.ab);
        ac = rootView.findViewById(R.id.ac);
        ad = rootView.findViewById(R.id.ad);
        ae = rootView.findViewById(R.id.ae);
        af = rootView.findViewById(R.id.af);
        ba = rootView.findViewById(R.id.ba);
        bb = rootView.findViewById(R.id.bb);
        bc = rootView.findViewById(R.id.bc);
        bd = rootView.findViewById(R.id.bd);
        be = rootView.findViewById(R.id.be);
        bf = rootView.findViewById(R.id.bf);
        ca = rootView.findViewById(R.id.ca);
        cb = rootView.findViewById(R.id.cb);
        cc = rootView.findViewById(R.id.cc);
        cd = rootView.findViewById(R.id.cd);
        ce = rootView.findViewById(R.id.ce);
        cf = rootView.findViewById(R.id.cf);
        price[0] = rootView.findViewById(R.id.price0);
        price[1] = rootView.findViewById(R.id.price1);
        price[2] = rootView.findViewById(R.id.price2);
        price[3] = rootView.findViewById(R.id.price3);
        price[4] = rootView.findViewById(R.id.price4);
        price[5] = rootView.findViewById(R.id.price5);
        price[6] = rootView.findViewById(R.id.price6);
        price[7] = rootView.findViewById(R.id.price7);
        price[8] = rootView.findViewById(R.id.price8);
        price[9] = rootView.findViewById(R.id.price9);
        price[10] = rootView.findViewById(R.id.price10);
        price[11] = rootView.findViewById(R.id.price11);
        price[12] = rootView.findViewById(R.id.price12);
        price[13] = rootView.findViewById(R.id.price13);
        price[14] = rootView.findViewById(R.id.price14);
        price[15] = rootView.findViewById(R.id.price15);
        price[16] = rootView.findViewById(R.id.price16);
        price[17] = rootView.findViewById(R.id.price17);
        buy[0] = rootView.findViewById(R.id.buy0);
        buy[1] = rootView.findViewById(R.id.buy1);
        buy[2] = rootView.findViewById(R.id.buy2);
        buy[3] = rootView.findViewById(R.id.buy3);
        buy[4] = rootView.findViewById(R.id.buy4);
        buy[5] = rootView.findViewById(R.id.buy5);
        buy[6] = rootView.findViewById(R.id.buy6);
        buy[7] = rootView.findViewById(R.id.buy7);
        buy[8] = rootView.findViewById(R.id.buy8);
        buy[9] = rootView.findViewById(R.id.buy9);
        buy[10] = rootView.findViewById(R.id.buy10);
        buy[11] = rootView.findViewById(R.id.buy11);
        buy[12] = rootView.findViewById(R.id.buy12);
        buy[13] = rootView.findViewById(R.id.buy13);
        buy[14] = rootView.findViewById(R.id.buy14);
        buy[15] = rootView.findViewById(R.id.buy15);
        buy[16] = rootView.findViewById(R.id.buy16);
        buy[17] = rootView.findViewById(R.id.buy17);
        coinImage[0] = rootView.findViewById(R.id.coinImage0);
        coinImage[1] = rootView.findViewById(R.id.coinImage1);
        coinImage[2] = rootView.findViewById(R.id.coinImage2);
        coinImage[3] = rootView.findViewById(R.id.coinImage3);
        coinImage[4] = rootView.findViewById(R.id.coinImage4);
        coinImage[5] = rootView.findViewById(R.id.coinImage5);
        coinImage[6] = rootView.findViewById(R.id.coinImage6);
        coinImage[7] = rootView.findViewById(R.id.coinImage7);
        coinImage[8] = rootView.findViewById(R.id.coinImage8);
        coinImage[9] = rootView.findViewById(R.id.coinImage9);
        coinImage[10] = rootView.findViewById(R.id.coinImage10);
        coinImage[11] = rootView.findViewById(R.id.coinImage11);
        coinImage[12] = rootView.findViewById(R.id.coinImage12);
        coinImage[13] = rootView.findViewById(R.id.coinImage13);
        coinImage[14] = rootView.findViewById(R.id.coinImage14);
        coinImage[15] = rootView.findViewById(R.id.coinImage15);
        coinImage[16] = rootView.findViewById(R.id.coinImage16);
        coinImage[17] = rootView.findViewById(R.id.coinImage17);
        countsSmiles[0] = rootView.findViewById(R.id.count_aa);
        countsSmiles[1] = rootView.findViewById(R.id.count_ba);
        countsSmiles[2] = rootView.findViewById(R.id.count_ca);
        ref.child("Smiles").child(myKeyId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue(Long.class)!=null){
                    statusEmojis = dataSnapshot.getValue(Long.class);
                }
                else{
                    statusEmojis = 0;
                }
                raspredelenie();
                setOnClickList();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        return rootView;
    }

    public SmilesFragment(Context context,String myKey, DatabaseReference ref) {
        this.context = context;
        myKeyId = myKey;
        statusEmojis = -1;
        price = new TextView[18];
        buy = new TextView[18];
        coinImage = new ImageView[18];
        countsSmiles = new TextView[3];
        this.ref = ref;
        coins = -1L;//проверкатка на загрузку данных

    }

    public void setCoins(long coin){
        coins = coin;
    }
    private void raspredelenie() {
        int count = 0;
        for(int i = 0; i < price.length;i++){
            if(i%6==0){
                count = 0;
            }
            if((statusEmojis&1<<i) == 1<<i) {
                count++;
                price[i].setVisibility(View.INVISIBLE);
                coinImage[i].setVisibility(View.INVISIBLE);
                buy[i].setVisibility(View.VISIBLE);
            }
            if(i%6==5){
                try {
                    countsSmiles[i / 6].setText(count + "/6");
                } catch (Exception e){
                    ref.child("Exception").child(System.currentTimeMillis()+"").setValue("Ошибка в countSmiles[i/6].setText(count + /6");
                }
            }
        }
    }
    private void setOnClickList() {
        aa.setOnClickListener(v ->onClickImage(0));
        ab.setOnClickListener(v ->onClickImage(1));
        ac.setOnClickListener(v ->onClickImage(2));
        ad.setOnClickListener(v ->onClickImage(3));
        ae.setOnClickListener(v ->onClickImage(4));
        af.setOnClickListener(v ->onClickImage(5));
        ba.setOnClickListener(v ->onClickImage(6));
        bb.setOnClickListener(v ->onClickImage(7));
        bc.setOnClickListener(v ->onClickImage(8));
        bd.setOnClickListener(v ->onClickImage(9));
        be.setOnClickListener(v ->onClickImage(10));
        bf.setOnClickListener(v ->onClickImage(11));
        ca.setOnClickListener(v ->onClickImage(12));
        cb.setOnClickListener(v ->onClickImage(13));
        cc.setOnClickListener(v ->onClickImage(14));
        cd.setOnClickListener(v ->onClickImage(15));
        ce.setOnClickListener(v ->onClickImage(16));
        cf.setOnClickListener(v ->onClickImage(17));
    }

    private void onClickImage(int index){
        if(statusEmojis == -1) return;
        if((statusEmojis&1<<index) == 1<<index) {
            Log.d("statusEmojis","Уже куплено");
        } else {
            Log.d("statusEmojis","Не куплено");
            AlertDialog.Builder quitDialog = new AlertDialog.Builder(context);
            quitDialog.setTitle("Купить за "+price[index].getText().toString()+" монет?");
            quitDialog.setPositiveButton("Купить", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if(myKeyId!=null){
                        long priceTemp = Long.valueOf(price[index].getText().toString());
                        if(priceTemp<coins) {
                            statusEmojis += (long) Math.pow(2, index);
                            raspredelenie();
                            ref.child("Coins").child(myKeyId).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    long temp = dataSnapshot.getValue(Long.class);
                                    ref.child("Coins").child(myKeyId).setValue(temp - priceTemp);
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                            ref.child("Smiles").child(myKeyId).setValue(statusEmojis);
                        } else {
                            Toast.makeText(context,"Недостаточно средств", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });
            quitDialog.setNegativeButton("Отмена",new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            quitDialog.show();

        }

    }
}
