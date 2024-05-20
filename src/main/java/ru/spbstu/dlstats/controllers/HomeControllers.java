package ru.spbstu.dlstats.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.web.authentication.RememberMeServices;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.spbstu.dlstats.managers.StaffAuthenticationManager;


@Controller
@RequestMapping({"/", "/home", "/index"})
public class HomeControllers {

    private final RememberMeServices rememberMeServices;

    private final StaffAuthenticationManager staffAuthenticationManager;


    @Autowired
    public HomeControllers(RememberMeServices rememberMeServices,
                           StaffAuthenticationManager staffAuthenticationManager) {
        this.rememberMeServices = rememberMeServices;
        this.staffAuthenticationManager = staffAuthenticationManager;
    }


    @GetMapping({"/", "/home", "/index"})
    public String initializeBasePage(
            @RequestParam(value = "error", required = false) String error,
            Model model) {
        if (error != null) {
            model.addAttribute("errorMassage", "Ошибка входа");
        }
        model.addAttribute("title", "dl-stat");
        return "/index";
    }


    /*@PostMapping("/login")
    public String login(@RequestParam String username,
                        @RequestParam String password,
                        @RequestParam String remember,
                        HttpServletRequest request,
                        HttpServletResponse response) {

        val authorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + RoleConst.USER));
        val auth = new UsernamePasswordAuthenticationToken(username, password, authorities);
        // пользователю не прошедшего авторизацию необходимо выдать ошибку
        try {
            this.staffAuthenticationManager.authenticate(auth);
        } catch (AuthenticationException ex) {
            return "redirect:/?error";
        }

        SecurityContextHolder.getContext().setAuthentication(auth);
        if (remember.equals("true")) {
            rememberMeServices.loginSuccess(request, response, auth);
        }

        return "redirect:/staff/menu";
    }*/
}
