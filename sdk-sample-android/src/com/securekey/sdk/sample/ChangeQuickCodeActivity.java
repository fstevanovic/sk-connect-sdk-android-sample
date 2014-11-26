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
import android.widget.TextView;

import com.securekey.briidge.Briidge;
import com.securekey.briidge.Briidge.ChangeQuickCodeListener;
import com.securekey.sdk.sample.R;

/**
 * Demonstrate Change QuickCode flow
 * 
 */
public class ChangeQuickCodeActivity extends Activity implements ChangeQuickCodeListener {

	public static Activity me = null;

	private ProgressDialog mProgressDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		SDKSampleApp.getInstance().setCurrentActivity(this);
		me = this;
		setContentView(R.layout.activity_changequickcode);

		mProgressDialog = new ProgressDialog(this);

		findViewById(R.id.buttonChangeQuickCode).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {

				// get saved userId
				String userId = SDKSampleApp.getInstance().retrieveUserId();
    			
				
				String oldQuickCode = ((TextView)findViewById(R.id.editOldQuickCode)).getText().toString();
    			if (oldQuickCode.length() == 0) {
    				showDialog("Please enter QuickCode!");
    				return;
    			}
				
            	String quickCode = ((TextView)findViewById(R.id.editTextQuickCode)).getText().toString();
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
				
    			changeQuickCode(oldQuickCode, quickCode, userId );
			}
		});
		
	}

	/**
	 * Starts change quickcode action

	 * @param	oldQuickCode	user's old quickcode
	 * @param	quickCode		user's new quickcode
	 * @param	userId			userId for the RP service
	 */	
	public void changeQuickCode(String oldQuickCode, String quickCode, String userId) {
		
		updateProgressDialog("Changing user QuickCode...");
		
		Briidge briidge = BriidgePlatformFactory.getBriidgePlatform(ChangeQuickCodeActivity.this);
		if(briidge != null) {
			briidge.changeQuickCode(oldQuickCode, quickCode, userId, ChangeQuickCodeActivity.this);
		}
	}

	/**
	 * Implementation of Briidge.ChangeQuickCodeListener.changeQuickCodeComplete
	 */	
	@Override
	public void changeQuickCodeComplete(int status) {
		dismissProgressDialog();
		if( status == Briidge.STATUS_OK) {
			showDialog( "QuickCode successfully changed." );
		} else {
			showDialog( "QuickCode could NOT be changed! Error code: " + status );
		}
	}
	
	private void dismissProgressDialog() {
		ChangeQuickCodeActivity.this.runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				if(mProgressDialog.isShowing()) {
					mProgressDialog.dismiss();
				}
			}
			
		});
	}
	
	private void updateProgressDialog(final String message) {
		ChangeQuickCodeActivity.this.runOnUiThread(new Runnable() {

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
		ChangeQuickCodeActivity.this.runOnUiThread(new Runnable() {

			@Override
			public void run() {
				Dialog dialog = SDKSampleApp.createDialog(ChangeQuickCodeActivity.this, message);
				dialog.show();	
			}
			
		});
	}


}
