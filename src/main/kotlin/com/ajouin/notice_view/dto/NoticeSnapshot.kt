package com.ajouin.notice_view.dto

import java.time.LocalDateTime
import java.util.*

data class NoticeSnapshot(
    val isTopFixed: Boolean,
    val createdAt: LocalDateTime,
    val fetchId: Long,
    val id: Long,
    val title: String,
    val noticeType: String,
    val date: String,
    val summary: String
)
