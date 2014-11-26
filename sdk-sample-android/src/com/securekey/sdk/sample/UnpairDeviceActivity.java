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
import android.widget.EditText;
import android.widget.TextView;

import com.securekey.briidge.Briidge;
import com.securekey.sdk.sample.R;

/**
 * Demonstrate un-pairing device from a user
 * 
 */
public class UnpairDeviceActivity extends Activity implements Briidge.UnpairListener {

	public static Activity me = null;

	private ProgressDialog mProgressDialog;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		SDKSampleApp.getInstance().setCurrentActivity(this);
		me = this;
		setContentView(R.layout.activity_unpairdevice);
		
		EditText userIdField = ((EditText)findViewById(R.id.editTextUserId));
		userIdField.setText(SDKSampleApp.getInstance().retrieveUserId());
		

		((Button) findViewById(R.id.buttonUnpairDevice)).setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
            	
           		final String userId = ((TextView)findViewById(R.id.editTextUserId)).getText().toString();
           		if (userId.length() == 0) {
           			showDialog( "Please enter user ID!");
           			return;
           		}
           		unpairDevice(userId);
            }

		});
	}

	private void showDialog(final String message) {
		UnpairDeviceActivity.this.runOnUiThread(new Runnable() {

			@Override
			public void run() {
				Dialog dialog = SDKSampleApp.createDialog(UnpairDeviceActivity.this, message);
				dialog.show();	
			}
			
		});
	}

	/**
	 * Start un-pairing of device
	 * <p>
	 * Gets Briidge object and calls briidge.pair
	 * 
	 * @param	userId	userId to which device was previously paired and now we want to remove association
	 * 
	 */		
	private void unpairDevice(final String userId) {

		mProgressDialog = new ProgressDialog(this);
		updateProgressDialog("Unpairing Device from a user");    			
		BriidgePlatformFactory.getBriidgePlatform(UnpairDeviceActivity.this).unpair(userId, UnpairDeviceActivity.this);
		
	}

	/**
	 * Implements UnpairListener.unpairComplete to handle result of un-pairing call
	 */
	@Override
	public void unpairComplete(int status) {
		dismissProgressDialog();
		if (status == Briidge.STATUS_OK) {
			showDialog( "SUCCESS!");	
		} else {
			showDialog( "Unpairing Failed! Error: " + status);
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
