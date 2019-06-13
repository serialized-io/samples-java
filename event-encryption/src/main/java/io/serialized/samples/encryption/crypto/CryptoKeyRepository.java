package io.serialized.samples.encryption.crypto;

import java.util.UUID;

public interface CryptoKeyRepository {

  byte[] getSecretKey(UUID uuid);

}
