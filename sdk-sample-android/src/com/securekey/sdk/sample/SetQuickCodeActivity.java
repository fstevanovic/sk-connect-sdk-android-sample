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

import org.json.me.JSONException;
import org.json.me.JSONObject;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.securekey.briidge.Briidge;
import com.securekey.briidge.Briidge.AuthenticateDeviceListener;
import com.securekey.briidge.Briidge.SetQuickCodeListener;
import com.securekey.sdk.sample.R;
import com.securekey.sdk.sample.rp.SampleRPSessionFactory;

/**
 * Demonstrate Set QuickCode flow
 * 
 */
public class SetQuickCodeActivity extends Activity implements AuthenticateDeviceListener, SetQuickCodeListener {

	public static Activity me = null;

	private ProgressDialog mProgressDialog;

	/**
	 * txnId returned from RP server (init-set-passcode.action)
	 */
	private String serverTxnId = "";

	/**
	 * txnId returned from briidge.authenticateDevice (for sending to RP server)
	 */
	private String deviceTxnId = "";

	private String userId;
	private String quickCode;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		SDKSampleApp.getInstance().setCurrentActivity(this);
		me = this;
		setContentView(R.layout.activity_setquickcode);

		mProgressDialog = new ProgressDialog(this);
		
		userId = SDKSampleApp.getInstance().retrieveUserId();
		
		EditText userIdField = ((EditText)findViewById(R.id.editTextUsername));
		userIdField.setText(userId);

		findViewById(R.id.buttonSetQuickCode).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {

            	quickCode = ((TextView)findViewById(R.id.editTextQuickCode)).getText().toString();
    			if (quickCode.length() == 0) {
    				showDialog("QuickCode has to be at least 4 digits long!");
    				return;
    			}
    			final String pwd2 =  ((TextView)findViewById(R.id.editTextQuickCodeConfirm)).getText().toString();
    			if (pwd2.length() ==0 ){
    				showDialog("QuickCode has to be at least 4 digits long!");
    				return;
    			}
    			if( !(quickCode.equalsIgnoreCase(pwd2)) ) {
    				showDialog("QuickCode entries not matching!");
    				return;
    			}
				
    			authenticateDevice();
			}
		});
		
	}

	/**
	 * Finalizing quickcode setup with the txnId recived from RP server
	 * <p>
	 * Gets Briidge object and calls briidge.setQuickCode
	 */	
	public void startSetQuickCodeSession(String quickCode, String transactionId) {
		
		updateProgressDialog("Setting your QuickCode...");
		
		Briidge briidge = BriidgePlatformFactory.getBriidgePlatform(SetQuickCodeActivity.this);
		if(briidge != null) {
			updateProgressDialog( "Calling setQuickCode ...");
			briidge.setQuickCode(quickCode, transactionId, SetQuickCodeActivity.this);
		}
	}

	
	/**
	 * Implementation of Briidge.SetQuickCodeListener.setQuickCodeComplete
	 */	
	@Override
	public void setQuickCodeComplete(int status) {
		dismissProgressDialog();
		if( status == Briidge.STATUS_OK) {
			showDialog( "QuickCode successfully set." );
			// if success, remember that quickcode is set so we can skip username/password login next time
			SDKSampleApp.getInstance().setPasscodeStatus(true);
		} else {
			showDialog( "QuickCode could NOT be set! Error code: " + status );
		}
	}
	
	private void dismissProgressDialog() {
		SetQuickCodeActivity.this.runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				if(mProgressDialog.isShowing()) {
					mProgressDialog.dismiss();
				}
			}
			
		});
	}
	
	private void updateProgressDialog(final String message) {
		SetQuickCodeActivity.this.runOnUiThread(new Runnable() {

			@Override
			public void run() {
				mProgressDialog.setMessage(message);
				if(!mProgressDialog.isShowing()) {
					mProgressDialog.show();
				}
				
			}
			
		});
	}
	
	private void showDialog(final String message) {
		SetQuickCodeActivity.this.runOnUiThread(new Runnable() {

			@Override
			public void run() {
				Dialog dialog = SDKSampleApp.createDialog(SetQuickCodeActivity.this, message);
				dialog.show();	
			}
			
		});
	}

	   
	/**
	 * Starts device authentication.
	 * <p>
	 * Gets Briidge object and calls briidge.authenticateDevice to obtain txnId for init-set-passcode
	 */		
	private void authenticateDevice() {
		Briidge briidge = BriidgePlatformFactory.getBriidgePlatform(SetQuickCodeActivity.this);
		if(briidge != null) {
			updateProgressDialog("Device authentication ...");
			briidge.authenticateDevice(SetQuickCodeActivity.this);
		}
		
	}
	
	/**
	 * Implementation of Briidge.AuthenticateDeviceListener.authenticateDeviceComplete
	 * <p>
	 * Will use received txnId to initiate call to RP server for init-set-passcode.action
	 */	
	@Override
	public void authenticateDeviceComplete(int status, String txnId) {
		if(status == Briidge.STATUS_OK) {
			if(txnId != null) {
				deviceTxnId = txnId;
				serverInitSetPasscode();
			} else {
				showDialog("Error: null transactionId!");
			}
		} else if(status == Briidge.STATUS_CONNECTIVITY_ERROR) {
			showDialog( "Error connecting to server!");
		} else {
			showDialog( "Error processing request!");
		}
	}
	
	/**
	 * Calls RP Server to perform init-set-passcode. Will recieve txnId for final setting of quickcode
	 */
	private void serverInitSetPasscode() {
		
		updateProgressDialog("Contacting SampleRP server for init-set-passcode...");
		JSONObject response = SampleRPSessionFactory.createSampleRPSession(SampleRPSessionFactory.URL_InitQuickCode).initMobileQuickCode( userId, deviceTxnId);
		try {
			serverTxnId = response.getString("txnId");
			startSetQuickCodeSession( quickCode, serverTxnId );
		} catch (JSONException e) {
			showDialog( "Invalid server response!");
		}
		
		
	}
}
