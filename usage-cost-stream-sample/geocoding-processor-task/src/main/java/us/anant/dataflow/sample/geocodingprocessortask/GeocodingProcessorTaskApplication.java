package us.anant.dataflow.sample.geocodingprocessortask;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.cloud.task.configuration.EnableTask;


// https://docs.spring.io/spring-cloud-task/docs/current/reference/html/#getting-started-writing-the-code

@EnableTask
@SpringBootApplication
public class GeocodingProcessorTaskApplication {

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
			System.out.println("Hello, World!");
		}
  }
}
