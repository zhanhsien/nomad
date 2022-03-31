package main.kiwitor.nomad.model.deserialize;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import main.kiwitor.nomad.model.House;

import java.io.IOException;

public class HouseDeserializer extends JsonDeserializer<House> {
    @Override
    public House deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        JsonNode rootNode = p.getCodec().readTree(p);
        House house = new House();
        if(rootNode.get("description").has("sold_date")) {
            house.setPrice(rootNode.get("description").get("sold_price").intValue());
        } else {
            house.setPrice(rootNode.get("list_price").intValue());
        }
        house.setBedrooms(rootNode.get("description").get("beds").intValue());
        house.setFullBaths(rootNode.get("description").get("baths_full").intValue());
        house.setHalfBaths(rootNode.get("description").get("baths_half").intValue());
        house.setSquareFeet(rootNode.get("description").get("sqft").intValue());
        house.setAddress(rootNode.get("location").get("address").get("line").textValue());
        house.setZipCode(rootNode.get("location").get("address").get("postal_code").textValue());

        JsonNode coordinates = rootNode.get("location").get("address").get("coordinate");
        house.setCoordinates(coordinates.get("lon").doubleValue(), coordinates.get("lat").doubleValue());

        return house;
    }
}
