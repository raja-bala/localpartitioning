package com.springbatch.localpartitioning.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class ChildJobMeta {
    private long minValue;
    private long maxValue;
    private long partitionId;
}
