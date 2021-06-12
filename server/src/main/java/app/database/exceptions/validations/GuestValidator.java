package app.database.exceptions.validations;


import app.database.exceptions.HotelException;
import app.database.exceptions.HotelExceptionBuilder;
import app.database.exceptions.messages.GuestErrorMessages;

import java.util.HashMap;


public class GuestValidator implements Validator<HashMap<String, String>> {

    @Override
    public void validateInsert(HashMap<String, String> param) throws HotelException {

        validateUpdate(param);
    }

    @Override
    public void validateUpdate(HashMap<String, String> param) throws HotelException {

        try {
            Long.parseLong(param.get("phoneNumber"));
        } catch (NumberFormatException nfe) {
            hotelExceptionBuilder.addError(GuestErrorMessages.WRONG_PHONE);
        }

        try {
            long discount = Long.parseLong(param.get("discount"));
            if (discount < 0 || discount > 20)
                hotelExceptionBuilder.addError(GuestErrorMessages.WRONG_DISCOUNT_VALUE);

        } catch (NumberFormatException nfe) {
            hotelExceptionBuilder.addError(GuestErrorMessages.WRONG_DISCOUNT);
        }

        if (hotelExceptionBuilder.isError())
            throw hotelExceptionBuilder.build();

    }
}
