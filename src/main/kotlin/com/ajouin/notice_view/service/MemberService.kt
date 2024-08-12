package com.ajouin.notice_view.service

import com.ajouin.notice_view.domain.Member
import com.ajouin.notice_view.repository.MemberRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class MemberService(
    private val memberRepository: MemberRepository,
) {

    @Transactional
    fun getMember(email: String): Member {
        return memberRepository.findByEmail(email) ?: addMember(email)
    }

    @Transactional
    fun addMember(email: String): Member {
        return memberRepository.save(
            Member(email = email)
        )
    }

    fun getMemberBookmark(member: Member): List<Long> {
        return member.bookmark
    }
}