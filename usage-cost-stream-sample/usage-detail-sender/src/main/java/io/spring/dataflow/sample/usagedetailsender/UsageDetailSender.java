package io.spring.dataflow.sample.usagedetailsender;

import java.util.Random;
import java.util.function.Supplier;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UsageDetailSender {

	private String[] users = {"user1", "user2", "user3", "user4", "user5"};

	/**
	 * - for why a Supplier is used for Source, see https://dataflow.spring.io/docs/feature-guides/streams/function-composition/#function-composition
	 *
	 */ 
	@Bean
	public Supplier<UsageDetail> sendEvents() {
		return () -> {
			UsageDetail usageDetail = new UsageDetail();
			usageDetail.setUserId(this.users[new Random().nextInt(5)]);
			usageDetail.setDuration(new Random().nextInt(300));
			usageDetail.setData(new Random().nextInt(700));
			return usageDetail;
		};
	}
}
