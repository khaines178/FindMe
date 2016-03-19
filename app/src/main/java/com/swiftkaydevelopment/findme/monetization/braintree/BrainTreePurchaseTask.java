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
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;

import com.swiftkaydevelopment.findme.monetization.PurchaseClient;

import java.lang.ref.WeakReference;

/**
 * Created by Kevin Haines on 3/9/16.
 * Class Overview:
 */
public class BrainTreePurchaseTask {
    private static final String TAG = "brntreepurchtask";

    private final BrainTreeProduct mProduct;
    private PurchaseClient mClient;
    private WeakReference<Activity> mActivityRef;

    private static final String MOCK_TOKEN = "eyJ2ZXJzaW9uIjoyLCJhdXRob3JpemF0aW9uRmluZ2VycHJpbnQiOiI1NWZkZWY3NjZlOWUzNmRmNWI1N2I0OTk2MzkwYjQ1MjFkMWQ1OTA" +
            "5MDg5OTY1ZmQwMzU0OWQ0OTNhZGE5OWJhfGNyZWF0ZWRfYXQ9MjAxNi0wMy0wOVQxNjoxMTozNS40NTEyNzU0NDcrMDAwMFx1MDAyNm1lcmNoYW50X2lkPTM0OHBrOWNnZj" +
            "NiZ3l3MmJcdTAwMjZwdWJsaWNfa2V5PTJuMjQ3ZHY4OWJxOXZtcHIiLCJjb25maWdVcmwiOiJodHRwczovL2FwaS5zYW5kYm94LmJyYWludHJlZWdhdGV3YXkuY29tOjQ0My9tZX" +
            "JjaGFudHMvMzQ4cGs5Y2dmM2JneXcyYi9jbGllbnRfYXBpL3YxL2NvbmZpZ3VyYXRpb24iLCJjaGFsbGVuZ2VzIjpbXSwiZW52aXJvbm1lbnQiOiJzYW5kYm94IiwiY2xpZW50QXBpVXJ" +
            "sIjoiaHR0cHM6Ly9hcGkuc2FuZGJveC5icmFpbnRyZWVnYXRld2F5LmNvbTo0NDMvbWVyY2hhbnRzLzM0OHBrOWNnZjNiZ3l3MmIvY2xpZW50X2FwaSIsImFzc2V0c1VybCI6Imh0dHBzOi8v" +
            "YXNzZXRzLmJyYWludHJlZWdhdGV3YXkuY29tIiwiYXV0aFVybCI6Imh0dHBzOi8vYXV0aC52ZW5tby5zYW5kYm94LmJyYWludHJlZWdhdGV3YXkuY29tIiwiYW5hbHl0aWNzIjp7InVybCI6Imh0d" +
            "HBzOi8vY2xpZW50LWFuYWx5dGljcy5zYW5kYm94LmJyYWludHJlZWdhdGV3YXkuY29tIn0sInRocmVlRFNlY3VyZUVuYWJsZWQiOnRydWUsInBheXBhbEVuYWJsZWQiOnRydWUsInBheXBhbCI6eyJkaXN" +
            "wbGF5TmFtZSI6IkFjbWUgV2lkZ2V0cywgTHRkLiAoU2FuZGJveCkiLCJjbGllbnRJZCI6bnVsbCwicHJpdmFjeVVybCI6Imh0dHA6Ly9leGFtcGxlLmNvbS9wcCIsInVzZXJBZ3JlZW1lbnRVcmwiOiJod" +
            "HRwOi8vZXhhbXBsZS5jb20vdG9zIiwiYmFzZVVybCI6Imh0dHBzOi8vYXNzZXRzLmJyYWludHJlZWdhdGV3YXkuY29tIiwiYXNzZXRzVXJsIjoiaHR0cHM6Ly9jaGVja291dC5wYXlwYWwuY29tIiwi" +
            "ZGlyZWN0QmFzZVVybCI6bnVsbCwiYWxsb3dIdHRwIjp0cnVlLCJlbnZpcm9ubWVudE5vTmV0d29yayI6dHJ1ZSwiZW52aXJvbm1lbnQiOiJvZmZsaW5lIiwidW52ZXR0ZWRNZXJjaGFudCI6ZmFsc2Us" +
            "ImJyYWludHJlZUNsaWVudElkIjoibWFzdGVyY2xpZW50MyIsImJpbGxpbmdBZ3JlZW1lbnRzRW5hYmxlZCI6dHJ1ZSwibWVyY2hhbnRBY2NvdW50SWQiOiJhY21ld2lkZ2V0c2x0ZHNhbmRib3giLCJj" +
            "dXJyZW5jeUlzb0NvZGUiOiJVU0QifSwiY29pbmJhc2VFbmFibGVkIjpmYWxzZSwibWVyY2hhbnRJZCI6IjM0OHBrOWNnZjNiZ3l3MmIiLCJ2ZW5tbyI6Im9mZiJ9";

