package org.tapestry.myoscar.utils;

import java.util.Locale;

import org.oscarehr.myoscar.client.ws_manager.MyOscarLoggedInInfoInterface;

public class MyOscarCredentialsImpl implements MyOscarLoggedInInfoInterface {

	private String serverBaseUrl;
	private Long loggedInPersonId;
	private String loggedInPersonSecurityToken;
	private String loggedInSessionId;
	private Locale locale;
	
	public MyOscarCredentialsImpl(String serverBaseUrl, Long loggedInPersonId, String loggedInPersonSecurityToken, String loggedInSessionId, Locale locale)
	{
		this.serverBaseUrl = serverBaseUrl;
		this.loggedInPersonId = loggedInPersonId;
		this.loggedInPersonSecurityToken = loggedInPersonSecurityToken;
		this.loggedInSessionId = loggedInSessionId;
		this.locale = locale;
	}

	@Override
	public String getServerBaseUrl()
	{
		return serverBaseUrl;
	}

	@Override
	public Long getLoggedInPersonId()
	{
		return loggedInPersonId;
	}

	@Override
	public String getLoggedInPersonSecurityToken()
	{
		return loggedInPersonSecurityToken;
	}

	@Override
	public String getLoggedInSessionId()
	{
		return loggedInSessionId;
	}

	@Override
	public Locale getLocale()
	{
		return locale;
	}

}
