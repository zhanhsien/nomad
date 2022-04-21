package main.kiwitor.nomad.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class Zone {
    private String id;
    private List<String> names;
}
