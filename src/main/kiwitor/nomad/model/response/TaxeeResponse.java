package main.kiwitor.nomad.model.response;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.Setter;
import main.kiwitor.nomad.constants.TaxType;
import main.kiwitor.nomad.model.v2.TaxBracket;

import java.util.List;
import java.util.ListIterator;
import java.util.Objects;

@Getter
@Setter
public class TaxeeResponse {
    private static final String NONE = "none";

    private TaxFilingStatus single;

    @SerializedName(value = "married")
    private TaxFilingStatus joint;

    @SerializedName(value = "married_separately")
    private TaxFilingStatus marriedSeparately;

    @SerializedName(value = "head_of_household")
    private TaxFilingStatus headOfHousehold;

    @Getter
    @Setter
    private class TaxFilingStatus {
        private String type;
        private List<Deduction> deductions;

        @SerializedName(value = "income_tax_brackets")
        private List<TaxBracket> brackets;

        @Getter
        @Setter
        private class Deduction {
            @SerializedName(value = "deduction_name")
            private String name;

            @SerializedName(value = "deduction_amount")
            private int amount;
        }

        private double getTotalDeductions() {
            return deductions.stream().mapToDouble(Deduction::getAmount).reduce(0, Double::sum);
        }

        private double getIncomeTax(double income) {
            double taxIncome = income - getTotalDeductions();
            double tax = 0.0;

            ListIterator<TaxBracket> itr = brackets.listIterator();
            while(itr.hasNext()) {
                TaxBracket curr = itr.next();
                TaxBracket next = itr.hasNext() ? itr.next() : null;
                itr.previous();
                if(Objects.isNull(next) || (taxIncome > curr.getAmount() && taxIncome <= next.getAmount())) {
                    tax += (taxIncome - curr.getAmount()) * curr.getRate();
                    break;
                } else if(taxIncome > next.getAmount()) {
                    tax += (next.getAmount() - curr.getAmount()) * curr.getRate();
                }
            }

            return tax;
        }
    }

    public TaxType getTaxType() {
        if(Objects.isNull(single.brackets)) {
            return TaxType.NONE;
        } else if(single.brackets.size() == 1) {
            return TaxType.FLAT;
        }

        return TaxType.TIER;
    }

    public double getIncomeTaxSingle(double income) {
        return single.getIncomeTax(income);
    }

    public double getIncomeTaxJoint(double income) {
        return joint.getIncomeTax(income);
    }
}
