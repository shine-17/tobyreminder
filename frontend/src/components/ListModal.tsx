"use client";

import { useState, useEffect, useRef, useCallback } from "react";
import { X } from "lucide-react";
import { ReminderList } from "@/types";

const PRESET_COLORS = [
  "#FF3B30", "#FF9500", "#FFCC00", "#34C759",
  "#5AC8FA", "#007AFF", "#5856D6", "#AF52DE",
  "#FF2D55", "#A2845E", "#8E8E93", "#636366",
];

const PRESET_ICONS = [
  "list.bullet", "bookmark", "star", "heart",
  "cart", "briefcase", "book", "house",
  "airplane", "gift", "music.note", "gamecontroller",
];

interface ListModalProps {
  editingList?: ReminderList | null;
  onSave: (data: { name: string; color: string; icon: string }) => void;
  onCancel: () => void;
}

export default function ListModal({
  editingList,
  onSave,
  onCancel,
}: ListModalProps) {
  const [name, setName] = useState(editingList?.name ?? "");
  const [color, setColor] = useState(editingList?.color ?? "#007AFF");
  const [icon, setIcon] = useState(editingList?.icon ?? "list.bullet");
  const inputRef = useRef<HTMLInputElement>(null);
  const modalRef = useRef<HTMLDivElement>(null);
  const previousFocusRef = useRef<HTMLElement | null>(null);

  // Save previous focus and focus input on mount
  useEffect(() => {
    previousFocusRef.current = document.activeElement as HTMLElement;
    inputRef.current?.focus();
    return () => {
      previousFocusRef.current?.focus();
    };
  }, []);

  // Focus trap: Tab/Shift+Tab cycles within modal
  const handleKeyDown = useCallback(
    (e: React.KeyboardEvent) => {
      if (e.key === "Escape") {
        onCancel();
        return;
      }
      if (e.key !== "Tab" || !modalRef.current) return;

      const focusable = modalRef.current.querySelectorAll<HTMLElement>(
        'button, [href], input, select, textarea, [tabindex]:not([tabindex="-1"])'
      );
      if (focusable.length === 0) return;

      const first = focusable[0];
      const last = focusable[focusable.length - 1];

      if (e.shiftKey) {
        if (document.activeElement === first) {
          e.preventDefault();
          last.focus();
        }
      } else {
        if (document.activeElement === last) {
          e.preventDefault();
          first.focus();
        }
      }
    },
    [onCancel]
  );

  const handleSubmit = () => {
    const trimmed = name.trim();
    if (!trimmed) return;
    onSave({ name: trimmed, color, icon });
  };

  return (
    <div
      className="fixed inset-0 z-50 flex items-center justify-center bg-black/30"
      role="dialog"
      aria-modal="true"
      aria-labelledby="list-modal-title"
      onKeyDown={handleKeyDown}
    >
      <div
        ref={modalRef}
        className="w-[340px] rounded-xl p-5 shadow-xl"
        style={{ backgroundColor: "var(--bg-main)" }}
      >
        {/* Header */}
        <div className="flex items-center justify-between mb-4">
          <h2
            id="list-modal-title"
            className="text-[15px] font-semibold"
            style={{ color: "var(--text-primary)" }}
          >
            {editingList ? "Edit List" : "New List"}
          </h2>
          <button onClick={onCancel} className="p-1 rounded hover:bg-gray-100" aria-label="Close dialog">
            <X size={16} style={{ color: "var(--text-secondary)" }} />
          </button>
        </div>

        {/* Preview icon */}
        <div className="flex justify-center mb-4">
          <div
            className="flex items-center justify-center rounded-full text-white text-lg font-bold"
            style={{
              backgroundColor: color,
              width: "60px",
              height: "60px",
            }}
          >
            {icon === "list.bullet"
              ? "☰"
              : icon === "star"
              ? "★"
              : icon === "heart"
              ? "♥"
              : icon === "cart"
              ? "🛒"
              : icon === "bookmark"
              ? "🔖"
              : icon === "briefcase"
              ? "💼"
              : icon === "book"
              ? "📚"
              : icon === "house"
              ? "🏠"
              : icon === "airplane"
              ? "✈"
              : icon === "gift"
              ? "🎁"
              : icon === "music.note"
              ? "♪"
              : icon === "gamecontroller"
              ? "🎮"
              : "☰"}
          </div>
        </div>

        {/* Name input */}
        <input
          ref={inputRef}
          type="text"
          value={name}
          onChange={(e) => setName(e.target.value)}
          onKeyDown={(e) => e.key === "Enter" && handleSubmit()}
          placeholder="List Name"
          maxLength={100}
          className="w-full text-[13px] leading-8 px-3 rounded-lg mb-4 outline-none"
          style={{
            backgroundColor: "var(--bg-sidebar)",
            color: "var(--text-primary)",
          }}
        />

        {/* Color palette */}
        <div className="mb-3">
          <div
            className="text-[11px] font-medium mb-2"
            style={{ color: "var(--text-secondary)" }}
          >
            Color
          </div>
          <div className="flex flex-wrap gap-2">
            {PRESET_COLORS.map((c) => (
              <button
                key={c}
                onClick={() => setColor(c)}
                className="relative rounded-full transition-transform"
                style={{
                  backgroundColor: c,
                  width: "24px",
                  height: "24px",
                  transform: color === c ? "scale(1.2)" : "scale(1)",
                  boxShadow:
                    color === c ? `0 0 0 2px white, 0 0 0 3.5px ${c}` : "none",
                }}
              />
            ))}
          </div>
        </div>

        {/* Icon grid */}
        <div className="mb-4">
          <div
            className="text-[11px] font-medium mb-2"
            style={{ color: "var(--text-secondary)" }}
          >
            Icon
          </div>
          <div className="grid grid-cols-6 gap-2">
            {PRESET_ICONS.map((ic) => (
              <button
                key={ic}
                onClick={() => setIcon(ic)}
                className="flex items-center justify-center rounded-lg text-sm transition-colors"
                style={{
                  width: "36px",
                  height: "36px",
                  backgroundColor:
                    icon === ic ? color : "var(--bg-sidebar)",
                  color: icon === ic ? "white" : "var(--text-primary)",
                }}
              >
                {ic === "list.bullet"
                  ? "☰"
                  : ic === "star"
                  ? "★"
                  : ic === "heart"
                  ? "♥"
                  : ic === "cart"
                  ? "🛒"
                  : ic === "bookmark"
                  ? "🔖"
                  : ic === "briefcase"
                  ? "💼"
                  : ic === "book"
                  ? "📚"
                  : ic === "house"
                  ? "🏠"
                  : ic === "airplane"
                  ? "✈"
                  : ic === "gift"
                  ? "🎁"
                  : ic === "music.note"
                  ? "♪"
                  : ic === "gamecontroller"
                  ? "🎮"
                  : "☰"}
              </button>
            ))}
          </div>
        </div>

        {/* Actions */}
        <div className="flex justify-end gap-2">
          <button
            onClick={onCancel}
            className="px-4 py-1.5 text-[13px] rounded-lg transition-colors"
            style={{
              backgroundColor: "var(--bg-sidebar)",
              color: "var(--text-primary)",
            }}
          >
            Cancel
          </button>
          <button
            onClick={handleSubmit}
            disabled={!name.trim()}
            className="px-4 py-1.5 text-[13px] rounded-lg text-white transition-colors disabled:opacity-40"
            style={{ backgroundColor: color }}
          >
            {editingList ? "Save" : "Create"}
          </button>
        </div>
      </div>
    </div>
  );
}
