package com.unclezs.novel.app.jfx.plugin.packager.util;

/**
 * Signer helper exception
 */
@SuppressWarnings("serial")
public class SignerException extends Exception {

  public SignerException(String message) {
    super(message);
  }

  public SignerException(String message, Throwable cause) {
    super(message, cause);
  }

}
