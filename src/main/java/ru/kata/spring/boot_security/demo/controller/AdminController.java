package ru.kata.spring.boot_security.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.service.UserService;
import ru.kata.spring.boot_security.demo.util.UserNewValidator;

import javax.validation.Valid;

@Controller
public class AdminController {

    private final ru.kata.spring.boot_security.demo.service.RoleService roleService;
    private final UserService userService;
    private final UserNewValidator userNewValidator;


    @Autowired
    public AdminController(ru.kata.spring.boot_security.demo.service.RoleService roleService, UserService userService, UserNewValidator userNewValidator) {
        this.roleService = roleService;
        this.userService = userService;
        this.userNewValidator = userNewValidator;
    }

    // название методов - максимально подробно, согласно конвенции, в названии используются глаголы

    @GetMapping("/admin/user") // отображаем всех пользователей,
    public String showAllUser(Model model) {  // модель для передачи данных в представление
        model.addAttribute("users", userService.findAll());
        return "admin_panel_user_table";
    }

    @GetMapping("/admin/{id}") // отображаем конкретного пользователя
    public String showOneUser(@PathVariable("id") int id, Model model) {
        model.addAttribute("user", userService.findOne(id));
        return "show";
    }

    @GetMapping("/admin/new")   // отображаем страницу создания нового пользователя
    public String showPageCreatingUser(Model model) {
        model.addAttribute("user", new User());
        model.addAttribute("allRoles", roleService.getRoles());
        return "new";
    }

    @PostMapping("/admin/user") //создаем нового пользователя
    public String createNewUser(@ModelAttribute("user") @Valid User user, BindingResult bindingResult) {
        userNewValidator.validate(user, bindingResult);
        if (bindingResult.hasErrors()) {
            return "new";
        }
        userService.save(user);
        return "redirect:/admin/user";
    }

    @GetMapping("/admin/{id}/update")  // отображение страницы редактирование пользователя
    public String showPageEditUser(Model model, @PathVariable("id") int id) {
        model.addAttribute("user", userService.findOne(id));
        model.addAttribute("allRoles", roleService.getRoles());
        return "update";
    }

    @PatchMapping("/admin/{id}") // обновление информации о пользователе
    public String updateDateUser(@ModelAttribute("user") @Valid User user, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "update";
        }
        userService.update(user);
        return "redirect:user";
    }

    @DeleteMapping("/admin/{id}") // удаляем пользователя с указанным id
    public String deleteUser(@PathVariable("id") int id) {
        userService.delete(id);
        return "redirect:user";
    }

}
