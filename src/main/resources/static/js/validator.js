
        /*
            FUNCTION RESPOSIBLE FOR CHECKING 
            IF INSERTED DATA MATCHES REGEXP
        */
        function checkInputData(index, type, data) {
            updateSucces = (typeof updateSucces === 'undefined') ? true : updateSucces;
            var complete = false;
            var n        = 0;


            var passStrength = document.getElementById("password_strength");
            var passInside = document.getElementById("password_strength_inside");
            

            /**
             * Password REGEXP - register formula
             * 
             * Width values:
             *  25 - Very weak password
             *  100 - Very strong password
             * 
             * [!! IMPORTANT !!]
             *  Only very strong password is considered as valid
             */
            if (type == "password" || type == "passwordRepeat") {
                n         = 4;
                var width = 100;
                var msg   = [];
                
                msg[0] = "<i class='fas fa-check ok'></i> Minimum 8 znaków<br>";
                msg[1] = "<i class='fas fa-check ok'></i> Duża litera<br>";
                msg[2] = "<i class='fas fa-check ok'></i> Cyfra<br>";
                msg[3] = "<i class='fas fa-check ok'></i> Znak specjalny<br>";
                
                if (data.length <= 0) {
                    width -= 10;
                }

                if (data.length < 8) {
                    width -= 15;
                    msg[0] = "<i class='fas fa-times error'></i> Minimum 8 znaków<br>";
                } 
                
                if (!data.match(/[A-Z]/)) {
                    width -= 25;
                    msg[1] = "<i class='fas fa-times error'></i> Duża litera<br>";
                }
                
                if (!data.match(/[0-9]/)) {
                    width -= 25;
                    msg[2] = "<i class='fas fa-times error'></i> Cyfra<br>";
                }
                
                if (!data.match(/[$&+,:;=?@#|<>.^*()%!-]/)) {
                    width -= 25;
                    msg[3] = "<i class='fas fa-times error'></i> Znak specjalny<br>";
                }


                if (type == "password") {
                    passStrength.style.visibility = "visible";
                    passInside.style.width = width + "%";
                    
                    
                    if (width < 25) passInside.style.background = "#DC2A03";
                    if (width >= 25 && width < 75) passInside.style.background = "#DC9F03";
                    if (width >= 75 && width < 100) passInside.style.background = "#1CBA03";
                    if (width == 100) passInside.style.background = "var(--color-light-blue)";
                }

                if (width == 100) complete = true;

            }
            

            /**
             * Email REGEXP
             */
            if (type == "email") {
                var n   = 1;
                var msg = Array(n);
                msg[0]  = "<i class='fas fa-times error'></i> Błędny format<br>";
                
                if (data.match(/^[a-z\d]+[\w\d.-]*@(?:[a-z\d]+[a-z\d-]+\.){1,5}[a-z]{2,6}$/i)) {
                    complete = true;
                    msg[0]   = "<i class='fas fa-check ok'></i> Ok<br>";
                }
            }
        

            /**
             * Password REGEXP - considered when user logs into account
             */
            if (type == "passwordLogin") {
                var n   = 1;
                var msg = Array(n);
                msg[0]  = "<i class='fas fa-times error'></i> Niedozwolone znaki<br>";
                
                if (data.match(/^[a-zA-Z0-9ąĄćĆśŚęĘóÓłŁńŃżŻźŹ$&+,:;=?@#|<>.^*()%!-]+$/)) {
                    complete = true;
                    msg[0]   = "<i class='fas fa-check ok'></i> Ok<br>";
                }
            }
            
            
            /**
             * Check if user accepted terms of use
             */
            if (type == "terms") {
                var n   = 1;
                var msg = Array(n);
                msg[0]  = "<i class='fas fa-times error'></i> Pole obowiązkowe<br>";
                
                if (data) {
                    complete = true;
                    msg[0]   = "<i class='fas fa-check ok'></i> Pole obowiązkowe<br>";
                }
            }
            

            /**
             * Complete = TRUE - Every inserted data matches regexp
             * Complete = FALSE - All or few statements weren't completed succesfully
             */
            showToolTip(index, msg, complete);
            return complete;
            
        }
        
        
        


        
        
        /**
         * 
         *                 --[ START ]--
         * sendAuthenticationData() function description
         *  
         * Listens for login and register action and validates
         * data inserted by user
         * 
         * Parameters:
         *  type -> specifies if user is trying to login or register - available values are "login" and "register"
         *  form -> form object containing whole data
         *  email -> user's email
         *  login -> user's login
         *  password -> user's password
         *  terms -> returns true if user accepted terms of use
         * 
         */
        function sendAuthenticationData(type, form, email, password, passwordRepeat, terms) {
            var correctData = 0; // Count correct data

            if (type == "login") {
                if (checkInputData(0, "email", email)) correctData++;
                if (checkInputData(1, "passwordLogin", password))  correctData++;
                    

                if (correctData != 2) return; // Value '2' means all data is correct
            
            
            } else if (type == "register") {
                if (checkInputData(0, "email", email)) correctData++;
                if (checkInputData(1, "password", password))  correctData++;
                if (checkInputData(2, "passwordRepeat", passwordRepeat))  correctData++;
                if (checkInputData(3, "terms", terms))  correctData++;

                if (correctData != 4) return; // Value '4' means all data is correct
            }
            


            var url = form.attr('action');
            
            $.ajax({
                data: form.serialize(), // Serializes the form's elements
                type: "POST",
                dataType:"json",
                url: url, 
                success: function(data) {
                    var loginForward = 0;
                    var idForward = 1;

                    if (type == "login") {
                        if (getUrlParam("id") != null) {
                            idForward = parseInt(getUrlParam("id"));
                            if (!Number.isInteger(idForward)) {
                                idForward = 0;
                            }
                        }

                        if (getUrlParam("buy") != null) {
                            loginForward = parseInt(getUrlParam("buy"));
                            if (!Number.isInteger(loginForward)) {
                                loginForward = 0;
                            }
                        }
                    }

                    if (data.response) {
                        if (loginForward == 1) {
                            window.location.href = "/edu/wyswietl.php?id=" + idForward + "&active=1";

                        } else {
                            window.location.href = "/edu/profil.php";
                        }
                    } else {
                        showPopup(data.message, 0);
                    }

                }, 
                error: function(xhr, status, error) {
                    alert(error);
                    console.warn(xhr.responseText);
                }
            });
        }













        function showToolTip(index, text, boolean) {
            var message = document.getElementsByClassName("tooltip_text")[index];
            var icon    = document.getElementsByClassName("tooltip_icon")[index];

            if (boolean) {
                icon.classList.remove('fa-exclamation-circle');
                icon.classList.add('fa-check');
                icon.style.color = "var(--color-light-blue)";
            } else {
                icon.classList.remove('fa-check');
                icon.classList.add('fa-exclamation-circle');
                icon.style.color = "var(--color-light-red)";
            }

            icon.style.visibility = "visible";
            text = text.toString().replaceAll(",", " ");  //Remove commas from array
            message.innerHTML = text;
        }










        function getUrlParam(name) {
            var results = new RegExp('[\\?&]' + name + '=([^&#]*)').exec(window.location.href);
            return (results && results[1]) || undefined;
        }