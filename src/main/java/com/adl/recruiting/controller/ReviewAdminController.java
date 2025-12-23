package com.adl.recruiting.controller;

import com.adl.recruiting.dto.CreateReviewRequest;
import com.adl.recruiting.dto.ReviewResponse;
import com.adl.recruiting.service.ReviewService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@PreAuthorize("hasAnyRole('DIRECTOR','TEAMLEAD','PM')")
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin/reviews")
public class ReviewAdminController {

    private final ReviewService reviewService;

    @PostMapping
    public ReviewResponse create(@Valid @RequestBody CreateReviewRequest req) {
        return reviewService.create(req);
    }

    @GetMapping
    public List<ReviewResponse> listByCandidate(@RequestParam("candidateId") long candidateId) {
        return reviewService.listByCandidate(candidateId);
    }
}
