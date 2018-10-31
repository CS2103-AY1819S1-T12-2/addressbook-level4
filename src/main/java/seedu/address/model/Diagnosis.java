package seedu.address.model;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.ArrayUtils;

import seedu.address.MainApp;
import seedu.address.commons.util.FileUtil;
import seedu.address.model.symptom.Disease;
import seedu.address.model.symptom.Symptom;

/**
 * Wraps all data at the diagnosis level.
 */
public class Diagnosis {

    private static final String pathStringForCSV = "datasetForSymptomAndDisease.csv";
    private HashMap<Disease, Set<Symptom>> matcher;


    public Diagnosis() {
        matcher = this.readDataFromCsvFile();
        requireNonNull(matcher);
    }

    /**
     * Checks if a disease is already contained in the database.
     *
     * @param disease disease for checking.
     * @return a boolean value for the check.
     */
    public boolean hasDisease(Disease disease) {
        requireNonNull(disease);
        Set<Disease> diseases = this.matcher.keySet();
        return diseases.contains(disease);
    }

    /**
     * Gets all related symptoms of a disease.
     *
     * @param disease disease input.
     * @return an array of all symptoms.
     */
    public List<Symptom> getSymptoms(Disease disease) {
        requireNonNull(disease);
        Set<Symptom> symptoms = matcher.get(disease);
        List<Symptom> symptomList = new ArrayList<>();
        symptomList.addAll(symptoms);
        symptomList.sort(Comparator.comparing(Symptom::toString));
        return symptomList;
    }

    /**
     * Gets all diseases stored in patient book.
     *
     * @return an array all diseases.
     */
    public List<Disease> getDiseases() {
        Set<Disease> diseases = matcher.keySet();
        List<Disease> diseasesList = new ArrayList<>();
        diseasesList.addAll(diseases);
        diseasesList.sort(Comparator.comparing(Disease::toString));
        return diseasesList;
    }

    /**
     * Adds a disease and its related symptoms into the database
     *
     * @param disease  disease input.
     * @param symptoms related symptoms.
     */


    public void addMatcher(Disease disease, Set<Symptom> symptoms) {
        requireNonNull(disease);
        requireAllNonNull(symptoms);
        boolean hasDisease = this.hasDisease(disease);
        if (!hasDisease) {
            this.matcher = writeDataFromCsvFile(disease, symptoms);
        }
    }

    /**
     * Predicts a disease with a given set of symptoms.
     *
     * @param symptoms symptoms input.
     * @return a list of qualified diseases.
     */
    public List<Disease> predictDisease(Set<Symptom> symptoms) {
        requireAllNonNull(symptoms);
        List<Disease> diseases = this.matcher.keySet().stream()
                .filter(disease -> this.matcher.get(disease).containsAll(symptoms)).collect(Collectors.toList());
        //Disease[] diseaseArray = new Disease[diseases.size()];
        diseases.sort(Comparator.comparing(Disease::toString));
        return diseases;
    }

    /**
     * Gets the data from CSV storage file.
     *
     * @return a HashMap object which its key is the disease and value is its related symptoms.
     */

    private static HashMap<Disease, Set<Symptom>> readDataFromCsvFile() {

        try {
            HashMap<Disease, Set<Symptom>> diseaseSymptomMatcher = new HashMap<>();

            if (!FileUtil.isFileExists(Paths.get(pathStringForCSV))) {
                FileUtil.createFile(Paths.get(pathStringForCSV));
                InputStream inputStream = MainApp.class
                        .getResourceAsStream("/storage/datasetForSymptomAndDisease.csv");
                FileUtil.writeToCsvFile(Paths.get(pathStringForCSV),
                        Diagnosis.convertStreamToString(inputStream));
            }

            List<String> strings = FileUtil.readFromCsvFile(Paths.get(pathStringForCSV));
            for (int i = 0; i < strings.size(); i++) {
                String[] nextRecord = strings.get(i).split(",");
                Disease disease = new Disease(nextRecord[0].toLowerCase());
                nextRecord = ArrayUtils.remove(nextRecord, 0);
                List<String> symptomsList = Arrays.asList(nextRecord);
                List<Symptom> symptoms = symptomsList.stream().map(x -> new Symptom(x.toLowerCase()))
                        .collect(Collectors.toList());
                HashSet<Symptom> symptoms1 = new HashSet<>();
                symptoms1.addAll(symptoms);
                diseaseSymptomMatcher.put(disease, symptoms1);
            }
            return diseaseSymptomMatcher;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Converts a stream to a string.
     * @param is input stream.
     * @return the formatted string.
     */
    private static String convertStreamToString(InputStream is) {
        java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }

    /**
     * Writes new data to CSV storage file.
     *
     * @return a new ashMap object which its key is the disease and value is its related symptoms.
     */
    private static HashMap<Disease, Set<Symptom>> writeDataFromCsvFile(Disease disease, Set<Symptom> symptoms) {

        try {
            String data = Diagnosis.stringConverter(disease.toString(), symptoms);
            Path path = Paths.get(pathStringForCSV);
            FileUtil.writeToCsvFile(path, data);

            return Diagnosis.readDataFromCsvFile();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Formats the string with disease and related symptoms so that it can be written to CSV file.
     *
     * @param diseaseString
     * @param symptoms
     * @return
     */
    private static String stringConverter(String diseaseString, Set<Symptom> symptoms) {
        diseaseString = diseaseString.concat(",");
        for (Symptom symptom : symptoms) {
            diseaseString = diseaseString.concat(symptom.toString());
            diseaseString = diseaseString.concat(",");
        }
        diseaseString = diseaseString.substring(0, diseaseString.length() - 1);
        diseaseString = diseaseString.concat("\n");
        return diseaseString;
    }
}
