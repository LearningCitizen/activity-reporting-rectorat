package com.jhippolyte;

public final class ActivityReportingConstantes {
    public final static String DIAPASON_GITLAB_URL = "https://gitlab.forge.education.gouv.fr";
    public final static String DIAPASON_GITLAB_GROUP = "diapason";
    public final static String MERGE_REQUESTS_URI = "/merge_requests";
    public final static String STATE_PARAM = "state";
    public final static String OPENED_STATE = "opened";
    public final static String MERGED_STATE = "merged";
    public final static String MERGED_STATE_FILTER = STATE_PARAM + "=" + MERGED_STATE;
    public final static String PRIVATE_TOKEN_HEADER = "PRIVATE-TOKEN";
    public final static String AND = "&";
    public final static String CREATED_AFTER = "created_after";
    public final static String AUTHOR_USERNAME = "author_username";
    public final static String CREATED_AFTER_FILTER = CREATED_AFTER + "=%s";
    public final static String AUTHOR_USERNAME_FILTER = AUTHOR_USERNAME + "=%s";
    public final static String PARAM_OP = "?";
    public final static String GROUP_URI = "/groups";
    public final static String GROUP_PATH = "/groups/%s";

    private ActivityReportingConstantes() {
    }
}
