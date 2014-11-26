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

import java.util.Vector;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.widget.TextView;

import com.securekey.briidge.Briidge;
import com.securekey.briidge.SKPairedUser;
import com.securekey.sdk.sample.R;

/**
 * Demonstrate getting list of users paired to the device
 * 
 */
public class GetPairedUsersActivity extends Activity implements
		Briidge.GetPairedUsersListener {

	public static Activity me = null;

	private ProgressDialog mProgressDialog;

	TextView list;
	TextView statusMessage;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		SDKSampleApp.getInstance().setCurrentActivity(this);
		me = this;
		setContentView(R.layout.activity_getpairedusers);

		
		list = ((TextView) findViewById(R.id.listViewUsers));
		statusMessage = ((TextView) findViewById(R.id.textGetPairedUsersStatus));

		getPairedUsers();

	}

	private void showDialog(final String message) {
		GetPairedUsersActivity.this.runOnUiThread(new Runnable() {

			@Override
			public void run() {
				Dialog dialog = SDKSampleApp.createDialog(
						GetPairedUsersActivity.this, message);
				dialog.show();
			}

		});
	}

	private void showItems(final String content) {
		GetPairedUsersActivity.this.runOnUiThread(new Runnable() {

			@Override
			public void run() {
				list.setText(content);
			}

		});
	}

	/**
	 * Start getting users request
	 * <p>
	 * Gets Briidge object and calls briidge.getPairedUsers
	 */	
	private void getPairedUsers() {

		mProgressDialog = new ProgressDialog(this);
		updateProgressDialog("Getting Paired Users ...");
		BriidgePlatformFactory.getBriidgePlatform(GetPairedUsersActivity.this)
				.getPairedUsers(GetPairedUsersActivity.this);

	}

	/**
	 * Implementation of Briidge.GetPairedUsersListener.getPairedUsersComplete
	 */	
	@Override
	public void getPairedUsersComplete(int status, Vector users) {
		dismissProgressDialog();
		if (status != Briidge.STATUS_OK) {
			showDialog("GetPairedUsers Failed! Error: " + status);
		} else {

			// number of users counter
			int counter = 0;
			
			// output builder
			String content = "";
			
			@SuppressWarnings("unchecked")
			Vector<SKPairedUser> skUsers = users;
			if (skUsers.size() == 0) {
				content = "None";
			} else {
				try {
					// extract user info for paired users
					for (SKPairedUser user : skUsers) {
						counter++;
						String itemText = counter + ": " + user.getUserId()	+ " (" + user.getCreationDate() + ")";
						content = content + itemText + "\n";
					}
				} catch (Exception e) {
					showDialog("Unable to display users paired to this device:"
							+ e.getMessage());
				}

			}
			
			// show results
			showItems(content);
			
			if (counter > 0) {
				String msgStatus = "Number of paired users: " + counter;
				showDialog(msgStatus);
			}
		}
	}

	private void dismissProgressDialog() {
		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				if (mProgressDialog.isShowing()) {
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
				if (!mProgressDialog.isShowing()) {
					mProgressDialog.show();
				}

			}

		});
	}
}
