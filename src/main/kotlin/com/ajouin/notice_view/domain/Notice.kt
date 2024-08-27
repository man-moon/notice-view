package com.ajouin.notice_view.domain

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.Field
import java.time.LocalDateTime
import java.util.*

@Document(collection = "notice")
data class Notice(
    @Id val id: String? = null,
    val after: After? = null
) {
    data class After(
        @Field("is_top_fixed")
        val isTopFixed: Boolean,

        @Field("created_at")
        val createdAt: Long,

        @Field("fetch_id")
        val fetchId: Long,

        @Field("id")
        val id: Long,

        @Field("original_url")
        val originalUrl: String,

        val title: String,
        val html: String,

        @Field("content")
        val content: String,

        @Field("date")
        val date: LocalDateTime,

        @Field("notice_type")
        val noticeType: String,

        val summary: String
    )
}
