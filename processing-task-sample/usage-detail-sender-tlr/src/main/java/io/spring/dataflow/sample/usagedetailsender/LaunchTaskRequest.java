package io.spring.dataflow.sample.usagedetailsender;

/**
 * takes props of a usagedetail instance and but makes request as output instead
 *
 */ 
public class LaunchTaskRequest {

	public LaunchTaskRequest() { }

	// job task name
	private String name;

	private long duration;

	private long data;

	private String address;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	// public long getDuration() {
	// 	return duration;
	// }

	// public void setDuration(long duration) {
	// 	this.duration = duration;
	// }

	// public long getData() {
	// 	return data;
	// }

	// public void setData(long data) {
	// 	this.data = data;
	// }

	// public String getAddress() {
	// 	return address;
	// }

	// public void setAddress(String address) {
	// 	this.address = address;
	// }

	public String toString() {
		// make sure lowercase for CQL
		// NOTE might not be what gets written to kafka after all
		//return "{\"userid\": \""+this.getName() + "\", \"longitude\": \""+ this.getLongitude()+"\", \"latitude\": \"" + this.getLatitude()+ "\" }";

		// for testing - a simple job request
		return "{\"name\": \""+ "timestamp-task" + "\"" + "}";
	}
}
