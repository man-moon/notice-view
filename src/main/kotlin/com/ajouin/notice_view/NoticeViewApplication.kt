package com.ajouin.notice_view

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication

@SpringBootApplication
@ConfigurationPropertiesScan
class NoticeViewApplication

fun main(args: Array<String>) {
	runApplication<NoticeViewApplication>(*args)
}
