package com.atomikos.persistence.dataserializable;

import java.io.DataOutput;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UTFDataFormatException;


public final class DataByteArrayOutputStream extends OutputStream implements DataOutput {
    private static final int DEFAULT_SIZE = 2048;
    private byte buf[];
    private int pos;

    /**
     * Creates a new byte array output stream, with a buffer capacity of the
     * specified size, in bytes.
     * 
     * @param size the initial size.
     * @exception IllegalArgumentException if size is negative.
     */
    public DataByteArrayOutputStream(int size) {
        if (size < 0) {
            throw new IllegalArgumentException("Invalid size: " + size);
        }
        buf = new byte[size];
    }

    /**
     * Creates a new byte array output stream.
     */
    public DataByteArrayOutputStream() {
        this(DEFAULT_SIZE);
    }

    /**
     * start using a fresh byte array
     * 
     * @param size
     */
    public void restart(int size) {
        buf = new byte[size];
        pos = 0;
    }

    /**
     * start using a fresh byte array
     */
    public void restart() {
        restart(DEFAULT_SIZE);
    }

  

    /**
     * Writes the specified byte to this byte array output stream.
     * 
     * @param b the byte to be written.
     */
    public void write(int b) {
        int newcount = pos + 1;
        ensureEnoughBuffer(newcount);
        buf[pos] = (byte)b;
        pos = newcount;
    }

    /**
     * Writes <code>len</code> bytes from the specified byte array starting at
     * offset <code>off</code> to this byte array output stream.
     * 
     * @param b the data.
     * @param off the start offset in the data.
     * @param len the number of bytes to write.
     */
    public void write(byte b[], int off, int len) {
        if (len == 0) {
            return;
        }
        int newcount = pos + len;
        ensureEnoughBuffer(newcount);
        System.arraycopy(b, off, buf, pos, len);
        pos = newcount;
    }

    /**
     * @return the underlying byte[] buffer
     */
    public byte[] getData() {
        return buf;
    }

    /**
     * @return the underlying byte[] buffer
     */
    public byte[] getContent() {
    	byte[] content= new byte[pos];
    	System.arraycopy(buf, 0, content, 0, pos);
        return content;
    }
    /**
     * reset the output stream
     */
    public void reset() {
        pos = 0;
    }

    /**
     * Set the current position for writing
     * 
     * @param offset
     */
    public void position(int offset) {
        ensureEnoughBuffer(offset);
        pos = offset;
    }

    public int size() {
        return pos;
    }

    public void writeBoolean(boolean v) {
        ensureEnoughBuffer(pos + 1);
        buf[pos++] = (byte)(v ? 1 : 0);
    }

    public void writeByte(int v) {
        ensureEnoughBuffer(pos + 1);
        buf[pos++] = (byte)(v >>> 0);
    }

    public void writeShort(int v) {
        ensureEnoughBuffer(pos + 2);
        buf[pos++] = (byte)(v >>> 8);
        buf[pos++] = (byte)(v >>> 0);
    }

    public void writeChar(int v) {
        ensureEnoughBuffer(pos + 2);
        buf[pos++] = (byte)(v >>> 8);
        buf[pos++] = (byte)(v >>> 0);
    }

    public void writeInt(int v) {
        ensureEnoughBuffer(pos + 4);
        buf[pos++] = (byte)(v >>> 24);
        buf[pos++] = (byte)(v >>> 16);
        buf[pos++] = (byte)(v >>> 8);
        buf[pos++] = (byte)(v >>> 0);
    }

    public void writeLong(long v) {
        ensureEnoughBuffer(pos + 8);
        buf[pos++] = (byte)(v >>> 56);
        buf[pos++] = (byte)(v >>> 48);
        buf[pos++] = (byte)(v >>> 40);
        buf[pos++] = (byte)(v >>> 32);
        buf[pos++] = (byte)(v >>> 24);
        buf[pos++] = (byte)(v >>> 16);
        buf[pos++] = (byte)(v >>> 8);
        buf[pos++] = (byte)(v >>> 0);
    }

    public void writeFloat(float v) throws IOException {
        writeInt(Float.floatToIntBits(v));
    }

    public void writeDouble(double v) throws IOException {
        writeLong(Double.doubleToLongBits(v));
    }

    public void writeBytes(String s) {
        int length = s.length();
        for (int i = 0; i < length; i++) {
            write((byte)s.charAt(i));
        }
    }

    public void writeChars(String s) {
        int length = s.length();
        for (int i = 0; i < length; i++) {
            int c = s.charAt(i);
            write((c >>> 8) & 0xFF);
            write((c >>> 0) & 0xFF);
        }
    }

    public void writeUTF(String str) throws IOException {
        //int strlen = str.length();
        char[] toCharArray=str.toCharArray();
        int encodedsize = 0;
        int c;
        for (int i = 0; i < toCharArray.length; i++) {
            c = toCharArray[i];
            if ((c >= 0x0001) && (c <= 0x007F)) {
                encodedsize++;
            } else if (c > 0x07FF) {
                encodedsize += 3;
            } else {
                encodedsize += 2;
            }
        }
        if (encodedsize > 65535) {
            throw new UTFDataFormatException("encoded string too long: " + encodedsize + " bytes");
        }
        ensureEnoughBuffer(pos + encodedsize + 2);
        writeShort(encodedsize);
        int i = 0;
        for (i = 0; i < toCharArray.length; i++) {
            c = toCharArray[i];
            if (!((c >= 0x0001) && (c <= 0x007F))) {
                break;
            }
            buf[pos++] = (byte)c;
        }
        for (; i < toCharArray.length; i++) {
            c = toCharArray[i];
            if ((c >= 0x0001) && (c <= 0x007F)) {
                buf[pos++] = (byte)c;
            } else if (c > 0x07FF) {
                buf[pos++] = (byte)(0xE0 | ((c >> 12) & 0x0F));
                buf[pos++] = (byte)(0x80 | ((c >> 6) & 0x3F));
                buf[pos++] = (byte)(0x80 | ((c >> 0) & 0x3F));
            } else {
                buf[pos++] = (byte)(0xC0 | ((c >> 6) & 0x1F));
                buf[pos++] = (byte)(0x80 | ((c >> 0) & 0x3F));
            }
        }
    }

    private void ensureEnoughBuffer(int newcount) {
        if (newcount > buf.length) {
            byte newbuf[] = new byte[Math.max(buf.length << 1, newcount)];
            System.arraycopy(buf, 0, newbuf, 0, pos);
            buf = newbuf;
        }
    }
}
