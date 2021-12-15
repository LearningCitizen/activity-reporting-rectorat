package com.jhippolyte.config;

import org.jasypt.encryption.StringEncryptor;
import org.jasypt.encryption.pbe.PooledPBEStringEncryptor;
import org.jasypt.iv.RandomIvGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EncryptorConfiguration {

    @Bean(value="encryptorBean")
    public StringEncryptor stringEncryptor() {
        PooledPBEStringEncryptor encryptor = new PooledPBEStringEncryptor();
        encryptor.setAlgorithm("PBEWithHmacSHA512AndAES_256");
        encryptor.setIvGenerator(new RandomIvGenerator());
        encryptor.setPoolSize(4);
        encryptor.setPassword("WxR9XR+q2qV=s8+>HeT8qY%kr7L^6835si@^E3~@");
        return encryptor;
    }
}
