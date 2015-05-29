
Template.adminDeleteUser.created = function() {
    // Create a session variable which updates
    // the user listing
    Session.setDefault('gotDeleteUsers', false);
    // Create a placeholder variable
    // to add to when we get users
    this.users = false;
};



Template.adminDeleteUser.helpers({
    getUsers: function() {
        if (Session.get('gotDeleteUsers')) {
            return Template.instance().users;
        }
    }
});


Template.adminDeleteUser.events({
    'click .redify': function(event, template) {
        // remember to set this to false
        // so we are starting from 'afresh'
        Session.set('gotDeleteUsers', false);

        var obj = this;
        var users = template.users;

        for (var i = 0; i < users.length; ++i) {
            var el = users[i];
            var is_in = (el.firstName === obj.firstName &&
                            el.email === obj.email);

            if (is_in) {
                HTTP.call(
                    "DELETE",
                    API_URL + "/users/" + el.id,
                    {
                        headers: {
                            'Content-Type': 'application/x-www-form-urlencoded',
                            'Authorization': localStorage.getItem("token")
                        }
                    },
                    function(error, result) {
                        if (result.statusCode == 204) {
                            users.splice(i, 1);
                            template.users = users;
                            Session.set('gotDeleteUsers', true);
                        }
                        else if (result.statusCode == 401 || error) {
                            Session.set('gotDeleteUsers', false);
                        }
                    });
                break;
            }
        }
    },
    'submit form': function (event, template) {
        // remember to set this to false
        // so we are starting from 'afresh'
        Session.set('gotDeleteUsers', false);

        // Get the values from the input boxes
        var $email = event.target.email.value;
        var $fname = event.target.fname.value;
        var $lname = event.target.lname.value;

        // Let's prepare the values to send
        var send_email = "%" + $email + "%";
        var send_fname = "%" + $fname + "%";
        var send_lname = "%" + $lname + "%";


        HTTP.call("GET",
            API_URL + "/users?filter[where][and][0][firstName][like]=" + send_fname +
            "&filter[where][and][1][lastName][like]=" + send_lname +
            "&filter[where][and][2][email][like]=" + send_email +
            "&filter[where][and][3][isAdmin]=0",
            {
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded',
                    'Authorization': localStorage.getItem("token")
                }
            },
            function(error, result) {
                if (error === null) {
                    template.users = result.data;
                    Session.set('gotDeleteUsers', true);
                }

                // Clear form
                $email = "";
                $fname = "";
                $lname = "";
            });

        return false;

    }
});