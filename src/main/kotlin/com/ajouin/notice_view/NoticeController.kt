package com.ajouin.notice_view

import com.ajouin.notice_view.dto.NoticeSnapshot
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDateTime

@RestController
@RequestMapping("/api/notices")
class NoticeController(
    private val noticeService: NoticeService,
) {

    // 공지사항 목록
    // pathvariable: type
    // paging: 20
    @GetMapping
    fun getNotices(@RequestParam(required = false) type: List<Long>?): List<NoticeSnapshot> {

        val notices = noticeService.getNotices()

        return listOf(
            NoticeSnapshot(
                isTopFixed = true,
                createdAt = LocalDateTime.now(),
                fetchId = 1,
                id = 1,
                title = "test",
                noticeType = "test",
                summary = "test"
            )
        )
    }

    // 공지사항 상세
    // param: fetch_id
    @GetMapping("/notices/{fetchId}")
    fun getSpecificNotice(@PathVariable fetchId: Long) {

    }
}