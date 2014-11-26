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
import java.util.Vector;

import org.json.me.JSONObject;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.securekey.briidge.Briidge;
import com.securekey.briidge.SKAudience;
import com.securekey.briidge.SKAudienceJwt;
import com.securekey.sdk.sample.R;
import com.securekey.sdk.sample.rp.SampleRPSessionFactory;

/**
 * Demonstrates verification of user quickcode (Verify QuickCode flow)
 * 
 */
public class VerifyQuickCodeActivity extends Activity implements Briidge.VerifyQuickCodeListener, Briidge.VerifyQuickCodeReturnJWSListener, Briidge.AssertUserListener {

	public static Activity me = null;
	private ProgressDialog mProgressDialog;
	private boolean jwsExpected = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		SDKSampleApp.getInstance().setCurrentActivity(this);
		me = this;
		setContentView(R.layout.activity_verifyquickcode);

		((Button) findViewById(R.id.buttonVerifyQuickCodeReturnJWS)).setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
            	
           		final String passcode = ((TextView)findViewById(R.id.editTextQuickCode)).getText().toString();
           		if (passcode.length() == 0) {
           			showDialog( "Please input quick code!");
           			return;
           		}
           		verifyQuickCodeReturnJWS(passcode);
            }

		});
		
		((Button) findViewById(R.id.buttonVerifyQuickCode)).setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
            	
           		final String passcode = ((TextView)findViewById(R.id.editTextQuickCode)).getText().toString();
           		if (passcode.length() == 0) {
           			showDialog( "Please input quick code!");
           			return;
           		}
           		verifyQuickCode(passcode);
            }

		});

		
		((Button) findViewById(R.id.buttonAssertUser)).setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
            	
           		final String passcode = ((TextView)findViewById(R.id.editTextQuickCode)).getText().toString();
           		if (passcode.length() == 0) {
           			showDialog( "Please input quick code!");
           			return;
           		}
           		asserUser(passcode);
            }

		});
	}


	private void showDialog(final String message) {
		VerifyQuickCodeActivity.this.runOnUiThread(new Runnable() {

			@Override
			public void run() {
				Dialog dialog = SDKSampleApp.createDialog(VerifyQuickCodeActivity.this, message);
				dialog.show();	
			}
			
		});
	}

	/**
	 * Assert user
	 * <p>
	 * Gets Briidge object and calls briidge.assertUser
	 * 
	 * @param	passcode	user supplied quickcode
	 */	
	private void asserUser(final String passcode) {

		mProgressDialog = new ProgressDialog(this);
		updateProgressDialog("Verifying");
		this.jwsExpected = false;
		
		EditText audiencesText = (EditText)findViewById(R.id.assert_audiences);
		String [] list = (audiencesText.length() == 0 ? (new String[] {}) : audiencesText.getText().toString().split("\n"));
		SKAudience [] audiences = new SKAudience[list.length];
		
		for (int i = 0; i < audiences.length; i++) {
			audiences[i] = new SKAudience(list[i]);
		}
		
		BriidgePlatformFactory.getBriidgePlatform(VerifyQuickCodeActivity.this).assertUser(SDKSampleApp.getInstance().retrieveUserId(), passcode, audiences, VerifyQuickCodeActivity.this);
	}
	
	/**
	 * Start quickcode verification
	 * <p>
	 * Gets Briidge object and calls briidge.verifyQuickCode
	 * 
	 * @param	passcode	user supplied quickcode
	 */	
	private void verifyQuickCode(final String passcode) {

		mProgressDialog = new ProgressDialog(this);
		updateProgressDialog("Verifying");
		this.jwsExpected = false;
		BriidgePlatformFactory.getBriidgePlatform(VerifyQuickCodeActivity.this).verifyQuickCode(passcode, SDKSampleApp.getInstance().retrieveUserId(), VerifyQuickCodeActivity.this);
	}

	/**
	 * Starts quickcode verification. Expects the results in JWS format.
	 * <p>
	 * Gets {@link Briidge} object and calls {@link Briidge#verifyQuickCodeReturnJWS}
	 */	
	private void verifyQuickCodeReturnJWS(final String passcode) {

		mProgressDialog = new ProgressDialog(this);
		updateProgressDialog("Verifying");    			
		this.jwsExpected = true;
		BriidgePlatformFactory.getBriidgePlatform(VerifyQuickCodeActivity.this).verifyQuickCodeReturnJWS(passcode, SDKSampleApp.getInstance().retrieveUserId(), VerifyQuickCodeActivity.this);
	}

	/**
	 * Implements VerifyQuickCodeListener.verifyQuickCodeComplete
	 * <p>
	 * If successful, will forward received txnId for final verification by the RP Server
	 * 
	 */
	@Override
	public void verifyQuickCodeComplete(int status, String transactionId) {
		if (status == Briidge.STATUS_OK) {
			if (this.jwsExpected) {
				// The JWT should be provide to a third party to be verify
				// It could be parse to see what's inside 

				dismissProgressDialog();			
				String [] jwtSegment = getJWSSegments(transactionId);
				try {
					final StringBuilder message = new StringBuilder();
					message.append(new String(Base64.decode(jwtSegment[0], Base64.NO_WRAP), "UTF-8") + "\n");
					message.append(new String(Base64.decode(jwtSegment[1], Base64.NO_WRAP), "UTF-8") + "\n\n\n");
					showDialog(message.toString());
					
					verifyJWT(transactionId);
				} catch (UnsupportedEncodingException e) {
					Log.e(VerifyQuickCodeActivity.class.toString(), e.getMessage());
				}

			} else {
				
				rpQuickCodeVerify(transactionId);				
			}
		} else if(status == Briidge.STATUS_TRY_LATER_TOO_MANY_INVALID_ATTEMPTS) {
			showDialog( "User suspended!");	
		} else if(status == Briidge.STATUS_WRONG_CODE) {
			showDialog( "Invalid code!");	
		} else {
			dismissProgressDialog();
			showDialog( "Failed during verification process.");
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
	 * Implements AssertUserListener.assertUserComplete
	 * <p>
	 * If successful, will forward received JWT data for a final usage for a third party
	 * 
	 */
	@Override
	public void assertUserComplete(int status, Vector<SKAudienceJwt> audiences) {
		if (status == Briidge.STATUS_OK) {

			// The JWT should be provide to a third party to be verify
			// It could be parse to see what's inside 

			dismissProgressDialog();			
			for (SKAudienceJwt skAudienceJwt : audiences) {
				String [] jwtSegment = getJWSSegments(skAudienceJwt.getJwt());
				try {
					final StringBuilder message = new StringBuilder();
					message.append(new String(Base64.decode(jwtSegment[0], Base64.NO_WRAP), "UTF-8") + "\n");
					message.append(new String(Base64.decode(jwtSegment[1], Base64.NO_WRAP), "UTF-8") + "\n\n\n");
					showDialog(message.toString());
					
					verifyJWT(skAudienceJwt.getJwt());
				} catch (UnsupportedEncodingException e) {
					Log.e(VerifyQuickCodeActivity.class.toString(), e.getMessage());
				}
			}

		} else if(status == Briidge.STATUS_TRY_LATER_TOO_MANY_INVALID_ATTEMPTS) {
			showDialog( "User suspended!");	
		} else if(status == Briidge.STATUS_WRONG_CODE) {
			showDialog( "Invalid code!");	
		} else {
			dismissProgressDialog();
			showDialog( "Failed during verification process.");
		}
	}	
	
	
	/**
	 * Communicates with the RP Server to complete quickcode verification

	 * @param transactionId  txnId received as a result of SDK call to verifyQuickCode
	 */
	private void rpQuickCodeVerify( String transactionId ) {

		updateProgressDialog("Checking with RP Server ...");
		JSONObject response = SampleRPSessionFactory.createSampleRPSession(SampleRPSessionFactory.URL_VerifyQuickCode).verifyQuickCode( transactionId);
		dismissProgressDialog();
		showDialog( response.toString());
	
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
