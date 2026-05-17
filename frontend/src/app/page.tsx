"use client";

import { useEffect, useState, useCallback } from "react";
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

export default function Home() {
  const [lists, setLists] = useState<ReminderList[]>([]);
  const [selectedListId, setSelectedListId] = useState<number | null>(null);
  const [reminders, setReminders] = useState<Reminder[]>([]);
  const [loading, setLoading] = useState(true);

  // Modal state
  const [showListModal, setShowListModal] = useState(false);
  const [editingList, setEditingList] = useState<ReminderList | null>(null);

  // Fetch lists
  const fetchLists = useCallback(async () => {
    try {
      const data = await getLists();
      setLists(data);
      return data;
    } catch (err) {
      console.error(err);
      return [];
    }
  }, []);

  useEffect(() => {
    fetchLists().then((data) => {
      if (data.length > 0 && selectedListId === null) {
        setSelectedListId(data[0].id);
      }
      setLoading(false);
    });
  }, []);

  // Fetch reminders when selected list changes
  const fetchReminders = useCallback(async () => {
    if (selectedListId === null) {
      setReminders([]);
      return;
    }
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

  // Refresh both lists and reminders
  const refreshAll = useCallback(async () => {
    await fetchLists();
    await fetchReminders();
  }, [fetchLists, fetchReminders]);

  // List CRUD handlers
  const handleAddList = () => {
    setEditingList(null);
    setShowListModal(true);
  };

  const handleEditList = (list: ReminderList) => {
    setEditingList(list);
    setShowListModal(true);
  };

  const handleDeleteList = async (list: ReminderList) => {
    if (!confirm(`Delete "${list.name}" and all its reminders?`)) return;
    try {
      await deleteList(list.id);
      const updated = await fetchLists();
      if (selectedListId === list.id) {
        setSelectedListId(updated.length > 0 ? updated[0].id : null);
      }
    } catch (err) {
      console.error(err);
    }
  };

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
      console.error(err);
    }
  };

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
