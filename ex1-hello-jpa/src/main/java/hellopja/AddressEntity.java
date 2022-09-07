package hellopja;

import javax.persistence.*;

@Entity
@Table(name = "ADDRESS")
public class AddressEntity {
    @Id
    @GeneratedValue
    private Long id;

    private Address address;


    public AddressEntity(String city, String street, String zipcode) {

        this.address = new Address(city, street, zipcode);
    }
}
