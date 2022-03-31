package com.jhippolyte.params;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Builder
public class ActivityReportingParams {
    private String gitlabUser;
    private String privateToken;
    private String gitlabGroup;
    private String gitLabUrl;
    private String startDate;
    private String endDate;
    private String dev;
    private String page;
}
