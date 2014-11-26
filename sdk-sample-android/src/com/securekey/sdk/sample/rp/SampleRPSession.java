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

package com.securekey.sdk.sample.rp;

import java.util.Hashtable;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.me.JSONObject;

import android.util.Log;

/**
 * Communication with the SampleRP App Server
 *  <p></p>
 *  SampleRPSession object implements request and response methods to invoke SampleRP App Server actions.
 *  
 * @author	Fedja Stevanovic (fedja.stevanovic@securekey.com)
 * @version 3.1.0 2014-11-21
 * 
 */
public class SampleRPSession {

	private static final String PARAM_JWT_ID 	= "jwt";
	private static final String PARAM_TXN_ID 	= "txnId";
	private static final String PARAM_USER_ID 	= "userId";
	private static final String STATUS_ERROR 	= "error";

	/**
	 * RP App Server URL
	 */
	private String serverAddress_;

	/**
	 * Should use HTTPS to send request? 
	 * NOT USED in this sample code!!!!!
	 */
	@SuppressWarnings("unused")
	private boolean disableSSL_;

	/**
	 * Should use HTTP POST to send request?
	 */
	private boolean usePOST_;
	
	/**
	 * Constructor for session object
	 * <p></p>
	 * Create SampleRPSession object for communication with the RP Server
	 * 
	 * @param	serverAddress	RP Server URL
	 * @param	disableSSL		use HTTPS to talk to RP Server
	 * @param	usePOST			submit request using HTTP POST (if false uses GET)
	 * 
	 */		
	public SampleRPSession(String serverAddress, boolean disableSSL, boolean usePOST) {
		serverAddress_ = serverAddress;
		disableSSL_ = disableSSL;
		usePOST_ = usePOST;
	}

	
	public String getProvisioningAuthorizationCode() {
		return sendMessage(null);
		
	}
	
	/**
	 * Complete authenticate device flow 
	 * <p></p>
	 * Calls RP Server to issue device-initiated-get-device.action to briidge.net service and verify device authentication info
	 * 
	 * @param	txnId	transactionID received from a call to briidge.authenticateDevice
	 * @return	JSONObject as received from briidge.net by RP Server
	 * 
	 */		
	public JSONObject authenticateDevice(String txnId) {

		String request = createAuthenticateDeviceRequest(txnId);
		JSONObject response = sendMessageExpectJson(request);
		
		return response;
	}
	
	/**
	 * Verify JWT on RP side
	 * <p></p>
	 * Calls RP Server to verify the JWT content
	 * 
	 * @param	jwt	transactionID received from a call to briidge.authenticateDevice
	 * @return	JSONObject as received from briidge.net by RP Server
	 * 
	 */		
	public JSONObject verifyJWT(String jwt) {

		String request = createVerifyJWTRequest(jwt);
		JSONObject response = sendMessageExpectJson(request);
		
		return response;
	}

	/**
	 * Complete read card flow 
	 * <p></p>
	 * Calls RP Server to retrieve card data
	 * 
	 * @param	txnId	transactionID received after read card was completed with briidge
	 * @return	JSONObject as received from briidge.net by RP Server
	 * 
	 */		
	public JSONObject getCardReadData(String txnId) {

		String request = createAuthenticateDeviceRequest(txnId);
		JSONObject response = sendMessageExpectJson(request);
		
		return response;
	}
	
	
	/**
	 * Initialize user quickcode setup on RP Server 
	 * <p></p>
	 * Calls RP Server to issue init-set-passcode.action to briidge.net service to verify device and user association
	 * 
	 * @param	userId	userId supplied by the RP
	 * @param	txnId	transactionID received from a call to briidge.authenticateDevice
	 * @return	JSONObject as received from briidge.net by RP Server
	 * 
	 */		
	public JSONObject initMobileQuickCode(String userId, String txnId) {

		String request = createRegisterRequest(userId, txnId);
		JSONObject response = sendMessageExpectJson(request);
		
		return response;
	}
	
