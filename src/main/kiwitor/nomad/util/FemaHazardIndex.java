package main.kiwitor.nomad.util;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import main.kiwitor.nomad.Nomad;
import main.kiwitor.nomad.model.County;
import main.kiwitor.nomad.model.FemaEntry;
import main.kiwitor.nomad.model.v2.CountyBean;
import main.kiwitor.nomad.model.v2.StateBean;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class FemaHazardIndex {
    private static Map<String, FemaEntry> hazardIndex;

    public static void getHazardIndex(StateBean state, CountyBean county) {
        if(Objects.isNull(hazardIndex)) {
            final String fileName = "NRI_Table_Counties/NRI_Table_Counties.csv";
            ClassLoader classloader = Nomad.class.getClassLoader();

            try (InputStream is = classloader.getResourceAsStream(fileName)) {
                String content = IOUtils.toString(is, StandardCharsets.UTF_8);
                MappingIterator<FemaEntry> femaItr = new CsvMapper()
                        .readerWithTypedSchemaFor(FemaEntry.class).readValues(content);

                Spliterator<FemaEntry> spliterator = Spliterators.spliteratorUnknownSize(femaItr, 0);
                hazardIndex = StreamSupport.stream(spliterator, true)
//                        .filter(entry -> entry.getRiskScore().matches("-?\\d+(\\.\\d+)?"))
                        .collect(Collectors.toMap(FemaEntry::getNriId, e -> e));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            county.setHazardIndex(hazardIndex.get(county.getId()));
        } catch (Exception e) {
            System.out.println("Couldn't find: " + county.getName());
        }
    }

    private static List<FemaEntry> index;
    public static List<FemaEntry> getIndex() {
        if(Objects.isNull(index)) {
            final String fileName = "NRI_Table_Counties/NRI_Table_Counties.csv";
            ClassLoader classloader = Nomad.class.getClassLoader();

            try (InputStream is = classloader.getResourceAsStream(fileName)) {
                String content = IOUtils.toString(is, StandardCharsets.UTF_8);
                MappingIterator<FemaEntry> femaItr = new CsvMapper()
                        .readerWithTypedSchemaFor(FemaEntry.class).readValues(content);

                index = femaItr.readAll();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return index;
    }
}
