"use client";

import { useState, useRef, useEffect } from "react";
import { ReminderList } from "@/types";
import { List } from "lucide-react";

interface SidebarListItemProps {
  list: ReminderList;
  isSelected: boolean;
  onClick: () => void;
  onEdit: () => void;
  onDelete: () => void;
}

export default function SidebarListItem({
  list,
  isSelected,
  onClick,
  onEdit,
  onDelete,
}: SidebarListItemProps) {
  const [showMenu, setShowMenu] = useState(false);
  const [menuPos, setMenuPos] = useState({ x: 0, y: 0 });
  const menuRef = useRef<HTMLDivElement>(null);

  useEffect(() => {
    const handleClick = (e: MouseEvent) => {
      if (menuRef.current && !menuRef.current.contains(e.target as Node)) {
        setShowMenu(false);
      }
    };
    if (showMenu) document.addEventListener("mousedown", handleClick);
    return () => document.removeEventListener("mousedown", handleClick);
  }, [showMenu]);

  const handleContextMenu = (e: React.MouseEvent) => {
    e.preventDefault();
    setMenuPos({ x: e.clientX, y: e.clientY });
    setShowMenu(true);
  };

  return (
    <>
      <button
        onClick={onClick}
        onContextMenu={handleContextMenu}
        className="flex items-center gap-2.5 w-full rounded-md px-2 py-1.5 text-left transition-colors duration-150"
        style={{
          backgroundColor: isSelected ? "rgba(0,0,0,0.08)" : "transparent",
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
          title={list.name}
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

      {/* Context menu */}
      <div
        ref={menuRef}
        className="fixed z-50 rounded-lg py-1 shadow-xl min-w-[140px]"
        style={{
          left: menuPos.x,
          top: menuPos.y,
          backgroundColor: "var(--bg-main)",
          border: "1px solid var(--bg-reminder-active)",
          visibility: showMenu ? "visible" : "hidden",
          opacity: showMenu ? 1 : 0,
          pointerEvents: showMenu ? "auto" : "none",
        }}
      >
          <button
            onClick={() => {
              setShowMenu(false);
              onEdit();
            }}
            className="w-full text-left px-3 py-1.5 text-[13px] hover:bg-gray-100 transition-colors"
            style={{ color: "var(--text-primary)" }}
          >
            Edit List
          </button>
          <button
            onClick={() => {
              setShowMenu(false);
              onDelete();
            }}
            className="w-full text-left px-3 py-1.5 text-[13px] hover:bg-red-50 transition-colors"
            style={{ color: "var(--list-red)" }}
          >
            Delete List
          </button>
        </div>
    </>
  );
}
