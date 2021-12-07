package com.jhippolyte.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class MergeRequestData {
    private String source_branch;
    private String merge_commit_sha;
    private List<String> changes;
    private String title;

}
