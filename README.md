## What is the project about?
Kursowo.pl is a Web Application written in Java witch offers online courses for students.
It's main feature is allowing easy management and clarity of the courses.

Technologies used:
*Spring Boot, Security, MVC
*JPA
*Thymeleaf
*MySQL
*HTML
*CSS
*JavaScript

### Setup

To properly setup all functionalities head to [application.properties](src/main/resources) file and complete all necessary authentication credentials.
It includes database and Google uthentication (OAuth2).

### Administration 
All courses are **configurable** on dedicated page under `/admin` location. To access admin panel you will need admin permissions identified by role named `ROLE_ADMIN`.
Inside of the admin panel you will be able to modify the course content including chapter and topic addition. 
Furhtermore you can always edit already existing courses in case of invalid informations or video refactoring.

### 


### License
The Online-Courses project is owned by the author ("Bartosz Malec"), who holds all copyrights and intellectual property rights.

You may use the code implemented in the Project for learning, evaluating, or improving your own skills or knowledge. 

You may not use the Project for commercial purposes or claim any ownership or intellectual property rights.

If you use any portion of the Project in your own work, you must ask its author for agreement.

By accessing or using the Project, you acknowledge that you have **read** and **agree** to be bound by the terms of this **license**.
