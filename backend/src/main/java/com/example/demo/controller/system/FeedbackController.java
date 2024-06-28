package com.example.demo.controller.system;

import com.example.demo.domain.system.feedback.Feedback;
import com.example.demo.domain.system.feedback.FeedbackRepoCustom;
import com.example.demo.dto.system.FeedbackDto;
import com.example.demo.service.system.FeedbackService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping(
        value = "/api/system/feedback",
        produces = "application/json"
)
public class FeedbackController {
    private final FeedbackService service;
    private final FeedbackRepoCustom query;

    @GetMapping("/search")
    public ResponseEntity<?> search(FeedbackDto.Keyword req) {
        return new ResponseEntity<>(query.search(req), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<?> insertOne(@RequestBody FeedbackDto.Req req) {
        return new ResponseEntity<>(service.insertOne(req), HttpStatus.OK);
    }

    @PatchMapping
    public ResponseEntity<?> updateOne(@RequestBody FeedbackDto.Req req) {
        return new ResponseEntity<>(service.updateOne(req), HttpStatus.OK);
    }

    @GetMapping("/hit/{id}")
    public void updatehit(@PathVariable Long id) {
        service.updateHit(id);
    }

    @GetMapping("/status/{id}")
    public void updateStatus(@PathVariable Long id) {
        service.updateStatus(id);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteOne(@PathVariable Long id) {
        return new ResponseEntity<>(service.deleteOne(id), HttpStatus.OK);
    }

}
