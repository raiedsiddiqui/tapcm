TAPP
====

The Tapestry thing.

For project mockups and planning, see https://oscaremr.atlassian.net/wiki/display/TAPP/Tapestry+Home

Documentation can be found at src/site/markdown/

Installation
------------

-Deploy the WAR file in the webapps directory of Tomcat7.
-Run db/init_database.sql and db/insert_users.sql to create the database tables and add a default administrator (username and password 'admin').
-Open WEB-INF/classes/tapestry.yaml in a text editor and fill in the database path (default should be fine), username and password for the database, and email settings (note that email probably only works with Gmail right now)
-Restart Tomcat (`sudo service tomcat7 restart`)

You should now be able to log in with the administrator account and begin adding users and patients. I recommend creating a different administrator and disabling the default one.

