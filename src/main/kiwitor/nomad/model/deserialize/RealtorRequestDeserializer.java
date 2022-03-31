package main.kiwitor.nomad.model.deserialize;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import main.kiwitor.nomad.model.request.RealtorRequest;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class RealtorRequestDeserializer extends JsonDeserializer<RealtorRequest> {
    @Override
    public RealtorRequest deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        JsonNode rootNode = p.getCodec().readTree(p);

        JsonNode variablesNode = rootNode.get("variables");
        JsonNode queryNode = variablesNode.get("query");

        String query = rootNode.get("query").textValue();

        int ageMax = queryNode.get("year_built").get("max").intValue();

        JsonNode sqftNode = queryNode.get("sqft");
        int sqftMin = sqftNode.get("min").intValue();
        int sqftMax = sqftNode.get("max").intValue();

        List<String> type = StreamSupport.stream(queryNode.get("type").spliterator(), false)
                .map(JsonNode::textValue).collect(Collectors.toList());
        List<String> status = StreamSupport.stream(queryNode.get("status").spliterator(), false)
                .map(JsonNode::textValue).collect(Collectors.toList());
        String location = queryNode.get("search_location").get("location").textValue();
        int limit = variablesNode.get("limit").intValue();
        int offset = variablesNode.get("offset").intValue();

        return new RealtorRequest(query, ageMax, sqftMin, sqftMax, type, status, offset, limit, location, null);
    }
}
