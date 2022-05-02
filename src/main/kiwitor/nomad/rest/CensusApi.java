package main.kiwitor.nomad.rest;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import main.kiwitor.nomad.Nomad;
import main.kiwitor.nomad.model.CensusEntry;
import org.apache.commons.io.IOUtils;

import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class CensusApi {
    private static final String BASE_URL = "http://api.census.gov/";
    private static final String CENSUS_RESOURCE = "data/2019/pep/population";
    private static final String CITY_QUERY = "?key=%s&get=NAME,POP&for=place";
    private static final String API_KEY = "da5d0297bc1c5c7e8f88c09eee5b3b48e5198080";

    private static List<CensusEntry> cityIndex;

    //TODO: This is mocked for now because RESTEasy doesn't have a practical way to follow redirects
    public static List<CensusEntry> getCities() {
        if(Objects.isNull(cityIndex)) {
//            String path = BASE_URL.concat(CENSUS_RESOURCE).concat(String.format(CITY_QUERY, API_KEY));
//            try(RestUtils restUtils = new RestUtils(path)) {
////                restUtils.query(getParams());
//                MultivaluedMap<String, Object> headers = new MultivaluedHashMap<>();
//                headers.putSingle("Content-Type", "application/json");
//                headers.putSingle("Accept", "*/*");
//                headers.putSingle("Host", "api.census.gov");
//                headers.putSingle("Connection", "keep-alive");
//                headers.putSingle("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/100.0.4896.75 Safari/537.36");
//                headers.putSingle("Accept-Encoding", "gzip, deflate, br");
//
//                Response response = restUtils.get(headers);
//                String result = response.readEntity(String.class);
//
//                Gson gson = new Gson();
//                Type censusCollectionType = new TypeToken<Collection<CensusEntry>>(){}.getType();
//                cityIndex = gson.fromJson(result, censusCollectionType);
//            }

            Gson gson = new Gson();
            Type censusCollectionType = new TypeToken<Collection<CensusEntry>>(){}.getType();
            cityIndex = gson.fromJson(getResponse(), censusCollectionType);
        }

        return cityIndex;
    }

    private static Map<String, String> getParams() {
        Map<String, String> params = new HashMap<>();
        params.put("key", API_KEY);
        params.put("get", "NAME");
        params.put("for", "place:*");

        return params;
    }

    private static String getResponse() {
        final String fileName = "data/censusResponse.json";
        ClassLoader classloader = CensusApi.class.getClassLoader();

        String response = null;
        try (InputStream is = classloader.getResourceAsStream(fileName)) {
            response = IOUtils.toString(is, StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return response;
    }
}
