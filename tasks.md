# Toby Reminder — Tasks

## Phase 1 — Backend 기초 + 단일 리스트 CRUD

### 1-1. 프로젝트 설정
- [x] `application.properties` → `application.yml` 전환
- [x] H2 in-memory 설정 (`jdbc:h2:mem:reminderdb`, 콘솔 활성화)
- [x] JPA 설정 (`ddl-auto: create-drop`, `show-sql: true`, `format_sql: true`)
- [x] CORS 설정 (`WebMvcConfigurer` — localhost:3000 허용)

### 1-2. 엔티티
- [x] `ReminderList` 엔티티 (id, name, color, icon, displayOrder, isDefault, createdAt, updatedAt)
- [x] `Reminder` 엔티티 (id, title, memo, completed, completedAt, displayOrder, list ManyToOne, createdAt, updatedAt)

### 1-3. Repository
- [x] `ReminderListRepository` (JpaRepository)
- [x] `ReminderRepository` (JpaRepository)
  - [x] `findByListIdAndCompletedFalseOrderByDisplayOrder()`
  - [x] `findByListIdAndCompletedTrueOrderByCompletedAtDesc()`
  - [x] `countByListIdAndCompletedFalse()`

### 1-4. DTO
- [x] `ReminderRequest` (title, memo, listId)
- [x] `ReminderResponse` (id, title, memo, completed, completedAt, displayOrder)
- [x] `ReminderListResponse` (id, name, color, icon, reminderCount)

### 1-5. Service
- [x] `ReminderListService` — getAll (with count), getById
- [x] `ReminderService` — create, getById, getByListId, update, toggleComplete, delete

### 1-6. Controller
- [x] `GET /api/lists` — 전체 리스트 (count 포함)
- [x] `GET /api/lists/{id}` — 리스트 단건
- [x] `GET /api/reminders?listId={id}` — 리스트별 리마인더 조회
- [x] `POST /api/reminders` — 생성
- [x] `PATCH /api/reminders/{id}` — 수정
- [x] `PATCH /api/reminders/{id}/complete` — 완료 토글
- [x] `DELETE /api/reminders/{id}` — 삭제

### 1-7. 초기 데이터
- [x] `data.sql` — 기본 리스트 1개 + 샘플 리마인더 5개

### 1-8. 검증
- [x] `./gradlew bootRun` 성공
- [x] H2 콘솔에서 테이블/데이터 확인
- [x] 전 API curl 테스트 통과

---

## Phase 2 — Frontend 기본 레이아웃 + 조회

### 2-1. 프로젝트 초기화
- [x] `npx create-next-app@latest frontend` (TypeScript, Tailwind, App Router, src-dir)
- [x] `next.config.mjs` API Proxy 설정 (`/api/:path*` → `localhost:8080`)

### 2-2. 디자인 토큰
- [x] `tailwind.config.ts` — spec.md 컬러/타이포/간격 커스텀 테마
- [x] `globals.css` — CSS 변수 정의, font-family 설정

### 2-3. 타입 + API 클라이언트
- [x] `types/index.ts` — ReminderList, Reminder 인터페이스
- [x] `lib/api.ts` — getLists(), getReminders(listId) fetch 래퍼

### 2-4. 레이아웃
- [x] `app/layout.tsx` — Sidebar + Main 2단 레이아웃
- [x] `components/Sidebar.tsx` — 사이드바 컨테이너
- [x] `components/SmartListCards.tsx` — 2x2 그리드 카드 (UI만, All만 동작)
- [x] `components/SidebarListItem.tsx` — 컬러 원형 아이콘 + 이름 + 카운트

### 2-5. 메인 콘텐츠
- [x] `components/ReminderListView.tsx` — 리마인더 목록 컨테이너
- [x] `components/ReminderRow.tsx` — 체크박스 + 제목 + 메모 미리보기
- [x] `components/Checkbox.tsx` — Apple 스타일 원형 체크박스

### 2-6. 상태 관리 + 연동
- [x] 사이드바 리스트 선택 state
- [x] 선택된 리스트 → API fetch → 리마인더 목록 표시

