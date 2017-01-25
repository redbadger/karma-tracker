package scraper;

import com.google.auth.Credentials;
import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.cloud.bigquery.BigQuery;
import com.google.cloud.bigquery.BigQueryOptions;

import java.io.IOException;

public class BigQueryBuilder {
    public static BigQuery build(String projectId, String clientId, String clientEmail, String privateKeyPkcs8, String privateKeyId) {
        return BigQueryOptions.newBuilder()
                .setCredentials(buildCredentials(clientId, clientEmail, privateKeyPkcs8, privateKeyId))
                .setProjectId(projectId)
                .build()
                .getService();
    }

    private static Credentials buildCredentials(String clientId, String clientEmail, String privateKeyPkcs8, String privateKeyId) {
        try {
            return ServiceAccountCredentials.fromPkcs8(clientId, clientEmail, privateKeyPkcs8, privateKeyId, null);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }
}
