package ru.tmkstd.cardgamedurakonline.ShopFragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import androidx.fragment.app.Fragment;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.SkuDetails;

import java.util.HashMap;
import java.util.List;

import ru.tmkstd.cardgamedurakonline.R;

public class DonutsFragment extends Fragment {

    LinearLayout containerDonuts, empty;
    Button[] myBuyButton;
    ProgressBar[] myBuyCycles;
    private BillingClient mBillingClient;
    private HashMap<String, SkuDetails> mSkuDetailsMap = new HashMap<>();
    String[] mSkusId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_shop_donuts, container, false);
        containerDonuts = rootView.findViewById(R.id.container_shop_donuts);

        myBuyButton[0] = rootView.findViewById(R.id.buttonDonut0);
        myBuyButton[1] = rootView.findViewById(R.id.buttonDonut1);
        myBuyButton[2] = rootView.findViewById(R.id.buttonDonut2);
        myBuyCycles[0] = rootView.findViewById(R.id.cycleDonut0);
        myBuyCycles[1] = rootView.findViewById(R.id.cycleDonut1);
        myBuyCycles[2] = rootView.findViewById(R.id.cycleDonut2);
        empty = rootView.findViewById(R.id.container_shop_donuts_empty);
        return rootView;
    }

    public DonutsFragment() {

        myBuyButton = new Button[3];
        myBuyCycles = new ProgressBar[3];
        mSkusId = new String[3];
        mSkusId[0] = "donut1";
        mSkusId[1] = "donut2";
        mSkusId[2] = "coffee1";
    }
    public void lookPurs(BillingClient billingClient, List<SkuDetails> skuDetailsList){
        mBillingClient = billingClient;
        for(SkuDetails skuDetails:skuDetailsList){
            mSkuDetailsMap.put(skuDetails.getSku(),skuDetails);
        }
        for(int i = 0; i < myBuyButton.length; i++) {
            myBuyButton[i].setText(mSkuDetailsMap.get(mSkusId[i]).getPrice());
        }
        if(myBuyButton[0]!=null) {
            myBuyButton[0].setOnClickListener(v -> launchBilling(mSkusId[0]));
            myBuyButton[1].setOnClickListener(v -> launchBilling(mSkusId[1]));
            myBuyButton[2].setOnClickListener(v -> launchBilling(mSkusId[2]));
            containerDonuts.setVisibility(View.VISIBLE);
            empty.setVisibility(View.GONE);
        }
    }
    public void pending(String SKU) {
        for (int i = 0;i<mSkusId.length;i++){
            if(mSkusId[i].equals(SKU)){
                myBuyButton[i].setVisibility(View.GONE);
                myBuyCycles[i].setVisibility(View.VISIBLE);
            }
        }
    }

    public void consume(String SKU) {
        for (int i = 0;i<mSkusId.length;i++){
            if(mSkusId[i].equals(SKU)){
                myBuyButton[i].setVisibility(View.VISIBLE);
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
}
