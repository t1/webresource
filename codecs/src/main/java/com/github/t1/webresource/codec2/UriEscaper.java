package com.github.t1.webresource.codec2;

import java.net.*;

public class UriEscaper {

    // square brackets '[' and ']' are not allowed in URIs
    // 'new URI(String,String,String)' escapes everything but these, so we'll have to do it by hand :(
    public URI escape(String path) {
        if (!containsSquareBrackets(path))
            return escapeNonSquareBrackets(path);
        StringBuilder out = new StringBuilder();

        int start = 0;
        for (int index = index(path, start); index >= 0; index = index(path, index + 1)) {
            out.append(escapeNonSquareBrackets(path.substring(start, index)));
            out.append(escape(path.charAt(index)));
            start = index + 1;
        }
        out.append(path.substring(start));

        return URI.create(out.toString());
    }

    private boolean containsSquareBrackets(String string) {
        return index(string, 0) >= 0;
    }

    private int index(String path, int start) {
        int open = path.indexOf('[', start);
        int close = path.indexOf(']', start);
        if (open < 0)
            return close;
        if (close < 0)
            return open;
        return Math.min(open, close);
    }

    private String escape(char c) {
        switch (c) {
            case '[':
                return "%5B";
            case ']':
                return "%5D";
            default:
                throw new RuntimeException("can't escape " + c);
        }
    }

    private URI escapeNonSquareBrackets(String path) {
        try {
            return new URI(null, path, null);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
}
