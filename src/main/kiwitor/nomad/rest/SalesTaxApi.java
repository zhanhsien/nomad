package main.kiwitor.nomad.rest;

import main.kiwitor.nomad.model.City;
import main.kiwitor.nomad.model.County;
import main.kiwitor.nomad.model.State;
import main.kiwitor.nomad.util.HtmlUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import javax.ws.rs.core.Response;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.StreamSupport;

public class SalesTaxApi {
    private static final String BASE_URL = "https://www.sale-tax.com/";
    private static final String ALL = "_all";

    private static Map<String, Double> stateSalesTax;

    public static void getStateSalesTax(State state) {
        if(Objects.isNull(stateSalesTax)) {
            try(RestUtils restUtils = new RestUtils(BASE_URL)) {
                Response response = restUtils.get();
                String result = response.readEntity(String.class);

                Document doc = Jsoup.parse(result);
                Elements elements = doc.select("tr[data-href]");

                stateSalesTax = elements.parallelStream().collect(Collectors.toMap(
                        (e) -> e.select("strong").text().replaceAll("\\s\\(([^)]+)\\)", ""),
                        (e) -> HtmlUtils.parsePercent(e.select("td[class=\"center\"]").text().replaceAll("%", ""))
                ));
            }
        }

        state.setSalesTax(stateSalesTax.getOrDefault(state.getName(), 0.0));
    }

    public static List<City> getCities(State state) {
        return getCities(state, false);
    }

    public static List<City> getCities(State state, boolean all) {
        if(!all) {
            return getCities(state, null);
        }

        List<City> cities = new LinkedList<>();
        StreamSupport.stream(IntStream.rangeClosed('A', 'Z').spliterator(), true)
                .forEach(letter -> cities.addAll(getCities(state, letter)));
        return cities;
    }

    private static List<City> getCities(State state, Integer subPath) {
        String path = state.getName().replaceAll(" ", "");
        if(Objects.nonNull(subPath)) {
            String letter = Character.toString((char) subPath.intValue());
            path = path.concat("_").concat(letter);
        }
        try(RestUtils restUtils = new RestUtils(BASE_URL, path)) {
            Response response = restUtils.get();
            String result = response.readEntity(String.class);

            Document doc = Jsoup.parse(result);
            Elements elements = doc.select("td[data-href$=" + state.getCode() + "] a strong");

            return elements.stream()
                    .map(element -> new City(element.text()))
                    .collect(Collectors.toList());
        }
    }

    public static void getCityData(State state, City city) {
        String cityName = city.getName();

        if(cityName.equalsIgnoreCase("Paul")) {
            System.out.println("For fuck sakes");
        }

        String path = sanitizePath(cityName).replaceAll("[^A-Za-z0-9]", "").concat(state.getCode());
        String cityStr = escapeCityName(cityName);

        try(RestUtils restUtils = new RestUtils(BASE_URL, path)) {
            Response response = restUtils.get();
            String result = response.readEntity(String.class);

            Document doc = Jsoup.parse(result);
            Elements elements = doc.select("table");

            Element cityElement = elements.select("td:not(County):contains(" + cityStr + ")").last();
            double cityRate = Double.parseDouble(Objects.requireNonNull(cityElement).nextElementSibling().text().replaceAll("%", "")) / 100;
            city.setSalesTax(cityRate);

            Elements countyElements = elements.select("a[href$=" + state.getCode() + "]");
            if(countyElements.size() > 0) {
                String countyName = countyElements.text();
                double countyRate = Double.parseDouble(Objects.requireNonNull(countyElements.parents().get(0).nextElementSibling()).text().replaceAll("%", "")) / 100;
                if (state.hasCounty(countyName)) {
                    County county = state.getCounty(countyName);
                    county.addCity(city);
                } else {
                    County county = new County(countyName);
                    county.setSalesTax(countyRate);
                    county.setState(state);
                    county.addCity(city);
                    state.addCounty(county);
                }
            } else {
                state.addOrphanCity(city);
            }
        } catch(Exception e) {
//            e.printStackTrace();
//            System.out.println("SalesTaxApi.getCityData(new State(States."
//                    + state.getName().toUpperCase().replaceAll(" ", "_")
//                    + "), "
//                    + "\"" + cityName + "\");");
        }
    }

    private static String escapeCityName(String str) {
        StringBuilder sb = new StringBuilder(str);
        for(int i = 0; i < sb.length(); i++) {
            char c = sb.charAt(i);

            if(c == '\'') {
                sb.insert(i, "\\");
                i++;
            }
        }

        return sb.toString();
    }

    private static String sanitizePath(String city) {
        StringBuilder sb = new StringBuilder(city);
        for(int i = 0; i < sb.length(); i++) {
            char cPrev = sb.charAt(i == 0 ? 0 : i - 1);
            char c = sb.charAt(i);

            boolean isValid = (c >= 65 && c <= 90) || (c >= 97 && c <= 122) || c == 32;
            if(!isValid) {
                switch((int) c) {
                    case 256:
                    case 192:
                        sb.setCharAt(i, 'A');
                        break;
                    case 257:
                    case 225:
                    case 224:
                        sb.setCharAt(i, 'a');
                        break;
                    case 233:
                    case 275:
                        sb.setCharAt(i, 'e');
                        break;
                    case 299:
                        sb.setCharAt(i, 'i');
                        break;
                    case 363:
                        sb.setCharAt(i, 'u');
                        break;
                    case 332:
                        sb.setCharAt(i, 'O');
                        break;
                    case 333:
                        sb.setCharAt(i, 'o');
                        break;
                    case 241:
                        sb.setCharAt(i, 'n');
                        break;
                }
            }

            if(i == 0 || cPrev == ' ' || cPrev == '\'' || cPrev == '-' || cPrev == '(' || cPrev == '‘' || cPrev == 'ʻ') {
                sb.setCharAt(i, Character.toUpperCase(sb.charAt(i)));
            }
        }

        return sb.toString();
    }
}
