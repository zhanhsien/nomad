package main.kiwitor.nomad.rest;

import main.kiwitor.nomad.model.v2.CountyBean;
import main.kiwitor.nomad.model.v2.StateBean;
import main.kiwitor.nomad.util.HtmlUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import javax.ws.rs.core.Form;
import javax.ws.rs.core.Response;
import java.util.*;
import java.util.stream.Collectors;

public class SmartAssetApi {
    private static final String BASE_URL = "https://smartasset.com/";
    private static final String LOCATION_RESOURCE = "api/ajax/data/locations";
    private static final String PROPERTY_TAX_RESOURCE = "taxes/%s-property-tax-calculator";

    private static final int year = 2020;

    public static Map<String, String> problematicCounties = new HashMap<>();

    private static Map<String, Map<String, Double>> propertyTaxRates = new HashMap<>();
    public static void getPropertyTax(StateBean state, CountyBean county) {
        if(Objects.isNull(propertyTaxRates.get(state.getId()))) {
            String path = String.format(PROPERTY_TAX_RESOURCE, state.getName().toLowerCase().replaceAll(" ", "-"));
            try (RestUtils restUtils = new RestUtils(BASE_URL, path)) {
                Response response = restUtils.get();
                String result = response.readEntity(String.class);

                Document doc = Jsoup.parse(result);
                Elements elements = doc.select("div[id^=table-pk] tr:not([class=head])");

                propertyTaxRates.put(state.getId(), elements.parallelStream().collect(Collectors.toMap(
                    e -> Objects.requireNonNull(e.select("td").first()).text(),
                    e -> HtmlUtils.parsePercent(Objects.requireNonNull(e.select("td").last()).text())
                )));
            }
        }

        try {
            Map<String, Double> stateMap = propertyTaxRates.get(state.getId());
            double rate = (double) county.getOrDefault(stateMap, -1.0);
            county.setPropertyTax(rate);
        } catch(Exception e) {
//            System.out.println("Shit fucked up");
        }
    }

    public static void getPropertyTax1(StateBean state, CountyBean county) {
        String path = String.format(PROPERTY_TAX_RESOURCE, state.getName().toLowerCase().replaceAll(" ", "-"));
        try(RestUtils restUtils = new RestUtils(BASE_URL, path)) {
            restUtils.query("render", "json");

            String countyName = getCounty(state, county);

            if(!StringUtils.isNotEmpty(countyName)) {
                throw new Exception("Problem");
            }

            Form form = new Form();
            form.param("ud-current-location", countyName);

            Response response = restUtils.post(form);
            String result = response.readEntity(String.class);

            JSONObject json = new JSONObject(result).getJSONObject("page_data");
            double propertyTax = json.optDouble("countyTax");
            county.setPropertyTax(propertyTax / 100);
        } catch(Exception e) {
            problematicCounties.put(county.getId(), county.getName().concat("\t").concat(state.getCode()));
            System.out.println("Problem: " + county.getName() + " State: " + state.getCode());
        }
    }

    private static String getCounty(StateBean state, CountyBean county) {
        String countyName = county.getName()
                .replaceAll("-", " ")
                .replaceAll("Kentucky State ", "")
                .replaceAll("'", "");
//                .replaceAll("Saint", "St.");

        for(int i = countyName.length(); i > 2; i--) {
            try(RestUtils restUtils = new RestUtils(BASE_URL, LOCATION_RESOURCE)) {
                String countyQuery = i == countyName.length() ?
                        countyName.substring(0, i).concat(", ").concat(state.getCode()) :
                        countyName.substring(0, i);

                restUtils.query("id", countyQuery);

                Response response = restUtils.get();
                String result = response.readEntity(String.class);
                String match = parseCountyMatch(state, county, result);
                if(StringUtils.isNotEmpty(match)) {
                    return match;
                }
            }
        }

        return null;
    }

    private static String parseCountyMatch(StateBean state, CountyBean county, String result) {
        if(StringUtils.isNotEmpty(result)) {
            List<String> matches = Arrays.stream(result.split("\n"))
                    .filter(match -> match.endsWith(state.getCode()))
                    .collect(Collectors.toList());

            if(matches.size() == 1) {
                String match = matches.get(0);
                return match.substring(0, match.indexOf(':'));
            } else {
                for(String match : matches) {
                    String sub = match.substring(match.indexOf(':') + 1, match.indexOf(','));
                    if(sub.equals(county.getName()) && match.startsWith("COUNTY")) {
                        return match.substring(0, match.indexOf(':'));
                    }
                }
            }
        }

        return null;
    }

    //Unable to find, but they probably exist by using the progressive search
    //C22037	East Feliciana	LA
    //C22065	Madison	LA
    //C22003	Allen	LA
    //C22009	Avoyelles	LA
    //C22081	Red River	LA
    //C22013	Bienville	LA
    //C28055	Issaquena	MS
    //C02164	Lake and Peninsula	AK
    //C02013	Aleutians East	AK
    //C02050	Bethel	AK
    //C02068	Denali	AK
    //C02158	Kusilvak	AK
    //C46102	Shannon	SD
}
