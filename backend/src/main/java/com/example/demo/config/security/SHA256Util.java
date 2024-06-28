package com.example.demo.config.security;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

@Slf4j
@Component
public class SHA256Util {
    /*
    공기관 원칙에 따라 패스워드 암호화 방식을 PBKDF2 -> SHA256 방식으로 변경, salt 추가.
    일방향 해시 암호화이므로 복호화 불가함에 주의.
     */

    /**
     * 패스워드 암호화. 순서는 salt+input. salt 를 별도로 반환하지 않으면 로그인 인증이 불가능하므로 메서드 분리 사용.
     *
     * @param input salt + 사용자 입력값
     * @return salt + 사용자 입력값 해싱 패스워드
     * @throws NoSuchAlgorithmException
     */
    private static String createHash(String input) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] encodedHash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
        return bytesToHex(encodedHash);
    }

    // 16바이트 길이의 무작위 솔트를 생성하고 Base64로 인코딩
    private static String generateSalt() {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[16];
        random.nextBytes(salt);
        return Base64.getEncoder().encodeToString(salt);
    }

    /**
     * 솔트 추가된 패스워드 생성 후 각각 리턴
     *
     * @param inputPw 사용자 입력 패스워드
     * @return get (salt, salted)
     */
    public static PwDto createPw(String inputPw) {
        PwDto pwDto = new PwDto();
        try {
            String salt = generateSalt();
            String salted = createHash(salt + inputPw);

            pwDto.setSalt(salt);
            pwDto.setSalted(salted);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return pwDto;
    }


    /**
     * 인증 시 패스워드 일치여부 검사
     *
     * @param salt          salt
     * @param inputPassword 사용자 입력값
     * @param dbPw          db 저장 패스워드
     * @return T: 일치, F: 불일치
     * @throws NoSuchAlgorithmException
     */
    public static boolean validatePassword(String salt, String inputPassword, String dbPw) {
//        솔트와 패스워드를 비교해 검증
        boolean isMatch = true;
        try {
            String salted = SHA256Util.createHash(salt + inputPassword);
            isMatch = dbPw.equals(salted);
        } catch (NoSuchAlgorithmException e) {
            log.error("로그인 패스워드 비교 중 예외 발생했습니다.");
        }
        return isMatch;
    }

    // 암호화 로직
    private static String bytesToHex(byte[] hash) {
        StringBuilder hexString = new StringBuilder(2 * hash.length);
        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }

//    패스워드 DTO
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PwDto {
        private String salt;
        private String salted;
    }

}