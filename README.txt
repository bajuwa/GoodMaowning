Good Maowning!
==============
A simple java project that will send an email to subscribers each morning with a cute cat picture.  


Requirements
============
The following are environment requirements and must be accessable on the path to properly build/use this project:
jdk 1.8.0_40
ant 1.9.4

Additional properties files are also required (samples included, must be renamed to the following and filled out with your own information):
src/email.properties
src/database.properties


Upgrades
========
If you are upgrading from previous versions, make sure to upgrade each of your databases as well.  To do so, run each of the v#_#_DB_NAME_upgrade.sql files in the develop/ folder that have a version number AFTER your old version, up to and INCLUDING your new version.
