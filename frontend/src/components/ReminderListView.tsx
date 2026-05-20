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

  const pendingFocusIndexRef = useRef<number | null>(null);

  const handleToggleComplete = useCallback(
    async (id: number) => {
      // Determine which index to focus after the toggled item is removed
      const idx = reminders.findIndex((r) => r.id === id);
      if (idx >= 0 && !reminders[idx].completed) {
        // Completing: focus next item (or previous if last)
        const nextIdx = idx < reminders.length - 1 ? idx : Math.max(idx - 1, 0);
        pendingFocusIndexRef.current = nextIdx;
      }
      try {
        await toggleComplete(id);
        onRefresh();
      } catch (err) {
        showToast("Failed to toggle reminder");
        pendingFocusIndexRef.current = null;
      }
    },
    [onRefresh, reminders]
  );

  // Apply pending focus after reminders update
  useEffect(() => {
    if (pendingFocusIndexRef.current !== null && reminders.length > 0) {
      const targetIdx = Math.min(pendingFocusIndexRef.current, reminders.length - 1);
      setFocusedIndex(targetIdx);
      pendingFocusIndexRef.current = null;
      // Focus the DOM element
      const listEl = document.querySelector(`[role="list"]`);
      const items = listEl?.querySelectorAll<HTMLElement>('[role="listitem"]');
      items?.[targetIdx]?.focus();
    }
  }, [reminders]);

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

  // Reset focused index when the list itself changes (not just reminders content)
  const prevListIdRef = useRef(list.id);
  useEffect(() => {
    if (prevListIdRef.current !== list.id) {
      setFocusedIndex(-1);
      prevListIdRef.current = list.id;
    }
  }, [list.id]);

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
