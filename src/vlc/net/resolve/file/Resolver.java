/*****************************************************************************
 *                The Virtual Light Company Copyright (c) 1999
 *                               Java Source
 *
 * This code is licensed under the GNU Library GPL. Please read license.txt
 * for the full details. A copy of the LGPL may be found at
 *
 * http://www.gnu.org/copyleft/lgpl.html
 *
 * Project:    URI Class libs
 *
 * Version History
 * Date        TR/IWOR  Version  Programmer
 * ----------  -------  -------  ------------------------------------------
 *
 ****************************************************************************/

package vlc.net.resolve.file;

// Standard imports
import java.io.*;

import java.net.URLConnection;
import java.net.MalformedURLException;
import java.util.Properties;
import java.util.HashMap;

// Application specific imports
import org.ietf.uri.*;
import org.ietf.uri.resolve.UnknownNIDException;
import org.ietf.uri.resolve.ConfigErrorException;

/**
 * Resolver discovery service that is based on file information.
 * <P>
 *
 * The bindings file can be specified in one of two forms. It is
 * located in the following manner.
 * <OL>
 * <LI>The system property <CODE>urn.bindings.path</CODE> points to a URL that
 *   is the path to the bindings file. To this path the filename
 *   <CODE>urn_bindings</CODE> is added. A trailing '/' is added if needed to
 *   the base URL.
 * <LI>The file <CODE>urn_bindings</CODE> is searched for in the system
 *   classpath.
 * </OL>
 *
 * If the file is not found after all this, the resolver exits with an
 * exception.
 * <P>
 *
 * Currently, there are some restrictions on usage. The bindings file is only
 * loaded on first reference to this class. That means the system property
 * needs to be set before asking to URN resolution and can't be changed.
 * <P>
 *
 * For details on URIs see the IETF working group:
 * <A HREF="http://www.ietf.org/html.charters/urn-charter.html">URN</A>
 * The specification on a File based RDS implementation may be found at
 * <A HREF="http://www.vlc.com.au/~justin/java/urn/file_based_resolver.html">
 * http://www.vlc.com.au/~justin/java/urn/file_based_resolver.html</A>
 * <P>
 *
 * This softare is released under the
 * <A HREF="http://www.gnu.org/copyleft/lgpl.html">GNU LGPL</A>
 * <P>
 *
 * DISCLAIMER:<BR>
 * This software is the under development, incomplete, and is
 * known to contain bugs. This software is made available for
 * review purposes only. Do not rely on this software for
 * production-quality applications or for mission-critical
 * applications.
 * <P>
 *
 * Portions of the APIs for some new features have not
 * been finalized and APIs may change. Some features are
 * not fully implemented in this release. Use at your own risk.
 * <P>
 *
 * @author  Justin Couch
 * @version 0.7 (27 August 1999)
 */
