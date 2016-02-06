
Template.topbar.rendered = function () {

    // Change the top bar if it is the homepage
    if (window.location.pathname == "/") {
        $("#title").text("");
        $(".banner i").css('color', 'white');
        $(".banner").css('box-shadow','0 0 0 0').css('background-color','transparent');
    } else if (window.location.pathname.indexOf("/admin") > -1) {
        $("#title").text("THE GIG IS UP / ADMIN");
    } else {
        $("#title").text("THE GIG IS UP");
    }
    if (this.drawer_open == null) {
        this.drawer_open = false;
    }

};

Template.topbar.helpers({
    isLoggedIn: function() {
        var token = localStorage.getItem("token");
        if (token === ""
            || token === null
            || token === undefined
            || SessionManager.getSessionEnded()) {
            return false
        }
        return true;
    }
});



Template.topbar.events({
    "click .row #drawer": function(event, template) {
        if (template.drawer_open) {
            TweenMax.to(".menu", 0.3, {x: 0});
            TweenMax.to("header", 0.3, {x: 0});
        } else {
            TweenMax.to(".menu", 0.3, {x: -300});
            TweenMax.to("header", 0.3, {x: -300});
        }
        template.drawer_open = !template.drawer_open;

        //TweenMax.to("#content", 0.3, {x: -200});
    },
    "click .row #logout": function(event, template) {
        event.preventDefault();
        SessionManager.logout();
    }


});