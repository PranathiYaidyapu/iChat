package com.ichat.ichat.repository;

import com.ichat.ichat.model.CommunityReply;
import com.ichat.ichat.model.CommunityThread;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommunityReplyRepository extends JpaRepository<CommunityReply, Long> {
    List<CommunityReply> findByThreadOrderByCreatedAtAsc(CommunityThread thread);
}
