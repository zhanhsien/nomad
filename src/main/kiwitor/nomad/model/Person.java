package main.kiwitor.nomad.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
public class Person {
    private String name;

    @Getter(AccessLevel.NONE)
    private double salary;
    private double geoPay;

    public double getSalary() {
        return salary + (salary * geoPay);
    }
}
