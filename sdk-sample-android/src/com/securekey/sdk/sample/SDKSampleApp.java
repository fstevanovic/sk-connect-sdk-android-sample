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
import android.app.Application;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
/**
 * Main application class
 * <p></p>
 * Provides application level services like persistent app data management
 * 
 * @author	Fedja Stevanovic (fedja.stevanovic@securekey.com)
 *  
 */

public class SDKSampleApp  extends Application {

	private static SDKSampleApp instance;

	private Activity currentActivity;//It is used for going to the main thread when there is an error received outside of an activity (like in the main listener)

	public static SDKSampleApp getInstance() {
		return instance;
	}
	
	public Activity getCurrentActivity() {
		return currentActivity;
	}


	public void setCurrentActivity(Activity currentActivity) {
		this.currentActivity = currentActivity;
	}
	
	@Override
	public void onCreate() {
		instance = this;
		super.onCreate();
	}
	
	/**
	 * Returns stored userId
	 * 
	 * @return	userId or null if not set
	 */
	public String retrieveUserId()  {
		try {
			SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(SDKSampleApp.getInstance());
			return (String) settings.getString("PERSISTED_USER_ID_KEY", "");
		} catch (Exception e) {
			return null;
		}
	}
	
	/**
	 * Stores userId to persistant storage (app preferences)
	 * 
	 * @param userid  userId to store
	 */
	public void setUserId(String userid) {
		try {
			SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(SDKSampleApp.getInstance());
			SharedPreferences.Editor editor = settings.edit();
			editor.putString("PERSISTED_USER_ID_KEY", userid);
			// 	Commit the edits!
			editor.commit();
		} catch (Exception e) {
			
		}
	}
	
	/**
	 * Removes stored userId
	 * 
	 */
	public void removeUserId() {
		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(SDKSampleApp.getInstance());
		SharedPreferences.Editor editor = settings.edit();
		editor.remove("PERSISTED_USER_ID_KEY");
		// Commit the edits!
	    editor.commit();
	}
	
	/**
	 * Sets the status of user quickcode setup. 
	 * <p>
	 * Called to check if app should use quickcode login option instead of username/password
	 * 
	 * @param isSet  true if user has quickcode, false otherwise
	 */
	public void setPasscodeStatus( boolean isSet  ) {
		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(SDKSampleApp.getInstance());
		SharedPreferences.Editor editor = settings.edit();
		editor.putBoolean("HAS_PASSCODE", isSet);
		// Commit the edits!
	    editor.commit();
	}

	/**
	 * Returns if there is quickcode set for the user
	 * 
	 * @return true if user previously set quickcode on this device
	 */
	public boolean retrievePasscodeStatus()  {
		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(SDKSampleApp.getInstance());
		boolean status = false;
		try {
			status = settings.getBoolean("HAS_PASSCODE", false);
			
		} catch (Exception ex) {
		}
		return status;
	}
	
	public static Dialog createDialog(Context context, String message) {
	        AlertDialog.Builder builder = new AlertDialog.Builder(context);
	        builder.setMessage(message)
	               .setPositiveButton("OK", new DialogInterface.OnClickListener() {
	                   public void onClick(DialogInterface dialog, int id) {
	                       dialog.dismiss();
	                   }
	               });
	        return builder.create();
	    }	
}
