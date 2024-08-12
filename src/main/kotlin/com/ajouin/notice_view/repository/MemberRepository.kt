package com.ajouin.notice_view.repository

import com.ajouin.notice_view.domain.Member
import org.springframework.data.domain.Pageable
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface MemberRepository : MongoRepository<Member, String> {

    fun findByEmail(email: String): Member?
}
