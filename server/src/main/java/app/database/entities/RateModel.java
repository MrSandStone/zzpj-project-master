package app.database.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;

@Document
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RateModel implements Serializable {

    @Id
    private String id;
    @JsonProperty("PLN")
    private float PLN;
    @JsonProperty("USD")
    private float USD;
    @JsonProperty("AUD")
    private float AUD;
    @JsonProperty("CAD")
    private float CAD;
    @JsonProperty("MXN")
    private float MXN;

}
