package com.cloudbase.common.core.query;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
public class PageQuery implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer current = 1;
    private Integer size = 10;
    private String orderBy;
    private String sortOrder = "ascending";

    public void validate() {
        if (current == null || current < 1) {
            current = 1;
        }
        if (size == null || size < 1) {
            size = 10;
        }
        if (size > 100) {
            size = 100;
        }
    }
}
