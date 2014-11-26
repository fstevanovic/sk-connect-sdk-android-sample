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

import java.io.UnsupportedEncodingException;

import org.json.me.JSONObject;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;

import com.securekey.briidge.Briidge;
import com.securekey.briidge.Briidge.AuthenticateDeviceListener;
import com.securekey.briidge.Briidge.AuthenticateDeviceReturnJWSListener;
import com.securekey.sdk.sample.R;
import com.securekey.sdk.sample.rp.SampleRPSessionFactory;

/**
 * Demonstrate AuthenticateDevice flow
 *  <p></p>
 *  Implements AuthenticateDevice flow. From briidge.authenticateDevice call, listener implementation and communication with the RP server
 * 
 */
public class AuthenticateDeviceActivity extends Activity implements AuthenticateDeviceListener, AuthenticateDeviceReturnJWSListener {

	public static Activity me = null;
	private ProgressDialog mProgressDialog;
	private boolean jwsExpected = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		SDKSampleApp.getInstance().setCurrentActivity(this);
		me = this;
		setContentView(R.layout.activity_authenticate);

		mProgressDialog = new ProgressDialog(this);
		
		findViewById(R.id.buttonAuthenticateDeviceReturnJWS).setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				authenticateDeviceAndReturnJWS();
			}
		});

		
		findViewById(R.id.buttonAuthenticateDevice).setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				authenticateDevice();
			}
		});

	}


	private void showDialog(final String message) {
		AuthenticateDeviceActivity.this.runOnUiThread(new Runnable() {

			@Override
			public void run() {
				Dialog dialog = SDKSampleApp.createDialog(AuthenticateDeviceActivity.this, message);
				dialog.show();	
			}
			
		});
	}


	private void dismissProgressDialog() {
		AuthenticateDeviceActivity.this.runOnUiThread(new Runnable() {

			@Override
			public void run() {
				if(mProgressDialog.isShowing()) {
					mProgressDialog.dismiss();
				}
			}

		});
	}

	private void updateProgressDialog(final String message) {
		AuthenticateDeviceActivity.this.runOnUiThread(new Runnable() {

			@Override
			public void run() {
				mProgressDialog.setMessage(message);
				if(!mProgressDialog.isShowing()) {
					mProgressDialog.show();
				}

			}

		});
	}

	/**
	 * Starts device authentication.
	 * <p>
	 * Gets Briidge object and calls briidge.authenticateDevice
	 */	
	private void authenticateDevice() {
		Briidge briidge = BriidgePlatformFactory.getBriidgePlatform(AuthenticateDeviceActivity.this);
		if(briidge != null) {
			updateProgressDialog("Device authentication ...");
			this.jwsExpected = false;
			briidge.authenticateDevice(AuthenticateDeviceActivity.this);
		}
	}

	/**
	 * Starts device authentication. Expects the results in JWS format.
	 * <p>
	 * Gets {@link Briidge} object and calls {@link Briidge#authenticateDeviceReturnJWS}
	 */	
	private void authenticateDeviceAndReturnJWS() {
		Briidge briidge = BriidgePlatformFactory.getBriidgePlatform(AuthenticateDeviceActivity.this);
		if(briidge != null) {
			updateProgressDialog("Device authentication ...");
			this.jwsExpected = true;
			briidge.authenticateDeviceReturnJWS(AuthenticateDeviceActivity.this);
		}
	}

	
	private String[] getJWSSegments(String data) {
		String[] segments = data.split("\\.");
		if(segments != null && segments.length <= 3) {
			return segments;
		}
		return null;
	}
	
	
	/**
	 * Implementation of Briidge.AuthenticateDeviceListener.authenticateDeviceComplete
	 * or Briidge.AuthenticateDeviceReturnJWSListener.authenticateDeviceComplete
	 */	
	@Override
	public void authenticateDeviceComplete(int status, String txnId) {
		dismissProgressDialog();
		if(status == Briidge.STATUS_OK) {
			if(txnId != null) {
				
				if (this.jwsExpected) {
					
					// The JWT should be provide to a third party to be verify
					// It could be parse to see what's inside 

					dismissProgressDialog();			
					String [] jwtSegment = getJWSSegments(txnId);
					try {
						final StringBuilder message = new StringBuilder();
						message.append(new String(Base64.decode(jwtSegment[0], Base64.NO_WRAP), "UTF-8") + "\n");
						message.append(new String(Base64.decode(jwtSegment[1], Base64.NO_WRAP), "UTF-8") + "\n\n\n");
						showDialog(message.toString());
						
						// JWT could be check on server side
						verifyJWT(txnId);
					} catch (UnsupportedEncodingException e) {
						Log.e(VerifyQuickCodeActivity.class.toString(), e.getMessage());
					}
					
				} else {
					
					getDeviceId(txnId);
				}
			} else {
				showDialog( "Error: null data!");
			}
		} else if(status == Briidge.STATUS_CONNECTIVITY_ERROR) {
			showDialog("Error connecting to server!");
		} else {
			showDialog( "Error processing request!");
		}
	}

	/**
	 * Calls RP Server to complete device authentication
	 * 
	 * @param	deviceTxnId	txnId returned in authenticateDeviceComplete
	 */	
	private void getDeviceId(String deviceTxnId) {
		updateProgressDialog("Contacting SampleRP server for device-initiated-get-device...");
		JSONObject response = SampleRPSessionFactory.createSampleRPSession(SampleRPSessionFactory.URL_DeviceInitiatedGetDevice).authenticateDevice(deviceTxnId);
		dismissProgressDialog();
		showDialog("RPServer response:\n\n"+response.toString());
	}
	
	
	/**
	 * Calls RP Server to verify JWT content
	 * 
	 * @param	jwt jwtDat to be verified
	 */	
	private void verifyJWT(String jwt) {
		updateProgressDialog("Contacting SampleRP to verify JWT...");
		JSONObject response = SampleRPSessionFactory.createSampleRPSession(SampleRPSessionFactory.URL_VerifyJWT).verifyJWT(jwt);
		dismissProgressDialog();
		showDialog("RPServer response:\n\n"+response.toString());
	}
}

