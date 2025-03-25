package com.ajouin.notice_view.sqs

import com.ajouin.notice_view.dto.SpecificNoticeResponse
import com.ajouin.notice_view.logger
import com.ajouin.notice_view.service.NoticeService
import com.ajouin.notice_view.sqs.event.NoticeRequestEvent
import com.ajouin.notice_view.sqs.event.NoticeResponseEvent
import com.fasterxml.jackson.databind.ObjectMapper
import io.awspring.cloud.sqs.annotation.SqsListener
import org.springframework.stereotype.Component

@Component
class NoticeRequestEventListener(
    private val objectMapper: ObjectMapper,
    private val noticeService: NoticeService,
    private val publisher: NoticeResponseEventPublisher,
) {
    @SqsListener("\${events.queues.notice-for-remind-request-queue}")
    fun receiveNoticeRequest(message: String) {
        val request: NoticeRequestEvent = objectMapper.readValue(message, NoticeRequestEvent::class.java)
        logger.info { "Received message: reminderId=${request.reminderId}" }

        val notice: SpecificNoticeResponse = noticeService.getSpecificNotice(request.id)
        val response = NoticeResponseEvent(
            isTopFixed = notice.isTopFixed,
            createdAt = notice.createdAt,
            fetchId = notice.fetchId,
            id = notice.id,
            originalUrl = notice.originalUrl,
            title = notice.title,
            html = notice.html,
            content = notice.content,
            date = notice.date,
            noticeType = notice.noticeType,
            summary = notice.summary,
            reminderId = request.reminderId,
        )

        publisher.publish(response)

    }
}