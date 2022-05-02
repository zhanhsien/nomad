package main.kiwitor.nomad.model.deserialize;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.*;
import main.kiwitor.nomad.model.County;
import main.kiwitor.nomad.model.State;

import java.io.IOException;
import java.util.List;

public class StateDeserializer extends ZoneDeserializer<State> {
    @Override
    public State deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        JsonNode rootNode = p.getCodec().readTree(p);
        State state = super.deserialize(p, ctxt);

        String countiesJson = rootNode.get("counties").toString();
        ObjectMapper mapper = new ObjectMapper();
        JavaType countyCollectionType = mapper.getTypeFactory().constructCollectionType(List.class, County.class);
        List<County> counties = mapper.readValue(countiesJson, countyCollectionType);
        counties.forEach(county -> county.setState(state));

        state.setCounties(counties);
        return state;
    }
}
