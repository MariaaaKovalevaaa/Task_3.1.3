package ru.kata.spring.boot_security.demo.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import ru.kata.spring.boot_security.demo.models.User;
import org.springframework.web.bind.annotation.*;
import ru.kata.spring.boot_security.demo.services.RoleService;
import ru.kata.spring.boot_security.demo.services.UserService;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final RoleService roleService;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public AdminController(RoleService roleService, UserService userService, PasswordEncoder passwordEncoder) {
        this.roleService = roleService;
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping()
    public String showAllUsers(Model model) {
        model.addAttribute("users", userService.findAll());//это пара ключ-значение. Ключ "users" будет во вьюшке list-users
        return "list-users";
    }

    @GetMapping("/user-profile/{id}")
    public String findUser(@PathVariable("id") Long id, Model model) {
        User user = userService.findUserById(id);
        model.addAttribute("user", user);
        model.addAttribute("userRoles", user.getRoles());
        return "/user-profile";
    }

    /**
     * //Метод, выводящий форму для редактирования - edit
     * // Вьюшка "edit". Из нее данные отправляются на контроллер,
     * // т.е. на @RequestMapping("/admin"), а там на метод, принимающий PATCH-запросы.
     * // Таким будет метод - save_changes с аннотацией @PatchMapping
     * //В методе editUser помещаем в модель юзера с переданным из url-адреса id
     **/
    @GetMapping("/edit/{id}")
    public String editUser(Model model, @PathVariable("id") Long id) {
        model.addAttribute("user", userService.findUserById(id));
        model.addAttribute("userRoles", roleService.findAll());
        return "/edit";
    }

    /**
     * //Метод, который принимает patch-запрос от вьюшки "edit"
     * // т.е. когда во view "edit" форму заполнили,
     * // нажали кнопку "Внести изменения", нас перебросит на url-адрес "/admin" на PATCH-метод
     * //@PatchMapping, потому что мы вносим при этом изменения, это заплатка сверху на то, что было
     * //Сюда придут новые данные юзера, которые хотят изменить
     * //И так, это метод сохраняющий изменения, которые пришли с формы метода editUser - это будет
     * // метод save_changes с аннотацией PATCH - @PatchMapping,
     * // Вьюшка будет редирект на url "/list-users"
     **/
    @PatchMapping("/save-changes/")
    public String save_changes(@ModelAttribute("user") User updateUser) {
        userService.saveUser(updateUser); //Находим по id того юзера, которого надо изменить
        return "redirect:/list-users";
    }

    @DeleteMapping("/delete-user/{id}")
    public String deleteUserById(@PathVariable("id") Long id) {
        userService.deleteUserById(id);
        return "redirect:/list-users";
    }


    /**
     * //Метод создания юзера - registration. Здесь хотим получить форму д/заполнения, поэтому аннотация с GET
     * // Будет появляться вьюшка "registration" для заполнения данных и потом перенаправлятся
     * // в контроллер на метод с аннотацией POST, поэтому на @RequestMapping("/admin"),
     * // а там на метод с аннотацией POST, т.е. @PostMapping(/create-user),
     * // А таким методом будет метод saveUser
     **/
    @GetMapping("/form_for_create_user")
    public String form_for_create_user(Model model, User user) {
        model.addAttribute("user", new User());
        model.addAttribute("userRoles", user.getAuthorities());
        return "/form_for_create_user";
    }

    /**
     * //Метод сохранения только что добавленного юзера - saveNewUser
     * //Метод, принимающий POST-запросы, т.е. @PostMapping(), после выполнения будет редирект на вьюшку "/list-users"
     * //User user - это созданный юзер по html-форме, которая реализована во view "registration"
     * //Аннотация @ModelAttribute создаст юзера с теми значениями, которые придут из формы вьюшки "registration".
     * //Мы здесь принимаем в аргумент юзера, который пришел из заполненной формы
     * //Аннотация @ModelAttribute делает: создание нового объекта, добавление значений
     * //в поля этого объекта и затем добавление этого объекта в модель
     **/

    @PostMapping("/creating-user")
    public String saveNewUser(@ModelAttribute("user") User user) {
        userService.saveUser(user); // Добавляем этого юзера в БД
        return "redirect:/list-users";
    }
}
