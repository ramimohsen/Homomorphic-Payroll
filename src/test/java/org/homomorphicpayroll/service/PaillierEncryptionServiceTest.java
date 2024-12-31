package org.homomorphicpayroll.service;

import com.n1analytics.paillier.EncryptedNumber;
import com.n1analytics.paillier.PaillierContext;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PaillierEncryptionServiceTest {

    private final PaillierEncryptionService paillierEncryptionService = new PaillierEncryptionService();

    @Test
    void testEncrypt() {
        double value = 123.45;
        EncryptedNumber encryptedNumber = paillierEncryptionService.encrypt(value);
        assertNotNull(encryptedNumber);
    }

    @Test
    void testAddEncryptedNumbersThrowsExceptionForEmptyList() {
        List<EncryptedNumber> emptyList = List.of();
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            paillierEncryptionService.addEncryptedNumbers(emptyList);
        });
        assertEquals("List of encrypted numbers is empty", exception.getMessage());
    }

    @Test
    void testDecrypt() {
        double value = 123.45;
        EncryptedNumber encryptedNumber = paillierEncryptionService.encrypt(value);
        double decryptedValue = paillierEncryptionService.decrypt(encryptedNumber);
        assertEquals(value, decryptedValue, 0.001);
    }

    @Test
    void testAddEncryptedNumbers() {
        EncryptedNumber num1 = paillierEncryptionService.encrypt(100.0);
        EncryptedNumber num2 = paillierEncryptionService.encrypt(200.0);
        EncryptedNumber sum = paillierEncryptionService.addEncryptedNumbers(List.of(num1, num2));
        double decryptedSum = paillierEncryptionService.decrypt(sum);
        assertEquals(300.0, decryptedSum, 0.001);
    }

    @Test
    void testMultiplyEncryptedByScalar() {
        EncryptedNumber num = paillierEncryptionService.encrypt(100.0);
        EncryptedNumber product = paillierEncryptionService.multiplyEncryptedByScalar(num, 2.0);
        double decryptedProduct = paillierEncryptionService.decrypt(product);
        assertEquals(200.0, decryptedProduct, 0.001);
    }

    @Test
    void testDeserializeEncryptedNumber() {
        EncryptedNumber encryptedNumber = paillierEncryptionService.encrypt(123.45);
        String serialized = paillierEncryptionService.serializeEncryptedNumber(encryptedNumber);
        EncryptedNumber deserialized = paillierEncryptionService.deserializeEncryptedNumber(serialized, encryptedNumber.getContext());
        double decryptedValue = paillierEncryptionService.decrypt(deserialized);
        assertEquals(123.45, decryptedValue, 0.001);
    }

    @Test
    void testDeserializeEncryptedNumberWithInvalidFormat() {
        String invalidSerialized = "invalid_format";
        PaillierContext context = paillierEncryptionService.getContext();
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            paillierEncryptionService.deserializeEncryptedNumber(invalidSerialized, context);
        });
        assertEquals("Invalid serialized format for EncryptedNumber", exception.getMessage());
    }

    @Test
    void testCheckKeysInitializedThrowsException() throws Exception {
        PaillierEncryptionService serviceWithNullKeys = new PaillierEncryptionService();

        // Use reflection to set private and final fields to null
        Field privateKeyField = PaillierEncryptionService.class.getDeclaredField("privateKey");
        privateKeyField.setAccessible(true);
        privateKeyField.set(serviceWithNullKeys, null);

        Field publicKeyField = PaillierEncryptionService.class.getDeclaredField("publicKey");
        publicKeyField.setAccessible(true);
        publicKeyField.set(serviceWithNullKeys, null);

        Field paillierContextField = PaillierEncryptionService.class.getDeclaredField("paillierContext");
        paillierContextField.setAccessible(true);
        paillierContextField.set(serviceWithNullKeys, null);

        Exception exception = assertThrows(IllegalStateException.class, serviceWithNullKeys::checkKeysInitialized);
        assertEquals("Paillier keys or context are not initialized. Call initializeKeys() first.", exception.getMessage());
    }

    @Test
    void testCheckKeysInitializedThrowsExceptionWhenPrivateKeyIsNull() throws Exception {
        PaillierEncryptionService serviceWithNullPrivateKey = new PaillierEncryptionService();

        // Use reflection to set privateKey to null
        Field privateKeyField = PaillierEncryptionService.class.getDeclaredField("privateKey");
        privateKeyField.setAccessible(true);
        privateKeyField.set(serviceWithNullPrivateKey, null);

        Exception exception = assertThrows(IllegalStateException.class, serviceWithNullPrivateKey::checkKeysInitialized);
        assertEquals("Paillier keys or context are not initialized. Call initializeKeys() first.", exception.getMessage());
    }

    @Test
    void testCheckKeysInitializedThrowsExceptionWhenPublicKeyIsNull() throws Exception {
        PaillierEncryptionService serviceWithNullPublicKey = new PaillierEncryptionService();

        // Use reflection to set publicKey to null
        Field publicKeyField = PaillierEncryptionService.class.getDeclaredField("publicKey");
        publicKeyField.setAccessible(true);
        publicKeyField.set(serviceWithNullPublicKey, null);

        Exception exception = assertThrows(IllegalStateException.class, serviceWithNullPublicKey::checkKeysInitialized);
        assertEquals("Paillier keys or context are not initialized. Call initializeKeys() first.", exception.getMessage());
    }

    @Test
    void testCheckKeysInitializedThrowsExceptionWhenPaillierContextIsNull() throws Exception {
        PaillierEncryptionService serviceWithNullContext = new PaillierEncryptionService();

        // Use reflection to set paillierContext to null
        Field paillierContextField = PaillierEncryptionService.class.getDeclaredField("paillierContext");
        paillierContextField.setAccessible(true);
        paillierContextField.set(serviceWithNullContext, null);

        Exception exception = assertThrows(IllegalStateException.class, serviceWithNullContext::checkKeysInitialized);
        assertEquals("Paillier keys or context are not initialized. Call initializeKeys() first.", exception.getMessage());
    }
}