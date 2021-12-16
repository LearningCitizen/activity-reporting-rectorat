package config;

import org.assertj.core.api.Assertions;
import org.jasypt.encryption.pbe.PooledPBEStringEncryptor;
import org.jasypt.iv.RandomIvGenerator;
import org.junit.jupiter.api.Test;

public class EncryptorTest {

    @Test
    public void should_encrypt(){
        PooledPBEStringEncryptor encryptor = new PooledPBEStringEncryptor();
        encryptor.setAlgorithm("PBEWithHmacSHA512AndAES_256");
        encryptor.setIvGenerator(new RandomIvGenerator());
        encryptor.setPoolSize(4);
        encryptor.setPassword("test");
        String data = "myData";
        String encrypted = encryptor.encrypt(data);
        Assertions.assertThat(encryptor.decrypt(encrypted)).isEqualTo(data);
    }
}
