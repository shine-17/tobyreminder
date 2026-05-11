# Toby Reminder — Spec

Apple Reminders 앱의 Web 버전 클론 프로젝트.

## Tech Stack

| Layer | Technology |
|-------|-----------|
| Backend | Spring Boot 4.0.6, Spring Data JPA, H2 (in-memory) |
| Frontend | Next.js (latest), TypeScript, Tailwind CSS |
| API | REST JSON |

---

## 1. 핵심 기능

### 1.1 리마인더 (Reminder)

- 제목 (필수)
- 메모 (선택)
- 마감일 / 시간 (선택)
- 우선순위: 없음 / 낮음 / 보통 / 높음
- 플래그 on/off
- 완료 체크박스
- 서브태스크 (1단계 중첩)
- 태그 (복수 지정 가능, `#태그명`)

### 1.2 리스트 (List)

- 사용자 정의 리스트 생성/수정/삭제
- 리스트별 아이콘 + 컬러 지정 (12색, 아이콘 프리셋)
- 리스트 내 섹션으로 그룹핑
- 리스트 순서 드래그 & 드롭 정렬
- 기본 리스트 설정 (새 리마인더의 기본 목적지)

### 1.3 스마트 리스트 (Smart List) — 읽기 전용, 자동 집계

| 이름 | 필터 조건 |
|------|----------|
| Today | 오늘 마감 + 지난 미완료 |
| Scheduled | 마감일이 설정된 항목 |
| All | 전체 미완료 |
| Flagged | 플래그 켜진 항목 |
| Completed | 완료된 항목 |

### 1.4 태그 (Tag)

- 리마인더에 태그 추가/제거
- 사이드바에 사용 중인 태그 목록 표시
- 태그 클릭 시 해당 태그의 리마인더 필터링

---

## 2. UI/UX 디자인 스펙 (Apple Reminders 스타일)

### 2.1 전체 레이아웃

```
+----------------------------------------------------------------------+
|  Sidebar (280px)                 |  Main Content                     |
|                                  |                                   |
|  [🔍 Search                   ]  |  📋 리스트명              12 items |
|                                  |  ─────────────────────────────── |
|  ┌─────────┐  ┌─────────┐       |                                   |
|  │ 📅 Today │  │ 📆 Sched│       |  ○  Reminder Title        !!! 🚩 |
|  │       3  │  │      5  │       |     memo preview text...          |
|  ├─────────┤  ├─────────┤       |     due: May 12  #tag1 #tag2      |
|  │ 📁 All  │  │ 🚩 Flag │       |                                   |
|  │      12  │  │      2  │       |  ○  Another Reminder              |
|  └─────────┘  └─────────┘       |     ○  subtask 1                  |
|                                  |     ○  subtask 2                  |
|  ── iCloud ──────────────        |                                   |
|  🔴 Personal               4    |  ── Section Name ──────────────   |
|  🔵 Work                   6    |                                   |
|  🟢 Shopping               2    |  ○  Section Reminder              |
|                                  |                                   |
|  ── Tags ────────────────        |  + Add Reminder                   |
|  #errands  #health  #work        |                                   |
|                                  |                                   |
|  [+ Add List]                    |                                   |
+----------------------------------------------------------------------+
```

### 2.2 디자인 토큰

#### 컬러 시스템

```
/* 배경 */
--bg-sidebar:         #F2F1F6    /* 사이드바 배경 (연한 회색-보라) */
--bg-main:            #FFFFFF    /* 메인 콘텐츠 배경 */
--bg-smart-card:      #FFFFFF    /* 스마트 리스트 카드 배경 */
--bg-reminder-hover:  #F5F5F7    /* 리마인더 행 hover */
--bg-reminder-active: #E8E8ED    /* 리마인더 행 선택(편집) */

/* 텍스트 */
--text-primary:       #1D1D1F    /* 제목, 본문 */
--text-secondary:     #86868B    /* 메모, 부가정보 */
--text-tertiary:      #AEAEB2    /* placeholder, 비활성 */

/* 스마트 리스트 아이콘 색상 */
--smart-today:        #007AFF    /* 파란색 */
--smart-scheduled:    #FF3B30    /* 빨간색 */
--smart-all:          #5856D6    /* 보라색 (인디고) */
--smart-flagged:      #FF9500    /* 주황색 */
--smart-completed:    #8E8E93    /* 회색 */

/* 리스트 프리셋 12색 */
--list-red:           #FF3B30
--list-orange:        #FF9500
--list-yellow:        #FFCC00
--list-green:         #34C759
--list-teal:          #5AC8FA
--list-blue:          #007AFF
--list-indigo:        #5856D6
--list-purple:        #AF52DE
--list-pink:          #FF2D55
--list-brown:         #A2845E
--list-gray:          #8E8E93
--list-darkgray:      #636366

/* 기능색 */
--flag-orange:        #FF9500    /* 플래그 아이콘 */
--priority-high:      #FF3B30    /* !!! 빨간색 */
--priority-medium:    #FF9500    /* !! 주황색 */
--priority-low:       #007AFF    /* ! 파란색 */
--checkbox-complete:  #86868B    /* 완료 시 채워지는 원 */
--tag-bg:             #E8E8ED    /* 태그 배경 */
--tag-text:           #636366    /* 태그 텍스트 */
```

