export interface ReminderList {
  id: number;
  name: string;
  color: string;
  icon: string | null;
  displayOrder: number;
  isDefault: boolean;
  reminderCount: number;
  createdAt: string;
  updatedAt: string;
}

export interface Reminder {
  id: number;
  title: string;
  memo: string | null;
  completed: boolean;
  completedAt: string | null;
  displayOrder: number;
  listId: number;
  createdAt: string;
  updatedAt: string;
}
