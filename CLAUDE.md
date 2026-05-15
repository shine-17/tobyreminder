# Coding Conventions

## 패키지 구조
- `domain/` — JPA 엔티티
- `service/ports/in/` — Service 인터페이스
- `service/` — Service 구현 클래스 (Default 접두사, 예: `DefaultReminderListService`)
- `repository/` — Spring Data JPA Repository
- `controller/` — REST Controller
- `dto/` — Request/Response DTO

## 네이밍
- Service 인터페이스: `XxxService` (ports/in 패키지)
- Service 구현 클래스: `DefaultXxxService` (service 패키지)

## 테스트
- 기능 추가/수정 시 반드시 검증 테스트를 함께 작성
- 도메인 엔티티 테스트는 순수 단위 테스트
- Service 테스트는 @SpringBootTest 통합 테스트 (Mock 사용 금지)

## 참고 문서
- spec.md: 기능 명세
- plan.md: 개발 계획 (6 phases)
- tasks.md: 구현 태스크 체크리스트