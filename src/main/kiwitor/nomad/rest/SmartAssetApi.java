package main.kiwitor.nomad.rest;

import main.kiwitor.nomad.model.County;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;

import javax.ws.rs.core.Form;
import javax.ws.rs.core.Response;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class SmartAssetApi {
    private static final String BASE_URL = "https://smartasset.com/";
    private static final String LOCATION_RESOURCE = "api/ajax/data/locations";
    private static final String PROPERTY_TAX_RESOURCE = "taxes/%s-property-tax-calculator";

    private static final int year = 2020;

    public static void getPropertyTax(County county) {
        String path = String.format(PROPERTY_TAX_RESOURCE, county.getState().getName().toLowerCase().replaceAll(" ", "-"));
        try(RestUtils restUtils = new RestUtils(BASE_URL, path)) {
            restUtils.query(new String[] {"render", "json"});

            String countyName = getCounty(county);

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
            System.out.println("Problem: " + county.getName() + " State: " + county.getState().getCode());
        }
    }

    public static String getCounty(County county) {
        String countyName = county.getName()
                .replaceAll("-", " ")
                .replaceAll("Kentucky State ", "");

        for(int i = countyName.length(); i > 2; i--) {
            try(RestUtils restUtils = new RestUtils(BASE_URL, LOCATION_RESOURCE)) {
                String countyQuery = i == countyName.length() ?
                        countyName.substring(0, i).concat(", ").concat(county.getState().getCode()) :
                        countyName.substring(0, i);

                restUtils.query(new String[] {"id", countyQuery});

                Response response = restUtils.get();
                String result = response.readEntity(String.class);
                String match = parseCountyMatch(county, result);
                if(StringUtils.isNotEmpty(match)) {
                    return match;
                }
            }
        }

        return null;
    }

    private static String parseCountyMatch(County county, String result) {
        if(StringUtils.isNotEmpty(result)) {
            List<String> matches = Arrays.stream(result.split("\n"))
                    .filter(match -> match.endsWith(county.getState().getCode()))
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
}