### 2-7. 검증
- [x] Sidebar 리스트 클릭 → Main에 리마인더 표시
- [x] Apple Reminders와 시각적 비교 확인

---

## Phase 3 — 리마인더 CRUD 연동 + 리스트 관리

### 3-1. Backend — 리스트 CRUD API
- [ ] `POST /api/lists` — 리스트 생성
- [ ] `PATCH /api/lists/{id}` — 리스트 수정 (이름, 색상, 아이콘)
- [ ] `DELETE /api/lists/{id}` — 리스트 삭제 (cascade)

### 3-2. 리마인더 추가
- [ ] `components/AddReminder.tsx` — 하단 "+ Add Reminder" 인라인 입력
- [ ] Enter로 생성, 포커스 유지하여 연속 입력

### 3-3. 리마인더 완료
- [ ] 체크박스 클릭 → `PATCH /api/reminders/{id}/complete`
- [ ] 완료 애니메이션 (원 채우기 0.3s → line-through → 0.5s fade-out)

### 3-4. 리마인더 인라인 편집
- [ ] `components/ReminderDetail.tsx` — 행 클릭 시 확장
- [ ] 제목/메모 borderless 인라인 편집
- [ ] 외부 클릭 시 자동 저장 (`PATCH /api/reminders/{id}`)

### 3-5. 리마인더 삭제
- [ ] 편집 모드에서 삭제 버튼
- [ ] `DELETE /api/reminders/{id}` 호출 후 목록 갱신

### 3-6. 리스트 관리
- [ ] `components/ListModal.tsx` — 생성/편집 모달 (이름, 12색 팔레트, 아이콘 그리드)
- [ ] 사이드바 "+ Add List" 버튼
- [ ] 리스트 우클릭 → 편집/삭제 컨텍스트 메뉴

### 3-7. 검증
- [ ] 리마인더 생성 → 목록에 즉시 표시
- [ ] 완료 → 애니메이션 후 사라짐
- [ ] 인라인 편집 → 외부 클릭 시 저장
- [ ] 리스트 생성/편집/삭제 동작

---

## Phase 4 — 날짜/우선순위/플래그 + 스마트 리스트

### 4-1. Backend — Reminder 엔티티 확장
- [ ] dueDate (LocalDate), dueTime (LocalTime) 필드 추가
- [ ] priority (Enum: NONE, LOW, MEDIUM, HIGH) 필드 추가
- [ ] flagged (Boolean) 필드 추가
- [ ] DTO 업데이트 (Request/Response에 새 필드 반영)

### 4-2. Backend — 스마트 리스트 API
- [ ] `GET /api/reminders/today` — dueDate <= today AND completed = false
- [ ] `GET /api/reminders/scheduled` — dueDate IS NOT NULL AND completed = false
- [ ] `GET /api/reminders/flagged` — flagged = true AND completed = false
- [ ] `GET /api/reminders/completed` — completed = true ORDER BY completedAt DESC

### 4-3. Backend — 검색 API
- [ ] `GET /api/reminders/search?q={keyword}` — title/memo LIKE 검색

### 4-4. Frontend — 리마인더 행 확장
- [ ] 우선순위 표시: `!`(파랑) / `!!`(주황) / `!!!`(빨강)
- [ ] 플래그 아이콘 (주황 깃발, 행 우측)
- [ ] 마감일 표시 (오늘=파랑, 지남=빨강)

### 4-5. Frontend — 인라인 편집 확장
- [ ] 날짜 picker
- [ ] 시간 picker
- [ ] 우선순위 드롭다운
- [ ] 플래그 토글

### 4-6. Frontend — 스마트 리스트 카드 동작
- [ ] Today / Scheduled / All / Flagged 카드 클릭 → 해당 API 호출
- [ ] 각 카드 실시간 카운트 표시
- [ ] Completed 스마트 리스트 표시

### 4-7. Frontend — 검색
- [ ] `components/SearchBar.tsx` — 사이드바 상단 검색
- [ ] debounce 300ms → API 호출 → 결과 표시

