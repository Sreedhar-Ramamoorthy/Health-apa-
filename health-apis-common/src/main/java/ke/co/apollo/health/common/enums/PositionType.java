package ke.co.apollo.health.common.enums;

import lombok.Getter;

@Getter
public enum PositionType {
  LEFT(1),
  MIDDLE(2),
  RIGHT(3);

  private final int place;

  PositionType(int place) {
    this.place = place;
  }
}
