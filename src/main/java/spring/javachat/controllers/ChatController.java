package spring.javachat.controllers;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Controller;
import spring.javachat.models.entity.Message;
import spring.javachat.models.service.MessageService;
import spring.javachat.models.service.UserService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

@Controller
public class ChatController {

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd yyyy HH:mm:ss", Locale.US);
    private final SimpMessageSendingOperations messagingTemplate;
    private final UserService userService;
    private final MessageService messageService;

    @Autowired
    public ChatController(SimpMessageSendingOperations messagingTemplate, UserService userService, MessageService messageService) {
        this.messagingTemplate = messagingTemplate;
        this.userService = userService;
        this.messageService = messageService;
    }

    @MessageMapping("/sendMessageToChat")
    @SendTo("/topic/public")
    public Message sendMessage(@Payload String jsonMessage) {
        //Отправляем сообщение всем пользователям в чат
        JSONObject jsonObject = new JSONObject(jsonMessage);
        Message message = new Message();
        message.setUser(userService.findUserByLogin(jsonObject.getString("sender")));
        message.setText(jsonObject.getString("text"));
        message.setSendingTime(LocalDateTime.parse(jsonObject.getString("sendingTime"), formatter));
        message.setType(Message.MessageType.CHAT);
        messageService.saveMessage(message);
        return message;
    }

    @MessageMapping("/addUserToChat")
    @SendTo("/topic/public")
    public Message addUser(@Payload String json, SimpMessageHeaderAccessor headerAccessor) {
        // Добавляем пользователя в чат
        JSONObject jsonObject = new JSONObject(json);
        Message message = new Message();
        message.setUser(userService.findUserByLogin(jsonObject.getString("user")));
        message.setText(jsonObject.getString("text"));
        message.setSendingTime(LocalDateTime.parse(jsonObject.getString("sendingTime"), formatter));
        message.setType(Message.MessageType.JOIN);
        messageService.saveMessage(message);
        Objects.requireNonNull(headerAccessor.getSessionAttributes()).put("user", message.getUser());
        List<String> namesOfUsersOnline = UserService.getNamesOfUsersOnline();
        namesOfUsersOnline.add(message.getUser().getLogin());
        messagingTemplate.convertAndSend("/topic/users", namesOfUsersOnline);
        return message;
    }
}
