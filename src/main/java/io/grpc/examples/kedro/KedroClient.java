package io.grpc.examples.kedro;

import io.grpc.Channel;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;

import java.util.Iterator;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A simple client that interacts with Kedro Server greeting.
 */
public class KedroClient {
    private static final Logger logger = Logger.getLogger(KedroClient.class.getName());

    private final KedroGrpc.KedroBlockingStub blockingStub;
    private final KedroGrpc.KedroStub stub;


    private KedroClient(Channel channel) {
        blockingStub = KedroGrpc.newBlockingStub(channel);
        stub = KedroGrpc.newStub(channel);
    }


    private RunSummary run(String pipelineName) {
        logger.info("Will try to run " + pipelineName);

        RunParams request = RunParams.newBuilder().setPipelineName(pipelineName).build();
        RunSummary response = null;

        try {
            response = blockingStub.run(request);
        } catch (StatusRuntimeException e) {
            logger.log(Level.WARNING, "RPC failed: {0}", e.getStatus());
        }

        logger.info(response.toString());
        return response;
    }

    private void getStatus(String runId) {
        RunId request = RunId.newBuilder().setRunId(runId).build();

        Iterator<RunStatus> response = null;

        StreamObserver<RunStatus> t = null;

        response = blockingStub.status(request);

        for (int i=0; response.hasNext(); i++) {
            RunStatus run = response.next();
            info("Result #" + i + ": {0}", run.toString());
        }

    }

    private void info(String msg, Object... params) {
        logger.log(Level.INFO, msg, params);
    }

    private PipelineSummary listPipelines() {
        PipelineParams request = PipelineParams.newBuilder().build();
        PipelineSummary response = null;

        try {
            response = blockingStub.listPipelines(request);
        } catch (StatusRuntimeException e) {
            logger.log(Level.WARNING, "RPC failed: {0}", e.getStatus());
        }

        assert response != null;
        logger.info(response.getPipelineList().toString());
        return response;
    }

    /**
     * Greet server. If provided, the first element of {@code args} is the name to use in the
     * greeting. The second argument is the target server.
     */
    public static void main(String[] args) throws Exception {
        // Access a service running on the local machine on port 50051
        String target = "localhost:50051";

        // Create a communication channel to the server, known as a Channel. Channels are thread-safe
        // and reusable. It is common to create channels at the beginning of your application and reuse
        // them until the application shuts down.
        ManagedChannel channel = ManagedChannelBuilder.forTarget(target)
                // Channels are secure by default (via SSL/TLS). For the example we disable TLS to avoid
                // needing certificates.
                .usePlaintext()
                .build();
        try {
            KedroClient client = new KedroClient(channel);
            PipelineSummary summary = client.listPipelines();
            System.out.println(summary);

            RunSummary run = client.run("de");
            logger.info(run.toString());

            String runId = run.getRunId();
            client.getStatus(runId);

        } finally {
            // ManagedChannels use resources like threads and TCP connections. To prevent leaking these
            // resources the channel should be shut down when it will no longer be used. If it may be used
            // again leave it running.
            channel.shutdownNow().awaitTermination(5, TimeUnit.SECONDS);
        }
    }
}
