/**
 * Created by gp on 16/03/2015.
 */

API_URL = "http://thegigisup.co.uk/api";


UI.registerHelper("setTitle", function() {
    var title = "";
    for (var i = 0; i < arguments.length - 1; ++i) {
        title += arguments[i];
    }
    document.title = "The Gig Is Up | " + title;
});


UI.registerHelper("resetTitle", function() {
    document.title = "The Gig Is Up";
});

// Show a message to a user given an element
// name
displayMessage = function(selector, message) {
    var red = '#C42425';
    var $message_box = $(selector);
    // Change text and animate
    $message_box.text(message);
    var original_color = $message_box.css("color");
    $message_box.css("color", red);
    $message_box.animate({color: original_color}, 1000);
};

// Make the background of an input box red if
// user forgot to enter stuff
inputsWrong = function(elements, message, message_selector) {
    var pale_red = '#F9CCCC';
    if ($.isArray(elements)) {

        for (var i in elements) {
            if (elements.hasOwnProperty(i)) {
                $(elements[i]).css({'background-color' : pale_red});
            }
        }

    } else {
        $(elements).css({'background-color' : pale_red});
    }

    displayMessage(message_selector, message);

    return false;
};


User = {
    id: -1,
    token: "",
    name: "",
    email: "",
    isAdmin: false
};

SessionManager = function() {
    var is_admin = false;

    // Returns date object
    // with a time that shows the end
    // of the session
    var addToTime = function(ms) {
        var date = new Date();
        return new Date(date.getTime() + ms);
    };

    // refreshSession: Refresh the session
    var refreshSession = function(time) {
        startSession(time);
    };

    // Private Functions

    var endSession = function() {
        // Logout asynchronous
        HTTP.call("POST",
            API_URL + "/users/logout?access_token=" + localStorage.getItem("token"),
            function(error, result) {
                if (result.statusCode == 204) {
                    localStorage.setItem('token', '');
                    localStorage.setItem('end', '');
                    localStorage.setItem('user_email', '');
                    localStorage.setItem('user_lname', '');
                    localStorage.setItem('user_fname', '');
                }
            });
    };

    var startSession = function(ms) {
        var sessionEnd = addToTime(ms);
        localStorage.setItem("end", sessionEnd.toString());
    };

    var adminCheck = function(bool) {
        if (bool !== undefined){
            is_admin = bool;
        }
        return is_admin;
    };

    var loginApi = function(user_details) {
        // Send the details
        var url = API_URL + "/users/login?include=user";
        HTTP.call("POST",
            url,
            {
                data: user_details,
                headers: {
                    'Content-Type': 'application/json'
                }
            },
            function(error, result) {
                if (error === null || error === undefined) {
                    var token = result.data.id;
                    var user_id = result.data.userId;
                    var user = result.data.user;
                    var user_isAdmin = result.data.user.isAdmin;
                    var isAdmin = (user_isAdmin ? 1 : 0);

                    if (token === "" || token === undefined) {
                        displayMessage('#message', "Login failed. No auth token received.");
                        return false;
                    }

                    if (user_isAdmin === 0 || user_isAdmin === undefined) {
                        displayMessage('#message', "Login failed. Not an admin.");
                        return false;
                    }

                    localStorage.setItem('token', token);
                    localStorage.setItem('user_fname', user.firstName);
                    localStorage.setItem('user_lname', user.lastName);
                    localStorage.setItem('user_email', user.email);
                    localStorage.setItem('user_isAdmin', isAdmin);
                    localStorage.setItem('user_id', user_id);
                    localStorage.setItem('user_ttl', result.data.ttl);
                    startSession(result.data.ttl);

                    Router.go('adminDash');

                } else {
                    if (error.response.statusCode == 400) {
                        displayMessage('#message', "Login failed. Unable to login.");

                    } else if (error.response.statusCode == 401) {
                        // The login actually did fail
                        displayMessage('#message', "Login failed. Please check your email address/password.");
                    } else if (error.response.statusCode == 500) {
                        // The login actually did fail
                        displayMessage('#message', "Login failed. Please check your email address/password.");
                    }
                }
            });

    };





    // Public Functions
    // These will be the only public methods available to outside callers
    return {

        extend: function() {
            refreshSession();
        },

        getSessionEnded: function() {
            var now = new Date();
            var end = new Date(localStorage.getItem("end"));

            return now > end;
        },

        logout: function() {
            endSession();
            Router.go("adminLogin");
        },
        login: function(user_details) {
            loginApi(user_details);
        },
        checkAccessToken: function (url) {
            HTTP.call("GET",
                url,
                function(error, result) {
                    if (error || result === undefined) {
                        Router.go("restricted");
                    }
                });
        },
        isAdmin: function () {
            HTTP.call("GET",
                API_URL + "/users/" + localStorage.getItem('user_id'),
                {
                    headers: {
                        'Authorization': localStorage.getItem("token")
                    }
                },
                function(error, result) {
                    if (result !== undefined) {
                        if (result.data.isAdmin == 0) {
                            return adminCheck(false);
                        } else {
                            adminCheck(true);
                        }
                    }

                });
            return adminCheck();
        }
    };
}();