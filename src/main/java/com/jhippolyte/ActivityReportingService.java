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

import static com.jhippolyte.ActivityReportingConstantes.AND;
import static com.jhippolyte.ActivityReportingConstantes.AUTHOR_USERNAME_FILTER;
import static com.jhippolyte.ActivityReportingConstantes.CREATED_AFTER_FILTER;
import static com.jhippolyte.ActivityReportingConstantes.GROUP_PATH;
import static com.jhippolyte.ActivityReportingConstantes.MERGED_STATE_FILTER;
import static com.jhippolyte.ActivityReportingConstantes.MERGE_REQUESTS_URI;
import static com.jhippolyte.ActivityReportingConstantes.PARAM_OP;
import static com.jhippolyte.ActivityReportingConstantes.PRIVATE_TOKEN_HEADER;

@Service
public class ActivityReportingService {

    Logger logger = LoggerFactory.getLogger(ActivityReportingService.class);
    HttpClient httpClient = HttpClient.newHttpClient();

    /**
     * Get All the merged merge requests from author created after a date.
     * <p>
     * Example : GET https://gitlab.com/api/v4/groups/9970/merge_requests?state=merged&created_after=2021-12-01T00:00:00Z&author_username=afontaine
     *
     * @param user
     * @param privateToken
     * @param gitlabGroup
     * @param gitLabUrl
     * @param startDate
     * @return
     */
    public List<String> getAllMergeRequestsByUserInGroup(String user, String privateToken, String gitlabGroup, String gitLabUrl, String startDate) {
        try {
            logger.info("Building the request to get merge request - user " + user + " group " + gitlabGroup + " url " + gitLabUrl);
            HttpRequest getMRrequest = HttpRequest.newBuilder()
                    .uri(new URI(gitLabUrl + String.format(GROUP_PATH, gitlabGroup) + MERGE_REQUESTS_URI + PARAM_OP + MERGED_STATE_FILTER + AND + String.format(AUTHOR_USERNAME_FILTER, user) + AND + String.format(CREATED_AFTER_FILTER, startDate)))
                    .headers(PRIVATE_TOKEN_HEADER, privateToken)
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
