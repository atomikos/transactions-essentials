package com.atomikos.datasource.xa;


public class StringUtils {
    
  static final byte[] HEX_CHAR_TABLE = {
    (byte)'0', (byte)'1', (byte)'2', (byte)'3',
    (byte)'4', (byte)'5', (byte)'6', (byte)'7',
    (byte)'8', (byte)'9', (byte)'A', (byte)'B',
    (byte)'C', (byte)'D', (byte)'E', (byte)'F'
  };    

  public static String getHexString(byte[] raw) 
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

  public static void main(String args[]) throws Exception{
    byte[] byteArray = {
      (byte)255, (byte)254, (byte)253, 
      (byte)252, (byte)251, (byte)250
    };

    System.out.println(StringUtils.getHexString(byteArray));
    
    /*
     * output :
     *   fffefdfcfbfa
     */
    
  }
}
