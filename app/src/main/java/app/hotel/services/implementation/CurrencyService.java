package app.hotel.services.implementation;

import app.database.api.StrategyContext;
import app.database.entities.CurrencyRestModel;
import app.database.entities.RateModel;
import app.database.repositories.CurrencyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.lang.reflect.Field;
import java.util.ArrayList;


@Service
public class CurrencyService {

    private static final String API_KEY = "5b34a02bed9a1306cfc730dcabc881ad";
    private final RestTemplate restTemplate;
    private final StrategyContext strategyContext;
    private final CurrencyRepository currencyRepository;

    @Autowired
    public CurrencyService(RestTemplate restTemplate, StrategyContext strategyContext, CurrencyRepository currencyRepository) {
        this.restTemplate = restTemplate;
        this.strategyContext = strategyContext;
        this.currencyRepository = currencyRepository;
        getCurrency();
    }

    public void getCurrency() {
        final String uri = "http://data.fixer.io/api/latest?access_key=" + API_KEY + "&symbols=USD,AUD,CAD,PLN,MXN&format=1";
            ResponseEntity<CurrencyRestModel> currencyRestModel = restTemplate.getForEntity(uri, CurrencyRestModel.class);
            if (currencyRestModel.getStatusCodeValue() == 200 )
                currencyRepository.save(currencyRestModel.getBody().getRates());

    }

    public ArrayList<String> getPossibleRates() {

       Field[] fields = RateModel.class.getDeclaredFields();
       ArrayList<String> arrayList = new ArrayList<>();
        for(Field f : fields) {
            arrayList.add(f.getName());
        }
        arrayList.add("EUR");
        arrayList.remove("id");
        return arrayList;
    }

    public StrategyContext getStrategyContext() {
        return strategyContext;
    }

    public RateModel getRateModel() {
        return currencyRepository.findTopByOrderByIdDesc();
    }

}

