package com.example.ajc254.weatherapp.data;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import org.json.JSONObject;

public class RetrieveData {

    // URL, without the city.
    private static final String OPEN_WEATHER_MAP_API =
            "http://api.openweathermap.org/data/2.5/forecast?q=%s&units=metric";

    /**
     * Performs a HTTP request, attempts to obtain forecast data.
     *
     * @param city The city to get forecast data for.
     *
     * @return The JSONObject the response has been parsed into, or null if failed.
     */
    public static JSONObject getJSON(String city) {

        // Add the city tot he URL constant and call query.
        try {
            URL url = new URL(String.format(OPEN_WEATHER_MAP_API, city));
            HttpURLConnection connection =
                    (HttpURLConnection) url.openConnection();

            // Provide API key.
            connection.addRequestProperty("x-api-key",
                    "99f32a910edc68f5fe6fc974b9cb8744");

            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(connection.getInputStream()));

            StringBuffer json = new StringBuffer(1024);
            String tmp = "";
            while ((tmp = reader.readLine()) != null)
                json.append(tmp).append("\n");
            reader.close();

            // Parse into JSON object.
            JSONObject data = new JSONObject(json.toString());

            // Evaluate whether or not the request succeeded.
            if (data.getInt("cod") != 200) {
                return null;
            }

            return data;

        } catch (Exception e) {
            System.out.println(e);
            return null;
        }
    }
}
