import { z } from "zod";

export const ReminderListSchema = z.object({
  id: z.number(),
  name: z.string(),
  color: z.string(),
  icon: z.string().nullable(),
  displayOrder: z.number(),
  isDefault: z.boolean(),
  reminderCount: z.number(),
  createdAt: z.string(),
  updatedAt: z.string(),
});

export const ReminderSchema = z.object({
  id: z.number(),
  title: z.string(),
  memo: z.string().nullable(),
  completed: z.boolean(),
  completedAt: z.string().nullable(),
  displayOrder: z.number(),
  listId: z.number(),
  createdAt: z.string(),
  updatedAt: z.string(),
});

export const ReminderListArraySchema = z.array(ReminderListSchema);
export const ReminderArraySchema = z.array(ReminderSchema);
