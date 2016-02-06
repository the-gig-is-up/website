
OnBeforeActions = {
    requireLogin: function() {

        // Then check if the token is still alive
        var token = localStorage.getItem("token");
        if (token === ""
            || token === null
            || token === undefined
            || SessionManager.getSessionEnded()) {
            Router.go("restricted");
        } else {
            //var url = API_URL + "/accesstokens/" + token + "/exists";
            //SessionManager.checkAccessToken(url);

            // If they are on the admin pages
            // check if they are an admin
            var current_path = Router.current().location.get().path;
            if (current_path.indexOf("/admin/") > -1) {
                //if (!SessionManager.isAdmin()) {
                //    Router.go("restricted");
                //}
            }
        }

        // If everything is okay, then render
        // the right page
        this.next();
    }
};


Router.onBeforeAction(OnBeforeActions.requireLogin, {except: ['home', 'adminLogin']});

Router.map(function () {
    var admin_dash = "/admin/dash";
    var names = ["Dash"];
    this.route('home', {
        path: '/',
        layoutTemplate: 'layout',
        action: function() {
            Session.set('apage', true);
            this.render('home');
        }
    });
    this.route('adminLogin', {
        path: '/admin',
        layoutTemplate: 'with-topbar'
    });
    this.route('login', {
        path: '/login',
        layoutTemplate: 'with-topbar'
    });
    this.route('adminDash', {
        path: admin_dash,
        layoutTemplate: 'empty'
    });
    this.route('adminDeleteUser', {
        path: admin_dash + "/delete-user",
        layoutTemplate: 'with-topbar-and-subbar',
        data: function() {
            names[1] = "Delete User";
            return names;
        }
    });
    this.route('adminAddUser', {
        path: admin_dash + "/add-user",
        layoutTemplate: 'with-topbar-and-subbar',
        data: function() {
            names[1] = "Add User";
            return names;
        }
    });
    this.route('adminViewBookings', {
        path: admin_dash + "/view-bookings",
        layoutTemplate: 'with-topbar-and-subbar',

        data: function() {
            names[1] = "View Bookings";
            return names;
        }
    });
    this.route('restricted', {
        layoutTemplate: 'with-topbar',
        action: function() {
            this.render("restricted");
        }
    });
    this.route('notFound', {
        path: '*'
    });
});
