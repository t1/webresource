package com.github.t1.webresource.html;

import java.io.PrintWriter;

public class Html extends Tag {
    public Html(PrintWriter writer) {
        super(writer, "html", true);
    }

    public Head head() {
        return new Head(this);
    }

    public Body body() {
        return new Body(this);
    }
}
