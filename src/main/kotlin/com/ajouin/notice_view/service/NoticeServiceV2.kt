package com.ajouin.notice_view.service

import com.ajouin.notice_view.domain.Notice
import com.ajouin.notice_view.dto.NoticeSnapshot
import com.ajouin.notice_view.dto.SpecificNoticeResponse
import com.ajouin.notice_view.logger
import com.ajouin.notice_view.repository.NoticeRepository
import org.springframework.context.annotation.Primary
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
            // 상단 고정 공지 처리: 해당 타입의 데이터를 먼저 조회
            val topFixedNotices: List<Notice> = if (includeTopFixed) {
                // 1단계: is_top_fixed가 true인 공지사항들을 먼저 조회
                val initialTopFixedQuery = Query().apply {
                    addCriteria(Criteria.where("after.noticeType").`is`(type))
                    addCriteria(Criteria.where("after.is_top_fixed").`is`(true))
                    with(Sort.by(Sort.Direction.DESC, "after.fetchId"))
                }
                mongoTemplate.find(initialTopFixedQuery, Notice::class.java)
            } else {
                emptyList()
            }

            // 2단계: 각 공지사항의 현재 상태 확인
            val ids = topFixedNotices.mapNotNull { it.after?.id }.distinct()
            val verifiedTopFixedNotices = ids.mapNotNull { id ->
                // 각 ID에 대해 최신 데이터 조회
                val latestNoticeQuery = Query().apply {
                    addCriteria(Criteria.where("after.id").`is`(id))
                    with(Sort.by(Sort.Direction.DESC, "tsMs"))
                    limit(1)
                }
                val latestNotice = mongoTemplate.findOne(latestNoticeQuery, Notice::class.java)
                logger.info { "${latestNotice?.after?.id}: ${latestNotice?.after?.title}, ${latestNotice?.tsMs}" }
                // 최신 데이터가 still top-fixed인 경우만 포함
                if (latestNotice?.after?.isTopFixed == true) latestNotice else null
            }

            // 변환하여 결과에 추가
            noticeSnapshots.addAll(
                verifiedTopFixedNotices.mapNotNull { notice ->
                    notice.after?.let { after ->
                        NoticeSnapshot(
                            isTopFixed = after.isTopFixed,
                            createdAt = LocalDateTime.ofInstant(
                                Instant.ofEpochMilli(after.createdAt / 1000),
                                ZoneId.systemDefault()
                            ),
                            fetchId = after.fetchId,
                            id = after.id,
                            title = after.title,
                            noticeType = after.noticeType,
                            date = convertDateToFormattedString(after.date),
                            summary = after.summary
                        )
                    }
                }
            )

            // 일반 공지 처리: 상단 고정이 아닌 공지에 대해 페이징 처리하여 조회
            val generalNoticesQuery = Query().apply {
                addCriteria(Criteria.where("after.noticeType").`is`(type))
                addCriteria(Criteria.where("after.is_top_fixed").`is`(false))
                with(Sort.by(Sort.Direction.DESC, "after.id"))
                skip(offset.toLong())
                limit(limit)
            }
            val generalNoticesList = mongoTemplate.find(generalNoticesQuery, Notice::class.java)

            // 일반 공지를 NoticeSnapshot으로 변환 후 결과에 추가
            noticeSnapshots.addAll(
                generalNoticesList.mapNotNull { notice ->
                    notice.after?.let { after ->
                        NoticeSnapshot(
                            isTopFixed = after.isTopFixed,
                            createdAt = LocalDateTime.ofInstant(
                                Instant.ofEpochMilli(after.createdAt / 1000),
                                ZoneId.systemDefault()
                            ),
                            fetchId = after.fetchId,
                            id = after.id,
                            title = after.title,
                            noticeType = after.noticeType,
                            date = convertDateToFormattedString(after.date),
                            summary = after.summary
                        )
                    }
                }
            )
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
