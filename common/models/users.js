var config = require('../../server/config.json');
var path = require('path');

module.exports = function(Users) {
  //send verification email after registration
  Users.afterRemote('create', function(context, Users) {
    console.log('> Users.afterRemote triggered');
   console.log(context); 
    var options = {
      type: 'email',
      to: Users.email,
      from: 'info@thegigisup.co.uk',
      subject: 'Thanks for registering.',
      template: path.resolve(__dirname, '../../server/views/verify.ejs'),
      redirect: 'http://thegigisup.co.uk/login',
      Users: Users
    };

    Users.verify(options, function(err, response) {
      if (err) {
        console.log(err);
        return;
      }

      console.log('> verification email sent:', response);

      context.res.render('response', {
        title: 'Signed up successfully',
        content: 'Please check your email and click on the verification link '
          + 'before logging in.',
        redirectTo: 'http://thegigisup.co.uk/login',
        redirectToLinkText: 'Log in'
      });
    });
  });

  //send password reset link when requested
  Users.on('resetPasswordRequest', function(info) {
    var url = 'http://' + config.host + ':' + config.port + '/reset-password';
    var html = 'Click <a href="' + url + '?access_token=' + info.accessToken.id
      + '">here</a> to reset your password';

    Users.app.models.Email.send({
      to: info.email,
      from: info.email,
      subject: 'Password reset',
      html: html
    }, function(err) {
      if (err) return console.log('> error sending password reset email');
      console.log('> sending password reset email to:', info.email);
    });
  });
};
