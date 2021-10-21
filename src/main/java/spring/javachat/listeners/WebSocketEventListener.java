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
import spring.javachat.models.entity.Message;
import spring.javachat.models.entity.User;
import spring.javachat.models.service.MessageService;

import java.time.LocalDateTime;

@Component
public class WebSocketEventListener {

    @Autowired
    private MessageService messageService;

    @Autowired
    private SimpMessageSendingOperations messagingTemplate;

    private static final Logger logger = LoggerFactory.getLogger(WebSocketEventListener.class);

    @EventListener
    public void handleWebSocketConnectListener(SessionConnectedEvent event) {
        logger.info("Получено новое соединение");
    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        User user = (User) headerAccessor.getSessionAttributes().get("user");
        if(user != null) {
            logger.info("Пользователь вышел из чата : " + user.getLogin());
            Message message = new Message();
            message.setType(Message.MessageType.LEAVE);
            message.setUser(user);
            message.setText(message.getUser().getLogin() + " покинул(-а) чат!");
            message.setSendingTime(LocalDateTime.now());
            messageService.createMessage(message);
            messagingTemplate.convertAndSend("/topic/public", message);
        }
    }
}