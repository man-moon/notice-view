package com.ajouin.notice_view.dto

data class NoticeResponse(
    val notice: List<NoticeSnapshot>,
    val bookmark: List<Long> = listOf(),
)