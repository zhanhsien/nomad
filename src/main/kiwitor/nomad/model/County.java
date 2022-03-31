package main.kiwitor.nomad.model;

import lombok.Getter;
import lombok.Setter;

import java.util.LinkedList;
import java.util.List;

@Getter
@Setter
public class County {
    private State state;
    private String name;
    private double salesTax;
    private double propertyTax;
    private List<City> cities = new LinkedList<>();

    public County(String name) {
        this.name = name;
    }

    public void addCity(City city) {
        city.setCounty(this);
        cities.add(city);
    }

    public boolean hasCity(String name) {
        return cities.stream().anyMatch(city -> city.getName().equals(name));
    }
}
