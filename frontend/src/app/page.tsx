"use client";

import { useEffect, useState, useCallback, useRef, useMemo } from "react";
import { ReminderList, Reminder } from "@/types";
import {
  getLists,
  getReminders,
  createList,
  updateList,
  deleteList,
} from "@/lib/api";
import Sidebar from "@/components/Sidebar";
import ReminderListView from "@/components/ReminderListView";
import ListModal from "@/components/ListModal";
import ToastContainer, { showToast } from "@/components/Toast";

export default function Home() {
  const [lists, setLists] = useState<ReminderList[]>([]);
  const [selectedListId, setSelectedListId] = useState<number | null>(null);
  const [reminders, setReminders] = useState<Reminder[]>([]);
  const [loading, setLoading] = useState(true);

  // Modal state
  const [showListModal, setShowListModal] = useState(false);
  const [editingList, setEditingList] = useState<ReminderList | null>(null);

  // Fetch lists
  const fetchLists = useCallback(async (signal?: AbortSignal) => {
    try {
      const data = await getLists(signal);
      setLists(data);
      return data;
    } catch (err) {
      if (err instanceof DOMException && err.name === "AbortError") return [];
      showToast("Failed to load lists");
      return [];
    }
  }, []);

  // Initial load — intentionally mount-once
  const initializedRef = useRef(false);
  useEffect(() => {
    if (initializedRef.current) return;
    initializedRef.current = true;

    const controller = new AbortController();
    fetchLists(controller.signal).then((data) => {
      if (data.length > 0) {
        setSelectedListId((prev) => prev ?? data[0].id);
      }
      setLoading(false);
    });
    return () => controller.abort();
  }, [fetchLists]);

  // Fetch reminders when selected list changes
  const fetchReminders = useCallback(
    async (signal?: AbortSignal) => {
      if (selectedListId === null) {
        setReminders([]);
        return;
      }
      try {
        const data = await getReminders(selectedListId, false, signal);
        setReminders(data);
      } catch (err) {
        if (err instanceof DOMException && err.name === "AbortError") return;
        showToast("Failed to load reminders");
      }
    },
    [selectedListId]
  );

  useEffect(() => {
    const controller = new AbortController();
    fetchReminders(controller.signal);
    return () => controller.abort();
  }, [fetchReminders]);

  // Refresh both lists and reminders — use ref to avoid dependency churn
  const fetchRemindersRef = useRef(fetchReminders);
  fetchRemindersRef.current = fetchReminders;

  const refreshAll = useCallback(async () => {
    await fetchLists();
    await fetchRemindersRef.current();
  }, [fetchLists]);

  // List CRUD handlers
  const handleAddList = () => {
    setEditingList(null);
    setShowListModal(true);
  };

  const handleEditList = useCallback((list: ReminderList) => {
    setEditingList(list);
    setShowListModal(true);
  }, []);

  const handleDeleteList = useCallback(
    async (list: ReminderList) => {
      if (!confirm(`Delete "${list.name}" and all its reminders?`)) return;
      try {
        await deleteList(list.id);
        const updated = await fetchLists();
        if (selectedListId === list.id) {
          setSelectedListId(updated.length > 0 ? updated[0].id : null);
        }
      } catch (err) {
        showToast("Failed to delete list");
      }
    },
    [fetchLists, selectedListId]
  );

  const handleSaveList = async (data: {
    name: string;
    color: string;
    icon: string;
  }) => {
    try {
      if (editingList) {
        await updateList(editingList.id, data);
      } else {
        const created = await createList(data);
        setSelectedListId(created.id);
      }
      await fetchLists();
      setShowListModal(false);
    } catch (err) {
      showToast("Failed to save list");
    }
  };

  const selectedList = useMemo(
    () => lists.find((l) => l.id === selectedListId) ?? null,
    [lists, selectedListId]
  );

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
        onAddList={handleAddList}
        onEditList={handleEditList}
        onDeleteList={handleDeleteList}
      />
      <main className="flex-1 overflow-y-auto bg-bg-main">
        {selectedList ? (
          <ReminderListView
            list={selectedList}
            reminders={reminders}
            onRefresh={refreshAll}
          />
        ) : (
          <div className="flex h-full items-center justify-center text-text-tertiary text-sm">
            Select a list
          </div>
        )}
      </main>

      <ToastContainer />

      {/* List Modal */}
      {showListModal && (
        <ListModal
          editingList={editingList}
          onSave={handleSaveList}
          onCancel={() => setShowListModal(false)}
        />
      )}
    </div>
  );
}
