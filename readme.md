# The Gig Is Up

*A software engineering project from the University of Leeds.*

The Gig Is Up is a gig booking system which allows users to view upcoming events around the UK. It has the following features:
- View a list of artists, events and previous bookings
- View a seating plan
- Sign up, change username, change email address, change password
- Make a booking with a card
- Confirmation emails

Included within this repo is the website, the app and the API. The main focus is on the app however you are free to view the code for the website and the API.
Website is done in Meteor and the API is by Strongloop's Loopback.

**Note: This app is a purely a mockup and no actual purchasing happens. Please do not use actual cards when purchasing a ticket.**

--------------------------------------------------------------------------------

### Installation instructions

#### Command Line:
```
    $ git clone https://gitlab.com/comp2541/dolphin.git
    $ git pull
    $ cd dolphin/
    $ gradlew build
    $ gradlew installDebug
```
#### Installation via IDE:

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

### Testing

Testing in the app has only been tested in IntelliJ. It requires the phone to be plugged in, with USB debugging enabled.

We have a custom XML file to handle testing. The file can be found on the wiki. Add the file to the folder `.idea/runConfigurations`. 

Once this file has been added, a new build configuration will be added called
"AppTester". This requires an Android device to be plugged in and unlocked,
 with USB debugging:
- Click run, and the tests will run.
- If you wish to see all tests, ensure that "Hide passed tests" is not clicked in
the Run tab of IntelliJ.

--------------------------------------------------------------------------------

App Information:

Uses Java 1.8 and Gradle v1.0.0.
Using Build tools version 21.1.1.
Targeting SDK version 21, minimum 14.
`gradle clean` can be used to clean the app folder.

--------------------------------------------------------------------------------

### Questions

Q: This isn't working in my IDE!
A: This was developed in IntelliJ IDEA, but should work 
   with Android Studio and Eclipse, but this has not been tested, and may 
   require more work.

Q: What versions of Android has this been tested on?
A: The development team has tried this on 4.4, 5.0, and 5.1. Lower versions
   should also work.
