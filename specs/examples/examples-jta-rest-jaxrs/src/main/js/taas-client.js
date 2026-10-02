var http = require('http');
var inventorySvc = require('./inventory.js');
var paymentSvc = require('./payment.js');

var propagation = '';
var extents = [];


var payment = {
		'cardno' : 'card10',
		'amount' : 20
	};

var purchaseRequest = {
		'cardno' : 'card10',
		'itemId' : 20,
		'qty' : 2
	};


setTimeout( function() {
    var options = {
        host: 'localhost',
        port: '9000',
        method: 'POST',
        headers: {
            'content-type': 'application/vnd.atomikos+json'
        }
    };

    beginTransaction = function(response) {
        console.log(`STATUS: ${response.statusCode}`);
    	response.on('data', function(chunk) {
    		propagation += chunk;
    	});

		response.on('end', function() {
			console.log('Created new propagation: ', propagation );
			
			console.log('purchase a thing and update inventory: ', JSON.stringify(purchaseRequest) );
			inventorySvc.purchase(propagation, purchaseRequest, function(response) {
				var extent = response.headers['atomikos-extent'];
				if (extent) {
					console.log('adding new extent: '+extent);
					extents.push(extent);
				}
			});
			console.log('perform payment: ', JSON.stringify(payment) );
			paymentSvc.pay(propagation, payment, function(response) {				
				var extent = response.headers['atomikos-extent'];
				if (extent) {
					console.log('adding new extent: '+extent);
					extents.push(extent);
				}
			});
			
			setTimeout(function() {
				var payload = '';
				extents.forEach(function(element) {
  					payload+=element;
  					payload+='|';
				});
				console.log('about to commit : ', payload);
				options.path='/atomikos/commit';
				var req = http.request(options, function(response) {
					console.log(`STATUS: ${response.statusCode}`);
				});
				req.write(payload);
			    req.end();
			}, 1000);
    	});
    }
    // transaction begin
    options.path='/atomikos/begin?timeout=5000'; 
    var req = http.request(options, beginTransaction);
    req.end();
    
}, 100);