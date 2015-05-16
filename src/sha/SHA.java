package sha;

// functions and file missing headers
// check for explicit variable initialization
// create public constructor and run self-test (throw on error)

import utilities.Utilities;

public class SHA
{
    private final int       SHA_BYTES_LENGTH               = 128;
    public static final int SHA_BYTES_RESULT               = 64;
    private final int       SHA_STATES_COUNT               = 8;
    private final int       SHA_WORDS_COUNT                = 80;
    private final int       SHA_INIT_WORDS                 = 16;
    private final long      LONG_BITS_COUNT                = 64;

    private final long[]    ROUND_CONSTANTS                =
                                                           { 0x428a2f98d728ae22L, 0x7137449123ef65cdL,
            0xb5c0fbcfec4d3b2fL, 0xe9b5dba58189dbbcL, 0x3956c25bf348b538L, 0x59f111f1b605d019L, 0x923f82a4af194f9bL,
            0xab1c5ed5da6d8118L, 0xd807aa98a3030242L, 0x12835b0145706fbeL, 0x243185be4ee4b28cL, 0x550c7dc3d5ffb4e2L,
            0x72be5d74f27b896fL, 0x80deb1fe3b1696b1L, 0x9bdc06a725c71235L, 0xc19bf174cf692694L, 0xe49b69c19ef14ad2L,
            0xefbe4786384f25e3L, 0x0fc19dc68b8cd5b5L, 0x240ca1cc77ac9c65L, 0x2de92c6f592b0275L, 0x4a7484aa6ea6e483L,
            0x5cb0a9dcbd41fbd4L, 0x76f988da831153b5L, 0x983e5152ee66dfabL, 0xa831c66d2db43210L, 0xb00327c898fb213fL,
            0xbf597fc7beef0ee4L, 0xc6e00bf33da88fc2L, 0xd5a79147930aa725L, 0x06ca6351e003826fL, 0x142929670a0e6e70L,
            0x27b70a8546d22ffcL, 0x2e1b21385c26c926L, 0x4d2c6dfc5ac42aedL, 0x53380d139d95b3dfL, 0x650a73548baf63deL,
            0x766a0abb3c77b2a8L, 0x81c2c92e47edaee6L, 0x92722c851482353bL, 0xa2bfe8a14cf10364L, 0xa81a664bbc423001L,
            0xc24b8b70d0f89791L, 0xc76c51a30654be30L, 0xd192e819d6ef5218L, 0xd69906245565a910L, 0xf40e35855771202aL,
            0x106aa07032bbd1b8L, 0x19a4c116b8d2d0c8L, 0x1e376c085141ab53L, 0x2748774cdf8eeb99L, 0x34b0bcb5e19b48a8L,
            0x391c0cb3c5c95a63L, 0x4ed8aa4ae3418acbL, 0x5b9cca4f7763e373L, 0x682e6ff3d6b2b8a3L, 0x748f82ee5defb2fcL,
            0x78a5636f43172f60L, 0x84c87814a1f0ab72L, 0x8cc702081a6439ecL, 0x90befffa23631e28L, 0xa4506cebde82bde9L,
            0xbef9a3f7b2c67915L, 0xc67178f2e372532bL, 0xca273eceea26619cL, 0xd186b8c721c0c207L, 0xeada7dd6cde0eb1eL,
            0xf57d4f7fee6ed178L, 0x06f067aa72176fbaL, 0x0a637dc5a2c898a6L, 0x113f9804bef90daeL, 0x1b710b35131c471bL,
            0x28db77f523047d84L, 0x32caab7b40c72493L, 0x3c9ebe0a15c9bebcL, 0x431d67c49c100d4cL, 0x4cc5d4becb3e42b6L,
            0x597f299cfc657e2aL, 0x5fcb6fab3ad6faecL, 0x6c44198c4a475817L };

    private long[]          state                          = new long[SHA_STATES_COUNT];
    private byte[]          MessageBlock                   = null;
    private byte[]          hashBlock                      = new byte[SHA_BYTES_LENGTH];
    private int             hashBlocksCount                = 0;

    private final long      TEN_LESS_SIGNIFICANT_BITS_MASK = 0x3FFFF;

    private long ch(long x, long y, long z)
    {
        return ((x & y) ^ ((~x) & z));
    }

    private long maj(long x, long y, long z)
    {
        return ((x & y) ^ (x & z) ^ (y & z));
    }

