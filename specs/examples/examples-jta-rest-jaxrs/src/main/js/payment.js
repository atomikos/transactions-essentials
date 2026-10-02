var http = require('http');

module.exports = {
	pay : function(propagation, payment, collecExtent) {
	let options = {
		host : 'localhost',
		path : '/payment/pay',
		port : '9002',
		method : 'POST',
		headers : {
			'accept': 'application/json',
			'content-type' : 'application/json',
			'Atomikos-Propagation': propagation	
		}
	};
	var req = http.request(options, collecExtent);
	// This is the data we are posting, it needs to be a string or a buffer
	req.write(JSON.stringify(payment));
	req.end();


	}
}