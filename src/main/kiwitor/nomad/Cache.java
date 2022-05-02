package main.kiwitor.nomad;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import main.kiwitor.nomad.model.CensusEntry;
import main.kiwitor.nomad.model.FemaEntry;
import main.kiwitor.nomad.model.Zone;
import main.kiwitor.nomad.model.v2.CityBean;
import main.kiwitor.nomad.model.v2.CountyBean;
import main.kiwitor.nomad.model.v2.StateBean;
import main.kiwitor.nomad.rest.CensusApi;
import main.kiwitor.nomad.rest.TaxeeApi;
import main.kiwitor.nomad.util.FemaHazardIndex;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

//TODO: Eventually will need to incorporate a way to pass Type as a parameter instead of using the same code 3 times
public class Cache {
    private static Cache instance = new Cache();
    private static ClassLoader classloader = Cache.class.getClassLoader();

    private final static String stateFile = "data/persist_states.json";
    private final static String countyFile = "data/persist_counties.json";
    private final static String cityFile = "data/persist_cities.json";

    private static List<CityBean> cities;
    private static ConcurrentMap<String, StateBean> stateIndex;
    private static ConcurrentMap<String, CountyBean> countyIndex;
    private static ConcurrentMap<String, FemaEntry> femaIndex;
    private static ConcurrentMap<String, CensusEntry> censusIndex;

    private Cache() { }

    public static Cache getInstance() {
        return instance;
    }

    public StateBean getState(String id) {
        if(Objects.isNull(stateIndex)) {
            loadStates();
        }

        return stateIndex.get(id);
    }

    public CountyBean getCounty(String id) {
        if(Objects.isNull(countyIndex)) {
            loadCounties();
        }

        return countyIndex.get(id);
    }

    public Map<String, StateBean> getStateIndex() {
        if(Objects.isNull(stateIndex)) {
            loadStates();
        }

        return stateIndex;
    }

    public Map<String, CountyBean> getCountyIndex() {
        if(Objects.isNull(countyIndex)) {
            loadCounties();
        }

        return countyIndex;
    }

    public List<CityBean> getCities() {
        if(Objects.isNull(cities)) {
            loadCities();
        }

        return cities;
    }

    private void loadStates() {
        try (InputStream inState = classloader.getResourceAsStream(stateFile)) {
            String stateContent = IOUtils.toString(inState, StandardCharsets.UTF_8);
            Gson gson = new Gson();
            Type stateCollectionType = new TypeToken<Collection<StateBean>>(){}.getType();
            List<StateBean> stateBeans = gson.fromJson(stateContent, stateCollectionType);
            stateIndex = Objects.requireNonNull(stateBeans).parallelStream().map(TaxeeApi::setStateTax)
//                    .filter(state -> state.getIncomeTax().getTaxType().equals(TaxType.NONE))
                    .collect(Collectors.toConcurrentMap(Zone::getId, e -> e));
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    private void loadCounties() {
        try (InputStream inCounty = classloader.getResourceAsStream(countyFile)) {
            String countyContent = IOUtils.toString(inCounty, StandardCharsets.UTF_8);
            Gson gson = new Gson();
            Type countyCollectionType = new TypeToken<Collection<CountyBean>>() {
            }.getType();
            List<CountyBean> countyBeans = gson.fromJson(countyContent, countyCollectionType);
            countyIndex = Objects.requireNonNull(countyBeans).parallelStream().peek(county -> {
                FemaEntry femaMatch = getFemaEntry(county.getId());
                county.setHazardIndex(femaMatch);
            }).collect(Collectors.toConcurrentMap(Zone::getId, e -> e));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadCities() {
        try (InputStream inCity = classloader.getResourceAsStream(cityFile)) {
            String cityContent = IOUtils.toString(inCity, StandardCharsets.UTF_8);
            Gson gson = new Gson();
            Type cityCollectionType = new TypeToken<Collection<CityBean>>() {
            }.getType();
            cities = gson.fromJson(cityContent, cityCollectionType);
            Objects.requireNonNull(cities).parallelStream().forEach(city -> {
                CensusEntry censusMatch = getCensuEntry(city.getId(), city.getStateId());
                city.setPopulation(censusMatch.getPopulation());
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private FemaEntry getFemaEntry(String id) {
        if(Objects.isNull(femaIndex)) {
            loadFemaIndex();
        }

        return femaIndex.get(id);
    }

    private CensusEntry getCensuEntry(String cityId, String stateId) {
        if(Objects.isNull(censusIndex)) {
            loadCensusIndex();
        }

        return censusIndex.get(cityId.concat(stateId));
    }

    private void loadFemaIndex() {
        List<FemaEntry> fema = FemaHazardIndex.getIndex();
        femaIndex = fema.parallelStream().collect(Collectors.toConcurrentMap(
                FemaEntry::getNriId,
                e -> e
        ));
    }

    private void loadCensusIndex() {
        List<CensusEntry> census = CensusApi.getCities();
        censusIndex = census.parallelStream().collect(Collectors.toConcurrentMap(
                e -> e.getPlaceId().concat(e.getStateId()),
                e -> e
        ));
    }

//    private CityBean getOrigin() {
//            origin = new CityBean();
//            origin.setStateId("48");
//            origin.setCountyId("C48085");
//            origin.setId("58016");
//            origin.setNames(Collections.singletonList("Plano"));
//            origin.setType("48");
//            CensusEntry censusEntry = censusIndex.get(origin.getId().concat(origin.getStateId()));
//            origin.setPopulation(censusEntry.getPopulation());
//            CityRatingApi.getCrimeStats(stateIndex.get(origin.getStateId()), origin);
//    }
}
