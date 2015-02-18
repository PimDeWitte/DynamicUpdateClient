package control.http;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * @author Pim de Witte(wwadewitte), Whitespell LLC
 *         11/17/14
 *         control.http
 */
public class HttpHandler {

    /**
     * Executes a HTTP Get request and return the result as a String
     *
     * @param url   the exact URL
     * @return      String with the response
     */

    public static String returnGetRequest(String url) throws Exception {

        URL obj = new URL("http://" + url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();

        con.setRequestMethod("GET");

        con.setRequestProperty("User-Agent", "duclient");

        int responseCode = con.getResponseCode();

        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            if(inputLine.length() > 0) {
                response.append(inputLine);
            }
        }
        in.close();

        return response.toString();

    }

}
