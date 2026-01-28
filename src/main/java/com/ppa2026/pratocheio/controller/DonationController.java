package com.ppa2026.pratocheio.controller;

import com.ppa2026.pratocheio.dto.DonationForm;
import com.ppa2026.pratocheio.model.PickupOption;
import com.ppa2026.pratocheio.model.User;
import com.ppa2026.pratocheio.service.DonationService;
import com.ppa2026.pratocheio.service.UserService;
import jakarta.servlet.http.HttpSession;
import java.util.Arrays;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
public class DonationController {
  private final DonationService donationService;
  private final UserService userService;

  @GetMapping("/doar")
  public String doa(Model model, HttpSession session) {
    populateDonationModel(model, session);
    return "doa";
  }

  @PostMapping("/doar")
  public String handleDoa(DonationForm form, Model model, HttpSession session) {
    Optional<User> user = getSessionUser(session);
    if (user.isEmpty()) {
      populateDonationModel(model, session);
      model.addAttribute("error", "Entre ou cadastre-se para registrar a doação.");
      model.addAttribute("donationForm", form);
      return "doa";
    }
    if (form.getDescription() == null || form.getDescription().isBlank() || form.getPickupOption() == null) {
      populateDonationModel(model, session);
      model.addAttribute("error", "Preencha a descrição e escolha como deseja doar.");
      model.addAttribute("donationForm", form);
      return "doa";
    }
    donationService.createDonation(form, user.get());
    return "redirect:/usuario";
  }

  private void populateDonationModel(Model model, HttpSession session) {
    model.addAttribute("donationForm", new DonationForm());
    model.addAttribute("pickupOptions", Arrays.asList(PickupOption.values()));
    getSessionUser(session).ifPresent(value -> model.addAttribute("user", value));
  }

  private Optional<User> getSessionUser(HttpSession session) {
    Object id = session.getAttribute(HomeController.SESSION_USER_ID);
    if (id instanceof Long userId) {
      return userService.findById(userId);
    }
    return Optional.empty();
  }
}
