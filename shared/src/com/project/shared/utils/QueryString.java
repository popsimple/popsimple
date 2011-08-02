// Copyright (c) 2011, Noam Lewis & Hadas Nahon

// Copied and modified from Richard Kennard's original QueryString. See notice below.

// Copyright (c) 2009, Richard Kennard
//
// All rights reserved.
//
// Redistribution and use in source and binary forms, with or without
// modification, are permitted provided that the following conditions are met:
// * Redistributions of source code must retain the above copyright
// notice, this list of conditions and the following disclaimer.
// * Redistributions in binary form must reproduce the above copyright
// notice, this list of conditions and the following disclaimer in the
// documentation and/or other materials provided with the distribution.
// * Neither the name of Richard Kennard nor the
// names of its contributors may be used to endorse or promote products
// derived from this software without specific prior written permission.
//
// THIS SOFTWARE IS PROVIDED BY RICHARD KENNARD ''AS IS'' AND ANY
// EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
// WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
// DISCLAIMED. IN NO EVENT SHALL RICHARD KENNARD BE LIABLE FOR ANY
// DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
// (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
// LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
// ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
// (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
// SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

package com.project.shared.utils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.project.shared.data.StringEncoder;

/**
 * Represents a www-form-urlencoded query string containing an (ordered) list of parameters.
 * <p>
 * An instance of this class represents a query string encoded using the
 * <code>www-form-urlencoded</code> encoding scheme, as defined by <a
 * href="http://www.w3.org/TR/REC-html40/interact/forms.html#h-17.13.4.1">HTML 4.01 Specification:
 * application/x-www-form-urlencoded</a>, and <a
 * href="http://www.w3.org/TR/1999/REC-html401-19991224/appendix/notes.html#h-B.2.2">HTML 4.01
 * Specification: Ampersands in URI attribute values</a>. This is a common encoding scheme of the
 * query component of a URI, though the <a href="http://www.ietf.org/rfc/rfc2396.txt">RFC 2396 URI
 * specification</a> itself does not define a specific format for the query component.
 * <p>
 * This class provides static methods for <a href="#create()">creating</a> QueryString
 * instances by <a href="#parse(java.lang.CharSequence)">parsing</a> URI and string forms. It can
 * then be used to create, retrieve, update and delete parameters, and to re-apply the query string
 * back to an existing URI.
 * <p>
 * <h4>Encoding and decoding</h4> QueryString automatically encodes and decodes parameter
 * names and values to and from <code>www-form-urlencoded</code> encoding by using
 * <code>java.net.URLEncoder</code> and <code>java.net.URLDecoder</code>, which follow the <a
 * href="http://www.w3.org/TR/html40/appendix/notes.html#non-ascii-chars"> HTML 4.01 Specification:
 * Non-ASCII characters in URI attribute values</a> recommendation.
 * <h4>Multivalued parameters</h4> Often, parameter names are unique across the name/value pairs of
 * a <code>www-form-urlencoded</code> query string. However, it is permitted for the same parameter
 * name to appear in multiple name/value pairs, denoting that a single parameter has multiple
 * values. This less common use case can lead to ambiguity when adding parameters - is the 'add' a
 * 'replace' (of an existing parameter, if one with the same name already exists) or an 'append'
 * (potentially creating a multivalued parameter, if one with the same name already exists)?
 * <p>
 * This requirement significantly shapes the <code>QueryString</code> API. In particular
 * there are:
 * <ul>
 * <li><code>set</code> methods for setting a parameter, potentially replacing an existing value
 * <li><code>append</code> methods for adding a parameter, potentially creating a multivalued
 * parameter
 * <li><code>get</code> methods for returning a single value, even if the parameter has multiple
 * values
 * <li><code>getValues</code> methods for returning multiple values
 * </ul>
 * <h4>Retrieving parameters</h4> QueryString can be used to parse and retrieve parameters
 * from a query string by passing either a URI or a query string:
 * <p>
 * <code>
 *      URI uri = new URI("http://java.sun.com?forum=2");<br/>
 *      QueryString queryString = QueryString.parse(uri);<br/>
 *      System.out.println(queryString.get("forum"));<br/>
 * </code>
 * <h4>Modifying parameters</h4> QueryString can be used to set, append or remove
 * parameters from a query string:
 * <p>
 * <code>
 *      URI uri = new URI("/forum/article.jsp?id=2&amp;para=4");<br/>
 *      QueryString queryString = QueryString.parse(uri);<br/>
 *      queryString.set("id", 3);<br/>
 *      queryString.remove("para");<br/>
 *      System.out.println(queryString);<br/>
 * </code>
 * <p>
 * When modifying parameters, the ordering of existing parameters is maintained. Parameters are
 * <code>set</code> and <code>removed</code> in-place, while <code>appended</code> parameters are
 * added to the end of the query string.
 * <h4>Applying the Query</h4> QueryString can be used to apply a modified query string
 * back to a URI, creating a new URI:
 * <p>
 * <code>
 *      URI uri = new URI("/forum/article.jsp?id=2");<br/>
 *      QueryString queryString = QueryString.parse(uri);<br/>
 *      queryString.set("id", 3);<br/>
 *      uri = queryString.apply(uri);<br/>
 * </code>
 * <p>
 * When reconstructing query strings, there are two valid separator parameters defined by the W3C
 * (ampersand "&amp;" and semicolon ";"), with ampersand being the most common. The
 * <code>apply</code> and <code>toString</code> methods both default to using an ampersand, with
 * overloaded forms for using a semicolon.
 * <h4>Thread Safety</h4> This implementation is not synchronized. If multiple threads access a
 * query string concurrently, and at least one of the threads modifies the query string, it must be
 * synchronized externally. This is typically accomplished by synchronizing on some object that
 * naturally encapsulates the query string.
 *
 * @author Richard Kennard
 * @version 1.1
 */

