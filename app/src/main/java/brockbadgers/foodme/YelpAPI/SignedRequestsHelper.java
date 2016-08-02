package brockbadgers.foodme.YelpAPI;

import android.util.Base64;
import android.util.Log;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.URLEncoder;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Formatter;
import java.util.Iterator;
import java.util.Map;
import java.util.SortedMap;
import java.util.TimeZone;
import java.util.TreeMap;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import brockbadgers.foodme.R;

/*
    Helper to encrypt the query string into a signature to ensure no changes occurred during transmission
*/
public class SignedRequestsHelper {
    private static final String UTF8_CHARSET = "UTF-8";
    private static final String HMAC_SHA1_ALGORITHM = "HmacSHA1";
    private static final String REQUEST_URI = "/onca/xml";
    private static final String REQUEST_METHOD = "GET";

    // query the yelp site
    private String endpoint = "http://api.yelp.com/v2/search"; // must be lowercase


    private String yelpConsumerKey = "Ep4zPJHfTkDnQu0Gb10pYg";
    private String yelpToken = "ffaWzSVhirPmh9GwVN7a8yHrjGlxURQ0";
    private String yelpTokenSecretKey = "03zRA9z9aogl7dT0CILrcwVskaw";

    private SecretKeySpec secretKeySpec = null;
    private Mac mac = null;

    public SignedRequestsHelper() throws UnsupportedEncodingException, NoSuchAlgorithmException, InvalidKeyException {
        byte[] secretyKeyBytes = yelpTokenSecretKey.getBytes(UTF8_CHARSET);
        secretKeySpec =
                new SecretKeySpec(secretyKeyBytes, HMAC_SHA1_ALGORITHM);
        mac = Mac.getInstance(HMAC_SHA1_ALGORITHM);
        mac.init(secretKeySpec);
    }

    public String sign(Map<String, String> params) {
        params.put("oauth_consumer_key", yelpConsumerKey);
        params.put("oauth_signature_method", "HMAC-SHA1");
        params.put("oauth_token", yelpToken);

        SortedMap<String, String> sortedParamMap =
                new TreeMap<String, String>(params);
        String canonicalQS = canonicalize(sortedParamMap);
        String toSign =
                REQUEST_METHOD + "?"
                        + endpoint
                        + REQUEST_URI + "?"
                        + canonicalQS;

        String hmac = hmac(toSign);
        String sig = percentEncodeRfc3986(hmac);
        String url = endpoint + REQUEST_URI + "?" +
                canonicalQS + "&oauth_signature=" + sig;
        Log.i("encryption", "URL: "+url);
        return url;
    }

    private static String toHexString(byte[] bytes) {
        Formatter formatter = new Formatter();

        for (byte b : bytes) {
            formatter.format("%02x", b);
        }

        return formatter.toString();
    }

    private String hmac(String stringToSign) {
        String signature = null;
        byte[] rawHmac;
        try {
            String stringToSign2 = stringToSign.replace("%","%25").replace("=","%3D").replace("&","%26").replace("?","\\u0026").replace("/","%2F").replace(":","%3A");
            Mac sha1_HMAC = Mac.getInstance(HMAC_SHA1_ALGORITHM);
            SecretKeySpec secret_key = new SecretKeySpec(yelpTokenSecretKey.getBytes(), HMAC_SHA1_ALGORITHM);
            sha1_HMAC.init(secret_key);

            rawHmac = sha1_HMAC.doFinal(stringToSign2.getBytes(UTF8_CHARSET));
            signature = Base64.encodeToString(rawHmac, Base64.NO_WRAP);
            Log.i("encryption","string to sign: " + stringToSign2);
            Log.i("encryption","signature: " + signature);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return signature;
    }

    private String canonicalize(SortedMap<String, String> sortedParamMap)
    {
        if (sortedParamMap.isEmpty()) {
            return "";
        }

        StringBuffer buffer = new StringBuffer();
        Iterator<Map.Entry<String, String>> iter =
                sortedParamMap.entrySet().iterator();

        while (iter.hasNext()) {
            Map.Entry<String, String> kvpair = iter.next();
            buffer.append(percentEncodeRfc3986(kvpair.getKey()));
            buffer.append("=");
            buffer.append(percentEncodeRfc3986(kvpair.getValue()));
            if (iter.hasNext()) {
                buffer.append("&");
            }
        }
        String cannoical = buffer.toString();
        return cannoical;
    }

    private String percentEncodeRfc3986(String s) {
        String out;
        try {
            out = URLEncoder.encode(s, UTF8_CHARSET)
                    .replace("+", "%20")
                    .replace("*", "%2A")
                    .replace(":", "%3A")
                    .replace("/", "%2F")
                    .replace("~", "%7E");
        } catch (UnsupportedEncodingException e) {
            out = s;
        }
        return out;
    }
}
