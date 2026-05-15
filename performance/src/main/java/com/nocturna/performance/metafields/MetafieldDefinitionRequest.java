package com.nocturna.performance.metafields;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MetafieldDefinitionRequest  {
    private String name;
    private String namespace;
    private String key;
    private String type;
    private String description;
    @JsonProperty("owner_type")
    private String ownerType; // "PRODUCT"
    private boolean pin;
}
