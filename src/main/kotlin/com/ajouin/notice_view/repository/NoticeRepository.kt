package com.ajouin.notice_view.repository

import com.ajouin.notice_view.domain.Notice
import org.springframework.data.domain.Pageable
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface NoticeRepository : MongoRepository<Notice, String> {
    fun findByAfterNoticeType(noticeType: String, pageable: Pageable): List<Notice>
    fun findByAfterId(id: Long): Notice
}