### 4-8. 검증
- [ ] 마감일 리마인더 → Today/Scheduled에 표시
- [ ] 플래그 토글 → Flagged 카운트 변경
- [ ] 검색어 입력 → 실시간 결과

---

## Phase 5 — 태그 + 서브태스크

### 5-1. Backend — Tag 엔티티
- [ ] `Tag` 엔티티 (id, name unique)
- [ ] `reminder_tag` 조인 테이블 (ManyToMany)
- [ ] `TagRepository`
- [ ] `GET /api/tags` — 전체 태그 + 사용 count
- [ ] Reminder 생성/수정 시 tags 배열 처리

### 5-2. Backend — Subtask 엔티티
- [ ] `Subtask` 엔티티 (id, title, completed, displayOrder, reminder ManyToOne)
- [ ] `SubtaskRepository`
- [ ] `POST /api/reminders/{id}/subtasks` — 서브태스크 추가
- [ ] `PATCH /api/subtasks/{id}` — 수정
- [ ] `DELETE /api/subtasks/{id}` — 삭제

### 5-3. Backend — 태그 필터
- [ ] `GET /api/reminders?tagId={id}` — 태그별 필터링

### 5-4. Frontend — 태그 UI
- [ ] 리마인더 행에 태그 pill 표시 (#tag, 회색 라운드)
- [ ] `components/TagInput.tsx` — 태그 입력 + 자동완성 드롭다운
- [ ] 인라인 편집에서 태그 추가/제거

### 5-5. Frontend — 사이드바 태그
- [ ] `components/SidebarTags.tsx` — 사용 중 태그 목록
- [ ] 태그 클릭 → 해당 태그 리마인더 필터링

### 5-6. Frontend — 서브태스크 UI
- [ ] `components/SubtaskRow.tsx` — 들여쓰기 + 작은 체크박스
- [ ] 인라인 편집에서 서브태스크 추가/삭제/완료 토글
- [ ] Reminder 응답에 subtasks 포함

### 5-7. 검증
- [ ] 태그 추가 → 행 + 사이드바 반영
- [ ] 사이드바 태그 클릭 → 필터링 동작
- [ ] 서브태스크 추가/완료/삭제

---

## Phase 6 — 인터랙션 완성 + 폴리싱

### 6-1. Backend — 순서 변경 API
- [ ] `PATCH /api/reminders/reorder` — `{ ids: [3, 1, 2] }`
- [ ] `PATCH /api/lists/reorder` — 리스트 순서 배열

### 6-2. Backend — 섹션
- [ ] Reminder `section` 필드 활용
- [ ] 리스트 내 섹션별 그룹핑 조회

### 6-3. Frontend — 드래그 & 드롭
- [ ] `@dnd-kit/core` + `@dnd-kit/sortable` 설치
- [ ] 리마인더 순서 드래그 정렬
- [ ] 사이드바 리스트 순서 드래그 정렬

### 6-4. Frontend — 섹션 UI
- [ ] 리스트 내 섹션 구분선 + 섹션명
- [ ] 섹션 추가 / 이름 변경

### 6-5. Frontend — 애니메이션 정교화
- [ ] 체크박스 완료 애니메이션 미세 조정
- [ ] 리스트 전환 시 fade 트랜지션
- [ ] 스마트 카드 선택 시 색상 전환

### 6-6. Frontend — 키보드 단축키
- [ ] `Enter` — 새 리마인더 추가
- [ ] `Escape` — 편집 닫기
- [ ] `Backspace` (빈 제목) — 리마인더 삭제
- [ ] `Tab` — 서브태스크 들여쓰기

### 6-7. Frontend — 빈 상태 (Empty State)
- [ ] 리스트 비어있을 때 안내 메시지
- [ ] 검색 결과 없을 때 안내

### 6-8. 검증
- [ ] 드래그 순서 변경 → 새로고침 후 유지
- [ ] 키보드만으로 생성/편집/완료 가능
- [ ] 전체 플로우 E2E 확인
