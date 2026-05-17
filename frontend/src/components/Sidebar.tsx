"use client";

import { Plus } from "lucide-react";
import { ReminderList } from "@/types";
import SmartListCards from "./SmartListCards";
import SidebarListItem from "./SidebarListItem";

interface SidebarProps {
  lists: ReminderList[];
  selectedListId: number | null;
  onSelectList: (id: number) => void;
  onAddList: () => void;
  onEditList: (list: ReminderList) => void;
  onDeleteList: (list: ReminderList) => void;
}

export default function Sidebar({
  lists,
  selectedListId,
  onSelectList,
  onAddList,
  onEditList,
  onDeleteList,
}: SidebarProps) {
  const totalCount = lists.reduce((sum, l) => sum + l.reminderCount, 0);

  return (
    <aside
      className="flex flex-col w-[280px] h-full overflow-y-auto"
      style={{ backgroundColor: "var(--bg-sidebar)" }}
    >
      {/* Search placeholder */}
      <div className="px-3 pt-3 pb-1">
        <div
          className="flex items-center gap-1.5 rounded-lg px-2 py-1.5 text-xs"
          style={{
            backgroundColor: "rgba(0,0,0,0.06)",
            color: "var(--text-tertiary)",
          }}
        >
          <svg
            width="12"
            height="12"
            viewBox="0 0 24 24"
            fill="none"
            stroke="currentColor"
            strokeWidth="2"
            strokeLinecap="round"
            strokeLinejoin="round"
          >
            <circle cx="11" cy="11" r="8" />
            <path d="m21 21-4.3-4.3" />
          </svg>
          Search
        </div>
      </div>

      {/* Smart list cards */}
      <SmartListCards allCount={totalCount} onSelectAll={() => {}} />

      {/* Section header */}
      <div
        className="px-4 pt-4 pb-1 text-[11px] font-semibold uppercase tracking-wide"
        style={{ color: "var(--text-secondary)" }}
      >
        My Lists
      </div>

      {/* User lists */}
      <div className="flex flex-col gap-0.5 px-2 flex-1">
        {lists.map((list) => (
          <SidebarListItem
            key={list.id}
            list={list}
            isSelected={list.id === selectedListId}
            onClick={() => onSelectList(list.id)}
            onEdit={() => onEditList(list)}
            onDelete={() => onDeleteList(list)}
          />
        ))}
      </div>

      {/* Add List button */}
      <div className="px-3 py-3">
        <button
          onClick={onAddList}
          className="flex items-center gap-1.5 text-[13px] font-medium transition-colors"
          style={{ color: "var(--text-secondary)" }}
        >
          <Plus size={14} />
          Add List
        </button>
      </div>
    </aside>
  );
}
