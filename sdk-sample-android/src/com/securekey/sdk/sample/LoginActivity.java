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

import com.securekey.sdk.sample.R;

import android.app.Dialog;
import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

/**
 * Simulates login to the RP service. In this sample app, it will just remember User ID for future use
 * 
 */
public class LoginActivity extends Activity  {

	public static Activity me = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		SDKSampleApp.getInstance().setCurrentActivity(this);
		me = this;
		setContentView(R.layout.activity_login);

		((Button) findViewById(R.id.buttonLogin)).setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
            	
           		final String userId = ((TextView)findViewById(R.id.editTextUserId)).getText().toString();
           		if( (userId.length() < 6) || !(userId.contains("@")) ) {
           			showDialog( "Please enter valid email address as User ID. Valid email address ensures uniqueness on briidge.net services.");
           			return;
           		} else {
           			SDKSampleApp.getInstance().setUserId(userId);
           			LoginActivity.this.finish();
           		}
            }

		});
	}

	private void showDialog(final String message) {
		LoginActivity.this.runOnUiThread(new Runnable() {

			@Override
			public void run() {
				Dialog dialog = SDKSampleApp.createDialog(LoginActivity.this, message);
				dialog.show();	
			}
			
		});
	}

}
