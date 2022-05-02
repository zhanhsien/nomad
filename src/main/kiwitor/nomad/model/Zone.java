package main.kiwitor.nomad.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.collections4.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
public class Zone {
    private String id;
    private List<String> names;
    private String type; //TODO: will probably become an enum

    public String getName() {
        return CollectionUtils.isNotEmpty(names) ? names.get(0) : null;
    }

    private Set<String> getNames() {
        Set<String> permutations = new HashSet<>();
        permutations.addAll(names);
        permutations.addAll(names.stream().map(name -> name.replaceAll(" ", "")).collect(Collectors.toList()));
        permutations.addAll(names.stream().map(name -> String.join(" ", name, type)).collect(Collectors.toList()));
        permutations.addAll(permutations.stream().filter(p -> p.contains("Saint")).map(p -> p.replaceAll("Saint", "St.")).collect(Collectors.toList()));
        permutations.addAll(permutations.stream().filter(p -> p.contains("St.")).map(p -> p.replaceAll("St.", "Saint")).collect(Collectors.toList()));
        return permutations;
    }

    public Object get(Map<String, ?> map) {
        return getNames().stream().filter(name -> Objects.nonNull(map.get(name))).findFirst().orElse(null);
    }

    public Object getOrDefault(Map<String, ?> map, Object defaultValue) {
        Object o;
        return Objects.nonNull(o = get(map)) ? o : defaultValue;
    }
}
