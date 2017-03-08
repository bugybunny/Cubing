package personspercountry;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Set;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

/**
 * Does not work for countries where further results are loaded via ajax while
 * scrolling. wca.org seems to load around 3600 results per request. So all
 * countries with more competitiors are wrong. I corrected them by hand (India,
 * China and USA).
 * 
 * Would be way easier to just download the SQL file query the database, but
 * this was easier to setup for me.
 */
public class WCACountryStatisticsRunner {
    private static final String WCA_ALLPERSONS_SINGLE_COUNTRY_URL = "https://www.worldcubeassociation.org/results/events.php?eventId=333&years=&show=All%2BPersons&single=Single&regionId=";

    public static void main(String[] args) throws IOException {
        File fout = new File("results.csv");
        FileOutputStream fos = new FileOutputStream(fout);

        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));

        Set<Country> allExistingCountries = CSVToCountry.getCoutries();
        Set<String> wcaCountryNames = new CountryExtractor().getCountries();

        // write header for csv
        bw.write("Country;Population;Continent;Number of people with official 3x3 2H single;% cubers in this country");

        for (Country country : allExistingCountries) {
            if (wcaCountryNames.contains(country.name)) {
                int noCompetitiors = getNumberOfWcaCompetitors(country);
                country.setNoCubers(noCompetitiors);
            }
            bw.write(country.toString());
            bw.newLine();
        }

        bw.close();
    }

    private static int getNumberOfWcaCompetitors(Country country) throws IOException {
        Document doc = Jsoup.connect(WCA_ALLPERSONS_SINGLE_COUNTRY_URL + country.name).get();
        return doc.select("tbody").get(0).children().size() - 4;
    }
}
