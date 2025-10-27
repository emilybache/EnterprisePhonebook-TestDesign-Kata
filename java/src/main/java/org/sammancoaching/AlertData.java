package org.sammancoaching;

import com.fasterxml.jackson.annotation.JsonProperty;

public record AlertData(
    @JsonProperty("size") String size,
    @JsonProperty("new_entry") String newEntry,
    @JsonProperty("clash") String clash
) {}