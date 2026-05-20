# Code Review Fix Tasks

## 🔴 HIGH Priority

### Backend

- [x] **B-H1. N+1 쿼리 해결** — `DefaultReminderListService.getAll()`
  - 파일: `service/DefaultReminderListService.java:25-29`
  - 리스트마다 별도 COUNT 쿼리 실행 (2N+1)
  - 수정: JOIN + GROUP BY 단일 쿼리로 리스트별 카운트 한 번에 조회

- [x] **B-H2. Request Validation 추가**
  - 파일: `dto/ReminderRequest.java`, `dto/ReminderListRequest.java`
  - `@NotBlank`, `@NotNull` 등 Jakarta Validation 어노테이션 추가
  - Controller에 `@Valid` 적용
  - `GlobalExceptionHandler`에 `MethodArgumentNotValidException` 핸들러 추가

- [x] **B-H3. Lazy Loading 안전성 확보**
  - 파일: `domain/Reminder.java:42`, `dto/ReminderResponse.java:28`
  - `ReminderResponse.from()`에서 `getList().getId()` 호출 시 Lazy 로딩 트리거
  - 수정: EAGER fetch 또는 `JOIN FETCH` 쿼리 사용, 혹은 `@Column(name="list_id", insertable=false, updatable=false)` 별도 필드

- [x] **B-H4. H2 Console 프로덕션 비활성화**
  - 파일: `src/main/resources/application.yml`
  - `h2.console.enabled`를 환경변수로 전환 (`${H2_CONSOLE_ENABLED:false}`)

### Frontend

- [x] **F-H1. useEffect 의존성 배열 수정** — `page.tsx`
  - 파일: `app/page.tsx:45`
  - `fetchLists`가 dependency에 누락, 초기화 로직 안정화 필요
  - 수정: 초기화 전용 useEffect와 데이터 페칭 useEffect 분리

- [x] **F-H2. Stale Closure 수정** — `ReminderDetail.tsx`
  - 파일: `components/ReminderDetail.tsx:35-46`
  - `handleSave`가 이벤트 리스너에 불안정 참조로 바인딩
  - 수정: `useCallback` + ref 패턴으로 최신 함수 참조 보장

- [x] **F-H3. React.memo 적용** — 리스트 아이템 컴포넌트
  - 파일: `components/ReminderRow.tsx`, `components/ReminderDetail.tsx`
  - `editingId` 변경 시 전체 리스트 리렌더 방지
  - 수정: `React.memo` + 적절한 비교 함수 적용

- [x] **F-H4. AbortController 추가** — API fetch
  - 파일: `lib/api.ts`, 각 컴포넌트의 useEffect
  - 컴포넌트 언마운트 시 진행 중인 fetch 취소
  - 수정: `fetchJson`에 signal 파라미터 추가, useEffect cleanup에서 abort

- [x] **F-H5. Error Boundary 추가**
  - 신규 파일: `components/ErrorBoundary.tsx` 또는 `app/error.tsx`
  - 자식 컴포넌트 크래시 시 전체 앱 중단 방지
  - 수정: Next.js `error.tsx` 컨벤션 활용

- [x] **F-H6. 키보드 내비게이션 구현**
  - 파일: `components/Sidebar.tsx`, `components/ReminderListView.tsx`
  - 리스트/리마인더 간 화살표 키 이동, Tab 순서 정의
  - 수정: `tabIndex`, `onKeyDown` 핸들러 추가

- [x] **F-H7. 모달 포커스 트랩**
  - 파일: `components/ListModal.tsx`
  - 모달 열림 시 포커스 가두기, 닫힘 시 원래 요소로 복귀
  - 수정: focus trap 로직 구현 (Tab/Shift+Tab 순환)

---

## 🟡 MEDIUM Priority

### Backend

- [x] **B-M1. DB 인덱스 추가**
  - 파일: `domain/Reminder.java`, `domain/ReminderList.java`
  - `@Table(indexes = { @Index(columnList = "list_id, completed"), @Index(columnList = "list_id, display_order") })`

- [x] **B-M2. GlobalExceptionHandler 확장**
  - 파일: `controller/GlobalExceptionHandler.java`
  - `MethodArgumentNotValidException`, `HttpMessageNotReadableException`, 일반 `Exception` 핸들러 추가

- [x] **B-M3. Cascade 설정**
  - 파일: `domain/ReminderList.java`
  - `@OneToMany(mappedBy = "list", cascade = CascadeType.ALL, orphanRemoval = true)` 추가
  - `DefaultReminderListService.delete()`에서 수동 삭제 로직 제거

- [x] **B-M4. Reorder 쿼리 최적화**
  - 파일: `service/DefaultReminderListService.java:73-78`, `service/DefaultReminderService.java`
  - N건 개별 UPDATE → 배치 UPDATE 또는 `@Modifying @Query` 사용

- [x] **B-M5. CORS 헤더 화이트리스트**
  - 파일: `config/WebConfig.java`
  - `allowedHeaders("*")` → `allowedHeaders("Content-Type", "Authorization")` 제한

- [x] **B-M6. XSS 입력 살균**
  - 파일: DTO 또는 서비스 레이어
  - `title`, `memo`, `name` 필드에 HTML 이스케이프 처리

- [x] **B-M7. show-sql 환경별 분리**
  - 파일: `application.yml`
  - `show-sql: ${JPA_SHOW_SQL:false}`, `format_sql: ${JPA_FORMAT_SQL:false}`

- [x] **B-M8. @Builder on Records 제거**
  - 파일: `dto/ReminderResponse.java`, `dto/ReminderListResponse.java`
  - Record의 canonical constructor 또는 정적 팩터리 메서드로 대체

