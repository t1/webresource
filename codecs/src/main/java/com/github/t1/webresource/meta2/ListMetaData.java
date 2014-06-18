package com.github.t1.webresource.meta2;

import java.util.List;

import lombok.*;
import lombok.experimental.Accessors;

@Getter
@AllArgsConstructor
@Accessors(fluent = true)
public class ListMetaData implements MetaData<List<?>> {
    private final String title;
}
