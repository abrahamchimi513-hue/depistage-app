package com.depistage.repository;

import com.depistage.entity.Participant;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ParticipantRepository extends JpaRepository<Participant, Long> {

    List<Participant> findAllByOrderByNomAscPrenomAsc();

    @Query("select p from Participant p where lower(p.nom) like lower(concat('%', :q, '%')) "
            + "or lower(p.prenom) like lower(concat('%', :q, '%')) "
            + "or lower(p.quartier) like lower(concat('%', :q, '%'))")
    Page<Participant> search(@Param("q") String q, Pageable pageable);
}
