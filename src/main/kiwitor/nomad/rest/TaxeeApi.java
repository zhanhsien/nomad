package main.kiwitor.nomad.rest;

import com.google.gson.Gson;
import main.kiwitor.nomad.model.response.TaxeeResponse;
import main.kiwitor.nomad.model.v2.StateBean;

import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

/**
 * DONE
 */
public class TaxeeApi {
    private static final String BASE_URL = "https://taxee.io/api/";
    private static final String STATE_TAX_RESOURCE = "v2/state/%s/%s";

    private static final int year = 2020;

    public static StateBean setStateTax(StateBean state) {
        String path = String.format(STATE_TAX_RESOURCE, year, state.getCode());
        try(RestUtils restUtils = new RestUtils(BASE_URL, path)) {
            MultivaluedMap<String, Object> headers = new MultivaluedHashMap<>();
            headers.putSingle("Content-Type", "application/json");
            headers.putSingle("Authorization", "Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJBUElfS0VZX01BTkFHRVIiLCJodHRwOi8vdGF4ZWUuaW8vdXNlcl9pZCI6IjYyMmY5NmU4MDI2NzRlNGU4M2U2MWI0MCIsImh0dHA6Ly90YXhlZS5pby9zY29wZXMiOlsiYXBpIl0sImlhdCI6MTY0NzI4NTk5Mn0.2C7YE3_V_s0TaD8lxBulcdovodbTVEMgY2b0I1Q9tKY");

            Response response = restUtils.get(headers);
            String result = response.readEntity(String.class);

            Gson gson = new Gson();
            TaxeeResponse resp = gson.fromJson(result, TaxeeResponse.class);
            state.setIncomeTax(resp);
        }

        return state;
    }
}
