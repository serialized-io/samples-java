package io.serialized.samples.encryption.crypto.impl;

import io.serialized.samples.encryption.crypto.EncryptionService;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.SealedObject;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import static org.apache.commons.codec.binary.Hex.decodeHex;
import static org.apache.commons.codec.binary.Hex.encodeHexString;

public class AesEncryptionService implements EncryptionService {

  private final String transformation = "AES";

  @Override
  public String encrypt(byte[] key, Serializable object) {
    try {
      ByteArrayOutputStream os = new ByteArrayOutputStream();
      SecretKey secretKey = new SecretKeySpec(key, transformation);
      Cipher cipher = Cipher.getInstance(transformation);
      cipher.init(Cipher.ENCRYPT_MODE, secretKey);
      SealedObject sealedObject = new SealedObject(object, cipher);
      ObjectOutputStream outputStream = new ObjectOutputStream(new CipherOutputStream(os, cipher));
      outputStream.writeObject(sealedObject);
      outputStream.close();
      return encodeHexString(os.toByteArray());
    } catch (Exception e) {
      throw new IllegalArgumentException("Failed to encrypt data", e);
    }
  }

  @Override
  public Serializable decrypt(byte[] key, String encrypted) {
    try {
      ByteArrayInputStream is = new ByteArrayInputStream(decodeHex(encrypted.toCharArray()));
      SecretKey secretKey = new SecretKeySpec(key, transformation);
      Cipher cipher = Cipher.getInstance(transformation);
      cipher.init(Cipher.DECRYPT_MODE, secretKey);
      ObjectInputStream inputStream = new ObjectInputStream(new CipherInputStream(is, cipher));
      SealedObject sealedObject = (SealedObject) inputStream.readObject();
      return (Serializable) sealedObject.getObject(cipher);
    } catch (Exception e) {
      throw new IllegalArgumentException("Failed to decrypt data", e);
    }
  }

}
