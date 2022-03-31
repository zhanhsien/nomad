package main.kiwitor.nomad.model.request;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.*;
import main.kiwitor.nomad.model.City;
import main.kiwitor.nomad.model.deserialize.RealtorRequestDeserializer;
import main.kiwitor.nomad.model.serialize.RealtorRequestSerializer;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.*;

@Getter
@Setter
@ToString
@AllArgsConstructor
@JsonSerialize(using = RealtorRequestSerializer.class)
@JsonDeserialize(using = RealtorRequestDeserializer.class)
public class RealtorRequest implements Serializable {
    private String query;

    private int ageMax;

    private int sqftMin;
    private int sqftMax;

    private List<String> type;
    private List<String> status;

    private int offset;
    private int limit;

    @Setter(AccessLevel.NONE)
    private String location;

    //yyyy-MM-dd'T'HH:mm:ss.SSS'Z'
    private String soldDate;

    public RealtorRequest() {
        this(false);
    }

    public RealtorRequest(boolean sold) {
        query = "\nquery ConsumerSearchQuery($query: HomeSearchCriteria!, $limit: Int, $offset: Int, $sort: [SearchAPISort], $sort_type: SearchSortType, $client_data: JSON, $bucket: SearchAPIBucket)\n{\n  home_search: home_search(query: $query,\n    sort: $sort,\n    limit: $limit,\n    offset: $offset,\n    sort_type: $sort_type,\n    client_data: $client_data,\n    bucket: $bucket,\n  ){\n    count\n    total\n    results {\n      property_id\n      list_price\n      primary_photo (https: true){\n        href\n      }\n      listing_id\n      virtual_tours{\n        href\n        type\n      }\n      status\n      permalink\n      price_reduced_amount\n      description{\n        beds\n        baths\n        baths_full\n        baths_3qtr\n        baths_half\n        sqft\n        lot_sqft\n        baths_max\n        baths_min\n        beds_max\n        sqft_min\n        sqft_max\n        type\n        sold_price\n        sold_date\n      }\n      location{\n        street_view_url\n        address{\n          line\n          postal_code\n          state\n          state_code\n          city\n          coordinate {\n            lat\n            lon\n          }\n        }\n      }\n      open_houses {\n        start_date\n        end_date\n      }\n      flags{\n        is_coming_soon\n        is_new_listing (days: 14)\n        is_price_reduced (days: 30)\n        is_foreclosure\n        is_new_construction\n        is_pending\n        is_contingent\n      }\n      list_date\n      photos(limit: 1, https: true){\n        href\n      }\n    }\n  }\n}";
        ageMax = 2000;
        sqftMin = 2500;
        sqftMax = 3000;
        type = new LinkedList<>(Collections.singletonList("single_family"));
        offset = 0;
        limit = 200;

        status = new LinkedList<>(sold ? Collections.singletonList("sold") : Arrays.asList("for_sale", "ready_to_build"));

        if(sold) {
            LocalDateTime aMonthAgo = LocalDateTime.now().minusMonths(1);
            soldDate = aMonthAgo.toString() + 'Z';
        }
    }

    public void setLocation(City city) {
        this.location = city.getName().concat(", ").concat(city.getState().getCode());
    }
}


