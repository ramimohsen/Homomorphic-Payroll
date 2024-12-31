package org.homomorphicpayroll.service;


import com.n1analytics.paillier.EncryptedNumber;
import com.n1analytics.paillier.PaillierContext;
import com.n1analytics.paillier.PaillierPrivateKey;
import com.n1analytics.paillier.PaillierPublicKey;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.List;

@Service
public class PaillierEncryptionService implements PHEncryptionService {

    private final PaillierPrivateKey privateKey; // Holds the private key
    private final PaillierPublicKey publicKey;  // Holds the public key
    private final PaillierContext paillierContext; // Context for encryption and operations

    /**
     * Initializes the Paillier keys and context using the injected key size.
     */
    public PaillierEncryptionService() {
        this.privateKey = PaillierPrivateKey.create(1024); // Create a private key
        this.publicKey = privateKey.getPublicKey();
        this.paillierContext = publicKey.createSignedContext(); // Create a signed context
    }

    /**
     * Encrypts a double value using the Paillier public key and context.
     */
    public EncryptedNumber encrypt(double value) {
        checkKeysInitialized();
        return paillierContext.encrypt(value);
    }

    /**
     * Decrypts an encrypted number back to a double value using the Paillier private key.
     */
    public double decrypt(EncryptedNumber encryptedValue) {
        checkKeysInitialized();
        return privateKey.decrypt(encryptedValue).decodeDouble();
    }

    /**
     * Adds a list of encrypted numbers and returns the resulting encrypted sum.
     */
    public EncryptedNumber addEncryptedNumbers(List<EncryptedNumber> encryptedNumbers) {
        checkKeysInitialized();
        return encryptedNumbers.stream().reduce(EncryptedNumber::add)
                .orElseThrow(() -> new IllegalArgumentException("List of encrypted numbers is empty"));
    }

    /**
     * Multiplies an encrypted number by a scalar and returns the resulting encrypted product.
     */
    public EncryptedNumber multiplyEncryptedByScalar(EncryptedNumber encryptedValue, double scalar) {
        checkKeysInitialized();
        return encryptedValue.multiply(scalar);
    }

    @Override
    public String serializeEncryptedNumber(EncryptedNumber encryptedNumber) {
        return encryptedNumber.calculateCiphertext().toString() + ":" + encryptedNumber.getExponent();
    }

    @Override
    public EncryptedNumber deserializeEncryptedNumber(String serialized, PaillierContext context) {
        String[] parts = serialized.split(":");
        if (parts.length != 2) {
            throw new IllegalArgumentException("Invalid serialized format for EncryptedNumber");
        }
        BigInteger ciphertext = new BigInteger(parts[0]);
        int exponent = Integer.parseInt(parts[1]);
        return new EncryptedNumber(context, ciphertext, exponent);
    }

    void checkKeysInitialized() {
        if (privateKey == null || publicKey == null || paillierContext == null) {
            throw new IllegalStateException("Paillier keys or context are not initialized. Call initializeKeys() first.");
        }
    }

    @Override
    public PaillierContext getContext() {
        return this.paillierContext;
    }
}
