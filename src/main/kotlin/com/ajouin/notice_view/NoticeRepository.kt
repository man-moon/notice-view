package com.ajouin.notice_view

import org.bson.types.ObjectId
import org.springframework.data.mongodb.repository.MongoRepository

interface NoticeRepository : MongoRepository<Notice, ObjectId> {
}