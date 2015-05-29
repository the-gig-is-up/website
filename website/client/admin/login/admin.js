



Template.adminLogin.events({
    "submit .login-admin-please": function (event) {

        // Get the values from the input boxes
        var $email = event.target.email;
        var $password = event.target.pass;

        // Check if the email and password are sane
        if ($email.value === "" && $password.value === "") {
            return inputsWrong([$email, $password],
                    "Email address and password required.",
                    '#message');

        } else if ($email.value === "") {
            return inputsWrong($email,
                "Email address required.",
                '#message');
        } else if ($password.value === "") {
            return inputsWrong($password,
                "Password required.",
                '#message');
        }

        // Let's prepare the values to send
        var send_data = {};
        send_data['email'] = $email.value;
        send_data['password'] = $password.value;
        //send_data['ttl'] = 6000;

        SessionManager.login(send_data);

        // Clear form
        $email.value = "";
        $password.value = "";

        // Prevent default form submit
        return false;
    }
});