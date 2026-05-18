"use client";

import { useEffect } from "react";

export default function Error({
  error,
  reset,
}: {
  error: Error & { digest?: string };
  reset: () => void;
}) {
  useEffect(() => {
    console.error("Application error:", error);
  }, [error]);

  return (
    <div className="flex h-full items-center justify-center">
      <div className="text-center space-y-4">
        <h2
          className="text-lg font-semibold"
          style={{ color: "var(--text-primary)" }}
        >
          Something went wrong
        </h2>
        <p
          className="text-sm"
          style={{ color: "var(--text-secondary)" }}
        >
          An unexpected error occurred. Please try again.
        </p>
        <button
          onClick={reset}
          className="px-4 py-2 rounded-lg text-sm font-medium text-white"
          style={{ backgroundColor: "var(--smart-today)" }}
        >
          Try again
        </button>
      </div>
    </div>
  );
}
