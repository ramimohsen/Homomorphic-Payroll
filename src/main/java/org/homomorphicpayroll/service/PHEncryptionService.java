package org.homomorphicpayroll.service;

import com.n1analytics.paillier.EncryptedNumber;
import com.n1analytics.paillier.PaillierContext;

import java.util.List;

public interface PHEncryptionService {

    EncryptedNumber encrypt(double value);

    double decrypt(EncryptedNumber encryptedValue);

    EncryptedNumber addEncryptedNumbers(List<EncryptedNumber> encryptedNumbers);

    EncryptedNumber multiplyEncryptedByScalar(EncryptedNumber encryptedValue, double scalar);

    String serializeEncryptedNumber(EncryptedNumber encryptedNumber);

    EncryptedNumber deserializeEncryptedNumber(String serialized, PaillierContext context);

    PaillierContext getContext();
}
