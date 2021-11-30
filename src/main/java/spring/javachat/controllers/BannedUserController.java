package spring.javachat.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import spring.javachat.models.entity.BannedUser;
import spring.javachat.models.service.BannedUserService;
import spring.javachat.models.service.CustomUserDetails;

import java.util.List;

@Controller
public class BannedUserController {
    private final BannedUserService bannedUserService;

    @Autowired
    public BannedUserController(BannedUserService bannedUserService) {
        this.bannedUserService = bannedUserService;
    }

    @GetMapping("/admin/banned")
    public String showBannedUsersList(Model model){
        List<BannedUser> bannedUsers  = bannedUserService.findAll();
        model.addAttribute("bannedUsers", bannedUsers);
        return "banned";
    }

    @PostMapping("/admin/users/ban")
    public String banUser(BannedUser bannedUser){
        if (bannedUserService.findBanByUserId(bannedUser.getUser().getId()) == null)
            bannedUserService.ban(bannedUser);
        return "redirect:/admin/users";
    }

    @GetMapping("/admin/banned/unban/{id}")
    public String unBanUser(@PathVariable("id") Integer id){
        BannedUser bannedUser = bannedUserService.findBanByUserId(id);
        bannedUserService.unBan(bannedUser);
        return "redirect:/admin/banned";
    }

    @GetMapping("/chat")
    public String showChat(Model model) {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = ((CustomUserDetails) principal).getUsername();
        for (BannedUser bannedUser : bannedUserService.findAll()) {
            if (bannedUser.getUser().getLogin().equals(username)) {
                model.addAttribute("userBanned", "Пользователь заблокирован по причине: " + bannedUser.getReason());
                return "ban";
            }
        }
        model.addAttribute("name", username);
        return "chat";
    }

    @GetMapping("/ban")
    public String showBan() {
        return "ban";
    }
}
