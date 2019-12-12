Todo sample application
==============
This todo application provides an API for adding, removing and changing todo entries.

Objectives
---------

The todo-list sample application should be both simple but yet capable to act as service in a fully qualified integration test
scenario. The application provides a REST API for accessing the todo entries via Http.
        
Run
---------

The sample application uses Maven as build tool. So you can compile, package and test the
sample with Maven.
 
     mvn clean install
    
This executes the complete Maven build lifecycle and creates the sample artifacts that are used throughout the other samples.
The build application represents the system under test.

System under test
---------

The application is a Spring Boot web application that you can deploy on any web container. Of course Spring Boot provides many other fantastic
ways of starting the application.

On of these possibilities is the Spring Boot Maven Plugin. You can start the sample todo list application with this command.

     mvn spring-boot:run

This starts the application in a Tomcat web container and automatically deploys the todo list app. Point your browser to
 
    http://localhost:8081/todolist/

You will see the web UI of the todo list. Now add some new todo entries manually and you are ready to go.


API doc
---------

    http://localhost:8081/swagger-ui.html

