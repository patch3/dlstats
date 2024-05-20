package ru.spbstu.dlstats.repositorys;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.spbstu.dlstats.models.ParseTaskHash;

@Repository
public interface ParseTaskHashRepository extends JpaRepository<ParseTaskHash, Long> {

}
