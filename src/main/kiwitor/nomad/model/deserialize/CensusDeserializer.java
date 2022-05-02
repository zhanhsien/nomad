package main.kiwitor.nomad.model.deserialize;

import com.google.gson.*;
import main.kiwitor.nomad.model.CensusEntry;

import java.lang.reflect.Type;

public class CensusDeserializer implements JsonDeserializer<CensusEntry> {
    @Override
    public CensusEntry deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonArray jsonArray = json.getAsJsonArray();

        CensusEntry entry = new CensusEntry();
        try {
            entry.setName(jsonArray.get(0).getAsString());
            entry.setPopulation(jsonArray.get(1).getAsInt());
            entry.setStateId(jsonArray.get(2).getAsString());
            entry.setPlaceId(jsonArray.get(3).getAsString());
        } catch (Exception e) {
            entry.setStateId("");
            entry.setPlaceId("");
        }

        return entry;
    }
}