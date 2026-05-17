"use client";

import { useEffect, useState, useCallback } from "react";
import { ReminderList, Reminder } from "@/types";
import { getLists, getReminders } from "@/lib/api";
import Sidebar from "@/components/Sidebar";
import ReminderListView from "@/components/ReminderListView";

export default function Home() {
  const [lists, setLists] = useState<ReminderList[]>([]);
  const [selectedListId, setSelectedListId] = useState<number | null>(null);
  const [reminders, setReminders] = useState<Reminder[]>([]);
  const [loading, setLoading] = useState(true);

  // Fetch lists
  useEffect(() => {
    getLists()
      .then((data) => {
        setLists(data);
        if (data.length > 0 && selectedListId === null) {
          setSelectedListId(data[0].id);
        }
      })
      .catch(console.error)
      .finally(() => setLoading(false));
  }, []);

  // Fetch reminders when selected list changes
  const fetchReminders = useCallback(async () => {
    if (selectedListId === null) return;
    try {
      const data = await getReminders(selectedListId);
      setReminders(data);
    } catch (err) {
      console.error(err);
    }
  }, [selectedListId]);

  useEffect(() => {
    fetchReminders();
  }, [fetchReminders]);

  const selectedList = lists.find((l) => l.id === selectedListId) ?? null;

  if (loading) {
    return (
      <div className="flex h-full items-center justify-center">
        <div className="text-text-secondary text-sm">Loading...</div>
      </div>
    );
  }

  return (
    <div className="flex h-full">
      <Sidebar
        lists={lists}
        selectedListId={selectedListId}
        onSelectList={setSelectedListId}
      />
      <main className="flex-1 overflow-y-auto bg-bg-main">
        {selectedList ? (
          <ReminderListView
            list={selectedList}
            reminders={reminders}
            onRefresh={fetchReminders}
          />
        ) : (
          <div className="flex h-full items-center justify-center text-text-tertiary text-sm">
            Select a list
          </div>
        )}
      </main>
    </div>
  );
}
