/**
 * Copyright (C) 2000-2017 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.datasource.xa;


class StringUtils {
    
  private static final byte[] HEX_CHAR_TABLE = {
    (byte)'0', (byte)'1', (byte)'2', (byte)'3',
    (byte)'4', (byte)'5', (byte)'6', (byte)'7',
    (byte)'8', (byte)'9', (byte)'A', (byte)'B',
    (byte)'C', (byte)'D', (byte)'E', (byte)'F'
  };    

  static String byteArrayToHexString(byte[] raw) 
  {
    byte[] hex = new byte[2 * raw.length];
    int index = 0;

    for (byte b : raw) {
      int v = b & 0xFF;
      hex[index++] = HEX_CHAR_TABLE[v >>> 4];
      hex[index++] = HEX_CHAR_TABLE[v & 0xF];
    }
    return new String(hex);
  }

 
}
