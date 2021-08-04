# Deploy Spring Cloud Data Flow locally using Docker-compose

Following this guide: https://dataflow.spring.io/docs/installation/local/docker/

[Note about "Docker Out of Docker" (DooD) approach](https://dataflow.spring.io/docs/installation/local/docker/#docker-stream--task-applications) 

## Spin up Infrastructure
Start using: 
```
DATAFLOW_VERSION=2.8.1 SKIPPER_VERSION=2.7.1 \
docker-compose up -d
```

- Check out dashboard here: http://localhost:9393/dashboard/
- Access the CLI using:
    ```
    docker exec -it dataflow-server java -jar shell.jar
    ```

## Run using Spring Cloud Data Flow

### Build all the jars
With tests:
```
../../usage-cost-stream-sample/mvnw clean package
```

### Copy jars to SCDF Skipper Server
You can make access jars from several different protocols (maven repo, HTTP, local file system, and docker image). [Find out more here](https://docs.spring.io/spring-cloud-dataflow/docs/current/reference/htmlsingle/#spring-cloud-dataflow-register-stream-apps)
- if want the jars local in the docker container, make sure to put in the skipper container, not the scdf server
- NOTE For one way to use file system uris, see here for how they expose the project using docker in the guide: https://dataflow.spring.io/docs/installation/local/docker/#accessing-the-host-file-system
- NOTE for a way to use local maven jars, see this guide: https://dataflow.spring.io/docs/installation/local/docker/#maven-local-repository-mounting

For this demo, we will use docker uri, but since the jars are in the filepath of the scdf skipper server itself, it's just as easy to use file uri. 
```
cd ../../usage-cost-stream-sample && \
export skipper_server_name=skipper && \
docker cp usage-cost-logger/target/usage-cost-logger-0.0.1-SNAPSHOT.jar $skipper_server_name:/home && \
docker cp usage-cost-processor/target/usage-cost-processor-0.0.1-SNAPSHOT.jar $skipper_server_name:/home && \
docker cp usage-detail-sender/target/usage-detail-sender-0.0.1-SNAPSHOT.jar $skipper_server_name:/home
```

You can register using the SCDF Dashboard if you're into GUIs, or run the script below. 

#### If you can get java in dataflow-server to work...
For some reason, java wasn't installed on the image I pulled. But according to their docs, you should be able to do this:

```
# start SCDF CLI
docker exec -it dataflow-server java -jar shell.jar

# register the apps
...
```

#### Download CLI and run manually
- Get link from [here](https://dataflow.spring.io/docs/installation/local/manual/#downloading-server-jars) or [here](https://dataflow.spring.io/docs/installation/local/docker/#using-the-shell)


NOTE as of 8/2/21, this link returning a 404 if using link provided in docs, but this link works (use "release" instead of "snapshot" in the path)
    ```
    # download CLI
    wget https://repo.spring.io/release/org/springframework/cloud/spring-cloud-dataflow-shell/2.8.1/spring-cloud-dataflow-shell-2.8.1.jar

    # run 
    java -jar spring-cloud-dataflow-shell-2.8.1.jar

    app register --name usage-detail-sender --type source --uri file:///home/usage-detail-sender-0.0.1-SNAPSHOT.jar
    app register --name usage-cost-processor --type processor --uri file:///home/usage-cost-processor-0.0.1-SNAPSHOT.jar
    app register --name usage-cost-logger --type sink --uri file:///home/usage-cost-logger-0.0.1-SNAPSHOT.jar
    ```

If your SCDF docker container is up and running already, this should connect automatically.

#### In GUI
1. Navigate to "Add Applications" View: http://localhost:9393/dashboard/#/apps/add
2. Click on dropdown to "Import application coordinates from a properties file"
3. Paste this in:

```
source.usage-detail-sender=file:///home/usage-detail-sender-0.0.1-SNAPSHOT.jar
processor.usage-cost-processor=file:///home/usage-cost-processor-0.0.1-SNAPSHOT.jar
sink.usage-cost-logger=file:///home/usage-cost-logger-0.0.1-SNAPSHOT.jar
```


You can confirm they were registered by viewing in the Apps index: http://localhost:9393/dashboard/#/apps


## Compose a Stream
1. Go to stream composition view: http://localhost:9393/dashboard/#/streams/list/create
2. String the apps together using the drag and drop feature, or insert this into the text box:
  - Set the ports for each app, since they are all local. [See this guide for more details](https://dataflow.spring.io/docs/stream-developer-guides/streams/data-flow-stream/#local). Just insert this in the "Freetext" tab:
    ```
    usage-detail-sender --server.port=9000 | usage-cost-processor --server.port=9001 | usage-cost-logger --server.port=9002
    ```
    
 - Note that these could also be set in the next step, using args such as `app.usage-detail-sender.server.port=9000`

3. Click "Create Stream(s)"
4. Name stream "usage-cost-logger-stream"
- You now have an undeployed stream.

5. Deploy by clicking the three dots on left of your stream in the [streams index](http://localhost:9393/dashboard/#/streams/list) and click "Deploy"
6. Set your deployment properties

- Then click "Deploy the Stream"


### Check the output of a stream
1. Find the path to the stdout in the stream runtimes list
2. Follow that path within the skipper container:
```
# e.g., 
export scdf_stream_stdout_path=/tmp/1627881451004/time-log.log-v1/stdout_0.log
docker exec -it skipper tail -f $scdf_stream_stdout_path
```

## Add Geolocation Processing as well
1. [Get google maps API key](https://developers.google.com/maps/documentation/geocoding/get-api-key)
- setup a GCP app
- Add Geocoding API
- Under "Credentials" Add an API Key by clicking "CREATE CREDENTIALS" Button and then "API Key"
- Optionally, set restrictions/limitations on the API key for security

2. Set API key under application.properties
    ```
    cp geocoding-processor/src/main/resources/application.properties.example geocoding-processor/src/main/resources/application.properties
    vim geocoding-processor/src/main/resources/application.properties
    # then add the api key under google.maps.api-key
    ```

3. Build and deploy jar
- If already built, need to build again to get the application.properties file into the target

    ```
    export skipper_server_name=skipper && \
    cd ../../usage-cost-stream-sample && \
    ./mvnw clean package && \
    docker cp ./geocoding-processor/target/geocoding-processor-0.0.1-SNAPSHOT.jar $skipper_server_name:/home
    ```

4. Register the geocoding processor app 
- Navigate to "Add Applications" View: http://localhost:9393/dashboard/#/apps/add
- Click on dropdown to "Import application coordinates from a properties file"
- Paste this in:
    ```
    processor.geocoding-processor=file:///home/geocoding-processor-0.0.1-SNAPSHOT.jar
    ```

Or in the CLI:
		```
    app register --name geocoding-processor --type processor --uri file:///home/geocoding-processor-0.0.1-SNAPSHOT.jar
		# expected response:
		# > Successfully registered application 'processor:geocoding-processor'
		```

5. Compose a new stream with this processor. For now, just log as a sink.
- You can use this stream definition
```
usage-detail-sender | geocoding-processor | log
```
- Name it `log-geocoding-processor`

6. Confirm that it's writing to a Kafka topic
- Check that kafka topic is made
    ```
    docker exec dataflow-kafka kafka-topics --zookeeper zookeeper:2181 --list
    ```
- Consume from that topic
    ```
    export scdf_topic_name=log-geocoding-processor.geocoding-processor
    docker exec dataflow-kafka kafka-console-consumer --bootstrap-server localhost:9092 --topic $scdf_topic_name
    ```
    
    Example results: 
    ```
    {"userId":"user3","longitude":"1.7282636","latitude":"52.6139407"}
    {"userId":"user2","longitude":"-0.0923534","latitude":"50.9932155"}
    {"userId":"user5","longitude":"-0.452421","latitude":"51.3720857"}
    {"userId":"user1","longitude":"-0.2613355","latitude":"51.5778763"}
    {"userId":"user1","longitude":"-1.5688602","latitude":"55.0042614"}
    {"userId":"user2","longitude":"1.7282636","latitude":"52.6139407"}
    {"userId":"user4","longitude":"-1.5619722","latitude":"54.61625960000001"}
    {"userId":"user5","longitude":"-0.0320057","latitude":"51.8169166"}
    {"userId":"user4","longitude":"-0.2054393","latitude":"51.4833025"}
    {"userId":"user5","longitude":"1.2873511","latitude":"52.6387799"}
    {"userId":"user5","longitude":"-3.818027499999999","latitude":"52.061626"}
    ```

## Sink directly from Kafka topic into Cassandra 
- basically, instead of log sink, use cassandra sink
    One example: https://docs.spring.io/spring-cloud-dataflow-samples/docs/current/reference/htmlsingle/#http-cassandra-local
    Reference docs: 
    * https://dataflow.spring.io/docs/applications/pre-packaged/
    * https://docs.spring.io/stream-applications/docs/2020.0.2/reference/html/#spring-cloud-stream-modules-cassandra-sink

1. Setup Schema in CQL

```
CREATE KEYSPACE clouddata WITH REPLICATION = { 'class' : 'SimpleStrategy', 'replication_factor': '1' } AND DURABLE_WRITES = true;
CREATE TABLE clouddata.usage_detail  (
    userId      TEXT PRIMARY KEY,
    latitude    TEXT,
    longitude    TEXT
);
```


2. Create the stream

The default Cassandra sink app expects json (see [reference](https://docs.spring.io/stream-applications/docs/2020.0.2/reference/html/#spring-cloud-stream-modules-cassandra-sink)) so we need to send that in our processor.

    ```
    # for example, in the CLI shell:
    stream create geocoding-stream-to-cassandra --definition "usage-detail-sender | geocoding-processor | cassandra --spring.data.cassandra.contact-points=dataflow-dse-server:9042 --spring.data.cassandra.local-datacenter=dc1 --keyspace=clouddata --ingestQuery='insert into usage_detail (userId, latitude, longitude) values (?, ?, ?)'" --deploy

    # should get response: 
    # > Created new stream 'geocoding-task-from-stream'
    # > Deployment request has been sent
    ```

TODO get it working...right now, not writing and silently. There is no clear indication why sink doesn't work. 

# Monitoring

TODO

Try this: https://dataflow.spring.io/docs/installation/local/docker-customize/#influxdb--grafana

# Interacting with Kafka

## List topics
You can reference the zookeeper using the hostname and port assigned by docker-compose network (ie "zookeeper:2181")

```
docker exec dataflow-kafka kafka-topics --zookeeper zookeeper:2181 --list
```

## Create one-off consumer
```
export scdf_topic_name_1=usage-cost-logger-stream.usage-detail-sender
docker exec dataflow-kafka kafka-console-consumer --bootstrap-server localhost:9092 --topic $scdf_topic_name_1
```



# Debugging
## Debugging Tips
### Server logs
```
docker logs -f dataflow-server
```

### Skipper logs
```
docker logs -f skipper
```

Or, for a specific application, find the stderr or stdout log file (e.g., in the GUI at http://localhost:9393/dashboard/#/streams/runtime).
Then tail that file specifically:

```
docker exec -it skipper tail -f /tmp/1627882658386/usage-cost-logger-stream.usage-cost-logger-v1/stderr_0.log
```

### Runtime GUI

E.g., for a stream called "usage-cost-logger-stream":

http://localhost:9393/dashboard/#/streams/list/usage-cost-logger-stream

You can then click "View Log" button for each app to see what failed.

### HTTP Requests to get more info
The GUI doesn't always give good error messages, but if you look at the actual returned parameters, you get more helpful info. 

[See also the REST API docs](https://docs.spring.io/spring-cloud-dataflow/docs/current/reference/htmlsingle/#api-guide).


#### For why a stream is failing:
```
curl http://localhost:9393/streams/definitions?page=0&size=20&sort=name,ASC

# or parse with jq to get just the status Description for your first stream (index 0)
curl "http://localhost:9393/streams/definitions?page=0&size=20&sort=name,ASC" | jq ._embedded.streamDefinitionResourceList[0].statusDescription

# or to get a certain stream rather than getting all and then just showing first result:
curl "http://localhost:9393/streams/definitions/usage-cost-logger" | jq .
```

#### Validate the apps in a given stream
[API Docs](https://docs.spring.io/spring-cloud-dataflow/docs/current/reference/htmlsingle/#api-guide-resources-task-validate)
```
export scdf_stream_name=usage-cost-logger
curl "http://localhost:9393/streams/validation/$scdf_stream_name" | jq .appStatuses
```

### Deploy a dummy stream to test your connections
Create a stream with this definition. No need to register any applications, since these are registered by default.

```
time --format='YYYY MM DD' | log
```

You should be able to see the SCDF server delegate this on to the Skipper server in its logs, saying something like this:
```
2021-08-02 05:17:30.805  INFO 1 --- [nio-9393-exec-5] o.s.c.d.s.stream.SkipperStreamDeployer   : Deploying Stream time-log using skipper.
```

Then in skipper logs, see something like this:

```
2021-08-02 05:17:31.104  INFO 1 --- [eTaskExecutor-4] o.s.c.d.spi.local.LocalAppDeployer       : Preparing to run an application from org.springframework.cloud.stream.app:log-sink-kafka:jar:3.0.2. This may take some time if the artifact must be downloaded from a remote host.
2021-08-02 05:17:45.925  INFO 1 --- [eTaskExecutor-4] o.s.c.d.spi.local.LocalAppDeployer       : Command to be executed: /layers/paketo-buildpacks_bellsoft-liberica/jre/bin/java -jar /home/cnb/.m2/repository/org/springframework/cloud/stream/app/log-sink-kafka/3.0.2/log-sink-kafka-3.0.2.jar
2021-08-02 05:17:45.927  INFO 1 --- [eTaskExecutor-4] o.s.c.d.spi.local.LocalAppDeployer       : Deploying app with deploymentId time-log.log-v1 instance 0.   Logs will be in /tmp/1627881451004/time-log.log-v1
2021-08-02 05:17:46.028  INFO 1 --- [eTaskExecutor-4] o.s.c.d.spi.local.LocalAppDeployer       : Preparing to run an application from org.springframework.cloud.stream.app:time-source-kafka:jar:3.0.2. This may take some time if the artifact must be downloaded from a remote host.
2021-08-02 05:18:00.127  INFO 1 --- [eTaskExecutor-4] o.s.c.d.spi.local.LocalAppDeployer       : Command to be executed: /layers/paketo-buildpacks_bellsoft-liberica/jre/bin/java -jar /home/cnb/.m2/repository/org/springframework/cloud/stream/app/time-source-kafka/3.0.2/time-source-kafka-3.0.2.jar
2021-08-02 05:18:00.128  INFO 1 --- [eTaskExecutor-4] o.s.c.d.spi.local.LocalAppDeployer       : Deploying app with deploymentId time-log.time-v1 instance 0.
   Logs will be in /tmp/1627881465927/time-log.time-v1
```

### Spin up a quick HTTP endpoint on C* 

Helpful for seeing how this all works with C*
Using [this guide](https://docs.spring.io/spring-cloud-dataflow-samples/docs/current/reference/htmlsingle/#http-cassandra-local), but setting contact points to access docker c*.


In CLI:
```
# import kafka 
app import --uri https://dataflow.spring.io/kafka-maven-latest

# create teh stream
# - make sure to use a port that is opened by Skipper Server, which is where the http endpoint is going to be running behind
stream create cassandrastream --definition "http --server.port=20000 --spring.cloud.stream.bindings.output.contentType='application/json' | cassandra --spring.data.cassandra.contact-points=dataflow-dse-server:9042  --spring.data.cassandra.local-datacenter=dc1 --ingestQuery='insert into book (id, isbn, title, author) values (uuid(), ?, ?, ?)' --keyspace=clouddata" --deploy

# wait for it to deploy....

# Hit teh endpoint, after waiting for 
http post --contentType 'application/json' --data '{"isbn": "1599869772", "title": "The Art of War", "author": "Sun Tzu"}' --target http://localhost:20000

```

### Debugging docs
For streams:
- https://dataflow.spring.io/docs/stream-developer-guides/troubleshooting/debugging-scdf-streams/

For tasks: 
- https://dataflow.spring.io/docs/batch-developer-guides/troubleshooting/debugging-scdf-tasks/

## Some Common Errors
### Cannot unregister then re-register an app without changing its name

This is especially exacerbating since it seems like you cannot just go in and change the URI without unregistering the app (?)

Solution: Looks like you need to delete the release in Skipper: https://stackoverflow.com/a/61010380/6952495
- Answer provided by SCDF core member, so probably what you have to do
- (NOTE I have not tried yet)

Workaround #1: just use a new name
Workaround #2: If the goal is just to push up new code, bump up the jar version and register the new jar. This will create a new version in the registry.


### ERROR: org.springframework.cloud.skipper.SkipperException: Statemachine is not in state ready to do UPGRADE
Sometimes just have to wait for Skipper to figure things out. E.g., maybe if state is not FAILED and not DEPLOYED, I think you just have to wait longer. For example, if state is `UNKNOWN` or `PARTIAL` you have to wait for the timeout (5 min).


### ERROR: An error occurred.  Failed to upload the package. Package [usage-cost-logger-stream:1.0.0] in Repository [local] already exists.

- https://stackoverflow.com/questions/58135060/spring-cloud-data-flow-failed-to-upload-the-package-package-test-stream-commen

Just delete the deploy (using REST API or CLI) and redeploy in the GUI

```
export scdf_stream_name=usage-cost-logger-stream
curl "http://localhost:9393/streams/deployments/$scdf_stream_name" -i -X DELETE
```

### Cassandra contact point keeps getting set as 127.0.0.1:9042 despite being set in --spring.data.cassandra.contact-points
For Cassandra sink, there was trouble when passing in array. Maybe comma separated works, or just a single value like this:

```
--spring.data.cassandra.contact-points=dataflow-dse-server:9042
```

### 
