package com.depistage.repository;

import com.depistage.entity.Depistage;
import com.depistage.enums.Resultat;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface DepistageRepository extends JpaRepository<Depistage, Long>, JpaSpecificationExecutor<Depistage> {

    boolean existsBySiteId(Long siteId);

    boolean existsByAgentId(Long agentId);

    boolean existsByParticipantId(Long participantId);

    boolean existsByTypeId(Long typeId);

    @Query("select count(d) > 0 from Depistage d where d.participant.id = :participantId "
            + "and d.type.id = :typeId and d.site.campagne.id = :campagneId")
    boolean existsDoublon(@Param("participantId") Long participantId,
                          @Param("typeId") Long typeId,
                          @Param("campagneId") Long campagneId);

    long countByResultat(Resultat resultat);

    // ----- Agrégations pour les tableaux de bord (tout se calcule dans la base) -----

    @Query("select t.libelle, count(d) from Depistage d join d.type t group by t.id, t.libelle order by t.libelle")
    List<Object[]> countParType();

    @Query("select d.resultat, count(d) from Depistage d group by d.resultat")
    List<Object[]> countParResultat();

    @Query("select s.nom, count(d) from Depistage d join d.site s group by s.id, s.nom order by s.nom")
    List<Object[]> countParSite();

    @Query("select s.campagne.id, count(d) from Depistage d join d.site s group by s.campagne.id")
    List<Object[]> countParCampagne();

    @Query("select count(d) from Depistage d where d.site.campagne.id = :campagneId")
    long countByCampagne(@Param("campagneId") Long campagneId);

    @Query("select d.resultat, count(d) from Depistage d where d.site.campagne.id = :campagneId group by d.resultat")
    List<Object[]> countParResultatPourCampagne(@Param("campagneId") Long campagneId);

    // ----- Listes : le graphe d'entités charge tout en une requête (pas de N+1) -----

    @EntityGraph(attributePaths = {"participant", "agent", "site", "site.campagne", "type"})
    Page<Depistage> findByResultatIn(Collection<Resultat> resultats, Pageable pageable);

    @Override
    @EntityGraph(attributePaths = {"participant", "agent", "site", "site.campagne", "type"})
    Page<Depistage> findAll(Specification<Depistage> spec, Pageable pageable);

    @Override
    @EntityGraph(attributePaths = {"participant", "agent", "site", "site.campagne", "type"})
    Optional<Depistage> findById(Long id);
}
