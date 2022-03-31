package main.kiwitor.nomad.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Coordinates {
    private final double longitude;
    private final double latitude;
}