public class QueryString {

    //
    // Public statics
    //

    /**
     * Enumeration of recommended www-form-urlencoded separators.
     * <p>
     * Recommended separators are defined by <a
     * href="http://www.w3.org/TR/REC-html40/interact/forms.html#h-17.13.4.1">HTML 4.01
     * Specification: application/x-www-form-urlencoded</a> and <a
     * href="http://www.w3.org/TR/html401/appendix/notes.html#h-B.2.2">HTML 4.01 Specification:
     * Ampersands in URI attribute values</a>.
     * <p>
     * <em>All</em> separators are recognised when parsing query strings. <em>One</em> separator may
     * be passed to <code>toString</code> and <code>apply</code> when outputting query strings.
     */

    public static enum Separator {
        /**
         * An ampersand <code>&amp;</code> - the separator recommended by <a
         * href="http://www.w3.org/TR/REC-html40/interact/forms.html#h-17.13.4.1">HTML 4.01
         * Specification: application/x-www-form-urlencoded</a>.
         */

        AMPERSAND {

            /**
             * Returns a String representation of this Separator.
             * <p>
             * The String representation matches that defined by the <a
             * href="http://www.w3.org/TR/REC-html40/interact/forms.html#h-17.13.4.1">HTML 4.01
             * Specification: application/x-www-form-urlencoded</a>.
             */

            @Override
            public String toString() {

                return "&";
            }
        },

        /**
         * A semicolon <code>;</code> - the separator recommended by <a
         * href="http://www.w3.org/TR/html401/appendix/notes.html#h-B.2.2">HTML 4.01 Specification:
         * Ampersands in URI attribute values</a>.
         */

        SEMICOLON {

            /**
             * Returns a String representation of this Separator.
             * <p>
             * The String representation matches that defined by the <a
             * href="http://www.w3.org/TR/html401/appendix/notes.html#h-B.2.2">HTML 4.01
             * Specification: Ampersands in URI attribute values</a>.
             */

            @Override
            public String toString() {

                return ";";
            }
        };
    }

    /**
     * Creates an empty QueryString.
     * <p>
     * Calling <code>toString()</code> on the created instance will return an empty String.
     */

    public static QueryString create(StringEncoder encoder) {

        return new QueryString(encoder);
    }

    /**
     * Creates a QueryString from the given Map.
     * <p>
     * The order the parameters are created in corresponds to the iteration order of the Map.
     *
     * @param parameterMap
     *            <code>Map</code> containing parameter names and values.
     */

