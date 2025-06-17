import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.InputStream;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;

public class ExchangeRateFetcher {
    public static BigDecimal getRate(String from, String to, String date) throws Exception {
        String urlStr = "https://api.frankfurter.app/" + date + "?from=" + from + "&to=" + to;
        URI uri = URI.create(urlStr);
        URL url = uri.toURL();
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");

        try (InputStream is = conn.getInputStream()) {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(is);
            JsonNode rates = root.get("rates");
            if (rates == null || rates.get(to) == null) {
                throw new RuntimeException("Rate not found for " + to);
            }
            return rates.get(to).decimalValue();
        } finally {
            conn.disconnect();
        }
    }
}