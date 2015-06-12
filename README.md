#Loaner Application

##What you will need:

- IntelliJ or other editor that can deal with scala.
- MongoDB (http://mongodb.com/)  version should not be important.
- java 8 (download from oracle) the jdk version is all you really need.
- Project Tracking - https://www15.v1host.com/CognitiveCreations/

##One time startup or redeploy:

- loader file (/init directory) will have a script to use mongorestore function.

##Running:

- go to the root directory of this program and run ./activator once it comes up type run. This will default run at the
9000 port. to run on a new port just type "run xxxx", where xxxx is the new port number.

##Main Directories:

- scala code is in "app"
- /app/controllers this is where the controllers that interact with the user go.  
- /app/model holds the object that are read to and from JSON.  
- /app/views holds the basic html/scala code.  this is used in the entry points return values.  
- /public this is anything that is html/javascript based.  Note: play sets this up to respond via the /assets
- /public/frontend - frontend code for the store. basic html and css. 
- /pubplc/backend - backend code based around angular


## Basic Play info

Google playframework to lookup detailed info about the framework. 

The /conf/routes file contains the routes that the server responds to. Has 3 columns, the server method, the url, 
and the method in the class to call.  If you see a :name that is a variable that should be passed. this should be 
referenced in the ()'s of the method to call. 
