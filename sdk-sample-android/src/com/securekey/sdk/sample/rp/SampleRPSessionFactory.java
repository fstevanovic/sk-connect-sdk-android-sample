/* 
* Copyright (c) 2014 SecureKey Technologies Inc. All rights reserved.
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

package com.securekey.sdk.sample.rp;

/**
 * SampleRP Server Communication Factory
 * <p></p>
 * Create instance of SampleRPSession object per defined parameters
 * 
 * @author	Fedja Stevanovic (fedja.stevanovic@securekey.com)
 * 
 */
public class SampleRPSessionFactory { 

	/**
	 * RP App Server URL to handle device-initiated-get-device.action
	 */
	public static String URL_DeviceInitiatedGetDevice;

	/**
	 * RP App Server URL to handle init-set-passcode.action
	 */
	public static String URL_InitQuickCode;

	/**
	 * RP App Server URL to handle verify-passcode-data.action
	 */
	public static String URL_VerifyQuickCode;

	/**
	 * RP App Server URL to handle getProvisioningAuthorizationCode.action
	 */
	public static String URL_GetProvisioningAuthorizationCode;
	
	/**
	 * RP App Server URL to handle verify JWT
	 */
	public static String URL_VerifyJWT;

	/**
	 * RP App Server URL to handle getCardReadData.action 
	 */
	public static String URL_GetCardReadData;
	
	// Local server values, change line bellow to the URL where the local RP Server is running
	static final String URL_Server_Local = "http://10.0.12.182:9080/samplerp/forms/";   
	static final String URL_DeviceInitiatedGetDevice_Local = URL_Server_Local  + "getDeviceId.json";
	static final String URL_InitQuickCode_Local = URL_Server_Local  + "initMobileQuickcode.json";
	static final String URL_VerifyQuickCode_Local = URL_Server_Local  + "verifyQuickcode.json";
	static final String URL_GetProvisioningAuthorizationCode_Local = URL_Server_Local  + "getProvisioningAuthorizationCodeSimple";
	static final String URL_VerifyJWT_Local = URL_Server_Local  + "verifyJWT.json";
	static final String URL_GetCardReadData_Local = URL_Server_Local + "getDeviceInitiatedCardReadData.json";

	// Sandbox server values
	static final String URL_Server_SB = "https://rpserver.securekey.com/samplerp/forms/"; 
	static final String URL_DeviceInitiatedGetDevice_SB = URL_Server_SB  + "getDeviceId.json";
	static final String URL_InitQuickCode_SB = URL_Server_SB  + "initMobileQuickcode.json";
	static final String URL_VerifyQuickCode_SB = URL_Server_SB  + "verifyQuickcode.json";
	static final String URL_GetProvisioningAuthorizationCode_SB = URL_Server_SB  + "getProvisioningAuthorizationCodeSimple";
	static final String URL_VerifyJWT_SB = URL_Server_SB  + "verifyJWT.json";
	static final String URL_GetCardReadData_SB = URL_Server_SB + "getDeviceInitiatedCardReadData.json";
	
	// following values are helpers for easy environment switching
	public final static int PROD = 1;
	public final static int SANDBOX = 2;
	public final static int LOCAL = 3;
	
	public static int currentConfig = SANDBOX;

	
	/**
	 * Should communication with RP Server use HTTPS
	 */
	public static boolean disbleHTTPS = false; // for local set to true

	/**
	 * Should communication with RP Srver use HTTP POST to submit request
	 */
	public static boolean usePOST = false;  // use HTTP POST

	/**
	 * Initialize Session factory
	 * <p>
	 * Sets URLs depending on currentConfig value
	 * 
	 */	
	private static void init() {
		if (currentConfig == LOCAL) {
			URL_DeviceInitiatedGetDevice = URL_DeviceInitiatedGetDevice_Local;
			URL_InitQuickCode = URL_InitQuickCode_Local;
			URL_VerifyQuickCode = URL_VerifyQuickCode_Local;
			URL_GetProvisioningAuthorizationCode = URL_GetProvisioningAuthorizationCode_Local;
			URL_VerifyJWT = URL_VerifyJWT_Local;
			URL_GetCardReadData = URL_GetCardReadData_Local;
		} else if( currentConfig == SANDBOX) {
			URL_DeviceInitiatedGetDevice = URL_DeviceInitiatedGetDevice_SB;
			URL_InitQuickCode = URL_InitQuickCode_SB;
			URL_VerifyQuickCode = URL_VerifyQuickCode_SB;
			URL_GetProvisioningAuthorizationCode = URL_GetProvisioningAuthorizationCode_SB;
			URL_VerifyJWT = URL_VerifyJWT_SB;
			URL_GetCardReadData = URL_GetCardReadData_SB;
		}
	}
	
	static {
		init();
	}
	
	
	/**
	 * Return SampleRPSession instance
	 *
	 * @param	url	URL for required RP App Server request
	 * @return	SampleRPSession instance
	 */	
	public static SampleRPSession createSampleRPSession(String url) {
		return new SampleRPSession(url, disbleHTTPS, usePOST);
	}
}
