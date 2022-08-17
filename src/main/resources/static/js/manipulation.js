
let categoryFilters  = [];
let typeFilters      = [];



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
function loadCourses(typeFilters, categoryFilters) {
            
    $.ajax({
        type:"GET",
        dataType:"json",
        data: { "typeFilters": JSON.stringify(typeFilters), "categoryFilters": JSON.stringify(categoryFilters) },
        url:"/manipulation/loadCourses",
        success: function(data) {

            console.log(data);

            let lista = data;
            if (lista.length == 0) {
                document.getElementById("courses_list").innerHTML = "<div id='empty_error'><p>Błąd: Nie znaleziono danych do wyświetlenia.</p></div>";  
            }


            let text = "";
            $.each(lista, function(index, element) {

                text = text + '<div class="course_container">'+
                                    '<div class="left">' +
                                        '<div class="photo">' +
                                            '<img src="/images/course_photo_' + element.id + '.png" />' +
                                        '</div>' +
                                        '<p class="category">[' + element.category.charAt(0) + ((element.category).slice(1)).toLowerCase() + ']</p>' +
                                    '</div>' +

                                    '<div class="right">' +
                                        '<div class="description">' +

                                            '<div class="title_with_shadow">' +
                                                '<span class="tit_text"><div class="tit_bck"></div>' + element.name +'</span>' +
                                            '</div>' +
                                            '<p class="desc ">' + element.description + '</p>' +

                                            '<a class="button_gray" href="/wyswietl/' + element.id + '">Sprawdź <i class="fas fa-arrow-circle-right"></i></a>' +

                                        '</div>' +
                                    '</div>' + 
                                '</div>';
            
            });


            if (lista.length > 0) document.getElementById("courses_list").innerHTML = text;

            //show heading above all courses list
            if (document.getElementById('top_panel') != null) updateCoursesHeading(lista.length, categoryFilters);

            
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
function updateCoursesHeading(size, categoryFilters) {
    let top_panel = document.getElementById('top_panel');
    let prefix = " kurs";
    if (size > 0 && size <= 4) prefix = " kursy";
    if (size >= 5) prefix = " kursów";

    let cat = "";
    let filters_size = categoryFilters.length;
    if (filters_size === 0) cat = "Wszystko";
    if (filters_size > 0) {
        for (let i = 0; i < filters_size; i++) {
            cat = cat + categoryFilters[i];
            if (i < filters_size -1) cat = cat + ", ";
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
        top_panel.style.paddingBottom = "0";
        tab.classList.remove('active');
    }
}
/**
 * 
 *      --[ END ]--
 * openFilters() function 
 * 
 */