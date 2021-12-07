package com.jhippolyte;

import com.jhippolyte.model.MergeRequestChangesParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;

import static com.jhippolyte.ActivityReportingConstantes.DIAPASON_GITLAB_ID;
import static com.jhippolyte.ActivityReportingConstantes.DIAPASON_GITLAB_URL_API;

@SpringBootApplication
public class ActivityReportingApplication {

    Logger LOGGER = LoggerFactory.getLogger(ActivityReportingApplication.class);
    @Autowired
    ActivityReportingService activityReportingService;
//    private String gitlabUser = "aUser";
//    private String privateToken = "myPrivateToken";
//    private String gitlabGroup = "9970";
//    private String gitLabUrl = "https://gitlab.com/api/v4";
//    private String startDate = "2021-12-01T00:00:00Z";
    private String gitlabUser = "aUser";
    private String privateToken = "myPrivateToken";
    private String gitlabGroup = DIAPASON_GITLAB_ID;
    private String gitLabUrl = DIAPASON_GITLAB_URL_API;
    private String startDate = "2021-12-01T00:00:00Z";

    public static void main(String[] args) {
        ConfigurableApplicationContext configurableApplicationContext = SpringApplication.run(ActivityReportingApplication.class);
        configurableApplicationContext.close();
    }

    @Bean
    CommandLineRunner run() {
        return (args) -> {
            LOGGER.info("Activity Reporting started");
            activityReportingService.generateReporting(gitlabUser, privateToken, gitlabGroup, gitLabUrl, startDate);
            LOGGER.info("Activity Reporting started");
        };
    }

    //public List<String> displayInformationsFromMR();

}
