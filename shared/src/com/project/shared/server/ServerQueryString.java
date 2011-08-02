// This file contains the parts of the original UrlEncodedQueryString
// that require java classes that are not available on the GWT client

// Copyright (c) 2009, Richard Kennard
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

package com.project.shared.server;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;

import com.project.shared.data.StringEncoder;
import com.project.shared.utils.QueryString;

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
 * This class provides static methods for <a href="#create()">creating</a> UrlEncodedQueryString
 * instances by <a href="#parse(java.lang.CharSequence)">parsing</a> URI and string forms. It can
 * then be used to create, retrieve, update and delete parameters, and to re-apply the query string
 * back to an existing URI.
 * <p>
 * <h4>Encoding and decoding</h4> UrlEncodedQueryString automatically encodes and decodes parameter
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
 * This requirement significantly shapes the <code>UrlEncodedQueryString</code> API. In particular
 * there are:
 * <ul>
 * <li><code>set</code> methods for setting a parameter, potentially replacing an existing value
 * <li><code>append</code> methods for adding a parameter, potentially creating a multivalued
 * parameter
 * <li><code>get</code> methods for returning a single value, even if the parameter has multiple
 * values
 * <li><code>getValues</code> methods for returning multiple values
 * </ul>
 * <h4>Retrieving parameters</h4> UrlEncodedQueryString can be used to parse and retrieve parameters
 * from a query string by passing either a URI or a query string:
 * <p>
 * <code>
 * 		URI uri = new URI("http://java.sun.com?forum=2");<br/>
 *     	UrlEncodedQueryString queryString = UrlEncodedQueryString.parse(uri);<br/>
 *     	System.out.println(queryString.get("forum"));<br/>
 * </code>
 * <h4>Modifying parameters</h4> UrlEncodedQueryString can be used to set, append or remove
 * parameters from a query string:
 * <p>
 * <code>
 *     	URI uri = new URI("/forum/article.jsp?id=2&amp;para=4");<br/>
 *     	UrlEncodedQueryString queryString = UrlEncodedQueryString.parse(uri);<br/>
 *     	queryString.set("id", 3);<br/>
 *     	queryString.remove("para");<br/>
 *     	System.out.println(queryString);<br/>
 * </code>
 * <p>
 * When modifying parameters, the ordering of existing parameters is maintained. Parameters are
 * <code>set</code> and <code>removed</code> in-place, while <code>appended</code> parameters are
 * added to the end of the query string.
 * <h4>Applying the Query</h4> UrlEncodedQueryString can be used to apply a modified query string
 * back to a URI, creating a new URI:
 * <p>
 * <code>
 *     	URI uri = new URI("/forum/article.jsp?id=2");<br/>
 *     	UrlEncodedQueryString queryString = UrlEncodedQueryString.parse(uri);<br/>
 *     	queryString.set("id", 3);<br/>
 *     	uri = queryString.apply(uri);<br/>
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

public class ServerQueryString extends QueryString {

	protected ServerQueryString(Map<String, List<String>> parameterMap)
    {
        super(ServerQueryString.getUrlEncoder(), parameterMap);
    }

	protected ServerQueryString(CharSequence query)
    {
	    super(ServerQueryString.getUrlEncoder(), query);
    }

	protected ServerQueryString()
    {
	    super(ServerQueryString.getUrlEncoder());
    }

    /**
	 * Creates an empty UrlEncodedQueryString.
	 * <p>
	 * Calling <code>toString()</code> on the created instance will return an empty String.
	 */
	public static ServerQueryString create() {

		return new ServerQueryString();
	}

	/**
	 * Creates a UrlEncodedQueryString from the given Map.
	 * <p>
	 * The order the parameters are created in corresponds to the iteration order of the Map.
	 *
	 * @param parameterMap
	 *            <code>Map</code> containing parameter names and values.
	 */
	public static ServerQueryString create( Map<String, List<String>> parameterMap ) {

		return new ServerQueryString(parameterMap);

	}

