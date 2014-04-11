package org.tapestry.controller;

import java.io.IOException;
import java.util.Map;
import java.util.HashMap;
import java.util.Properties;

import javax.servlet.http.HttpSession;

import org.springframework.core.io.ClassPathResource;
import org.springframework.security.web.servletapi.SecurityContextHolderAwareRequestWrapper;
import org.tapestry.dao.NarrativeDao;
import org.tapestry.dao.UserDao;
import org.tapestry.objects.User;
import org.yaml.snakeyaml.Yaml;

public class Utils {
	
		static String DB = "";
		static String UN = "";
		static String PW = "";
		static String mailHost = "";
		static String mailUser = "";
		static String mailPassword = "";
		static String mailPort = "";
		static String useTLS = "";
		static String useAuth = "";
		static String mailAddress="";
		
		static NarrativeDao narrativeDao = null;
   		static UserDao userDao = null;
   		
		
	public static void setDatabaseConfig(){   	
   		
		try{
			ClassPathResource dbConfigFile = new ClassPathResource("tapestry.yaml");
			Yaml yaml = new Yaml();
			Map<String, String> config = (Map<String, String>) yaml.load(dbConfigFile.getInputStream());
			DB = config.get("url");
			UN = config.get("username");
			PW = config.get("password");
			mailHost = config.get("mailHost");
			mailUser = config.get("mailUser");
			mailPassword = config.get("mailPassword");
			mailAddress = config.get("mailFrom");
			mailPort = config.get("mailPort");
			useTLS = config.get("mailUsesTLS");
			useAuth = config.get("mailRequiresAuth");
		} catch (IOException e) {
			System.out.println("Error: reading from config file");
			System.out.println(e.toString());
		}
		
		Properties props = System.getProperties();
		
		props.setProperty("db", DB);
		props.setProperty("un", UN);
		props.setProperty("pwd", PW);
		
		
		props.setProperty("mail.smtp.host", mailHost);
		props.setProperty("mail.smtp.socketFactory.port", mailPort);
		props.setProperty("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
		props.setProperty("mail.smtp.auth", useAuth);
		props.setProperty("mail.smtp.starttls.enable", useTLS);
		props.setProperty("mail.user", mailUser);
		props.setProperty("mail.password", mailPassword);
		
		}
	
	public static String timeFormat(String time){
		String hour = null;
		int iHour = 0;
		
		hour = time.substring(0,2);
		iHour = Integer.parseInt(hour);
		
		if (iHour >= 12)
			time = time + " PM";
		else 
			time = time + " AM";
		
		return time;
	}
	
	public static int getLoggedInUserId(HttpSession session, SecurityContextHolderAwareRequestWrapper request ){				
		String name = null;		
		int loggedInUserId = 0;
		String strLoggedInUserId = null;
		User loggedInUser = null;
		//check if loggedInUserId is in the session
		if (session.getAttribute("loggedInUserId") != null){
			//get loggedInUserId from session
			strLoggedInUserId = session.getAttribute("loggedInUserId").toString();
			loggedInUserId = Integer.parseInt(strLoggedInUserId);
			
		}//get loggedInUserId from request
		else if (request.getUserPrincipal() != null){			
			name = request.getUserPrincipal().getName();	
					
			if (name != null){				
				userDao = (UserDao)session.getAttribute("userDao");
				loggedInUser = userDao.getUserByUsername(name);
				loggedInUserId = loggedInUser.getUserID();
						
				session.setAttribute("loggedInUserId", String.valueOf(loggedInUserId));			
			}
		}
		
		return loggedInUserId;
	}
	
	public static String getStrLoggedInUserId(HttpSession session, SecurityContextHolderAwareRequestWrapper request){
		int loggedInUserId = 0;
		loggedInUserId = getLoggedInUserId(session, request);
		
		return String.valueOf(loggedInUserId);
				
	}	
	
	public static boolean isNullOrEmpty(String str){
		if (str != null && !str.equals(""))
			return false;
		else
			return true;
		
	}
	
	
	
}
