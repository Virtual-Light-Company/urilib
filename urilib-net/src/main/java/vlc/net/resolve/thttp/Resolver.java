/*
 * Copyright (c) 1999 - 2012 The Virtual Light Company
 *                            http://www.vlc.com.au/
 *
 * This code is licensed under the GNU Library GPL v2.1. Please read docs/LICENSE.txt
 * for the full details. A copy of the LGPL may be found at
 *
 * http://www.gnu.org/copyleft/lgpl.html
 *
 * The code is distributed as-is and contains no warranty or guarantee for fitnesse of
 * purpose. Use it at your own risk.
 */

package vlc.net.resolve.thttp;

// Standard imports

import java.io.*;

import java.net.URLConnection;
import java.net.MalformedURLException;
import java.util.Properties;
import java.util.HashMap;
import java.util.ArrayList;

// Application specific imports
import org.ietf.uri.*;
import org.ietf.uri.resolve.UnknownNIDException;
import org.ietf.uri.resolve.ConfigErrorException;

/**
 * Resolver discovery service that is based on THTTP.
 * <p/>
 * <p/>
 * The THTTP specification is defined by <A HREF="http://www.csl.sony.co.jp/rfc/mirror/rfc2169.txt">RFC
 * 2169</A> and uses a service based on the HTTP specification. Therefore, in order for this
 * resolver to work, it must first be able to get hold of a HTTP resource handler.
 * <p/>
 * <p/>
 * The resolver requires the use of any named web server so we need to specify the names of the
 * servers that can be set. There are two ways of specifying the servers to be used: <OL> <LI>The
 * system property <CODE>urn.resolver.thttp.path</CODE> points to a URL that is the path to the
 * bindings file. To this path the filename <CODE>thttp_urn.conf</CODE> is added. A trailing '/' is
 * added if needed to the base URL. <LI>The file <CODE>thttp_urn.conf</CODE> is read from the
 * classpath and parsed to find the location of the servers. The list is a whitespace separated list
 * of servers and ports following the same syntax as above for the system property. <LI>The system
 * property <CODE>urn.resolver.thttp.servers</CODE> contains a pipe '|' separated list of web
 * servers and ports to be queried in order of preference. Since the web server may be running on a
 * non standard port, the strings may include an optional port number as such: <PRE>
 * www.bar.com:8000 | www.urn.net | resolver.foo.com:4321 </PRE> These servers are to be considered
 * as generally authoritative for when they are queried. They should be able to resolve any URN or
 * return the appropriate error code. </OL>
 * <p/>
 * For details on URIs see the IETF working group: <A HREF="http://www.ietf.org/html.charters/urn-charter.html">URN</A>
 * <p/>
 * <p/>
 * This softare is released under the <A HREF="http://www.gnu.org/copyleft/lgpl.html">GNU LGPL</A>
 * <p/>
 * <p/>
 * DISCLAIMER:<BR> This software is the under development, incomplete, and is known to contain bugs.
 * This software is made available for review purposes only. Do not rely on this software for
 * production-quality applications or for mission-critical applications.
 * <p/>
 * <p/>
 * Portions of the APIs for some new features have not been finalized and APIs may change. Some
 * features are not fully implemented in this release. Use at your own risk.
 * <p/>
 *
 * @author Justin Couch
 * @version 0.7 (27 August 1999)
 */
