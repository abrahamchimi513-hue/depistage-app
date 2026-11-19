package com.depistage.repository;

import com.depistage.entity.Site;
import com.depistage.enums.StatutCampagne;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface SiteRepository extends JpaRepository<Site, Long> {

    boolean existsByCampagneId(Long campagneId);

    /** join fetch : la campagne est chargée avec le site, donc une seule requête pour toute la page. */
    @Query(value = "select s from Site s join fetch s.campagne "
            + "where lower(s.nom) like lower(concat('%', :q, '%')) "
            + "or lower(s.quartier) like lower(concat('%', :q, '%'))",
            countQuery = "select count(s) from Site s "
                    + "where lower(s.nom) like lower(concat('%', :q, '%')) "
                    + "or lower(s.quartier) like lower(concat('%', :q, '%'))")
    Page<Site> search(@Param("q") String q, Pageable pageable);

    @Query("select s from Site s join fetch s.campagne where s.id = :id")
    Optional<Site> findWithCampagne(@Param("id") Long id);

    @EntityGraph(attributePaths = "campagne")
    List<Site> findByCampagneIdOrderByNom(Long campagneId);

    @Query("select s from Site s join fetch s.campagne c where c.statut = :statut order by s.nom")
    List<Site> findByCampagneStatut(@Param("statut") StatutCampagne statut);
}
