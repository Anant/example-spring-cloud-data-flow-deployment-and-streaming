# Standalone Stream Sample

## Deploy using Docker (locally)
[Instructions here](./../deployment-platforms/local/README.md).

## Building the apps

Use the appropriate binder profiles `kafka` (active by default) or `rabbit` to build a binary for use with that binder.

```bash
$./mvnw clean package
```

## Building the distribution zip file

```bash
$./mvnw package -Pdist

```

This must be run from this directory and will build `dist/usage-cost-stream-sample.zip` 

## Guide/Instructions for this template:
https://dataflow.spring.io/docs/stream-developer-guides/streams/standalone-stream-sample/

### Source Code for the tutorial: 
https://github.com/spring-cloud/spring-cloud-dataflow-samples/blob/master/dataflow-website/stream-developer-guides/streams/standalone-stream-sample

