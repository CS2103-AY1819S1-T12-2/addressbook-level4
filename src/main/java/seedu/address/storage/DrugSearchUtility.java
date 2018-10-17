package seedu.address.storage;

import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;


/**
 * For searching through "src/main/resources/storage/datasetForDrugs.csv"
 * for drugs whose names contain a certain keyword, then
 * returning pharmacological information about those drugs.
 */

public class DrugSearchUtility {
    private static final String DATASET_PATH = "./src/main/resources/storage/datasetForDrugs.csv"; //filepath to drug dataset

    private static ArrayList<String[]> resultsCache = new ArrayList<>(); //cached results of most recent keyword search

    private static String[] tooGeneric = {"capsule","cream","emulsi","gel","hydrochloride",
            "injection","lotion","ointment","paste","patch","pill","powder",
            "solution","supposit","syrup","tablet"};  //keywords too generic to search for, as they will match tens of drugs

    /*
     * For writing to error log.
     */

    private static FileWriter f = setF();
    private static PrintWriter errorLog = new PrintWriter(new BufferedWriter(f));

    /**
     * Find in "dataSetForDrugs.csv" all drugs whose names
     * contain the given keyword.
     *
     * @return a formatted String containing the list (and
     * pharmacological data) of all matching drugs.
     */
    public static String find(String keyword) {
        String results = "";
        resultsCache.clear(); //clearing cached results from previous search

        for (String i: tooGeneric) {
            if(i.toLowerCase().contains(keyword.toLowerCase())) {
                return "Your entered keyword " + keyword + " is too generic, and will lead to too many results. Try a longer keyword," +
                        " or a more specific one.";
            }
        }

        try {
            Reader reader = Files.newBufferedReader(Paths.get(DATASET_PATH));
            CSVReader csvReader = new CSVReaderBuilder(reader).withSkipLines(1).build();

           String nextRecord[];
           while ((nextRecord = csvReader.readNext()) != null) {
               if(nextRecord[1].toLowerCase().contains(keyword.toLowerCase())) {
                   resultsCache.add(nextRecord);
               }
           }

           for(int i = 0; i < resultsCache.size(); i++) {
               String[] currentRecord = resultsCache.get(i);
               results = results.concat("\nName: "+currentRecord[1]);
               results = results.concat("\nActive Ingredient(s): "+currentRecord[10].replace("&&",", "));
               results = results.concat("\nClassification: "+currentRecord[4]);
               results = results.concat("\n(for more information, enter \"moreinfo "+(i+1)+"\"");
               results = results.concat("\n\n");
           }

           return results;
        }

        catch(Exception e) {
            e.printStackTrace();
            StringWriter s = new StringWriter();
            PrintWriter p = new PrintWriter(s);
            e.printStackTrace(p);
            errorLog.print("--------------------\n"+p.toString()+"\n--------------------\n");

            return "There was an error. Please try again with a different keyword.";
        }
    }

    /**
     * Allows user to read more information about any particular drug that was returned as a search result.
     * @param index Specifies the drug about which the user wishes to know more.
     * @return A String containing full pharmacological data about the specified drug.
     */
    public static String readMore(int index) {
        String results = "";
        if(resultsCache.isEmpty()) {
            return "Please carry out a search using \"drug [drugname]\" first.";
        }

        index--;
        String record[] = resultsCache.get(index);
        results = results.concat("\nName: "+record[1]);
        results = results.concat("\nActive Ingredient(s): "+record[10].replace("&&",", "));
        results = results.concat("\nStrengths Available: "+record[11].replace("&&", ", "));
        results = results.concat("\nDosage Form: "+record[6]);
        results = results.concat("\nAdministration : "+record[7].replace("&&", ", "));
        results = results.concat("\nClassification: "+record[4]);
        results = results.concat("\nLicense Holder: "+record[2]);
        results = results.concat("\nATC Code: "+record[5]);
        results = results.concat("\n\n");

        return results;
    }


    /**
     * Setter for the FileWriter object that creates the error log file.
     * Including a setter allows try/catch blocks to be used for exception handling.
     */
    private static FileWriter setF() {
        try {
            return new FileWriter("./src/main/resources/storage/drugSearchErrorLog.txt", true);
        } catch (Exception e) {
            System.out.println("Fatal error: Log file could not be created.");
            System.exit(-1);
            return null;
        }
    }
}
