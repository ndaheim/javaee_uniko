# This was a student project we did for the JavaEE course at Uni Koblenz in 2018

## Instructions

## Open project
1. install maven (https://maven.apache.org/install.html).
2. Open the maven `pom.xml` with your IDE.

## Setup derby
1. Download the derby database: https://db.apache.org/derby/derby_downloads.html
2. Start the database with: WIN: `.\bin\startNetworkServer.bat` UNIX: `./bin/startNetworkServer`

## Configure database in intelliJ
1. Click on **Database** on the right side and click the **add** button.
2. Select `Database > Derby (Remote)`.
3. Enter `jdbc:derby://localhost:1527/echo_tss;create=true` in the URL field.
4. Click test & OK.
5. Execute all files located in the `db-scripts` folder except the `999_insert-fake-data.sql`.
6. Execute a file by "right click on the file" > "Run '<scriptname>.sql'" > Select echo_tss database 
7. OPTIONAL - execute `999_insert-fake-data.sql` as above if you want some fakedata.  
8. OPTIONAL - Disconnect from the database by clicking the **red stop** button on the right side under the database tab.

## Setup payara
1. Install JDK 8: http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html 
2. Download latest payara version here: https://www.payara.fish/all_downloads
3. Ensure that `JAVA_HOME` points to java version 8!
4. Ensure that `java -version` returns java version 8!
5. Start payara server with: WIN: `.\bin\asadmin.bat start-domain` UNIX: `./bin/asadmin start-domain`
6. Open: http://localhost:4848
7. Navigate to `Resources > JDBC > JDBC Connection Pools` and add a new **JDBC Connection Pool**
    - Pool Name: echo_tss
    - Resource Type: javax.sql.DataSource
    - Database Driver Vendor: Derby
    - **Next** - Button and **Finish** - Button 
8. Open your **echo_tss** JDBC Connection Pool, go to the **Additional Properties** Tab and add the following properties:
    - serverName: localhost
    - PortNumber: 1527
    - Password: 
    - User: 
    - DatabaseName: echo_tss
9. Hit the **Save** Button & navigate to the **General** tab, to validate the configuration click the **Ping** Button.
10. Navigate to `Resources > JDBC > JDBC Resources` and add a new **JDBC Resources**
    - JNDI Name: echo_tss_resource
    - Pool Name: echo_tss
    - Click `OK`
11. OPTIONAL - Stop payara server with: WIN: `.\bin\asadmin.bat stop-domain` UNIX: `./bin/asadmin stop-domain`

## Setup Test Mail Server
1. Open localhost:4848
2. Navigate to `JavaMail Sessions` and add a new **JavaMail Session**
    - JNDI Name: foo
    - Mail Host: localhost
    - Default User: nobody
    - Default Sender Adress: nobody@localhost
3. Add a new Property to **foo**
    - mail.smtp.port: 2500
4. Keep the **TestSmtpServer** Application running during development and testing

## Build project
### Method 1 - Use the IDE
1. Open on the right side the **Maven Projects** Tab (for intelliJ).
2. Navigate to `echo tss Webapp > Lifecycle`
3. double click **clean** and after that double click **package**.
3. This will produce a `echo_tss.war` file in the "target" folder of your web sub module (./web/target/echo_tss.war).

### Method 2 - Use the command line
1. Navigate to the project root
2. Execute `mvn clean package`

## Run the project
1. Configure glassfish for intellij (https://www.jetbrains.com/help/idea/configuring-and-managing-application-server-integration.html)
2. Click Run from IDE - The **green Play** button in the toolbar (select echo_tss.war if the IDE asks)

## Develop for project
- Add new dependencies in pom.xml. For more info: http://www.tech-recipes.com/rx/39256/add-dependencies-to-maven-pom-xml-file/
- Add a new Entity: 
    - Add the new entity class in the package `echo.entities` 
    - Add the entity class in `resources/META-INF/persistence.xml`
    - Create the according DTO Class in `echo.dto`
    - Create an access Class in `echo.dao`
