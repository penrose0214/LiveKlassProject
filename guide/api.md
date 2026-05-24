# API 명세

현재 구현된 컨트롤러 기준의 API 명세다.

공통 규칙:

- 인증/인가는 단순화되어 있으며 `userId` 요청 헤더를 사용한다.
- Command API는 상태 변경을 수행한다.
- Query API는 조회만 수행한다.
- 현재 구현 기준으로 성공 응답은 대부분 `200 OK`다.

## API 목록

| 구분 | 메서드 | 경로 | `userId` 헤더 | 요청 본문 | 응답 |
|---|---|---|---|---|---|
| Command | `POST` | `/api/command/lectures` | 필수 | `CreateLectureRequest` | 없음 |
| Command | `PUT` | `/api/command/lectures/{lectureId}` | 필수 | `UpdateLectureRequest` | 없음 |
| Command | `POST` | `/api/command/lectures/{lectureId}/open` | 필수 | 없음 | 없음 |
| Command | `POST` | `/api/command/lectures/{lectureId}/close` | 필수 | 없음 | 없음 |
| Command | `POST` | `/api/command/lectures/{lectureId}/enrollments` | 필수 | 없음 | `ApplyEnrollmentResponse` |
| Command | `POST` | `/api/command/enrollments/{enrollmentId}/confirm-payment` | 필수 | 없음 | `ConfirmPaymentResponse` |
| Command | `POST` | `/api/command/enrollments/{enrollmentId}/cancel` | 필수 | 없음 | `CancelEnrollmentResponse` |
| Query | `GET` | `/api/query/lectures?page={page}&size={size}` | 없음 | 없음 | `Page<LectureSummaryResponse>` |
| Query | `GET` | `/api/query/lectures/{lectureId}` | 없음 | 없음 | `LectureDetailResponse` |
| Query | `GET` | `/api/query/enrollments/me?page={page}&size={size}` | 필수 | 없음 | `Page<MyEnrollmentResponse>` |
| Query | `GET` | `/api/query/lectures/{lectureId}/students` | 없음 | 없음 | `LectureStudentResponse[]` |

## DTO 요약

### Command Request

| 이름 | 필드 |
|---|---|
| `CreateLectureRequest` | `title`, `description`, `price`, `capacity`, `recruitmentStartAt`, `recruitmentEndAt`, `lectureStartAt`, `lectureEndAt` |
| `UpdateLectureRequest` | `title`, `description`, `price`, `capacity`, `recruitmentStartAt`, `recruitmentEndAt`, `lectureStartAt`, `lectureEndAt` |

### Command Response

| 이름 | 필드 |
|---|---|
| `ApplyEnrollmentResponse` | `enrollmentId`, `lectureId`, `userId`, `status`, `appliedAt`, `paymentDueAt`, `waitlistedAt` |
| `ConfirmPaymentResponse` | `enrollmentId`, `status`, `confirmedAt` |
| `CancelEnrollmentResponse` | `enrollmentId`, `status`, `cancelledAt` |

### Query Response

| 이름 | 필드 |
|---|---|
| `LectureSummaryResponse` | `lectureId`, `creatorId`, `creatorName`, `title`, `price`, `capacity`, `recruitmentStartAt`, `recruitmentEndAt`, `lectureStartAt`, `lectureEndAt`, `status` |
| `LectureDetailResponse` | `lectureId`, `creatorId`, `creatorName`, `title`, `description`, `price`, `capacity`, `recruitmentStartAt`, `recruitmentEndAt`, `lectureStartAt`, `lectureEndAt`, `status`, `occupiedCount`, `confirmedCount`, `waitlistedCount` |
| `MyEnrollmentResponse` | `enrollmentId`, `lectureId`, `lectureTitle`, `creatorName`, `lectureStatus`, `enrollmentStatus`, `price`, `appliedAt`, `paymentDueAt`, `confirmedAt`, `cancelledAt`, `lectureStartAt`, `lectureEndAt` |
| `LectureStudentResponse` | `enrollmentId`, `userId`, `userName`, `status`, `appliedAt`, `confirmedAt` |

## 샘플 요청/응답

### 1. 강의 등록

요청:

```http
POST /api/command/lectures
userId: 1
Content-Type: application/json
```

```json
{
  "title": "Spring Boot 입문",
  "description": "기초부터 실습까지",
  "price": 30000,
  "capacity": 30,
  "recruitmentStartAt": "2026-05-24T10:00:00",
  "recruitmentEndAt": "2026-05-31T23:59:59",
  "lectureStartAt": "2026-06-02T19:00:00",
  "lectureEndAt": "2026-06-30T21:00:00"
}
```

