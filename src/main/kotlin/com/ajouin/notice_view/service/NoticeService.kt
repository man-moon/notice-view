package com.ajouin.notice_view.service

import com.ajouin.notice_view.domain.Notice
import com.ajouin.notice_view.dto.NoticeSnapshot
import com.ajouin.notice_view.repository.NoticeRepository
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.stereotype.Service
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*

@Service
class NoticeService(
    private val noticeRepository: NoticeRepository,
    private val mongoTemplate: MongoTemplate,
) {

    fun getNotices(type: String, offset: Int, limit: Int): List<NoticeSnapshot> {

        val pageable = PageRequest.of(offset / 20, 20)

        val result = noticeRepository.findByAfterNoticeType(type, pageable)

        return listOf(
            NoticeSnapshot(
                isTopFixed = true,
                createdAt = LocalDateTime.now(),
                fetchId = 1,
                id = 1,
                title = "test",
                noticeType = "test",
                date = Date(),
                summary = "test"
            )
        )
    }

    fun findNoticesByPaging(
        offset: Int,
        limit: Int,
        types: List<String>,
        includeTopFixed: Boolean
    ): List<NoticeSnapshot> {

        val noticeSnapshots = mutableListOf<NoticeSnapshot>()

        types.forEach { type ->
            // 각 타입별로 상단 고정 공지를 가져옵니다.
            val topFixedNotices: List<Notice> = if (includeTopFixed) {
                val topFixedQuery = Query().apply {
                    addCriteria(Criteria.where("after.noticeType").`is`(type))
                    addCriteria(Criteria.where("after.is_top_fixed").`is`(true))
                    with(Sort.by(Sort.Direction.DESC, "after.fetchId"))
                }
                mongoTemplate.find(topFixedQuery, Notice::class.java)
            } else {
                emptyList()
            }

            // 각 타입별로 상단 고정이 아닌 공지를 페이징 처리하여 최대 20개까지 가져옵니다.
            val generalNoticesQuery = Query().apply {
                addCriteria(Criteria.where("after.noticeType").`is`(type))
                addCriteria(Criteria.where("after.is_top_fixed").`is`(false))
                with(Sort.by(Sort.Direction.DESC, "after.fetchId"))
                skip(offset.toLong()) // 오프셋 설정
                limit(limit) // 최대 limit개의 공지만 가져옴
            }
            val generalNoticesList = mongoTemplate.find(generalNoticesQuery, Notice::class.java)

            // 가져온 공지사항들을 NoticeSnapshot으로 변환하여 리스트에 추가합니다.
            noticeSnapshots.addAll((topFixedNotices + generalNoticesList).mapNotNull { notice ->
                notice.after?.let {
                    NoticeSnapshot(
                        isTopFixed = it.isTopFixed,
                        createdAt = LocalDateTime.ofInstant(
                            Instant.ofEpochMilli(it.createdAt / 1000),
                            ZoneId.systemDefault()
                        ),
                        fetchId = it.fetchId,
                        id = it.id,
                        title = it.title,
                        noticeType = it.noticeType,
                        date = it.date,
                        summary = it.summary
                    )
                }
            })
        }

        return noticeSnapshots
    }

    fun getSpecificNotice(id: Long): Notice {
        return noticeRepository.findByAfterId(id)
    }

    fun getNoticeSnapshotById(id: Long): NoticeSnapshot {
        val notice = noticeRepository.findByAfterId(id)
        return NoticeSnapshot(
            isTopFixed = notice.after!!.isTopFixed,
            createdAt = LocalDateTime.ofInstant(
                Instant.ofEpochMilli(notice.after.createdAt / 1000),
                ZoneId.systemDefault()
            ),
            fetchId = notice.after.fetchId,
            id = notice.after.id,
            title = notice.after.title,
            noticeType = notice.after.noticeType,
            date = notice.after.date,
            summary = notice.after.summary
        )
    }

}