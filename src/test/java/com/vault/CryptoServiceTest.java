package com.vault;
import com.vault.service.CryptoService;import org.junit.jupiter.api.Test;import static org.junit.jupiter.api.Assertions.*;
class CryptoServiceTest{@Test void encryptsAndDecryptsWithoutStoringPlaintext(){CryptoService crypto=new CryptoService("0123456789abcdef0123456789abcdef");String secret="sk-test-not-a-real-credential";String ciphertext=crypto.encrypt(secret);assertNotEquals(secret,ciphertext);assertFalse(ciphertext.contains(secret));assertEquals(secret,crypto.decrypt(ciphertext));assertNotEquals(ciphertext,crypto.encrypt(secret));}}
