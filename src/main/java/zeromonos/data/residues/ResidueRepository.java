package zeromonos.data.residues;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ResidueRepository extends JpaRepository<Residue, Long> {

    Optional<Residue> findByName(String name);

    @Query("SELECT r FROM Residue r WHERE LOWER(r.name) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(r.desc) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<Residue> findAllBySimilarNameOrDesc(@Param("query") String query);

}
