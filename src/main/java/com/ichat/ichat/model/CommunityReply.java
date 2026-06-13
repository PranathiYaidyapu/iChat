package com.ichat.ichat.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "community_reply")
public class CommunityReply {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "thread_id")
    private CommunityThread thread;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_reply_id")
    private CommunityReply parentReply;

    @OneToMany(mappedBy = "parentReply", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<CommunityReply> children = new ArrayList<>();

    @Column(name = "reply_text", columnDefinition = "TEXT")
    private String replyText;

    @Column(name = "created_by")
    private Long createdBy;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Transient
    private String authorDisplayName;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public CommunityThread getThread() { return thread; }
    public void setThread(CommunityThread thread) { this.thread = thread; }

    public CommunityReply getParentReply() { return parentReply; }
    public void setParentReply(CommunityReply parentReply) { this.parentReply = parentReply; }

    public List<CommunityReply> getChildren() { return children; }
    public void setChildren(List<CommunityReply> children) { this.children = children; }

    public String getReplyText() { return replyText; }
    public void setReplyText(String replyText) { this.replyText = replyText; }

    public Long getCreatedBy() { return createdBy; }
    public void setCreatedBy(Long createdBy) { this.createdBy = createdBy; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public String getAuthorDisplayName() { return authorDisplayName; }
    public void setAuthorDisplayName(String authorDisplayName) { this.authorDisplayName = authorDisplayName; }

    public void setAuthorAvatar(String s) {
    }
}
