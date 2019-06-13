package io.serialized.samples.encryption.crypto.impl;

import io.serialized.samples.encryption.crypto.EncryptionService;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;

import static org.apache.commons.codec.binary.Hex.decodeHex;
import static org.apache.commons.codec.binary.Hex.encodeHexString;

public class AesEncryptionService implements EncryptionService {

  private final String transformation = "AES";

  @Override
  public String encrypt(byte[] key, Serializable object) {
    try {
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      SecretKey secretKey = new SecretKeySpec(key, transformation);
      Cipher cipher = Cipher.getInstance(transformation);
      cipher.init(Cipher.ENCRYPT_MODE, secretKey);
      SealedObject sealedObject = new SealedObject(object, cipher);
      ObjectOutputStream outputStream = new ObjectOutputStream(new CipherOutputStream(baos, cipher));
      outputStream.writeObject(sealedObject);
      outputStream.close();
      return encodeHexString(baos.toByteArray());
    } catch (Exception e) {
      throw new IllegalArgumentException("Failed to encrypt data", e);
    }
  }

  @Override
  public Serializable decrypt(byte[] key, String encrypted) {
    try {
      ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(decodeHex(encrypted.toCharArray()));
      SecretKey secretKey = new SecretKeySpec(key, transformation);
      Cipher cipher = Cipher.getInstance(transformation);
      cipher.init(Cipher.DECRYPT_MODE, secretKey);
      ObjectInputStream inputStream = new ObjectInputStream(new CipherInputStream(byteArrayInputStream, cipher));
      SealedObject sealedObject = (SealedObject) inputStream.readObject();
      return (Serializable) sealedObject.getObject(cipher);
    } catch (Exception e) {
      throw new IllegalArgumentException("Failed to decrypt data", e);
    }
  }

}
