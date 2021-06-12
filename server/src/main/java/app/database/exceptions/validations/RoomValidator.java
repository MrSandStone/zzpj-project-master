package app.database.exceptions.validations;

import app.database.exceptions.HotelException;
import app.database.exceptions.messages.RoomErrorMessages;

import java.util.HashMap;

public class RoomValidator implements Validator<HashMap<String, String>> {

    @Override
    public void validateInsert(HashMap<String, String> param) {

        try {
            int price = Integer.parseInt(param.get("price"));
            if (price < 0)
                hotelExceptionBuilder.addError(RoomErrorMessages.WRONG_PRICE_RANGE);
        } catch (NumberFormatException nfe) {
            hotelExceptionBuilder.addError(RoomErrorMessages.WRONG_PRICE);
        }


        try {
            Float.parseFloat(param.get("capacity"));
        } catch (NumberFormatException nfe) {
            hotelExceptionBuilder.addError(RoomErrorMessages.WRONG_CAPACITY);
        }

        if (hotelExceptionBuilder.isError())
            throw hotelExceptionBuilder.build();
    }

    @Override
    public void validateUpdate(HashMap<String, String> param) throws HotelException {
        validateInsert(param);
    }
}
