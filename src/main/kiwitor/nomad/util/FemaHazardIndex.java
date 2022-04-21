package main.kiwitor.nomad.util;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import main.kiwitor.nomad.Nomad;
import main.kiwitor.nomad.model.County;
import main.kiwitor.nomad.model.FemaEntry;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class FemaHazardIndex {
    private static Map<String, String> hazardIndex;
    private static Map<String, FemaEntry> entries;

    public static void getHazardIndex(County county) {
        if(Objects.isNull(hazardIndex)) {
            final String fileName = "NRI_Table_Counties/NRI_Table_Counties.csv";
            ClassLoader classloader = Nomad.class.getClassLoader();

            try (InputStream is = classloader.getResourceAsStream(fileName)) {
                String content = IOUtils.toString(is, StandardCharsets.UTF_8);
                MappingIterator<FemaEntry> femaItr = new CsvMapper()
                        .readerWithTypedSchemaFor(FemaEntry.class).readValues(content);

                Spliterator<FemaEntry> spliterator = Spliterators.spliteratorUnknownSize(femaItr, 0);
                hazardIndex = StreamSupport.stream(spliterator, true)
                        .filter(entry -> entry.getRiskScore().matches("-?\\d+(\\.\\d+)?"))
                        .collect(Collectors.toMap(
                            e -> e.getCounty().concat(" ").concat(e.getCountyType()).concat(":").concat(e.getStateCode()).toLowerCase(),
                            e -> e.getRiskScore().concat("\t").concat(e.getRiskRating().concat("\t").concat(e.getPopulation()))
                ));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            String name = county.getName().replaceAll("Saint", "St.");
            String key = name.concat(":").concat(county.getState().getCode()).toLowerCase();
            county.setHazardIndex(hazardIndex.get(key));
        } catch (Exception e) {
            System.out.println("Couldn't find: " + county.getName());
        }
    }

    public static Map<String, FemaEntry> getEntries() {
        if(Objects.isNull(entries)) {
            final String fileName = "NRI_Table_Counties/NRI_Table_Counties.csv";
            ClassLoader classloader = Nomad.class.getClassLoader();

            try (InputStream is = classloader.getResourceAsStream(fileName)) {
                String content = IOUtils.toString(is, StandardCharsets.UTF_8);
                MappingIterator<FemaEntry> femaItr = new CsvMapper()
                        .readerWithTypedSchemaFor(FemaEntry.class).readValues(content);

                Spliterator<FemaEntry> spliterator = Spliterators.spliteratorUnknownSize(femaItr, 0);
                entries = StreamSupport.stream(spliterator, true)
                        .collect(Collectors.toMap(
                                e -> e.getCounty().concat(" ").concat(e.getCountyType()).concat(",").concat(e.getStateCode()).toLowerCase(),
                                e -> e
                        ));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return entries;
    }
}
