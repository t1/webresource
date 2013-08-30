package com.github.t1.webresource;

import static java.lang.annotation.RetentionPolicy.*;

import java.lang.annotation.Retention;

/**
 * Adds a CSS file to the header. Given a resource path <code>http://example.com/myapp/rest/resource</code> the
 * {@link #value() URI of the style sheet} can be:
 * <table>
 * <tr>
 * <td><b>Name</b></td>
 * <td><b>Description</b></td>
 * <td><b>Effect</b></td>
 * <td><b>Example Value</b></td>
 * <td><b>Example Result</b></td>
 * <td><b>Usecase</b></td>
 * </tr>
 * <tr>
 * <td>absolute</td>
 * <td>Starts with a scheme</td>
 * <td>Taken as is</td>
 * <td>http://example.com/main.css</td>
 * <td>http://example.com/main.css</td>
 * <td>on a different host</td>
 * </tr>
 * <tr>
 * <td>root-path</td>
 * <td>No scheme, but starting with a slash</td>
 * <td>Relative to the host</td>
 * <td>/stylesheet.css</td>
 * <td>/stylesheet.css</td>
 * <td>in a different app or static on the same host</td>
 * </tr>
 * <tr>
 * <td>relative-path</td>
 * <td>No scheme and not starting with a slash</td>
 * <td>Relative to the app, i.e.</td>
 * <td>stylesheets/main.css</td>
 * <td>/myapp/stylesheets/main.css</td>
 * <td>within the app</td>
 * </tr>
 * </table>
 */
@Retention(RUNTIME)
public @interface HtmlStyleSheet {
    /** The name of the style sheet resource */
    public String value();

    /**
     * By default the resource is included as a <code>link</code>. By setting this property to <code>true</code>, the
     * resource is copied and inlined in a <code>style</code> element.
     */
    public boolean inline() default false;
}
