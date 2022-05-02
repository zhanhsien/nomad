package main.kiwitor.nomad.model;

import com.google.gson.annotations.JsonAdapter;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import main.kiwitor.nomad.model.deserialize.CensusDeserializer;
import org.apache.commons.lang3.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Getter
@Setter
@NoArgsConstructor
@JsonAdapter(value = CensusDeserializer.class)
public class CensusEntry {
    //<Place name> <Place Type>, <State Name
    private String name;
    private int population;
    private String stateId;
    private String placeId;

    private static final Pattern pattern = Pattern.compile(" [a-z ]+,");

    public String getType() {
        Matcher matcher = pattern.matcher(name);
        String type = matcher.find() ? matcher.group() : "";

        if(StringUtils.isNotEmpty(type)) {
            type = type.substring(0, type.lastIndexOf(',')).trim();
        }

        return type;
    }
}
