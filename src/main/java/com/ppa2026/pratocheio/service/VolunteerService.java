package com.ppa2026.pratocheio.service;

import com.ppa2026.pratocheio.dto.VolunteerForm;
import com.ppa2026.pratocheio.model.VolunteerApplication;
import com.ppa2026.pratocheio.repository.VolunteerApplicationRepository;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class VolunteerService {
  private final VolunteerApplicationRepository volunteerRepository;

  public VolunteerApplication createApplication(VolunteerForm form) {
    VolunteerApplication application = VolunteerApplication.builder()
        .name(form.getName())
        .email(form.getEmail())
        .phone(form.getPhone())
        .availability(form.getAvailability())
        .createdAt(LocalDateTime.now())
        .build();
    return volunteerRepository.save(application);
  }
}
