package main.kiwitor.nomad.model;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
public class City extends Zone {
    private String name;
    private String censusId;
    private County county;
    private int population;

    @Getter(AccessLevel.NONE)
    private double salesTax;
    private double costOfLiving = 0.0;
    private double geoPay = 0.0;
    private List<Double> housePrices = new LinkedList<>();
    private List<House> houses = new LinkedList<>();
    private Map<String, Integer> crimeRates = new HashMap<>();

    boolean hasGeoPay = false;
    boolean hasHouseData = false;

    private final String PROPERTIES = "properties";
    private final String PRICE = "price";
    private final String LIST_DATE = "list_date";

    public City(String name) {
        this.name = name;
    }

    public City(String name, double salesTax) {
        this.name = name;
        this.salesTax = salesTax;
    }

    public State getState() {
        return county.getState();
    }

    public int getAverageHousePrice() {
        List<House> results = houses.stream().filter(house -> house.getPrice() > 0).collect(Collectors.toList());

        if(results.size() == 0) {
            return 600000;
        }

        int sum = 0;
        for(House house : results) {
            sum += house.getPrice();
        }

        hasHouseData = true;
        return sum / houses.size();
    }

    public double getSalesTax() {
        return salesTax + county.getSalesTax() + getState().getSalesTax();
    }
}
