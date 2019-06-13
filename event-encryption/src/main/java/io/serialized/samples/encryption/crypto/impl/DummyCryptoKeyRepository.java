package io.serialized.samples.encryption.crypto.impl;

import io.serialized.samples.encryption.crypto.CryptoKeyRepository;

import java.util.UUID;

public class DummyCryptoKeyRepository implements CryptoKeyRepository {

  @Override
  public byte[] getSecretKey(UUID uuid) {
    // Returning a fake and deterministic secret key based on the input UUID.
    return uuid.toString().replaceAll("-", "").getBytes();
  }

}
