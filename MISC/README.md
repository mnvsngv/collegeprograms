# Miscellaneous Programs
To run the samples, execute the appropriate command from the root directory.

## Attendance Calculator
`gradlew --quiet MISC:executeAttendanceCalculator`

## Chat Room
You'll need to run the server first:
`gradlew --quiet MISC:executeChatRoomServer`

Then you can have as many clients as you want:
`gradlew --quiet MISC:executeChatRoomClient`

### Usage
Run the server, and note down the IP Address shown.
Then run a client and when prompted, input the IP Address shown on the server.

## Group Communication
You'll need to run the server first:
`gradlew --quiet MISC:executeGroupCommunicationAdmin`

Then you can have as many clients as you want:
`gradlew --quiet MISC:executeGroupCommunicationUser`

### Usage
Run the admin. You should see a UI Frame which allows you to create or view groups. Create a group.
Now run the client. You'll see a frame which has a button to get existing groups. Click and request to join a group which was created by the admin.
In the Admin console, type "y" or "Y" to allow the requested user to join the group.
Done!

## Database GUI
`gradlew --quiet MISC:executeDatabaseGUI`