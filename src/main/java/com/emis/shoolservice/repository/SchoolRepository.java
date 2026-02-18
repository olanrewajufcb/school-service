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

    @Query("SELECT EXISTS(SELECT 1 FROM schools WHERE school_code = :schoolCode AND status = 'ACTIVE')")
    Mono<Boolean> existsActiveBySchoolCode(String schoolCode);

}
