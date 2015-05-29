
Template.adminViewBookings.created = function() {
    // Create a session variable which updates
    // the user listing
    Session.setDefault('adminBookings', false);
    // Create a placeholder variable
    // to add to when we get users
    this.bookings = false;
};



Template.adminViewBookings.helpers({
    getBookings: function() {
        if (Session.get('adminBookings')) {
            // TODO: get rid of the array brackets
            return [Template.instance().bookings];
        }
    }
});


Template.adminViewBookings.events({
    'click .redify': function(event, template) {
        // remember to set this to false
        // so we are starting from 'afresh'
        Session.set('adminBookings', false);

        var obj = this;
        var users = template.bookings;


    },
    'submit form': function (event, template) {
        // remember to set this to false
        // so we are starting from 'afresh'
        Session.set('adminBookings', false);

        // Get the values from the input boxes
        var $id = event.target.id.value;

        // TODO: Sort this out to use events. currently 401's
        HTTP.call("GET",
            API_URL + "/users/" + $id  + "?access_token=" + localStorage.getItem("token"),
            {},
            function(error, result) {
                if (error === null) {
                    template.bookings = result.data;
                    Session.set('adminBookings', true);
                }

                // Clear form
                $id = "";
            });

        return false;

    }
});