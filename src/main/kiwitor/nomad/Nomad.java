package main.kiwitor.nomad;

import main.kiwitor.nomad.model.v2.CityBean;
import main.kiwitor.nomad.process.CityProcessor;
import java.util.List;

public class Nomad {
    public static void main(String[] args) {
        Nomad nomad = new Nomad();
        nomad.process();
    }

    private void process() {
        CityProcessor processor = new CityProcessor();
        List<CityBean> cities = processor.getFilteredCities();
        System.out.println(cities.size());
    }
}
