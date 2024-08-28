package com.ajouin.notice_view

import com.ajouin.notice_view.domain.Notice
import com.ajouin.notice_view.dto.*
import com.ajouin.notice_view.service.NoticeService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/notice")
class NoticeController(
    private val noticeService: NoticeService,
) {

    @GetMapping
    fun getNotice(
        @RequestParam types: List<String>,
        @RequestParam includeTopFixed: Boolean,
        @RequestParam offset: Int,
        @RequestParam limit: Int,
    ): NoticeResponse {

        logger.info { "공지사항 목록 요청" }
        logger.info { "types: $types, includeTopFixed: $includeTopFixed, offset: $offset, limit: $limit" }

        val notice = noticeService.findNoticesByPaging(offset, limit, types, includeTopFixed)
        logger.info { "notice 개수: ${notice.size}" }

        return NoticeResponse(notice)
    }

    @GetMapping("/{id}")
    fun getSpecificNotice(@PathVariable id: Long): SpecificNoticeResponse {
        logger.info { "세부 공지사항 요청: $id" }
        return noticeService.getSpecificNotice(id)
    }

    @PostMapping("/bookmark")
    fun getBookmarkNotice(@RequestBody bookmarkRequests: List<BookmarkRequest>): ResponseEntity<List<NoticeSnapshot>> {
        val notices = bookmarkRequests.map {
            noticeService.getNoticeSnapshotById(it.noticeId)
        }
        return ResponseEntity.ok(notices)
    }

    @PostMapping("/reminder")
    fun getReminderNotice(@RequestBody reminderRequests: List<ReminderRequest>): ResponseEntity<List<NoticeSnapshot>> {
        val notices = reminderRequests.map {
            noticeService.getNoticeSnapshotById(it.noticeId)
        }
        return ResponseEntity.ok(notices)
    }

}