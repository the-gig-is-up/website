Template.adminDash.created = function() {
    Session.setDefault('currentStep', 1);
};



Template.adminDash.helpers({
    isStep: function(n) {
        return Session.equals('currentStep', n);
    }
});


Template.adminDash.events({
    'click #admin-users': function() {
        return Session.set('currentStep', 1);
    },
    'click #admin-bookings': function() {
        return Session.set('currentStep', 2);
    },
    'click #admin-events': function() {
        return Session.set('currentStep', 3);
    }
});