Software Engineering Project - Team Dolphin.

The Gig Is Up.
 
A Gig booking system, that allows users to sign up to a service that allows them
to purchase tickets to events, and see upcoming ones.

--------------------------------------------------------------------------------

Installation instructions:

App:

Command Line:

    $ git clone https://gitlab.com/comp2541/dolphin.git
    $ git pull
    $ cd dolphin/
    $ gradlew build
    $ gradlew installDebug

Installation via IDE:

 - Open as Project in an IDE.
 - Ensure that the gradle file in the project is being used, and not a new or
   generic one. This ensures that the correct libraries are downloaded for the
   app.
 - Once inside the project, allow gradle to sync.
 - From here, run the app, with a phone connected over USB with USB debugging
   enabled. Push the app to the device, and it will open automatically.
 - From here, the app can just be opened on the device.
 
 - Alternatively, the compiled apk can be distributed to users, to skip having 
   to compile each time.

Testing:

Testing in the app has only been tested in IntelliJ.
It requires the phone to be plugged in, with USB debugging.

Add the custom XML file to the .idea/runConfigurations

The File can be found on the wiki at
https://gitlab.com/comp2541/dolphin/wikis/app-install

Once this file has been added, a new build configuration will be added called
"AppTester". This requires an Android device to be plugged in and unlocked,
 with USB debugging.
 Click run, and the tests will run.
If you wish to see all tests, ensure that "Hide passed tests" is not clicked in
the Run tab of IntelliJ.
   
--------------------------------------------------------------------------------

Folder Structure:

tgiu-app/
  App source that touches Android APIs, resources.
 
--------------------------------------------------------------------------------

App Information:

Uses Java 1.8 and Gradle v1.0.0.
Using Build tools version 21.1.1.
Targeting SDK version 21, minimum 14.
"gradle clean" can be used to clean the app folder.

--------------------------------------------------------------------------------

Questions:

Q: Where is the user documentation for this?
A: https://gitlab.com/comp2541/dolphin/wikis/home
   Java docs for the app are included, but need to be compiled.

Q: Where is the latest version of the source code for the app?
A: https://gitlab.com/comp2541/dolphin

Q: This isn't working in my IDE!
A: This was developed in IntelliJ IDEA, but should work 
   with Android Studio and Eclipse, but this has not been tested, and may 
   require more work.

Q: What versions of Android has this been tested on?
A: The development team has tried this on 4.4, 5.0, and 5.1. Lower versions
   should also work.
   
--------------------------------------------------------------------------------

Team Members :

Gurpreet Paul,
Kieran Haden,
Jade Wilson,
Ryan Cross,
Ben Peggs

Contact information can be found in the wiki.