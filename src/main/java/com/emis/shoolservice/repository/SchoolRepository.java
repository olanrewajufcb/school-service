package com.emis.shoolservice.repository;

import com.emis.shoolservice.domain.db.School;
import com.emis.shoolservice.enums.SchoolType;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.repository.query.Param;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface SchoolRepository extends R2dbcRepository<School, Long> {

    @Query("SELECT * FROM schools WHERE type = :type AND status = 'ACTIVE'")
    Flux<School> findActiveByType(SchoolType type);



    @Query("SELECT EXISTS(SELECT 1 FROM schools WHERE id = :schoolId AND status = 'ACTIVE')")
    Mono<Boolean> existsActiveById(Long schoolId);

    @Query("SELECT * FROM schools WHERE name ILIKE :namePattern")
    Flux<School> searchByName(String namePattern);

    @Query("""
        SELECT s.*, COUNT(st.id) as student_count 
        FROM schools s 
        LEFT JOIN students st ON s.id = st.school_id 
        WHERE s.status = 'ACTIVE' 
        GROUP BY s.id 
        HAVING COUNT(st.id) < :maxCapacity 
        ORDER BY student_count DESC
    """)
    Flux<School> findActiveSchoolsWithCapacity(int maxCapacity);

    Mono<School> findBySchoolCode(String schoolCode);

    @Query("""
        SELECT * FROM schools 
        WHERE status = 'ACTIVE' 
        ORDER BY school_code 
        LIMIT :size OFFSET :offset
    """)
    Flux<School> findAllSchools(int size, long offset);

    @Query("""
        SELECT COUNT(*) FROM schools 
        WHERE status = 'ACTIVE'
    """)
    Mono<Long> countAllSchools();

    @Query("""
        INSERT INTO school_schema.schools (
            school_code, school_name, type, school_level, status, 
            address, phone, email, principal_name, max_students_per_class, 
            school_capacity, academic_calendar, established_year, location, 
            ward, lga, city, state, created_at, updated_at
        ) VALUES (
            :#{#school.schoolCode}, 
            :#{#school.schoolName}, 
            CAST(:#{#school.type} AS school_schema.school_type),
            CAST(:#{#school.schoolLevel} AS school_schema.school_level),
            CAST(:#{#school.status} AS school_schema.school_status),
            :#{#school.address}, 
            :#{#school.phone}, 
            :#{#school.email}, 
            :#{#school.principalName}, 
            :#{#school.maxStudentsPerClass}, 
            :#{#school.schoolCapacity}, 
            CAST(:#{#school.academicCalendar} AS school_schema.academic_calendar_type),
            :#{#school.establishedYear}, 
            CAST(:#{#school.location} AS school_schema.location_type),
            :#{#school.ward}, 
            :#{#school.lga}, 
            :#{#school.city}, 
            :#{#school.state}, 
            :#{#school.createdAt}, 
            :#{#school.updatedAt}
        )
        RETURNING *
    """)
    Mono<School> insertSchool(@Param("school") School school);

    @Query("""
        UPDATE school_schema.schools SET
            school_name = :#{#school.schoolName},
            type = CAST(:#{#school.type} AS school_schema.school_type),
            school_level = CAST(:#{#school.schoolLevel} AS school_schema.school_level),
            status = CAST(:#{#school.status} AS school_schema.school_status),
            address = :#{#school.address},
            phone = :#{#school.phone},
            email = :#{#school.email},
            principal_name = :#{#school.principalName},
            max_students_per_class = :#{#school.maxStudentsPerClass},
            school_capacity = :#{#school.schoolCapacity},
            academic_calendar = CAST(:#{#school.academicCalendar} AS school_schema.academic_calendar_type),
            established_year = :#{#school.establishedYear},
            location = :#{#school.location},
            ward = :#{#school.ward},
            lga = :#{#school.lga},
            city = :#{#school.city},
            state = :#{#school.state},
            updated_at = :#{#school.updatedAt}
        WHERE school_id = :#{#school.schoolId}
        RETURNING *
    """)
    Mono<School> updateSchool(@Param("school") School school);

}
