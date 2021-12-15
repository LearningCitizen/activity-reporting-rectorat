package com.jhippolyte.service;

import org.jasypt.encryption.pbe.PooledPBEStringEncryptor;
import org.jasypt.iv.RandomIvGenerator;
import org.junit.jupiter.api.Test;

public class ClassTest {

    @Test
    public void should_encrypt(){
        PooledPBEStringEncryptor encryptor = new PooledPBEStringEncryptor();
        encryptor.setAlgorithm("PBEWithHmacSHA512AndAES_256");
        encryptor.setIvGenerator(new RandomIvGenerator());
        encryptor.setPoolSize(4);
        encryptor.setPassword("WxR9XR+q2qV=s8+>HeT8qY%kr7L^6835si@^E3~@");
        String encrypted = encryptor.encrypt("zzsvCzFVPGxdJr6uo_H_");
        String decrypted = encryptor.decrypt(encrypted);
        System.out.println("encrypted : "+encrypted+" "+decrypted);
    }
}
