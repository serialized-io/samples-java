package io.serialized.samples.encryption.crypto;

import java.io.Serializable;

public interface EncryptionService {

  String encrypt(byte[] key, Serializable object);

  Serializable decrypt(byte[] key, String encrypted);

}
