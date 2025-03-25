package com.ajouin.notice_view.sqs.event

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

data class NoticeRequestEvent @JsonCreator constructor (
    @JsonProperty("id") val id: Long,
    @JsonProperty("reminderId") val reminderId: Long,
)