@startuml

actor "수강생" as Student
participant "EnrollmentCommandController" as Controller
participant "EnrollmentCommandService" as Service
participant "LecturePolicy" as LecturePolicy
participant "EnrollmentPolicy" as EnrollmentPolicy
participant "CapacityPolicy" as CapacityPolicy
participant "LectureRepository" as LectureRepo
participant "EnrollmentRepository" as EnrollmentRepo
participant "EntityManager" as EM
database "DB" as DB

Student -> Controller : POST /lectures/{lectureId}/enrollments\nuserId header
Controller -> Service : apply(userId, lectureId)

Service -> LectureRepo : findByIdForUpdate(lectureId)
LectureRepo -> DB : SELECT lecture FOR UPDATE
DB --> LectureRepo : Lecture
LectureRepo --> Service : Lecture

Service -> LecturePolicy : validateRecruitmentOpen(lecture, now)
Service -> EnrollmentPolicy : validateApplicantNotCreator(creatorId, userId)

Service -> EnrollmentRepo : getEnrollValidation(lectureId, userId, ACTIVE_STATUSES, OCCUPIED_STATUSES)
EnrollmentRepo -> DB : SELECT occupiedCount, hasActiveEnrollment
DB --> EnrollmentRepo : EnrollValidation
EnrollmentRepo --> Service : EnrollValidation

alt hasActiveEnrollment = true
Service --> Controller : throw IllegalArgumentException
Controller --> Student : error response
else hasActiveEnrollment = false
Service -> EM : getReference(AppUser.class, userId)
EM --> Service : AppUser reference

Service -> CapacityPolicy : hasAvailableSeat(lecture, enrollValidation.occupiedCount())

alt occupiedCount < capacity
Service -> Service : Enrollment.pending(..., paymentDueAt = min(now + 24h, recruitmentEndAt))
Service -> EnrollmentRepo : save(PENDING enrollment)
EnrollmentRepo -> DB : INSERT enrollment
DB --> EnrollmentRepo : saved
EnrollmentRepo --> Service : Enrollment
Service --> Controller : ApplyEnrollmentResponse(PENDING)
Controller --> Student : 200 OK
else occupiedCount >= capacity
Service -> Service : Enrollment.waitlisted(..., waitlistedAt = now)
Service -> EnrollmentRepo : save(WAITLISTED enrollment)
EnrollmentRepo -> DB : INSERT enrollment
DB --> EnrollmentRepo : saved
EnrollmentRepo --> Service : Enrollment
Service --> Controller : ApplyEnrollmentResponse(WAITLISTED)
Controller --> Student : 200 OK
end
end

@enduml


@startuml

actor "수강생" as Student
participant "EnrollmentCommandController" as Controller
participant "EnrollmentCommandService" as Service
participant "EnrollmentPolicy" as EnrollmentPolicy
participant "WaitlistPolicy" as WaitlistPolicy
participant "EnrollmentRepository" as EnrollmentRepo
participant "LectureRepository" as LectureRepo
database "DB" as DB

Student -> Controller : POST /enrollments/{id}/cancel\nuserId header
Controller -> Service : cancel(userId, enrollmentId)

Service -> EnrollmentRepo : findByIdForUpdate(enrollmentId)
EnrollmentRepo -> DB : SELECT enrollment FOR UPDATE
DB --> EnrollmentRepo : Enrollment
EnrollmentRepo --> Service : Enrollment

Service -> LectureRepo : findByIdForUpdate(lectureId)
LectureRepo -> DB : SELECT lecture FOR UPDATE
DB --> LectureRepo : Lecture
LectureRepo --> Service : Lecture

Service -> EnrollmentPolicy : releasesSeat(enrollment)
EnrollmentPolicy --> Service : true / false

Service -> EnrollmentPolicy : validateCancelable(enrollment, userId, now)

alt cancel not allowed
Service --> Controller : throw IllegalArgumentException
Controller --> Student : error response
else cancel allowed
Service -> Service : enrollment.cancel(now)

alt cancelled status was PENDING or CONFIRMED
Service -> WaitlistPolicy : canPromote(lecture, now)
WaitlistPolicy --> Service : true / false

    alt lecture is OPEN and recruiting
    Service -> EnrollmentRepo : findFirstWaitlistedForUpdate(lectureId, WAITLISTED, PageRequest.of(0, 1))
    EnrollmentRepo -> DB : SELECT first WAITLISTED FOR UPDATE
    DB --> EnrollmentRepo : Waitlisted Enrollment or empty
    EnrollmentRepo --> Service : result

        alt waitlisted exists
        Service -> WaitlistPolicy : validateWaitlisted(waitlisted)
        Service -> Service : waitlisted.promoteToPending(now + 1 day)
        else no waitlisted
        Service -> Service : no promotion
        end
    else lecture not open or not recruiting
    Service -> Service : no promotion
    end
else cancelled status was WAITLISTED
Service -> Service : no seat released
end

Service --> Controller : CancelEnrollmentResponse(CANCELLED)
Controller --> Student : 200 OK
end

@enduml