#### 타이포그래피 (SF Pro / system-ui)

```
--font-family:        -apple-system, BlinkMacSystemFont, "SF Pro Text", system-ui, sans-serif

/* Smart List 카드 */
--smart-count:        28px / bold       /* 카드 내 숫자 */
--smart-label:        11px / medium     /* 카드 라벨 (Today, Scheduled...) */

/* Sidebar */
--sidebar-section:    11px / semibold / uppercase / --text-secondary  /* iCloud, Tags 헤더 */
--sidebar-list:       13px / regular    /* 리스트명 */
--sidebar-count:      13px / regular / --text-secondary  /* 리마인더 카운트 */

/* Main Content */
--list-title:         22px / bold       /* 선택된 리스트 제목 (리스트 컬러 적용) */
--reminder-title:     13px / regular    /* 리마인더 제목 */
--reminder-memo:      11px / regular / --text-secondary  /* 메모 미리보기 */
--reminder-meta:      11px / regular / --text-secondary  /* 날짜, 태그 */
--section-header:     13px / bold / --text-secondary     /* 섹션 구분선 */
--add-reminder:       13px / regular / --text-secondary  /* "+ Add Reminder" */
```

#### 간격 및 크기

```
--sidebar-width:      280px
--sidebar-padding:    12px
--smart-card-size:    약 130px x 80px (2x2 그리드)
--smart-card-gap:     8px
--smart-card-radius:  12px

--list-row-height:    32px          /* 사이드바 리스트 항목 */
--list-icon-size:     22px          /* 리스트 아이콘 (원형 배경 + 아이콘) */
--list-row-padding:   8px 12px
--list-row-radius:    6px           /* hover/selected 배경 */

--reminder-row-padding:   10px 16px
--reminder-min-height:    36px
--checkbox-size:          20px      /* 원형 체크박스 */
--checkbox-border:        1.5px solid --text-tertiary
--subtask-indent:         28px      /* 서브태스크 들여쓰기 */

--section-divider-margin: 16px 0
--content-max-width:      640px     /* 메인 콘텐츠 최대 너비 */
```

### 2.3 컴포넌트 상세

#### Smart List 카드 (2x2 그리드)

```
┌──────────────────┐
│  (●) 아이콘  숫자 │   ← 아이콘: 28px 원형 배경 + 흰색 SF Symbol
│  라벨             │   ← 라벨: "Today", "Scheduled" 등
└──────────────────┘
```

- 사이드바 상단에 2x2 그리드로 배치 (Today, Scheduled / All, Flagged)
- 각 카드: 흰색 배경, `border-radius: 12px`, 미세한 `box-shadow`
- 아이콘: 해당 스마트 리스트 색상의 원형 배경 + 흰색 아이콘
- 카운트 숫자: 우측 상단에 bold로 표시
- 선택 시: 카드 전체가 해당 스마트 리스트 색상으로 채워지고, 텍스트 흰색

#### 사이드바 리스트 항목

```
  🔴 Personal                    4
  ^^                              ^
  컬러 원형(22px) + 아이콘       카운트(회색)
```

- 왼쪽: 리스트 컬러 배경의 원형 아이콘 (22px)
- 가운데: 리스트 이름 (13px)
- 오른쪽: 미완료 리마인더 수 (회색)
- hover: 연한 회색 배경 (`border-radius: 6px`)
- selected: 진한 회색 배경, 리스트 이름 볼드 처리

#### 체크박스 (Reminder)

```
미완료:  ○   22px 원형, 1.5px 테두리 (리스트 컬러)
완료:    ●   22px 원형, 리스트 컬러로 채움
```

- 미완료: 빈 원 (테두리 색 = 해당 리스트 컬러)
- 클릭 시: 원이 리스트 컬러로 채워지는 애니메이션 (scale 0→1, 0.3s ease)
- 완료 후: 제목에 `text-decoration: line-through`, 색상 `--text-tertiary`
- 0.5초 후 리스트에서 fade-out (completed 리스트로 이동)

#### 리마인더 행

