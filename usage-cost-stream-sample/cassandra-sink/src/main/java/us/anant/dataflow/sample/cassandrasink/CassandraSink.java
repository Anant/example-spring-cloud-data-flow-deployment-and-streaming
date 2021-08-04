package us.anant.dataflow.sample.cassandrasink;

import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.*;
import java.net.InetSocketAddress;

@Configuration
public class CassandraSink {

	private static final Logger logger = LoggerFactory.getLogger(CassandraSinkApplication.class);

	/**
	 * - for why a Consumer is used for Sink, see https://dataflow.spring.io/docs/feature-guides/streams/function-composition/#function-composition
	 *
	 */ 
	@Bean
	public Consumer<UsageCoordinates> process() {
		return usageCoordinates -> {
			logger.info(usageCoordinates.toString());

			try (CqlSession session = CqlSession.builder()
					// since application.conf is not working
					.addContactPoint(new InetSocketAddress("dataflow-dse-server", 9042))
					.withLocalDatacenter("dc1")
					.build()) {                     

				ResultSet rs = session.execute("select release_version from system.local");           
				Row row = rs.one();
				logger.info(row.getString("release_version"));                       

				ResultSet rs2 = session.execute("INSERT INTO clouddata.usage_details (userid, longitude, latitude) VALUES(" + usageCoordinates.getUserId() + ", " + usageCoordinates.getLongitude() + ", " + usageCoordinates.getLatitude() + ");");              

				logger.info("Finished");                                    
			}
		};
	}
}
