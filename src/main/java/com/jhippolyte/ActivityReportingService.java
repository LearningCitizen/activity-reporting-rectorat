package com.jhippolyte;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

import static com.jhippolyte.ActivityReportingConstantes.MERGE_REQUESTS_URI;
import static com.jhippolyte.ActivityReportingConstantes.OPENED_STATE_FILTER;
import static com.jhippolyte.ActivityReportingConstantes.PRIVATE_TOKEN_HEADER;

@Service
public class ActivityReportingService {

    Logger logger = LoggerFactory.getLogger(ActivityReportingService.class);
    HttpClient httpClient = HttpClient.newHttpClient();

    public List<String> getAllMergeRequestsByUserInGroup(String user, String privateToken, String gitlabGroup, String gitLabUrl) {
        try {
            logger.info("Building the request to get merge request - user "+user+" group "+gitlabGroup+" url "+gitLabUrl);
            HttpRequest getMRrequest = HttpRequest.newBuilder()
                    .uri(new URI(gitLabUrl+MERGE_REQUESTS_URI+"?"+OPENED_STATE_FILTER) )
                    .headers(PRIVATE_TOKEN_HEADER , privateToken)
                    .GET()
                    .build();
            HttpResponse<String> response = httpClient.send(getMRrequest, HttpResponse.BodyHandlers.ofString());
            logger.info(response.body());
        } catch (URISyntaxException | IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }
}
