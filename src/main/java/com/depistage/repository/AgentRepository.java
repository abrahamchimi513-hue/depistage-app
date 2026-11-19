package com.depistage.repository;

import com.depistage.entity.Agent;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AgentRepository extends JpaRepository<Agent, Long> {

    boolean existsByTelephone(String telephone);

    boolean existsByTelephoneAndIdNot(String telephone, Long id);

    List<Agent> findByActifTrueOrderByNom();

    @Query("select a from Agent a where lower(a.nom) like lower(concat('%', :q, '%')) "
            + "or lower(a.prenom) like lower(concat('%', :q, '%')) "
            + "or a.telephone like concat('%', :q, '%')")
    Page<Agent> search(@Param("q") String q, Pageable pageable);
}
