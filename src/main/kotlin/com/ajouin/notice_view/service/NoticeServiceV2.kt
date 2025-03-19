package com.ajouin.notice_view.service

import com.ajouin.notice_view.domain.Notice
import com.ajouin.notice_view.dto.NoticeSnapshot
import com.ajouin.notice_view.dto.SpecificNoticeResponse
import com.ajouin.notice_view.repository.NoticeRepository
import org.springframework.context.annotation.Primary
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
@Primary
class NoticeServiceV2(
    private val noticeRepository: NoticeRepository,
    private val mongoTemplate: MongoTemplate,
) : NoticeService {

    override fun findNoticesByPaging(
        offset: Int,
        limit: Int,
        types: List<String>,
        includeTopFixed: Boolean
    ): List<NoticeSnapshot> {

        val noticeSnapshots = mutableListOf<NoticeSnapshot>()

        types.forEach { type ->
            // 각 타입별로 상단 고정 공지를 가져옴
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

            // 각 타입별로 상단 고정이 아닌 공지를 페이징 처리하여 최대 20개까지 가져옴
            val generalNoticesQuery = Query().apply {
                addCriteria(Criteria.where("after.noticeType").`is`(type))
                addCriteria(Criteria.where("after.is_top_fixed").`is`(false))
                with(Sort.by(Sort.Direction.DESC, "after.id"))
                skip(offset.toLong())
                limit(limit)
            }
            val generalNoticesList = mongoTemplate.find(generalNoticesQuery, Notice::class.java)

            val combinedNotices = (topFixedNotices + generalNoticesList)
                .filter { it.after != null }
                .groupBy { it.after!!.id }
                .map { (_, notices) ->
                    notices.maxByOrNull { it.tsMs!! }
                }

            noticeSnapshots.addAll(combinedNotices.mapNotNull { notice ->
                notice?.after?.let {
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
                        date = convertDateToFormattedString(it.date),
                        summary = it.summary
                    )
                }
            })
        }

        return noticeSnapshots
    }

    override fun getSpecificNotice(id: Long): SpecificNoticeResponse {
        val notice = noticeRepository.findByAfterId(id)
        return SpecificNoticeResponse(
            isTopFixed = notice.after!!.isTopFixed,
            createdAt = notice.after.createdAt,
            fetchId = notice.after.fetchId,
            id = notice.after.id,
            title = notice.after.title,
            noticeType = notice.after.noticeType,
            date = convertDateToFormattedString(notice.after.date),
            summary = notice.after.summary,
            content = notice.after.content,
            html = notice.after.html,
            originalUrl = notice.after.originalUrl
        )
    }

    override fun getNoticeSnapshotById(id: Long): NoticeSnapshot {
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
            date = convertDateToFormattedString(notice.after.date),
            summary = notice.after.summary
        )
    }

    private fun convertDateToFormattedString(date: Date): String {
        val millis = date.time.div(1000)
        val localDateTime = Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault()).toLocalDateTime()
        return localDateTime.toLocalDate().toString()
    }

}