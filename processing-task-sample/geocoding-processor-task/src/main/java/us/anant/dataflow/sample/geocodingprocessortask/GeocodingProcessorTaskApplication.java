package us.anant.dataflow.sample.geocodingprocessortask;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.cloud.task.configuration.EnableTask;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


// https://docs.spring.io/spring-cloud-task/docs/current/reference/html/#getting-started-writing-the-code

@EnableTask
@SpringBootApplication
public class GeocodingProcessorTaskApplication {

  private static final Log logger = LogFactory.getLog(GeocodingProcessorTaskApplication.class);

	@Bean
  public CommandLineRunner commandLineRunner() {
    return new GeocodingProcessorTaskCommandLineRunner();
  }

  public static void main(String[] args) {
    SpringApplication.run(GeocodingProcessorTaskApplication.class, args);
  }

  public static class GeocodingProcessorTaskCommandLineRunner implements CommandLineRunner {
		@Override
		public void run(String... strings) throws Exception {
			logger.info("Hello, World!");
		}
  }
}