	/**
	 * Verify user quickcode on RP Server 
	 * <p></p>
	 * Calls RP Server to issue verify-passcode-data.action to briidge.net service to verify user quick code
	 * 
	 * @param	txnId	transactionID received from a call to briidge.verifyQuickCode
	 * @return	JSONObject as received from briidge.net by RP Server
	 * 
	 */		
	public JSONObject verifyQuickCode(String txnId) {

		String request = createVerifyRequest(txnId);
		JSONObject response = sendMessageExpectJson(request);
		return response;
	}
	
	/**
	 * Synchronous HTTP(S) request/response to RP Server 
	 * <p></p>
	 * Sends request to RP Server, waits for response and sends response back to caller
	 * 
	 * @param	request		payload (HTTP query parameters)
	 * @return	JSONObject as received from briidge.net by RP Server
	 * 
	 */		
	private JSONObject sendMessageExpectJson(final String request) {
		
		
		String result = sendMessage(request);
		JSONObject resultJson = null;
		
		
		try {
				resultJson = new JSONObject(result);
			
		} catch (Exception e) {
			Hashtable<String,String> responseData = new Hashtable<String, String>();
			responseData.put(STATUS_ERROR, "Network Error");
			resultJson = new JSONObject(responseData);
		}
		
		return resultJson;
		
	}

	
	/**
	 * Synchronous HTTP(S) request/response to RP Server 
	 * <p></p>
	 * Sends request to RP Server, waits for response and sends response back to caller
	 * 
	 * @param	request		payload (HTTP query parameters), optional, it can be null
	 * @return	String as received from briidge.net by RP Server, it can be JSONObject
	 * 
	 */	
	private String sendMessage(final String request) {
		
		String result = null;
		HttpClient httpClient = new DefaultHttpClient();
		
		try {
			if( usePOST_ ) {
				
				HttpPost httpPost = new HttpPost( serverAddress_ );
				if(request != null) {
					httpPost.setEntity( new StringEntity( request ) );
				}
				
				HttpResponse response = httpClient.execute(httpPost);
				
				result = EntityUtils.toString(response.getEntity());
				
				
			} else {
				String url = request == null ? serverAddress_ : serverAddress_ + "?" + request;
				Log.i("SDKSample", "url:" + url);
				HttpGet httpGet = new HttpGet( url );
				
				HttpResponse response = httpClient.execute(httpGet);
				
				Log.i("SDKSample", "response status:" + response.getStatusLine().getStatusCode());
				result = EntityUtils.toString(response.getEntity());
				Log.i("SDKSample", "result:" + result);
				
				
			}
		} catch (Exception e) {
			Log.i("SDKSample", "exception:" + e);
			result = null;
		}
		
		return result;
		
	}
	
	/**
	 * Helper to create request payload to verify JWT (query string) 
	 * 
	 * @param	jwt		payload parameter
	 * @return	HTTP Query String 
	 * 
	 */		
	private String createVerifyJWTRequest(String jwt) {
		String request = PARAM_JWT_ID + "=" + jwt;
		return request;
	}	
	
	/**
	 * Helper to create request payload (query string) 
	 * 
	 * @param	txnId		payload parameter
	 * @return	HTTP Query String 
	 * 
	 */		
	private String createAuthenticateDeviceRequest(String txnId) {
		String request = PARAM_TXN_ID + "=" + txnId;
		return request;
	}	

	/**
	 * Helper to create request payload (query string) 
	 * 
	 * @param	userId		payload parameter
	 * @param	txnId		payload parameter
	 * @return	HTTP Query String 
	 * 
	 */		
	private String createRegisterRequest(String userId, String txnId) {
		String request = PARAM_USER_ID + "=" + userId + "&" + PARAM_TXN_ID + "=" + txnId;
		return request;
	}
	
	/**
	 * Helper to create request payload (query string) 
	 * 
	 * @param	txnId		payload parameter
	 * @return	HTTP Query String 
	 * 
	 */		
	private String createVerifyRequest(String txnId) {
		String request = PARAM_TXN_ID + "=" + txnId;
		return request;
	}
}
