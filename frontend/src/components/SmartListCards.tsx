"use client";

import {
  Calendar,
  CalendarDays,
  Inbox,
  Flag,
} from "lucide-react";

interface SmartListCardsProps {
  allCount: number;
  onSelectAll: () => void;
}

interface CardData {
  label: string;
  icon: React.ReactNode;
  color: string;
  count: number;
  enabled: boolean;
}

export default function SmartListCards({
  allCount,
  onSelectAll,
}: SmartListCardsProps) {
  const cards: CardData[] = [
    {
      label: "Today",
      icon: <Calendar size={16} color="white" />,
      color: "var(--smart-today)",
      count: 0,
      enabled: false,
    },
    {
      label: "Scheduled",
      icon: <CalendarDays size={16} color="white" />,
      color: "var(--smart-scheduled)",
      count: 0,
      enabled: false,
    },
    {
      label: "All",
      icon: <Inbox size={16} color="white" />,
      color: "var(--smart-all)",
      count: allCount,
      enabled: true,
    },
    {
      label: "Flagged",
      icon: <Flag size={16} color="white" />,
      color: "var(--smart-flagged)",
      count: 0,
      enabled: false,
    },
  ];

  return (
    <div className="grid grid-cols-2 gap-2 px-3 pt-2 pb-1">
      {cards.map((card) => (
        <button
          key={card.label}
          onClick={card.enabled ? onSelectAll : undefined}
          disabled={!card.enabled}
          className="flex flex-col justify-between rounded-xl p-2.5 text-left transition-colors duration-200"
          style={{
            backgroundColor: "var(--bg-smart-card)",
            opacity: card.enabled ? 1 : 0.6,
            boxShadow: "0 1px 3px rgba(0,0,0,0.08)",
            minHeight: "70px",
          }}
        >
          <div className="flex items-center justify-between">
            <div
              className="flex items-center justify-center rounded-full"
              style={{
                backgroundColor: card.color,
                width: "28px",
                height: "28px",
              }}
            >
              {card.icon}
            </div>
            <span
              className="text-[22px] font-bold"
              style={{ color: "var(--text-primary)" }}
            >
              {card.count}
            </span>
          </div>
          <span
            className="text-[11px] font-medium mt-1"
            style={{ color: "var(--text-secondary)" }}
          >
            {card.label}
          </span>
        </button>
      ))}
    </div>
  );
}
