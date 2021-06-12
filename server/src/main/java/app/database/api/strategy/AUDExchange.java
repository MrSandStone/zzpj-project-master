package app.database.api.strategy;

import app.database.entities.RateModel;

public class AUDExchange implements Exchange{

    @Override
    public float rateMoney(float cash, RateModel rateModel) {
        return cash / rateModel.getPLN() * rateModel.getAUD();
    }

    @Override
    public String getStrategyName() {
        return StrategyName.AUD.toString();
    }
}
