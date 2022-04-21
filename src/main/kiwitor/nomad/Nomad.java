package main.kiwitor.nomad;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import main.kiwitor.nomad.constants.States;
import main.kiwitor.nomad.model.*;
import main.kiwitor.nomad.rest.*;
import main.kiwitor.nomad.util.FemaHazardIndex;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Nomad {
    private List<State> states;

    private Nomad() {
        states = Arrays.stream(States.values())
                .map(State::new).collect(Collectors.toList());
    }

    private Nomad(List<State> states) {
        this.states = states;
    }

    private void process(City origin, boolean all) {
        states.forEach(TaxeeApi::setStateTax);
        states.forEach(SalesTaxApi::getStateSalesTax);
        states.forEach(state -> {
            List<City> cities = SalesTaxApi.getCities(state, all);
            cities.parallelStream().forEach(city -> SalesTaxApi.getCityData(state, city));
            state.getCounties().parallelStream().forEach(county -> {
                FemaHazardIndex.getHazardIndex(county);
                SmartAssetApi.getPropertyTax(county);
                county.getCities().parallelStream().forEach(city -> {
                    RealtorApi.getPropertyValues(city);
                    SalaryApi.getCostOfLivingDelta(origin, city);
                    CityRatingApi.getCrimeRates(city);
                });
            });
        });
    }

    private void postProcess() {
        Path path = Paths.get("report.txt");
        Person gary = new Person("Gary", 187380, 0.0);
        Person sarah = new Person("Sarah", 68000, 0.0);
        double monthlyExpenses = 2500.00;

        try {
            String[] headers = {"STATE", "CITY", "MURDER/MANSLAUGHTER", "AUTO_THEFT", "RAPE", "AGGRAVATED_ASSAULT", "ROBBERY", "LARCENY_THEFT", "VIOLENT_CRIME", "TOTAL_INCIDENTS", "ARSON", "BURGLARY", "PROPERTY_CRIME", "HAZARD_INDEX", "HAZARD_RATING", "POPULATION", "GARY", "SARAH", "JOINT", "HOUSE_DATA", "HOUSES", "AVG_PRICE", "PROP_TAX_RATE", "PROP_TAX", "MONTH_EXPENSE", "SALES_TAX", "SALES_TAX_RATE", "ANNUAL_EXPENSE", "GEO_PAY", "TOTAL"};
            Files.write(path, StringUtils.join(headers, "\t").concat("\n").getBytes(StandardCharsets.UTF_8),
                    StandardOpenOption.CREATE, StandardOpenOption.APPEND);

            states.forEach(state -> {
                state.getAllCities().forEach(city -> {
                    String crimeRates = StringUtils.join(city.getCrimeRates().values(), "\t");

                    gary.setGeoPay(city.getGeoPay());
                    sarah.setGeoPay(city.getGeoPay());
                    double joint = state.getIncomeTax(gary, sarah);
                    double singleGary = state.getIncomeTax(gary);
                    double singleSarah = state.getIncomeTax(sarah);

                    int houses = city.getHouses().size();
                    int avgHousePrice = city.getAverageHousePrice();
                    double propertyTaxRate = city.getCounty().getPropertyTax();
                    double propertyTax = avgHousePrice * propertyTaxRate;

                    double monthlyExpensesCity = monthlyExpenses + (monthlyExpenses * city.getCostOfLiving());
                    double salesTax = monthlyExpensesCity * city.getSalesTax();
                    double annualExpenses = monthlyExpensesCity * 12;

                    double total = singleGary + singleSarah + propertyTax + annualExpenses;

                    String entry = state.getName() + "\t" + city.getName() + "\t" + crimeRates + "\t" + city.getCounty().getHazardIndex() + "\t" + singleGary + "\t" + singleSarah + "\t" + joint + "\t" +
                            city.isHasHouseData() + "\t" + houses + "\t" + avgHousePrice + "\t" + propertyTaxRate + "\t" + propertyTax + "\t" +
                            monthlyExpensesCity + "\t" + salesTax + "\t" + city.getSalesTax() + "\t" + annualExpenses + "\t" +
                            city.isHasGeoPay() + "\t" + total + "\n";

                    try {
                        Files.write(path, entry.getBytes(StandardCharsets.UTF_8),
                                StandardOpenOption.CREATE, StandardOpenOption.APPEND);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

//    private List<State> states;
    private void loadData() {
        final String fileName = "data/states.json";
        ClassLoader classloader = Nomad.class.getClassLoader();


        try (InputStream is = classloader.getResourceAsStream(fileName)) {
            String content = IOUtils.toString(is, StandardCharsets.UTF_8);
            JSONArray statesJson = new JSONArray(content);

            ObjectMapper mapper = new ObjectMapper();
            JavaType stateCollectionType = mapper.getTypeFactory().constructCollectionType(List.class, State.class);
            states = mapper.readValue(statesJson.toString(), stateCollectionType);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //loadData();
    //
    //        Map<String, StateBean> states = new HashMap<>();
    //
    //        cities.forEach(city -> {
    //            String stateId = city.getStateId();
    //            StateBean state = states.get(stateId);
    //            if(Objects.isNull(state)) {
    //                String stateCode = city.getState();
    //                state = new StateBean();
    //                state.setId(stateId);
    //                state.setName(States.getByCode(stateCode).getName());
    //                state.setCode(stateCode);
    //            }
    //
    //            String[] cityNames = city.getName().split("/");
    //            city.setNames(Arrays.asList(cityNames));
    //
    //            Map<String, CountyBean> counties = state.getCounties();
    //            String[] countyNames = city.getCounty().split("/");
    //            String[] countyIds = city.getCountyId().split("/");
    //
    //            for(int i = 0; i < countyIds.length; i++) {
    //                CountyBean county = counties.get(countyIds[i]);
    //                if(Objects.isNull(county)) {
    //                    county = new CountyBean();
    //                    county.setId(countyIds[i]);
    //                    county.setNames(Collections.singletonList(countyNames[i]));
    //                }
    //
    //                county.addCity(city);
    //                state.addCounty(county);
    //            }
    //
    //            states.put(state.getId(), state);
    //        });
    //
    //        JSONArray statesJson = new JSONArray();
    //        for(StateBean state : states.values()) {
    //            JSONObject stateJson = new JSONObject();
    //            stateJson.put("id", state.getId());
    //            stateJson.put("code", state.getCode());
    //            stateJson.put("name", state.getName());
    //            JSONArray countiesJson = new JSONArray();
    //            for(CountyBean county : state.getCounties().values()) {
    //                JSONObject countyJson = new JSONObject();
    //                countyJson.put("id", county.getId());
    //                countyJson.put("names", county.getNames());
    //                JSONArray citiesJson = new JSONArray();
    //                for(CityBean city : county.getCities().values()) {
    //                    JSONObject cityJson = new JSONObject();
    //                    cityJson.put("id", city.getId());
    //                    cityJson.put("names", city.getNames());
    //                    citiesJson.put(cityJson);
    //                }
    //
    //                countyJson.put("cities", citiesJson);
    //                countiesJson.put(countyJson);
    //            }
    //
    //            stateJson.put("counties", countiesJson);
    //            statesJson.put(stateJson);
    //        }
    //
    //        Path path = Paths.get("states.json");
    //        try {
    //            Files.write(path, statesJson.toString().getBytes(StandardCharsets.UTF_8),
    //                    StandardOpenOption.CREATE, StandardOpenOption.APPEND);
    //        } catch (IOException e) {
    //            e.printStackTrace();
    //        }

    private void test() {
        AtomicInteger i = new AtomicInteger();
        loadData();
        states.forEach(state -> {
//            System.out.println(state.getName());
            state.getCounties().forEach(county -> {
//                System.out.println("\t" + county.getNames());
                county.getCities().forEach(city -> {
//                    System.out.println("\t\t" + city.getNames());
                    i.set(i.get() + 1);
                });
            });
        });

        System.out.println(i);
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Where do you currently live (City, County, State Code)?");
//        String location  = scanner.nextLine();
        String location = "Plano,Collin County,TX";

        System.out.println("Any particular states in mind (Comma delimited State Codes)?");
//        List<State> states = Arrays.stream(scanner.nextLine().split(","))
//                .map(state -> new State(States.getByCode(state.trim()))).collect(Collectors.toList());
//        List<State> states = Arrays.stream("CO,FL,WA,OR,MI,TX".split(","))
//                .map(state -> new State(States.getByCode(state.trim()))).collect(Collectors.toList());
        List<State> states = Arrays.stream(States.values()).map(State::new).collect(Collectors.toList());

        System.out.println("Do you want to search on all cities? (Y/N)?");
//        boolean all = scanner.nextLine().equalsIgnoreCase("Y");
        boolean all = true;

        String[] locationTuple = location.split(",");

        City origin = new City(locationTuple[0].trim());
        County originCounty = new County(locationTuple[1].trim());
        originCounty.addCity(origin);
        State originState = new State(States.getByCode(locationTuple[2].trim()));
        originState.addCounty(originCounty);

        Nomad nomad = new Nomad(states);
        try {
            long before = System.currentTimeMillis();
            nomad.test();
//            nomad.process(origin, all);
//            nomad.postProcess();
            long after = System.currentTimeMillis();
            System.out.println((after-before)/1000);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}