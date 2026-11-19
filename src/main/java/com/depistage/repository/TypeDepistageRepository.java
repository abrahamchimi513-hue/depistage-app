package com.depistage.repository;

import com.depistage.entity.TypeDepistage;
import com.depistage.enums.StatutCampagne;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TypeDepistageRepository extends JpaRepository<TypeDepistage, Long> {

    boolean existsByCode(String code);

    boolean existsByCodeAndIdNot(String code, Long id);

    List<TypeDepistage> findAllByOrderByLibelle();

    @Query("select t from TypeDepistage t where lower(t.code) like lower(concat('%', :q, '%')) "
            + "or lower(t.libelle) like lower(concat('%', :q, '%'))")
    Page<TypeDepistage> search(@Param("q") String q, Pageable pageable);

    @Query("select distinct t from Campagne c join c.types t where c.statut = :statut order by t.libelle")
    List<TypeDepistage> findProposesParCampagnesDeStatut(@Param("statut") StatutCampagne statut);
}
