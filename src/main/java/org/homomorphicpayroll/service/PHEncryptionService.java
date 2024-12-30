package org.homomorphicpayroll.service;

import com.n1analytics.paillier.EncryptedNumber;

import java.util.List;

public interface PHEncryptionService {

    EncryptedNumber encrypt(double value);

    double decrypt(EncryptedNumber encryptedValue);

    EncryptedNumber addEncryptedNumbers(List<EncryptedNumber> encryptedNumbers);

    EncryptedNumber multiplyEncryptedByScalar(EncryptedNumber encryptedValue, double scalar);
}