```
○  Buy groceries                              !!! 🚩
   Pick up milk and eggs                              
   May 12, 2:00 PM   #shopping  #errands              
```

- **1행**: 체크박스 + 제목 + (우선순위 아이콘) + (플래그 아이콘)
- **2행**: 메모 미리보기 (1줄, `text-overflow: ellipsis`)
- **3행**: 마감일시 + 태그 목록
- 우선순위 표시: `!`(파랑) / `!!`(주황) / `!!!`(빨강), 제목 좌측에 표시
- 플래그: 🚩 주황색 아이콘, 행 우측 끝
- 태그: 작은 라운드 pill (`#tag`, 회색 배경)

#### 리마인더 인라인 편집 (확장)

리마인더 클릭 시 행이 확장되어 편집 모드:

```
┌─────────────────────────────────────────┐
│  ○  [제목 입력 필드                    ]│
│     [메모 입력 필드                    ]│
│                                         │
│  📅 날짜    [날짜 선택]  🕐 [시간 선택]  │
│  🏷️ 태그    [태그 입력, 자동완성]       │
│  ⚑ 우선순위 [없음 ▾]                   │
│  🚩 플래그   [토글 스위치]              │
│                                         │
│  ── Subtasks ──                         │
│  ○ [서브태스크 1]              ✕        │
│  ○ [서브태스크 2]              ✕        │
│  + Add Subtask                          │
└─────────────────────────────────────────┘
```

- 배경: `--bg-reminder-active` 또는 약간 elevation
- 제목/메모: borderless 인라인 텍스트 필드
- 날짜: 캘린더 date picker 팝오버
- 태그: 입력 시 기존 태그 자동완성 드롭다운
- 외부 클릭 시 접히며 자동 저장

#### 리스트 생성/편집 모달

```
┌─────────────────────────────────────┐
│        New List                     │
│                                     │
│  ┌──────────────────────────────┐   │
│  │      [🔴 큰 원형 아이콘]     │   │
│  └──────────────────────────────┘   │
│                                     │
│  [리스트 이름 입력 필드           ]  │
│                                     │
│  컬러 선택:                         │
│  🔴 🟠 🟡 🟢 🩵 🔵 🟣 💜 💗 🟤 ⚪ ⚫ │
│                                     │
│  아이콘 선택:                       │
│  📋 📝 📌 🎯 🛒 💼 📚 🏠 ✈️ ...    │
│                                     │
│  [Cancel]              [OK]         │
└─────────────────────────────────────┘
```

- 상단: 선택한 컬러+아이콘의 큰 미리보기 (60px 원)
- 컬러: 12색 원형 팔레트, 선택 시 체크 표시
- 아이콘: 그리드 형태의 아이콘 프리셋 목록

### 2.4 애니메이션 & 트랜지션

| 요소 | 애니메이션 | 시간 |
|------|-----------|------|
| 체크박스 완료 | 원 채우기 (scale 0→1) | 0.3s ease-out |
| 완료 리마인더 | fade-out + 높이 collapse | 0.5s ease |
| 리마인더 확장(편집) | 높이 expand + fade-in 상세필드 | 0.25s ease |
| 리마인더 접기 | 높이 collapse + fade-out | 0.2s ease |
| 사이드바 hover | 배경색 fade-in | 0.15s ease |
| 스마트 카드 선택 | 배경색 전환 | 0.2s ease |
| 드래그 정렬 | 항목 이동 + 그림자 elevation | 실시간 |
| 리스트 삭제 | slide-out + collapse | 0.3s ease |

### 2.5 반응형 (참고, 우선순위 낮음)

| 너비 | 동작 |
|------|------|
| ≥ 1024px | 사이드바 항상 표시 |
| 768–1023px | 사이드바 overlay, 햄버거 토글 |
| < 768px | 모바일 뷰, 사이드바 → 리스트 → 상세 네비게이션 |

---

## 3. API 설계

### Reminders

| Method | Endpoint | 설명 |
|--------|----------|------|
| GET | `/api/reminders` | 전체 조회 (?listId, ?tagId, ?flagged, ?completed 필터) |
| GET | `/api/reminders/today` | Today 스마트 리스트 |
| GET | `/api/reminders/scheduled` | Scheduled 스마트 리스트 |
| GET | `/api/reminders/flagged` | Flagged 스마트 리스트 |
| GET | `/api/reminders/completed` | Completed 스마트 리스트 |
| GET | `/api/reminders/{id}` | 단건 조회 |
| POST | `/api/reminders` | 생성 |
| PATCH | `/api/reminders/{id}` | 수정 |
| PATCH | `/api/reminders/{id}/complete` | 완료 토글 |
| DELETE | `/api/reminders/{id}` | 삭제 |
| PATCH | `/api/reminders/reorder` | 순서 변경 |

