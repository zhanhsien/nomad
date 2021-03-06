package main.kiwitor.nomad.rest;

import main.kiwitor.nomad.model.City;
import main.kiwitor.nomad.model.v2.CityBean;
import main.kiwitor.nomad.model.v2.StateBean;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import javax.ws.rs.core.Response;
import java.util.Objects;
import java.util.stream.Collectors;

public class CityRatingApi {
    private static final String BASE_URL = "https://www.cityrating.com/";
    private static final String CRIME_RATE_RESOURCE = "crime-statistics/%s/%s.html";

    public static void getCrimeStats(StateBean state, CityBean city) {
        String stateName = getPathParam(state.getName());
        String cityName = getPathParam(city.getName());
        String path = String.format(CRIME_RATE_RESOURCE, stateName, cityName);
        try(RestUtils restUtils = new RestUtils(BASE_URL, path)) {
            Response response = restUtils.get();
            String result = response.readEntity(String.class);

            Elements elements = Jsoup.parse(result).select("table[id=contentMain_grdSummaryData] .key");

            city.setCrimeStats(elements.stream().collect(Collectors.toMap(
                    Element::text,
                    (e) -> {
                        Element stat = e.nextElementSibling();
                        if(Objects.isNull(stat) || stat.text().equalsIgnoreCase("N/A")) {
                            return -1;
                        }

                        return Integer.parseInt(stat.text().replaceAll(",", ""));
                    }
            )));
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    private static String getPathParam(String param) {
        return param.replaceAll(" ", "-")
                .replaceAll("Saint", "St").toLowerCase();
    }
}