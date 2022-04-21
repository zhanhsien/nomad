package main.kiwitor.nomad.rest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import main.kiwitor.nomad.model.City;
import main.kiwitor.nomad.model.House;
import main.kiwitor.nomad.model.request.RealtorRequest;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import java.util.LinkedList;
import java.util.List;

/**
 * DONE
 */
public class RealtorApi {
    private static final String BASE_URL = "https://www.realtor.com/";
    private static final String PROPERTY_RESOURCE = "api/v1/hulk";

    public static void getPropertyValues(City city) {
        List<House> houses = new LinkedList<>();
        try(RestUtils restUtils = new RestUtils(BASE_URL, PROPERTY_RESOURCE)) {
            restUtils.query("schema", "vesta");

            houses.addAll(getHouses(restUtils, city, true));
            houses.addAll(getHouses(restUtils, city, false));
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            city.setHouses(houses);
        }
    }

    private static List<House> getHouses(RestUtils restUtils, City city, boolean sold) throws JsonProcessingException {
        List<House> houses = new LinkedList<>();
        ObjectMapper mapper = new ObjectMapper();
        RealtorRequest request = new RealtorRequest(sold);
        request.setLocation(city);
        String serialized = mapper.writeValueAsString(request);

        MultivaluedMap<String, Object> headers = new MultivaluedHashMap<>();
//            headers.putSingle("Content-Type", "application/json");

        Response response = restUtils.post(serialized, headers);
        String result = response.readEntity(String.class);
        JSONObject json = new JSONObject(result).getJSONObject("data");

        if(!json.isNull("home_search")) {
            JSONArray results = json.getJSONObject("home_search").getJSONArray("results");
            JavaType houseCollectionType = mapper.getTypeFactory().constructCollectionType(List.class, House.class);
            houses = mapper.readValue(results.toString(), houseCollectionType);
        }

        return houses;
    }
}
