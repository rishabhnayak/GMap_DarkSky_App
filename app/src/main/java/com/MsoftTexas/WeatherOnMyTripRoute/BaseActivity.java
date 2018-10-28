package com.MsoftTexas.WeatherOnMyTripRoute;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.MsoftTexas.WeatherOnMyTripRoute.util.IabBroadcastReceiver;
import com.MsoftTexas.WeatherOnMyTripRoute.util.IabHelper;
import com.MsoftTexas.WeatherOnMyTripRoute.util.IabResult;
import com.MsoftTexas.WeatherOnMyTripRoute.util.Inventory;
import com.MsoftTexas.WeatherOnMyTripRoute.util.Purchase;

import io.trialy.library.Trialy;
import io.trialy.library.TrialyCallback;

import static io.trialy.library.Constants.STATUS_TRIAL_JUST_ENDED;
import static io.trialy.library.Constants.STATUS_TRIAL_JUST_STARTED;
import static io.trialy.library.Constants.STATUS_TRIAL_NOT_YET_STARTED;
import static io.trialy.library.Constants.STATUS_TRIAL_OVER;
import static io.trialy.library.Constants.STATUS_TRIAL_RUNNING;

public class BaseActivity extends AppCompatActivity implements  IabBroadcastReceiver.IabBroadcastListener {
    static String TRIALY_APP_KEY = "CNXFXUSWNXNREPZN6FW"; //TODO: Replace with your app key, which can be found on your Trialy developer dashboard
    static String TRIALY_SKU = "t21_test"; //TODO: Replace with a trial SKU, which can be found on your Trialy developer dashboard. Each app can have multiple trials
    static String base64EncodedPublicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAnh6LDOmwwPSQ8KesBlRQ/LrN/75xUFQhVmvfJG6uUlmgxU4iWiMzwr1iydveIz3cNT2C1IdnBpohHuDhn9RlOn5uaR3Cw0BDGrnRzwHZRPdoJ3/tAWIS+cLD/5LU7sriMOi6spMaPTYjgrT/Lck36goPwY88FK+e2G09cFrd54WQBPwHO+COKlKOFQ7Yt9yiCLlwivhdSDbacuVGg696JjAeTBvnw0eqks7Q/FHg2U0TlhBf/RU2+tvCnR2L0hk1kgkkdZFua8aDrZ1xQkEkBzlrrHrGnmqCyVoPHwMcxoOKM61BX511NMRuBJv9Eg19n4QITqT/fsR7vzmnljjxLQIDAQAB" ;

    IabHelper mHelper;

    // Provides purchase notification while this app is running
    IabBroadcastReceiver mBroadcastReceiver;
    Trialy mTrialy;

    String SKU_INFINITE_GAS_MONTHLY = "infinite_gas_monthly";
    String SKU_INFINITE_GAS_QUATERLY = "quaterly";
    String SKU_INFINITE_GAS_HALFYEARLY = "halfyearly";
    String SKU_INFINITE_GAS_YEARLY = "infinite_gas_yearly";

    boolean mSubscribedToInfiniteGas = true;
    boolean mAutoRenewEnabled = false;
    static boolean havetrial=true;
    String mInfiniteGasSku = "";
    String TAG ="Base Activity";
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        Log.d(TAG, "Creating IAB helper.");
        mHelper = new IabHelper(this, base64EncodedPublicKey);

        // enable debug logging (for a production application, you should set this to false).
        mHelper.enableDebugLogging(true);

