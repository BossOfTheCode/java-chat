package spring.javachat.controllers;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
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

    private final UserService userService;
    private final MessageService messageService;

    @Autowired
    public ChatController(UserService userService, MessageService messageService) {
        this.userService = userService;
        this.messageService = messageService;
    }

    @MessageMapping("/sendMessageToChat")
    @SendTo("/topic/public")
    public ChatMessage sendMessage(@Payload String jsonMessage) {
        //Отправляем сообщение всем пользователям в сессии
        JSONObject jsonObject = new JSONObject(jsonMessage);
        Message message = new Message();
        message.setUser(userService.findUserByLogin(jsonObject.getString("sender")));
        message.setText(jsonObject.getString("text"));
        message.setSendingTime(LocalDateTime.parse(jsonObject.getString("sendingTime"), formatter));
        message.setType(Message.MessageType.CHAT);
        messageService.saveMessage(message);
        return new ChatMessage(message, UserService.getUsersOnline());
    }

    @MessageMapping("/addUserToChat")
    @SendTo("/topic/public")
    public ChatMessage addUser(@Payload String json, SimpMessageHeaderAccessor headerAccessor) {
        // Добавляем пользователя в сессию WS
        JSONObject jsonObject = new JSONObject(json);
        Message message = new Message();
        message.setUser(userService.findUserByLogin(jsonObject.getString("username")));
        message.setType(Message.MessageType.JOIN);
        message.setText(message.getUser().getLogin() + " вошел(-ла) в чат!");
        message.setSendingTime(LocalDateTime.parse(jsonObject.getString("sendingTime"), formatter));
        messageService.saveMessage(message);
        Objects.requireNonNull(headerAccessor.getSessionAttributes()).put("user", message.getUser());
        List<String> usersOnline = UserService.getUsersOnline();
        usersOnline.add(message.getUser().getLogin());
        return new ChatMessage(message, usersOnline);
    }

    public static class ChatMessage {
        private Message message;
        private List<String> usersOnline;

        public ChatMessage(Message message, List<String> usersOnline) {
            this.message = message;
            this.usersOnline = usersOnline;
        }

        public Message getMessage() {
            return message;
        }

        public void setMessage(Message message) {
            this.message = message;
        }

        public List<String> getUsersOnline() {
            return usersOnline;
        }

        public void setUsersOnline(List<String> usersOnline) {
            this.usersOnline = usersOnline;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            ChatMessage that = (ChatMessage) o;
            return Objects.equals(message, that.message) && Objects.equals(usersOnline, that.usersOnline);
        }

        @Override
        public int hashCode() {
            return Objects.hash(message, usersOnline);
        }

        @Override
        public String toString() {
            return "ChatMessage{" +
                    "message=" + message +
                    ", usersOnline=" + usersOnline +
                    '}';
        }
    }
}
