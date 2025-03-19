package com.ajouin.notice_view.service

import com.ajouin.notice_view.dto.NoticeSnapshot
import com.ajouin.notice_view.dto.SpecificNoticeResponse

interface NoticeService {

    fun findNoticesByPaging(
        offset: Int,
        limit: Int,
        types: List<String>,
        includeTopFixed: Boolean
    ): List<NoticeSnapshot>

    fun getSpecificNotice(id: Long): SpecificNoticeResponse
    fun getNoticeSnapshotById(id: Long): NoticeSnapshot



}