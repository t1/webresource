package com.github.t1.webresource.log;

import java.util.List;

public interface LogContextScanner {
    List<String> getKeys();

    String valueFor(String kEY);
}
