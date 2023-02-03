/**
 * Opens navigation menu on smaller devices
 */
const navToggle = document.getElementById("navPanelToggle");
const shadow    = document.getElementById("shadow");
const header    = document.getElementById("header");
const minHeader = document.getElementById("minHeader");

function openNav() {
    void minHeader.offsetWidth;
    minHeader.className = "";

    if (!navToggle.classList.contains("active")) {
        navToggle.classList.add("active");
        shadow.classList.add('active');
        minHeader.classList.add('active');
    } else {
        navToggle.classList.remove("active");
        shadow.classList.remove("active");
        minHeader.classList.remove('active');
    }
}

window.addEventListener("click", function(event) {

    // If user clicked on navPanelToggle button, then open minHeader.
    if (navToggle.contains(event.target)) {
        openNav();
        return;
    }

    // If user clicked outside minHeader and header
    if (!minHeader.contains(event.target) && !header.contains(event.target)){
        // If minHeader is currently visible
        if (navToggle.classList.contains("active")) {
            navToggle.classList.remove("active");
            shadow.classList.remove("active");
            minHeader.classList.remove('active');
        }
    }
});


