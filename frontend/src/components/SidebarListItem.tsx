"use client";

import { ReminderList } from "@/types";
import { List } from "lucide-react";

interface SidebarListItemProps {
  list: ReminderList;
  isSelected: boolean;
  onClick: () => void;
}

export default function SidebarListItem({
  list,
  isSelected,
  onClick,
}: SidebarListItemProps) {
  return (
    <button
      onClick={onClick}
      className="flex items-center gap-2.5 w-full rounded-md px-2 py-1.5 text-left transition-colors duration-150"
      style={{
        backgroundColor: isSelected
          ? "rgba(0,0,0,0.08)"
          : "transparent",
      }}
    >
      {/* Color circle icon */}
      <div
        className="flex items-center justify-center rounded-full shrink-0"
        style={{
          backgroundColor: list.color,
          width: "22px",
          height: "22px",
        }}
      >
        <List size={12} color="white" strokeWidth={2.5} />
      </div>

      {/* List name */}
      <span
        className="flex-1 truncate text-[13px]"
        style={{
          color: "var(--text-primary)",
          fontWeight: isSelected ? 600 : 400,
        }}
      >
        {list.name}
      </span>

      {/* Reminder count */}
      {list.reminderCount > 0 && (
        <span
          className="text-[13px] tabular-nums"
          style={{ color: "var(--text-secondary)" }}
        >
          {list.reminderCount}
        </span>
      )}
    </button>
  );
}
