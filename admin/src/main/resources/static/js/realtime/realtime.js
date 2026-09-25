/**
 * 
 */
var stompClient = null;

function connectWebSocket(callback) {
	var socket = new SockJS(CONTEXT_PATH + 'ws');
	stompClient = Stomp.over(socket);
	stompClient.connect({}, function(frame) {
		console.log('Connected: ' + frame);
		stompClient.subscribe('/topic/orders', function(message) {
			console.log("receive order message :: "+message)
			var update = JSON.parse(message.body);
			showOrderNotification(update, callback);
		});
	});
}

function showOrderNotification(update, callback) {
	if (typeof showToast === 'function') {
		if(update.type == 1)
			showToast('Received new order #' + update.code, 'succcess');
		else if(update.type == 2)
			showToast('Order #' + update.code + ' is now ' + update.status, 'info');
		
		callback(update)
	}
}


