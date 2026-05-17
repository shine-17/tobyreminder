"use client";

import { useState, useRef } from "react";
import { Plus } from "lucide-react";
import { createReminder } from "@/lib/api";

interface AddReminderProps {
  listId: number;
  listColor: string;
  onAdded: () => void;
}

export default function AddReminder({
  listId,
  listColor,
  onAdded,
}: AddReminderProps) {
  const [isEditing, setIsEditing] = useState(false);
  const [title, setTitle] = useState("");
  const inputRef = useRef<HTMLInputElement>(null);

  const handleSubmit = async () => {
    const trimmed = title.trim();
    if (!trimmed) return;

    try {
      await createReminder({ title: trimmed, listId });
      setTitle("");
      onAdded();
      // Keep focus for continuous input
      setTimeout(() => inputRef.current?.focus(), 0);
    } catch (err) {
      console.error(err);
    }
  };

  const handleKeyDown = (e: React.KeyboardEvent) => {
    if (e.key === "Enter") {
      e.preventDefault();
      handleSubmit();
    } else if (e.key === "Escape") {
      setTitle("");
      setIsEditing(false);
    }
  };

  if (!isEditing) {
    return (
      <button
        onClick={() => {
          setIsEditing(true);
          setTimeout(() => inputRef.current?.focus(), 0);
        }}
        className="flex items-center gap-2 py-2.5 px-2 text-[13px] rounded-lg transition-colors duration-150"
        style={{ color: "var(--text-secondary)" }}
      >
        <Plus size={16} style={{ color: listColor }} />
        Add Reminder
      </button>
    );
  }

  return (
    <div className="flex items-center gap-3 py-2.5 px-2">
      <div
        className="flex items-center justify-center rounded-full shrink-0"
        style={{
          width: "20px",
          height: "20px",
          border: `1.5px solid ${listColor}`,
        }}
      />
      <input
        ref={inputRef}
        type="text"
        value={title}
        onChange={(e) => setTitle(e.target.value)}
        onKeyDown={handleKeyDown}
        onBlur={() => {
          if (!title.trim()) setIsEditing(false);
          else handleSubmit();
        }}
        placeholder="New Reminder"
        className="flex-1 text-[13px] leading-5 bg-transparent outline-none"
        style={{ color: "var(--text-primary)" }}
        autoFocus
      />
    </div>
  );
}
