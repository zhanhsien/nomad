package main.kiwitor.nomad.model.serialize;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import main.kiwitor.nomad.model.request.RealtorRequest;

import java.io.IOException;

public class RealtorRequestSerializer extends JsonSerializer<RealtorRequest> {
    @Override
    public void serialize(RealtorRequest value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        gen.writeStartObject();
        gen.writeStringField("query", value.getQuery());
        gen.writeObjectFieldStart("variables");
        gen.writeObjectFieldStart("query");
        gen.writeObjectFieldStart("year_built");
        gen.writeNumberField("max", value.getAgeMax());
        gen.writeEndObject();
        gen.writeObjectFieldStart("sqft");
        gen.writeNumberField("min", value.getSqftMin());
        gen.writeNumberField("max", value.getSqftMax());
        gen.writeEndObject();
        gen.writeArrayFieldStart("type");
        for(String type : value.getType()) {
            gen.writeString(type);
        }
        gen.writeEndArray();
        gen.writeArrayFieldStart("status");
        for(String status : value.getStatus()) {
            gen.writeString(status);
        }
        gen.writeEndArray();
        gen.writeObjectFieldStart("sold_date");
        gen.writeStringField("min", value.getSoldDate());
        gen.writeEndObject();
        gen.writeObjectFieldStart("search_location");
        gen.writeStringField("location", value.getLocation());
        gen.writeEndObject();
        gen.writeEndObject();
        gen.writeNumberField("limit", value.getLimit());
        gen.writeNumberField("offset", value.getOffset());
        gen.writeEndObject();
        gen.writeEndObject();
    }
}
