package spring.javachat.listeners;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import spring.javachat.controllers.ChatController;
import spring.javachat.models.entity.Message;
import spring.javachat.models.entity.User;
import spring.javachat.models.service.MessageService;
import spring.javachat.models.service.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Component
public class WebSocketEventListener {

    private final MessageService messageService;

    private static final Logger logger = LoggerFactory.getLogger(WebSocketEventListener.class);

    private final SimpMessageSendingOperations messagingTemplate;

    @Autowired
    public WebSocketEventListener(MessageService messageService, UserService userService, SimpMessageSendingOperations messagingTemplate) {
        this.messageService = messageService;
        this.messagingTemplate = messagingTemplate;
    }

    @EventListener
    public void handleWebSocketConnectListener(SessionConnectedEvent event) {
        logger.info("Получено новое соединение");
    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        User user = (User) Objects.requireNonNull(headerAccessor.getSessionAttributes()).get("user");
        if(user != null) {
            logger.info("Пользователь вышел из чата : " + user.getLogin());
            Message message = new Message();
            message.setType(Message.MessageType.LEAVE);
            message.setUser(user);
            message.setText(message.getUser().getLogin() + " покинул(-а) чат!");
            message.setSendingTime(LocalDateTime.now().withNano(0));
            messageService.saveMessage(message);
            List<String> usersOnline = UserService.getUsersOnline();
            usersOnline.remove(user.getLogin());
            ChatController.ChatMessage chatMessage = new ChatController.ChatMessage(message, usersOnline);
            messagingTemplate.convertAndSend("/topic/public", chatMessage);
        }
    }
}