"use client";

import { ReminderList, Reminder } from "@/types";
import ReminderRow from "./ReminderRow";
import { toggleComplete } from "@/lib/api";

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
  const handleToggleComplete = async (id: number) => {
    try {
      await toggleComplete(id);
      onRefresh();
    } catch (err) {
      console.error(err);
    }
  };

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
      <div className="flex flex-col">
        {reminders.length === 0 ? (
          <div
            className="text-center py-12 text-sm"
            style={{ color: "var(--text-tertiary)" }}
          >
            No Reminders
          </div>
        ) : (
          reminders.map((reminder) => (
            <ReminderRow
              key={reminder.id}
              reminder={reminder}
              listColor={list.color}
              onToggleComplete={handleToggleComplete}
            />
          ))
        )}
      </div>
    </div>
  );
}
