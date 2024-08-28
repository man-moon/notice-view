package com.ajouin.notice_view.dto

data class SpecificNoticeResponse(
    val isTopFixed: Boolean,
    val createdAt: Long,
    val fetchId: Long,
    val id: Long,
    val originalUrl: String,
    val title: String,
    val html: String,
    val content: String,
    val date: String,
    val noticeType: String,
    val summary: String,
)
