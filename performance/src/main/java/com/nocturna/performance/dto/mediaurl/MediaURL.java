package com.nocturna.performance.dto.mediaurl;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.nocturna.performance.dto.exportproduct.ExportProductID;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Entity
@Table(name = "media_url")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@IdClass(MediaUrlID.class)
public class MediaURL { // TODO add foreign key to product and process from holley into this table
    @Column
    private String part_number;
    @Column
    private String upc;

    private String url;
}
