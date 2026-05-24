@startuml

[*] --> PENDING : apply()\n[occupied < capacity]
[*] --> WAITLISTED : apply()\n[occupied >= capacity]

WAITLISTED --> PENDING : promote()\n[seat available]
WAITLISTED --> CANCELLED : cancel()

PENDING --> CONFIRMED : confirmPayment()\n[now <= paymentDueAt]
PENDING --> CANCELLED : cancel()

CONFIRMED --> CANCELLED : cancel()\n[confirmedAt + 7일 이내\nAND 수강 시작일 이전]

CANCELLED --> [*]

note right of WAITLISTED
정원 미점유
DB 기반 대기열 상태
정렬 기준: waitlistedAt ASC, id ASC
end note

note right of PENDING
정원 점유
결제 대기
paymentDueAt 기록
end note

note right of CONFIRMED
최종 수강 확정
end note

note right of CANCELLED
최종 상태
복구 불가
end note

@enduml



---

@startuml

[*] --> DRAFT : 강의 등록

DRAFT --> OPEN : open()
OPEN --> CLOSED : close()

CLOSED --> [*]

note right of DRAFT
초안 상태
수강 신청 불가
end note

note right of OPEN
모집 중
모집 기간 내 신청 가능
end note

note right of CLOSED
모집 종료
신규 신청 및 대기 등록 불가
재OPEN 불가
end note

@enduml
