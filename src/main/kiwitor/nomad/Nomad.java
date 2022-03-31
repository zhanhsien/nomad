package main.kiwitor.nomad;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import main.kiwitor.nomad.constants.States;
import main.kiwitor.nomad.model.*;
import main.kiwitor.nomad.rest.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.*;
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
                SmartAssetApi.getPropertyTax(county);
                county.getCities().parallelStream().forEach(city -> {
                    RealtorApi.getPropertyValues(city);
                    SalaryApi.getCostOfLivingDelta(origin, city);
                });
            });
        });
    }

    private void postProcess() {
        Path path = Paths.get("report.txt");
        Person bob = new Person("Bob", 75000, 0.0);
        double monthlyExpenses = 1500.00;

        states.forEach(state -> {
            state.getAllCities().forEach(city -> {
                bob.setGeoPay(city.getGeoPay());
                double singleBob = state.getIncomeTax(bob);

                int houses = city.getHouses().size();
                int avgHousePrice = city.getAverageHousePrice();
                double propertyTaxRate = city.getCounty().getPropertyTax();
                double propertyTax = avgHousePrice * propertyTaxRate;

                double monthlyExpensesCity = monthlyExpenses + (monthlyExpenses * city.getCostOfLiving());
                double salesTax = monthlyExpensesCity * city.getSalesTax();
                double annualExpenses = monthlyExpensesCity * 12;

                double total = singleBob + propertyTax + annualExpenses;

                String entry = state.getName() + "\t" + city.getName() + "\t" + singleBob + "\t" +
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
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Where do you currently live (City, County, State Code)?");
        String location = scanner.nextLine();

        System.out.println("Any particular states in mind (Comma delimited State Codes)?");
        List<State> states = Arrays.stream(scanner.nextLine().split(","))
                .map(state -> new State(States.getByCode(state.trim()))).collect(Collectors.toList());

        System.out.println("Do you want to search on all cities? (Y/N)?");
        boolean all = scanner.nextLine().equalsIgnoreCase("Y");

        String[] locationTuple = location.split(",");

        City origin = new City(locationTuple[0].trim());
        County originCounty = new County(locationTuple[1].trim());
        originCounty.addCity(origin);
        State originState = new State(States.getByCode(locationTuple[2].trim()));
        originState.addCounty(originCounty);

        Nomad nomad = new Nomad(states);
        try {
            long before = System.currentTimeMillis();
            nomad.process(origin, all);
            nomad.postProcess();
            long after = System.currentTimeMillis();
            System.out.println((after-before)/1000);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}