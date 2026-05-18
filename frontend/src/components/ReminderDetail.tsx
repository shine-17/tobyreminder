"use client";

import { useState, useRef, useEffect, useCallback } from "react";
import { Trash2 } from "lucide-react";
import { Reminder } from "@/types";
import { updateReminder, deleteReminder } from "@/lib/api";
import Checkbox from "./Checkbox";

interface ReminderDetailProps {
  reminder: Reminder;
  listColor: string;
  onToggleComplete: (id: number) => void;
  onSaved: () => void;
  onClose: () => void;
}

export default function ReminderDetail({
  reminder,
  listColor,
  onToggleComplete,
  onSaved,
  onClose,
}: ReminderDetailProps) {
  const [title, setTitle] = useState(reminder.title);
  const [memo, setMemo] = useState(reminder.memo ?? "");
  const [saving, setSaving] = useState(false);
  const containerRef = useRef<HTMLDivElement>(null);
  const titleRef = useRef<HTMLInputElement>(null);

  // Focus title on mount
  useEffect(() => {
    titleRef.current?.focus();
  }, []);

  const handleSave = useCallback(async () => {
    if (saving) return;
    const trimmedTitle = title.trim();
    if (!trimmedTitle) {
      onClose();
      return;
    }

    const titleChanged = trimmedTitle !== reminder.title;
    const memoChanged = memo.trim() !== (reminder.memo ?? "");

    if (titleChanged || memoChanged) {
      setSaving(true);
      try {
        await updateReminder(reminder.id, {
          title: trimmedTitle,
          memo: memo.trim() || null,
        });
        onSaved();
      } catch (err) {
        console.error(err);
      } finally {
        setSaving(false);
      }
    }
    onClose();
  }, [title, memo, reminder, saving, onSaved, onClose]);

  // Use ref to always have latest handleSave in the event listener
  const handleSaveRef = useRef(handleSave);
  handleSaveRef.current = handleSave;

  // Click outside to save & close
  useEffect(() => {
    const handleClickOutside = (e: MouseEvent) => {
      if (
        containerRef.current &&
        !containerRef.current.contains(e.target as Node)
      ) {
        handleSaveRef.current();
      }
    };
    document.addEventListener("mousedown", handleClickOutside);
    return () => document.removeEventListener("mousedown", handleClickOutside);
  }, []);

  const handleDelete = async () => {
    try {
      await deleteReminder(reminder.id);
      onSaved();
      onClose();
    } catch (err) {
      console.error(err);
    }
  };

  const handleKeyDown = (e: React.KeyboardEvent) => {
    if (e.key === "Escape") {
      handleSave();
    }
  };

  return (
    <div
      ref={containerRef}
      className="flex items-start gap-3 py-3 px-2 rounded-lg"
      style={{ backgroundColor: "var(--bg-reminder-active)" }}
    >
      {/* Checkbox */}
      <div className="pt-0.5">
        <Checkbox
          checked={reminder.completed}
          color={listColor}
          onChange={() => onToggleComplete(reminder.id)}
        />
      </div>

      {/* Edit fields */}
      <div className="flex-1 min-w-0 flex flex-col gap-1">
        <input
          ref={titleRef}
          type="text"
          value={title}
          onChange={(e) => setTitle(e.target.value)}
          onKeyDown={handleKeyDown}
          className="text-[13px] leading-5 bg-transparent outline-none w-full"
          style={{ color: "var(--text-primary)" }}
          placeholder="Title"
        />
        <textarea
          value={memo}
          onChange={(e) => setMemo(e.target.value)}
          onKeyDown={handleKeyDown}
          className="text-[11px] leading-4 bg-transparent outline-none w-full resize-none"
          style={{ color: "var(--text-secondary)" }}
          placeholder="Add memo..."
          rows={2}
        />
      </div>

      {/* Delete button */}
      <button
        onClick={handleDelete}
        className="shrink-0 p-1 rounded hover:bg-red-50 transition-colors"
        title="Delete"
        aria-label="Delete reminder"
      >
        <Trash2 size={14} style={{ color: "var(--list-red)" }} />
      </button>
    </div>
  );
}