응답:

```http
200 OK
```

### 2. 강의 OPEN

요청:

```http
POST /api/command/lectures/10/open
userId: 1
```

응답:

```http
200 OK
```

### 3. 수강 신청

요청:

```http
POST /api/command/lectures/10/enrollments
userId: 2
```

응답 예시 1: 정원 여유가 있는 경우

```json
{
  "enrollmentId": 100,
  "lectureId": 10,
  "userId": 2,
  "status": "PENDING",
  "appliedAt": "2026-05-24T15:00:00",
  "paymentDueAt": "2026-05-25T15:00:00",
  "waitlistedAt": null
}
```

응답 예시 2: 정원이 가득 찬 경우

```json
{
  "enrollmentId": 101,
  "lectureId": 10,
  "userId": 2,
  "status": "WAITLISTED",
  "appliedAt": "2026-05-24T15:01:00",
  "paymentDueAt": null,
  "waitlistedAt": "2026-05-24T15:01:00"
}
```

### 4. 결제 확정

요청:

```http
POST /api/command/enrollments/100/confirm-payment
userId: 2
```

응답:

```json
{
  "enrollmentId": 100,
  "status": "CONFIRMED",
  "confirmedAt": "2026-05-24T16:00:00"
}
```

### 5. 수강 취소

요청:

```http
POST /api/command/enrollments/100/cancel
userId: 2
```

응답:

```json
{
  "enrollmentId": 100,
  "status": "CANCELLED",
  "cancelledAt": "2026-05-24T17:00:00"
}
```

### 6. 강의 목록 조회

요청:

```http
GET /api/query/lectures?page=0&size=20
```

응답:

```json
{
  "content": [
    {
      "lectureId": 10,
      "creatorId": 1,
      "creatorName": "creator",
      "title": "Spring Boot 입문",
      "price": 30000,
      "capacity": 30,
      "recruitmentStartAt": "2026-05-24T10:00:00",
      "recruitmentEndAt": "2026-05-31T23:59:59",
      "lectureStartAt": "2026-06-02T19:00:00",
      "lectureEndAt": "2026-06-30T21:00:00",
      "status": "OPEN"
    }
  ],
  "number": 0,
  "size": 20,
  "totalElements": 1,
  "totalPages": 1
}
```

### 7. 강의 상세 조회

요청:

```http
GET /api/query/lectures/10
```

응답:

```json
{
  "lectureId": 10,
  "creatorId": 1,
  "creatorName": "creator",
  "title": "Spring Boot 입문",
  "description": "기초부터 실습까지",
  "price": 30000,
  "capacity": 30,
  "recruitmentStartAt": "2026-05-24T10:00:00",
  "recruitmentEndAt": "2026-05-31T23:59:59",
  "lectureStartAt": "2026-06-02T19:00:00",
  "lectureEndAt": "2026-06-30T21:00:00",
  "status": "OPEN",
  "occupiedCount": 5,
  "confirmedCount": 3,
  "waitlistedCount": 2
}
```

### 8. 내 수강 신청 목록 조회

요청:

```http
GET /api/query/enrollments/me?page=0&size=20
userId: 2
```

응답:

```json
{
  "content": [
    {
      "enrollmentId": 100,
      "lectureId": 10,
      "lectureTitle": "Spring Boot 입문",
      "creatorName": "creator",
      "lectureStatus": "OPEN",
      "enrollmentStatus": "PENDING",
      "price": 30000,
      "appliedAt": "2026-05-24T15:00:00",
      "paymentDueAt": "2026-05-25T15:00:00",
      "confirmedAt": null,
      "cancelledAt": null,
      "lectureStartAt": "2026-06-02T19:00:00",
      "lectureEndAt": "2026-06-30T21:00:00"
    }
  ],
  "number": 0,
  "size": 20,
  "totalElements": 1,
  "totalPages": 1
}
```

### 9. 강의별 수강생 목록 조회

요청:

```http
GET /api/query/lectures/10/students
```

응답:

```json
[
  {
    "enrollmentId": 100,
    "userId": 2,
    "userName": "student",
    "status": "CONFIRMED",
    "appliedAt": "2026-05-24T15:00:00",
    "confirmedAt": "2026-05-24T16:00:00"
  }
]
```
