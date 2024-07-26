package com.ajouin.notice_view.dto

import java.time.LocalDateTime

data class NoticeSnapshot(
    val isTopFixed: Boolean,
    val createdAt: LocalDateTime,
    val fetchId: Long,
    val id: Long,
    val title: String,
    val noticeType: String,
    val summary: String
)
