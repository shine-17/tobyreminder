package cozy.ai.reminder.controller;

import cozy.ai.reminder.dto.request.ReminderListRequest;
import cozy.ai.reminder.dto.request.ReorderRequest;
import cozy.ai.reminder.dto.response.ReminderListResponse;
import cozy.ai.reminder.service.ports.in.ReminderListService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

import java.util.List;

@RestController
@RequestMapping("/api/lists")
@RequiredArgsConstructor
public class ReminderListController {

    private final ReminderListService reminderListService;

    @GetMapping
    public ResponseEntity<List<ReminderListResponse>> getAll() {
        return ResponseEntity.ok(reminderListService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReminderListResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(reminderListService.getById(id));
    }

    @PostMapping
    public ResponseEntity<ReminderListResponse> create(@Valid @RequestBody ReminderListRequest request) {
        ReminderListResponse created = reminderListService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ReminderListResponse> update(@PathVariable Long id,
                                                       @RequestBody ReminderListRequest request) {
        return ResponseEntity.ok(reminderListService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        reminderListService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/reorder")
    public ResponseEntity<Void> reorder(@RequestBody ReorderRequest request) {
        reminderListService.reorder(request.ids());
        return ResponseEntity.noContent().build();
    }
}
