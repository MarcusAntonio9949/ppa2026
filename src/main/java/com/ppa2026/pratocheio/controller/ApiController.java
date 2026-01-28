package com.ppa2026.pratocheio.controller;

import com.ppa2026.pratocheio.dto.DonationForm;
import com.ppa2026.pratocheio.dto.LoginForm;
import com.ppa2026.pratocheio.dto.RegistrationForm;
import com.ppa2026.pratocheio.dto.VolunteerForm;
import com.ppa2026.pratocheio.model.Donation;
import com.ppa2026.pratocheio.model.User;
import com.ppa2026.pratocheio.service.DonationService;
import com.ppa2026.pratocheio.service.UserService;
import com.ppa2026.pratocheio.service.VolunteerService;
import jakarta.servlet.http.HttpSession;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ApiController {
  public static final String SESSION_USER_ID = "userId";
  private static final String SESSION_ADMIN = "admin";
  private static final String ADMIN_USERNAME = "ADMIN";
  private static final String ADMIN_PASSWORD = "PASSWORD";

  private final UserService userService;
  private final DonationService donationService;
  private final VolunteerService volunteerService;

  @PostMapping("/cadastro")
  public ResponseEntity<ApiResponse> cadastro(@RequestBody RegistrationForm form) {
    if (isBlank(form.getName()) || isBlank(form.getPassword()) || isBlank(form.getCpf())
        || isBlank(form.getPhone())) {
      return ResponseEntity.badRequest().body(new ApiResponse("Preencha todos os campos para concluir o cadastro."));
    }
    userService.register(form);
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(new ApiResponse("Cadastro realizado com sucesso!"));
  }

  @PostMapping("/entrar")
  public ResponseEntity<?> entrar(@RequestBody LoginForm form, HttpSession session) {
    if (isBlank(form.getName()) || isBlank(form.getPassword())) {
      return ResponseEntity.badRequest().body(new ApiResponse("Informe nome e senha para entrar."));
    }
    if (ADMIN_USERNAME.equals(form.getName()) && ADMIN_PASSWORD.equals(form.getPassword())) {
      session.setAttribute(SESSION_ADMIN, true);
      return ResponseEntity.ok(new AdminLogin(true));
    }
    Optional<User> user = userService.authenticate(form.getName(), form.getPassword());
    if (user.isEmpty()) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
          .body(new ApiResponse("Usuário ou senha inválidos."));
    }
    session.setAttribute(SESSION_USER_ID, user.get().getId());
    return ResponseEntity.ok(UserProfile.from(user.get(), donationService.findByUser(user.get())));
  }

  @GetMapping("/usuario")
  public ResponseEntity<?> usuario(HttpSession session, @RequestParam(required = false) Long userId) {
    Optional<User> user = getSessionUser(session);
    if (user.isEmpty() && userId != null) {
      user = userService.findById(userId);
    }
    if (user.isEmpty()) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
          .body(new ApiResponse("Faça login para acessar a conta."));
    }
    User current = user.get();
    return ResponseEntity.ok(UserProfile.from(current, donationService.findByUser(current)));
  }

  @PostMapping("/doar")
  public ResponseEntity<ApiResponse> doar(@RequestBody DonationForm form, HttpSession session) {
    Optional<User> user = getSessionUser(session);
    if (user.isEmpty() && form.getUserId() != null) {
      user = userService.findById(form.getUserId());
    }
    if (user.isEmpty()) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
          .body(new ApiResponse("Entre ou cadastre-se para registrar a doação."));
    }
    if (isBlank(form.getDescription()) || form.getPickupOption() == null) {
      return ResponseEntity.badRequest()
          .body(new ApiResponse("Preencha a descrição e escolha como deseja doar."));
    }
    donationService.createDonation(form, user.get());
    return ResponseEntity.ok(new ApiResponse("Doação registrada com sucesso!"));
  }

  @PostMapping("/voluntario")
  public ResponseEntity<ApiResponse> voluntario(@RequestBody VolunteerForm form) {
    if (isBlank(form.getName()) || isBlank(form.getEmail()) || isBlank(form.getPhone())
        || isBlank(form.getAvailability())) {
      return ResponseEntity.badRequest()
          .body(new ApiResponse("Preencha todos os campos para enviar a candidatura."));
    }
    volunteerService.createApplication(form);
    return ResponseEntity.ok(new ApiResponse("Candidatura enviada! Em breve entraremos em contato."));
  }

  @GetMapping("/sair")
  public ResponseEntity<ApiResponse> sair(HttpSession session) {
    session.invalidate();
    return ResponseEntity.ok(new ApiResponse("Sessão encerrada."));
  }

  @GetMapping("/admin/overview")
  public ResponseEntity<?> adminOverview(HttpSession session, @RequestParam(required = false) Boolean admin) {
    if (!isAdmin(session, admin)) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
          .body(new ApiResponse("Acesso restrito ao administrador."));
    }
    return ResponseEntity.ok(AdminOverview.from(
        userService.findAll(),
        donationService.findAll(),
        volunteerService.findAll()));
  }

  private Optional<User> getSessionUser(HttpSession session) {
    Object id = session.getAttribute(SESSION_USER_ID);
    if (id instanceof Long userId) {
      return userService.findById(userId);
    }
    return Optional.empty();
  }

  private boolean isBlank(String value) {
    return value == null || value.isBlank();
  }

  private boolean isAdmin(HttpSession session, Boolean admin) {
    Object flag = session.getAttribute(SESSION_ADMIN);
    if (flag instanceof Boolean isAdmin && isAdmin) {
      return true;
    }
    return Boolean.TRUE.equals(admin);
  }

  public record ApiResponse(String message) {}

  public record AdminLogin(boolean admin) {}

  public record AdminOverview(
      List<UserSummary> users,
      List<DonationSummary> donations,
      List<VolunteerSummary> volunteers
  ) {
    static AdminOverview from(List<User> users, List<Donation> donations,
                              List<com.ppa2026.pratocheio.model.VolunteerApplication> volunteers) {
      return new AdminOverview(
          users.stream().map(UserSummary::from).collect(Collectors.toList()),
          donations.stream().map(DonationSummary::from).collect(Collectors.toList()),
          volunteers.stream().map(VolunteerSummary::from).collect(Collectors.toList())
      );
    }
  }

  public record UserSummary(Long id, String name, String cpf, String phone, int points) {
    static UserSummary from(User user) {
      return new UserSummary(user.getId(), user.getName(), user.getCpf(), user.getPhone(), user.getPoints());
    }
  }

  public record VolunteerSummary(Long id, String name, String email, String phone,
                                 String availability, String createdAt) {
    static VolunteerSummary from(com.ppa2026.pratocheio.model.VolunteerApplication volunteer) {
      return new VolunteerSummary(
          volunteer.getId(),
          volunteer.getName(),
          volunteer.getEmail(),
          volunteer.getPhone(),
          volunteer.getAvailability(),
          volunteer.getCreatedAt().toString()
      );
    }
  }

  public record DonationSummary(String description, String pickupOption, String createdAt) {
    static DonationSummary from(Donation donation) {
      return new DonationSummary(
          donation.getDescription(),
          donation.getPickupOption().getLabel(),
          donation.getCreatedAt().toString());
    }
  }

  public record UserProfile(Long id, String name, String cpf, String phone, int points,
                             List<DonationSummary> donations) {
    static UserProfile from(User user, List<Donation> donations) {
      List<DonationSummary> summaries = donations.stream()
          .map(DonationSummary::from)
          .collect(Collectors.toList());
      return new UserProfile(user.getId(), user.getName(), user.getCpf(), user.getPhone(),
          user.getPoints(), summaries);
    }
  }
}
