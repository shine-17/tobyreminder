package cozy.ai.reminder.domain;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "reminder_list")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ReminderList {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String color;

    private String icon;

    @Column(nullable = false)
    private Integer displayOrder;

    @Column(nullable = false)
    private boolean isDefault = false;

    @Column(updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "list", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Reminder> reminders = new ArrayList<>();

    @Builder
    public ReminderList(String name, String color, String icon, Integer displayOrder, boolean isDefault) {
        this.name = name;
        this.color = color;
        this.icon = icon;
        this.displayOrder = displayOrder;
        this.isDefault = isDefault;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = this.createdAt;
    }

    public void update(String name, String color, String icon) {
        if (name != null) this.name = name;
        if (color != null) this.color = color;
        if (icon != null) this.icon = icon;
        this.updatedAt = LocalDateTime.now();
    }

    public void updateDisplayOrder(Integer displayOrder) {
        this.displayOrder = displayOrder;
        this.updatedAt = LocalDateTime.now();
    }
}
