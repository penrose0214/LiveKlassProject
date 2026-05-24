package com.liveklass.command.application.controller;

import com.liveklass.command.application.dto.CreateLectureRequest;
import com.liveklass.command.application.dto.UpdateLectureRequest;
import com.liveklass.command.application.service.LectureCommandService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/command/lectures")
@RequiredArgsConstructor
public class LectureCommandController {

    private final LectureCommandService lectureCommandService;

    // LEC-REG-001
    @PostMapping
    public ResponseEntity<Void> createLecture(
            @RequestHeader("userId") Long userId,
            @RequestBody CreateLectureRequest request
    ) {
        lectureCommandService.createLecture(userId, request);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{lectureId}")
    public ResponseEntity<Void> updateLecture(
            @RequestHeader("userId") Long userId,
            @PathVariable Long lectureId,
            @RequestBody UpdateLectureRequest request
    ) {
        lectureCommandService.updateLecture(userId, lectureId, request);
        return ResponseEntity.ok().build();
    }

    // LEC-STAT-001
    @PostMapping("/{lectureId}/open")
    public ResponseEntity<Void> openLecture(
            @RequestHeader("userId") Long userId,
            @PathVariable Long lectureId
    ) {
        lectureCommandService.openLecture(userId, lectureId);
        return ResponseEntity.ok().build();
    }

    // LEC-STAT-002
    @PostMapping("/{lectureId}/close")
    public ResponseEntity<Void> closeLecture(
            @RequestHeader("userId") Long userId,
            @PathVariable Long lectureId
    ) {
        lectureCommandService.closeLecture(userId, lectureId);
        return ResponseEntity.ok().build();
    }
}
