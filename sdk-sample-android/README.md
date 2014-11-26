SecureKey briidge.net Connect SDK Sample Application

This sample is using Sample RP Server deployed at [https://rpserver.securekey.com/samplerp/](https://rpserver.securekey.com/samplerp/). 
To build and run using local RP server:

1. Download Sample RP Server code from [https://github.com/securekey/connect-rp-server-sample](https://github.com/securekey/connect-rp-server-sample)

2. Build and deploy your server, write down the URL, for example "http://myserver:8080/samplerp".

3) In SDK Sample, modify _com.securekey.briidge.sample.rp.SampleRPSessionFactory_ with your server URLs (from step #2)

4) Build and deploy SDK Sample app


Please note that app will work with sample RP Server only if mobile app package name is **com.securekey.briidge.sample**.
Package name is configured at the RP level and when you are ready to move to the next step and have own RP provisioned, you will be able to specify package names for your organization. 

More information available at [http://developer.securekey.com](http://developer.securekey.com).