    private BrainTreePurchaseTask(BrainTreeProduct product) {
        this.mProduct = product;
    }

    public static BrainTreePurchaseTask init(BrainTreeProduct product) {
        return new BrainTreePurchaseTask(product);
    }

    public BrainTreePurchaseTask purchase(PurchaseClient client, Activity activity) {
        mClient = client;
        mActivityRef = new WeakReference<Activity>(activity);
        getClientToken();
        return this;
    }

    /**
     * This call finalizes the purchase by sending the required information to
     * the server so the server can send it off to Braintree to process the
     * transaction.
     *
     * @param username User's account
     * @param nonce payment nonce generated by Braintree
     */
    public void processTransaction(String username, String nonce) {
        new SubmitTransactionToServerTask(mProduct, username, nonce).execute();
    }

    /**
     * Gets the token required to make the purchase with. This token is a unique
     * String identifier generated by our server
     *
     */
    private void getClientToken() {
        new GetClientTokenTask().execute();
    }

    /**
     * AsyncTask to fetch the Client Token String from the server
     *
     */
    private class GetClientTokenTask extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... params) {
            //Connect to server and get a unique client
            //token for this transaction
            return MOCK_TOKEN;
        }

        @Override
        protected void onPostExecute(String token) {
            super.onPostExecute(token);
            processTokenRecieved(token);
        }
    }

    /**
     * Processes the response from our server after getting the unique
     * client token String for this purchase
     *
     * @param clientToken Unique client token String
     */
    private void processTokenRecieved(String clientToken) {
        if (TextUtils.isEmpty(clientToken) || mActivityRef == null || mActivityRef.get() == null) {
            Log.e(TAG, "Cannot process purchase");
            mClient.onPurchaseComplete(mProduct);
            return;
        }

//        PaymentRequest paymentRequest = new PaymentRequestest()
//                .clientToken(clientToken);
//        mActivityRef.get().startActivityForResult(paymentRequest.getIntent(mActivityRef.get()), BrainTreePurchaseManager.REQUEST_CODE_BRAINTREE_PURCHASE);
    }

    /**
     * Class to Asyncronously send the user's payment nonce, product being purchased, and the user's
     * account to the server to process payment for a product
     *
     */
    private class SubmitTransactionToServerTask extends AsyncTask<Void, Void, Void> {
        private final BrainTreeProduct mProduct;
        private final String mUsername;
        private final String mNonce;

        public SubmitTransactionToServerTask(BrainTreeProduct product, String username, String nonce) {
            this.mProduct = product;
            this.mUsername = username;
            this.mNonce = nonce;
        }

        @Override
        protected Void doInBackground(Void... params) {
            //todo: this is where we send the nonce, user, and product up to the server
            //we will send the product.mProduct or product.mProductGroup depending on
            //if the user is buying a single product or the whole product group.
            //the server will handle transaction success and return that value to us.
            return null;
        }
    }
}
