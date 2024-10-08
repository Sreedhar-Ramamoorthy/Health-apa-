package ke.co.apollo.health.common.domain.model;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductPremium {

  private Health health;
  private List<Life> life;

  @Builder
  @Data
  @AllArgsConstructor
  @NoArgsConstructor
  public static class Health {

    private JamiiPlus jamiiPlus;
    private AfyaNafuu afyaNafuu;
    private List<Femina> femina;
    private JamiiPlusShared jamiiPlusShared;
    private JamiiPlusChildOnly jamiiPlusChildOnly;

    @Builder
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class JamiiPlusChildOnly {
      private List<Inpatient> inpatient;
      private List<Outpatient> outpatient;

      @Builder
      @Data
      @AllArgsConstructor
      @NoArgsConstructor
      public static class Inpatient {
        private int benefit;
        private int premium;
      }

      @Builder
      @Data
      @AllArgsConstructor
      @NoArgsConstructor
      public static class Outpatient {
        private int benefit;
        private int premium;
      }
    }

    @Builder
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class JamiiPlus {

      private List<Inpatient> inpatient;
      private List<Outpatient> outpatient;
      private List<Permium> maternity;
      private List<Permium> dental;
      private List<Permium> optical;
      private Permium travel;

      @Builder
      @Data
      @AllArgsConstructor
      @NoArgsConstructor
      public static class Inpatient {

        private int benefit;
        private int child;
        private List<Integer> age;
        private List<Integer> principal;
        private List<Integer> spouse;
      }

      @Builder
      @Data
      @AllArgsConstructor
      @NoArgsConstructor
      public static class Outpatient {

        private int benefit;
        private List<Integer> age;
        private List<Integer> premium;
      }

      @Builder
      @Data
      @AllArgsConstructor
      @NoArgsConstructor
      public static class Permium {

        private int benefit;
        private int premium;
      }

    }

    @Builder
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class JamiiPlusShared {

      private List<Inpatient> inpatient;
      private List<Outpatient> outpatient;
      private List<Permium> maternity;
      private List<Permium> dental;
      private List<Permium> optical;
      private Permium travel;

      @Builder
      @Data
      @AllArgsConstructor
      @NoArgsConstructor
      public static class Inpatient {

        private int benefit;
        private int child;
        private List<Integer> age;
        private List<Integer> principal;
        private List<Integer> spouse;
      }

      @Builder
      @Data
      @AllArgsConstructor
      @NoArgsConstructor
      public static class Outpatient {

        private int benefit;
        private List<Integer> age;
        private List<Integer> premium;
      }

      @Builder
      @Data
      @AllArgsConstructor
      @NoArgsConstructor
      public static class Permium {

        private int benefit;
        private int premium;
      }

    }

    @Builder
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class AfyaNafuu {

      private List<AgePermium> inpatient;
      private List<AgePermium> outpatient;
      private List<Permium> maternity;

      @Builder
      @Data
      @AllArgsConstructor
      @NoArgsConstructor
      public static class AgePermium {

        private int benefit;
        private List<Integer> age;
        private List<Integer> premium;
      }

      @Builder
      @Data
      @AllArgsConstructor
      @NoArgsConstructor
      public static class Permium {

        private int benefit;
        private int premium;
      }
    }

    @Builder
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Femina {

      private int benefit;
      private List<Integer> age;
      private List<Integer> premium;
    }
  }

  @Builder
  @Data
  @AllArgsConstructor
  @NoArgsConstructor
  public static class Life {

    private String duration;
    private int premium;
  }
}
