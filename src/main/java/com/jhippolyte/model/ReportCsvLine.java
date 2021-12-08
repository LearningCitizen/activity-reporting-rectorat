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
    public static final String[] HEADERS = {"Dév.", "Carte Jira", "Tâche", "Sous-tâche", "OBM_C", "OBM_M", "ACV_C", "ACV_M", "FLX_C", "FLM_M", "DOC_C", "DOC_M", "DAO_C", "DAO_M", "PGM_C", "PGM_M", "INT_C", "INT_M", "REQ_C", "REQ_M", "Projet", "Branche", "Livrable"};
}
