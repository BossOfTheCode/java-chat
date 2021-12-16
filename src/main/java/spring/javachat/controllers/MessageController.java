package spring.javachat.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import spring.javachat.models.entity.Message;
import spring.javachat.models.entity.User;
import spring.javachat.models.service.MessageService;
import java.util.List;


@Controller
public class MessageController {

    private final MessageService messageService;

    @Autowired
    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    @GetMapping("/admin/messages")
    public String showAllMessages(Model model){
        List<Message> messages  = messageService.findAll();
        model.addAttribute("messages", messages);
        return "messages";
    }

    @PostMapping("/admin/messages")
    public String findUserMessages(User user) {
        return "redirect:/admin/messages/" + user.getLogin();
    }

    @GetMapping("/admin/messages/{userName}")
    public String showUserMessages(@PathVariable("userName") String userName, Model model){
        List<Message> userMessages = messageService.findMessagesByUserName(userName);
        model.addAttribute("messages", userMessages);
        return "user-messages";
    }
}
