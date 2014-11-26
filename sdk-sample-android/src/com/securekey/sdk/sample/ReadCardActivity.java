/* 
* Copyright (c) 2014 SecureKey Technologies Inc. All rights reserved.
* 
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

package com.securekey.sdk.sample;

import org.json.me.JSONArray;
import org.json.me.JSONException;
import org.json.me.JSONObject;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NfcAdapter;
import android.nfc.tech.NfcA;
import android.nfc.tech.NfcB;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import com.securekey.briidge.Briidge;
import com.securekey.briidge.Briidge.CardReadTransaction;
import com.securekey.sdk.sample.R;
import com.securekey.sdk.sample.rp.SampleRPSessionFactory;

public class ReadCardActivity extends Activity implements Briidge.CardReadListener {
	
	/**
	* The object used to perform the card read and continue the API call session.
	* 
	* It has the methods:
	* 
	*  <ul>
	*        <li>{@link CardReadTransaction#isNFCIntent()}</li>
	*        <li>{@link CardReadTransaction#readCard(Intent)}</li>
	*        <li>{@link CardReadTransaction#cancelCardRead()}</li>
	* </ul>
	* {@link CardReadTransaction}
	*/
	private CardReadTransaction cardReadTransaction;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_readcard);
		
		BriidgePlatformFactory.getBriidgePlatform(ReadCardActivity.this).prepareToReadCard(null, ReadCardActivity.this);
		
	}

	/**
	 * This method is called when the call to {@link Briidge#prepareToReadCard(CardReadListener)} API completes.
	 * @param status			The status of the request.
	 * @param transactionId		The transaction id used by RP to make a server-server call to retrieve result.
	 */	
	@Override
	public void cardReadComplete(int status, String txnId) {
		Log.i("SDKSample", "card read complete: " + (status == Briidge.STATUS_OK? "OK" : "ERROR( " + status + " )") + " txnId: " + txnId);
		
		if(status == Briidge.STATUS_OK) {
			JSONObject response = SampleRPSessionFactory.createSampleRPSession(SampleRPSessionFactory.URL_GetCardReadData).getCardReadData(txnId);
			showDialog( processCardDataResponse(response));
		} else {
			showDialog("card read complete: " + "ERROR( " + status + " )");
		}
	}


	/**
	* This method is called during executing of {@link Briidge#prepareToReadCard(CardReadListener)} method as status of card reading changes.
	* 
	* Status values are:
	* <ul>
	*        <li><tt>STATUS_CARD_READ_READING_DONE</tt> once card data was read successfully. API isn't done yet, the data is being sent.</li>
	*        <li><tt>STATUS_CARD_READ_ERROR</tt> if reading card failed. Can call {@link Briidge#prepareToReadCard(CardReadListener)} again to retry.</li>
	*        <li><tt>STATUS_CARD_READ_NON_NFC_INTENT</tt> if the intent passed to {@link Briidge#prepareToReadCard(CardReadListener)} is not an NFC intent. Can call {@link Briidge#prepareToReadCard(CardReadListener)} again to retry.</li>
	* </ul>
	*/
	@Override
	public void cardReadStatus(int status) {
		
		String msg = "Initializing...";
		switch (status) {
		case Briidge.STATUS_CARD_READ_ERROR:
			Log.i("SDKSample", "Reading card status STATUS_CARD_READ_ERROR");
			msg = "Error, try again.";
			break;
			
		case Briidge.STATUS_CARD_READ_NON_NFC_INTENT:
			Log.i("SDKSample", "Reading card status STATUS_CARD_READ_NON_NFC_INTENT");
			msg = "Error, try again.";
			break;
			
		case Briidge.STATUS_CARD_READ_READING_DONE:
			Log.i("SDKSample", "Reading card status STATUS_CARD_READ_READING_DONE");
			msg = "Success!";
			break;

		default:
			break;
		}
		
		final String uiMsg = msg;
		runOnUiThread(new Runnable() {
		
			@Override
			public void run() {
				((TextView) findViewById(R.id.cardReadMsg)).setText(uiMsg);
			}
		});
	}


	/**
	 * This method is called once the API reaches the stage where the card data has to be read. It is called after the initialization with {@link Briidge#prepareToReadCard(CardReadListener)}.  
	 * @param txn The object used to perform the card read and continue the API call session.
	*/
	@Override
	public void presentCardReadUI(CardReadTransaction txn) {
		this.cardReadTransaction = txn;
		runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				enableForegroundDispatch();
				((TextView) findViewById(R.id.cardReadMsg)).setText("Tap Card");
			}
		});
	}
	
	@Override
	protected void onPause() {
		if (cardReadTransaction != null) {
			disableForegroundDispatch();
		}
		super.onPause();
	}

	
	@Override
	protected void onResume() {
		super.onResume();
		
		if (cardReadTransaction != null) {
			enableForegroundDispatch();
		}
	}
	
	
	@Override
	protected void onStop() {
		if (cardReadTransaction != null) {
			cardReadTransaction.cancelCardRead();
		}
		super.onStop();
	}
	
	
	@Override
	protected void onNewIntent(Intent intent) {
		if (cardReadTransaction != null && cardReadTransaction.isNFCIntent(intent)) {
			cardReadTransaction.readCard(intent);
		} else {
			super.onNewIntent(intent);
		}
	}
	
	// Begins filtering for the NFC intent.
	private void enableForegroundDispatch() {
		Log.i("SDKSample", "enable foreground dispatch");
		if (NfcAdapter.getDefaultAdapter(this) != null) {
			try {
				NfcAdapter.getDefaultAdapter(this).enableForegroundDispatch(
						this, 
						PendingIntent.getActivity(this, 0, new Intent(this, this.getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0), 
						new IntentFilter[] { new IntentFilter(NfcAdapter.ACTION_TECH_DISCOVERED) }, 
						new String[][] { new String[] { NfcA.class.getName() }, new String[] { NfcB.class.getName() } });
			} catch (Exception e) {
				Log.i("SDKSample", "enableForegroundDispatch failed");
			}
		}
	}

	// Stops filtering for the NFC intent.
	private void disableForegroundDispatch() {
		Log.i("SDKSample", "disable foreground dispatch");
		if (NfcAdapter.getDefaultAdapter(this) != null) {
			try {
				NfcAdapter.getDefaultAdapter(this).disableForegroundDispatch(this);
			} catch (Exception e) {
				Log.i("SDKSample", "disableForegroundDispatch failed = " + e.getMessage());
			}
		}
	}
	
	@SuppressLint("DefaultLocale")
	private String processCardDataResponse(JSONObject response) {
		StringBuffer result = new StringBuffer("Card read complete.\n\n");
		
		try {
			JSONArray jsonArray = response.getJSONArray("cardData");
			for (int i = 0; i < jsonArray.length(); i++) {
				JSONObject object = jsonArray.getJSONObject(i);
				String tag = object.getString("tag");
				if("57".equals(tag)) {
					String value = object.getString("value");
					value = value.toLowerCase();
					int separatorIndex = value.indexOf("d");
					String cardNumber = value.substring(0, separatorIndex);
					String expiryDateYear = value.substring(separatorIndex + 1, separatorIndex + 3);
					String expiryDateMonth = value.substring(separatorIndex + 3, separatorIndex + 5);
					result.append("Card Number:").append(cardNumber).append("\nExpiry Date:").append(expiryDateYear).append("/").append(expiryDateMonth).append("\n\n");
					Log.i("SDKSample", "cardData" + result.toString());
					break;
				}
			}
		} catch (JSONException e) {
			Log.i("SDKSample", "Card data cannot be parsed.");
		}
		
		return result.append("Card Data:\n").append(response).toString();
	}
	
	private void showDialog(final String message) {
		ReadCardActivity.this.runOnUiThread(new Runnable() {

			@Override
			public void run() {
				Dialog dialog = SDKSampleApp.createDialog(ReadCardActivity.this, message);
				dialog.show();	
			}
			
		});
	}
}
