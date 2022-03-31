package main.kiwitor.nomad.rest;

import main.kiwitor.nomad.model.City;
import main.kiwitor.nomad.util.HtmlUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import javax.ws.rs.core.Form;
import javax.ws.rs.core.Response;

public class SalaryApi {
    private static final String BASE_URL = "https://www.salary.com/";
    private static final String COMPARE_RESOURCE = "research/cost-of-living/compare/%s/%s";

    public static void getCostOfLivingDelta(City origin, City destination) {
        String path = String.format(COMPARE_RESOURCE, getCityPath(origin), getCityPath(destination));
        try(RestUtils restUtils = new RestUtils(BASE_URL, path)) {
            Form form = new Form();
            form.param("chl", getCityParam(origin));
            form.param("nhl", getCityParam(destination));

            Response response = restUtils.post(form);

            if(response.getStatus() == 403) {
                return;
            }

            String result = response.readEntity(String.class);

            Document doc = Jsoup.parse(result);
            Elements elements = doc.select("div[class=col-md-4]:contains(Percent Change)");

            double costOfLiving = HtmlUtils.parsePercent(elements.next().text());
            double geoPay = HtmlUtils.parsePercent(elements.next().next().text());

            destination.setCostOfLiving(costOfLiving);
            destination.setGeoPay(geoPay);
            destination.setHasGeoPay(true);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    private static String getCityPath(City city) {
        return getCityParam(city).toLowerCase();
    }

    private static String getCityParam(City city) {
        return city.getName().replaceAll(" ", "-")
                .concat("-" + city.getState().getCode());
    }
}
