package spring.javachat.controllers;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;

import org.springframework.web.bind.annotation.RestController;
import spring.javachat.models.entity.Message;
import spring.javachat.models.service.MessageService;
import spring.javachat.models.service.UserService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;


@RestController
public class MessageController {

    private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd yyyy HH:mm:ss", Locale.US);

    @Autowired
    private MessageService messageService;
    @Autowired
    private UserService userService;

    @MessageMapping("/chat.sendMessage")
    @SendTo("/topic/public")
    public Message sendMessage(@Payload String jsonMessage) {
        //Отправляем сообщение всем пользователям в сессии
        JSONObject jsonObject = new JSONObject(jsonMessage);
        Message message = new Message();
        message.setUser(userService.findUserByLogin(jsonObject.getString("sender")));
        message.setText(jsonObject.getString("text"));
        message.setSendingTime(LocalDateTime.parse(jsonObject.getString("sendingTime"), formatter));
        message.setType(Message.MessageType.CHAT);
        messageService.createMessage(message);
        return message;
    }

    @MessageMapping("/chat.addUser")
    @SendTo("/topic/public")
    public Message addUser(@Payload String json,
                               SimpMessageHeaderAccessor headerAccessor) {
        // Добавляем пользователя в сессию WS
        JSONObject jsonObject = new JSONObject(json);
        Message chatMessage = new Message();
        chatMessage.setUser(userService.findUserByLogin(jsonObject.getString("username")));
        chatMessage.setType(Message.MessageType.JOIN);
        chatMessage.setText(chatMessage.getUser().getLogin() + " вошел(-ла) в чат!");
        chatMessage.setSendingTime(LocalDateTime.parse(jsonObject.getString("sendingTime"), formatter));
        messageService.createMessage(chatMessage);
        headerAccessor.getSessionAttributes().put("user", chatMessage.getUser());
        return chatMessage;
    }

}
