package com.jhippolyte;

public final class ActivityReportingConstantes {
    public final static String DIAPASON_GITLAB_URL = "https://gitlab.forge.education.gouv.fr";
    public final static String DIAPASON_GITLAB_GROUP = "diapason";
    public final static String MERGE_REQUESTS_URI = "/merge_requests";
    public final static String STATE_PARAM = "state";
    public final static String OPENED_STATE = "opened";
    public final static String OPENED_STATE_FILTER = STATE_PARAM+"="+OPENED_STATE;
    public final static String PRIVATE_TOKEN_HEADER = "PRIVATE-TOKEN";

    private ActivityReportingConstantes() {
    }
}
