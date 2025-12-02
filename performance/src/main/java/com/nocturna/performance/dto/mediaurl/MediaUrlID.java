package com.nocturna.performance.dto.mediaurl;

import java.io.Serializable;
import java.util.Objects;

public class MediaUrlID implements Serializable {
    private String upc;
    private String part_number;


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MediaUrlID that = (MediaUrlID) o;
        return Objects.equals(upc, that.upc) && Objects.equals(part_number, that.part_number);
    }

    @Override
    public int hashCode() {
        return Objects.hash(upc, part_number);
    }
}

