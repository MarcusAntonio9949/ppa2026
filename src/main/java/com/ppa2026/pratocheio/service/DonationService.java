package com.ppa2026.pratocheio.service;

import com.ppa2026.pratocheio.dto.DonationForm;
import com.ppa2026.pratocheio.model.Donation;
import com.ppa2026.pratocheio.model.User;
import com.ppa2026.pratocheio.repository.DonationRepository;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DonationService {
  private static final int POINTS_PER_DONATION = 10;

  private final DonationRepository donationRepository;
  private final UserService userService;

  public Donation createDonation(DonationForm form, User user) {
    Donation donation = Donation.builder()
        .description(form.getDescription())
        .pickupOption(form.getPickupOption())
        .createdAt(LocalDateTime.now())
        .user(user)
        .build();
    Donation saved = donationRepository.save(donation);
    user.setPoints(user.getPoints() + POINTS_PER_DONATION);
    userService.save(user);
    return saved;
  }

  public List<Donation> findByUser(User user) {
    return donationRepository.findAllByUserOrderByCreatedAtDesc(user);
  }

  public List<Donation> findAll() {
    return donationRepository.findAll();
  }
}
