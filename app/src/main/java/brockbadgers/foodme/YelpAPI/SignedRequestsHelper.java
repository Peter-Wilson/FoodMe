package brockbadgers.foodme.YelpAPI;

import android.util.Base64;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.URLEncoder;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
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
    private String endpoint = "api.yelp.com/v2/search"; // must be lowercase


    private String yelpConsumerKey = "Ep4zPJHfTkDnQu0Gb10pYg";
    private String yelpToken = "6HFtuuurLGjVRWipDttj4h3O7NdVRzDE";
    private String yelpTokenSecretKey = "xTeygTY1syOp8PLVKyP0zD9V3sg";

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
        SecureRandom random = new SecureRandom();
        params.put("oauth_consumer_key", yelpConsumerKey);
        params.put("oauth_signature_method", "hmac-sha1");
        params.put("oauth_token", yelpToken);
        params.put("oauth_timestamp", timestamp());
        params.put("oauth_nonce", new BigInteger(130, random).toString(32));

        SortedMap<String, String> sortedParamMap =
                new TreeMap<String, String>(params);
        String canonicalQS = canonicalize(sortedParamMap);
        String toSign =
                REQUEST_METHOD + "\n"
                        + endpoint + "\n"
                        + REQUEST_URI + "\n"
                        + canonicalQS;

        String hmac = hmac(toSign);
        String sig = percentEncodeRfc3986(hmac);
        String url = "http://" + endpoint + REQUEST_URI + "?" +
                canonicalQS + "&Signature=" + sig;

        return url;
    }

    private String hmac(String stringToSign) {
        String signature = null;
        byte[] rawHmac;
        try {
            Mac sha256_HMAC = Mac.getInstance(HMAC_SHA1_ALGORITHM);
            SecretKeySpec secret_key = new SecretKeySpec(yelpTokenSecretKey.getBytes(UTF8_CHARSET), HMAC_SHA1_ALGORITHM);
            sha256_HMAC.init(secret_key);

            rawHmac = sha256_HMAC.doFinal(stringToSign.getBytes(UTF8_CHARSET));
            signature = Base64.encodeToString(rawHmac, Base64.NO_WRAP);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(UTF8_CHARSET + " is unsupported!", e);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }
        return signature;
    }

    private String timestamp() {
        String timestamp = null;
        Calendar cal = Calendar.getInstance();
        //DateFormat dfm = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        //dfm.setTimeZone(TimeZone.getTimeZone("ET"));
        timestamp = ""+cal.getTime().getTime();
        return timestamp;
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
