Good Maowning! v0.2-dev
=======================
A simple java project that uses both Reddit and email to collect, categorize, and distribute cat pictures.


Requirements
============
The following are environment requirements and must be accessable on the path to properly build/use this project:
jdk 1.8.0_40
ant 1.9.4

Additional properties files are also required (samples included, must be renamed to the following and filled out with your own information):
src/email.properties
src/database.properties
src/reddit.properties


Setting Up Databases
====================
In order to run any of the components, some databases are required.  In order to create the basic database files (with empty/minimal tables) run each 'create.sql' script in the develop/ folder.
If you are upgrading from previous versions, make sure to upgrade each of your databases as well.  To do so, run each of the v#_#_DB_NAME_upgrade.sql files in the develop/ folder that have a version number AFTER your old version, up to and INCLUDING your new version.


How to Run GoodMaowning!
========================
Once you have the above requirements set up and configured, run the following commands from the main git repository folder (named 'GoodMaowning' by default):
$ ant 					# This does the initial build of the project, you only need to run this once on setup, and each time you update the codebase
$ ant -Dcomponent=component_name run 	# This runs an instance of GoodMaowning under any of the following components: maower, bot

Components
==========
Maower:
The maower is in charge of sending the good maowning emails, ie "Maowing" at the subscribers.  This component does reads on the images and subscribers databases, while also updating the subscribers database when it successfully sends them an email.  Manipulating subscribers is currently a manual process.
Bot:
The reddit bot takes user-driven suggestions for cat images and adds them to the images database.  Any user that types the phrase such as "It's a black kitty!" or "It's a sleepy fluffy kitty!" from the comments of an imgur post within /r/cats, the bot will add the image under the given adjective categories and respond with confirmation.
