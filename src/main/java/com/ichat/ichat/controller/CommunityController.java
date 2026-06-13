package com.ichat.ichat.controller;

import com.ichat.ichat.model.CommunityReply;
import com.ichat.ichat.model.CommunityThread;
import com.ichat.ichat.model.User;
import com.ichat.ichat.service.CommunityService;
import com.ichat.ichat.service.ContentModerationService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/community")
public class CommunityController {

    @Autowired
    private ContentModerationService contentModerationService;

    private final CommunityService communityService;

    public CommunityController(CommunityService communityService) {
        this.communityService = communityService;
    }

    // List threads
    @GetMapping
    public String listThreads(Model model, HttpSession session) {
        if (session.getAttribute("loggedInUser") == null) return "redirect:/login";
        model.addAttribute("threads", communityService.findAllThreads());
        return "community/list";
    }

    // Show form to create thread
    @GetMapping("/new")
    public String newThreadForm(Model model, HttpSession session) {
        if (session.getAttribute("loggedInUser") == null) return "redirect:/login";
        if(model.containsAttribute("error")){
            model.addAttribute("error", "Your message contains inappropriate content. Please modify it.");
        }
        if (model.containsAttribute("title")) {
            model.addAttribute("title", model.getAttribute("title"));
            model.addAttribute("content", model.getAttribute("content"));
        }
        return "community/new";
    }

    @PostMapping
    public String createThread(@RequestParam String title,
                               @RequestParam String content,
                               HttpSession session,
                               RedirectAttributes redirectAttributes) {
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null) return "redirect:/login";

        // Validate content using moderation service
        boolean safe = contentModerationService.verifyContentSafe(title + " " + content);

        if (!safe) {

            redirectAttributes.addFlashAttribute("error",
                    "Your message contains inappropriate content. Please modify it.");
            redirectAttributes.addFlashAttribute("title", title);
            redirectAttributes.addFlashAttribute("content", content);
            return "redirect:/community/new";
        }

        CommunityThread thread = new CommunityThread();
        thread.setTitle(title);
        thread.setContent(content);
        communityService.createThread(thread, user);
        return "redirect:/community";
    }

    @GetMapping("/{id}")
    public String viewThread(@PathVariable Long id, Model model, HttpSession session) {
        CommunityThread thread = communityService.findThreadById(id)
                .orElseThrow(() -> new RuntimeException("Thread not found"));
        List<CommunityReply> flatReplies = communityService.getRepliesForThread(thread);
        List<CommunityReply> tree = communityService.buildReplyTree(flatReplies);

        model.addAttribute("thread", thread);
        model.addAttribute("replies", tree);

        // Add error message if any from reply submission
        if (session.getAttribute("replyError") != null) {
            model.addAttribute("replyError", session.getAttribute("replyError"));
            model.addAttribute("unsafeReplyText", session.getAttribute("unsafeReplyText"));
            session.removeAttribute("replyError");
            session.removeAttribute("unsafeReplyText");
        }

        return "community/view-thread";
    }

    @PostMapping("/reply")
    public String addReply(@RequestParam Long threadId,
                           @RequestParam(required = false) Long parentReplyId,
                           @RequestParam String replyText,
                           HttpSession session) {
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null) return "redirect:/login";

        // Validate content using moderation service
        boolean safe = contentModerationService.verifyContentSafe(replyText);

        if (!safe) {

            session.setAttribute("replyError",
                    "Your reply contains inappropriate content. Please modify it.");
            session.setAttribute("unsafeReplyText", replyText);
            return "redirect:/community/" + threadId;
        }

        communityService.addReply(threadId, parentReplyId, replyText, user);
        return "redirect:/community/" + threadId;
    }
}