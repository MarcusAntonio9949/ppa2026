package com.ppa2026.pratocheio.repository;

import com.ppa2026.pratocheio.model.Donation;
import com.ppa2026.pratocheio.model.User;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DonationRepository extends JpaRepository<Donation, Long> {
  List<Donation> findAllByUserOrderByCreatedAtDesc(User user);
}
