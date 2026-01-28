package com.ppa2026.pratocheio.controller;

import com.ppa2026.pratocheio.dto.VolunteerForm;
import com.ppa2026.pratocheio.service.VolunteerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
public class VolunteerController {
  private final VolunteerService volunteerService;

  @GetMapping("/voluntario")
  public String voluntario(Model model) {
    model.addAttribute("volunteerForm", new VolunteerForm());
    return "voluntario";
  }

  @PostMapping("/voluntario")
  public String handleVoluntario(VolunteerForm form, Model model) {
    if (isBlank(form.getName()) || isBlank(form.getEmail()) || isBlank(form.getPhone())
        || isBlank(form.getAvailability())) {
      model.addAttribute("error", "Preencha todos os campos para enviar a candidatura.");
      model.addAttribute("volunteerForm", form);
      return "voluntario";
    }
    volunteerService.createApplication(form);
    model.addAttribute("success", "Candidatura enviada! Em breve entraremos em contato.");
    model.addAttribute("volunteerForm", new VolunteerForm());
    return "voluntario";
  }

  private boolean isBlank(String value) {
    return value == null || value.isBlank();
  }
}
