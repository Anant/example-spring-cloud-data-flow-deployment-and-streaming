package io.spring.dataflow.sample.usagecostprocessor;

import java.util.function.Function;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Configuration
public class UsageCostProcessor {

	private static final Logger logger = LoggerFactory.getLogger(UsageCostProcessor.class);

	private double ratePerSecond = 0.1;

	private double ratePerMB = 0.05;

	/**
	 * - for why a Function is used for processor, see https://dataflow.spring.io/docs/feature-guides/streams/function-composition/#function-composition
	 *
	 */ 
	@Bean
	public Function<UsageDetail, UsageCostDetail> processUsageCost() {
		return usageDetail -> {
			UsageCostDetail usageCostDetail = new UsageCostDetail();
			usageCostDetail.setUserId(usageDetail.getUserId());
			usageCostDetail.setCallCost(usageDetail.getDuration() * this.ratePerSecond);
			usageCostDetail.setDataCost(usageDetail.getData() * this.ratePerMB);

			return usageCostDetail;
		};
	}
}