public class Resolver
    implements URNResolverService
{
    private static final String SERVER_FILE = "thttp_urn.conf";

    private static final String URN_SERVER_PROP = "urn.resolver.thttp.servers";
    private static final String URN_SYSTEM_PROP = "urn.resolver.thttp.path";

    private static final String INVALID_RESPONSE =
        "The server sent an Invalid response code for THTTP Queries";

    /** The list of static namespace resolvers available. */
    private static HashMap namespace_resolvers;

    /** The resource factory used by this resolver */
    private URIResourceFactory resource_factory = null;

    /**
     * Global initialiser reads in the bindings file for use within this class. It does not do any
     * parsing of individual elements at this stage. This method should only ever be called once as
     * calling it again would trash any previously loaded data
     *
     * @throws ConfigErrorException There was something wrong with the files that were read in for
     * basic configuration information.
     */
    public void init()
        throws ConfigErrorException
    {
        try
        {
            // fetch the properties file as a stream
            InputStream is = null;

            // OK, lets try the system property read for the full path
            // If we're not allowed to read it a security exception will be
            // thrown.
            String prop_path = System.getProperty(URN_SYSTEM_PROP);

            if(prop_path != null)
            {
                StringBuffer buffer = new StringBuffer(prop_path);

                if(!prop_path.endsWith("/"))
                    buffer.append('/');

                buffer.append(SERVER_FILE);

                // Create the URL. If this fails due to bad formatting, the catch
                // clause below will fix it.
                java.net.URL url = new java.net.URL(buffer.toString());
                URLConnection connection = url.openConnection();

                // open the connection and clear any possible header stuff that is
                // not needed to load this from the file.
                connection.connect();
                connection.getContentType();
                connection.getContentEncoding();
                connection.getContentLength();

                is = connection.getInputStream();
            }
            else
            {
                // try the system resources
                is = ClassLoader.getSystemResourceAsStream(SERVER_FILE);
            }

            // from that stream load it into a properties table
            if(is != null)
            {
                // This may produce a ConfigErrorException that we the pass back
                // upstream to the caller
                namespace_resolvers = FileParser.parseBindingsFile(is);
                is.close();
            }
            else
            {
                // create an empty hashmap to take its place
                namespace_resolvers = new HashMap();
            }
        }
        catch(IOException ioe)
        {
            throw new NoURNBindingsException("Static init URN Bindings:\n" + ioe);
        }
    }

    /**
     * Check that the list of services that this resolver provides matches the requested one. The only
     * one that this can't handle is II
     *
     * @param type The type of service this RDS requires
     * @return true if the RDS can handle the nominated type.
     */
    public boolean checkService(int type)
    {
        if((type == II) || (type == I2C) || (type == I2Cs))
            return false;
        else
            return true;
    }

    /**
     * Check that this RDS can resolve the given namespace identifier. If if can then return true.
     *
     * @param nid The namespace identifier to check
     * @return true if this RDS can resolve the given NID.
     */
    public boolean canResolve(String nid)
    {
        String servers = System.getProperty(URN_SYSTEM_PROP);

        if(servers != null)
            return true;
        else
            return (namespace_resolvers.get(nid) != null);
    }

    /**
     * Set the resource factory to be used by the resolver. This will be called once only during the
     * life of this class instance. If the implementation wishes to ignore this then it shall not
     * throw any errors.
     *
     * @param fac The factory to be set.
     */
    public void setResourceFactory(URIResourceFactory fac)
    {
        resource_factory = fac;
    }

    /**
     * Deal with the response of the HTTP connection and create an object for it for an I2L request
     *
     * @param res The connection to the HTTP resource - unconnected
     * @return The object representing the service type
     * @throws IOException I/O problems or invalid response
     * @throws MalformedURLException An error forming the response as a URL
     */
    private Object getI2LResponse(HttpResourceConnection res)
        throws IOException, MalformedURLException
    {
        int response;
        String header;
        Object ret_val = null;

        // we want to rip the headers so don't permit the redirect
        res.setFollowRedirects(false);
        res.connect();

        response = res.getResponseCode();

        // check for a 30X response code
        if((response / 100) != 3)
            throw new IOException(INVALID_RESPONSE);

        header = res.getHeaderField(HttpResourceConnection.LOCATION_HEADER);

        ret_val = new URL(header);

        return ret_val;
    }

    /**
     * Deal with the response of the HTTP connection and create an object for it for an I2Ls request
     *
     * @param res The connection to the HTTP resource - unconnected
     * @return The object representing the service type
     * @throws IOException I/O problems or invalid response
     * @throws MalformedURLException An error forming the response as a URL
     */
    private Object getI2LsResponse(HttpResourceConnection res)
        throws IOException, MalformedURLException
    {
        int response;
        String header;
        Object ret_val = null;

        // we want to rip the headers so don't permit the redirect
        // should put code in here to allow negotiation of content types too
        res.setFollowRedirects(false);
        res.connect();

        response = res.getResponseCode();

        // check for a 20X response code. If we get anything else, we've got
        // a dodgy response
        if((response / 100) != 2)
            throw new IOException(INVALID_RESPONSE);

        header = res.getContentType();

        // only process text/uri-list for the moment. Will have to deal
        // with HTML at some stage
        if(header.equals("text/uri-list"))
        {
            String[] uri_list = (String[])res.getContent();
            if((uri_list == null) || (uri_list.length == 0))
                throw new IOException(INVALID_RESPONSE);

            int i;
            ArrayList url_out = new ArrayList(uri_list.length);

            for(i = 0; i < uri_list.length; i++)
            {
                try
                {
                    url_out.add(new URL(uri_list[i]));
                }
                catch(MalformedURLException mue)
                {
                    // catch this here so we can ignore it and keep processing
                }
            }

            URL[] tmp_list = new URL[0];
            ret_val = url_out.toArray(tmp_list);
        }
        else if(header.equals("text/html") ||
            header.equals("application/html"))
        {
            System.out.println("Got a HTML THTTP Response!");
        }

        return ret_val;
    }

    /**
     * Deal with the response of the HTTP connection and create an object for it for an I2R request.
     * This also follows redirects if they are needed. It does not directly fetch the object, rather
     * it returns the correctly setup unconnected resource connection for the user to make use of
     *
     * @param res The connection to the HTTP resource - unconnected
     * @return The object representing the service type
     * @throws IOException I/O problems or invalid response
     * @throws MalformedURLException An error forming the response as a URL
     */
    private Object getI2RResponse(HttpResourceConnection res)
        throws IOException, MalformedURLException
    {
        res.setFollowRedirects(true);
        return res;
    }

    /**
     * Deal with the response of the HTTP connection and create an object for it for an I2R request.
     * This also follows redirects if they are needed. It does not directly fetch the object, rather
     * it returns the correctly setup unconnected resource connection for the user to make use of
     *
     * @param res The connection to the HTTP resource - unconnected
     * @return The object representing the service type
     * @throws IOException I/O problems or invalid response
     * @throws MalformedURLException An error forming the response as a URL
     */
    private Object getI2RsResponse(HttpResourceConnection res)
        throws IOException, MalformedURLException
    {
        res.setFollowRedirects(true);

        // what should we do here. We are returning a mime multipart message
        // which means only one resource connection with many parts rather than the
        // original goal of many resource connections. Could be sticky trying to
        // deal with this.
        ResourceConnection[] ret_val = { res };
        return ret_val;
    }

    /**
     * Deal with the response of the HTTP connection and create an object for it for an I2Ns request
     *
     * @param res The connection to the HTTP resource - unconnected
     * @return The object representing the service type
     * @throws IOException I/O problems or invalid response
     * @throws MalformedURLException An error forming the response as a URL
     */
    private Object getI2NsResponse(HttpResourceConnection res)
        throws IOException, MalformedURLException
    {
        int response;
        String header;
        Object ret_val = null;

        // we want to rip the headers so don't permit the redirect
        // should put code in here to allow negotiation of content types too
        res.setFollowRedirects(false);
        res.connect();

        response = res.getResponseCode();

        // check for a 20X response code. If we get anything else, we've got
        // a dodgy response that won't give us the answers that we need.
        if((response / 100) != 2)
            throw new IOException(INVALID_RESPONSE);

        header = res.getContentType();

        // only process text/uri-list for the moment. Will have to deal
        // with HTML at some stage
        if(header.equals("text/uri-list"))
        {
            String[] uri_list = (String[])res.getContent();
            if((uri_list == null) || (uri_list.length == 0))
                throw new IOException(INVALID_RESPONSE);

            int i;
            ArrayList url_out = new ArrayList(uri_list.length);

            for(i = 0; i < uri_list.length; i++)
            {
                try
                {
                    url_out.add(new URN(uri_list[i]));
                }
                catch(MalformedURNException mue)
                {
                    // catch this here so we can ignore it and keep processing
                }
            }

            URN[] tmp_list = new URN[0];
            ret_val = url_out.toArray(tmp_list);
        }
        else if(header.equals("text/html") ||
            header.equals("application/html"))
        {
            System.out.println("Got a HTML THTTP Response!");
        }

        return ret_val;
    }

    /**
     * Decode the namespace specific string into a particular resource and retrieve it. The service
     * may not be able to resolve the requested service type, although before asking a check should be
     * made of the base class checkService method.
     * <p/>
     * If the resolver cannot deal with this URN, then return null.
     *
     * @param urn The URN that this service should decode.
     * @param service The type of object that should be returned
     * @return A URC, URI or ResourceConnection as appropriate
     * @throws UnsupportedServiceException The service requested is not available from this resolver.
     */
    public Object decode(URN urn, int service)
        throws UnsupportedServiceException
    {
        String nid = urn.getNamespace();

        // first try the list of specified servers
        NamespaceResolver resolver =
            (NamespaceResolver)namespace_resolvers.get(nid);

        Object ret_val = null;

        if(resolver != null)
        {
            try
            {
                ResourceDescriptor desc = resolver.decode(urn, service);

                // we synchronize on the path string buffer just to prevent another
                // caller from accidentally calling it just as we've added the URN to
                // the end. This way, we can correct it before anyone else gets to use
                // it.
                ResourceConnection resc = null;
                synchronized(desc.path)
                {
                    desc.path.append(urn.toExternalForm());
                    int end = desc.path.length();

                    resc = resource_factory.requestResource(URIConstants.HTTP_SCHEME,
                                                            desc.server,
                                                            desc.port,
                                                            desc.path.toString());

                    // reset the path!
                    desc.path.delete(desc.pathLength, end);
                }

                // With the valid connection, make the connect, post the URL and get the
                // answer. Then, depending on the content type, we'd need to process it.
                HttpResourceConnection http_res = (HttpResourceConnection)resc;

                switch(service)
                {
                    case I2L:
                        ret_val = getI2LResponse(http_res);
                        break;

                    case I2Ls:
                        ret_val = getI2LsResponse(http_res);
                        break;

                    case I2R:
                        ret_val = getI2RResponse(http_res);
                        break;

                    case I2Rs:
                        ret_val = getI2RsResponse(http_res);
                        break;

                    case I2N:
                        // just return the URN itself
                        ret_val = urn;
                        break;

                    case I2Ns:
                        ret_val = getI2NsResponse(http_res);
                        break;

                    case I2C:
                        break;

                    case I2Cs:
                        break;

                    case II:
                        break;
                }
            }
            catch(IOException ioe)
            {
            }
            catch(UnknownNIDException une)
            {
            }
        }
        else
        {
            System.out.println("Null namespace resolver - not good!");
        }

        return ret_val;
    }

    /**
     * Decode the namespace specific string into a list of resources and retrieve them. The service
     * may not be able to resolve the requested service type, although before asking a check should be
     * made of the base class.
     *
     * @param urn The URN that this service should decode.
     * @param service The type of object that should be returned
     * @return A URC, URI or ResourceConnections as appropriate
     * @throws UnsupportedServiceException The service requested is not available from this resolver.
     */
    public Object[] decodeList(URN urn, int service)
        throws UnsupportedServiceException
    {
        String nid = urn.getNamespace();

        return null;
    }
}
