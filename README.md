## OnlineCourses - Simple online courses application
Kursowo.pl is a **Web Application** written in Java which offers online courses for students.
It's main responsibility is to allow easy configuration management and clarity of the GUI.

## Technologies:
* Spring Boot, Security, MVC
* JPA
* Thymeleaf
* MySQL
* HTML, Google Fonts, Font-awesome  
* CSS
* JavaScript

## Preview
Working DEMO is available under [http://146.59.16.44:8080/](http://146.59.16.44:8080/).

![preview](https://user-images.githubusercontent.com/27568559/228951777-cd6b3dc3-2d0b-4eb1-84c2-18ccdb5c662e.png)

### Test responsiveness of the website on different devices

![preview-github](https://user-images.githubusercontent.com/27568559/228950129-21c4ef7e-51d6-4ee7-b1fc-95828517b0e5.png)


## Setup

1. Make Sure you have MySQL database installed. Application won't work without it. 
> You are also obliged to run a Tomcat server which will host the application. Using `Intellij` is convinient as it has build-in Tomcat Server. 
2. Import repository into your IDE with `git clone https://github.com/Vertonowsky/Online-Courses.git`.
3. Rename [application.properties.example](src/main/resources/application.properties.example) file to `application.properties`. 
4. Fill all necessary authentication credentials inside `application.properties` file. (**MySQL**, **gmail**, **OAuth 2.0**)
5. Run application

## Admin Panel
All of the courses are **configurable** under `/admin` location. To access admin panel you will need admin permissions identified by `ROLE_ADMIN` role.
Inside of the admin panel you will be able to modify the course content including chapter and topic addition. 
Furthermore, you can always edit already existing courses in case of invalid information or video refactoring.

![admin-panel](https://user-images.githubusercontent.com/27568559/228951590-a4bb49df-4322-4616-a8b4-337ef9686195.png)

## Profile page
Every user has access to his individual profile page under `/profile`. It includes list of all of his owned courses and some statistics. This is the default page that user is redirected to, after successfull login. 

![profile](https://user-images.githubusercontent.com/27568559/228952449-3464d9a7-efda-498d-8f91-2119ea0bc3db.png)


## Course 
All courses consist of **3 free videos** which are a demo version of the course. If user got interested in our product, then he can buy a full version of the course.

![course](https://user-images.githubusercontent.com/27568559/228953117-fb1a6762-f425-46c2-b0f9-c06660d93269.png)


## Summary
Consider that this is only a superficial description of the whole application. Project consist of many features that haven't been specified here. To see all of them please visit [http://146.59.16.44:8080/](http://146.59.16.44:8080/). 
Passwords are encrypted and application in **secure**. You can safely create an account and test all functionalities. 


## License
The **Online-Courses** project is owned by the author - Bartosz Malec, who holds **all copyrights** and intellectual property rights.

You may use the code implemented in the Project for learning, evaluating, or improving your own skills or knowledge. 
You may not use the Project for commercial purposes or claim any ownership or intellectual property rights.
If you use any portion of the Project in your own work, you must ask its author for agreement.
By accessing or using the Project, you acknowledge that you have **read** and **agree** to be bound by the terms of this **license**.
