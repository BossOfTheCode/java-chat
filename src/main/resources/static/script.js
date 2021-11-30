let usernamePage = document.querySelector('#username-page');
let chatPage = document.querySelector('#chat-page');
let usernameForm = document.querySelector('#usernameForm');
let messageForm = document.querySelector('#messageForm');
let messageInput = document.querySelector('#message');
let messageArea = document.querySelector('#messageArea');
let usersOnlineArea = document.querySelector('#usersOnlineArea');
let connectingElement = document.querySelector('.connecting');
let users = [];

let stompClient = null;
let username = null;


let colors = [
    '#DC143C', '#7CFC00', '#FF1493', '#FFA07A',
    '#8A2BE2', '#6A5ACD', '#87CEEB', '#F0E68C',
    '#2196F3', '#32c787', '#00BCD4', '#ff5652',
    '#ffc107', '#ff85af', '#FF9800', '#39bbb0'
];

function connect(event) {
    username = document.querySelector('#name').value.trim();

    if(username) {
        usernamePage.classList.add('hidden');
        chatPage.classList.remove('hidden');

        let socket = new SockJS('/websocket');
        stompClient = Stomp.over(socket);

        stompClient.connect({}, onConnected, onError);
    }
    event.preventDefault();
}


function onConnected() {
    stompClient.subscribe('/topic/public', onMessageReceived);

    // Сообщаем серверу имя подключившегося пользователя
    stompClient.send("/app/addUserToChat",
        {},
        JSON.stringify({username: username, type: 'JOIN', sendingTime: new Date().toString().substring(4, 24)})
    )

    connectingElement.classList.add('hidden');
}

function onError(error) {
    connectingElement.textContent = 'Не удается подключиться к серверу WebSocket. Попробуйте еще раз!';
    connectingElement.style.color = 'darkred';
}


function sendMessage(event) {
    let messageContent = messageInput.value.trim();

    if(messageContent && stompClient) {
        let message = {
            sender: username,
            text: messageInput.value,
            sendingTime: new Date().toString().substring(4, 24),
            type: 'CHAT'
        };

        stompClient.send("/app/sendMessageToChat", {}, JSON.stringify(message));
        messageInput.value = '';
    }
    event.preventDefault();
}


function onMessageReceived(payload) {
    let chatMessage = JSON.parse(payload.body);

    console.log(chatMessage)

    let messageElement = document.createElement('li');

    if(chatMessage.message.type === 'JOIN') {
        messageElement.classList.add('event-message');
        users = chatMessage.usersOnline;
        chatMessage.message.text = chatMessage.message.user.login + ' вошел(-ла) в чат!';
    } else if (chatMessage.message.type === 'LEAVE') {
        users = chatMessage.usersOnline;
        messageElement.classList.add('event-message');
        chatMessage.message.text = chatMessage.message.user.login + ' покинул(-а) чат!';
        chatMessage.message.sendingTime = chatMessage.message.sendingTime.substring(0, 19)
    } else {
        messageElement.classList.add('chat-message');
        let avatarElement = document.createElement('i');
        let avatarText = document.createTextNode(chatMessage.message.user.login[0]);
        avatarElement.appendChild(avatarText);
        avatarElement.style['background-color'] = getAvatarColor(chatMessage.message.user.login);

        messageElement.appendChild(avatarElement);

        let usernameElement = document.createElement('span');
        let usernameText = document.createTextNode(chatMessage.message.user.login);
        usernameElement.appendChild(usernameText);
        messageElement.appendChild(usernameElement);
    }

    let textElement = document.createElement('p');
    let messageText = document.createTextNode(chatMessage.message.sendingTime.replace('T', ' ') + ': ' + chatMessage.message.text);
    textElement.appendChild(messageText);

    messageElement.appendChild(textElement);
    messageArea.appendChild(messageElement);
    messageArea.scrollTop = messageArea.scrollHeight;
    $('#usersOnlineArea').empty();
    $.each(users, function( key, value ) {
        $('#usersOnlineArea').append('<li>' + value + '</li>');
    });
    usersOnlineArea.scrollTop = usersOnlineArea.scrollHeight;


}


function getAvatarColor(messageSender) {
    let hash = 0;
    for (let i = 0; i < messageSender.length; i++) {
        hash = 31 * hash + messageSender.charCodeAt(i);
    }

    let index = Math.abs(hash % colors.length);
    return colors[index];
}


usernameForm.addEventListener('submit', connect, true)
messageForm.addEventListener('submit', sendMessage, true)