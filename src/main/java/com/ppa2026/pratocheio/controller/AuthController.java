package com.ppa2026.pratocheio.controller;

import com.ppa2026.pratocheio.dto.LoginForm;
import com.ppa2026.pratocheio.dto.RegistrationForm;
import com.ppa2026.pratocheio.model.User;
import com.ppa2026.pratocheio.service.UserService;
import jakarta.servlet.http.HttpSession;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping
@RequiredArgsConstructor
public class AuthController {
  private final UserService userService;

  @GetMapping("/cadastro")
  public String cadastro(Model model) {
    model.addAttribute("registrationForm", new RegistrationForm());
    return "cadastro";
  }

  @PostMapping("/cadastro")
  public String handleCadastro(RegistrationForm form, Model model) {
    if (isBlank(form.getName()) || isBlank(form.getPassword()) || isBlank(form.getCpf())
        || isBlank(form.getPhone())) {
      model.addAttribute("error", "Preencha todos os campos para concluir o cadastro.");
      model.addAttribute("registrationForm", form);
      return "cadastro";
    }
    userService.register(form);
    return "redirect:/entrar";
  }

  @GetMapping("/entrar")
  public String entrar(Model model) {
    model.addAttribute("loginForm", new LoginForm());
    return "entrar";
  }

  @PostMapping("/entrar")
  public String handleEntrar(LoginForm form, Model model, HttpSession session) {
    if (isBlank(form.getName()) || isBlank(form.getPassword())) {
      model.addAttribute("error", "Informe nome e senha para entrar.");
      model.addAttribute("loginForm", form);
      return "entrar";
    }
    Optional<User> user = userService.authenticate(form.getName(), form.getPassword());
    if (user.isEmpty()) {
      model.addAttribute("error", "Usuário ou senha inválidos.");
      model.addAttribute("loginForm", form);
      return "entrar";
    }
    session.setAttribute(HomeController.SESSION_USER_ID, user.get().getId());
    return "redirect:/usuario";
  }

  @GetMapping("/sair")
  public String sair(HttpSession session) {
    session.invalidate();
    return "redirect:/";
  }

  private boolean isBlank(String value) {
    return value == null || value.isBlank();
  }
}
