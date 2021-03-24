package example;

import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.DeleteMessageRequest;
import software.amazon.awssdk.services.sqs.model.GetQueueUrlRequest;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageRequest;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageResponse;

import java.net.URI;

/**
 * Starts the example application.
 */
public class Application {
    private static final String SQS_ENDPOINT = "http://localhost:4566";
    private static final String SQS_ENDPOINT_REGION = "us-east-1";
    private static final String QUEUE_NAME = "example-queue";

    /**
     * Main entry-point of the application.
     *
     * @param args command line arguments
     * @throws Exception
     */
    public static void main(String... args) throws Exception {
        final GraalReader reader = new GraalReader();
        reader.start();
    }

    /**
     * Reads sqs queue and prints the messages it receives.
     */
    static class GraalReader {

        public GraalReader() {}

        /**
         * Starts the queue reader.
         */
        public void start() {
            final SqsClient sqs = getSqsConnection();
            final String queueUrl = getQueueUrl(sqs);

            while (true) {
                System.out.println("Checking for messages...");

                final ReceiveMessageRequest request = ReceiveMessageRequest.builder()
                        .queueUrl(queueUrl)
                        .maxNumberOfMessages(5)
                        .build();

                final ReceiveMessageResponse receiveMessageResponse = sqs.receiveMessage(request);
                if (receiveMessageResponse.hasMessages()) {
                    receiveMessageResponse.messages().forEach(message -> {
                        System.out.println(message.body());

                        final DeleteMessageRequest deleteMessageRequest = DeleteMessageRequest.builder()
                                .queueUrl(queueUrl)
                                .receiptHandle(message.receiptHandle())
                                .build();

                        sqs.deleteMessage(deleteMessageRequest);
                    });
                } else {
                    System.out.println("No messages found... sleeping");

                    try {
                        // Backoff
                        Thread.sleep(10_000);
                    } catch (InterruptedException e) {
                        // Break execution
                        throw new RuntimeException();
                    }
                }
            }
        }

        /**
         * Gets a connection to SQS.
         *
         * @return SQS connection
         */
        private SqsClient getSqsConnection() {
            System.out.printf("Connecting to SQS: %s [%s]%n", SQS_ENDPOINT, SQS_ENDPOINT_REGION);

            return SqsClient.builder()
                    .endpointOverride(URI.create(SQS_ENDPOINT))
                    .build();
        }

        /**
         * Gets the SQS queue url.
         *
         * @param sqs SQS connection
         * @return queue url
         */
        private String getQueueUrl(final SqsClient sqs) {
            final GetQueueUrlRequest request = GetQueueUrlRequest.builder()
                    .queueName(QUEUE_NAME)
                    .build();

            return sqs.getQueueUrl(request).queueUrl();
        }
    }
}
