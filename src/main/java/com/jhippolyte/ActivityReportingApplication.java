package com.jhippolyte;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import static com.jhippolyte.ActivityReportingConstantes.DIAPASON_GITLAB_GROUP;
import static com.jhippolyte.ActivityReportingConstantes.DIAPASON_GITLAB_URL;

@SpringBootApplication
public class ActivityReportingApplication {

    Logger LOGGER = LoggerFactory.getLogger(ActivityReportingApplication.class);
    private String gitlabUser = "hjohnson";
    private String privateToken = "";
    private String gitlabGroup = DIAPASON_GITLAB_GROUP;
    private String gitLabUrl = ActivityReportingConstantes.DIAPASON_GITLAB_URL;

    @Autowired
    ActivityReportingService activityReportingService;

    public static void main(String[] args) {
        SpringApplication.run(ActivityReportingApplication.class);
    }

    @Bean
    CommandLineRunner run (){
        return (args) -> {
            LOGGER.info("Activity Reporting started");
            activityReportingService.getAllMergeRequestsByUserInGroup(gitlabUser, privateToken, gitlabGroup, gitLabUrl);
        };
    }

    //public List<String> displayInformationsFromMR();

}
