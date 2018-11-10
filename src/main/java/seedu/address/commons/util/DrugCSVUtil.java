package seedu.address.commons.util;

import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;

public class DrugCSVUtil {

    //filepath to drug dataset
    private static final String DATASET_PATH = "./src/main/resources/storage/datasetForDrugs.csv";

    private Reader reader;
    private CSVReader csvReader;

    private String keyword; //keyword to be searched for

    public DrugCSVUtil(String keyword) throws IOException{
        this.reader = Files.newBufferedReader(Paths.get(DATASET_PATH));
        this.csvReader = new CSVReaderBuilder(reader).withSkipLines(1).build();
        this.keyword = keyword;
    }

    /**
     * Reads through "dataSetForDrugs.csv" and returns its lines one by one.
     */
    public String[] nextMatchingEntry() throws IOException{
        String[] nextRecord;
        while ((nextRecord = csvReader.readNext()) != null) {
            if (nextRecord[1].toLowerCase().equals(keyword)) {
                return nextRecord;
            }
        }

        return null;
    }
}
