package com.cloudbase.common.core.domain;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
public class PageResult<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    private long total;
    private long current;
    private long size;
    private List<T> records;

    public PageResult(List<T> records, long total, long current, long size) {
        this.records = records;
        this.total = total;
        this.current = current;
        this.size = size;
    }

    public long getPages() {
        if (size == 0) {
            return 0;
        }
        return (total + size - 1) / size;
    }
}