        // Start setup. This is asynchronous and the specified listener
        // will be called once setup completes.
        Log.d(TAG, "Starting setup.");
        mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
            public void onIabSetupFinished(IabResult result) {
                Log.d(TAG, "Setup finished.");

                if (!result.isSuccess()) {
                    // Oh noes, there was a problem.
                    complain("Problem setting up in-app billing: " + result);
                    return;
                }

                // Have we been disposed of in the meantime? If so, quit.
                if (mHelper == null) return;

                // Important: Dynamically register for broadcast messages about updated purchases.
                // We register the receiver here instead of as a <receiver> in the Manifest
                // because we always call getPurchases() at startup, so therefore we can ignore
                // any broadcasts sent while the app isn't running.
                // Note: registering this listener in an Activity is a bad idea, but is done here
                // because this is a SAMPLE. Regardless, the receiver must be registered after
                // IabHelper is setup, but before first call to getPurchases().
                mBroadcastReceiver = new IabBroadcastReceiver(BaseActivity.this);
                IntentFilter broadcastFilter = new IntentFilter(IabBroadcastReceiver.ACTION);
                registerReceiver(mBroadcastReceiver, broadcastFilter);

                // IAB is fully set up. Now, let's get an inventory of stuff we own.
                Log.d(TAG, "Setup successful. Querying inventory.");
                try {
                    mHelper.queryInventoryAsync(mGotInventoryListener);
                } catch (IabHelper.IabAsyncInProgressException e) {
                    complain("Error querying inventory. Another async operation in progress.");
                }
            }
        });
    }



    IabHelper.QueryInventoryFinishedListener mGotInventoryListener = new IabHelper.QueryInventoryFinishedListener() {
        public void onQueryInventoryFinished(IabResult result, Inventory inventory) {
            Log.d("TAG", "Query inventory finished.");

            // Have we been disposed of in the meantime? If so, quit.
            if (mHelper == null) return;

            // Is it a failure?
            if (result.isFailure()) {
                complain("Failed to query inventory: " + result);
                return;
            }

            Log.d("TAG", "Query inventory was successful.");

            /*
             * Check for items we own. Notice that for each purchase, we check
             * the developer payload to see if it's correct! See
             * verifyDeveloperPayload().
             */

            // Do we have the premium upgrade?
//            Purchase premiumPurchase = inventory.getPurchase(SKU_PREMIUM);
//            mIsPremium = (premiumPurchase != null && verifyDeveloperPayload(premiumPurchase));
//            Log.d(TAG, "User is " + (mIsPremium ? "PREMIUM" : "NOT PREMIUM"));

            // First find out which subscription is auto renewing
            Purchase gasMonthly = inventory.getPurchase(SKU_INFINITE_GAS_MONTHLY);
            Purchase gasQuaterly = inventory.getPurchase(SKU_INFINITE_GAS_QUATERLY);
            Purchase gasHalfYearly = inventory.getPurchase(SKU_INFINITE_GAS_HALFYEARLY);
            Purchase gasYearly = inventory.getPurchase(SKU_INFINITE_GAS_YEARLY);
            if (gasMonthly != null && gasMonthly.isAutoRenewing()) {
                mInfiniteGasSku = SKU_INFINITE_GAS_MONTHLY;
                mAutoRenewEnabled = true;
            }else if (gasQuaterly != null && gasQuaterly.isAutoRenewing()) {
                mInfiniteGasSku = SKU_INFINITE_GAS_QUATERLY;
                mAutoRenewEnabled = true;
            }else if (gasHalfYearly!= null && gasHalfYearly.isAutoRenewing()) {
                mInfiniteGasSku = SKU_INFINITE_GAS_HALFYEARLY;
                mAutoRenewEnabled = true;
            }
            else if (gasYearly != null && gasYearly.isAutoRenewing()) {
                mInfiniteGasSku = SKU_INFINITE_GAS_YEARLY;
                mAutoRenewEnabled = true;
            } else {
                mInfiniteGasSku = "";
                mAutoRenewEnabled = false;
            }

            // The user is subscribed if either subscription exists, even if neither is auto
            // renewing
            mSubscribedToInfiniteGas = (gasMonthly != null && verifyDeveloperPayload(gasMonthly))
                    || (gasQuaterly != null && verifyDeveloperPayload(gasQuaterly))
                    || (gasHalfYearly != null && verifyDeveloperPayload(gasHalfYearly))
                    || (gasYearly != null && verifyDeveloperPayload(gasYearly));
            Log.d("TAG", "User " + (mSubscribedToInfiniteGas ? "HAS" : "DOES NOT HAVE")
                    + " infinite gas subscription.");
            haveTrialorSubs();

        }
    };

    @Override
    public void receivedBroadcast() {
        // Received a broadcast notification that the inventory of items has changed
        Log.d("TAG", "Received broadcast notification. Querying inventory.");
        try {
            mHelper.queryInventoryAsync(mGotInventoryListener);
        } catch (IabHelper.IabAsyncInProgressException e) {
            complain("Error querying inventory. Another async operation in progress.");
        }
    }

    private TrialyCallback mTrialyCallback = new TrialyCallback() {
        @Override
        public void onResult(int status, long timeRemaining, String sku) {
            switch (status){
                case STATUS_TRIAL_JUST_STARTED:

                    break;
                case STATUS_TRIAL_RUNNING:

                    break;
                case STATUS_TRIAL_JUST_ENDED:
                    havetrial=false;
                    haveTrialorSubs();
                    break;
                case STATUS_TRIAL_NOT_YET_STARTED:
                    havetrial=false;
                    haveTrialorSubs();
                    break;
                case STATUS_TRIAL_OVER:
                    havetrial=false;
                    haveTrialorSubs();
                    break;
            }
            Log.i("TRIALY", "Returned status: " + Trialy.getStatusMessage(status));
        }

    };

    void complain(String message) {
        Log.e("TAG", "**** TrivialDrive Error: " + message);
        alert("Error: " + message);
    }

    void alert(String message) {
        android.app.AlertDialog.Builder bld = new android.app.AlertDialog.Builder(this);
        bld.setMessage(message);
        bld.setNeutralButton("OK", null);
        Log.d("TAG", "Showing alert dialog: " + message);
        bld.create().show();


    }

    boolean verifyDeveloperPayload(Purchase p) {
        String payload = p.getDeveloperPayload();
        return true;
    }

    void haveTrialorSubs(){
        if(!havetrial && !mSubscribedToInfiniteGas) {
            startActivity(new Intent(BaseActivity.this, Subscription.class));
            finish();
        }
    }
}
