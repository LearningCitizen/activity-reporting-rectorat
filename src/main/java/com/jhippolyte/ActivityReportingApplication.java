package com.jhippolyte;

//import com.atlassian.jira.rest.client.internal.async.AsynchronousJiraRestClient;
import com.jhippolyte.params.ActivityReportingParams;
import com.jhippolyte.service.ActivityReportingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;

import java.net.URI;

import static com.jhippolyte.ActivityReportingConstantes.DIAPASON_GITLAB_ID;
import static com.jhippolyte.ActivityReportingConstantes.DIAPASON_GITLAB_URL_API;

@SpringBootApplication
public class ActivityReportingApplication {

    Logger LOGGER = LoggerFactory.getLogger(ActivityReportingApplication.class);
    @Autowired
    ActivityReportingService activityReportingService;
//    private String gitlabUser = "mayra-cabrera";
//    private String privateToken = "glpat-J33PHL-dgy3ma7sM9xEv";
//    private String gitlabGroup = "9970";
//    private String gitLabUrl = "https://gitlab.com/api/v4";
//    private String startDate = "2021-12-01T00:00:00Z";
    private String gitlabUser = "hjohnson";
    @Value("${gitlab.token}")
    private String privateToken;
//    private String privateToken = "zzsvCzFVPGxdJr6uo_H_";
    private String gitlabGroup = DIAPASON_GITLAB_ID;
    private String gitLabUrl = DIAPASON_GITLAB_URL_API;
    private String startDate = "2021-11-01T00:00:00Z";
    private String endDate = "2021-12-01T00:00:00Z";
    private String dev = "jh";

    public static void main(String[] args) {
        ConfigurableApplicationContext configurableApplicationContext = SpringApplication.run(ActivityReportingApplication.class);
        configurableApplicationContext.close();
    }

    @Bean
    @Profile("!test")
    CommandLineRunner run() {
        return (args) -> {
            LOGGER.info("Activity Reporting started");
            ActivityReportingParams activityReportingParams = ActivityReportingParams.builder().gitlabGroup(gitlabGroup).gitLabUrl(gitLabUrl)
                    .dev(dev).privateToken(privateToken).gitlabUser(gitlabUser).startDate(startDate).endDate(endDate).build();
            activityReportingService.generateReporting(activityReportingParams);
            LOGGER.info("Activity Reporting started");
        };
    }

    //public List<String> displayInformationsFromMR();

}
