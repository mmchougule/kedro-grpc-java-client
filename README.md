## Kedro gRPC Java Client Example

## Requirements:
`jave -version: 1,8`

`gradlew --version: 5.2.1`



## To build example client

1. Run:
```
$ ./gradlew installDist
```

This creates the scripts `kedro-client` in the
`build/install/kedro-grpc-client/bin/` directory that run the examples.

This example requires `kedro-grpc-server` to be running in a kedro project.


## Run a pipeline and check streaming status of this pipeline run

```
$ ./build/install/kedro-grpc-client/bin/kedro-client
```



Make sure you have `kedro-grpc-server` installed and running in a kedro project
In your Kedro project:

`kedro server grpc-start`




Kedro gRPC Server installation:

`https://git.mckinsey-solutions.com/Mayur-Chougule/kedro-grpc-server`