public class Resolver
  implements URNResolverService
{
  private static final String URN_BINDINGS_FILE = "urn_bindings";

  private static final String URN_SYSTEM_PROP = "urn.bindings.path";

  /** Unsupported service type error message */
  private static final String BAD_TYPE_MSG = 
    "File service does not support service type: ";

  /** The list of all the bindings for all URNs type */
  private static HashMap namespace_resolvers;

  /** The resource factory used to implement connections */
  private URIResourceFactory resource_factory = null;

  /**
   * Static initialiser reads in the bindings file for use within this
   * class. It does not do any parsing of individual elements at this stage.
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

        buffer.append(URN_BINDINGS_FILE);

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
        is = ClassLoader.getSystemResourceAsStream(URN_BINDINGS_FILE);
      }

      // from that stream load it into a properties table
      if(is == null)
        throw new NoURNBindingsException("No stream to bindings file");

      // parse the bindings file. If there are syntax errors here this
      // will throw a ConfigErrorException, which we want to pass through
      // to the caller.
      namespace_resolvers = FileParser.parseBindingsFile(is);
      is.close();
    }
    catch(IOException ioe)
    {
      throw new NoURNBindingsException("Static init URN Bindings:\n" +
                                       ioe);
    }
  }

  /**
   * Check that the list of services that this resolver provides matches the
   * requested one.
   *
   * @param type The type of service this RDS requires
   * @return true if the RDS can handle the nominated type.
   */
  public boolean checkService(int type)
  {
    boolean ret_val = false;

    switch(type)
    {
      case I2R:
      case I2Rs:
      case I2L:
      case I2Ls:
        ret_val = true;
        break;

      case I2C:
      case I2Cs:
      case I2N:
      case I2Ns:
      case II:
        ret_val = false;
        break;
    }

    return ret_val;
  }

  /**
   * Check that this RDS can resolve the given namespace identifier. If if can
   * then return true.
   *
   * @param nid The namespace identifier to check
   * @return true if this RDS can resolve the given NID.
   */
  public boolean canResolve(String nid)
  {
    return namespace_resolvers.containsKey(nid);
  }

  /**
   * Set the resource factory to be used by the resolver. This will be called
   * once only during the life of this class instance. If the implementation
   * wishes to ignore this then it shall not throw any errors.
   *
   * @param fac The factory to be set.
   */
  public void setResourceFactory(URIResourceFactory fac)
  {
    resource_factory = fac;
  }

  /**
   * Decode the namespace specific string into a particular resource
   * and retrieve it. The service may not be able to resolve the requested
   * service type, although before asking a check should be made of the base
   * class checkService method.
   * <P>
   * If the resolver cannot deal with this URN, then return null.
   *
   * @param urn The URN that this service should decode.
   * @param service The type of object that should be returned
   * @return A URC, URI or ResourceConnection as appropriate
   * @exception UnsupportedServiceException The service requested is not
   *   available from this resolver.
   */
  public Object decode(URN urn, int service)
    throws UnsupportedServiceException
  {
    String nid = urn.getNamespace();

    if((service != I2L) && (service != I2R))
      throw new UnsupportedServiceException(BAD_TYPE_MSG + service);

    if(!canResolve(nid))
      return null;

    Object ret_val = null;
    String current_url = null;
    NamespaceResolver res = (NamespaceResolver)namespace_resolvers.get(nid);

    try
    {
      current_url = res.decode(urn);
    }
    catch(UnknownNIDException une)
    {
      // ignore this because it allows us to return null
    }
    catch(UnresolvableURIException ure)
    {
      // ignore this because it allows us to return null
    }

    if(service == I2L)
    {
      try
      {
        ret_val = new URL(current_url);
      }
      catch(MalformedURLException mue)
      {
        // ignore this because it allows us to return null
      }
    }
    else
    {
      // OK, make a resource connection out of this
      try
      {
        String protocol = URI.getScheme(current_url);
        String [] tmp = URIUtils.getHostAndPortFromUrl(current_url);
        String host = null;
        int port = 0;

        if(tmp != null)
        {
          host = tmp[0];
          port = Integer.parseInt(tmp[1]);
        }

        String path = URIUtils.getPathFromUrlString(current_url);
        String query = URIUtils.getQueryFromUrlString(current_url);

        String real_path = path + "?" + query;

        ret_val = 
          resource_factory.requestResource(protocol, host, port, real_path);
      }
      catch(IOException ioe)
      {
        // ignore this because it allows us to return null
      }
    }

    return ret_val;
  }

  /**
   * Decode the namespace specific string into a list of resources and
   * retrieve them. The service may not be able to resolve the requested
   * service type, although before asking a check should be made of the base
   * class.
   *
   * @param urn The URN that this service should decode.
   * @param service The type of object that should be returned
   * @return A URC, URI or ResourceConnections as appropriate
   * @exception UnsupportedServiceException The service requested is not
   *   available from this resolver.
   */
  public Object[] decodeList(URN urn, int service)
    throws UnsupportedServiceException
  {
    String nid = urn.getNamespace();

    if((service != I2Ls) && (service != I2Rs))
      throw new UnsupportedServiceException(BAD_TYPE_MSG + service);

    if(!canResolve(nid))
      return null;

    NamespaceResolver res = (NamespaceResolver)namespace_resolvers.get(nid);

    int i, size;
    int ret_cnt = 0;
    Object[] ret_vals = null;
    String[] current_url = null;

    try
    {
      current_url = res.decodeList(urn);
    }
    catch(UnknownNIDException une)
    {
      // ignore this because it allows us to return null
    }

    if(service == I2Ls)
    {
      size = (current_url != null) ? current_url.length : 0;
      ret_vals = new Object[size];

      for(i = 0; i < size; i++)
      {
        try
        {
          ret_vals[ret_cnt++] = new URL(current_url[i]);
        }
        catch(MalformedURLException mue)
        {
          // ignore this and go on to the next one
        }
      }
    }
    else
    {
      // OK, make a list of resource connections out of this

      size = (current_url != null) ? current_url.length : 0;
      ret_vals = new Object[size];

      for(i = 0; i < size; i++)
      {
        try
        {
          String protocol = URI.getScheme(current_url[i]);
          String [] tmp = URIUtils.getHostAndPortFromUrl(current_url[i]);
          String host = null;
          int port = 0;

          if(tmp != null)
          {
            host = tmp[0];
            port = Integer.parseInt(tmp[1]);
          }

          String path = URIUtils.getPathFromUrlString(current_url[i]);
          String query = URIUtils.getQueryFromUrlString(current_url[i]);

          String real_path = path + "?" + query;

          ret_vals[ret_cnt++] =
            resource_factory.requestResource(protocol, host, port, real_path);

        }
        catch(IOException ioe)
        {
          // ignore this because it allows us to return null
        }
      }
    }

    // quick check to see if we need to reallocate the size of the array
    // that is being returned.
    if(ret_cnt != size)
    {
      URL[] tmp = new URL[ret_cnt];
      System.arraycopy(ret_vals, 0, tmp, 0, ret_cnt);
      ret_vals = tmp;
    }

    return ret_vals;
  }

/*******

  public static void main(String[] args)
  {
    String[] urn_list = {
      "urn:vrml:eai:/worker/home.gif",
      "urn:vrml:umel:/worker/home.gif",
      "urn:vrml:umel:worker/home.gif",
      "urn:cid:199606121851.1@mordred.gatech.edu"
    };

    String[] comment_list = {
      "VRML urn trial",
      "UMEL basic",
      "UMEL, no leading slash",
      "Weirdo example using CID"
    };

    Resolver resolver = new Resolver();

    for(int i = 0; i < urn_list.length; i++)
    {
      System.out.println();
      System.out.println(comment_list[i]);
      System.out.println("URI: " + urn_list[i]);

      try
      {
        URN urn = new URN(urn_list[i]);
        URL url = (URL)resolver.decode(urn, I2L);
        if(url == null)
          System.out.println("Unable to resolve this to a URL");
        else
          System.out.println("URL is: " +  url.toExternalForm());

        Object[] list = resolver.decodeList(urn, I2Ls);

        if((list != null) && (list.length > 0))
        {
          System.out.println("Got " + list.length + " urls");
          for(int j = 0; j < list.length; j++)
            System.out.println(" url[" + j + "] is " + ((URL)list[j]).toExternalForm());

        }

        ResourceConnection res = (ResourceConnection)resolver.decode(urn, I2R);
        if(res == null)
          System.out.println("Unable to resolve this to a Connection");
        else
          System.out.println("Connection is: " +  res);
      }
      catch (Throwable e)
      {
        System.out.println("Ooops: " + e);
        e.printStackTrace();
      }
    }
  }
*******/
}
