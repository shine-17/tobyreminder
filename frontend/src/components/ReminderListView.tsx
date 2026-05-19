"use client";

import { useState, useCallback, useEffect, useRef } from "react";
import { ReminderList, Reminder } from "@/types";
import ReminderRow from "./ReminderRow";
import ReminderDetail from "./ReminderDetail";
import AddReminder from "./AddReminder";
import { toggleComplete } from "@/lib/api";
import { showToast } from "./Toast";

interface ReminderListViewProps {
  list: ReminderList;
  reminders: Reminder[];
  onRefresh: () => void;
}

export default function ReminderListView({
  list,
  reminders,
  onRefresh,
}: ReminderListViewProps) {
  const [editingId, setEditingId] = useState<number | null>(null);
  const [focusedIndex, setFocusedIndex] = useState<number>(-1);

  const handleToggleComplete = useCallback(
    async (id: number) => {
      try {
        await toggleComplete(id);
        onRefresh();
      } catch (err) {
        showToast("Failed to toggle reminder");
      }
    },
    [onRefresh]
  );

  // Keyboard navigation
  useEffect(() => {
    const handleKeyDown = (e: KeyboardEvent) => {
      if (editingId !== null) return; // skip when editing

      if (e.key === "ArrowDown") {
        e.preventDefault();
        setFocusedIndex((prev) =>
          prev < reminders.length - 1 ? prev + 1 : prev
        );
      } else if (e.key === "ArrowUp") {
        e.preventDefault();
        setFocusedIndex((prev) => (prev > 0 ? prev - 1 : prev));
      } else if (e.key === "Enter" && focusedIndex >= 0) {
        e.preventDefault();
        setEditingId(reminders[focusedIndex].id);
      } else if (e.key === "Escape") {
        setFocusedIndex(-1);
      }
    };

    document.addEventListener("keydown", handleKeyDown);
    return () => document.removeEventListener("keydown", handleKeyDown);
  }, [editingId, focusedIndex, reminders]);

  // Reset focused index when reminders change
  useEffect(() => {
    setFocusedIndex(-1);
  }, [reminders]);

  return (
    <div className="max-w-[640px] mx-auto px-4 py-6">
      {/* List title */}
      <h1
        className="text-[22px] font-bold mb-4"
        style={{ color: list.color }}
      >
        {list.name}
      </h1>

      {/* Reminders */}
      <div className="flex flex-col" role="list">
        {reminders.length === 0 && editingId === null ? (
          <div
            className="text-center py-12 text-sm"
            style={{ color: "var(--text-tertiary)" }}
          >
            No Reminders
          </div>
        ) : (
          reminders.map((reminder, index) =>
            editingId === reminder.id ? (
              <div key={reminder.id} role="listitem">
                <ReminderDetail
                  reminder={reminder}
                  listColor={list.color}
                  onToggleComplete={handleToggleComplete}
                  onSaved={onRefresh}
                  onClose={() => setEditingId(null)}
                />
              </div>
            ) : (
              <div
                key={reminder.id}
                role="listitem"
                tabIndex={0}
                style={{
                  outline:
                    focusedIndex === index
                      ? `2px solid ${list.color}`
                      : "none",
                  borderRadius: "8px",
                }}
              >
                <ReminderRow
                  reminder={reminder}
                  listColor={list.color}
                  onToggleComplete={handleToggleComplete}
                  onClick={() => setEditingId(reminder.id)}
                />
              </div>
            )
          )
        )}
      </div>

      {/* Add Reminder */}
      <div
        className="mt-2 border-t"
        style={{ borderColor: "var(--bg-reminder-active)" }}
      >
        <AddReminder
          listId={list.id}
          listColor={list.color}
          onAdded={onRefresh}
        />
      </div>
    </div>
  );
}
