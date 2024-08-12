package com.ajouin.notice_view.domain

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = "member")
class Member (
    @Id
    val id: String? = null,
    val email: String,
    val bookmark: List<Long> = listOf(),
) {

}