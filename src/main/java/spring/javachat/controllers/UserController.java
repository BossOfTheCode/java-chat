package spring.javachat.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import spring.javachat.models.entity.BannedUser;
import spring.javachat.models.entity.Role;
import spring.javachat.models.entity.User;
import spring.javachat.models.service.RoleService;
import spring.javachat.models.service.UserService;
import javax.validation.Valid;
import java.util.List;

@Controller
public class UserController {
    private final UserService userService;
    private final RoleService roleService;

    @Autowired
    public UserController(UserService userService, RoleService roleService) {
        this.userService = userService;
        this.roleService = roleService;
    }


    @GetMapping("/signup")
    public String showSignUpForm(Model model) {
        model.addAttribute("user", new User());
        return "signup";
    }

    @PostMapping("/signup")
    public String registerNewUser(@Valid User user, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors())
            return "signup";
        if (userService.findUserByLogin(user.getLogin()) != null) {
            model.addAttribute("userExists", "Такой пользователь уже зарегистрирован!");
            return "signup";
        }
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String encodedPassword = encoder.encode(user.getPassword());
        user.setPassword(encodedPassword);
        userService.save(user);
        return "signup_success";
    }

    @GetMapping("/login")
    public String showLoginForm() {
        return "login";
    }

    @PostMapping("/login")
    public String userLogin(User user, Model model,
                          @RequestParam(value = "error", required = false) String error,
                          @RequestParam(value = "logout", required = false) String logout) {
        model.addAttribute("error", error != null);
        model.addAttribute("logout", logout != null);
        return "login";
    }

    @GetMapping("/admin")
    public String admin() {
        return "admin";
    }

    @GetMapping("/admin/users")
    public String showUsersList(Model model){
        List<User> users  = userService.findAll();
        model.addAttribute("users", users);
        return "users";
    }


    @GetMapping("/admin/users/edit/{id}")
    public String showUserEditForm(@PathVariable("id") Integer Id, Model model){
        User user = userService.findUserById(Id);
        System.out.println(user);
        List<Role> roles = roleService.findAll();
        model.addAttribute("user", user);
        model.addAttribute("listOfRoles", roles);
        return "user-edit";
    }

    @PostMapping("/admin/users/edit")
    public String saveEditedUser(User user){
        userService.saveEditedUser(user);
        return "redirect:/admin/users";
    }

    @GetMapping("/admin/users/ban/{id}")
    public String showUserBanForm(@PathVariable("id") Integer Id, Model model){
        BannedUser bannedUser = new BannedUser();
        User user = userService.findUserById(Id);
        bannedUser.setUser(user);
        model.addAttribute("user", user);
        model.addAttribute("bannedUser", bannedUser);
        return "user-ban";
    }

    @GetMapping("/admin/users/delete/{id}")
    public String deleteUser(@PathVariable("id") Integer id){
        userService.deleteUserById(id);
        return "redirect:/admin/users";
    }
}
