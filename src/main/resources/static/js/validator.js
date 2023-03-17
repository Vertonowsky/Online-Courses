
        const AuthDataTypeEnum = Object.freeze({ "Password":1, "PasswordRepeat":2, "PasswordLogin": 3, "Email":4, "Terms":5 });
        const passStrength     = document.getElementById("password_strength");
        const passInside       = document.getElementById("password_strength_inside");
        let   previousBarColor = "#D1D1D1";


        /**
         * Checks data entered by user
         *
         * Parameters:
         *  index -> individual number of tooltip on specified page
         *  type -> AuthDataTypeEnum specifies type of input text
         *  data -> data passed from user
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
            if (type == AuthDataTypeEnum.Password || type == AuthDataTypeEnum.PasswordRepeat) {
                let width = 100;
                n = 4;
                msg = Array(n);

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

                if (!data.match(/[$&+,:;=?@#|<>.^*()%!-'"\]\[]/)) {
                    width -= 25;
                    msg[3] = "<i class='fas fa-times error'></i> Znak specjalny<br>";
                }


                if (type == AuthDataTypeEnum.Password) {
                    passStrength.style.visibility = "visible";
                    passInside.style.width = width + "%";

                    if (width < 25) { passInside.style.background = "#DC2A03"; previousBarColor = "#DC2A03";}
                    if (width >= 25 && width < 75) { passInside.style.background = "#DC9F03"; previousBarColor = "#DC9F03";}
                    if (width >= 75 && width < 100) { passInside.style.background = "#1CBA03"; previousBarColor = "#1CBA03";}
                    if (width == 100) { passInside.style.background = "var(--color-light-blue)"; previousBarColor = "var(--color-light-blue)";}
                }

                if (width == 100) complete = true;
            }
            

            /**
             * Email REGEXP
             */
            if (type == AuthDataTypeEnum.Email) {
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
            if (type == AuthDataTypeEnum.PasswordLogin) {
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
            if (type == AuthDataTypeEnum.Terms) {
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







        /**
         * Check if password and repeated password are equal
         */
        function checkPasswordsEquality() {
            const passwordsEquality = document.getElementById("passwords_equality");
            let pass = document.getElementById("register_data_password").value;
            let repeatPass = document.getElementById("register_data_password_repeat").value;
            if (pass != repeatPass && pass != "" && repeatPass != "") {
                passInside.style.background = "#DC2A03";
                if (!passwordsEquality.classList.contains("active"))
                    passwordsEquality.classList.add("active");

                return;
            }

            passInside.style.background = previousBarColor;
            if (passwordsEquality.classList.contains("active"))
                passwordsEquality.classList.remove("active");
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
                if (checkInputData(0, AuthDataTypeEnum.Email, email)) correctData++;
                if (checkInputData(1, AuthDataTypeEnum.Password, password))  correctData++;
                if (checkInputData(2, AuthDataTypeEnum.PasswordRepeat, passwordRepeat))  correctData++;
                if (checkInputData(3, AuthDataTypeEnum.Terms, terms))  correctData++;

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
                error: function(xhr) {
                    let data = xhr.responseJSON;
                    if (data != null)
                        showPopup(data.message, 0);
                }
            });
        }












        /**
         * Shows tooltip
         *
         * Parameters:
         *  index -> individual number of tooltip on specified page
         *  text -> tooltip text
         *  boolean -> should it consist of positive or negative color and icon
         */
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