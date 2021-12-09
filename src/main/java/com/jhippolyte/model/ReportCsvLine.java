package com.jhippolyte.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class ReportCsvLine {
    public static final String[] HEADERS = {"Dev.", "Carte Jira", "Tache", "Sous-tache", "OBM_C", "OBM_M", "ACV_C", "ACV_M", "FLX_C", "FLM_M", "DOC_C", "DOC_M", "DAO_C", "DAO_M", "PGM_C", "PGM_M", "INT_C", "INT_M", "REQ_C", "REQ_M", "Projet", "Branche", "Livrable"};
    private String carteJira;
    private String tache;
    private String sousTache;
    private String obm_c;
    private String obm_m;
    private String acv_c;
    private String acv_m;
    private String flx_c;
    private String flx_m;
    private String doc_c;
    private String doc_m;
    private String dao_c;
    private String dao_m;
    private String pgm_c;
    private String pgm_m;
    private String int_c;
    private String int_m;
    private String req_c;
    private String req_m;
    private String projet;
    private String branche;
    private String livrable;
}
