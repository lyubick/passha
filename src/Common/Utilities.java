/**
 *
 */
package Common;

/**
 * @author lyubick
 *
 */
public final class Utilities
{
    /**@brief converts byte[] to long (8 bytes). Big Endian;
    *
    * @param [in] input - bytes array input
    * @param [in] indexFrom - index to first byte
    * @return long value
    *
    * @note does not validate input array length
    */
   public static  long load64(byte[] input, int indexFrom)
   {
       long value = 0;

       for (int i = 0; i < 8; i++)
       {
           value = (value << 8) + (input[indexFrom + i] & 0xff);
       }

       return value;
   }

   /**@brief converts long value to byte[] (8 bytes). Big Endian
    *
    * @param [in] value - long value to be converted
    * @param [out] output - bytes array to be converted to
    * @param [in] indexFrom - start index in bytes array
    *
    * @note does not validate output array length
    */
   public static void store64(long value, byte[] output, int indexFrom)
   {
       for (int i = 7; i >= 0; i--)
       {
           output[indexFrom + i] = (byte) (value & 0xff);
           value = value >> 8;
       }
   }
}
