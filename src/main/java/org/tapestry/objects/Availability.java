package org.tapestry.objects;

public class Availability {
	int vId; //volunteer ID
	String vDisplayName;//volunteer display name with experience level
	String vPhone; //volunteer phone number
	String vEmail; //volunteer Email
	int pId; //partner ID	
	String pDisplayName; //partner display name with experience level
	String pPhone; //partner phone number
	String pEmail; //partner Email
	int patientId;
	String patientName;
	String matchedTime; //volunteer and partner matched availability
	
	public Availability(){
		
	}
	
	public String getvDisplayName() {
		return vDisplayName;
	}
	public void setvDisplayName(String vDisplayName) {
		this.vDisplayName = vDisplayName;
	}
	public String getvPhone() {
		return vPhone;
	}
	public void setvPhone(String vPhone) {
		this.vPhone = vPhone;
	}
	public String getvEmail() {
		return vEmail;
	}
	public void setvEmail(String vEmail) {
		this.vEmail = vEmail;
	}
	public String getpDisplayName() {
		return pDisplayName;
	}
	public void setpDisplayName(String pDisplayName) {
		this.pDisplayName = pDisplayName;
	}
	public String getpPhone() {
		return pPhone;
	}
	public void setpPhone(String pPhone) {
		this.pPhone = pPhone;
	}
	public String getpEmail() {
		return pEmail;
	}
	public void setpEmail(String pEmail) {
		this.pEmail = pEmail;
	}
	public String getMatchedTime() {
		return matchedTime;
	}
	public void setMatchedTime(String matchedTime) {
		this.matchedTime = matchedTime;
	}
	public int getvId() {
		return vId;
	}

	public void setvId(int vId) {
		this.vId = vId;
	}

	public int getpId() {
		return pId;
	}

	public void setpId(int pId) {
		this.pId = pId;
	}
	
	public int getPatientId() {
		return patientId;
	}

	public void setPatientId(int patientId) {
		this.patientId = patientId;
	}

	public String getPatientName() {
		return patientName;
	}

	public void setPatientName(String patientName) {
		this.patientName = patientName;
	}

}
