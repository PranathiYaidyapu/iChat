package com.ichat.ichat.service;

import com.ichat.ichat.model.CommunityReply;
import com.ichat.ichat.model.CommunityThread;
import com.ichat.ichat.model.User;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface CommunityService {
    CommunityThread createThread(CommunityThread thread, User author);
    Optional<CommunityThread> findThreadById(Long id);
    List<CommunityReply> getRepliesForThread(CommunityThread thread);
    List<CommunityReply> buildReplyTree(List<CommunityReply> flatReplies);



    CommunityReply addReply(Long threadId, Long parentReplyId, String text, User author);
    List<CommunityThread> findAllThreads();


}
