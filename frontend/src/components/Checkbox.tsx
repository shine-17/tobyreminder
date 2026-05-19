"use client";

interface CheckboxProps {
  checked: boolean;
  color: string;
  onChange: () => void;
  label?: string;
}

export default function Checkbox({ checked, color, onChange, label }: CheckboxProps) {
  const ariaLabel = label
    ? `${checked ? "Mark incomplete" : "Mark complete"}: ${label}`
    : checked ? "Mark incomplete" : "Mark complete";

  return (
    <button
      onClick={onChange}
      className="flex items-center justify-center shrink-0 rounded-full transition-all duration-300 ease-out"
      style={{
        width: "20px",
        height: "20px",
        border: checked ? "none" : `1.5px solid ${color}`,
        backgroundColor: checked ? color : "transparent",
        transform: checked ? "scale(1)" : "scale(1)",
      }}
      aria-label={ariaLabel}
    >
      {checked && (
        <svg
          width="10"
          height="10"
          viewBox="0 0 24 24"
          fill="none"
          stroke="white"
          strokeWidth="3"
          strokeLinecap="round"
          strokeLinejoin="round"
        >
          <polyline points="20 6 9 17 4 12" />
        </svg>
      )}
    </button>
  );
}
