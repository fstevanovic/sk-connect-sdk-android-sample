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
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.securekey.briidge.Briidge;
import com.securekey.sdk.sample.R;

/**
 * Top activity for this sample app
 * 
 * @author	Fedja Stevanovic (fedja.stevanovic@securekey.com)
 * 
 */
public class MainActivity extends Activity implements Briidge.ResetListener {
	
	private boolean handlersSet = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		SDKSampleApp.getInstance().setCurrentActivity(this);

		setContentView(R.layout.activity_main);
		
		String userId = SDKSampleApp.getInstance().retrieveUserId();
		if( (userId == null) || (userId.length() == 0) ) {
			
			Intent activity = new Intent(MainActivity.this, LoginActivity.class);
			startActivity(activity);
			
		} else {

			TextView welcomeText = (TextView)findViewById(R.id.textViewWelcome);
			welcomeText.setText("Welcome " + userId);

			setHandlers();

		
		}
		
	}

	@Override
	protected void onResume() {
		
		super.onResume();
		
		String userId = SDKSampleApp.getInstance().retrieveUserId();
		
		TextView welcomeText = (TextView)findViewById(R.id.textViewWelcome);
		welcomeText.setText("Welcome " + userId);
		
		setHandlers();
		
	}
	
	private void setHandlers() {
		
		if( !handlersSet ) {

		((Button) findViewById(R.id.buttonDoAuthenticateDevice)).setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
            	
				Intent activity = new Intent(MainActivity.this, AuthenticateDeviceActivity.class);
				startActivity(activity);
				
            }
        });
		
		((Button) findViewById(R.id.buttonDoReadCard)).setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
            	
				Intent activity = new Intent(MainActivity.this, ReadCardActivity.class);
				startActivity(activity);
				
            }
        });
		
		((Button) findViewById(R.id.buttonDoSetQuickCode)).setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
            	
				Intent activity = new Intent(MainActivity.this, SetQuickCodeActivity.class);
				startActivity(activity);
				
            }
        });
		
		((Button) findViewById(R.id.buttonDoVerifyQuickCode)).setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
            	
				Intent activity = new Intent(MainActivity.this, VerifyQuickCodeActivity.class);
				startActivity(activity);
				
            }
        });

		((Button) findViewById(R.id.buttonDoChangeQuickCode)).setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
            	
				Intent activity = new Intent(MainActivity.this, ChangeQuickCodeActivity.class);
				startActivity(activity);
				
            }
        });
		
		((Button) findViewById(R.id.buttonDoPairDevice)).setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
            	
				Intent activity = new Intent(MainActivity.this, PairDeviceActivity.class);
				startActivity(activity);
				
            }
        });

		((Button) findViewById(R.id.buttonDoUnpairDevice)).setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
            	
				Intent activity = new Intent(MainActivity.this, UnpairDeviceActivity.class);
				startActivity(activity);
				
            }
        });
		
		((Button) findViewById(R.id.buttonDoGetPairedUsers)).setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
            	
				Intent activity = new Intent(MainActivity.this, GetPairedUsersActivity.class);
				startActivity(activity);
				
            }
        });
		
		((Button) findViewById(R.id.buttonReset)).setOnClickListener(new OnClickListener() {
            public void onClick(View v) {

        	    new AlertDialog.Builder(MainActivity.this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Reset Device")
                .setMessage("Are you sure you want to reset device? This will result in deleting all provisioning and authentication data related to this device and on successful reset, app will close.")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                {
                	@Override
                		public void onClick(DialogInterface dialog, int which) {
                			resetDevice();
                	}

            })
            .setNegativeButton("No", null)
            .show();		
            }
        });
		
		}
	}
	
	
	private void showDialog(final String message) {
		MainActivity.this.runOnUiThread(new Runnable() {

			@Override
			public void run() {
				Dialog dialog = SDKSampleApp.createDialog(MainActivity.this, message);
				dialog.show();	
			}
			
		});
	}
	
	private void resetDevice() {
		try {
			SDKSampleApp.getInstance().removeUserId();
			Briidge accessPlatform = BriidgePlatformFactory.getBriidgePlatform(SDKSampleApp.getInstance());
			accessPlatform.reset(MainActivity.this);
		} catch (Exception e) {
			showDialog( "Error on device reset: " + e.getMessage());
		}
	}
	
	@Override
	public void resetComplete(int status) {
		if(status == Briidge.STATUS_OK) {
			SDKSampleApp.getInstance().removeUserId();
			SDKSampleApp.getInstance().setPasscodeStatus(false);
			MainActivity.this.finish();
		} else {
			showDialog( "Device reset failed. Error: " + status);
		}

	}		

}
