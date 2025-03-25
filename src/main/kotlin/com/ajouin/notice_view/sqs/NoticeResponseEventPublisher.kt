package com.ajouin.notice_view.sqs

import com.ajouin.notice_view.logger
import com.ajouin.notice_view.sqs.config.EventQueuesProperties
import com.ajouin.notice_view.sqs.event.NoticeResponseEvent
import com.fasterxml.jackson.databind.ObjectMapper
import io.awspring.cloud.sqs.operations.SqsTemplate
import org.springframework.stereotype.Component

@Component
class NoticeResponseEventPublisher(
    private val sqsTemplate: SqsTemplate,
    private val objectMapper: ObjectMapper,
    private val eventQueuesProperties: EventQueuesProperties,
) {

    fun publish(event: NoticeResponseEvent) {
        val messagePayload = objectMapper.writeValueAsString(event)
        sqsTemplate.send { to ->
            to.queue(eventQueuesProperties.noticeForRemindResponseQueue)
                .payload(messagePayload)
        }

        logger.info { "Message sent with payload: reminderId=${event.reminderId}" }
    }

}