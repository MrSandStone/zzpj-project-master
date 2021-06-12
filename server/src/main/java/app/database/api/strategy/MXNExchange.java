package app.database.api.strategy;

import app.database.entities.RateModel;

public class MXNExchange implements Exchange{

    @Override
    public float rateMoney(float cash, RateModel rateModel) {
        return cash / rateModel.getPLN() * rateModel.getMXN();
    }

    @Override
    public String getStrategyName() {
        return StrategyName.MXN.toString();
    }
}