    public static QueryString create(StringEncoder encoder,  Map<String, List<String>> parameterMap ) {

        return new QueryString(encoder, parameterMap);
    }

    /**
     * Creates a QueryString by parsing the given query string.
     * <p>
     * This method assumes the given string is the <code>www-form-urlencoded</code> query component
     * of a URI. When parsing, all <a href="QueryString.Separator.html">Separators</a> are
     * recognised.
     * <p>
     * The result of calling this method with a string that is not <code>www-form-urlencoded</code>
     * (eg. passing an entire URI, not just its query string) will likely be mismatched parameter
     * names.
     *
     * @param query
     *            query string to be parsed
     */

    public static QueryString parse(StringEncoder encoder,  final CharSequence query ) {
        return new QueryString(encoder, query);
    }


    //
    // Private statics
    //

    /**
     * Separators to honour when parsing query strings.
     * <p>
     * <em>All</em> Separators are recognized when parsing parameters, regardless of what the user
     * later nominates as their <code>toString</code> output parameter.
     */

    protected static final String             PARSE_PARAMETER_SEPARATORS  = String.valueOf( Separator.AMPERSAND ) + Separator.SEMICOLON;

    //
    // Private members
    //

    /**
     * Map of query parameters.
     */

    // Note: we initialize this Map upon object creation because, realistically, it
    // is always going to be needed (eg. there is little point lazy-initializing it)
    protected final Map<String, List<String>> queryMap                    = new LinkedHashMap<String, List<String>>();

    private StringEncoder encoder;

    //
    // Public methods
    //

    /**
     * Returns the value of the named parameter as a String. Returns <code>null</code> if the
     * parameter does not exist, or exists but has a <code>null</code> value (see {@link #contains
     * contains}).
     * <p>
     * You should only use this method when you are sure the parameter has only one value. If the
     * parameter might have more than one value, use <a
     * href="#getValues(java.lang.String)">getValues</a>.
     * <p>
     * If you use this method with a multivalued parameter, the value returned is equal to the first
     * value in the List returned by <a href="#getValues(java.lang.String)">getValues</a>.
     *
     * @param name
     *            <code>String</code> specifying the name of the parameter
     * @return <code>String</code> representing the single value of the parameter, or
     *         <code>null</code> if the parameter does not exist or exists but with a null value
     *         (see {@link #contains contains}).
     */

    public String get( final String name ) {

        List<String> parameters = getValues( name );

        if ( parameters == null || parameters.isEmpty() ) {
            return null;
        }

        return parameters.get( 0 );
    }

    /**
     * Returns whether the named parameter exists.
     * <p>
     * This can be useful to distinguish between a parameter not existing, and a parameter existing
     * but with a <code>null</code> value (eg. <code>foo=1&bar</code>). This is distinct from a
     * parameter existing with a value of the empty String (eg. <code>foo=1&bar=</code>).
     */

    public boolean contains( final String name ) {

        return this.queryMap.containsKey( name );
    }

    /**
     * Returns an <code>Iterator</code> of <code>String</code> objects containing the names of the
     * parameters. If there are no parameters, the method returns an empty Iterator. For names with
     * multiple values, only one copy of the name is returned.
     *
     * @return an <code>Iterator</code> of <code>String</code> objects, each String containing the
     *         name of a parameter; or an empty Iterator if there are no parameters
     */

    public Iterator<String> getNames() {

        return this.queryMap.keySet().iterator();
    }

    /**
     * Returns a List of <code>String</code> objects containing all of the values the named
     * parameter has, or <code>null</code> if the parameter does not exist.
     * <p>
     * If the parameter has a single value, the List has a size of 1.
     *
     * @param name
     *            name of the parameter to retrieve
     * @return a List of String objects containing the parameter's values, or <code>null</code> if
     *         the paramater does not exist
     */

    public List<String> getValues( final String name ) {

        return this.queryMap.get( name );
    }

    /**
     * Returns a mutable <code>Map</code> of the query parameters.
     *
     * @return <code>Map</code> containing parameter names as keys and parameter values as map
     *         values. The keys in the parameter map are of type <code>String</code>. The values in
     *         the parameter map are Lists of type <code>String</code>, and their ordering is
     *         consistent with their ordering in the query string. Will never return
     *         <code>null</code>.
     */

