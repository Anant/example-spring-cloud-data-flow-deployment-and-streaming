# Launch a Task from a stream


Basically taking our source for stream deployment and just updating so it can launch a task instead.

## Key differences from the pure stream implementation:
- add the app-starters-task-launch-request-common component to the source
- Add a prebuilt sink which kicks off the task
- Create and register a task that you want to be launched

## Follows this guide: 
https://docs.spring.io/spring-cloud-dataflow/docs/current/reference/htmlsingle/#spring-cloud-dataflow-launch-tasks-from-stream

## Deploy using Docker (locally)
[Instructions here](./../deployment-platforms/local/README.md).

