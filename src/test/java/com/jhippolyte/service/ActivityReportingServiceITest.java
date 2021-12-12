package com.jhippolyte.service;

import com.jhippolyte.model.ChangeData;
import com.jhippolyte.model.MergeRequestData;
import com.jhippolyte.model.ReportCsvLine;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Profile;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Profile("test")
class ActivityReportingServiceITest {

    private static MergeRequestData mrData1 = new MergeRequestData();
    private static ReportCsvLine reportCsvLine1 = new ReportCsvLine();
    private static List<MergeRequestData> mrDatas = new ArrayList<>();
    private static List<ReportCsvLine> reportCsvLines1 = new ArrayList();
    private static String project = "myProject";
    private static String urlMergeRequest = String.format("https://gitlab.example.com/myGroup/%s/-/merge_requests/76616", project);
    @Autowired
    private ActivityReportingService activityReportingService;

    @BeforeAll
    private static void init(){
        String title = "My feature 1";
        String livrable1 = "Class1.java";
        String livrable2 = "Class2.java";
        String branche1= "jh/Ticket1";
        String branchSha1 = "sha1";
        ChangeData changeData1 = new ChangeData();
        changeData1.setNew_file(true);
        changeData1.setNew_path(livrable1);
        ChangeData changeData2 = new ChangeData();
        changeData2.setNew_file(false);
        changeData2.setNew_path(livrable2);
        mrData1.setTitle(title);
        mrData1.setWeb_url(urlMergeRequest);
        mrData1.setSource_branch(branche1);
        mrData1.setMerge_commit_sha(branchSha1);
        mrData1.setChanges(Arrays.asList(changeData1, changeData2));
        mrDatas = Arrays.asList(mrData1);
        reportCsvLine1.setProjet(project);
        reportCsvLine1.setCarteJira(branche1);
        reportCsvLine1.setTache(title);
        reportCsvLine1.setBranche(branchSha1);
        reportCsvLine1.setPgm_c("1");
        reportCsvLine1.setPgm_m("1");
        reportCsvLine1.setDao_c("0");
        reportCsvLine1.setDao_m("0");
        reportCsvLine1.setInt_c("0");
        reportCsvLine1.setInt_m("0");
        reportCsvLine1.setLivrable(livrable1+"\n"+livrable2);
        reportCsvLines1 = Arrays.asList(reportCsvLine1);
    }

    @Test
    public void should_convert_to_reportCsvLine (){
        ReportCsvLine reportCsvLineResult = activityReportingService.convertToReportCsvLine(mrData1);
        Assertions.assertThat(reportCsvLineResult).isEqualTo(reportCsvLine1);
    }

    @Test
    public void should_convert_to_reportCsvLines (){
        List<ReportCsvLine> reportCsvLinesResult = activityReportingService.convertToReportCsvLines(mrDatas);
        Assertions.assertThat(reportCsvLinesResult).isEqualTo(reportCsvLines1);
    }

    @Test
    public void should_get_project_from_url (){
        String result = activityReportingService.getProjetFromMrUrl(urlMergeRequest);
        Assertions.assertThat(result).isEqualTo(project);
    }
}