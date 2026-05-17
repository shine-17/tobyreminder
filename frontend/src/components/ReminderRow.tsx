"use client";

import { Reminder } from "@/types";
import Checkbox from "./Checkbox";

interface ReminderRowProps {
  reminder: Reminder;
  listColor: string;
  onToggleComplete: (id: number) => void;
}

export default function ReminderRow({
  reminder,
  listColor,
  onToggleComplete,
}: ReminderRowProps) {
  return (
    <div
      className="flex items-start gap-3 py-2.5 px-2 rounded-lg transition-colors duration-150 group"
      style={{ minHeight: "36px" }}
      onMouseEnter={(e) =>
        (e.currentTarget.style.backgroundColor =
          "var(--bg-reminder-hover)")
      }
      onMouseLeave={(e) =>
        (e.currentTarget.style.backgroundColor = "transparent")
      }
    >
      {/* Checkbox */}
      <div className="pt-0.5">
        <Checkbox
          checked={reminder.completed}
          color={listColor}
          onChange={() => onToggleComplete(reminder.id)}
        />
      </div>

      {/* Content */}
      <div className="flex-1 min-w-0">
        {/* Title */}
        <div
          className="text-[13px] leading-5"
          style={{
            color: reminder.completed
              ? "var(--text-tertiary)"
              : "var(--text-primary)",
            textDecoration: reminder.completed ? "line-through" : "none",
          }}
        >
          {reminder.title}
        </div>

        {/* Memo */}
        {reminder.memo && (
          <div
            className="text-[11px] leading-4 truncate mt-0.5"
            style={{ color: "var(--text-secondary)" }}
          >
            {reminder.memo}
          </div>
        )}
      </div>
    </div>
  );
}
