# Toby Reminder — Development Plan

spec.md 기반, 단순한 기능부터 점진적으로 확장하는 개발 계획.

---

## 기술 스택 상세

### Backend

| 항목 | 기술 | 비고 |
|------|------|------|
| Framework | Spring Boot 4.0.6 | Spring Framework 7.0, Jakarta EE 11 |
| Build | Gradle 9.4.1, Kotlin DSL | Spring Initializr 생성 완료 |
| ORM | Spring Data JPA + Hibernate 7 | jakarta.persistence 네임스페이스 |
| DB | H2 (in-memory) | 개발용, 콘솔 활성화 |
| Web | spring-boot-starter-webmvc | Spring Boot 4 새 스타터명 |
| 유틸 | Lombok | @Getter, @NoArgsConstructor 등 |
| Java | 17+ | Spring Boot 4 최소 요구 |
| 테스트 | JUnit 5, Spring Boot Test | MockMvc 기반 API 테스트 |

### Frontend

| 항목 | 기술 | 비고 |
|------|------|------|
| Framework | Next.js 15 (App Router) | React 19, Server Components |
| Language | TypeScript | strict mode |
| Styling | Tailwind CSS 4 | Apple 디자인 토큰 커스텀 |
| HTTP | fetch API | Next.js 내장, no axios |
| 상태관리 | React useState/useReducer | 별도 상태 라이브러리 불필요 |
| 드래그 | @dnd-kit/core | Phase 5에서 도입 |
| 아이콘 | Lucide React | SF Symbol 대체 |

### 개발 환경

