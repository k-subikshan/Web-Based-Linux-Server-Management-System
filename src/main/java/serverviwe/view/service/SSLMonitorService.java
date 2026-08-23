package serverviwe.view.service;


import org.springframework.stereotype.Service;

import javax.net.ssl.HttpsURLConnection;
import java.net.URL;
import java.security.cert.X509Certificate;
import java.util.Date;

@Service
public class SSLMonitorService {

    public long getDaysRemaining(
            String website) throws Exception {

        @SuppressWarnings("deprecation")
        URL url =
                new URL(website);

        HttpsURLConnection connection =
                (HttpsURLConnection)
                        url.openConnection();

        connection.connect();

        X509Certificate certificate =
                (X509Certificate)
                        connection
                                .getServerCertificates()[0];

        Date expiry =
                certificate.getNotAfter();

        long difference =
                expiry.getTime()
                - System.currentTimeMillis();

        connection.disconnect();

        return difference /
                (1000 * 60 * 60 * 24);
    }
}