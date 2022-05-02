package main.kiwitor.nomad.model.v2;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class TaxBracket {
    private double amount;

    @SerializedName(value = "marginal_rate")
    private double rate;
}
