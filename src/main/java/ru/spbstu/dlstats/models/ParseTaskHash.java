package ru.spbstu.dlstats.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

@Data
@NoArgsConstructor
@AllArgsConstructor

@Entity
@Table(name = "parse_task_hash")
public class ParseTaskHash {
    @Id
    @Column(name = "task_num")
    private Long id;

    @Column(name = "task_hash")
    private String taskHash;

    public static String calculateHash(String content) {
        try {
            var digest = MessageDigest.getInstance("MD5");
            byte[] hashBytes = digest.digest(content.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hashBytes);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Неподдерживаемый алгоритм хеширования", e);
        }
    }


    public void setTaskHashByContent(String content) {
        this.taskHash = calculateHash(content);
    }
}
