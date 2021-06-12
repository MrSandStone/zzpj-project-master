package app.database.exceptions;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.stream.Collectors;

@Setter
@Getter
public class HotelException extends RuntimeException{
    private List<String> errors;

    public String displayErrors(){
        return errors.stream().
                map(error -> error + ",\n")
                .collect(Collectors.joining(""));
    }
}
