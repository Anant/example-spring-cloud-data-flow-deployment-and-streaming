package us.anant.dataflow.sample.geocodingprocessor;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

import org.springframework.boot.WebApplicationType;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cloud.stream.binder.test.InputDestination;
import org.springframework.cloud.stream.binder.test.OutputDestination;
import org.springframework.cloud.stream.binder.test.TestChannelBinderConfiguration;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.converter.CompositeMessageConverter;
import org.springframework.messaging.converter.MessageConverter;

import static org.assertj.core.api.Assertions.assertThat;

public class GeocodingProcessorApplicationTests {

	@Test
	public void contextLoads() {
	}

	@Test
	public void testGeocodingProcessor() {
		try (ConfigurableApplicationContext context = new SpringApplicationBuilder(
				TestChannelBinderConfiguration.getCompleteConfiguration(
						GeocodingProcessorApplication.class)).web(WebApplicationType.NONE)
				.run()) {

			InputDestination source = context.getBean(InputDestination.class);

			UsageDetail usageDetail = new UsageDetail();
			usageDetail.setUserId("user1");
			// google's address
			usageDetail.setAddress("1600 Amphitheatre Pkwy, Mountain View, CA 94043, USA");

			final MessageConverter converter = context.getBean(CompositeMessageConverter.class);
			Map<String, Object> headers = new HashMap<>();
			headers.put("contentType", "application/json");
			MessageHeaders messageHeaders = new MessageHeaders(headers);
			final Message<?> message = converter.toMessage(usageDetail, messageHeaders);

			source.send(message);

			OutputDestination target = context.getBean(OutputDestination.class);
			Message<byte[]> sourceMessage = target.receive(10000);

			final UsageCoordinates usageCoordinates = (UsageCoordinates) converter
					.fromMessage(sourceMessage, UsageCoordinates.class);

			// google's lat and lng
			assertThat(usageCoordinates.getLatitude()).isEqualTo("37.4240109");
			assertThat(usageCoordinates.getLongitude()).isEqualTo("-122.0867615");
		}
	}
}
