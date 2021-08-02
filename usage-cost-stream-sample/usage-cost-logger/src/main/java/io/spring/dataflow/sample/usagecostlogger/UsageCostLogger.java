package io.spring.dataflow.sample.usagecostlogger;

import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UsageCostLogger {

	private static final Logger logger = LoggerFactory.getLogger(UsageCostLoggerApplication.class);

	/**
	 * - for why a Consumer is used for Sink, see https://dataflow.spring.io/docs/feature-guides/streams/function-composition/#function-composition
	 *
	 */ 
	@Bean
	public Consumer<UsageCostDetail> process() {
		return usageCostDetail -> {
			logger.info(usageCostDetail.toString());
		};
	}
}
