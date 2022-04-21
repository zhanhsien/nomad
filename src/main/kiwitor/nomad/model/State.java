package main.kiwitor.nomad.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import main.kiwitor.nomad.constants.States;
import main.kiwitor.nomad.constants.TaxType;
import main.kiwitor.nomad.model.deserialize.StateDeserializer;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@JsonDeserialize(using = StateDeserializer.class)
public class State {
    private String id;
    private String name;
    private String code;
    private String censusId;
    private TaxType taxType;
    private double stdDeductSingle = 0.00;
    private double stdDeductJoint = 0.00;
    private double perExemptSingle = 0.00;
    private double perExemptJoint = 0.00;
    private double salesTax;
    private List<TaxBracket> brackets = new LinkedList<>();
    private List<County> counties = new LinkedList<>();
    private List<City> orphanCities = new LinkedList<>();

    public State(States state) {
        name = state.getName();
        code = state.getCode();
    }

    public double getIncomeTax(Person p) {
        return getIncomeTax(p, null);
    }

    public double getIncomeTax(Person p1, Person p2) {
        boolean joint = Objects.nonNull(p2);
        double income = joint ? p1.getSalary() + p2.getSalary() - stdDeductJoint : p1.getSalary() - stdDeductSingle;
        double tax = 0.0;

        for(int i = 0; i < brackets.size(); i++) {
            TaxBracket b1 = brackets.get(i);
            TaxBracket b2 = i < brackets.size() - 1 ? brackets.get(i+1) : null;
            double rate = b1.getRate();
            double min = joint ? b1.getJoint() : b1.getSingle();
            double max = b2 == null ? -1.0 : joint ? b2.getJoint() : b2.getSingle();

            if((income > min && income <= max) || max == -1.0) {
                tax += (income - min) * rate;
                break;
            } else if(income > min && income > max) {
                tax += (max - min) * rate;
            }
        }

        return tax;
    }

    public void incrementSingle(double deduction) {
        stdDeductSingle += deduction;
    }

    public void incrementJoint(double deduction) {
        stdDeductJoint += deduction;
    }

    public void addBracket(TaxBracket bracket) {
        brackets.add(bracket);
    }

    public void addCounty(County county) {
        county.setState(this);
        counties.add(county);
    }

    public boolean hasCounty(String name) {
        return counties.stream().anyMatch(county -> county.getName().equals(name));
    }

    public County getCounty(String name) {
        return counties.stream().filter(county -> county.getName().equals(name)).findFirst().orElse(null);
    }

    public void addOrphanCity(City city) {
        orphanCities.add(city);
    }

    public List<City> getAllCities() {
        List<City> cities = new LinkedList<>();
        counties.forEach(county -> cities.addAll(county.getCities()));
        return cities;
    }
}