- [x] **B-M9. Boolean → boolean 프리미티브**
  - 파일: `domain/Reminder.java:35`, `domain/ReminderList.java:38`
  - non-nullable 필드는 `Boolean` 래퍼 대신 `boolean` 프리미티브

- [x] **B-M10. 테스트 엣지 케이스 추가**
  - 빈 리스트 reorder, 리스트 삭제 시 리마인더 연쇄 삭제 검증, 빈 문자열 vs null 업데이트

### Frontend

- [x] **F-M1. 에러 UI 표시**
  - 파일: 전체 컴포넌트
  - `console.error` → 사용자 대상 토스트/알림 메시지 표시
  - 신규: `components/Toast.tsx` 또는 에러 상태 관리

- [x] **F-M2. 로딩 상태 추가**
  - 파일: `components/ReminderDetail.tsx`, `app/page.tsx`
  - 저장/삭제 중 버튼 비활성화, 스피너 표시로 중복 클릭 방지

- [x] **F-M3. Optimistic UI 적용**
  - 파일: `app/page.tsx` (삭제), `components/ReminderRow.tsx` (완료)
  - 즉시 UI 반영 후 API 응답 시 확인/롤백

- [x] **F-M4. AddReminder 이중 제출 방지**
  - 파일: `components/AddReminder.tsx`
  - Enter + onBlur 동시 트리거 방지 (submitting 플래그 또는 setTimeout)

- [x] **F-M5. inline style → Tailwind 전환**
  - 파일: `components/ReminderRow.tsx`, `components/Sidebar.tsx`, `components/SmartListCards.tsx` 등
  - `style={{}}` → Tailwind 유틸리티 클래스로 이관 (CSS 변수 참조는 유지)

- [x] **F-M6. 반응형 레이아웃**
  - 파일: `app/page.tsx`, `components/Sidebar.tsx`
  - Sidebar 280px 고정 → 모바일에서 숨김/드로어 패턴
  - `hidden md:flex`, `w-full md:w-[280px]`

- [x] **F-M7. fetchJson 타입 안전성**
  - 파일: `lib/api.ts:10`
  - `undefined as T` 타입 단언 제거
  - 204 응답용 `fetchVoid()` 함수 분리

- [x] **F-M8. ARIA 레이블 보강**
  - 파일: `components/Checkbox.tsx`, `components/ListModal.tsx`
  - 체크박스에 리마인더 제목 포함한 aria-label
  - 모달에 `role="dialog"`, `aria-labelledby` 추가
  - 아이콘 버튼에 `aria-label` 추가

- [x] **F-M9. 시맨틱 HTML 개선**
  - 파일: `app/page.tsx`, `components/Sidebar.tsx`
  - Sidebar → `<nav>`, 데코레이티브 아이콘 → `aria-hidden="true"`

- [x] **F-M10. useMemo 최적화**
  - 파일: `app/page.tsx:114`
  - `selectedList = lists.find(...)` → `useMemo`로 감싸기

- [x] **F-M11. 긴 텍스트 처리**
  - 파일: `components/SidebarListItem.tsx:66`, `components/ReminderRow.tsx:79`
  - truncate된 텍스트에 `title` 속성으로 툴팁 추가

- [x] **F-M12. Sidebar 빈 상태 UI**
  - 파일: `components/Sidebar.tsx`
  - 리스트 0개일 때 "리스트를 만들어보세요" 안내 메시지

- [x] **F-M13. 컨텍스트 메뉴 렌더링 최적화**
  - 파일: `components/SidebarListItem.tsx:87-119`
  - 조건부 렌더링 대신 Portal 또는 CSS visibility 활용

- [x] **F-M14. refreshAll 안정화**
  - 파일: `app/page.tsx:66-69`
  - `refreshAll`이 `selectedListId` 변경마다 재생성 → 자식 불필요 리렌더
  - 수정: ref 기반 최신값 참조 패턴

---

## 🔵 LOW Priority

### Backend

- [x] **B-L1. Ports/Out 레이어 도입** — 리포지토리 추상화 인터페이스
- [x] **B-L2. DTO 패키지 분리** — `dto/request/`, `dto/response/`
- [x] **B-L3. Stream 연산 최적화** — `DefaultReminderService.getByListId()` 불필요 ArrayList 제거
- [x] **B-L4. isDefault 네이밍** — `getIsDefault()` → Lombok fluent 또는 필드명 변경
- [x] **B-L5. HikariCP 설정** — 커넥션 풀 사이즈 명시 (프로덕션 대비)
- [x] **B-L6. SQL init mode 환경별 분리** — `mode: ${SQL_INIT_MODE:never}`
- [x] **B-L7. Rate Limiting** — 프로덕션 환경 요청 제한 설정

### Frontend

- [x] **F-L1. 다크 모드 지원** — `globals.css`에 `prefers-color-scheme: dark` 대응
- [x] **F-L2. 삭제 Undo 기능** — 토스트 + 되돌리기 버튼
- [x] **F-L3. ListModal 입력값 검증 강화** — 최대 길이 제한, 특수문자 처리
- [x] **F-L4. 환경변수 API URL** — `lib/api.ts` BASE를 `NEXT_PUBLIC_API_BASE`로
- [x] **F-L5. next.config.mjs 환경변수** — rewrite destination을 `BACKEND_URL`로
- [x] **F-L6. API 응답 런타임 검증** — zod 등으로 응답 스키마 검증
- [x] **F-L7. 데코레이티브 아이콘 aria-hidden** — `Sidebar.tsx` 검색 아이콘 등
- [x] **F-L8. confirm() → 커스텀 확인 모달** — 브라우저 네이티브 대화상자 교체
- [x] **F-L9. 완료 후 포커스 관리** — 리마인더 완료 애니메이션 후 다음 항목으로 포커스 이동
