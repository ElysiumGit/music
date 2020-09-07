package com.kabouzeid.gramophone.ui.activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.anjlab.android.iab.v3.BillingProcessor;
import com.anjlab.android.iab.v3.TransactionDetails;
import com.kabouzeid.appthemehelper.color.MaterialColor;
import com.kabouzeid.gramophone.App;
import com.kabouzeid.gramophone.R;
import com.kabouzeid.gramophone.ui.activities.base.AbsBaseActivity;
import com.sofakingforever.stars.AnimatedStarsView;

import java.lang.ref.WeakReference;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.view.View.GONE;

public class PurchaseActivity extends AbsBaseActivity implements BillingProcessor.IBillingHandler {

    public static final String TAG = PurchaseActivity.class.getSimpleName();

    private static final int ACTIVITY_COLOR = MaterialColor.Green._500.getAsColor();

    @BindView(R.id.restore_button)
    TextView restoreButton;

    @BindView(R.id.purchase_button)
    ConstraintLayout purchaseButton;

    @BindView(R.id.stars)
    AnimatedStarsView stars;

    @BindView(R.id.close_button)
    ImageView closeButton;

    @BindView(R.id.purchase_text)
    TextView purchaseText;

    @BindView(R.id.purchase_layout)
    ConstraintLayout purchaseLayout;

    @BindView(R.id.purchased_layout)
    ConstraintLayout purchasedLayout;

    private BillingProcessor billingProcessor;
    private AsyncTask restorePurchaseAsyncTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pro);
        setDrawUnderStatusbar();
        ButterKnife.bind(this);

        setStatusbarColor(ACTIVITY_COLOR);
        setNavigationbarColor(ACTIVITY_COLOR);
        setTaskDescriptionColor(ACTIVITY_COLOR);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow();
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }

        restoreButton.setEnabled(false);
        purchaseButton.setEnabled(false);

        restoreButton.setOnClickListener(v -> {
            if (restorePurchaseAsyncTask == null || restorePurchaseAsyncTask.getStatus() != AsyncTask.Status.RUNNING) {
                restorePurchase();
            }
        });

        purchaseButton.setOnClickListener(v -> billingProcessor.purchase(PurchaseActivity.this, App.PRO_VERSION_PRODUCT_ID));

        closeButton.setOnClickListener(v -> onBackPressed());

        billingProcessor = new BillingProcessor(this, App.GOOGLE_PLAY_LICENSE_KEY, this);

        if(App.isProVersion()){
            purchaseLayout.setVisibility(GONE);
            purchasedLayout.setVisibility(View.VISIBLE);
        }else{
            purchaseLayout.setVisibility(View.VISIBLE);
            purchasedLayout.setVisibility(GONE);
        }
    }

    private void restorePurchase() {
        if (restorePurchaseAsyncTask != null) {
            restorePurchaseAsyncTask.cancel(false);
        }
        restorePurchaseAsyncTask = new RestorePurchaseAsyncTask(this).execute();
    }

    @Override
    public void onProductPurchased(@NonNull String productId, @Nullable TransactionDetails details) {
        Toast.makeText(this, R.string.thank_you, Toast.LENGTH_SHORT).show();
        App.notifyProVersionChanged();
        purchaseText.setText("Purchased");
    }

    @Override
    public void onPurchaseHistoryRestored() {
        if (App.isProVersion()) {
            Toast.makeText(this, R.string.restored_previous_purchase_please_restart, Toast.LENGTH_LONG).show();
            App.notifyProVersionChanged();
            purchaseText.setText("Purchased");
        } else {
            Toast.makeText(this, R.string.no_purchase_found, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBillingError(int errorCode, @Nullable Throwable error) {
        Log.e(TAG, "Billing error: code = " + errorCode, error);
    }

    @Override
    public void onBillingInitialized() {
        restoreButton.setEnabled(true);
        purchaseButton.setEnabled(true);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (!billingProcessor.handleActivityResult(requestCode, resultCode, data)) {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDestroy() {
        if (billingProcessor != null) {
            billingProcessor.release();
        }
        super.onDestroy();
    }

    private static class RestorePurchaseAsyncTask extends AsyncTask<Void, Void, Boolean> {
        private final WeakReference<PurchaseActivity> buyActivityWeakReference;

        public RestorePurchaseAsyncTask(PurchaseActivity purchaseActivity) {
            this.buyActivityWeakReference = new WeakReference<>(purchaseActivity);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            PurchaseActivity purchaseActivity = buyActivityWeakReference.get();
            if (purchaseActivity != null) {
                Toast.makeText(purchaseActivity, R.string.restoring_purchase, Toast.LENGTH_SHORT).show();
            } else {
                cancel(false);
            }
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            PurchaseActivity purchaseActivity = buyActivityWeakReference.get();
            if (purchaseActivity != null) {
                return purchaseActivity.billingProcessor.loadOwnedPurchasesFromGoogle();
            }
            cancel(false);
            return null;
        }

        @Override
        protected void onPostExecute(Boolean b) {
            super.onPostExecute(b);
            PurchaseActivity purchaseActivity = buyActivityWeakReference.get();
            if (purchaseActivity == null || b == null) return;

            if (b) {
                purchaseActivity.onPurchaseHistoryRestored();
            } else {
                Toast.makeText(purchaseActivity, R.string.could_not_restore_purchase, Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        stars.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        stars.onStop();
    }
}
