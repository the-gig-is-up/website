module.exports = function(Event) {
    
    Event.summary = function(eventID, cb) {
        Event.findOne({ where: {"id": eventID} }, function (err, event) {

                var app = Event.app;
                var Venue = app.models.Venue;

                var vID = event["venueID"];

                Venue.findOne({ where: {"id": vID} }, function (err, venue) {

                        var address = venue["address"];

                        event["address"] = address;
                                cb(null, event);
                });

        });
    }

    

	
    Event.hotEvents = function(cb) {
       toJSONLocal = function(date) {
         var local = new Date(date);
         local.setMinutes(date.getMinutes() - date.getTimezoneOffset());
         return local.toJSON().slice(0, 10);

       },
       getNow = function() {
              var today = new Date();
              return toJSONLocal(today);
              },
        getMonth = function() {
                var a_month = new Date();
                var today = new Date();
                a_month.setMonth(today.getMonth() + 1);
                return toJSONLocal(a_month);
                }
       
        
       
       Event.find({
        where: {date: {gt: getNow()} }, 
        include: ['venue', 'artists']
        }, function(err, events) {
          var result = [];
         
          // Get each event
          for (var n in events) {
            if (events.hasOwnProperty(n)){
                 var event = events[n];
                 var venue = {};
                 // Get venue object
                 for (var i in event) {
                     if (event.hasOwnProperty(i)){
         
                       if (typeof event[i].venue !== 'undefined') {
                            venue = event[i].venue;
                            break;
                        }
                   }  
                 }
            
                
                var tickets_left = event.ticketsLeft;
                 
                if ((event.ticketsLeft > 0) && ((event.ticketsLeft/venue.capacity) <= 0.1)) {
                    result.push(event);
                 }
             }
          }
          cb(null, result);
      });
    }

    Event.remoteMethod(
        'hotEvents',
        {
          http: {path: '/hotevents', verb: 'get'},
         returns: {arg: 'events', type: 'array'} 
        }
    );

    Event.remoteMethod(
        'summary',
        {
          http: {path: '/:id/summary', verb: 'get'},
          accepts: {arg: 'id', type: 'number', required: true},
          returns: {arg: 'summary', type: 'string'}
        }
    );
};
