package com.ppa2026.pratocheio.dto;

import com.ppa2026.pratocheio.model.PickupOption;
import lombok.Data;

@Data
public class DonationForm {
  private String description;
  private PickupOption pickupOption;
  private Long userId;
}