    public Map<String, List<String>> getMap() {

        LinkedHashMap<String, List<String>> map = new LinkedHashMap<String, List<String>>();

        // Defensively copy the List<String>'s

        for ( Map.Entry<String, List<String>> entry : this.queryMap.entrySet() ) {
            List<String> listValues = entry.getValue();
            map.put( entry.getKey(), new ArrayList<String>( listValues ) );
        }

        return map;
    }

    /**
     * Sets a query parameter.
     * <p>
     * If one or more parameters with this name already exist, they will be replaced with a single
     * parameter with the given value. If no such parameters exist, one will be added.
     *
     * @param name
     *            name of the query parameter
     * @param value
     *            value of the query parameter. If <code>null</code>, the parameter is removed
     * @return a reference to this object
     */

    public QueryString set( final String name, final String value ) {

        appendOrSet( name, value, false );
        return this;
    }

    /**
     * Sets a query parameter.
     * <p>
     * If one or more parameters with this name already exist, they will be replaced with a single
     * parameter with the given value. If no such parameters exist, one will be added.
     * <p>
     * This version of <code>set</code> accepts a <code>Number</code> suitable for auto-boxing. For
     * example:
     * <p>
     * <code>
     *  queryString.set( "id", 3 );<br/>
     * </code>
     *
     * @param name
     *            name of the query parameter
     * @param value
     *            value of the query parameter. If <code>null</code>, the parameter is removed
     * @return a reference to this object
     */

    public QueryString set( final String name, final Number value ) {

        if ( value == null ) {
            remove( name );
            return this;
        }

        appendOrSet( name, value.toString(), false );
        return this;
    }

    /**
     * Sets query parameters from a <code>www-form-urlencoded</code> string.
     * <p>
     * The given string is assumed to be in <code>www-form-urlencoded</code> format. The result of
     * passing a string not in <code>www-form-urlencoded</code> format (eg. passing an entire URI,
     * not just its query string) will likely be mismatched parameter names.
     * <p>
     * The given string is parsed into named parameters, and each is added to the existing
     * parameters. If a parameter with the same name already exists, it is replaced with a single
     * parameter with the given value. If the same parameter name appears more than once in the
     * given string, it is stored as a multivalued parameter. When parsing, all <a
     * href="QueryString.Separator.html">Separators</a> are recognised.
     *
     * @param query
     *            <code>www-form-urlencoded</code> string. If <code>null</code>, does nothing
     * @return a reference to this object
     */

    public QueryString set( final String query ) {

        appendOrSet( query, false );
        return this;
    }

    /**
     * Appends a query parameter.
     * <p>
     * If one or more parameters with this name already exist, their value will be preserved and the
     * given value will be stored as a multivalued parameter. If no such parameters exist, one will
     * be added.
     *
     * @param name
     *            name of the query parameter
     * @param value
     *            value of the query parameter. If <code>null</code>, does nothing
     * @return a reference to this object
     */

    public QueryString append( final String name, final String value ) {

        appendOrSet( name, value, true );
        return this;
    }

    /**
     * Appends a query parameter.
     * <p>
     * If one or more parameters with this name already exist, their value will be preserved and the
     * given value will be stored as a multivalued parameter. If no such parameters exist, one will
     * be added.
     * <p>
     * This version of <code>append</code> accepts a <code>Number</code> suitable for auto-boxing.
     * For example:
     * <p>
     * <code>
     *  queryString.append( "id", 3 );<br/>
     * </code>
     *
     * @param name
     *            name of the query parameter
     * @param value
     *            value of the query parameter. If <code>null</code>, does nothing
     * @return a reference to this object
     */

    public QueryString append( final String name, final Number value ) {

        appendOrSet( name, value.toString(), true );
        return this;
    }

