package com.ajouin.notice_view

import org.springframework.stereotype.Service

@Service
class NoticeService(
    private val noticeRepository: NoticeRepository,

) {

    fun getNotices() {

    }
}