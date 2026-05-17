package cozy.ai.reminder.controller;

import cozy.ai.reminder.domain.Reminder;
import cozy.ai.reminder.domain.ReminderList;
import cozy.ai.reminder.repository.ReminderListRepository;
import cozy.ai.reminder.repository.ReminderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
class ReminderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ReminderRepository reminderRepository;

    @Autowired
    private ReminderListRepository reminderListRepository;

    private ReminderList defaultList;

    @BeforeEach
    void setUp() {
        reminderRepository.deleteAllInBatch();
        reminderListRepository.deleteAllInBatch();
        defaultList = reminderListRepository.save(
                ReminderList.builder().name("Personal").color("#007AFF").displayOrder(0).build());
    }

    @Nested
    @DisplayName("GET /api/reminders?listId={id}")
    class GetByListIdTest {

        @Test
        @DisplayName("리스트별 미완료 리마인더를 반환한다")
        void returnsActiveReminders() throws Exception {
            saveReminder("Task 1", 0);
            saveReminder("Task 2", 1);

            mockMvc.perform(get("/api/reminders").param("listId", defaultList.getId().toString()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(2)))
                    .andExpect(jsonPath("$[0].title", is("Task 1")));
        }

        @Test
        @DisplayName("includeCompleted=true이면 완료 항목도 포함한다")
        void includesCompleted() throws Exception {
            saveReminder("Active", 0);
            Reminder done = saveReminder("Done", 1);
            done.toggleComplete();
            reminderRepository.save(done);

            mockMvc.perform(get("/api/reminders")
                            .param("listId", defaultList.getId().toString())
                            .param("includeCompleted", "true"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(2)));
        }
    }

    @Nested
    @DisplayName("GET /api/reminders/{id}")
    class GetByIdTest {

        @Test
        @DisplayName("ID로 리마인더를 조회한다")
        void returnsReminder() throws Exception {
            Reminder saved = saveReminder("Test", 0);

            mockMvc.perform(get("/api/reminders/{id}", saved.getId()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.title", is("Test")));
        }

        @Test
        @DisplayName("존재하지 않는 ID는 404를 반환한다")
        void returns404() throws Exception {
            mockMvc.perform(get("/api/reminders/{id}", 99))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("POST /api/reminders")
    class CreateTest {

        @Test
        @DisplayName("리마인더를 생성하고 201을 반환한다")
        void createsReminder() throws Exception {
            mockMvc.perform(post("/api/reminders")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(String.format("""
                                    {"title": "Buy milk", "memo": "From store", "listId": %d}
                                    """, defaultList.getId())))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.title", is("Buy milk")))
                    .andExpect(jsonPath("$.memo", is("From store")))
                    .andExpect(jsonPath("$.completed", is(false)))
                    .andExpect(jsonPath("$.id").isNumber());
        }
    }

    @Nested
    @DisplayName("PATCH /api/reminders/{id}")
    class UpdateTest {

        @Test
        @DisplayName("리마인더를 수정하고 200을 반환한다")
        void updatesReminder() throws Exception {
            Reminder saved = saveReminder("Old", 0);

            mockMvc.perform(patch("/api/reminders/{id}", saved.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                                    {"title": "New", "memo": "Updated"}
                                    """))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.title", is("New")))
                    .andExpect(jsonPath("$.memo", is("Updated")));
        }
    }

    @Nested
    @DisplayName("PATCH /api/reminders/{id}/complete")
    class ToggleCompleteTest {

        @Test
        @DisplayName("완료 상태를 토글한다")
        void togglesComplete() throws Exception {
            Reminder saved = saveReminder("Test", 0);

            mockMvc.perform(patch("/api/reminders/{id}/complete", saved.getId()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.completed", is(true)))
                    .andExpect(jsonPath("$.completedAt").isNotEmpty());

            mockMvc.perform(patch("/api/reminders/{id}/complete", saved.getId()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.completed", is(false)))
                    .andExpect(jsonPath("$.completedAt").isEmpty());
        }
    }

    @Nested
    @DisplayName("DELETE /api/reminders/{id}")
    class DeleteTest {

        @Test
        @DisplayName("리마인더를 삭제하고 204를 반환한다")
        void deletesReminder() throws Exception {
            Reminder saved = saveReminder("Test", 0);

            mockMvc.perform(delete("/api/reminders/{id}", saved.getId()))
                    .andExpect(status().isNoContent());

            mockMvc.perform(get("/api/reminders/{id}", saved.getId()))
                    .andExpect(status().isNotFound());
        }
    }

    private Reminder saveReminder(String title, int displayOrder) {
        return reminderRepository.save(
                Reminder.builder()
                        .title(title)
                        .displayOrder(displayOrder)
                        .list(defaultList)
                        .build());
    }
}
