package com.tapstream.ga4j;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.security.SecureRandom;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {
    
    private static final Pattern RE_LOCALE 
        = Pattern.compile("(^|\\s*,\\s*)([a-zA-Z]{1,8}(-[a-zA-Z]{1,8})*)\\s*(;\\s*q\\s*=\\s*(1(\\.0{0,3})?|0(\\.[0-9]{0,3})))?", Pattern.CASE_INSENSITIVE);
        
    private static final Pattern RE_ACCOUNT_VALID = Pattern.compile("^(UA|MO)-[0-9]*-[0-9]*$");
    private static SecureRandom RANDOM = new SecureRandom();
    
    public static String encodeUriComponent(String value) {
        try {
            return convertToGAStyleEncoding(URLEncoder.encode(value, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }
    
    public static String convertToGAStyleEncoding(String urlEncodedString) {
        return urlEncodedString
                .replace("+", "%20")
                .replace("%21", "!")
                .replace("%2A", "*")
                .replace("%27", "'")
                .replace("%28", "(")
                .replace("%29", ")");
    }
    
    public static String join(Iterable<? extends Object> iterable, String delimiter) {
        return join(iterable, delimiter, "", "");
    }

    public static String join(Iterable<? extends Object> iterable, String delimiter,
            String prepend, String append) {

        StringBuilder buffer = new StringBuilder();
        Iterator<?> iterator = iterable.iterator();
        boolean hasNext = iterator.hasNext();

        while (hasNext) {

            buffer.append(prepend);
            buffer.append(String.valueOf(iterator.next()));
            buffer.append(append);

            if (iterator.hasNext()) {
                buffer.append(delimiter);
            } else {
                hasNext = false;
            }
        }

        return buffer.toString();
    }
    
    public static String anonymizeIp(String ip) {
        StringTokenizer tokenizer = new StringTokenizer(ip, ".");
        StringBuilder builder = new StringBuilder();

        int x = 0;
        while(tokenizer.hasMoreElements() && x < 3){
            builder.append(tokenizer.nextToken());
            builder.append(".");
            x++;
        }

        builder.append("0");
        return builder.toString();
    }
    
    public static String parseAcceptLanguage(String locale){
        if (locale == null)
            return null;
        
        Matcher matcher = RE_LOCALE.matcher(locale);
        
        Float winningQuality = null;
        String winningLang = null;
        
        while (matcher.find()) {
            String lang = matcher.group(2).replace("_", "-");
            if (lang == null)
                continue;
            
            float quality;
            String qualityString = matcher.group(4);
            if (qualityString == null)
                quality = 0.0f;
            else {
                try {quality = Float.parseFloat(qualityString);} 
                catch (NumberFormatException e) {quality = 0.0f;}
            }
            
            if (winningQuality == null || quality > winningQuality) {
                winningQuality = quality;
                winningLang = lang;
            }
            
        }
        
        return winningLang;
    }
    
    public static int generateHash(String stringToHash) {
        if (stringToHash == null || stringToHash.isEmpty())
            return 1;
        
        int hashVal = 0;
        
        byte[] bytes = stringToHash.getBytes(Charset.forName("UTF-8"));
        for (int x = bytes.length - 1; x >= 0; x--) {
            byte ord = bytes[x];
            hashVal = ((hashVal << 6) & 0xFFFFFFF) + ord + (ord << 14);
            int leftMostSeven = hashVal & 0xFE00000;
            if (leftMostSeven != 0)
                hashVal ^= (leftMostSeven >> 21);
        }
        
        return hashVal;
    }
    
    public static boolean isValidGoogleAccount(String accountId){
        return RE_ACCOUNT_VALID.matcher(accountId).matches();
    }
    
    public static int getRandomInt(){
        return RANDOM.nextInt(0x7FFFFFFF);
    }
    
    public static int getUnixTimestamp() {
        return (int)(System.currentTimeMillis() / 1000);
    }

    /**
     * Parse GA timestamp strings that might be in milliseconds.
     */
    public static int parseGaTimestamp(String timestampString) {
        long timestamp = (long)Double.parseDouble(timestampString);
        if (timestamp > Integer.MAX_VALUE)
            timestamp = timestamp / 1000;
        return (int)timestamp;
    }

    /**
     * Dump GA timestamp strings that might be in milliseconds.
     */
    public static String dumpGaTimestamp(int timestampString) {
        long timestamp = (long) timestampString;
        if (timestamp > Integer.MAX_VALUE)
            timestamp = timestamp / 1000;
        return Long.toString(timestamp);
    }
    /**
     * Returns true if the ip address is part of a private range.
     * 
     * @param ipString
     * @return
     */
    public static boolean isPrivateIp(String ipString){
        int ip = ipToInt(ipString);
        
        // Class A
        if ((ip & 0xFF000000) == 0x0A000000)
            return true;

        // Class B
        if ((ip & 0xFFF00000) == 0xAC100000)
            return true;

        // Class C
        if ((ip & 0xFFFF0000) == 0xC0A80000)
            return true;

        return false;
    }
    
    /**
     * Convert a string ip address to it's integer representation.
     * 
     * @param ipString - The ip address string to convert.
     * @return int representation of ipString.
     */
    public static int ipToInt(String ipString){
        StringTokenizer tokenizer = new StringTokenizer(ipString.trim(), ".");
        int ip = ((Integer.parseInt(tokenizer.nextToken()) & 0xFF) << 24)
                | ((Integer.parseInt(tokenizer.nextToken()) & 0xFF) << 16)
                | ((Integer.parseInt(tokenizer.nextToken()) & 0xFF) << 8)
                | ((Integer.parseInt(tokenizer.nextToken()) & 0xFF));
        return ip;
    }
    
    /**
     * Chooses the best client ip address.
     * 
     * Prefers the first non-private address listed in xForwardedFor. Falls
     * back to remoteAddr.
     * 
     * @param remoteAddr
     * @param xForwardedFor
     * @return
     */
    public static String getClientIp(String remoteAddr, String xForwardedFor){
        if (xForwardedFor != null){
            StringTokenizer forwardedForTokenizer = new StringTokenizer(xForwardedFor, ",");
            while (forwardedForTokenizer.hasMoreTokens()){
                String ip = forwardedForTokenizer.nextToken().trim();
                try{
                    if (!isPrivateIp(ip))
                        return ip;
                } catch (NumberFormatException e){
                    continue;
                }
                
            }
        }
        
        return remoteAddr;
    }
}