### Lists

| Method | Endpoint | 설명 |
|--------|----------|------|
| GET | `/api/lists` | 전체 리스트 조회 (리마인더 count 포함) |
| POST | `/api/lists` | 리스트 생성 |
| PATCH | `/api/lists/{id}` | 리스트 수정 (이름, 색상, 아이콘) |
| DELETE | `/api/lists/{id}` | 리스트 삭제 (소속 리마인더도 삭제) |
| PATCH | `/api/lists/reorder` | 리스트 순서 변경 |

### Tags

| Method | Endpoint | 설명 |
|--------|----------|------|
| GET | `/api/tags` | 전체 태그 조회 (사용 count 포함) |

### Subtasks

| Method | Endpoint | 설명 |
|--------|----------|------|
| POST | `/api/reminders/{id}/subtasks` | 서브태스크 추가 |
| PATCH | `/api/subtasks/{id}` | 서브태스크 수정 |
| DELETE | `/api/subtasks/{id}` | 서브태스크 삭제 |

---

## 4. 데이터 모델

```
ReminderList
├── id: Long (PK)
├── name: String
├── color: String (#hex)
├── icon: String (프리셋 이름)
├── displayOrder: Integer
├── isDefault: Boolean
└── createdAt / updatedAt

Reminder
├── id: Long (PK)
├── title: String
├── memo: String (nullable)
├── dueDate: LocalDate (nullable)
├── dueTime: LocalTime (nullable)
├── priority: Enum (NONE, LOW, MEDIUM, HIGH)
├── flagged: Boolean
├── completed: Boolean
├── completedAt: LocalDateTime (nullable)
├── displayOrder: Integer
├── section: String (nullable, 리스트 내 섹션명)
├── list: ManyToOne → ReminderList
├── tags: ManyToMany → Tag
└── createdAt / updatedAt

Subtask
├── id: Long (PK)
├── title: String
├── completed: Boolean
├── displayOrder: Integer
└── reminder: ManyToOne → Reminder

Tag
├── id: Long (PK)
└── name: String (unique)
```

---

## 5. 주요 인터랙션

| 동작 | 설명 |
|------|------|
| 리마인더 추가 | 리스트 하단 "+ Add Reminder" 클릭, 인라인 입력 |
| 완료 처리 | 체크박스 클릭 → 애니메이션 후 완료 처리 |
| 인라인 편집 | 리마인더 행 클릭 → 확장되어 상세 필드 표시 |
| 드래그 정렬 | 리마인더 / 리스트 순서를 드래그로 변경 |
| 리스트 생성 | 사이드바 하단 "+ New List" → 이름/색상/아이콘 선택 |
| 태그 필터 | 사이드바 태그 클릭 → 해당 태그 리마인더만 표시 |
| 검색 | 사이드바 상단 검색 → 제목/메모 전문 검색 |

---

## 6. 범위 외 (Out of Scope)

- 사용자 인증/로그인
- 위치 기반 리마인더
- 리스트 공유/협업
- 알림 푸시
- 반복 리마인더
- Grocery 리스트 자동 분류
- 커스텀 스마트 리스트

---

## 7. 프로젝트 구조

```
tobyreminder/
├── src/main/java/cozy/ai/reminder/
│   ├── TobyReminderApplication.java
│   ├── entity/          # JPA 엔티티
│   ├── repository/      # Spring Data JPA 리포지토리
│   ├── service/         # 비즈니스 로직
│   ├── controller/      # REST 컨트롤러
│   └── dto/             # 요청/응답 DTO
├── src/main/resources/
│   ├── application.yml
│   └── data.sql         # 초기 샘플 데이터
└── frontend/            # Next.js 앱
    ├── src/
    │   ├── app/         # App Router
    │   ├── components/  # UI 컴포넌트
    │   ├── hooks/       # Custom Hooks
    │   ├── lib/         # API 클라이언트, 유틸리티
    │   └── types/       # TypeScript 타입
    └── package.json
```

---

## 8. 마일스톤

| Phase | 내용 | 목표 |
|-------|------|------|
| **M1** | Backend API + H2 설정 | CRUD API 완성, H2 콘솔로 데이터 확인 |
| **M2** | Frontend 기본 레이아웃 | Sidebar + Main Content, 리스트/리마인더 조회 |
| **M3** | CRUD 연동 | 리마인더 생성/수정/삭제/완료, 리스트 관리 |
| **M4** | 스마트 리스트 + 태그 | Today/Scheduled/Flagged/Completed, 태그 필터 |
| **M5** | 인터랙션 완성 | 드래그 정렬, 인라인 편집, 서브태스크, 검색 |
