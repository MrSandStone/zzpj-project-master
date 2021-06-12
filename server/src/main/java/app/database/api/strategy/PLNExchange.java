package app.database.api.strategy;

import app.database.entities.RateModel;

public class PLNExchange implements Exchange {

    @Override
    public float rateMoney(float cash, RateModel rateModel) {
        return cash;
    }

    @Override
    public String getStrategyName() {
        return StrategyName.PLN.toString();
    }
}
