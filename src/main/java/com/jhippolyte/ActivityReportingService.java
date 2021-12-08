package com.jhippolyte;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jhippolyte.model.MergeRequestData;
import com.jhippolyte.model.ReportCsvLine;
import com.jhippolyte.params.ActivityReportingParams;
import com.jhippolyte.params.MergeRequestChangesParams;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.BufferedWriter;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
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

    private static final String DEFAULT_ACTIVITY_REPORT = "./activity-report.csv";
    Logger logger = LoggerFactory.getLogger(ActivityReportingService.class);
    HttpClient httpClient = HttpClient.newHttpClient();
    ObjectMapper mapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    public void generateReporting(ActivityReportingParams params) {
        String jsonMergedMr = getAllMergeRequestsByUserInGroup(params.getGitlabUser(), params.getPrivateToken(), params.getGitlabGroup(), params.getGitLabUrl(), params.getStartDate());
        List<MergeRequestChangesParams> mrParams = parsetoMergeRequestChangesParam(jsonMergedMr);
        List<MergeRequestData> mrDatas = mrParams.stream().map(mrParam -> getAllMergeRequestsChanges(params.getGitlabUser(), params.getPrivateToken(), params.getGitLabUrl(), mrParam))
                .map(jsonResponse -> parsetoMergeRequestData(jsonResponse)).filter(mrDdataOpt -> mrDdataOpt.isPresent())
                .map(mrDdataOpt -> mrDdataOpt.get()).collect(Collectors.toList());
        mrDatas.forEach(result -> logger.info("result :" + result));
        generateCsvReport(mrDatas, params.getDev());
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

    public List<MergeRequestChangesParams> parsetoMergeRequestChangesParam(String jsonMergeRequestsList) {
        List<MergeRequestChangesParams> result = new ArrayList<MergeRequestChangesParams>();
        try {
            result.addAll(mapper.readValue(jsonMergeRequestsList, new TypeReference<List<MergeRequestChangesParams>>() {
            }));
        } catch (JsonProcessingException e) {
            logger.error(e.getMessage());
        }
        return result;
    }

    public String getAllMergeRequestsChanges(String user, String privateToken, String gitLabUrl, MergeRequestChangesParams mrChangesParam) {
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

    public Optional<MergeRequestData> parsetoMergeRequestData(String jsonMergeChangesList) {
        Optional<MergeRequestData> result = Optional.empty();
        try {
            result = Optional.of(mapper.readValue(jsonMergeChangesList, MergeRequestData.class));
        } catch (JsonProcessingException e) {
            logger.error(e.getMessage());
        }
        return result;
    }

    public void generateCsvReport(List<MergeRequestData> mrDta, String dev) {
        logger.info("Generating csv report for dev : "+dev);
        try (
                BufferedWriter writer = Files.newBufferedWriter(Paths.get(DEFAULT_ACTIVITY_REPORT));
                CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT.withHeader(ReportCsvLine.HEADERS))
        ) {
            csvPrinter.printRecord("1", "Sundar Pichai ♥", "CEO", "Google");
            csvPrinter.printRecord("2", "Satya Nadella", "CEO", "Microsoft");
            csvPrinter.printRecord("3", "Tim cook", "CEO", "Apple");

            csvPrinter.flush();
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }
}
