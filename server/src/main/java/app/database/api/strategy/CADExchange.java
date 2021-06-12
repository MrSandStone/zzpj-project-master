package app.database.api.strategy;

import app.database.entities.RateModel;

public class CADExchange implements Exchange{

    @Override
    public float rateMoney(float cash, RateModel rateModel) {
        return cash / rateModel.getPLN() * rateModel.getCAD();
    }

    @Override
    public String getStrategyName() {
        return StrategyName.CAD.toString();
    }
}
