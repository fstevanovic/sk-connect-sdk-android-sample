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

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.securekey.briidge.Briidge;
import com.securekey.sdk.sample.R;

/**
 * Demonstrate pairing device to the user
 * 
 */
public class PairDeviceActivity extends Activity implements Briidge.PairListener {

	public static Activity me = null;

	private ProgressDialog mProgressDialog;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		SDKSampleApp.getInstance().setCurrentActivity(this);
		me = this;
		setContentView(R.layout.activity_pairdevice);

		((Button) findViewById(R.id.buttonPairDevice)).setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
            	
           		final String paircode = ((TextView)findViewById(R.id.editTextPairingCode)).getText().toString();
           		if (paircode.length() == 0) {
           			showDialog( "Please input pairing code!");
           			return;
           		}
           		pairDevice(paircode);
            }

		});
	}

	private void showDialog(final String message) {
		PairDeviceActivity.this.runOnUiThread(new Runnable() {

			@Override
			public void run() {
				Dialog dialog = SDKSampleApp.createDialog(PairDeviceActivity.this, message);
				dialog.show();	
			}
			
		});
	}

	/**
	 * Start pairing of device
	 * <p>
	 * Gets Briidge object and calls briidge.pair
	 * 
	 * @param	paircode	paring code communication to the user outside of the mobile app
	 * 
	 */		
	private void pairDevice(final String paircode) {

		mProgressDialog = new ProgressDialog(this);
		updateProgressDialog("Pairing Device to User");    			
		BriidgePlatformFactory.getBriidgePlatform(PairDeviceActivity.this).pair(paircode, PairDeviceActivity.this);
		
	}

	/**
	 * Implementation of PairListener.pairComplete
	 * 
	 */		
	@Override
	public void pairComplete(int status) {
		dismissProgressDialog();
		if (status == Briidge.STATUS_OK) {
			showDialog( "SUCCESS!");	
		} else {
			showDialog( "Pairing Failed! Error: " + status);
		}
	}
	
	private void dismissProgressDialog() {
		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				if(mProgressDialog.isShowing()) {
					mProgressDialog.dismiss();
				}
			}
			
		});
	}
	
	private void updateProgressDialog(final String message) {
		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				mProgressDialog.setMessage(message);
				if(!mProgressDialog.isShowing()) {
					mProgressDialog.show();
				}
				
			}
			
		});
	}	
}
