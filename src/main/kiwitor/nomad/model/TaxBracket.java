package main.kiwitor.nomad.model;

import lombok.Getter;

@Getter
public class TaxBracket {
    private double single;
    private double joint;
    private double rate;

    public TaxBracket(double single, double joint, double rate) {
        this.single = single;
        this.joint = joint;
        this.rate = rate / 100;
    }
}
