package personspercountry;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class CSVToCountry {
    public static File ALL_COUNTRIES = new File("countries.csv");
    private static boolean parsed;

    private static Set<Country> allExistingCountries;

    public static Set<Country> getCoutries() {
        parseCSV();
        return allExistingCountries;
    }

    public static void parseCSV() {
        if (!parsed) {
            allExistingCountries = new HashSet<>();

            String line = "";
            String cvsSplitBy = ";";

            try (BufferedReader br = new BufferedReader(new FileReader(ALL_COUNTRIES))) {
                while ((line = br.readLine()) != null) {
                    String[] countryLine = line.split(cvsSplitBy);
                    if (countryLine.length == 3) {
                        allExistingCountries
                                .add(new Country(countryLine[0], countryLine[1], Integer.parseInt(countryLine[2])));
                    } else {
                        System.err.println(countryLine);
                    }

                }

            } catch (IOException e) {
                parsed = false;
            }
            parsed = true;
        }
    }
}