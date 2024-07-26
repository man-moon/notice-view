package com.ajouin.notice_view

import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = "notice")
data class Notice(
    @Id
    val id: ObjectId = ObjectId(),
    val before: Any? = null,
    val after: NoticeContent? = null,
    val source: Source? = null,
    val op: String? = null,
    val tsMs: Long? = null,
    val transaction: Any? = null,
)

data class NoticeContent(
    val isTopFixed: Boolean,
    val createdAt: Long,
    val fetchId: Long,
    val id: Long,
    val originalUrl: String,
    val title: String,
    val html: String,
    val noticeType: String,
    val summary: String
)

data class Source(
    val version: String,
    val connector: String,
    val name: String,
    val tsMs: Long,
    val snapshot: String,
    val db: String,
    val sequence: Any? = null,
    val table: String,
    val serverId: Long,
    val gtid: Any? = null,
    val file: String,
    val pos: Long,
    val row: Int,
    val thread: Any? = null,
    val query: Any? = null
)
