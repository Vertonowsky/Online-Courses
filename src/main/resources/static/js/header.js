/**
 * 
 *       --[ START ]--
 * openNav() function description
 *  
 * Opens navigation menu on smaller devices
 * 
 */
function openNav() {
    button = document.getElementById("navPanelToggle");
    tab = document.getElementById("navPanel");
    void tab.offsetWidth;
    tab.className = "";

    if (!button.classList.contains("active")) {
        button.classList.add("active");
        tab.classList.add('active');
        tab.classList.add('slideInFromRight');
    } else {
        button.classList.remove("active");
        tab.classList.remove('active');
        tab.classList.add('slideOutToRight');
    }
}