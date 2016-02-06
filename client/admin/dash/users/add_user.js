
Template.adminAddUser.created = function() {

};



Template.adminAddUser.helpers({

});


Template.adminAddUser.events({
    'submit form': function (event, template) {

        // Get the values from the input boxes
        var $email = event.target.email.value;
        var $fname = event.target.fname.value;
        var $lname = event.target.lname.value;
        var $username = event.target.username.value;
        var $password = event.target.password.value;
        var $admin = event.target.admin.checked;
        var $artist = event.target.artist.checked;

        // Let's prepare the values to send
        var send_data = {};
        send_data['firstName'] = $fname;
        send_data['lastName'] = $lname;
        send_data['email'] = $email;
        send_data['username'] = $username;
        send_data['password'] = $password;
        send_data['isAdmin'] = $admin;
        send_data['isArtist'] = $artist;


        HTTP.call(
            "PUT",
            API_URL + "/users",
            {
                data: send_data,
                headers: {
                    'Content-Type': 'application/json'
                }
            },
            function(error, result) {
                if (result.statusCode == 200) {
                    displayMessage("#message", "Sweet, it worked.");
                }
                else if (result.statusCode == 401) {
                    displayMessage("#message", "Failed, try again.");
                }
                else if (result.statusCode == 422) {
                    displayMessage("#message", result.data.error.message);
                }
            });

        return false;

    }
});