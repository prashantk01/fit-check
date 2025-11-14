package com.fitcheck.fit_check.controller;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.fitcheck.fit_check.dto.weight.WeightAdd;
import com.fitcheck.fit_check.service.WeightService;

import jakarta.validation.Valid;

import com.fitcheck.fit_check.dto.weight.WeightResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/weights")
public class WeightController {

    private final WeightService weightService;

    public WeightController(WeightService weightService) {
        this.weightService = weightService;
    }

    @GetMapping
    public ResponseEntity<List<WeightResponse>> getWeight(@RequestParam String userId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime start,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime end) {
        List<WeightResponse> weights;
        if (start == null && end == null) {
            weights = weightService.getWeightEntriesOfAUser(userId);
            return ResponseEntity.ok(weights);
        }
        if (start == null) {
            start = OffsetDateTime.MIN;
        }
        if (end == null) {
            end = OffsetDateTime.now(ZoneOffset.UTC);
        }
        if (start.isAfter(end)) {
            throw new IllegalArgumentException("startTime must be before endTime");
        }
        if (end.isAfter(OffsetDateTime.now(ZoneOffset.UTC))) {
            throw new IllegalArgumentException("endTime cannot be in the future");
        }
        weights = weightService.getWeightEntriesOfAUserInDateRange(userId, start, end);
        return ResponseEntity.ok(weights);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteWeightEntry(@PathVariable String id, @RequestParam String userId) {
        weightService.deleteWeightEntry(userId, id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/clear")
    public ResponseEntity<Void> deleteAllWeightEntriesOfAUser(@RequestParam String userId) {
        weightService.deleteAllWeightEntriesOfAUser(userId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping
    public ResponseEntity<WeightResponse> addWeightEntry(@Valid @RequestBody WeightAdd weightAdd,
            @RequestParam String userId) {
        WeightResponse weightResponse = weightService.addWeightEntry(userId, weightAdd);
        return ResponseEntity.status(HttpStatus.CREATED).body(weightResponse);
    }
}
