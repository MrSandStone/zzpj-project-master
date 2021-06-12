package app.database.exceptions.validations;

import app.database.exceptions.HotelException;
import app.database.exceptions.HotelExceptionBuilder;

public interface Validator<T> {

    HotelExceptionBuilder hotelExceptionBuilder = new HotelExceptionBuilder();

    void validateInsert(T param) throws HotelException;

    void validateUpdate(T param) throws HotelException;
}
