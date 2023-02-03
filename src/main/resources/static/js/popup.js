


/**
 * Popup configuration
 */
    
const popup = document.getElementById('popup_info');
let delay   = 0;
let text    = "";
let status  = 0;
let timer;
 

/**
 * Parameters:
 *  text -> information displayed to user
 *  status -> 0 = negative, 1 = positive (mostly to format colors)
 */
function showPopup(text1, status1) {
    delay  = 5;
    text   = text1;
    status = status1;


    popup.style.visibility = "visible";
    popup.style.opacity    = "1";

    if (status1) {
        popup.style.background = "#22A8E0"; 
        popup.style.boxShadow  = "0px 0 4px 2px #209FD4";
        document.getElementById("popup_title").innerHTML = "<i class='icon fas fa-check'></i>" + text1;
    } else {
        popup.style.background = "#FF7070"; 
        popup.style.boxShadow  = "0px 0 4px 2px #E65353";
        document.getElementById("popup_title").innerHTML = "<i class='icon fas fa-exclamation-triangle'></i>" + text1;
    }

    clearTimeout(timer);

    conf(); // close popup after 5 seconds
}


/**
 * If popup is being displayed - update current text
 * Else - create popup with choosen style
 */
function conf() {
    delay--;
        
    if (delay < 0) {
        clearTimeout(timer);
        timer = null;
        return;
    }
    
    if (delay == 0) {
        popup.style.visibility = "hidden";
        popup.style.opacity    = "0";
    }
    timer = setTimeout("conf()", 1000);
}
 
 
 

/**
 * On popup close [X] click, hide popup from user
 */
const closePopup = document.getElementById("popup_close");
closePopup.onclick = function() {
    delay = 0;
    document.getElementById('popup_info').style.opacity   = "0";
    document.getElementById('popup_info').style.visiblity = "hidden";
}