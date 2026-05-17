import { Reminder, ReminderList } from "@/types";

const BASE = "/api";

async function fetchJson<T>(url: string, init?: RequestInit): Promise<T> {
  const res = await fetch(url, init);
  if (!res.ok) {
    throw new Error(`API error: ${res.status} ${res.statusText}`);
  }
  if (res.status === 204) return undefined as T;
  return res.json();
}

// --- Lists ---

export function getLists(): Promise<ReminderList[]> {
  return fetchJson(`${BASE}/lists`);
}

export function getListById(id: number): Promise<ReminderList> {
  return fetchJson(`${BASE}/lists/${id}`);
}

export function createList(data: {
  name: string;
  color: string;
  icon?: string | null;
}): Promise<ReminderList> {
  return fetchJson(`${BASE}/lists`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(data),
  });
}

export function updateList(
  id: number,
  data: { name?: string; color?: string; icon?: string | null }
): Promise<ReminderList> {
  return fetchJson(`${BASE}/lists/${id}`, {
    method: "PATCH",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(data),
  });
}

export function deleteList(id: number): Promise<void> {
  return fetchJson(`${BASE}/lists/${id}`, { method: "DELETE" });
}

// --- Reminders ---

export function getReminders(
  listId: number,
  includeCompleted = false
): Promise<Reminder[]> {
  const params = new URLSearchParams({
    listId: String(listId),
    includeCompleted: String(includeCompleted),
  });
  return fetchJson(`${BASE}/reminders?${params}`);
}

export function getReminderById(id: number): Promise<Reminder> {
  return fetchJson(`${BASE}/reminders/${id}`);
}

export function createReminder(data: {
  title: string;
  memo?: string | null;
  listId: number;
}): Promise<Reminder> {
  return fetchJson(`${BASE}/reminders`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(data),
  });
}

export function updateReminder(
  id: number,
  data: { title?: string; memo?: string | null }
): Promise<Reminder> {
  return fetchJson(`${BASE}/reminders/${id}`, {
    method: "PATCH",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(data),
  });
}

export function toggleComplete(id: number): Promise<Reminder> {
  return fetchJson(`${BASE}/reminders/${id}/complete`, { method: "PATCH" });
}

export function deleteReminder(id: number): Promise<void> {
  return fetchJson(`${BASE}/reminders/${id}`, { method: "DELETE" });
}
