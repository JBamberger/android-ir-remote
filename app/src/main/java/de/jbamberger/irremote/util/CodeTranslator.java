package de.jbamberger.irremote.util;

/**
 * @author Jannik Bamberger (dev.jbamberger@gmail.com)
 */

abstract class CodeTranslator {

    private final int frequency;
    private final int[] initSequence;
    private final int[] endSequence;
    private final int[] zero;
    private final int[] one;

    /**
     * one and zero have to have the same length.
     *
     * @param initSequence Sequence transmitted only once in the beginning
     * @param endSequence  Sequence transmitted only once in the end
     * @param zero         Sequence representing a binary zeri
     * @param one          Sequence representing a binary one
     */
    CodeTranslator(int[] initSequence, int[] endSequence, int[] zero, int[] one, int frequency) {
        this.initSequence = initSequence;
        this.endSequence = endSequence;
        this.zero = zero;
        this.one = one;
        this.frequency = frequency;
    }

    public int getFrequency() {
        return frequency;
    }

    /**
     * returns the on off sequence that is represented by the codeString
     *
     * @param codeString code
     * @return on off sequence of the codeString
     */
    public abstract int[] buildCode(String codeString);

    /**
     * This method injects the inverse of every byte into the array. {a, b} becomes {a, ~a. b, ~b}.
     *
     * @param data input data
     * @return data with injected inverses
     */
    byte[] injectInverse(byte[] data) {
        byte[] res = new byte[data.length * 2];
        for (int i = 0; i < data.length; i++) {
            res[2 * i] = data[i];
            res[2 * i + 1] = (byte) ~data[i];
        }
        return res;
    }

    /**
     * Creates an int array, writes the start sequence, then the bytes represented by one and zero
     * and finally the end sequence. TODO: endianness
     *
     * @param data the bytes to be sandwiched between start and end
     * @return the encoded sequence
     */
    int[] buildRawCode(byte[] data) {
        int size = initSequence.length + data.length * (8 * zero.length) + endSequence.length;
        int[] code = new int[size];
        System.arraycopy(initSequence, 0, code, 0, initSequence.length);
        int c = initSequence.length;
        for (byte b : data) {
            for (int j = 0; j < 8; j++) {
                if ((b & 128 >> j) == 0) {
                    System.arraycopy(zero, 0, code, c, zero.length);
                } else {
                    System.arraycopy(one, 0, code, c, one.length);
                }
                c += 2;
            }
        }
        System.arraycopy(endSequence, 0, code, c, endSequence.length);
        return code;
    }

    /**
     * Creates a byte representation of a hexadecimal string.
     *
     * @param s hex string
     * @return bytes encoded in the string
     */
    static byte[] hexToBytes(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i + 1), 16));
        }
        return data;
    }

    private final static char[] hexArray = "0123456789ABCDEF".toCharArray();

    /**
     * encodes a byte sequence into a hex string.
     *
     * @param bytes input
     * @return bytes as hex string
     */
    static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }
}
