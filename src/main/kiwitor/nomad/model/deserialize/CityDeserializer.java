package main.kiwitor.nomad.model.deserialize;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.node.ArrayNode;
import main.kiwitor.nomad.model.City;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class CityDeserializer extends JsonDeserializer<City> {
    @Override
    public City deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
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

        City city = new City();
        city.setId(rootNode.get("id").textValue());
        city.setNames(names);
        return city;
    }
}