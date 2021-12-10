package com.jhippolyte;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jhippolyte.model.ChangeData;
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
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
    private static List<String> filesToIgnore = Arrays.asList("(.*)changelog.md(.*)", "(.*)generated/api(.*)", "(.*)gitignore(.*)");
    Logger logger = LoggerFactory.getLogger(ActivityReportingService.class);
    HttpClient httpClient = HttpClient.newHttpClient();
    ObjectMapper mapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    public void generateReporting(ActivityReportingParams params) {
        String jsonMergedMr = getAllMergeRequestsByUserInGroup(params.getGitlabUser(), params.getPrivateToken(), params.getGitlabGroup(), params.getGitLabUrl(), params.getStartDate());
        List<MergeRequestChangesParams> mrParams = parsetoMergeRequestChangesParam(jsonMergedMr);
        List<MergeRequestData> mrDatas = mrParams.stream().map(mrParam -> getAllMergeRequestsChanges(params.getGitlabUser(), params.getPrivateToken(), params.getGitLabUrl(), mrParam))
                .map(jsonResponse -> parsetoMergeRequestData(jsonResponse)).filter(mrDdataOpt -> mrDdataOpt.isPresent())
                .map(mrDdataOpt -> mrDdataOpt.get()).collect(Collectors.toList());
        List<ReportCsvLine> reportCsvLines = convertToReportCsvLines(mrDatas);
        reportCsvLines.forEach(result -> logger.info("cvLine :" + result));
        generateCsvReport(reportCsvLines, params.getDev());
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

    public List<ReportCsvLine> convertToReportCsvLines(List<MergeRequestData> mrDatas) {
        return mrDatas.stream().map(this::convertToReportCsvLine).collect(Collectors.toList());
    }

    public ReportCsvLine convertToReportCsvLine(MergeRequestData mrData) {
        ReportCsvLine result = new ReportCsvLine();
        result.setCarteJira(mrData.getSource_branch());
        result.setTache(mrData.getTitle());
        result.setSousTache("");
        result.setProjet(getProjetFromMrUrl(mrData.getWeb_url()));
        result.setBranche(mrData.getMerge_commit_sha());
        result.setProjet(getProjetFromMrUrl(mrData.getWeb_url()));
        result.setAcv_c("");
        result.setAcv_m("");
        result.setFlx_c("");
        result.setFlx_m("");
        result.setObm_c("");
        result.setObm_m("");
        result.setReq_c("");
        result.setReq_m("");
        result.setDoc_c("");
        result.setDoc_m("");
        processChanges(result, mrData.getChanges());
        return result;
    }

    public void processChanges(ReportCsvLine reportCsvLine, List<ChangeData> changesData) {
        final String DAO = "dao", PGM = "pgm", INT = "int";
        final boolean CREATION = true, MODIFICATION = false;
        Map<String, Integer> creationCounter = new HashMap<>(Map.of(DAO, 0, PGM, 0, INT, 0));
        Map<String, Integer> modificationCounter = new HashMap<>(Map.of(DAO, 0, PGM, 0, INT, 0));
        Map<Boolean, Map<String, Integer>> mapToUpdate = new HashMap<>(Map.of(CREATION, creationCounter, MODIFICATION, modificationCounter));
        String livrable = changesData.stream().filter(ch -> !filesToIgnore.stream().anyMatch(fileToIgnore -> ch.getNew_path().toLowerCase().matches(fileToIgnore)))
                .map(ch -> {
                    if (ch.getNew_path().toLowerCase().matches("(.*)test(.*)")) {
                        mapToUpdate.get(ch.isNew_file()).put(PGM, mapToUpdate.get(ch.isNew_file()).get(PGM) + 1);
                    } else if (ch.getNew_path().toLowerCase().matches("(.*)dao(.*)|(.*)dto(.*)|(.*)service(.*)")) {
                        mapToUpdate.get(ch.isNew_file()).put(DAO, mapToUpdate.get(ch.isNew_file()).get(DAO) + 1);
                    } else if (ch.getNew_path().toLowerCase().matches("(.*)resource(.*)java")) {
                        mapToUpdate.get(ch.isNew_file()).put(INT, mapToUpdate.get(ch.isNew_file()).get(INT) + 1);
                    } else {
                        mapToUpdate.get(ch.isNew_file()).put(PGM, mapToUpdate.get(ch.isNew_file()).get(PGM) + 1);
                    }
                    return ch.getNew_path();
                }).collect(Collectors.joining("\n"));
        reportCsvLine.setLivrable(livrable);
        reportCsvLine.setDao_c(mapToUpdate.get(CREATION).get(DAO) + "");
        reportCsvLine.setInt_c(mapToUpdate.get(CREATION).get(INT) + "");
        reportCsvLine.setPgm_c(mapToUpdate.get(CREATION).get(PGM) + "");
        reportCsvLine.setDao_m(mapToUpdate.get(MODIFICATION).get(DAO) + "");
        reportCsvLine.setInt_m(mapToUpdate.get(MODIFICATION).get(INT) + "");
        reportCsvLine.setPgm_m(mapToUpdate.get(MODIFICATION).get(PGM) + "");
    }

    public String getProjetFromMrUrl(String mrUrl) {
        String[] result = mrUrl.split("/");
        return result[result.length - 4];
    }

    public void generateCsvReport(List<ReportCsvLine> repLines, String dev) {
        logger.info("Generating csv report for dev : " + dev);
        try (
                BufferedWriter writer = Files.newBufferedWriter(Paths.get(DEFAULT_ACTIVITY_REPORT));
                CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT.withHeader(ReportCsvLine.HEADERS).withDelimiter(';'))
        ) {
            repLines.forEach(line -> {
                try {
                    csvPrinter.printRecord(dev, line.getCarteJira(), line.getTache(), line.getSousTache(), line.getObm_c(), line.getObm_m(), line.getAcv_c(), line.getAcv_m(), line.getFlx_c(), line.getFlx_m(), line.getDoc_c(), line.getDoc_m(), line.getDao_c(), line.getDao_m(), line.getPgm_c(), line.getPgm_m(), line.getInt_c(), line.getInt_m(), line.getReq_c(), line.getReq_m(), line.getProjet(), line.getBranche(), line.getLivrable());
                } catch (IOException e) {
                    logger.error(e.getMessage());
                }
            });
            csvPrinter.flush();
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }
}