| 항목 | 설정 |
|------|------|
| Backend 포트 | 8080 |
| Frontend 포트 | 3000 |
| API Proxy | next.config.ts → rewrites로 /api/** → localhost:8080 |
| CORS | 개발 시 Spring Boot에서 localhost:3000 허용 |

---

## Phase 1 — Backend 기초 + 단일 리스트 CRUD

> 가장 단순한 형태. 리스트 1개, 리마인더 CRUD만 동작.

### Backend 작업

#### 1-1. 프로젝트 설정
- `application.properties` → `application.yml` 전환
- H2 in-memory 설정 (콘솔 활성화, `jdbc:h2:mem:reminderdb`)
- JPA 설정 (`ddl-auto: create-drop`, `show-sql: true`)
- CORS 설정 (`WebMvcConfigurer` — localhost:3000 허용)

#### 1-2. 엔티티 (최소 버전)
- `ReminderList` — id, name, color, icon, displayOrder, isDefault, createdAt, updatedAt
- `Reminder` — id, title, memo, completed, completedAt, displayOrder, list(ManyToOne), createdAt, updatedAt
  - **이 단계에서 제외**: dueDate, dueTime, priority, flagged, section, tags

#### 1-3. Repository
- `ReminderListRepository extends JpaRepository<ReminderList, Long>`
- `ReminderRepository extends JpaRepository<Reminder, Long>`
  - `findByListIdAndCompletedFalseOrderByDisplayOrder(Long listId)`
  - `findByListIdAndCompletedTrueOrderByCompletedAtDesc(Long listId)`
  - `countByListIdAndCompletedFalse(Long listId)`

#### 1-4. DTO
- `ReminderRequest` — title, memo
- `ReminderResponse` — id, title, memo, completed, completedAt, displayOrder
- `ReminderListResponse` — id, name, color, icon, reminderCount

#### 1-5. Service
- `ReminderService` — create, getById, getByListId, update, toggleComplete, delete
- `ReminderListService` — getAll (with count), getById

#### 1-6. Controller
- `GET /api/lists` — 전체 리스트 (count 포함)
- `GET /api/lists/{id}` — 리스트 단건
- `GET /api/reminders?listId={id}` — 리스트별 리마인더 조회
- `POST /api/reminders` — 리마인더 생성
- `PATCH /api/reminders/{id}` — 리마인더 수정
- `PATCH /api/reminders/{id}/complete` — 완료 토글
- `DELETE /api/reminders/{id}` — 리마인더 삭제

#### 1-7. 초기 데이터
- `data.sql` — 기본 리스트 1개 ("Reminders", blue, isDefault=true) + 샘플 리마인더 3~5개

#### 검증
- `./gradlew bootRun` → H2 콘솔에서 데이터 확인
- curl 또는 HTTPie로 전 API 테스트

---

## Phase 2 — Frontend 기본 레이아웃 + 조회

> Sidebar + Main Content 레이아웃. 리스트 선택 → 리마인더 목록 표시.

### Frontend 작업

#### 2-1. Next.js 프로젝트 초기화
```bash
cd tobyreminder
npx create-next-app@latest frontend --typescript --tailwind --app --src-dir
```

#### 2-2. API Proxy 설정
- `next.config.ts` — rewrites: `/api/:path*` → `http://localhost:8080/api/:path*`

#### 2-3. 디자인 토큰 설정
- `tailwind.config.ts` — spec.md 2.2절의 컬러/타이포/간격을 Tailwind 커스텀 테마로 등록
- `globals.css` — CSS 변수 정의, font-family 설정

#### 2-4. 타입 정의
```typescript
// types/index.ts
interface ReminderList { id, name, color, icon, reminderCount }
interface Reminder { id, title, memo, completed, completedAt, displayOrder }
```

#### 2-5. API 클라이언트
- `lib/api.ts` — fetch 래퍼 (getLists, getReminders, etc.)

#### 2-6. 레이아웃 컴포넌트
- `app/layout.tsx` — 전체 레이아웃 (Sidebar + Main)
- `components/Sidebar.tsx` — 사이드바 컨테이너
- `components/SmartListCards.tsx` — 2x2 스마트 리스트 카드 (UI만, All 카드만 동작)
- `components/SidebarListItem.tsx` — 리스트 항목 (아이콘 원형 + 이름 + 카운트)

#### 2-7. 메인 콘텐츠
- `components/ReminderListView.tsx` — 리마인더 목록 컨테이너
- `components/ReminderRow.tsx` — 리마인더 행 (체크박스 + 제목 + 메모 미리보기)
- `components/Checkbox.tsx` — Apple 스타일 원형 체크박스

#### 2-8. 상태 관리
- 사이드바 리스트 선택 → URL 또는 state로 관리
- 선택된 리스트의 리마인더를 fetch하여 표시

#### 검증
- 브라우저에서 Sidebar 리스트 클릭 → Main 영역에 리마인더 목록 표시
- Apple Reminders 레이아웃과 시각적으로 비교

---

## Phase 3 — 리마인더 CRUD 연동 + 리스트 관리

> 리마인더 생성/편집/삭제/완료. 리스트 생성/편집/삭제.

### Backend 추가
- `POST /api/lists` — 리스트 생성
- `PATCH /api/lists/{id}` — 리스트 수정
- `DELETE /api/lists/{id}` — 리스트 삭제 (cascade)

### Frontend 작업

#### 3-1. 리마인더 추가
- `components/AddReminder.tsx` — 리스트 하단 "+ Add Reminder" 인라인 입력
- Enter로 빠르게 추가, 포커스 유지하여 연속 입력

#### 3-2. 리마인더 완료
- 체크박스 클릭 → 완료 애니메이션 (원 채우기 0.3s → line-through → 0.5s fade-out)
- API: `PATCH /api/reminders/{id}/complete`

#### 3-3. 리마인더 인라인 편집
- `components/ReminderDetail.tsx` — 행 클릭 시 확장
- 제목/메모 borderless 편집
- 외부 클릭 시 자동 저장 (`PATCH /api/reminders/{id}`)

#### 3-4. 리마인더 삭제
- 편집 모드에서 삭제 버튼 또는 컨텍스트 메뉴

#### 3-5. 리스트 관리
- `components/ListModal.tsx` — 리스트 생성/편집 모달
  - 이름 입력, 12색 컬러 팔레트, 아이콘 그리드
- 사이드바 "+ Add List" 버튼
- 리스트 우클릭 → 편집/삭제 컨텍스트 메뉴

#### 검증
- 리마인더 생성 → 목록에 즉시 표시
- 완료 → 애니메이션 후 사라짐
- 인라인 편집 → 외부 클릭 시 저장 확인
- 리스트 생성 → 사이드바에 표시, 색상/아이콘 반영

---

## Phase 4 — 날짜/우선순위/플래그 + 스마트 리스트

> Reminder 필드 확장. 5개 스마트 리스트 동작.

### Backend 추가

#### 4-1. Reminder 엔티티 확장
- dueDate (LocalDate), dueTime (LocalTime), priority (Enum), flagged (Boolean)

#### 4-2. 스마트 리스트 API
- `GET /api/reminders/today` — `dueDate <= today AND completed = false`
- `GET /api/reminders/scheduled` — `dueDate IS NOT NULL AND completed = false`
- `GET /api/reminders/flagged` — `flagged = true AND completed = false`
- `GET /api/reminders/completed` — `completed = true ORDER BY completedAt DESC`

#### 4-3. 검색 API
- `GET /api/reminders/search?q={keyword}` — title/memo LIKE 검색

### Frontend 작업

#### 4-4. 리마인더 행 확장
- 우선순위 표시: `!`(파랑) / `!!`(주황) / `!!!`(빨강)
- 플래그 아이콘 (주황 깃발)
- 마감일 표시 (오늘이면 파란색, 지났으면 빨간색)

#### 4-5. 인라인 편집 확장
- 날짜 picker (date input 또는 커스텀 캘린더)
- 시간 picker
- 우선순위 드롭다운 (없음/낮음/보통/높음)
- 플래그 토글

#### 4-6. 스마트 리스트 카드 동작
- Today / Scheduled / All / Flagged 카드 클릭 → 해당 API 호출 → 메인 영역 표시
- 각 카드에 실시간 카운트 표시
- Completed 스마트 리스트 (사이드바 하단 또는 카드 아래)

#### 4-7. 검색
- `components/SearchBar.tsx` — 사이드바 상단 검색 입력
- debounce 300ms → API 호출 → 결과 메인 영역 표시

#### 검증
- 마감일 설정한 리마인더 → Today / Scheduled 스마트 리스트에 표시
- 플래그 토글 → Flagged 스마트 리스트 카운트 변경
- 검색어 입력 → 실시간 결과

---

## Phase 5 — 태그 + 서브태스크

> 태그 시스템, 서브태스크, 태그 필터링.

### Backend 추가

#### 5-1. Tag 엔티티 + Reminder-Tag 관계
- `Tag` 엔티티 (id, name)
- `reminder_tag` 조인 테이블 (ManyToMany)
- `GET /api/tags` — 전체 태그 + 사용 count
- Reminder 생성/수정 시 tags 배열 처리

#### 5-2. Subtask 엔티티
- `Subtask` 엔티티 (id, title, completed, displayOrder, reminder ManyToOne)
- `POST /api/reminders/{id}/subtasks`
- `PATCH /api/subtasks/{id}`
- `DELETE /api/subtasks/{id}`

#### 5-3. 태그 필터 API
- `GET /api/reminders?tagId={id}` — 태그별 필터링

### Frontend 작업

#### 5-4. 태그 UI
- 리마인더 행에 태그 pill 표시 (#tag, 회색 라운드 배경)
- 인라인 편집에서 태그 입력 + 기존 태그 자동완성 드롭다운
- `components/TagInput.tsx` — 태그 입력 컴포넌트

#### 5-5. 사이드바 태그 영역
- `components/SidebarTags.tsx` — 사용 중인 태그 목록
- 태그 클릭 → 메인 영역에 해당 태그 리마인더 필터링

#### 5-6. 서브태스크 UI
- 리마인더 행에서 서브태스크 표시 (들여쓰기 28px, 작은 체크박스)
- 인라인 편집에서 서브태스크 추가/삭제/완료 토글
- `components/SubtaskRow.tsx`

#### 검증
- 태그 추가 → 리마인더 행과 사이드바에 반영
- 사이드바 태그 클릭 → 필터링 동작
- 서브태스크 추가/완료/삭제

---

## Phase 6 — 인터랙션 완성 + 폴리싱

> 드래그 정렬, 섹션, 애니메이션 세밀 조정, 키보드 단축키.

### Backend 추가

#### 6-1. 순서 변경 API
- `PATCH /api/reminders/reorder` — `{ ids: [3, 1, 2] }` 순서 배열
- `PATCH /api/lists/reorder` — 리스트 순서 배열

#### 6-2. 섹션
- Reminder의 `section` 필드 활용
- 리스트 내 섹션별 그룹핑 조회

### Frontend 작업

#### 6-3. 드래그 & 드롭
- `@dnd-kit/core` + `@dnd-kit/sortable` 도입
- 리마인더 순서 드래그 정렬
- 사이드바 리스트 순서 드래그 정렬

#### 6-4. 섹션 UI
- 리스트 내 섹션 구분선 + 섹션명
- 섹션 추가/이름 변경

#### 6-5. 애니메이션 정교화
- 체크박스 완료 애니메이션 미세 조정
- 리스트 전환 시 fade 트랜지션
- 스마트 카드 선택 시 색상 전환

#### 6-6. 키보드 단축키
- `Enter` — 새 리마인더 추가
- `Escape` — 편집 닫기
- `Backspace` (빈 제목) — 리마인더 삭제
- `Tab` — 서브태스크로 들여쓰기

#### 6-7. 빈 상태 (Empty State)
- 리스트 비어있을 때 안내 메시지
- 검색 결과 없을 때 안내

#### 검증
- 드래그로 리마인더 순서 변경 → 새로고침 후 유지
- 키보드만으로 리마인더 생성/편집/완료 가능
- 전체 플로우 E2E 테스트

---

## Phase별 산출물 요약

| Phase | Backend | Frontend | 핵심 결과 |
|-------|---------|----------|----------|
| **1** | Entity, Repo, Service, Controller, data.sql | — | API 동작, H2 확인 |
| **2** | — | Layout, Sidebar, ReminderRow, Checkbox | 리스트 선택 → 목록 표시 |
| **3** | List CRUD API | AddReminder, Detail, ListModal | 전체 CRUD 동작 |
| **4** | 날짜/우선순위/플래그 필드, 스마트리스트 API, 검색 | SmartList 연동, DatePicker, SearchBar | 스마트 리스트 + 검색 |
| **5** | Tag/Subtask 엔티티, API | TagInput, SidebarTags, SubtaskRow | 태그 필터 + 서브태스크 |
| **6** | Reorder API, Section | @dnd-kit, 키보드 단축키, Empty State | 완성도 높은 UX |
