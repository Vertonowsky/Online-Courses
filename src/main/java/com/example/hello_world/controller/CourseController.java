package com.example.hello_world.controller;

import com.example.hello_world.Category;
import com.example.hello_world.entity.Course;
import com.example.hello_world.mysql.MySQL;
import com.example.hello_world.repository.CourseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class CourseController {

    @Autowired
    private CourseRepository courseRepository;

    @PostMapping("/add2")
    public String addCourse() {
        Course course = new Course("Egzamin Ósmoklasisty",
                "Kurs Ósmoklasisty Matematyka",
                "Zawiera wiedzę potrzebną, żeby napisać egzamin nawet na 100%! Obejmuje nie tylko zagadnienia z klasy 8 ale z całej szkoły podstawowej – tłumaczy od podstaw działania na ułamkach, jednostki, i wiele innych rzeczy.",
                Category.MATEMATYKA,
                "Brak korzyści",
                249.0,
                120.0);


        Course course2 = new Course("Egzamin Maturalny Podstawa",
                "Kurs Maturalny - Matematyka PODSTAWA",
                "Matura podstawowa z wynikiem 100% to nie problem! Dokładnie zrozumiesz zagadnienia wymagane na maturze. Nawet najtrudniejsze tematy staną się dla Ciebie proste i zrozumiałe.",
                Category.MATEMATYKA,
                "<li>Ponad 70 godzin materiałów wideo</li>\n" +
                        "            <li>110 zadań do samodzielnego rozwiązania + odpowiedzi</li>\n" +
                        "            <li>3-miesięczny dostęp do kursu</li>\n" +
                        "            <li>starannie wyselekcjonowane zadania</li>\n" +
                        "            <li>swobodę uczenia się o dowolnej porze</li>\n" +
                        "            <li>tłumaczenie od doświadczonego korepetytora</li>",
                289.0,
                0.0);

        Course course3 = new Course("Egzamin Maturalny Rozszerzenie",
                "Kurs Maturalny - Matematyka ROZ",
                "Rozszerzona matematyka wcale nie jest taka ciężka! Przekonaj się o tym na własne oczy i zaskocz wszystkich niedowiarków.",
                Category.MATEMATYKA,
                "<li>Ponad 70 godzin materiałów wideo</li>\n" +
                        "            <li>110 zadań do samodzielnego rozwiązania + odpowiedzi</li>\n" +
                        "            <li>3-miesięczny dostęp do kursu</li>\n" +
                        "            <li>starannie wyselekcjonowane zadania</li>\n" +
                        "            <li>swobodę uczenia się o dowolnej porze</li>\n" +
                        "            <li>tłumaczenie od doświadczonego korepetytora</li>",
                329.0,
                0.0);

        courseRepository.save(course);
        courseRepository.save(course2);
        courseRepository.save(course3);
        return "Added 3 courses to repository.";
    }


    @GetMapping("/list2")
    public Iterable<Course> getCourses() {
        return courseRepository.findAll();
    }




    @RequestMapping("/lista-kursow")
    public String courseListView(Model model) {
        model.addAttribute("courses", courseRepository.findAll());
        model.addAttribute("subjects", MySQL.getSingleFilter("SELECT DISTINCT type FROM courses"));
        model.addAttribute("categories", MySQL.getSingleFilter("SELECT DISTINCT category FROM courses"));
        return "lista-kursow";
    }

}
