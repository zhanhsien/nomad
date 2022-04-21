//package main.kiwitor.nomad.rest;
//
//import com.fasterxml.jackson.databind.MappingIterator;
//import com.fasterxml.jackson.dataformat.csv.CsvMapper;
//import main.kiwitor.nomad.Nomad;
//import main.kiwitor.nomad.constants.TaxType;
//import main.kiwitor.nomad.model.*;
//import org.apache.commons.io.IOUtils;
//import org.json.JSONArray;
//import org.json.JSONObject;
//import org.jsoup.Jsoup;
//import org.jsoup.nodes.Element;
//import org.jsoup.select.Elements;
//
//import javax.ws.rs.core.Response;
//import java.io.IOException;
//import java.io.InputStream;
//import java.nio.charset.StandardCharsets;
//import java.util.*;
//import java.util.stream.Collectors;
//import java.util.stream.StreamSupport;
//
//public class CensusApi {
//    private static final String BASE_URL = "http://api.census.gov/";
//    private static final String CRIME_RATE_RESOURCE = "data/2019/pep/population";
//    private static final String CITY_QUERY = "?key=%s&get=NAME,POP&for=place:*&in=state:%s";
//    private static final String API_KEY = "da5d0297bc1c5c7e8f88c09eee5b3b48e5198080";
//
//    private static Map<String, CensusPlaceEntry> cityIndex;
//
//    public static void getCityPopulation(City city) {
//        if(Objects.isNull(cityIndex)) {
//            final String fileName = "censusCityIds.csv";
//            ClassLoader classloader = Nomad.class.getClassLoader();
//
//            try (InputStream is = classloader.getResourceAsStream(fileName)) {
//                String content = IOUtils.toString(is, StandardCharsets.UTF_8);
//                MappingIterator<CensusPlaceEntry> censusItr = new CsvMapper()
//                        .readerWithTypedSchemaFor(CensusPlaceEntry.class).readValues(content);
//
//                Spliterator<CensusPlaceEntry> spliterator = Spliterators.spliteratorUnknownSize(censusItr, 0);
//                cityIndex = StreamSupport.stream(spliterator, true)
//                        .collect(Collectors.toMap(
//                                e -> e.getName().concat(":").concat(e.getStateCode()).toLowerCase(),
//                                e -> e
//                        ));
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//
//        try {
//            String name = county.getName().replaceAll("Saint", "St.");
//            String key = name.concat(":").concat(county.getState().getCode()).toLowerCase();
//            county.setHazardIndex(hazardIndex.get(key));
//        } catch (Exception e) {
//            System.out.println("Couldn't find: " + county.getName());
//        }
//    }
//
//    public static void getPopulation(City city) {
//        if(Objects.isNull(cityPopulations)) {
//            try(RestUtils restUtils = new RestUtils(BASE_URL)) {
//                restUtils.query(getParams());
//
//                Response response = restUtils.get();
//                String result = response.readEntity(String.class);
//                deserialize(result);
//            }
//        }
//
//        city.setPopulation(cityPopulations.getOrDefault(generateKey(city), 0));
//    }
//
//    private static void deserialize(String str) {
//        JSONArray json = new JSONArray(str);
//        StreamSupport.stream(json.spliterator(), true).collect(Collectors.toMap(
//                (e) -> {
//                    JSONArray cityArray = new JSONArray(e);
//                    String cityName =
//                    cityArray.optString(0).substring(0, )
//                }, (e) -> new JSONArray(e).optString(1)
//        ));
//    }
//
//    private static Map<String, String> getParams() {
//        Map<String, String> params = new HashMap<>();
//        params.put("key", API_KEY);
//        params.put("get", "NAME,POP");
//        params.put("for", "place:*");
//
//        return params;
//    }
//
//    private static String generateKey(City city) {
//        return city.getName().concat(":").concat(city.getState().getCode()).toLowerCase();
//    }
//}
