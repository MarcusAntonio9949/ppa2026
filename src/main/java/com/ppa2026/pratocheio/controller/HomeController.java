package com.ppa2026.pratocheio.controller;

import com.ppa2026.pratocheio.model.User;
import com.ppa2026.pratocheio.service.UserService;
import jakarta.servlet.http.HttpSession;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class HomeController {
  public static final String SESSION_USER_ID = "userId";

  private final UserService userService;

  @GetMapping("/")
  public String home(Model model, HttpSession session) {
    Optional<User> user = getSessionUser(session);
    user.ifPresent(value -> model.addAttribute("user", value));
    return "main";
  }

  Optional<User> getSessionUser(HttpSession session) {
    Object id = session.getAttribute(SESSION_USER_ID);
    if (id instanceof Long userId) {
      return userService.findById(userId);
    }
    return Optional.empty();
  }
}