    /**
     * Appends query parameters from a <code>www-form-urlencoded</code> string.
     * <p>
     * The given string is assumed to be in <code>www-form-urlencoded</code> format. The result of
     * passing a string not in <code>www-form-urlencoded</code> format (eg. passing an entire URI,
     * not just its query string) will likely be mismatched parameter names.
     * <p>
     * The given string is parsed into named parameters, and appended to the existing parameters. If
     * a parameter with the same name already exists, or if the same parameter name appears more
     * than once in the given string, it is stored as a multivalued parameter. When parsing, all <a
     * href="QueryString.Separator.html">Separators</a> are recognised.
     *
     * @param query
     *            <code>www-form-urlencoded</code> string. If <code>null</code>, does nothing
     * @return a reference to this object
     */

    public QueryString append( final String query ) {

        appendOrSet( query, true );
        return this;
    }

    /**
     * Returns whether the query string is empty.
     *
     * @return true if the query string has no parameters
     */

    public boolean isEmpty() {

        return queryMap.isEmpty();
    }

    /**
     * Removes the named query parameter.
     * <p>
     * If the parameter has multiple values, all its values are removed.
     *
     * @param name
     *            name of the parameter to remove
     * @return a reference to this object
     */

    public QueryString remove( final String name ) {

        appendOrSet( name, null, false );
        return this;
    }



    /**
     * Compares the specified object with this QueryString for equality.
     * <p>
     * Returns <code>true</code> if the given object is also a QueryString and the two
     * QueryStrings have the same parameters. More formally, two QueryStrings
     * <code>t1</code> and <code>t2</code> represent the same QueryString if
     * <code>t1.toString().equals(t2.toString())</code>. This ensures that the <code>equals</code>
     * method checks the ordering, as well as the existence, of every parameter.
     * <p>
     * Clients interested only in the existence, not the ordering, of parameters are recommended to
     * use <code>getMap().equals</code>.
     * <p>
     * This implementation first checks if the specified object is this QueryString; if so
     * it returns <code>true</code>. Then, it checks if the specified object is a
     * QueryString whose toString() is identical to the toString() of this
     * QueryString; if not, it returns <code>false</code>. Otherwise, it returns
     * <code>true</code>
     *
     * @param obj
     *            object to be compared for equality with this QueryString.
     * @return <code>true</code> if the specified object is equal to this QueryString.
     */

    @Override
    public boolean equals( Object obj ) {

        if ( obj == this ) {
            return true;
        }

        if ( !( obj instanceof QueryString ) ) {
            return false;
        }

        String query = toString();
        String thatQuery = ( (QueryString) obj ).toString();

        return query.equals( thatQuery );
    }

    /**
     * Returns a hash code value for the QueryString.
     * <p>
     * The hash code of the QueryString is defined to be the hash code of the
     * <code>String</code> returned by toString(). This ensures the ordering, as well as the
     * existence, of parameters is taken into account.
     * <p>
     * Clients interested only in the existence, not the ordering, of parameters are recommended to
     * use <code>getMap().hashCode</code>.
     *
     * @return a hash code value for this QueryString.
     */

    @Override
    public int hashCode() {

        return toString().hashCode();
    }

    /**
     * Returns a <code>www-form-urlencoded</code> string of the query parameters.
     * <p>
     * The HTML specification recommends two parameter separators in <a
     * href="http://www.w3.org/TR/REC-html40/interact/forms.html#h-17.13.4.1">HTML 4.01
     * Specification: application/x-www-form-urlencoded</a> and <a
     * href="http://www.w3.org/TR/1999/REC-html401-19991224/appendix/notes.html#h-B.2.2">HTML 4.01
     * Specification: Ampersands in URI attribute values</a>. Of those, the ampersand is the more
     * commonly used and this method defaults to that.
     *
     * @return <code>www-form-urlencoded</code> string, or <code>null</code> if there are no
     *         parameters.
     */

    @Override
    public String toString() {

        return toString( Separator.AMPERSAND );
    }

    /**
     * Returns a <code>www-form-urlencoded</code> string of the query parameters, using the given
     * separator between parameters.
     *
     * @param separator
     *            separator to use between parameters
     * @return <code>www-form-urlencoded</code> string, or an empty String if there are no
     *         parameters
     */

