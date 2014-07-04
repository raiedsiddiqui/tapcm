package org.tapestry.myoscar.utils;

import java.io.IOException;
import java.util.Locale;
import java.util.Map;
import java.util.HashMap;

import org.oscarehr.myoscar.client.ws_manager.AccountManager;
import org.oscarehr.myoscar_server.ws.LoginResultTransfer3;
import org.oscarehr.myoscar_server.ws.PersonTransfer3;
import org.oscarehr.util.MiscUtils;
import org.springframework.core.io.ClassPathResource;
import org.yaml.snakeyaml.Yaml;

public class ClientManager {
	private static final String MYOSCAR_URL = "myoscar_url";
	private static final String MYOSCAR_USERNAME = "myoscar_username";
	private static final String MYOSCAR_PASSWORD = "myoscar_password";
	private static ClassPathResource dbConfigFile;
	private static Map<String, String> config;
	private static Yaml yaml;
	
	public static PersonTransfer3 getClientByUsername(String userName) throws Exception
	{			
		Map<String, String> intiMyOscarWS = readMyOscarWSConfig();
		
		MiscUtils.setJvmDefaultSSLSocketFactoryAllowAllCertificates();
		
		String serverUrl=intiMyOscarWS.get(MYOSCAR_URL);	
		String user=intiMyOscarWS.get(MYOSCAR_USERNAME);	
		String password=intiMyOscarWS.get(MYOSCAR_PASSWORD);	
		
		LoginResultTransfer3 loginResultTransfer = AccountManager.login(serverUrl, user, password);		
		
		MyOscarCredentialsImpl credentials=new MyOscarCredentialsImpl(serverUrl, loginResultTransfer.getPerson().getId(), 
				loginResultTransfer.getSecurityTokenKey(), "fake sessionId, not from browser", Locale.ENGLISH);
		
		
		//--- this will retrieve an account and print some of the patients demographic information ---
		
		PersonTransfer3 client = AccountManager.getPerson(credentials, userName);	
		
		return client;
	}
	
	private static Map<String, String> readMyOscarWSConfig()
	{
		Map<String, String> myOscarWSInit = new HashMap<String, String>();
		try{
			dbConfigFile = new ClassPathResource("tapestry.yaml");
			yaml = new Yaml();
			config = (Map<String, String>) yaml.load(dbConfigFile.getInputStream());
			
			String myOscarServerBaseUrl = config.get(MYOSCAR_URL);			
			String myOscarUserName = config.get(MYOSCAR_USERNAME);
			String myOscarPwd = config.get(MYOSCAR_PASSWORD);
			
			myOscarWSInit.put(MYOSCAR_URL, myOscarServerBaseUrl);
			myOscarWSInit.put(MYOSCAR_USERNAME, myOscarUserName);
			myOscarWSInit.put(MYOSCAR_PASSWORD, myOscarPwd);
			
		} catch (IOException e) {
			System.out.println("Error reading from config file");
			System.out.println(e.toString());
		}
		
		return myOscarWSInit;
	}

}
