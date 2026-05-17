package cozy.ai.reminder.controller;

import cozy.ai.reminder.domain.ReminderList;
import cozy.ai.reminder.repository.ReminderListRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
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
class ReminderListControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ReminderListRepository reminderListRepository;

    @BeforeEach
    void setUp() {
        reminderListRepository.deleteAllInBatch();
    }

    @Nested
    @DisplayName("GET /api/lists")
    class GetAllTest {

        @Test
        @DisplayName("전체 리스트를 displayOrder 순으로 반환한다")
        void returnsAllLists() throws Exception {
            saveList("Work", "#FF3B30", 1);
            saveList("Personal", "#007AFF", 0);

            mockMvc.perform(get("/api/lists"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(2)))
                    .andExpect(jsonPath("$[0].name", is("Personal")))
                    .andExpect(jsonPath("$[1].name", is("Work")));
        }

        @Test
        @DisplayName("리스트가 없으면 빈 배열을 반환한다")
        void returnsEmptyArray() throws Exception {
            mockMvc.perform(get("/api/lists"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(0)));
        }
    }

    @Nested
    @DisplayName("GET /api/lists/{id}")
    class GetByIdTest {

        @Test
        @DisplayName("ID로 리스트를 조회한다")
        void returnsList() throws Exception {
            ReminderList saved = saveList("Personal", "#007AFF", 0);

            mockMvc.perform(get("/api/lists/{id}", saved.getId()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.name", is("Personal")))
                    .andExpect(jsonPath("$.color", is("#007AFF")));
        }

        @Test
        @DisplayName("존재하지 않는 ID는 404를 반환한다")
        void returns404() throws Exception {
            mockMvc.perform(get("/api/lists/{id}", 99))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("POST /api/lists")
    class CreateTest {

        @Test
        @DisplayName("새 리스트를 생성하고 201을 반환한다")
        void createsList() throws Exception {
            mockMvc.perform(post("/api/lists")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                                    {"name": "Shopping", "color": "#34C759", "icon": "cart"}
                                    """))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.name", is("Shopping")))
                    .andExpect(jsonPath("$.color", is("#34C759")))
                    .andExpect(jsonPath("$.icon", is("cart")))
                    .andExpect(jsonPath("$.displayOrder", is(0)))
                    .andExpect(jsonPath("$.id").isNumber());
        }

        @Test
        @DisplayName("기존 리스트가 있으면 displayOrder가 자동 증가한다")
        void autoIncrementsDisplayOrder() throws Exception {
            saveList("Existing", "#007AFF", 0);

            mockMvc.perform(post("/api/lists")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                                    {"name": "New", "color": "#FF3B30"}
                                    """))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.displayOrder", is(1)));
        }
    }

    @Nested
    @DisplayName("PATCH /api/lists/{id}")
    class UpdateTest {

        @Test
        @DisplayName("리스트를 수정하고 200을 반환한다")
        void updatesList() throws Exception {
            ReminderList saved = saveList("Old", "#007AFF", 0);

            mockMvc.perform(patch("/api/lists/{id}", saved.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                                    {"name": "New", "color": "#FF3B30", "icon": "star"}
                                    """))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.name", is("New")))
                    .andExpect(jsonPath("$.color", is("#FF3B30")))
                    .andExpect(jsonPath("$.icon", is("star")));
        }

        @Test
        @DisplayName("null 필드는 기존 값을 유지한다")
        void preservesNullFields() throws Exception {
            ReminderList saved = saveList("Personal", "#007AFF", 0);

            mockMvc.perform(patch("/api/lists/{id}", saved.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                                    {"name": "Updated"}
                                    """))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.name", is("Updated")))
                    .andExpect(jsonPath("$.color", is("#007AFF")));
        }

        @Test
        @DisplayName("존재하지 않는 ID는 404를 반환한다")
        void returns404() throws Exception {
            mockMvc.perform(patch("/api/lists/{id}", 99)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                                    {"name": "New"}
                                    """))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("DELETE /api/lists/{id}")
    class DeleteTest {

        @Test
        @DisplayName("리스트를 삭제하고 204를 반환한다")
        void deletesList() throws Exception {
            ReminderList saved = saveList("Personal", "#007AFF", 0);

            mockMvc.perform(delete("/api/lists/{id}", saved.getId()))
                    .andExpect(status().isNoContent());

            mockMvc.perform(get("/api/lists/{id}", saved.getId()))
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("존재하지 않는 ID는 404를 반환한다")
        void returns404() throws Exception {
            mockMvc.perform(delete("/api/lists/{id}", 99))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("PATCH /api/lists/reorder")
    class ReorderTest {

        @Test
        @DisplayName("리스트 순서를 변경하고 204를 반환한다")
        void reordersLists() throws Exception {
            ReminderList a = saveList("A", "#007AFF", 0);
            ReminderList b = saveList("B", "#FF3B30", 1);
            ReminderList c = saveList("C", "#34C759", 2);

            mockMvc.perform(patch("/api/lists/reorder")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(String.format("""
                                    {"ids": [%d, %d, %d]}
                                    """, c.getId(), a.getId(), b.getId())))
                    .andExpect(status().isNoContent());

            mockMvc.perform(get("/api/lists"))
                    .andExpect(jsonPath("$[0].name", is("C")))
                    .andExpect(jsonPath("$[1].name", is("A")))
                    .andExpect(jsonPath("$[2].name", is("B")));
        }
    }

    private ReminderList saveList(String name, String color, int displayOrder) {
        return reminderListRepository.save(
                ReminderList.builder()
                        .name(name)
                        .color(color)
                        .displayOrder(displayOrder)
                        .build()
        );
    }
}
