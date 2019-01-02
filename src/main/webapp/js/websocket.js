var stompClient = null;

function setConnected(connected) {
    document.getElementById('connect').disabled = connected;
    document.getElementById('disconnect').disabled = !connected;
    document.getElementById('conversationDiv').style.visibility = connected ? 'visible' : 'hidden';
}

function connect() {
    var socket = new SockJS('/monitor-bot');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame) {
        setConnected(true);
        console.log('Connected: ' + frame);
        stompClient.subscribe('/listen/bot', function (response) {
            console.log(response);
            showResponse(JSON.parse(response.body));
        });
    });
}

function disconnect() {
    if (stompClient != null) {
        stompClient.disconnect();
    }
    setConnected(false);
    console.log("Disconnected");
}

function showResponse(object) {
    var table = document.getElementById('response');
    var tr = document.createElement('tr');

    var td_date = document.createElement('td');
    td_date.appendChild(document.createTextNode(object.date));

    var td_userId = document.createElement('td');
    td_userId.appendChild(document.createTextNode(object.user.userId));

    var td_userFirstName = document.createElement('td');
    td_userFirstName.appendChild(document.createTextNode(object.user.userFirstName));

    var td_userLastName = document.createElement('td');
    td_userLastName.appendChild(document.createTextNode(object.user.userLastName));

    var td_userName = document.createElement('td');
    td_userName.appendChild(document.createTextNode(object.user.userName));

    var td_usersRequest = document.createElement('td');
    td_usersRequest.appendChild(document.createTextNode(object.usersRequest));

    var td_botsResponse = document.createElement('td');
    td_botsResponse.appendChild(document.createTextNode(object.botsResponse));

    tr.appendChild(td_date);
    tr.appendChild(td_userId);
    tr.appendChild(td_userFirstName);
    tr.appendChild(td_userLastName);
    tr.appendChild(td_userName);
    tr.appendChild(td_usersRequest);
    tr.appendChild(td_botsResponse);

    table.appendChild(tr);
}