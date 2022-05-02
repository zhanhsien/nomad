package main.kiwitor.nomad.model.deserialize;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.node.ArrayNode;
import main.kiwitor.nomad.model.City;
import main.kiwitor.nomad.model.County;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class CountyDeserializer extends ZoneDeserializer<County> {
    @Override
    public County deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        County county = super.deserialize(p, ctxt);
        JsonNode rootNode = p.readValueAsTree();
//
//        String citiesJson = rootNode.get("cities").toString();
//        ObjectMapper mapper = new ObjectMapper();
//        JavaType cityCollectionType = mapper.getTypeFactory().constructCollectionType(List.class, City.class);
//        List<City> cities = mapper.readValue(citiesJson, cityCollectionType);

//        county.setCities(cities);
        return county;
    }
}