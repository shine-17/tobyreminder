"use client";

import { useState, memo } from "react";
import { Reminder } from "@/types";
import Checkbox from "./Checkbox";

interface ReminderRowProps {
  reminder: Reminder;
  listColor: string;
  onToggleComplete: (id: number) => void;
  onClick: () => void;
}

function ReminderRowInner({
  reminder,
  listColor,
  onToggleComplete,
  onClick,
}: ReminderRowProps) {
  const [fading, setFading] = useState(false);

  const handleToggle = () => {
    if (!reminder.completed) {
      // Completing: animate then toggle
      setFading(true);
      onToggleComplete(reminder.id);
    } else {
      onToggleComplete(reminder.id);
    }
  };

  return (
    <div
      className="flex items-start gap-3 py-2.5 px-2 rounded-lg transition-all duration-150 group cursor-pointer min-h-[36px]"
      style={{
        opacity: fading ? 0 : 1,
        transition: fading
          ? "opacity 0.5s ease, max-height 0.5s ease"
          : "background-color 0.15s ease",
      }}
      onClick={onClick}
      onMouseEnter={(e) =>
        (e.currentTarget.style.backgroundColor =
          "var(--bg-reminder-hover)")
      }
      onMouseLeave={(e) =>
        (e.currentTarget.style.backgroundColor = "transparent")
      }
    >
      {/* Checkbox */}
      <div
        className="pt-0.5"
        onClick={(e) => {
          e.stopPropagation();
          handleToggle();
        }}
      >
        <Checkbox
          checked={reminder.completed}
          color={listColor}
          onChange={() => {}}
          label={reminder.title}
        />
      </div>

      {/* Content */}
      <div className="flex-1 min-w-0">
        {/* Title */}
        <div
          className="text-[13px] leading-5 truncate"
          style={{
            color: reminder.completed
              ? "var(--text-tertiary)"
              : "var(--text-primary)",
            textDecoration: reminder.completed ? "line-through" : "none",
            transition: "all 0.3s ease",
          }}
          title={reminder.title}
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

const ReminderRow = memo(ReminderRowInner, (prev, next) => {
  return (
    prev.reminder === next.reminder &&
    prev.listColor === next.listColor &&
    prev.onToggleComplete === next.onToggleComplete &&
    prev.onClick === next.onClick
  );
});

ReminderRow.displayName = "ReminderRow";

export default ReminderRow;
