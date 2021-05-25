# Crash Handler library + Example

This repo contains an application, which has a sub project - an SDK for handling crashes. 

To install the crash handler you can see: https://github.com/elcuco/CrashHandlerExample/blob/master/app/src/main/java/com/example/crashhandlerexample/CrashHandlerApp.kt


To integrate this into a new project, you will need to copy https://github.com/elcuco/CrashHandlerExample/tree/master/crashhandlerlibrary 
into your library, and then link to your app. 


If you look into:

https://github.com/elcuco/CrashHandlerExample/blob/62a4fdddecff42ae0de63520e92caf37b39a00a4/app/src/main/java/com/example/crashhandlerexample/CrashHandlerApp.kt#L45

You can see that you can pass a custom class to upload crash dumps. If you don't pass a custom uploader, 
the library can use simple internal implementation. Integration is done.


TODO:
 - I should make a maven repository. 
 - Github actions to publish the library uppon "tag".
 - Separate the library and the demo app.
 - What more data can be added to the report?
 - You will need to change the API endpoint, the url is hardcoded here: 
   https://github.com/elcuco/CrashHandlerExample/blob/master/crashhandlerlibrary/src/main/java/com/example/crashhandlerlibrary/LibraryState.java#L21
 - ... and remove from the app the HTTP - plain text exception.
