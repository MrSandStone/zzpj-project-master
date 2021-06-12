package app.database.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;


@Document
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Reservation {

    @Id
    private String id;
    private String guestId;
    private String roomId;
    private LocalDate startDate;
    private LocalDate endDate;
    private String totalPrice;
    private boolean isPayed;
}
