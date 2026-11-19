package com.depistage.repository;

import com.depistage.entity.Campagne;
import com.depistage.enums.StatutCampagne;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CampagneRepository extends JpaRepository<Campagne, Long> {

    boolean existsByNomIgnoreCase(String nom);

    boolean existsByNomIgnoreCaseAndIdNot(String nom, Long id);

    boolean existsByTypesId(Long typeId);

    long countByStatut(StatutCampagne statut);

    List<Campagne> findByStatutOrderByNom(StatutCampagne statut);

    @Query("select c from Campagne c where lower(c.nom) like lower(concat('%', :q, '%')) "
            + "or lower(c.ville) like lower(concat('%', :q, '%'))")
    Page<Campagne> search(@Param("q") String q, Pageable pageable);

    @Query("select c from Campagne c left join fetch c.types where c.id = :id")
    Optional<Campagne> findWithTypes(@Param("id") Long id);
}
