var config = require('../../server/config.json');
var path = require('path');

module.exports = function(Booking) {

  


  Booking.afterRemote('upsert', function(context, Bookings, next) {
    
    var User = Booking.app.models.Users;
    
    
    var user_id = context.result.userID;
    // Find the related user
    User.findOne({ where: {"id": user_id} }, function (err, user) {
     
      
       

       var options = {
      type: 'email',
      to: user.email,
      from: 'info@thegigisup.co.uk',
      subject: 'Booking confirmation.',
      html: '<h1>Thank you for booking</h1><p>Your ticket ref is: <b>' + context.result.ticketRef + '</b></p>Bring this ticket ref to the door so you can enter the gig.</p>',
      Booking: Booking
    };

    // send an email
     
    
      Booking.app.models.Email.send(options, function(err, mail) {
       
        if (err !== undefined) {
         console.log(err);
        }
        next();
      }); 

    });

    
   
   
     

    });
}

