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

public class CountyDeserializer extends JsonDeserializer<County> {
    @Override
    public County deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        JsonNode rootNode = p.getCodec().readTree(p);

        if (rootNode.isNull() || rootNode.isEmpty()) {
            throw new RuntimeException("Invalid JSON");
        }

        JsonNode namesNode = rootNode.get("names");
        if(!namesNode.isArray()) {
            throw new RuntimeException("Invalid names");
        }

        ArrayNode namesArray = (ArrayNode) namesNode;
        List<String> names = StreamSupport.stream(namesArray.spliterator(), false)
                .map(JsonNode::textValue).collect(Collectors.toList());

        County county = new County();
        county.setId(rootNode.get("id").textValue());
        county.setNames(names);

        String citiesJson = rootNode.get("cities").toString();
        ObjectMapper mapper = new ObjectMapper();
        JavaType cityCollectionType = mapper.getTypeFactory().constructCollectionType(List.class, City.class);
        List<City> cities = mapper.readValue(citiesJson, cityCollectionType);

        county.setCities(cities);
        return county;
    }
}