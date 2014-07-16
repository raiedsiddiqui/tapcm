package org.tapestry.report;

import java.util.Map;
import java.util.List;
import java.util.Iterator;

import org.tapestry.controller.Utils;

public class AlertManager {
	public static List<String> getNutritionAlerts(int scores, List<String> alerts, List<String> qList){
		String ans ="";
		
		if (scores < 50)
			alerts.add(AlertsInReport.NUTRITION_ALERT1);
				
		//weight alert-- the first question in nutrition survey
		ans = qList.get(1);
		ans = ans.trim();		
		
		if (!Utils.isNullOrEmpty(ans)) {
			if (ans.equals("2"))				
				alerts.add(AlertsInReport.NUTRITION_ALERT2A);			
			else if (ans.equals("3"))
				alerts.add(AlertsInReport.NUTRITION_ALERT2B);
			else if (ans.equals("6"))
				alerts.add(AlertsInReport.NUTRITION_ALERT2C);
		}
		
		//meal alert -- forth question in nutrition survey
		ans = qList.get(4);
		ans = ans.trim();
		if (!Utils.isNullOrEmpty(ans) && ans.equals("4"))
			alerts.add(AlertsInReport.NUTRITION_ALERT3);
		
		//Appetitive alert --- sixth question in nutrition survey
		ans = qList.get(6);
		ans = ans.trim();
		if (!Utils.isNullOrEmpty(ans) && ans.equals("4"))
			alerts.add(AlertsInReport.NUTRITION_ALERT4);
		
		//cough, choke and pain alert --- ninth question in nutrition survey
		ans = qList.get(11);
		ans = ans.trim();
		if (!Utils.isNullOrEmpty(ans) && ans.equals("4"))
			alerts.add(AlertsInReport.NUTRITION_ALERT5);	
		
		return alerts;
	}
	
	public static List<String> getMobilityAlerts(Map<String, String> mMobilitySurvey, List<String> alerts){		
		String key = "";
		String value = "";
		String a2aValue = "";
		String a2bValue = ""; 
		String a3aValue = "";
		String a3bValue ="";
		String a4aValue = ""; 
		String a4bValue = "";
		
		Iterator iterator = mMobilitySurvey.entrySet().iterator();
		while (iterator.hasNext()) {
			Map.Entry mapEntry = (Map.Entry) iterator.next();
			key = mapEntry.getKey().toString();
			value = mapEntry.getValue().toString();
			
			if ("a2a".equalsIgnoreCase(key))
				a2aValue = value;
			
			if ("a2b".equalsIgnoreCase(key))
				a2bValue = value;
			
			if ("a3a".equalsIgnoreCase(key))
				a3aValue = value;
			
			if ("a3b".equalsIgnoreCase(key))
				a3bValue = value;
			
			if ("a4a".equalsIgnoreCase(key))
				a4aValue = value;
			
			if ("a4b".equalsIgnoreCase(key))
				a4bValue = value;			
		}
			
		if (("1".equals(a2aValue))&&(("2".equals(a2bValue))||("3".equals(a2bValue))||("4".equals(a2bValue))||("5".equals(a2bValue))))
			alerts.add(AlertsInReport.MOBILITY_WALKING_ALERT1);
		
		if ("2".equals(a2aValue))
			alerts.add(AlertsInReport.MOBILITY_WALKING_ALERT2);
		
		if (("3".equals(a2aValue))||("4".equals(a2aValue))||("5".equals(a2aValue)))
			alerts.add(AlertsInReport.MOBILITY_WALKING_ALERT3);
		
		if (("1".equals(a3aValue))&&(("2".equals(a3bValue))||("3".equals(a3bValue))||("4".equals(a3bValue))||("5".equals(a3bValue))
				||("6".equals(a3bValue))))
			alerts.add(AlertsInReport.MOBILITY_WALKING_ALERT4);
		
		if ("2".equals(a3aValue))
			alerts.add(AlertsInReport.MOBILITY_WALKING_ALERT5);
		
		if (("3".equals(a3aValue))||("4".equals(a3aValue))||("5".equals(a3aValue)))
			alerts.add(AlertsInReport.MOBILITY_WALKING_ALERT6);
		
		if (("1".equals(a4aValue))&&(("2".equals(a4bValue))||("3".equals(a4bValue))||("4".equals(a4bValue))||("5".equals(a4bValue))
				||("6".equals(a4bValue))||("7".equals(a4bValue))))
			alerts.add(AlertsInReport.MOBILITY_CLIMBING_ALERT1);
		
		if ("2".equals(a4aValue))
			alerts.add(AlertsInReport.MOBILITY_CLIMBING_ALERT2);
		
		if (("3".equals(a4aValue))||("4".equals(a4aValue))||("5".equals(a4aValue)))
			alerts.add(AlertsInReport.MOBILITY_CLIMBING_ALERT3);
				
		return alerts;
	}
	
	public static List<String> getGeneralHealthyAlerts(int score, List<String> alerts){
		if (score >= 7)
			alerts.add(AlertsInReport.EDMONTON_FRAIL_SCALE_ALERT);
		
		return alerts;
	}
	
	public static List<String> getSocialLifeAlerts(int score, List<String> alerts){
		if (score < 10)
			alerts.add(AlertsInReport.DUKE_INDEX_OF_SOCIAL_SUPPORT_ALERT);
		
		return alerts;
	}
}
