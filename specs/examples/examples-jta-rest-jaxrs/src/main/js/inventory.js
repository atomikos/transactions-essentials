var http = require('http');

module.exports = {
	purchase : function(propagation, purchaseRequest, collecExtent) {
	let options = {
		host : 'localhost',
		path : '/inventory/purchase',
		port : '9001',
		method : 'POST',
		headers : {
			'accept': 'application/json',
			'content-type' : 'application/json',
			'Atomikos-Propagation': propagation	
		}
	};
	var req = http.request(options, collecExtent);
	// This is the data we are posting, it needs to be a string or a buffer
	req.write(JSON.stringify(purchaseRequest));
	req.end();
	}
}