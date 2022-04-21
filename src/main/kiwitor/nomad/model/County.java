package main.kiwitor.nomad.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import main.kiwitor.nomad.model.deserialize.CountyDeserializer;

import java.util.LinkedList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@JsonDeserialize(using = CountyDeserializer.class)
public class County extends Zone {
    private State state;
    private String name;
    private double salesTax;
    private double propertyTax;
    private String hazardIndex;
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
