package org.sammancoaching.phonebook

import com.fasterxml.jackson.annotation.JsonProperty


@JvmRecord
data class AlertData(
    @field:JsonProperty("size") @param:JsonProperty("size") val size: String?,
    @field:JsonProperty("new_entry") @param:JsonProperty("new_entry") val newEntry: String?,
    @field:JsonProperty("clash") @param:JsonProperty("clash") val clash: String?
)