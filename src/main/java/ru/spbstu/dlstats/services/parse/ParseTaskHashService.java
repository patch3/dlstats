package ru.spbstu.dlstats.services.parse;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.spbstu.dlstats.models.ParseTaskHash;
import ru.spbstu.dlstats.repositorys.ParseTaskHashRepository;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Optional;

@Service
public class ParseTaskHashService {
    private final ParseTaskHashRepository parseTaskHashRepository;

    @Autowired
    public ParseTaskHashService(ParseTaskHashRepository parseTaskHashRepository) {
        this.parseTaskHashRepository = parseTaskHashRepository;
    }

    public void save(Long id, String text) {
        this.parseTaskHashRepository.save(new ParseTaskHash(id, ParseTaskHash.calculateHash(text)));
    }

    public void save(ParseTaskHash parseTaskHash) {
        this.parseTaskHashRepository.save(parseTaskHash);
    }


    public Optional<ParseTaskHash> findById(Long id) {
        return this.parseTaskHashRepository.findById(id);
    }

    public boolean isHashInDatabase(Long id, String text) {
        var parseTaskHash = this.parseTaskHashRepository.findById(id);
        return parseTaskHash.isPresent()
                && parseTaskHash.get().getTaskHash().equals(ParseTaskHash.calculateHash(text));
    }



}
