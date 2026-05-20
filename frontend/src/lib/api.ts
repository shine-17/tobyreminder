import { Reminder, ReminderList } from "@/types";
import { z } from "zod";
import {
  ReminderListSchema,
  ReminderListArraySchema,
  ReminderSchema,
  ReminderArraySchema,
} from "./schemas";

const BASE = process.env.NEXT_PUBLIC_API_BASE ?? "/api";

async function fetchJson<T>(
  url: string,
  schema: z.ZodType<T>,
  init?: RequestInit
): Promise<T> {
  const res = await fetch(url, init);
  if (!res.ok) {
    throw new Error(`API error: ${res.status} ${res.statusText}`);
  }
  const json = await res.json();
  return schema.parse(json);
}

async function fetchVoid(url: string, init?: RequestInit): Promise<void> {
  const res = await fetch(url, init);
  if (!res.ok) {
    throw new Error(`API error: ${res.status} ${res.statusText}`);
  }
}

// --- Lists ---

export function getLists(signal?: AbortSignal): Promise<ReminderList[]> {
  return fetchJson(`${BASE}/lists`, ReminderListArraySchema, { signal });
}

export function getListById(
  id: number,
  signal?: AbortSignal
): Promise<ReminderList> {
  return fetchJson(`${BASE}/lists/${id}`, ReminderListSchema, { signal });
}

export function createList(data: {
  name: string;
  color: string;
  icon?: string | null;
}): Promise<ReminderList> {
  return fetchJson(`${BASE}/lists`, ReminderListSchema, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(data),
  });
}

export function updateList(
  id: number,
  data: { name?: string; color?: string; icon?: string | null }
): Promise<ReminderList> {
  return fetchJson(`${BASE}/lists/${id}`, ReminderListSchema, {
    method: "PATCH",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(data),
  });
}

export function deleteList(id: number): Promise<void> {
  return fetchVoid(`${BASE}/lists/${id}`, { method: "DELETE" });
}

// --- Reminders ---

export function getReminders(
  listId: number,
  includeCompleted = false,
  signal?: AbortSignal
): Promise<Reminder[]> {
  const params = new URLSearchParams({
    listId: String(listId),
    includeCompleted: String(includeCompleted),
  });
  return fetchJson(`${BASE}/reminders?${params}`, ReminderArraySchema, {
    signal,
  });
}

export function getReminderById(
  id: number,
  signal?: AbortSignal
): Promise<Reminder> {
  return fetchJson(`${BASE}/reminders/${id}`, ReminderSchema, { signal });
}

export function createReminder(data: {
  title: string;
  memo?: string | null;
  listId: number;
}): Promise<Reminder> {
  return fetchJson(`${BASE}/reminders`, ReminderSchema, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(data),
  });
}

export function updateReminder(
  id: number,
  data: { title?: string; memo?: string | null }
): Promise<Reminder> {
  return fetchJson(`${BASE}/reminders/${id}`, ReminderSchema, {
    method: "PATCH",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(data),
  });
}

export function toggleComplete(id: number): Promise<Reminder> {
  return fetchJson(`${BASE}/reminders/${id}/complete`, ReminderSchema, {
    method: "PATCH",
  });
}

export function deleteReminder(id: number): Promise<void> {
  return fetchVoid(`${BASE}/reminders/${id}`, { method: "DELETE" });
}
