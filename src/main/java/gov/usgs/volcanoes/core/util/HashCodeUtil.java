/*
 * Copyright (c) 2002-2009, Hirondelle Systems
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 * Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 * Neither the name of Hirondelle Systems nor the
 * names of its contributors may be used to endorse or promote products
 * derived from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY HIRONDELLE SYSTEMS ''AS IS'' AND ANY
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL HIRONDELLE SYSTEMS BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package gov.usgs.volcanoes.core.util;

import java.lang.reflect.Array;

/**
 * Collected methods which allow easy implementation of <tt>hashCode</tt>.
 *
 * <p>Example use case:
 * 
 * <pre>
 * public int hashCode() {
 *   int result = HashCodeUtil.SEED;
 *   // collect the contributions of various fields
 *   result = HashCodeUtil.hash(result, fPrimitive);
 *   result = HashCodeUtil.hash(result, fObject);
 *   result = HashCodeUtil.hash(result, fArray);
 *   return result;
 * }
 * </pre>
 * 
 * <p>Taken from http://www.javapractices.com/topic/TopicAction.do?Id=28
 */
public final class HashCodeUtil {

  /**
   * An initial value for a <tt>hashCode</tt>, to which is added contributions
   * from fields. Using a non-zero value decreases collisons of <tt>hashCode</tt>
   * values.
   */
  public static final int SEED = 23;

  /** booleans. */
  public static int hash(int inSeed, boolean inBoolean) {
    log("boolean...");
    return firstTerm(inSeed) + (inBoolean ? 1 : 0);
  }

  /*** chars. */
  public static int hash(int inSeed, char inChar) {
    log("char...");
    return firstTerm(inSeed) + (int) inChar;
  }

  /** ints. */
  public static int hash(int inSeed, int inInt) {
    /*
     * Implementation Note
     * Note that byte and short are handled by this method, through
     * implicit conversion.
     */
    log("int...");
    return firstTerm(inSeed) + inInt;
  }

  /** longs. */
  public static int hash(int inSeed, long inLong) {
    log("long...");
    return firstTerm(inSeed) + (int) (inLong ^ (inLong >>> 32));
  }

  /** floats. */
  public static int hash(int inSeed, float inFloat) {
    return hash(inSeed, Float.floatToIntBits(inFloat));
  }

  /** doubles. */
  public static int hash(int inSeed, double inDouble) {
    return hash(inSeed, Double.doubleToLongBits(inDouble));
  }

  /**
   * <tt>aObject</tt> is a possibly-null object field, and possibly an array.
   *
   * <p>If <tt>aObject</tt> is an array, then each element may be a primitive
   * or a possibly-null object.
   */
  public static int hash(int inSeed, Object inObject) {
    int result = inSeed;
    if (inObject == null) {
      result = hash(result, 0);
    } else if (!isArray(inObject)) {
      result = hash(result, inObject.hashCode());
    } else {
      int length = Array.getLength(inObject);
      for (int idx = 0; idx < length; ++idx) {
        Object item = Array.get(inObject, idx);
        // if an item in the array references the array itself, prevent infinite looping
        if (!(item == inObject)) {
          // recursive call!
          result = hash(result, item);
        }
      }
    }
    return result;
  }

  // PRIVATE
  private static final int fODD_PRIME_NUMBER = 37;

  private static int firstTerm(int inSeed) {
    return fODD_PRIME_NUMBER * inSeed;
  }

  private static boolean isArray(Object inObject) {
    return inObject.getClass().isArray();
  }

  private static void log(String inMessage) {
    System.out.println(inMessage);
  }
}
