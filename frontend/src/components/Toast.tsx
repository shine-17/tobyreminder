"use client";

import { useEffect, useState, useCallback, useRef } from "react";

export interface ToastMessage {
  id: number;
  text: string;
  type: "error" | "success" | "info";
}

let toastId = 0;
let addToastCallback: ((msg: Omit<ToastMessage, "id">) => void) | null = null;

/** Global function to show a toast from anywhere */
export function showToast(text: string, type: ToastMessage["type"] = "error") {
  addToastCallback?.({ text, type });
}

export default function ToastContainer() {
  const [toasts, setToasts] = useState<ToastMessage[]>([]);
  const timersRef = useRef<Map<number, NodeJS.Timeout>>(new Map());

  const removeToast = useCallback((id: number) => {
    setToasts((prev) => prev.filter((t) => t.id !== id));
    const timer = timersRef.current.get(id);
    if (timer) {
      clearTimeout(timer);
      timersRef.current.delete(id);
    }
  }, []);

  const addToast = useCallback(
    (msg: Omit<ToastMessage, "id">) => {
      const id = ++toastId;
      setToasts((prev) => [...prev.slice(-4), { ...msg, id }]);
      const timer = setTimeout(() => removeToast(id), 4000);
      timersRef.current.set(id, timer);
    },
    [removeToast]
  );

  useEffect(() => {
    addToastCallback = addToast;
    return () => {
      addToastCallback = null;
    };
  }, [addToast]);

  if (toasts.length === 0) return null;

  return (
    <div className="fixed bottom-4 right-4 z-[100] flex flex-col gap-2">
      {toasts.map((toast) => (
        <div
          key={toast.id}
          className="flex items-center gap-2 px-4 py-2.5 rounded-lg shadow-lg text-[13px] text-white animate-slide-in"
          style={{
            backgroundColor:
              toast.type === "error"
                ? "var(--list-red, #FF3B30)"
                : toast.type === "success"
                ? "#34C759"
                : "#007AFF",
          }}
          role="alert"
        >
          <span className="flex-1">{toast.text}</span>
          <button
            onClick={() => removeToast(toast.id)}
            className="ml-2 opacity-70 hover:opacity-100"
            aria-label="Dismiss"
          >
            &times;
          </button>
        </div>
      ))}
    </div>
  );
}
