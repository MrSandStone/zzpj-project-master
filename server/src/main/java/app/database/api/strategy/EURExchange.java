package app.database.api.strategy;

import app.database.entities.RateModel;

public class EURExchange implements Exchange{
    @Override
    public float rateMoney(float cash, RateModel rateModel) {
        return cash / rateModel.getPLN();
    }

    @Override
    public String getStrategyName() {
        return StrategyName.EUR.toString();
    }
}
