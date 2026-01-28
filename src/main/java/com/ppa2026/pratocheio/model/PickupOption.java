package com.ppa2026.pratocheio.model;

public enum PickupOption {
  DROP_OFF("Levar até a sede"),
  MEET_POINT("Marcar ponto de encontro"),
  DELIVERY("Solicitar motoboy");

  private final String label;

  PickupOption(String label) {
    this.label = label;
  }

  public String getLabel() {
    return label;
  }
}
