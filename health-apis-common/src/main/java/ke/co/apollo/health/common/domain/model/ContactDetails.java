package ke.co.apollo.health.common.domain.model;


import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ContactDetails {

  List<Address> addresses;
  List<Phone> phones;

}
