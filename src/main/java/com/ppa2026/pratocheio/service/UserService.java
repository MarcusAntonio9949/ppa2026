package com.ppa2026.pratocheio.service;

import com.ppa2026.pratocheio.dto.RegistrationForm;
import com.ppa2026.pratocheio.model.User;
import com.ppa2026.pratocheio.repository.UserRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
  private final UserRepository userRepository;

  public User register(RegistrationForm form) {
    User user = User.builder()
        .name(form.getName())
        .password(form.getPassword())
        .cpf(form.getCpf())
        .phone(form.getPhone())
        .points(0)
        .build();
    return userRepository.save(user);
  }

  public Optional<User> authenticate(String name, String password) {
    return userRepository.findByNameAndPassword(name, password);
  }

  public Optional<User> findById(Long id) {
    return userRepository.findById(id);
  }

  public User save(User user) {
    return userRepository.save(user);
  }
}
