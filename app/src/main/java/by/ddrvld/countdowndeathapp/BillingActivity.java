package by.ddrvld.countdowndeathapp;

import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.SkuDetails;
import com.android.billingclient.api.SkuDetailsParams;
import com.android.billingclient.api.SkuDetailsResponseListener;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BillingActivity extends AppCompatActivity {

    private BillingClient mBillingClient;
    private Map<String, SkuDetails> mSkuDetailsMap = new HashMap<>();

    private final static String CHANGE_YOUR_FATE = "change_your_fate";
    private final static String DISABLE_ADS = "disable_ads";
    private final static String GPLAY_LICENSE = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAlh3IXfvwhrH43ZO3anu7x7mbf3oT9JqAOD+3bTKocpYtvBexKwCiKhv9CrhAkZNaY48sfM80PtnVFAlqljPAcj9UthtHR94YCOSWL/F1SJB8FWxGa94d/JHc4ivuOLw0aNkoh6EdJX+0MH61FFI444bwmMYSKEjZLCkVcoddxq0CMdFcZTb3j4UsWhpgf2OMDLvEPn+qKqYVtrdKnoMd/vK9RTcC6iHvNNssAtBbQUEiA2SPA45shVgxK/jfxshNt96/jzhQUyvGiwYgwOVWrd6gXqkj5oiafzDGZkc6QTknMU2fYovy5FI1h8rKj3PfXaxR1sG5l+CavTj2s1F+QwIDAQAB";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mBillingClient = BillingClient.newBuilder(this).enablePendingPurchases().setListener(new PurchasesUpdatedListener() {
            @Override
            public void onPurchasesUpdated(BillingResult billingResult, List<Purchase> purchases) {
                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK && purchases != null) {
//                    for (Purchase purchase : purchases) {
//                        handlePurchase(purchase);
//                    }
                    // Perform your Successful Purchase Task here

//                    Snackbar.make(findViewById(android.R.id.content), "Great!! Purchase Flow Successful! :)", Snackbar.LENGTH_LONG).show();
//                    dismiss();
                } else if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.USER_CANCELED) {
                    // Handle an error caused by a user cancelling the purchase flow.
//                    Snackbar.make(findViewById(android.R.id.content), "User Cancelled the Purchase Flow!", Snackbar.LENGTH_LONG).show();
                } else {
                    // Handle any other error codes.
//                    Snackbar.make(findViewById(android.R.id.content), "Error! Purchase Task was not performed!", Snackbar.LENGTH_LONG).show();
                }
            }
        }).build();

        mBillingClient.startConnection(new BillingClientStateListener() {

            @Override
            public void onBillingSetupFinished(@NonNull BillingResult billingResult) {

                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                    // The BillingClient is ready. You can query purchases here.

                    List<String> skuList = new ArrayList<>();
                    skuList.add(CHANGE_YOUR_FATE);
                    skuList.add(DISABLE_ADS);
                    SkuDetailsParams.Builder params = SkuDetailsParams.newBuilder();
                    params.setSkusList(skuList).setType(BillingClient.SkuType.INAPP);
                    mBillingClient.querySkuDetailsAsync(params.build(),
                            new SkuDetailsResponseListener() {
                                @Override
                                public void onSkuDetailsResponse(BillingResult billingResult,
                                                                 List<SkuDetails> skuDetailsList) {
                                    // Process the result.
                                    // Retrieve a value for "skuDetails" by calling querySkuDetailsAsync().
                                    BillingFlowParams billingFlowParams = BillingFlowParams.newBuilder()
                                            .setSkuDetails(skuDetailsList.get(0))
                                            .build();
                                    int responseCode = mBillingClient.launchBillingFlow(BillingActivity.this, billingFlowParams).getResponseCode();

                                    if (responseCode == 0) {
                                        for (SkuDetails skuDetails : skuDetailsList) {
                                            mSkuDetailsMap.put(skuDetails.getSku(), skuDetails);
                                        }
                                    }
                                }
                            });
                }
            }

            @Override
            public void onBillingServiceDisconnected() {
                // Try to restart the connection on the next request to Google Play by calling the startConnection() method.
                Toast.makeText(BillingActivity.this, "Service Disconnected!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void launchBilling(String skuId) {
        BillingFlowParams billingFlowParams = BillingFlowParams.newBuilder()
                .setSkuDetails(mSkuDetailsMap.get(0))
                .build();
        mBillingClient.launchBillingFlow(this, billingFlowParams);
    }
//
//    void handlePurchase(Purchase purchase) {
//        ConsumeParams consumeParams =
//                ConsumeParams.newBuilder()
//                        .setPurchaseToken(purchase.getPurchaseToken())
//                        .build();
//
//        ConsumeResponseListener listener = new ConsumeResponseListener() {
//            @Override
//            public void onConsumeResponse(BillingResult billingResult, @NonNull String purchaseToken) {
//                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
//                    // Handle the success of the consume operation.
//                }
//            }
//        };
//        mBillingClient.consumeAsync(consumeParams, listener);
//    }
}
