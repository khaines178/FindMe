/*
 *      Copyright (C) 2015 Kevin Haines
 *
 *     Licensed under the Apache License, Version 2.0 (the "License");
 *     you may not use this file except in compliance with the License.
 *      You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *     Unless required by applicable law or agreed to in writing, software
 *     distributed under the License is distributed on an "AS IS" BASIS,
 *     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *      See the License for the specific language governing permissions and
 *     limitations under the License.
 *
 */

package com.swiftkaydevelopment.findme.monetization.braintree;

import android.app.Activity;
import android.util.Log;

import com.swiftkaydevelopment.findme.monetization.PurchaseClient;
import com.swiftkaydevelopment.findme.monetization.PurchaseFlow;


/**
 * Created by Kevin Haines on 3/9/16.
 * Class Overview:
 */
public class BrainTreePurchaseManager implements PurchaseClient<BrainTreeProduct> {
    private static final String TAG = "braintreeprchmangr";

    public static final int REQUEST_CODE_BRAINTREE_PURCHASE = 5565;
    private PurchaseFlow mPurchaseFlow;

    private BrainTreePurchaseTask mCurrentTask;

    private static BrainTreePurchaseManager sInstance = null;

    private BrainTreePurchaseManager(){}

    public static BrainTreePurchaseManager instance() {
        synchronized (BrainTreePurchaseManager.class) {
            if (sInstance == null) {
                sInstance = new BrainTreePurchaseManager();
            }
        }
        return sInstance;
    }

    @Override
    public void makePurchase(Activity activity, BrainTreeProduct product) {
        if (activity instanceof PurchaseFlow) {
            mPurchaseFlow = (PurchaseFlow) activity;
        }
        if (mCurrentTask == null) {
            mCurrentTask = BrainTreePurchaseTask.init(product).purchase(this, activity);
        } else {
            Log.w(TAG, "can only process one purchase at a time");
        }
    }

    @Override
    public void onPurchaseComplete(BrainTreeProduct product) {
        clearCurrentPurchase();
        if (product.isPurchased) {
            mPurchaseFlow.onPurchaseSuccessful();
        } else {
            mPurchaseFlow.onPurchaseFailed();
        }
    }

    /**
     * Clears the current task to make available for
     * a new purchase task.
     *
     */
    public void clearCurrentPurchase() {
        mCurrentTask = null;
        System.gc(); // for added security we request gc to clear any transaction info still in memory
    }

    /**
     * Submits the nonce and user to the BrainTreePurchaseTask to send
     * to the server in order to process the transaction
     *
     * @param username User's account
     * @param nonce payment nonce generated by BrainTree
     */
    public void submitTransactionToServer(String username, String nonce) {
        if (mCurrentTask != null) {
            mCurrentTask.processTransaction(username, nonce);
        } else {
            mPurchaseFlow.onPurchaseFailed();
        }
    }
}
