package app.database.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Objects;

@Document
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Guest {

    @Id
    private String IDcard;
    private String name;
    private String surname;
    private long phoneNumber;
    private int discount;


}
