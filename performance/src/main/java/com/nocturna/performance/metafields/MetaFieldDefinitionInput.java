package com.nocturna.performance.metafields;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MetaFieldDefinitionInput {
    private String name;
    private String namespace;
    private String key;
    private String description;
    private String type;
    private String ownerType; // "PRODUCT"
}
