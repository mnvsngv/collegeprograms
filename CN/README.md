# Computer Networking
To run the samples, execute the appropriate command from the root directory.

## CRC: Cyclic Redundancy Check
`gradlew --quiet CN:executeCRC`

## Hamming Code
`gradlew --quiet CN:executeHamming`

## Internet Checksum
`gradlew --quiet CN:executeInternetChecksum`

## Go Back N ARQ
**WARNING**: The code gets stuck at "timed out" often. Try using different values of the number of frames and window size.
You'll need to open 2 consoles in the root directory.
Run the following command in one of the consoles:
`gradlew --quiet CN:executeGoBackNARQSender`

And then run this command in the other console:
`gradlew --quiet CN:executeGoBackNARQReceiver`

## Selective Repeat ARQ
**WARNING**: Ensure you provide the input to Sender before you give the input to the receiver, otherwise the simulation gets stuck in an infinite loop.

You'll need to open 2 consoles in the root directory.
Run the following command in one of the consoles:
`gradlew --quiet CN:executeSelectiveRepeatARQSender`

And then run this command in the other console:
`gradlew --quiet CN:executeSelectiveRepeatARQReceiver`