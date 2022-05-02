package main.kiwitor.nomad.model.v2;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import main.kiwitor.nomad.model.Zone;
import main.kiwitor.nomad.model.response.TaxeeResponse;

import java.util.LinkedList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class StateBean extends Zone {
    private String code;
//    private List<CountyBean> counties = new LinkedList<>();
    private transient TaxeeResponse incomeTax;
}
