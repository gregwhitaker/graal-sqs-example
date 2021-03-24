# graal-sqs-example
An example of an application that reads from Amazon SQS built as a native image with [GraalVM](https://www.graalvm.org/).

## Prerequisites
This example requires that you have [GraalVM](https://www.graalvm.org/docs/getting-started/) installed on your system.

## Building the Example
Run the following command to build the example application as both a JAR distribution and a native image:

    ./gradlew buildAll

## Running the Example
Follow the steps below to run the example:

1. Run the following command to start a mock AWS environment using LocalStack:

         ./gradlew startLocalStack

2. Run the following command to execute the application as an Uber Jar on the JVM:

         ./run-uber-jar.sh

   If successful, you will see the following in the terminal:

         Connecting to SQS: http://localhost:4566 [us-east-1]
         SLF4J: Failed to load class "org.slf4j.impl.StaticLoggerBinder".
         SLF4J: Defaulting to no-operation (NOP) logger implementation
         SLF4J: See http://www.slf4j.org/codes.html#StaticLoggerBinder for further details.
         Checking for messages...
         No messages found... sleeping

3. In a new terminal window, run the following command to start the application as a native image:

         ./run-native-image.sh

   If successful, you will see the following in the terminal:

         Connecting to SQS: http://localhost:4566 [us-east-1]
         Checking for messages...
         No messages found... sleeping

   Notice that you do not see the SLF4J missing configuration error. This is because the reflection has already occurred at compile time.

4. In a new terminal window, run the following command to begin publishing messages to the queue for the readers to receive:

         ./gradlew publishMessages

   If successful, you will see both readers printing messages in their terminals:

         Checking for messages...
         message-872
         message-873
         message-874
         message-875
         message-876
         Checking for messages...