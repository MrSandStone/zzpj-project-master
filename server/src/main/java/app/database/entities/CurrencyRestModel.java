package app.database.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.sql.Timestamp;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class CurrencyRestModel implements Serializable {

    private boolean success;
    private Timestamp timestamp;
    private String base;
    private String date;

    @JsonProperty("rates")
    private RateModel rates;

}