    // Note: this method takes a Separator, not just any String. Taking any String may
    // be useful in some circumstances (eg. you could pass '&amp;' to generate query
    // strings for use in HTML pages) but would break the implied contract between
    // toString() and parse() (eg. you can always parse() what you toString() ).
    //
    // It was thought better to leave it to the user to explictly break this contract
    // (eg. toString().replaceAll( '&', '&amp;' ))
    public String toString( Separator separator ) {

        StringBuilder builder = new StringBuilder();

        for ( String name : this.queryMap.keySet() ) {
            for ( String value : this.queryMap.get( name ) ) {
                if ( builder.length() != 0 ) {
                    builder.append( separator );
                }

                // Encode names and values. Do this in toString(), rather than
                // append/set, so that the Map always contains the
                // raw, unencoded values

                builder.append( this.encoder.encode( name ) );

                if ( value != null ) {
                    builder.append( '=' );
                    builder.append( this.encoder.encode( value ) );
                }
            }
        }

        return builder.toString();
    }

    //
    // Private methods
    //

    /**
     * Private constructor.
     * <p>
     * Clients should use one of the <code>create</code> or <code>parse</code> methods to create a
     * <code>QueryString</code>.
     */

    protected QueryString(StringEncoder encoder)
    {
        this.encoder = encoder;    }

    protected QueryString(StringEncoder urlEncoder, Map<String, List<String>> parameterMap)
    {
        this(urlEncoder);

        // Defensively copy the List<String>'s
        for ( Map.Entry<String, List<String>> entry : parameterMap.entrySet() ) {
            this.queryMap.put( entry.getKey(), new ArrayList<String>( entry.getValue() ) );
        }

    }

    protected QueryString(StringEncoder urlEncoder, CharSequence query)
    {
        this(urlEncoder);
        // Note: import to call appendOrSet with 'true', in
        // case the given query contains multi-valued parameters
        this.appendOrSet( query, true );
    }

    /**
     * Helper method for append and set
     *
     * @param name
     *            the parameter's name
     * @param value
     *            the parameter's value
     * @param append
     *            whether to append (or set)
     */

    private void appendOrSet( final String name, final String value, final boolean append ) {

        if ( name == null ) {
            throw new NullPointerException( "name" );
        }

        // If we're appending, and there's an existing parameter...

        if ( append ) {
            List<String> listValues = this.queryMap.get( name );

            // ...add to it

            if ( listValues != null ) {
                listValues.add( value );
                return;
            }
        }

        // ...otherwise, if we're setting and the value is null...

        else if ( value == null ) {
            // ...remove it

            this.queryMap.remove( name );
            return;
        }

        // ...otherwise, create a new one

        List<String> listValues = new ArrayList<String>();
        listValues.add( value );

        this.queryMap.put( name, listValues );
    }

    /**
     * Helper method for append and set
     *
     * @param query
     *            <code>www-form-urlencoded</code> string
     * @param append
     *            whether to append (or set)
     */

    private void appendOrSet( final CharSequence parameters, final boolean append ) {

        // Nothing to do?

        if ( parameters == null ) {
            return;
        }

        // Note we always parse using PARSE_PARAMETER_SEPARATORS, regardless
        // of what the user later nominates as their output parameter
        // separator using toString()

        //StringTokenizer tokenizer = new StringTokenizer( parameters.toString(), PARSE_PARAMETER_SEPARATORS );
        String[] parts = parameters.toString().split("(" + Separator.AMPERSAND  + "|" + Separator.SEMICOLON + ")");

        Set<String> setAlreadyParsed = null;

        for (String parameter : parts)
        {
            int indexOf = parameter.indexOf( '=' );

            String name;
            String value;

            if ( indexOf == -1 ) {
                name = parameter;
                value = null;
            } else {
                name = parameter.substring( 0, indexOf );
                value = parameter.substring( indexOf + 1 );
            }

            // When not appending, the first time we see a given
            // name it is important to remove it from the existing
            // parameters

            if ( !append ) {
                if ( setAlreadyParsed == null ) {
                    setAlreadyParsed = new HashSet<String>();
                }

                if ( !setAlreadyParsed.contains( name ) ) {
                    remove( name );
                }

                setAlreadyParsed.add( name );
            }

            if ( value != null ) {
                value = this.encoder.decode( value );
            }

            appendOrSet( name, value, true );
        }
    }


}
