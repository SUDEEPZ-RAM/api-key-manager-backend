package com.vault;
import com.vault.entity.ApiKey;import org.junit.jupiter.api.Test;import java.time.Instant;import static org.junit.jupiter.api.Assertions.*;
class ApiKeyExpirationTest{@Test void effectiveStatusExpiresActiveKeyAtDeadline(){ApiKey key=new ApiKey(null,"OpenAI","test","ciphertext","Production",null,Instant.now().minusSeconds(1));assertEquals("EXPIRED",key.effectiveStatus());} @Test void futureExpirationRemainsActive(){ApiKey key=new ApiKey(null,"OpenAI","test","ciphertext","Production",null,Instant.now().plusSeconds(3600));assertEquals("ACTIVE",key.effectiveStatus());}}
