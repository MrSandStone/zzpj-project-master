package app.database.api.strategy;

import app.database.entities.RateModel;

public class USDExchange implements Exchange{

    @Override
    public float rateMoney(float cash, RateModel rateModel) {
        return cash / rateModel.getPLN() * rateModel.getUSD();
    }

    @Override
    public String getStrategyName() {
        return StrategyName.USD.toString();
    }
}
