package com.ajouin.notice_view.sqs.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "events.queues")
class EventQueuesProperties (
    val noticeForRemindRequestQueue: String,
    val noticeForRemindResponseQueue: String,
)