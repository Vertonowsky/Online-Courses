
        const passStrength = document.getElementById("password_strength");
        const passInside   = document.getElementById("password_strength_inside");

        /**
         * FUNCTION RESPOSIBLE FOR CHECKING
         * IF INSERTED DATA MATCHES REGEXP
        */
        function checkInputData(index, type, data) {
            let complete = false;
            let msg = [];
            let n   = 0;
            

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
            if (type === "password" || type === "passwordRepeat") {
                let width = 100;
                let errors = 0;
                n   = 4;
                msg = Array(n);
                
                msg[0] = "<i class='fas fa-check ok'></i> Minimum 8 znaków<br>";
                msg[1] = "<i class='fas fa-check ok'></i> Duża litera<br>";
                msg[2] = "<i class='fas fa-check ok'></i> Cyfra<br>";
                msg[3] = "<i class='fas fa-check ok'></i> Znak specjalny<br>";
                if (type === "passwordRepeat") {
                    msg[4] = "<i class='fas fa-check ok'></i> Identyczne hasła<br>";
                    n++;
                }

                if (data.length <= 0) {
                    width -= 10;
                }

                if (data.length < 8) {
                    width -= 15;
                    errors++;
                    msg[0] = "<i class='fas fa-times error'></i> Minimum 8 znaków<br>";
                } 
                
                if (!data.match(/[A-Z]/)) {
                    width -= 25;
                    errors++;
                    msg[1] = "<i class='fas fa-times error'></i> Duża litera<br>";
                }
                
                if (!data.match(/[0-9]/)) {
                    width -= 25;
                    errors++;
                    msg[2] = "<i class='fas fa-times error'></i> Cyfra<br>";
                }
                
                if (!data.match(/[$&+,:;=?@#|<>.^*()%!-'"\]\[]/)) {
                    width -= 25;
                    errors++;
                    msg[3] = "<i class='fas fa-times error'></i> Znak specjalny<br>";
                }

                let pass = document.getElementById("register_data_password").value;
                let repeatPass = document.getElementById("register_data_password_repeat").value;
                if ((type === "passwordRepeat" && data !== pass) || (type === "password" && pass !== repeatPass && repeatPass != "")) {
                    passInside.style.background = "#DC2A03";
                    msg[4] = "<i class='fas fa-times error'></i> Identyczne hasła<br>";
                    errors++;
                }

                if (type === "password" || type === "passwordRepeat" && data === pass) {
                    passStrength.style.visibility = "visible";
                    passInside.style.width = width + "%";

                    if (width < 25) passInside.style.background = "#DC2A03";
                    if (width >= 25 && width < 75) passInside.style.background = "#DC9F03";
                    if (width >= 75 && width < 100) passInside.style.background = "#1CBA03";
                    if (width == 100 && errors == 0) passInside.style.background = "var(--color-light-blue)";

                    if (width == 100 && errors == 0) complete = true;
                }
            }
            

            /**
             * Email REGEXP
             */
            if (type === "email") {
                n   = 1;
                msg = Array(n);
                msg[0]  = "<i class='fas fa-times error'></i> Błędny format<br>";
                
                if (data.match(/^[a-z\d]+[\w\d.-]*@(?:[a-z\d]+[a-z\d-]+\.){1,5}[a-z]{2,6}$/i)) {
                    complete = true;
                    msg[0]   = "<i class='fas fa-check ok'></i> Ok<br>";
                }
            }
        

            /**
             * Password REGEXP - considered when user logs into account
             */
            if (type === "passwordLogin") {
                n   = 1;
                msg = Array(n);
                msg[0]  = "<i class='fas fa-times error'></i> Niedozwolone znaki<br>";
                
                if (data.match(/^[a-zA-Z0-9ąĄćĆśŚęĘóÓłŁńŃżŻźŹ$&+,:;=?@#|<>.^*()%!-]+$/)) {
                    complete = true;
                    msg[0]   = "<i class='fas fa-check ok'></i> Ok<br>";
                }
            }
            
            
            /**
             * Check if user accepted terms of use
             */
            if (type === "terms") {
                n   = 1;
                msg = Array(n);
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

        function checkInputDataRepeatPass(index, password, repeatPass) {

            let complete = checkInputData(index, "password", repeatPass);
            if (password === repeatPass) return;

            passInside.style.background = "#DC2A03";


        }
        
        
        


        
        
        /**
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
         */
        function sendAuthenticationData(type, form, email, password, passwordRepeat, terms) {
            let correctData = 0; // Count correct data
            
            if (type === "register") {
                if (checkInputData(0, "email", email)) correctData++;
                if (checkInputData(1, "password", password))  correctData++;
                if (checkInputData(2, "passwordRepeat", passwordRepeat))  correctData++;
                if (checkInputData(3, "terms", terms))  correctData++;

                if (correctData != 4) return; // Value '4' means all data is correct
            }

            let url = form.attr('action');
            let token = $("meta[name='_csrf']").attr("content");
            let header = $("meta[name='_csrf_header']").attr("content");

            
            $.ajax({
                data: form.serialize(), // Serializes the form's elements
                type: "POST",
                dataType: "json",
                url: url,
                beforeSend: function (xhr) {
                    xhr.setRequestHeader(header, token)
                },
                success: function(data) {

                    if (data == null || data.type == null || data.success == null || data.message == null) {
                        showPopup("Wystąpił błąd podczas komunikacji z serwerem.", 0);
                        return;
                    }
                    if (data.type != "register") {
                        showPopup("Wystąpił błąd podczas komunikacji z serwerem.", 0);
                        return;
                    }


                    if (data.type === "register") {
                        if (data.success) {
                            window.location.href = "/logowanie?registered=true";
                            return;
                        }
                        showPopup(data.message, data.success);
                    }


                }, 
                error: function(xhr, status, error) {
                    let data = xhr.responseJSON;
                    if (data != null || data != undefined)
                        showPopup(data.message, 0);
                }
            });
        }













        function showToolTip(index, text, boolean) {
            let message = document.getElementsByClassName("tooltip_text")[index];
            let icon    = document.getElementsByClassName("tooltip_icon")[index];
            if (message === null || icon === null) return;

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