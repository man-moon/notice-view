package com.ajouin.notice_view.sqs.event

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

data class NoticeResponseEvent @JsonCreator constructor (
    @JsonProperty("isTopFixed") @get:JsonProperty("isTopFixed") val isTopFixed: Boolean,
    @JsonProperty("createdAt") val createdAt: Long,
    @JsonProperty("fetchId") val fetchId: Long,
    @JsonProperty("id") val id: Long,
    @JsonProperty("originalUrl") val originalUrl: String,
    @JsonProperty("title") val title: String,
    @JsonProperty("html") val html: String,
    @JsonProperty("content") val content: String,
    @JsonProperty("date") val date: String,
    @JsonProperty("noticeType") val noticeType: String,
    @JsonProperty("summary") val summary: String,
    @JsonProperty("reminderId") val reminderId: Long,
)