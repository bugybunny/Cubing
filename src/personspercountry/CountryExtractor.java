package personspercountry;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class CountryExtractor {
    public static final Path COUNTRIES_WCA_FILE = Paths.get("countries_seclection_wca.org.txt");
    public static final Path ALL_COUNTRIES_POPULATION = Paths.get("countries.csv");
    public static final String WCA_COUNTRY_URL = "https://www.worldcubeassociation.org/results/events.php";

    private final Set<String> countries = new HashSet<>();

    public CountryExtractor() throws IOException {
        extractAvailableCountriesFromWCA();
    }

    private void extractAvailableCountriesFromWCA() throws IOException {
        Document doc = Jsoup.connect(WCA_COUNTRY_URL).get();
        Elements selects = doc.select("#regionId");

        if (selects.size() == 1) {
            Element countrySelect = selects.get(0);
            for (Element option : countrySelect.children()) {
                String countryName = option.text().trim();
                if (!countryName.isEmpty()) {
                    countries.add(countryName);
                }
            }
        }
    }

    public Set<String> getCountries() {
        return countries;
    }

    public static void main(String[] args) throws IOException {
        System.out.println(new CountryExtractor().getCountries().size());
    }

}
