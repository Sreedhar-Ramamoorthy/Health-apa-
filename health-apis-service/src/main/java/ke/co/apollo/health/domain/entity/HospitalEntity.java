package ke.co.apollo.health.domain.entity;

import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "tbl_hospitals")
public class HospitalEntity extends AbstractEntity {

  @Id
  @Column(name = "id")
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @Column(name = "name", length = 200)
  @NotNull
  private String name;

  @Column(name = "address", length = 200)
  @NotNull
  private String address;

  @Column(name = "contact", length = 100)
  @NotNull
  private String contact;

  @Column(name = "email", length = 100)
  @NotNull
  private String email;

  @Column(name = "locations_id", length = 11)
  @NotNull
  private Integer locationId;

  @Column(name = "payments_id", length = 11)
  @NotNull
  private Integer paymentId;

  @Column(name = "working_hours", length = 200)
  @NotNull
  private String workingHours;

  @ManyToOne(cascade = CascadeType.MERGE , targetEntity = LocationEntity.class, fetch = FetchType.EAGER)
  @JoinColumn(name = "locations_id", referencedColumnName = "id", insertable = false, updatable = false)
  private LocationEntity location;

  @ManyToMany(cascade = CascadeType.MERGE , fetch = FetchType.EAGER)
  @JoinTable(
      name = "tbl_hospital_service_rel",
      joinColumns = {@JoinColumn(name = "hospital_id", referencedColumnName = "id")},
      inverseJoinColumns = {@JoinColumn(name = "service_id", referencedColumnName = "id")})
  private Set<ServiceEntity> services;

  @OneToMany(cascade = CascadeType.MERGE , mappedBy = "hospital", fetch = FetchType.EAGER)
  private Set<PaymentEntity> coPayments;
}
