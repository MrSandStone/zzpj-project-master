package app.database.api.strategy;

import app.database.entities.Guest;
import app.database.entities.RateModel;

public interface Exchange {

    float rateMoney(float cash, RateModel rateModel);

    String getStrategyName();

}
