package us.anant.dataflow.sample.geocodingprocessor;

public class UsageCoordinates {

	public UsageCoordinates() { }

	private String userId;

	private String longitude;

	private String latitude;

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getLongitude() {
		return longitude;
	}

	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}

	public String getLatitude() {
		return latitude;
	}

	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}

	public String toString() {
		// make sure lowercase for CQL
		return "{\"userid\": \""+this.getUserId() + "\", \"longitude\": \""+ this.getLongitude()+"\", \"latitude\": \"" + this.getLatitude()+ "\" }";
	}
}
