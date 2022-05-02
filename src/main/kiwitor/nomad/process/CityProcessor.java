package main.kiwitor.nomad.process;

import main.kiwitor.nomad.Cache;
import main.kiwitor.nomad.model.v2.CityBean;
import main.kiwitor.nomad.model.v2.CountyBean;
import main.kiwitor.nomad.model.v2.StateBean;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CityProcessor {
    private CountDownLatch filterLatch;
    private List<CityBean> filteredCities = Collections.synchronizedList(new LinkedList<>());
    private Map<String, StateBean> stateIndex;
    private Map<String, CountyBean> countyIndex;

    public List<CityBean> getFilteredCities() {
        filterCities();
        return filteredCities;
    }

    private void filterCities() {
        stateIndex = Cache.getInstance().getStateIndex();
        countyIndex = Cache.getInstance().getCountyIndex();
        List<CityBean> cities = Cache.getInstance().getCities();

        filterLatch = new CountDownLatch(cities.size());
        ExecutorService exec = Executors.newCachedThreadPool();
        cities.forEach(city -> exec.submit(new Thread(() -> filter(city))));

        try {
            filterLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            exec.shutdown();
        }
    }

    private void filter(CityBean city) {
        StateBean state = Cache.getInstance().getState(city.getStateId());
//        if(!state.getCode().equalsIgnoreCase("WA") &&
//                !state.getCode().equalsIgnoreCase("CO") &&
//                !state.getCode().equalsIgnoreCase("FL") &&
//                !state.getCode().equalsIgnoreCase("MI") &&
//                !state.getCode().equalsIgnoreCase("NV")) {
//            filterLatch.countDown();
//            return;
//        }

        CountyBean county = Cache.getInstance().getCounty(city.getCountyId());
        String rating = county.getHazardIndex().getRiskRating();
        if(rating.equalsIgnoreCase("Very High") || rating.equalsIgnoreCase("Relatively High")) {
            filterLatch.countDown();
            return;
        }

        if(city.getPopulation() < 25000) {
            filterLatch.countDown();
            return;
        }

        //TODO: Crime rates are tricky because they can be too strict of a filter, need to tweak it more
//        CityRatingApi.getCrimeStats(stateIndex.get(city.getStateId()), city);
//        if (city.sucks()) {
//            latch.countDown();
//            return;
//        }

        filteredCities.add(city);
        filterLatch.countDown();
    }

    //TODO: steps left to run
//    states.forEach(TaxeeApi::setStateTax);
//        states.forEach(SalesTaxApi::getStateSalesTax);
//        states.forEach(state -> {
//            List<City> cities = SalesTaxApi.getCities(state, all);
//            cities.parallelStream().forEach(city -> SalesTaxApi.getCityData(state, city));
//            state.getCounties().parallelStream().forEach(county -> {
//                FemaHazardIndex.getHazardIndex(county);
//                SmartAssetApi.getPropertyTax(county);
//                county.getCities().parallelStream().forEach(city -> {
//                    RealtorApi.getPropertyValues(city);
//                    SalaryApi.getCostOfLivingDelta(origin, city);
//                    CityRatingApi.getCrimeRates(city);
//                });
//            });
//        });
}
