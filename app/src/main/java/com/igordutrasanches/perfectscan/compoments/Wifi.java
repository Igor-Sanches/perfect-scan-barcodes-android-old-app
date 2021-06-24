package com.igordutrasanches.perfectscan.compoments;

import android.content.Context;

import com.igordutrasanches.perfectscan.R;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

class Wifi  {

    private final String ssid;
    private String tipe;
    private final String password;
    private final boolean hidden;
    private final String identity;
    private final String anonymousIdentity;
    private final String eapMethod;
    private final String phase2Method;

    public static Wifi from(String r, Context c){
        return new Wifi(r, c);
    }

    public Wifi(String result, Context c) {
        context = c;
        String rawText = getMassagedText(result);
        if (!rawText.startsWith("WIFI:")) {
            //return null;
        }
        rawText = rawText.substring("WIFI:".length());
        ssid = matchSinglePrefixedField("S:", rawText, ';', false);
        if (ssid == null || ssid.isEmpty()) {
            //return null;
        }
        password = matchSinglePrefixedField("P:", rawText, ';', false);
        tipe = matchSinglePrefixedField("T:", rawText, ';', false);
        if (tipe == null) {
            tipe = "nopass";
        }
        hidden = Boolean.parseBoolean(matchSinglePrefixedField("H:", rawText, ';', false));
        identity = matchSinglePrefixedField("I:", rawText, ';', false);
        anonymousIdentity = matchSinglePrefixedField("A:", rawText, ';', false);
        eapMethod = matchSinglePrefixedField("E:", rawText, ';', false);
        phase2Method = matchSinglePrefixedField("H:", rawText, ';', false);
        //return new WifiParsedResult(type, ssid, pass, hidden, identity, anonymousIdentity, eapMethod, phase2Method);
    }

    public String getSsid() {
        return ssid;
    }

    public String getType() {
        return tipe;
    }

    public String getPassword() {
        return password;
    }

    public boolean isHidden() {
        return hidden;
    }

    public String getIdentity() {
        return identity;
    }

    public String getAnonymousIdentity() {
        return anonymousIdentity;
    }

    public String getEapMethod() {
        return eapMethod;
    }

    public String getPhase2Method() {
        return phase2Method;
    }

    private Context context;
    public String getDisplayResult() {
        StringBuilder result = new StringBuilder(80);
        maybeAppend(context.getString(R.string.wifi_type) + " " + tipe, result);
        maybeAppend(context.getString(R.string.wifi_ssd) + " " + ssid, result);
        if(password != null)
            maybeAppend(context.getString(R.string.wifi_senha) + " " + password, result);

        return result.toString().trim();
    }

    private static final Pattern DIGITS = Pattern.compile("\\d+");
    private static final Pattern AMPERSAND = Pattern.compile("&");
    private static final Pattern EQUALS = Pattern.compile("=");
    private static final String BYTE_ORDER_MARK = "\ufeff";

    static final String[] EMPTY_STR_ARRAY = new String[0];

    protected static String getMassagedText(String result) {
        String text = result;
        if (text.startsWith(BYTE_ORDER_MARK)) {
            text = text.substring(1);
        }
        return text;
    }


    protected static void maybeAppend(String value, StringBuilder result) {
        if (value != null) {
            result.append('\n');
            result.append(value);
        }
    }

    protected static String[] maybeWrap(String value) {
        return value == null ? null : new String[] { value };
    }

    protected static String unescapeBackslash(String escaped) {
        int backslash = escaped.indexOf('\\');
        if (backslash < 0) {
            return escaped;
        }
        int max = escaped.length();
        StringBuilder unescaped = new StringBuilder(max - 1);
        unescaped.append(escaped.toCharArray(), 0, backslash);
        boolean nextIsEscaped = false;
        for (int i = backslash; i < max; i++) {
            char c = escaped.charAt(i);
            if (nextIsEscaped || c != '\\') {
                unescaped.append(c);
                nextIsEscaped = false;
            } else {
                nextIsEscaped = true;
            }
        }
        return unescaped.toString();
    }

    protected static int parseHexDigit(char c) {
        if (c >= '0' && c <= '9') {
            return c - '0';
        }
        if (c >= 'a' && c <= 'f') {
            return 10 + (c - 'a');
        }
        if (c >= 'A' && c <= 'F') {
            return 10 + (c - 'A');
        }
        return -1;
    }

    static String[] matchPrefixedField(String prefix, String rawText, char endChar, boolean trim) {
        List<String> matches = null;
        int i = 0;
        int max = rawText.length();
        while (i < max) {
            i = rawText.indexOf(prefix, i);
            if (i < 0) {
                break;
            }
            i += prefix.length(); // Skip past this prefix we found to start
            int start = i; // Found the start of a match here
            boolean more = true;
            while (more) {
                i = rawText.indexOf(endChar, i);
                if (i < 0) {
                    // No terminating end character? uh, done. Set i such that loop terminates and break
                    i = rawText.length();
                    more = false;
                } else if (countPrecedingBackslashes(rawText, i) % 2 != 0) {
                    // semicolon was escaped (odd count of preceding backslashes) so continue
                    i++;
                } else {
                    // found a match
                    if (matches == null) {
                        matches = new ArrayList<>(3); // lazy init
                    }
                    String element = unescapeBackslash(rawText.substring(start, i));
                    if (trim) {
                        element = element.trim();
                    }
                    if (!element.isEmpty()) {
                        matches.add(element);
                    }
                    i++;
                    more = false;
                }
            }
        }
        if (matches == null || matches.isEmpty()) {
            return null;
        }
        return matches.toArray(EMPTY_STR_ARRAY);
    }

    private static int countPrecedingBackslashes(CharSequence s, int pos) {
        int count = 0;
        for (int i = pos - 1; i >= 0; i--) {
            if (s.charAt(i) == '\\') {
                count++;
            } else {
                break;
            }
        }
        return count;
    }

    static String matchSinglePrefixedField(String prefix, String rawText, char endChar, boolean trim) {
        String[] matches = matchPrefixedField(prefix, rawText, endChar, trim);
        return matches == null ? null : matches[0];
    }

}