    private long ror(long x, long n)
    {
        return (((x) >>> n) | ((x) << (LONG_BITS_COUNT - n)));
    }

    private long s(long x, long n)
    {
        return ror(x, n);
    }

    private long r(long x, long n)
    {
        return (x >>> n);
    }

    private long sigma0(long x)
    {
        return s(x, 28) ^ s(x, 34) ^ s(x, 39);
    }

    private long sigma1(long x)
    {
        return s(x, 14) ^ s(x, 18) ^ s(x, 41);
    }

    private long gamma0(long x)
    {
        return s(x, 1) ^ s(x, 8) ^ r(x, 7);
    }

    private long gamma1(long x)
    {
        return s(x, 19) ^ s(x, 61) ^ r(x, 6);
    }

    void initializeSHA512()
    {
        state[0] = 0x6a09e667f3bcc908L;
        state[1] = 0xbb67ae8584caa73bL;
        state[2] = 0x3c6ef372fe94f82bL;
        state[3] = 0xa54ff53a5f1d36f1L;
        state[4] = 0x510e527fade682d1L;
        state[5] = 0x9b05688c2b3e6c1fL;
        state[6] = 0x1f83d9abfb41bd6bL;
        state[7] = 0x5be0cd19137e2179L;

    }

    void compressSHA512()
    {
        long W[] = new long[SHA_WORDS_COUNT];
        long a, b, c, d, e, f, g, h;
        long t1, t2;

        a = state[0];
        b = state[1];
        c = state[2];
        d = state[3];
        e = state[4];
        f = state[5];
        g = state[6];
        h = state[7];

        /* WRITE W[0.. 15] FROM MSG */
        for (int i = 0; i < SHA_INIT_WORDS; i++)
        {
            W[i] = Utilities.load64(hashBlock, 8 * i);
        }

        /* CALCULATE W[16.. 63] */
        for (int i = SHA_INIT_WORDS; i < SHA_WORDS_COUNT; i++)
        {
            W[i] = gamma1(W[i - 2]) + W[i - 7] + gamma0(W[i - 15]) + W[i - 16];
        }

        /* MODIFY */
        for (int i = 0; i < SHA_WORDS_COUNT; i++)
        {
            t1 = h + sigma1(e) + ch(e, f, g) + ROUND_CONSTANTS[i] + W[i];
            t2 = sigma0(a) + maj(a, b, c);

            /* XCHANGE */
            h = g;
            g = f;
            f = e;
            e = d + t1;
            d = c;
            c = b;
            b = a;
            a = t1 + t2;
        }
        /* SAVE STATES */
        state[0] += a;
        state[1] += b;
        state[2] += c;
        state[3] += d;
        state[4] += e;
        state[5] += f;
        state[6] += g;
        state[7] += h;
    }

    private void padMessage(byte[] input)
    {
        int length = input.length;
        long bitLength = length * 8 + 1 + 128;

        hashBlocksCount = (int) ((bitLength >>> 10) + Math.min(bitLength & TEN_LESS_SIGNIFICANT_BITS_MASK, 1));

        MessageBlock = new byte[SHA_BYTES_LENGTH * hashBlocksCount];

        for (int i = 0; i < length; i++)
        {
            MessageBlock[i] = input[i];
        }

        MessageBlock[length] = (byte) 0x80;

        Utilities.store64(length * 8, MessageBlock, SHA_BYTES_LENGTH * hashBlocksCount - 8);

    }

    private byte[] getResult()
    {
        byte[] output = new byte[SHA_BYTES_RESULT];

        for (int i = 0; i < SHA_STATES_COUNT; i++)
        {
            Utilities.store64(state[i], output, i * 8);
        }

        return output;
    }

    /*
     * Public main functions
     */
    public synchronized byte[] getBytesSHA512(final byte[] input)
    {
        initializeSHA512();
        padMessage(input);
        for (int i = 0; i < hashBlocksCount; i++)
        {
            int offset = i * SHA_BYTES_LENGTH;
            for (int j = 0; j < SHA_BYTES_LENGTH; j++)
            {
                hashBlock[j] = MessageBlock[j + offset];
            }
            compressSHA512();
        }

        return getResult();
    }

    /*
     * Public interface functions
     */
    public synchronized String getStringSHA512(final byte[] input)
    {
        return Utilities.bytesToHex(getBytesSHA512(input));
    }

}
