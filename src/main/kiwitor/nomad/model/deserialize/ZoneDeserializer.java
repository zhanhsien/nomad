package main.kiwitor.nomad.model.deserialize;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import main.kiwitor.nomad.model.Zone;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class ZoneDeserializer<T extends Zone> extends JsonDeserializer<T> {
    private T getInstance() {
        ParameterizedType clazz = (ParameterizedType) getClass().getGenericSuperclass();
        Class<T> type = (Class<T>) clazz.getActualTypeArguments()[0];
        try {
            return type.newInstance();
        } catch (Exception e) {
            // Oops, no default constructor
            throw new RuntimeException(e);
        }
    }

    @Override
    public T deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
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

        T zone = getInstance();
        zone.setId(rootNode.get("id").textValue());
        zone.setNames(names);
        return zone;
    }
}
