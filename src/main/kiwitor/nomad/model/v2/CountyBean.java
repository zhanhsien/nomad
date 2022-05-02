package main.kiwitor.nomad.model.v2;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import main.kiwitor.nomad.model.FemaEntry;
import main.kiwitor.nomad.model.Zone;

import java.util.LinkedList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class CountyBean extends Zone {
    private String stateId;
    private List<CityBean> cities = new LinkedList<>();
    private transient FemaEntry hazardIndex;
    private transient double propertyTax;

    public boolean isSafe() {
        String riskRating = hazardIndex.getRiskRating();
        return !riskRating.equalsIgnoreCase("Very High")
                && !riskRating.equalsIgnoreCase("Relatively High");
    }
}

//TODO: Missing places
//NRI_ID	COUNTY	STATEABBRV	POPULATION
//C55037	Florence	WI	4423
//C55078	Menominee	WI	4232
//C50009	Essex	VT	6306
//C51041	Chesterfield	VA	316236
//C51087	Henrico	VA	306935
//C51013	Arlington	VA	207627
//C51760	Richmond	VA	204214
//C51179	Stafford	VA	128961
//C51177	Spotsylvania	VA	122397
//C51770	Roanoke	VA	97032
//C51095	James City	VA	67009
//C51199	York	VA	65464
//C51683	Manassas	VA	37821
//C51073	Gloucester	VA	36858
//C51149	Prince George	VA	35725
//C51145	Powhatan	VA	28046
//C51065	Fluvanna	VA	25691
//C51099	King George	VA	23584
//C51600	Fairfax	VA	22565
//C51075	Goochland	VA	21717
//C51127	New Kent	VA	18429
//C51125	Nelson	VA	15020
//C51007	Amelia	VA	12690
//C51133	Northumberland	VA	12330
//C51081	Greensville	VA	12243
//C51049	Cumberland	VA	10052
//C51115	Mathews	VA	8978
//C51620	Franklin	VA	8582
//C51036	Charles City	VA	7256
//C51097	King and Queen	VA	6945
//C51021	Bland	VA	6824
//C51017	Bath	VA	4731
//C48505	Zapata	TX	14018
//C48247	Jim Hogg	TX	5300
//C48105	Crockett	TX	3719
//C48173	Glasscock	TX	1226
//C48443	Terrell	TX	984
//C48311	McMullen	TX	707
//C48033	Borden	TX	641
//C48261	Kenedy	TX	416
//C48269	King	TX	286
//C48301	Loving	TX	82
//C46017	Buffalo	SD	1912
//C44009	Washington	RI	126979
//C44001	Bristol	RI	49875
//C36047	Kings	NY	2504700
//C36005	Bronx	NY	1385108
//C36085	Richmond	NY	468730
//C32005	Douglas	NV	46997
//C32023	Nye	NV	43946
//C32015	Lander	NV	5775
//C32021	Mineral	NV	4772
//C32029	Storey	NV	4010
//C32011	Eureka	NV	1987
//C32009	Esmeralda	NV	783
//C35028	Los Alamos	NM	17950
//C33003	Carroll	NH	47818
//C31007	Banner	NE	690
//C31117	McPherson	NE	539
//C37053	Currituck	NC	23547
//C37029	Camden	NC	9980
//C37095	Hyde	NC	5810
//C29510	St. Louis	MO	319294
//C26135	Oscoda	MI	8640
//C23017	Oxford	ME	57833
//C23025	Somerset	ME	52228
//C23015	Lincoln	ME	34457
//C23007	Franklin	ME	30768
//C23021	Piscataquis	ME	17535
//C24510	Baltimore	MD	620961
//C24027	Howard	MD	287085
//C25007	Dukes	MA	16535
//C25019	Nantucket	MA	10172
//C22089	St. Charles	LA	52780
//C22095	St. John the Baptist	LA	45924
//C22087	St. Bernard	LA	35897
//C22075	Plaquemines	LA	23042
//C22023	Cameron	LA	6839
//C21147	McCreary	KY	18306
//C15001	Hawaii	HI	185079
//C15009	Maui	HI	154834
//C15007	Kauai	HI	67091
//C15005	Kalawao	HI	90
//C13101	Echols	GA	4034
//C13307	Webster	GA	2799
//C11001	Washington	D.C.	601723
//C09013	Tolland	CT	152691
//C06043	Mariposa	CA	18251
//C06105	Trinity	CA	13786
//C06003	Alpine	CA	1175
//C02100	Haines	AK	2508
//C02275	Wrangell	AK	2369
//C02060	Bristol Bay	AK	997
//C02230	Skagway	AK	968
//C02282	Yakutat	AK	662