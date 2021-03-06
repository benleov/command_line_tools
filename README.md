command_line_tools
========================

command_line_tools is a command line application that allows you to perform common tasks very quickly, such as checking and sending emails, getting a weather forecast, searching the music brainz database or using twitter.


Configuring the Settings
-------------------------

When the application is run for the first time, it will prompt you for a password. This will be used to
encrypt your settings with, and will be required to be entered each time you start the application.

On the inital run it will then create a file within the conf folder, called settings.ini.

Edit this file, putting in the required settings. See "supported_commands.txt" for a list of options
that are available to you.

When you run the application for the second time the application will then encrypt your settings.

NOTE: You cannot edit your settings manually in this state. See "Modifying your settings" if you wish
to modify them in at a later date.

Optional settings are stored in a file called unencryp_settings.ini within the conf folder. These are
not encrypted and can be edited manually.

When running the application, please use the help command to which required and optional settings are
available.

Running the application
-------------------------

To run the application, all you need to do is unzip the main zip file, and then run the command:

java -jar webservicesapi.jar

If you have downloaded the source code, there is a script (linux only) for running it called 
"run.sh", contained within the root directory. You will need to update this file to suit your 
needs.


Running on a 64 bit system
-------------------------

If you wish to run this application on a 64 bit system, remove the file named ./lib/libjcurses_32.so and restart the application.

Required Properties
-------------------------

You can modify your settings at runtime using the set command.

The format is set <setting name> <value>

For example:

set gmail.username myemail@gmail.com

If you wish to modify your settings all at once, you can also do so by performing the following steps.

1. Add the following line to your settings.ini file:

application.reset = reset

2. Restart the application. It will ask you to enter your password. When you do this, your settings.ini file
   will be decrypted, and the application will exit.

3. Once you have finished editing your settings, restart the application. Your settings will be re-encrypted.

Optional Properties
-------------------------

Optional properties are stored in a clearProperties text file in conf/unencrypted_settings.ini. You can edit these
manually with any text editor. Note you will need to restart the application for any changes to become
effective.

Usage
-------------------------

Type the command:

java -jar CLWebServices.jar
