"use client";

import { useCallback, useEffect, useRef, useState } from "react";

interface ConfirmDialogProps {
  message: string;
  confirmLabel?: string;
  cancelLabel?: string;
  onConfirm: () => void;
  onCancel: () => void;
}

export default function ConfirmDialog({
  message,
  confirmLabel = "Delete",
  cancelLabel = "Cancel",
  onConfirm,
  onCancel,
}: ConfirmDialogProps) {
  const modalRef = useRef<HTMLDivElement>(null);
  const cancelBtnRef = useRef<HTMLButtonElement>(null);
  const previousFocusRef = useRef<HTMLElement | null>(null);

  useEffect(() => {
    previousFocusRef.current = document.activeElement as HTMLElement;
    cancelBtnRef.current?.focus();
    return () => {
      previousFocusRef.current?.focus();
    };
  }, []);

  const handleKeyDown = useCallback(
    (e: React.KeyboardEvent) => {
      if (e.key === "Escape") {
        onCancel();
        return;
      }
      if (e.key !== "Tab" || !modalRef.current) return;

      const focusable = modalRef.current.querySelectorAll<HTMLElement>(
        'button, [href], input, select, textarea, [tabindex]:not([tabindex="-1"])'
      );
      if (focusable.length === 0) return;

      const first = focusable[0];
      const last = focusable[focusable.length - 1];

      if (e.shiftKey) {
        if (document.activeElement === first) {
          e.preventDefault();
          last.focus();
        }
      } else {
        if (document.activeElement === last) {
          e.preventDefault();
          first.focus();
        }
      }
    },
    [onCancel]
  );

  return (
    <div
      className="fixed inset-0 z-[60] flex items-center justify-center bg-black/30"
      role="dialog"
      aria-modal="true"
      aria-labelledby="confirm-dialog-title"
      onKeyDown={handleKeyDown}
    >
      <div
        ref={modalRef}
        className="w-[300px] rounded-xl p-5 shadow-xl"
        style={{ backgroundColor: "var(--bg-main)" }}
      >
        <p
          id="confirm-dialog-title"
          className="text-[13px] leading-5 mb-5 text-center"
          style={{ color: "var(--text-primary)" }}
        >
          {message}
        </p>
        <div className="flex justify-end gap-2">
          <button
            ref={cancelBtnRef}
            onClick={onCancel}
            className="px-4 py-1.5 text-[13px] rounded-lg transition-colors"
            style={{
              backgroundColor: "var(--bg-sidebar)",
              color: "var(--text-primary)",
            }}
          >
            {cancelLabel}
          </button>
          <button
            onClick={onConfirm}
            className="px-4 py-1.5 text-[13px] rounded-lg text-white transition-colors"
            style={{ backgroundColor: "var(--list-red, #FF3B30)" }}
          >
            {confirmLabel}
          </button>
        </div>
      </div>
    </div>
  );
}
