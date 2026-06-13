package com.ichat.ichat.service;

import com.ichat.ichat.model.CommunityReply;
import com.ichat.ichat.model.CommunityThread;
import com.ichat.ichat.model.User;
import com.ichat.ichat.repository.CommunityReplyRepository;
import com.ichat.ichat.repository.CommunityThreadRepository;
import com.ichat.ichat.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class CommunityServiceImpl implements CommunityService {

    private final CommunityThreadRepository threadRepo;
    private final CommunityReplyRepository replyRepo;
    private final UserRepository userRepository;

    public CommunityServiceImpl(CommunityThreadRepository threadRepo, CommunityReplyRepository replyRepo, UserRepository userRepository) {
        this.threadRepo = threadRepo;
        this.replyRepo = replyRepo;
        this.userRepository = userRepository;
    }

    @Override
    public CommunityThread createThread(CommunityThread thread, User author) {
        thread.setCreatedBy(author.getId());
        return threadRepo.save(thread);
    }

    @Override
    public Optional<CommunityThread> findThreadById(Long id) {
        return threadRepo.findById(id);
    }

    @Override
    public List<CommunityReply> getRepliesForThread(CommunityThread thread) {
        List<CommunityReply> replies = replyRepo.findByThreadOrderByCreatedAtAsc(thread);

        replies.forEach(reply -> {
            User author = userRepository.findById(reply.getCreatedBy()).orElse(null);
            if (author != null) {
                reply.setAuthorDisplayName(formatUserDisplayName(author));
            }
        });
        return replies;
    }

    /**
     * Build tree from flat list: returns list of root replies each with children populated.
     */
    @Override
    public List<CommunityReply> buildReplyTree(List<CommunityReply> flatReplies) {
        Map<Long, CommunityReply> map = new LinkedHashMap<>();
        List<CommunityReply> roots = new ArrayList<>();


        for (CommunityReply reply : flatReplies) {
            reply.setChildren(new ArrayList<>());
            map.put(reply.getId(), reply);
        }


        for (CommunityReply reply : flatReplies) {
            if (reply.getParentReply() != null) {
                CommunityReply parent = map.get(reply.getParentReply().getId());
                if (parent != null) {
                    parent.getChildren().add(reply);
                } else {
                    // If parent not found (shouldn't happen normally), treat as root
                    roots.add(reply);
                }
            } else {
                roots.add(reply);
            }
        }

        return roots;
    }

    @Override
    public List<CommunityThread> findAllThreads() {
        List<CommunityThread> threads = threadRepo.findAll();

        threads.forEach(thread -> {
            User author = userRepository.findById(thread.getCreatedBy()).orElse(null);
            if (author != null) {
                thread.setAuthorDisplayName(formatUserDisplayName(author));
            }
        });
        return threads;
    }

    @Override
    @Transactional
    public CommunityReply addReply(Long threadId, Long parentReplyId, String text, User author) {
        CommunityThread thread = threadRepo.findById(threadId)
                .orElseThrow(() -> new RuntimeException("Thread not found"));

        CommunityReply reply = new CommunityReply();
        reply.setThread(thread);
        reply.setReplyText(text);
        reply.setCreatedBy(author.getId());

        if (parentReplyId != null) {
            CommunityReply parent = replyRepo.findById(parentReplyId)
                    .orElseThrow(() -> new RuntimeException("Parent reply not found"));
            reply.setParentReply(parent);
        }
        return replyRepo.save(reply);
    }

    private String formatUserDisplayName(User user) {
        String name = user.getLastName() + " " + user.getFirstname();

        if (user.getRole().equals("ROLE_DOCTOR") && user.getDoctor() != null) {
            String designation = user.getDoctor().getDesignation();
            return name + " (" + designation + ")";
        }

        return name;
    }

}