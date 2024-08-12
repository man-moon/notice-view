package com.ajouin.notice_view

import com.ajouin.notice_view.domain.Notice
import com.ajouin.notice_view.dto.BookmarkRequest
import com.ajouin.notice_view.dto.NoticeResponse
import com.ajouin.notice_view.dto.NoticeSnapshot
import com.ajouin.notice_view.dto.ReminderRequest
import com.ajouin.notice_view.service.MemberService
import com.ajouin.notice_view.service.NoticeService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/notice")
class NoticeController(
    private val noticeService: NoticeService,
    private val memberService: MemberService,
) {

    @GetMapping
    fun getNotice(
        @RequestParam types: List<String>,
        @RequestParam includeTopFixed: Boolean,
        @RequestParam offset: Int,
        @RequestParam limit: Int,
    ): NoticeResponse {

        val notice = noticeService.findNoticesByPaging(offset, limit, types, includeTopFixed)

        return NoticeResponse(notice)
    }

    @GetMapping("/{id}")
    fun getSpecificNotice(@PathVariable id: Long): Notice {
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