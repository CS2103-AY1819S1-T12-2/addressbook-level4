package seedu.address.model;

import seedu.address.commons.util.DrugCSVUtil;

import java.io.IOException;
import java.util.ArrayList;

/**
 * For returning pharmacological information about drugs whose names match a certain keyword.
 */

public class DrugSearch {

    //cached results of most recent keyword search
    private static ArrayList<String[]> resultsCache = new ArrayList<>();

    /**
     * keywords too generic to search for, as they will match tens of drugs
     */
    private static String[] tooGeneric = {
        "capsule", "cream", "emulsi", "gel", "hydrochloride",
        "injection", "lotion", "ointment", "paste", "patch", "pill", "powder",
        "solution", "supposit", "syrup", "tablet"
    };

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
            if (i.toLowerCase().contains(keyword.toLowerCase())) {
                return "Your entered keyword " + keyword + " is too generic, and will lead to too many results."
                        + "Try a longer keyword, or a more specific one.";
            }
        }

        try {
            DrugCSVUtil database = new DrugCSVUtil(keyword);
            String[] nextMatchingEntry;
            while ((nextMatchingEntry = database.nextMatchingEntry()) != null) {
                resultsCache.add(nextMatchingEntry);
            }
        }
        catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        for (int i = 0; i < resultsCache.size(); i++) {
            String[] currentRecord = resultsCache.get(i);
            results = results.concat("\nName: " + currentRecord[1]);
            results = results.concat("\nActive Ingredient(s): "
                    + currentRecord[10].replace("&&", ", "));
            results = results.concat("\nClassification: " + currentRecord[4]);
            results = results.concat("\n(for more information, enter \"moreinfo " + (i + 1) + "\"");
            results = results.concat("\n\n");
        }

        return results;
    }

    /**
     * Allows user to read more information about any particular drug that was returned as a search result.
     * @param index Specifies the drug about which the user wishes to know more.
     * @return A String containing full pharmacological data about the specified drug.
     */
    public static String moreInfo(int index) {
        String results = "";
        if (resultsCache.isEmpty()) {
            return "Please carry out a search using \"drug [drugname]\" first.";
        }

        index--;
        String[] record = resultsCache.get(index);
        results = results.concat("\nName: " + record[1]);
        results = results.concat("\nActive Ingredient(s): " + record[10].replace("&&", ", "));
        results = results.concat("\nStrengths Available: " + record[11].replace("&&", ", "));
        results = results.concat("\nDosage Form: " + record[6]);
        results = results.concat("\nAdministration : " + record[7].replace("&&", ", "));
        results = results.concat("\nClassification: " + record[4]);
        results = results.concat("\nLicense Holder: " + record[2]);
        results = results.concat("\nATC Code: " + record[5]);
        results = results.concat("\n\n");

        return results;
    }
}
