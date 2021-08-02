package us.anant.dataflow.sample.geocodingprocessor;

import java.util.function.Function;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Value;
import org.apache.hc.client5.http.fluent.Request;
import org.apache.hc.client5.http.fluent.Content;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Configuration
public class GeocodingProcessor {


	// pull from application.properties
	@Value("${google.maps.api-key}")
	private String googleApiKey;

	/**
	 * - for why a Function is used for processor, see https://dataflow.spring.io/docs/feature-guides/streams/function-composition/#function-composition
	 *
	 */ 
	@Bean
	public Function<UsageDetail, UsageCoordinates> processGeocoding() {
		return usageDetail -> {
			UsageCoordinates usageCoordinates = new UsageCoordinates();
			usageCoordinates.setUserId(usageDetail.getUserId());

			// TODO set address using usage detail address

			try {
				String address = URLEncoder.encode(usageDetail.getAddress(), StandardCharsets.UTF_8.toString()); 

				String path = "https://maps.googleapis.com/maps/api/geocode/json?address=" + address + "&key=" + googleApiKey;
				Content resultContent = Request.get(path).execute().returnContent();

				String resultJson = resultContent.asString();
				JSONObject location = new JSONObject(resultJson)
					.getJSONArray("results")
					.getJSONObject(0)
					.getJSONObject("geometry")
					.getJSONObject("location");

				// TODO probably should make all the fields doubles, but not important for POC
				String lat = String.valueOf(location.getDouble("lat"));
				String lng = String.valueOf(location.getDouble("lng"));

				// google's lat
				usageCoordinates.setLatitude(lat);
				// google's long
				usageCoordinates.setLongitude(lng);

			} catch (Exception e) {
				e.printStackTrace();
			} 

			return usageCoordinates;
		};
	}
}
