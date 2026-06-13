package com.ichat.ichat.repository;

import com.ichat.ichat.model.CommunityThread;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommunityThreadRepository extends JpaRepository<CommunityThread, Long> {

}
