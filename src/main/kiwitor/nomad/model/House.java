package main.kiwitor.nomad.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import main.kiwitor.nomad.model.deserialize.HouseDeserializer;

@Setter
@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonDeserialize(using = HouseDeserializer.class)
public class House {
    private int price;
    private int squareFeet;
    private int bedrooms;
    private int fullBaths;
    private int halfBaths;
    private String address;
    private String zipCode;
    private City city;

    @Setter(AccessLevel.NONE)
    private Coordinates coordinates;

    public void setCoordinates(double longitude, double latitude) {
        coordinates = new Coordinates(longitude, latitude);
    }
}
