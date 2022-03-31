package main.kiwitor.nomad.rest;

import main.kiwitor.nomad.constants.TaxType;
import main.kiwitor.nomad.model.State;
import main.kiwitor.nomad.model.TaxBracket;
import org.json.JSONArray;
import org.json.JSONObject;

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

    private static final String SINGLE = "single";
    private static final String MARRIED = "married";
    private static final String TYPE = "type";
    private static final String NONE = "none";
    private static final String DEDUCTIONS = "deductions";
    private static final String DEDUCTION_AMOUNT = "deduction_amount";
    private static final String BRACKETS = "income_tax_brackets";
    private static final String BRACKET = "bracket";
    private static final String RATE = "marginal_rate";

    public static void setStateTax(State state) {
        String path = String.format(STATE_TAX_RESOURCE, year, state.getCode());
        try(RestUtils restUtils = new RestUtils(BASE_URL, path)) {
            MultivaluedMap<String, Object> headers = new MultivaluedHashMap<>();
            headers.putSingle("Content-Type", "application/json");
            headers.putSingle("Authorization", "Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJBUElfS0VZX01BTkFHRVIiLCJodHRwOi8vdGF4ZWUuaW8vdXNlcl9pZCI6IjYyMmY5NmU4MDI2NzRlNGU4M2U2MWI0MCIsImh0dHA6Ly90YXhlZS5pby9zY29wZXMiOlsiYXBpIl0sImlhdCI6MTY0NzI4NTk5Mn0.2C7YE3_V_s0TaD8lxBulcdovodbTVEMgY2b0I1Q9tKY");

            Response response = restUtils.get(headers);
            String result = response.readEntity(String.class);
            deserialize(state, result);
        }
    }

    private static void deserialize(State state,  String str) {
        JSONObject json = new JSONObject(str);
        JSONObject singleJson = json.getJSONObject(SINGLE);
        JSONObject jointJson = json.getJSONObject(MARRIED);

        if(singleJson.optString(TYPE).equals(NONE)) {
            state.setTaxType(TaxType.NONE);
        } else {
            JSONArray singleBracketsJson = singleJson.optJSONArray(BRACKETS);
            JSONArray jointBracketsJson = jointJson.optJSONArray(BRACKETS);
            for(int i = 0; i < singleBracketsJson.length(); i++) {
                JSONObject singleBracketJson = singleBracketsJson.getJSONObject(i);
                JSONObject jointBracketJson = jointBracketsJson.getJSONObject(i);
                double single = singleBracketJson.optDouble(BRACKET, 0.00);
                double joint = jointBracketJson.optDouble(BRACKET, 0.00);
                double rate = singleBracketJson.optDouble(RATE, 0.00);
                state.addBracket(new TaxBracket(single, joint, rate));
            }

            singleJson.optJSONArray(DEDUCTIONS).forEach(obj -> {
                JSONObject jsonObj = (JSONObject) obj;
                state.incrementSingle(jsonObj.optDouble(DEDUCTION_AMOUNT, 0.00));
            });

            jointJson.optJSONArray(DEDUCTIONS).forEach(obj -> {
                JSONObject jsonObj = (JSONObject) obj;
                state.incrementJoint(jsonObj.optDouble(DEDUCTION_AMOUNT, 0.00));
            });

            if(state.getBrackets().size() == 1) {
                state.setTaxType(TaxType.FLAT);
            } else {
                state.setTaxType(TaxType.TIER);
            }
        }
    }
}
