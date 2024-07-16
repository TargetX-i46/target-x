/* Copyright (c) 2016 Jon Chambers
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE. */

package com.i46.otp;

import com.eatthepath.otp.TimeBasedOneTimePasswordGenerator;
import org.apache.commons.codec.binary.Hex;

import javax.crypto.KeyGenerator;
import javax.crypto.Mac;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.time.Instant;
import java.security.*;
import java.util.Base64;
public class Main {
    public static void main(final String[] args) throws NoSuchAlgorithmException, InvalidKeyException {
        if (args.length < 1){
            System.out.println("Valid args: getKey, validate <otp>, createKey <password>");
            return;
        }
        String param = args[0];
        String param2;

        if (!param.equalsIgnoreCase("getKey") &&
                !param.equalsIgnoreCase("validate") &&
                !param.equalsIgnoreCase("createKey")){
            System.out.println("Valid args: getKey, validate, createKey <password>");
            return;
        }

        final TimeBasedOneTimePasswordGenerator totp = new TimeBasedOneTimePasswordGenerator(Duration.ofSeconds(30L), 6, "HmacSHA512");

        final Key key;
        {
            final KeyGenerator keyGenerator = KeyGenerator.getInstance(totp.getAlgorithm());

            // Key length should match the length of the HMAC output (160 bits for SHA-1, 256 bits
            // for SHA-256, and 512 bits for SHA-512). Note that while Mac#getMacLength() returns a
            // length in _bytes,_ KeyGenerator#init(int) takes a key length in _bits._
            final int macLengthInBytes = Mac.getInstance(totp.getAlgorithm()).getMacLength();
            keyGenerator.init(macLengthInBytes * 8);

            key = keyGenerator.generateKey();
        }


                KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
                kpg.initialize(2048); KeyPair kp = kpg.generateKeyPair();

                System.out.println ("-----BEGIN PRIVATE KEY-----");
                System.out.println (Base64.getMimeEncoder().encodeToString( kp.getPrivate().getEncoded()));
                System.out.println ("-----END PRIVATE KEY-----");
                System.out.println ("-----BEGIN PUBLIC KEY-----");
                System.out.println (Base64.getMimeEncoder().encodeToString( kp.getPublic().getEncoded()));
                System.out.println ("-----END PUBLIC KEY-----");


        final Instant now = Instant.now();
        final Instant later = now.plus(totp.getTimeStep());
        String otp = totp.generateOneTimePasswordString(key, now);

        if (param.equalsIgnoreCase("getKey")){
            System.out.println("Key: " + key.getAlgorithm() + " " + Hex.encodeHexString(key.getEncoded()));

            System.out.println("Current password:  " + otp);
            System.out.println("Future password:  " + totp.generateOneTimePasswordString(key, later));
            return;
        }
        if (param.equalsIgnoreCase("validate")){
            param2 = args[1];
            if (param2.isEmpty()){
                System.out.println("validate <otp> required");
                return;
            }
            System.out.println("Valid: " + param2.equals(otp));
        }
        if (param.equalsIgnoreCase("createKey")){
            param2 = args[1];
            if (param2.isEmpty()){
                System.out.println("createKey <password> required");
                return;
            }
        }

    }
}