	/**
	 * Creates a UrlEncodedQueryString by parsing the given query string.
	 * <p>
	 * This method assumes the given string is the <code>www-form-urlencoded</code> query component
	 * of a URI. When parsing, all <a href="UrlEncodedQueryString.Separator.html">Separators</a> are
	 * recognised.
	 * <p>
	 * The result of calling this method with a string that is not <code>www-form-urlencoded</code>
	 * (eg. passing an entire URI, not just its query string) will likely be mismatched parameter
	 * names.
	 *
	 * @param query
	 *            query string to be parsed
	 */
	public static ServerQueryString parse( final CharSequence query )
	{
		return new ServerQueryString(query);
	}

	/**
	 * Creates a UrlEncodedQueryString by extracting and parsing the query component from the given
	 * URI.
	 * <p>
	 * This method assumes the query component is <code>www-form-urlencoded</code>. When parsing,
	 * all separators from the Separators enum are recognised.
	 * <p>
	 * The result of calling this method with a query component that is not
	 * <code>www-form-urlencoded</code> will likely be mismatched parameter names.
	 *
	 * @param uri
	 *            URI to be parsed
	 */
	public static ServerQueryString parse( final URI uri ) {
		// Note: use uri.getRawQuery, not uri.getQuery, in case the
		// query parameters contain encoded ampersands (%26)
	    return new ServerQueryString(uri.getRawQuery());
	}

	//
	// Private statics
	//
    private static StringEncoder getUrlEncoder() {
        return new StringEncoder() {
            @Override
            public String encode(String value)
            {
                try {
                    return URLEncoder.encode(value, "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    throw new RuntimeException(e);
                }
            }

            @Override
            public String decode(String value)
            {
                try {
                    return URLDecoder.decode(value, "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    throw new RuntimeException(e);
                }
            }};
    }


	/**
	 * Applies the query string to the given URI.
	 * <p>
	 * A copy of the given URI is taken and its existing query string, if there is one, is replaced.
	 * The query string parameters are separated by <code>Separator.Ampersand</code>.
	 *
	 * @param uri
	 *            URI to copy and update
	 * @return a copy of the given URI, with an updated query string
	 */

	public URI apply( URI uri ) {

		return apply( uri, Separator.AMPERSAND );
	}

	/**
	 * Applies the query string to the given URI, using the given separator between parameters.
	 * <p>
	 * A copy of the given URI is taken and its existing query string, if there is one, is replaced.
	 * The query string parameters are separated using the given <code>Separator</code>.
	 *
	 * @param uri
	 *            URI to copy and update
	 * @param separator
	 *            separator to use between parameters
	 * @return a copy of the given URI, with an updated query string
	 */

	public URI apply( URI uri, Separator separator ) {

		// Note this code is essentially a copy of 'java.net.URI.defineString',
		// which is private. We cannot use the 'new URI( scheme, userInfo, ... )' or
		// 'new URI( scheme, authority, ... )' constructors because they double
		// encode the query string using 'java.net.URI.quote'

		StringBuilder builder = new StringBuilder();
		if ( uri.getScheme() != null ) {
			builder.append( uri.getScheme() );
			builder.append( ':' );
		}
		if ( uri.getHost() != null ) {
			builder.append( "//" );
			if ( uri.getUserInfo() != null ) {
				builder.append( uri.getUserInfo() );
				builder.append( '@' );
			}
			builder.append( uri.getHost() );
			if ( uri.getPort() != -1 ) {
				builder.append( ':' );
				builder.append( uri.getPort() );
			}
		} else if ( uri.getAuthority() != null ) {
			builder.append( "//" );
			builder.append( uri.getAuthority() );
		}
		if ( uri.getPath() != null ) {
			builder.append( uri.getPath() );
		}

		String query = toString( separator );
		if ( query.length() != 0 ) {
			builder.append( '?' );
			builder.append( query );
		}
		if ( uri.getFragment() != null ) {
			builder.append( '#' );
			builder.append( uri.getFragment() );
		}

		try {
			return new URI( builder.toString() );
		} catch ( URISyntaxException e ) {
			// Can never happen, as the given URI will always be valid,
			// and getQuery() will always return a valid query string

			throw new RuntimeException( e );
		}
	}


}
