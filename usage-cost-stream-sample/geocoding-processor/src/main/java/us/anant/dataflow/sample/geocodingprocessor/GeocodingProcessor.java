package us.anant.dataflow.sample.geocodingprocessor;

import java.util.function.Function;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Value;

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

			// TODO get from external API
			// https://maps.googleapis.com/maps/api/geocode/json?address=1600+Amphitheatre+Parkway,+Mountain+View,+CA&key=${googleApiKey}


			usageCoordinates.setLatitude("37.4240109");
			// google's long
			usageCoordinates.setLongitude("-122.0867615");

			return usageCoordinates;
		};
	}
}
