import java.io.*;
import java.math.BigInteger;
import java.util.Scanner;
import java.util.stream.IntStream;

public class MyFeal4Output {
    private int L0;
    private int R0;
    private int L4;
    private int R4;
    private final int NUMBER_OF_PAIRS = 200;
    private String[] plaintext;
    private String[] ciphertext;

    public MyFeal4Output() {
        plaintext = new String[NUMBER_OF_PAIRS];
        ciphertext = new String[NUMBER_OF_PAIRS];
    }

    public int getL0() {
        return L0;
    }

    public void setL0(int l0) {
        L0 = l0;
    }

    public int getR0() {
        return R0;
    }

    public void setR0(int r0) {
        R0 = r0;
    }

    public int getL4() {
        return L4;
    }

    public void setL4(int l4) {
        L4 = l4;
    }

    public int getR4() {
        return R4;
    }

    public void setR4(int r4) {
        R4 = r4;
    }

    public int getNUMBER_OF_PAIRS() {
        return NUMBER_OF_PAIRS;
    }

    public String[] getPlaintext() {
        return plaintext;
    }

    public void setPlaintext(String[] plaintext) {
        this.plaintext = plaintext;
    }

    public String[] getCiphertext() {
        return ciphertext;
    }

    public void setCiphertext(String[] ciphertext) {
        this.ciphertext = ciphertext;
    }

    public int getBit(int num, int n) {
        return (num >> (31 - n)) & 1;
    }

    public int pairSplitA0A1(int wordIndex, byte a0, byte a1) {
        splittingBytes(wordIndex);
        return calculate(a0, a1);

    }

    private void splittingBytes(int wordIndex) {
        L0 = (int) Long.parseLong(plaintext[wordIndex].substring(0, 8), 16);
        R0 = (int) Long.parseLong(plaintext[wordIndex].substring(8), 16);
        L4 = (int) Long.parseLong(ciphertext[wordIndex].substring(0, 8), 16);
        R4 = (int) Long.parseLong(ciphertext[wordIndex].substring(8), 16);
    }

    public int pairSplitD0D1A0A1(int wordIndex, byte d0, byte d1, byte a0, byte a1) {
        splittingBytes(wordIndex);
        return calculate(d0, d1, a0, a1);
    }

    public void readKnownTextPairs() {
        try {
            FileReader reader = new FileReader("Known.txt");
            Scanner scan = new Scanner(reader);
            int count = 0;
            boolean isPlainText = true;
            while (scan.hasNext() && count < plaintext.length){
                String line = scan.nextLine();
                if (line.length() == 0)
                    continue;
                if (isPlainText)
                    plaintext[count] = line.substring(12);
                else {
                    ciphertext[count] = line.substring(12);
                    count++;
                }
                isPlainText = !isPlainText;
            }
        } catch (FileNotFoundException e) {
            System.out.println("Could not find input file.");
        }
    }

    public byte[] xor(byte[] a, byte[] b) {
        if (a.length == b.length) {
            int i = 0;
            byte[] output = new byte[a.length];
            for (int j = 0; j < a.length; j++) {
                output[i] = (byte) (a[j] ^ b[i++]);
            }
            return output;
        }

        return null;
    }

    public byte rot2(byte x) {
        return (byte) (((x & 255) << 2) | ((x & 255) >>> 6));
    }

    public byte g0(byte a, byte b) {
        return rot2((byte) ((a + b) & 255));
    }

    public byte g1(byte a, byte b) {
        return rot2((byte) ((a + b + 1) & 255));
    }

    public int pack(byte[] b, int startIndex) {
        return ((b[startIndex + 3] & 255) | ((b[startIndex + 2] & 255) << 8) |
                ((b[startIndex + 1] & 255) << 16) | ((b[startIndex] & 255) << 24));
    }

    public void unpack(int a, byte[] b, int startIndex) {
        b[startIndex] = (byte) (a >>> 24);
        b[startIndex + 1] = (byte) (a >>> 16);
        b[startIndex + 2] = (byte) (a >>> 8);
        b[startIndex + 3] = (byte) a;
    }

    public int function(int input) {
        byte[] x = new byte[4];
        byte[] y = new byte[4];
        extracted(input, x, y);
        return pack(y, 0);
    }

    private void extracted(int input, byte[] x, byte[] y) {
        unpack(input, x, 0);
        y[1] = g1((byte) ((x[0] ^ x[1]) & 255), (byte) ((x[2] ^ x[3]) & 255));
        y[0] = g0((byte) (x[0] & 255), (byte) (y[1] & 255));
        y[2] = g0((byte) (y[1] & 255), (byte) ((x[2] ^ x[3]) & 255));
        y[3] = g1((byte) (y[2] & 255), (byte) (x[3] & 255));
    }


    public int calculate(byte X0, byte X1) {
        byte[] k0 = {0, X0, X1, 0};
        int a1 = getBit(L0 ^ R0 ^ L4, 5) ^ getBit(L0 ^ R0 ^ L4, 13) ^ getBit(L0 ^ R0 ^ L4, 21);
        int a2 = getBit(L0 ^ L4 ^ R4, 15);
        int x = L0 ^ R0;
        BigInteger bigInt = BigInteger.valueOf(x);
        int a4 = getA5(k0, bigInt);
        int a5 = getBit(function(a4), 15);

        return a1 ^ a2 ^ a5;

    }

    public int calculate(byte d0, byte d1, byte a0, byte a1) {
        byte[] k0 = {d0, (byte) (a0 ^ d0), (byte) (a1 ^ d1), d1};
        int a2 = getBit(L0 ^ R0 ^ L4, 13);
        int a3 = getBit(L0 ^ L4 ^ R4, 7) ^ getBit(L0 ^ L4 ^ R4, 15) ^ getBit(L0 ^ L4 ^ R4, 23)
        int x = L0 ^ R0;
        BigInteger bigInt = BigInteger.valueOf(x);
        int a5 = getA5(k0, bigInt);

        int a6 = getBit(function(a5), 7);
        return a2 ^ a3 ^ a6;

    }

    private int getA5(byte[] k0, BigInteger bigInt) {
        byte[] a4 = bigInt.toByteArray();
        byte[] y0 = xor(a4, k0);
        return pack(y0, 0);
    }

    public void printBytes(byte a0, byte a1) {
        IntStream.range(-128, 127).forEach(d0 ->{
            IntStream.range(-128, 127).forEach(d1 -> {
                extracted(a0, a1, d0, d1);
            });
        });
    }

    private void extracted(byte a0, byte a1, int d0, int d1) {
        int counter_1 = 0;
        int counter_2 = 0;
        for (int innerLoop = 0; innerLoop < NUMBER_OF_PAIRS; innerLoop++) {
            int a = pairSplitD0D1A0A1(innerLoop, (byte) d0, (byte) d1, a0, a1);
            if (a == 0)
                counter_1++;
            else if (a == 1)
                counter_2++;
        }
        if (counter_1 == NUMBER_OF_PAIRS || counter_2 == NUMBER_OF_PAIRS) {
            byte[] k0 = {
                    (byte) d0, (byte) (a0 ^ d0), (byte) (a1 ^ d1), (byte) d1};
            int key0 = pack(k0, 0);
            System.out.println("\tK0: 0x" + Integer.toHexString(key0));
        }
    }
}