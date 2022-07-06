
const category_filters = ["null"];
const subject_filters  = ["null"];




/*$(function() {
    loadCourses(category_filters, subject_filters);
});*/




/**
 * 
 *           --[ START ]--
 * loadCourses() function description
 *  
 * Loads all course data from database including 
 * chosen flters
 * 
 * Parameters:
 *  category_filters -> filters describing type of the course
 *  subject_filters  -> filters describing course subject
 * 
 */
function loadCourses(category_filters, subject_filters) {
            
    $.ajax({
        type:"POST",
        dataType:"json",
        data: { "category_filters":JSON.stringify(category_filters), "subject_filters":JSON.stringify(subject_filters) },
        url:"php/manipulation/loadCourses.php", 
        success: function(data) {

            let lista = data.lista;
            if (lista.length == 0) {
                document.getElementById("courses_list").innerHTML = "<div id='empty_error'><p>Błąd: Nie znaleziono danych do wyświetlenia.</p></div>";  
            }


            let text = "";
            $.each(lista, function(index, element) {

                text = text + '<div class="course_container">'+
                                    '<div class="left">' +
                                        '<div class="photo">' +
                                            '<img src="/edu/images/course_photo_' + element.course_id + '.png" />' +
                                        '</div>' +
                                        '<p class="category">[' + element.category + ']</p>' +
                                    '</div>' +

                                    '<div class="right">' +
                                        '<div class="description">' +

                                            '<div class="title_with_shadow">' +
                                                '<span class="tit_text"><div class="tit_bck"></div>' + element.name +'</span>' +
                                            '</div>' +
                                            '<p class="desc ">' + element.description + '</p>' +

                                            '<div class="button_green" onclick="openCourseDisplay(' + element.course_id + ')">Sprawdź <i class="fas fa-arrow-circle-right"></i></div>' +

                                        '</div>' +
                                    '</div>' + 
                                '</div>';
            
            });


            if (lista.length > 0) document.getElementById("courses_list").innerHTML = text;

            //show heading above all courses list
            if (document.getElementById('top_panel') != null) updateCoursesHeading(lista.length, subject_filters);
            
        },
        error: function(xhr, status, error) {
            alert(error);
            console.warn(xhr.responseText);
        }
    });
}
/**
 * 
 *      --[ END ]--
 * loadCourses() function 
 * 
 */










/**
 * 
 *                 --[ START ]--
 * updateCoursesHeading() function description
 *  
 * Updates heading of displayed courses, counts courses,
 * shows current category filter
 * 
 * Parameters:
 *  size            -> specifies amount of loaded courses
 *  subject_filters -> filters describing course subject
 * 
 */
function updateCoursesHeading(size, subject_filters) {
    let top_panel = document.getElementById('top_panel');
    let prefix = " kursów";
    if (size > 0 && size <= 4) prefix = " kursy";
    if (size >= 5) prefix = " kursów";

    let cat = "";
    let filters_size = subject_filters.length;
    if (filters_size == 1) cat = "Wszystko";
    if (filters_size > 0) {
        for (let i=1; i < filters_size; i++) {
            cat = cat + subject_filters[i];
            if (i < filters_size-1) cat = cat + ", ";
        }

    }

    let text = '<h1 class="heading"><span class="bold">' + size + prefix + '</span> w kategorii <span class="category">' + cat + '</span></h1>';
    
    let filters_toggle = document.getElementById("filtersToggle");
    if (filters_toggle.classList.contains("active")) text = text + '<div id="filtersToggle" class="active" onclick="openFilters()">Filtry <i class="fa fa-bars"></i></div>';
    else text = text + '<div id="filtersToggle" onclick="openFilters()">Filtry <i class="fa fa-bars"></i></div>';

    top_panel.innerHTML = text;
}
/**
 * 
 *         --[ END ]--
 * updateCoursesHeading() function 
 * 
 */








/**
 * 
 *           --[ START ]--
 * openFilters() function description
 *  
 * Opens filters menu for smaller devices
 * 
 */
function openFilters() {
    let button     = document.getElementById("filtersToggle");
    let top_panel  = document.getElementById("top_panel");
    let filters_ul = document.getElementById("filters_ul");
    let tab        = document.getElementById("right_panel");

    if (!button.classList.contains("active")) {
        button.classList.add("active");
        top_panel.style.paddingBottom = (filters_ul.offsetHeight + 30) + "px";
        tab.classList.add('active');
    } else {
        button.classList.remove("active");
        top_panel.style.paddingBottom = "0px";
        tab.classList.remove('active');
    }
}
/**
 * 
 *      --[ END ]--
 * openFilters() function 
 * 
 */





function openCourseDisplay(courseId) {
    window.location = "/edu/wyswietl.php?id=" + courseId;
}