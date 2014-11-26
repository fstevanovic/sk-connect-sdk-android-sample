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

import java.util.concurrent.CountDownLatch;

import android.content.Context;
import android.util.Log;

import com.securekey.briidge.Briidge;
import com.securekey.briidge.SKBriidgeFactory;
import com.securekey.sdk.sample.rp.SampleRPSession;
import com.securekey.sdk.sample.rp.SampleRPSessionFactory;

public class BriidgePlatformFactory {

	public static Briidge getBriidgePlatform(Context context) {
		final Briidge briidge = SKBriidgeFactory.getBriidgePlatform(context);
		
		if (!briidge.isProvisioned()) {
			final CountDownLatch latch = new CountDownLatch(1);
		
			new Thread() {
				public void run() {
					SampleRPSession rpSession = SampleRPSessionFactory.createSampleRPSession(SampleRPSessionFactory.URL_GetProvisioningAuthorizationCode);
					String provisioningAuthorizationCode = rpSession.getProvisioningAuthorizationCode();
					briidge.setProvisioningAuthorizationCode(provisioningAuthorizationCode);
					latch.countDown();
				}
			}.start();
			try {
				latch.await();
			} catch (InterruptedException e) {
				Log.i("SDKSample", "InterruptedException exception:" + e);
			}
			
		}
		
		return briidge;
	}
}
