package main.kiwitor.nomad.model.v2;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import main.kiwitor.nomad.model.Zone;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
public class CityBean extends Zone {
    private String stateId;
    private String countyId;
    private transient int population;
    private transient Map<String, Integer> crimeStats = new HashMap<>();

    public Map<String, Double> getCrimeRates() {
        return crimeStats.entrySet().stream().collect(Collectors.toMap(
                Map.Entry::getKey,
                e -> (double) e.getValue() / population
        ));
    }

    public boolean sucks() {
        Map<String, Double> crimeRates = getCrimeRates();
        try {
            return
                    crimeRates.get("Aggravated Assault") > 0.4 ||
                            crimeRates.get("Arson") > 0.021 ||
                            crimeRates.get("Burglary") > 0.51 ||
                            crimeRates.get("Larceny and Theft") > 2.55 ||
                            crimeRates.get("Motor Vehicle Theft") > 0.7 ||
                            crimeRates.get("Murder and Manslaughter") > 0.01 ||
                            crimeRates.get("Rape") > 0.1 ||
                            crimeRates.get("Robbery") > 0.11 ||
                            crimeRates.get("Crime Rate (Total Incidents)") > 4.1 ||
                            crimeRates.get("Property Crime") > 3.7 ||
                            crimeRates.get("Violent Crime") > 0.6;
        } catch(Exception e) {
            System.out.println(getName());
            System.out.println(crimeStats);
        }

        return false;
    }

    public boolean isNotSafe() {
        return getCrimeRates().values().stream().allMatch(e -> e > 1);
    }

    public boolean isTotallyWorse(CityBean origin) {
        try {
            Map<String, Double> localRates = origin.getCrimeRates();
//            Map<String, Double> originRates = origin.getCrimeRates();

//            boolean completelyWorse = true;
//            for(String key : originRates.keySet()) {
//                if(localRates.get(key) < originRates.get(key)) {
//                    completelyWorse = false;
//                    break;
//                }
//            }
//
//            return completelyWorse;

            return getCrimeRates().entrySet().stream().allMatch(e -> localRates.get(e.getKey()) < e.getValue());
        } catch(Exception e) {
            return false;
        }
    }
}
