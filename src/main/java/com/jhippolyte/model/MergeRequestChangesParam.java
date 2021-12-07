package com.jhippolyte.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class MergeRequestChangesParam {
    /**
     * Project ID
     */
    private String project_id;
    /**
     * Merge request ID
     */
    private String iid;
}
