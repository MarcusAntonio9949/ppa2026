package com.ppa2026.pratocheio.controller;

import com.ppa2026.pratocheio.model.User;
import com.ppa2026.pratocheio.service.DonationService;
import com.ppa2026.pratocheio.service.UserService;
import jakarta.servlet.http.HttpSession;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class UserController {
  private final UserService userService;
  private final DonationService donationService;

  @GetMapping("/usuario")
  public String usuario(Model model, HttpSession session) {
    Optional<User> user = getSessionUser(session);
    if (user.isEmpty()) {
      return "redirect:/entrar";
    }
    User current = user.get();
    model.addAttribute("user", current);
    model.addAttribute("donations", donationService.findByUser(current));
    return "userPage";
  }

  private Optional<User> getSessionUser(HttpSession session) {
    Object id = session.getAttribute(HomeController.SESSION_USER_ID);
    if (id instanceof Long userId) {
      return userService.findById(userId);
    }
    return Optional.empty();
  }
}
