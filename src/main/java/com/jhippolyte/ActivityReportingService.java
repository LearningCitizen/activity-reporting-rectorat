package com.jhippolyte;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jhippolyte.model.MergeRequestChangesParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.jhippolyte.ActivityReportingConstantes.AND;
import static com.jhippolyte.ActivityReportingConstantes.AUTHOR_USERNAME_FILTER;
import static com.jhippolyte.ActivityReportingConstantes.CHANGES_URI;
import static com.jhippolyte.ActivityReportingConstantes.CREATED_AFTER_FILTER;
import static com.jhippolyte.ActivityReportingConstantes.GROUP_PATH;
import static com.jhippolyte.ActivityReportingConstantes.MERGED_STATE_FILTER;
import static com.jhippolyte.ActivityReportingConstantes.MERGE_REQUESTS_URI;
import static com.jhippolyte.ActivityReportingConstantes.MERGE_REQUEST_PATH;
import static com.jhippolyte.ActivityReportingConstantes.PARAM_OP;
import static com.jhippolyte.ActivityReportingConstantes.PRIVATE_TOKEN_HEADER;
import static com.jhippolyte.ActivityReportingConstantes.PROJECT_PATH;

@Service
public class ActivityReportingService {

    Logger logger = LoggerFactory.getLogger(ActivityReportingService.class);
    HttpClient httpClient = HttpClient.newHttpClient();
    ObjectMapper mapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    public void generateReporting(String user, String privateToken, String gitlabGroup, String gitLabUrl, String startDate) {
        String jsonMergedMr = getAllMergeRequestsByUserInGroup(user, privateToken, gitlabGroup, gitLabUrl, startDate);
        List<MergeRequestChangesParam> mrParams = parsetoMergeRequestChangesParam(jsonMergedMr);
        List<String> results = mrParams.stream().map(mrParam -> getAllMergeRequestsChanges(user, privateToken, gitLabUrl, mrParam))
                .collect(Collectors.toList());
        results.forEach(result -> logger.info("result :" + result));
    }

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
    public String getAllMergeRequestsByUserInGroup(String user, String privateToken, String gitlabGroup, String gitLabUrl, String startDate) {
        try {
            logger.info("Building the request to get merge request - user " + user + " group " + gitlabGroup + " url " + gitLabUrl);
            HttpRequest getMRrequest = HttpRequest.newBuilder()
                    .uri(new URI(gitLabUrl + String.format(GROUP_PATH, gitlabGroup) + MERGE_REQUESTS_URI + PARAM_OP + MERGED_STATE_FILTER + AND + String.format(AUTHOR_USERNAME_FILTER, user) + AND + String.format(CREATED_AFTER_FILTER, startDate)))
                    .headers(PRIVATE_TOKEN_HEADER, privateToken)
                    .GET()
                    .build();
            HttpResponse<String> response = httpClient.send(getMRrequest, HttpResponse.BodyHandlers.ofString());
            logger.info(response.body());
            return response.body();
        } catch (URISyntaxException | IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<MergeRequestChangesParam> parsetoMergeRequestChangesParam(String jsonMergeRequestsList) {
        List<MergeRequestChangesParam> result = new ArrayList<MergeRequestChangesParam>();
        try {
            result.addAll(mapper.readValue(jsonMergeRequestsList, new TypeReference<List<MergeRequestChangesParam>>() {
            }));
        } catch (JsonProcessingException e) {
            logger.error(e.getMessage());
        }
        return result;
    }

    public String getAllMergeRequestsChanges(String user, String privateToken, String gitLabUrl, MergeRequestChangesParam mrChangesParam) {
        try {
            logger.info("Building the request to get merge request changes - user " + user + " mrParam " + mrChangesParam + " url " + gitLabUrl);
            HttpRequest getMRrequest = HttpRequest.newBuilder()
                    .uri(new URI(gitLabUrl + String.format(PROJECT_PATH, mrChangesParam.getProject_id()) + String.format(MERGE_REQUEST_PATH, mrChangesParam.getIid()) + CHANGES_URI))
                    .headers(PRIVATE_TOKEN_HEADER, privateToken)
                    .GET()
                    .build();
            HttpResponse<String> response = httpClient.send(getMRrequest, HttpResponse.BodyHandlers.ofString());
            logger.info(response.body());
            return response.body();
        } catch (URISyntaxException | IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }
}
