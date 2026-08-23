package serverviwe.view.service;

import org.springframework.stereotype.Service;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

@Service
public class WebsiteMonitorService {

    public Map<String, Object> check(
            String website) throws Exception {

        Map<String, Object> result =
                new HashMap<>();

        long start =
                System.currentTimeMillis();

        @SuppressWarnings("deprecation")
        URL url =
                new URL(website);

        HttpURLConnection connection =
                (HttpURLConnection)
                        url.openConnection();

        connection.setRequestMethod("GET");

        connection.setConnectTimeout(5000);

        connection.setReadTimeout(5000);

        int status =
                connection.getResponseCode();

        long responseTime =
                System.currentTimeMillis() - start;

        result.put(
                "status",
                status
        );

        result.put(
                "responseTime",
                responseTime
        );

        result.put(
                "online",
                status >= 200 && status < 400
        );

        connection.disconnect();

        return result;
    }
}
