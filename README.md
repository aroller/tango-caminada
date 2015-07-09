# Tango Caminada
A mapping application for Google's Project Tango that gives the device "legs" to discover and navigate indoor environments. Named for "the walking steps" of the [Tango Dance][1], this application provides guidance for moving the device through an indoor environment that has been "learned" with the help of a "teacher".  

#Features
* Learns an area through observation
* Remembers Places named by the teacher
* Provides the name of the Place closest to the device 
* Allows the teacher to choose a destination Place 
* Provides guidance to the teacher to move the device to reach a destination
* Provides the distance to the nearest object in the center of the device's view
* Save a PointCloud to a .csv file

#Road Map
* Guide around objects blocking the path to a destination
* Calculate a Route to a place using intermediate Places that may be reached.
* Provide autonomous movement using an [iRobot Create 2][2].
* Simplify and streamline the area learning

# Setup 
The project and code is very raw right now and may not work.  The project was forked from [briangaffey][3]'s copy of the [tango-examples-java][4] so it [would work on Android Studio][5].  The AreaLearning project has received the majority of additions.  It is intended this "exploratory" code will be replaced entirely.

## Prerequisites
* Android Studio
* Project Tango Tablet
* Experience using [tango-examples-java][4]

## Build & Deploy
1. Import the project in Android Studio
1. Build and Deploy AreaLearning project to your Tango Device
1. Confirm the appliction is running on your Device

#Usage

First learn and area, then use the guidance.

##Learn an Area
1. Toggle _Learning mode off_ to _Learning mode on_
2. Press Start
3. Record the area of interest by pointing the device in all directions of the room using smooth movement. Move around to get better angles of an area.
4. Press _Save ADF_ to store the area providing it the name of the area you recorded

##Create Places
1. An area must already have been learned and the most recent ADF file saved on the device
2. Toggle _Learning mode off_ to _Learning mode on_
3. Toggle _Load ADF off_ to _Load ADF on_
4. Press Start
5. Follow the instructions in the _Awareness_ text (rotate the device so it will recognize the area)
6. Once the are is recognized the _Awareness_ will say the name of the ADF recognized
7. Move to an area of interest and press _Mark Waypoint_ button (office door, desk, kitchen, etc)
8. Name the area 
9. The Place will be remembered and associated with the ADF file
10. Save the ADF file again.  This is not mandatory, but it will improve future area recognition and accuracy. It will append to the existing ADF.

##Navigate to a Place
1. Places must have been marked and associated to an ADF file that is recognized
2. Press _Go_ button
3. Type the name of the place you wish to navigate
4. The app will choose the closest match to what you typed
5. Follow the text in Guidance (_Left, Right,Forward_) until you have _Arrived_

##Measure Depth
1. Press _Start_ (no setup required)
2. Point the device at an object and observe the distance
3. Confirm correct measurements by pointing at a wall next to an open doorway and then slide towards the door so the measurement reports the depth through the doorway


[1]: http://www.tejastango.com/terminology.html#C
[2]: http://www.irobot.com/About-iRobot/STEM/Create-2.aspx
[3]: https://github.com/briangriffey/tango-examples-java
[4]: https://github.com/googlesamples/tango-examples-java
[5]: https://github.com/googlesamples/tango-examples-java
