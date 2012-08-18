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

package org.ietf.uri;

// Standard imports
import java.io.*;
import java.util.*;

// Application specific imports
import org.ietf.uri.resolve.ConfigErrorException;

/**
 * Class <code>RDSManager</code> represents the manager of RDS resources used
 * by a single application.
 * <P>
 *
 * Resolvers are located by using a list of Resolver Discovery Services
 * (<A HREF=http://info.internet.isi.edu/in-notes/rfc/files/rfc2168.txt">RFC 2168</A>)
 * As a system may have multiple RDS options, the class determines the order
 * from:
 * <OL>
 * <LI>Reading the system property <CODE>urn.resolve.order</CODE> that contains
 *     a pipe ('|') separated list of names of resolvers to query in decreasing
 *     order of preference.
 * <LI>Finding the file <CODE>urn.conf</CODE> within the classpath specified
 *     for this application's instance. This file contains a list of resolvers,
 *     one per line to use.
 * <LI>If no resolver list is found the class will not be able to create an
 *     instance of itself and will fail with a
 *     <CODE>NoResolverServicesException</CODE>
 * </OL>
 *
 * The names specified in this list shall not contain anything other than
 * alphanumeric characters and the first character of the RDS type
 * shall be alphabetical. Whitespace and other characters are not permitted.
 * <P>
 *
 * To locate a particular RDS, the following proceedure is used:
 * <OL>
 * <LI>If a <CODE>URNResolverServiceFactory</CODE> has been set, then it a
 *     request is made using the name of the discovery service required. If it
 *     knows of the correct service type it is returned. If not, then null is
 *     returned.
 * <LI>The <CODE>urn.resolve.pkgs</CODE> system property is read that contains
 *     a pipe ('|') separated list of packages to examine. The name extracted
 *     from the resolver order list is turned into a lowercase string and
 *     appended to the package name to form the full package name. Within that
 *     package, <CODE>Class.forName()</CODE> is used to load the class named
 *     <CODE>Resolver</CODE>.
 * <LI>The package <CODE>org.ietf.uri.resolve</CODE> is queried using the
 *     name of the resolver as per the previous step.
 * </OL>
 * <P>
 *
 * There are many possiblities of how RDSs can be found, depending on the setup
 * of the individual machine. This manager attempts to make the most efficient
 * lookup scheme for each situation. For this reason, there is no public
 * constructor as it allows the internals to produce the best representation.
 * <P>
 *
 * To make use of this class, call the <CODE>getRDSManager</CODE> to allow the
 * the use of multiple RDS's or <CODE>getSingleRDS</CODE> if the request only
 * needs a single RDS instance for its dealings.
 * <P>
 *
 * For details on URIs see the IETF working group:
 * <A HREF="http://www.ietf.org/html.charters/urn-charter.html">URN</A>
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
public final class RDSManager
{
  private static final String CONFIG_FILE = "urn.conf";

  private static final String CONFIRM_POLICY = "urn.resolve.confirm";
  private static final String PKG_LIST_PROP = "urn.resolve.pkgs";
  private static final String RESOLVE_ORDER = "urn.resolve.order";

  private static final String DEFAULT_PKG = "org.ietf.uri.resolve";

  /** The factory to use, if set */
  private static URIResolverServiceFactory rds_factory = null;

  /**
   * The list of currently loaded RDSs. They are stored using the name
   * used in the config settings as the key, and the RDS instance as the value.
   */
  private static HashMap rds_loaded = new HashMap();

  /**
   * The list of RDSs to search in order, as supplied by the URN config file.
   * Typically, this could be overridden by the system property settings, so
   * is stored as the file only values that are referenced when needed.
   */
  private static String[] file_rds_search_order;

  /**
   * The order to be used for checking the RDS's. This should only be used if
   * a known fixed resolve order is available. When it can be changed on the
   * fly (sys_prop_fixed == false), this should not be used.
   */
  private static String[] resolve_order;

  /**
   * Flag defining that the system property is fixed and can't be changed.
   * Fixed means that either it can't be written or the security manager won't
   * allow it to be read in the first place.
   */
  private static boolean sys_prop_fixed = false;

  /** The resource factory implementation passed to all the resolvers */
  private static URIResourceFactory resource_factory = null;

  /** List of locally registered URN resolvers */
  private static ArrayList local_resolvers = null;


  /**
   * Inner class that is used as an enumerator of all the resolvers when
   * a request is made to get all resolvers. The enumeration takes a snapshot
   * of the current resolver order which is then presented to the user as they
   * request the next item.
   */
  private static class RDSEnumerator implements Enumeration
  {
    private String[] resolver_order;

    private int current_element = 0;

    private int last_element;

    private String namespace;
    private int rds_service;

    private boolean locals_checked;

    /**
     * Create a new instance of this enumerator. Sets the enumeration up and
     * takes a snapshot of the current resolver order. This is performed using
     * System.arraycopy allowing the passed in parameter to be discarded if not
     * used anywhere else.
     * <P>
     *
     * The enumerator provides two levels of service. The check for more
     * elements just does a numerical check to see if there are still some
     * that we haven't considered yet. The request for the next properly checks
     * and can throw an exception if none of the remaining candidates match
     * the required profile. A user should always check for a
     * {@link java.util.NoSuchElementException} when calling this method.
     *
     * @param order The resolver order.
     * @param nid The namespace identifier that must satisfy this list
     * @param service The RDS service required for the namespace ID
     */
    public RDSEnumerator(String[] order, String nid, int service)
    {
      resolver_order = new String[order.length];
      System.arraycopy(order, 0, resolver_order, 0, order.length);

      namespace = nid;
      rds_service = service;

      last_element = resolver_order.length - 1;
      locals_checked = false;
    }

    /**
     * Test to see if this enumeration has more elements. Returns true if
     * we haven't reached to end of the resolver list but does not check to
     * see if the remaining elements are useful.
     *
     * @return true if more resolvers are potentially available.
     */
    public boolean hasMoreElements()
    {
      return (!locals_checked && local_resolvers.size() > 0) ||
             (current_element <= last_element);
    }

    /**
     * Find the next resolver that matches the required capabilities. A search
     * of the remaining list is made to find a match. If no match is made then
     * a <CODE>NoSuchElementException</CODE> is generated and no further
     * queries should be made of this method.
     *
     * @return The next URNResolverService in order of preference
     * @exception NoSuchElementException There are no more matching services.
     */
    public Object nextElement()
      throws NoSuchElementException
    {
      URNResolverService ret_val = null;

      int i = current_element;

      if(!locals_checked)
      {
        int size = local_resolvers.size();
        for(int j = 0; j < size; j++)
        {
          URNResolverService res = (URNResolverService)local_resolvers.get(j);
          if(res.canResolve(namespace))
          {
            ret_val = res;
            break;
          }

        }

        locals_checked = true;
      }

      for( ; (ret_val == null) && (i <= last_element); i++)
      {
        // try the hashmap first to see if we have it loaded.
        Object resolver = rds_loaded.get(resolver_order[i]);

        if(resolver != null)
          ret_val = (URNResolverService)resolver;
        else
          ret_val = loadResolver(resolver_order[i]);

        if((ret_val != null) &&
           ret_val.canResolve(namespace) &&
           ret_val.checkService(rds_service))
        {
          break;
        }
        else
          ret_val = null;
      }

      current_element = i + 1;

      if(ret_val == null)
        throw new NoSuchElementException();

      return ret_val;
    }
  }

  //
  // end of RDSEnumerator inner class.
  //

  /**
   * Read in the contents of the URN config file. This is read into an
   * array that is used as the sort order if the global variable is not
   * set. Note that we do not form a static view of the world here, yet.
   * If permissions have been set correctly, it is possible that the
   * caller may change the search order dynamically. We can make certain
   * assumptions by checking the security manager settings and determining
   * whether we can pre-load some of this stuff. See the getHandler method
   * for more details on how this is done.
   */
  static
  {
    SecurityManager sm = System.getSecurityManager();
    String resolve_prop = null;

    if(sm != null)
    {
      try
      {
        PropertyPermission perm = new PropertyPermission(RESOLVE_ORDER, "read");
        sm.checkPermission(perm);
        resolve_prop = System.getProperty(RESOLVE_ORDER);

        perm = new PropertyPermission(RESOLVE_ORDER, "write");
        sm.checkPermission(perm);
      }
      catch(SecurityException se)
      {
        System.out.println("Not able to read sys prop " + RESOLVE_ORDER);
        sys_prop_fixed = true;
      }
    }

    if(sys_prop_fixed)
    {
      String[] resolve_order;

      if(resolve_prop != null)
        resolve_order = parsePropConfig(resolve_prop);
      else
      {
        loadConfigFile();
        resolve_order = file_rds_search_order;
      }
    }

    // OK, all worked, now lets create our resource factory interface
    resource_factory = new URIResourceFactoryImpl();
    local_resolvers = new ArrayList();
  }

  /**
   * Load and parse the config file for RDS search order.
   */
  private static void loadConfigFile()
  {
    // read in and process the urn config file
    InputStream is = ClassLoader.getSystemResourceAsStream(CONFIG_FILE);

    String current_line;
    ArrayList temp_list = new ArrayList(10);

    if(is != null)
    {
      InputStreamReader isr = new InputStreamReader(is);

      StreamTokenizer strtok = new StreamTokenizer(isr);

      strtok.commentChar('#');
      strtok.wordChars('-', '-');
      strtok.eolIsSignificant(false);

      try
      {
        String token;
        while(strtok.nextToken() != StreamTokenizer.TT_EOF)
        {
          if(strtok.ttype != StreamTokenizer.TT_WORD)
          {
            System.err.println("Error in urn.conf on line " +
                               strtok.lineno() +
                               ". Invalid input");
            continue;
          }

          token = strtok.sval;
          temp_list.add(token);
        }
      }
      catch(IOException ioe)
      {
        System.err.println("General I/O Error reading config file " + ioe);
      }
    }

    int size = temp_list.size();
    file_rds_search_order = new String[size];

    temp_list.toArray(file_rds_search_order);
  }

  /**
   * Parse a property string and find all the available parts. This assumes
   * that the list is separated using pipe '|' characters. The list of
   * individual items are returned as an array of strings. If there are no
   * elements in the property list, the array returned is of length zero.
   *
   * @param propList The string value of the property to dice
   * @return An array of all the strings extracted from the property
   */
  private static String[] parsePropConfig(String propList)
  {
    if(propList == null)
      return new String[0];

    int i = 0;
    StringTokenizer strtok = new StringTokenizer(propList, "|");
    int size = strtok.countTokens();
    String[] ret_val = new String[size];

    try
    {
      for(i = 0; i < size; i++)
        ret_val[i] = strtok.nextToken();
    }
    catch(NoSuchElementException nse)
    {
      // Hmmm.... OK. well, just exit, first triming the array to the
      // correct size since i
      String[] tmp_list = new String[i];
      System.arraycopy(ret_val, 0, tmp_list, 0, i);
      ret_val = tmp_list;
    }

    return ret_val;
  }

  /**
   * Get all of the available resolvers for the nominated query type. This
   * method would be called to handle resolution of <CODE>N2Xs</CODE> query
   * types where a list of all services are wanted. If no resolvers are found
   * the enumeration will contain no elements.
   * <P>
   * The list of resolvers is guaranteed to be accurate in order at the
   * time of the request. That is, requesting all resolvers, changing the
   * resolving order property and then making requests of the original
   * enumeration will retain the original order.
   * <P>
   * The return values from the enumeration are guaranteed to be
   * <CODE>URNResolverSerivces</CODE>. The difference between this and
   * standard enumerations is that the <CODE>hasMoreElements</CODE> method
   * returns true if there are <I>potentially</I> more resolvers available.
   * There may be more resolvers in the list but none satisfy the requirements
   * and therefore nothing can be returned. If the enumeration runs out of
   * options then a {@link java.util.NoSuchElementException} is generated.
   * <P>
   *
   * @param nid The namespace identifier to be resolved.
   * @param type The type indicated by the values I2R, I2L etc from the
   *  URIResolver interface.
   * @return A reference the available resolvers
   * @exception NoURNConfigException Unable to find order for RDS loading.
   */
  public static Enumeration getAllResolvers(String nid, int type)
    throws NoURNConfigException
  {
    String[] local_resolve_order = getResolverOrder();

    // if everything is dead then....
    if(local_resolve_order == null)
      throw new NoURNConfigException("Fetching all resolvers");

    return new RDSEnumerator(local_resolve_order, nid, type);
  }

  /**
   * Request the manager to return us the first available resolver that can
   * handle the nominated query type. If no resolver can be found then null
   * is returned.
   *
   * @param nid The namespace identifier to be resolved.
   * @param type The type indicated by the values I2R, I2L etc from the
   *  URIResolver interface.
   * @return A reference to the first available resolver
   * @exception NoURNConfigException Unable to find order for RDS loading.
   */
  public static URNResolverService getFirstResolver(String nid, int type)
    throws NoURNConfigException
  {
    URNResolverService ret_val = null;
    String[] local_resolve_order = getResolverOrder();

    int size = local_resolvers.size();

    // if everything is dead then....
    if((local_resolve_order == null) && (size == 0))
      throw new NoURNConfigException("Fetching single resolver");

    int i;

    // Try the local resolvers first
    if(size != 0)
    {
      for(i = 0; i < size; i++)
      {
        URNResolverService res = (URNResolverService)local_resolvers.get(i);
        if(res.canResolve(nid))
        {
          ret_val = res;
          break;
        }

      }
    }

    for(i = 0; (ret_val != null) && (i < local_resolve_order.length); i++)
    {
      // try the hashmap first to see if we have it loaded.
      Object resolver = rds_loaded.get(local_resolve_order[i]);

      if(resolver != null)
        ret_val = (URNResolverService)resolver;
      else
        ret_val = loadResolver(local_resolve_order[i]);

      if((ret_val != null) &&
         ret_val.canResolve(nid) &&
         ret_val.checkService(type))
        break;
      else
        ret_val = null;
    }

    return ret_val;
  }

  /**
   * A simple convenience method that allows the caller to see a list of the
   * available resolver types that could be used. These strings have no use
   * other than for informational purposes only. If there are no resolvers
   * specified then this will return an empty list.
   *
   * @return An un-editable list of the resolvers as strings
   */
  public static Enumeration listResolverTypesOrder()
  {
    String[] ordered_values = getResolverOrder();

    Enumeration ret_val = null;

    if(ordered_values != null)
    {
      ret_val = new StringEnumerator(ordered_values);
    }

    return ret_val;
  }

  /**
   * Search for the list of all resolvers available and return the ones
   * that are needed.
   *
   * @return An array of available resolvers or null if none
   */
  private static String[] getResolverOrder()
  {
    String[] ret_val = null;

    if(sys_prop_fixed)
    {
      ret_val = resolve_order;
    }
    else
    {
      // read the property then dice it up. If the property has not been
      // defined then default back to the values from the config file. If
      // both don't exist, BARF!
      try
      {
        String sys_prop = System.getProperty(RESOLVE_ORDER);

        if(sys_prop != null)
          ret_val = parsePropConfig(sys_prop);
        else
        {
          if(file_rds_search_order == null)
            loadConfigFile();

          ret_val = file_rds_search_order;
        }
      }
      catch(SecurityException se)
      {
        // DOH! This shouldn't happen. Default back to loaded values....
        ret_val = resolve_order;
      }
    }
    return ret_val;
  }

  /**
   * Find load the named resolver. First check the factory (if it is set)
   * then the search path, and if that all fails, try the default package
   * name. If no resolver of the type required can be found, null is
   * returned.
   *
   * @param type The type (defined in the resolver order) to load.
   * @return The URNResolver needed.
   */
  private static URNResolverService loadResolver(String type)
  {
    // if we have a factory, use it.
    if(rds_factory != null)
    {
      URIResolverService service = rds_factory.findResolverService(type);
      if((service != null) && (service instanceof URNResolverService))
      {
        try
        {
          service.init();
          ((URNResolverService)service).setResourceFactory(resource_factory);
          rds_loaded.put(type, service);

          return (URNResolverService)service;
        }
        catch(ConfigErrorException cee)
        {
            // ignore this and head to the package list area then.
        }
      }
    }

    // hmmm... didn't find one in the factory, lets try the system properties.
    String pkg_list = null;

    try
    {
      pkg_list = System.getProperty(PKG_LIST_PROP);
    }
    catch(SecurityException se)
    {
      // if we can't read it, then ignore it. We still have the default pkg
    }

    // append the default package to the list and prepare it for use
    pkg_list = (pkg_list == null) ? DEFAULT_PKG : pkg_list + '|' + DEFAULT_PKG;

    boolean resolver_found = false;
    URNResolverService resolver = null;
    StringTokenizer strtok = new StringTokenizer(pkg_list, "|");

    // Loop through all of the available package names. Trim, splice and dice
    // each package looking for the appropriate class instance. If we find one,
    // then do a check to make sure that it implements the correct interface
    // (URNResolver) before committing to creating an instance of it. If
    // everything checks out OK, the create an instance and then exit from
    // the check.
    while(!resolver_found && strtok.hasMoreTokens())
    {
      String pkg_name = strtok.nextToken().trim();
      pkg_name = pkg_name.toLowerCase();
      StringBuffer buffer = new StringBuffer(pkg_name);

      // make up the class name to load
      buffer.append('.');
      buffer.append(type);
      buffer.append(".Resolver");

      String class_name = buffer.toString();

      try
      {
        Class reference = URNResolverService.class;
        Class rds_class = Class.forName(class_name);

        Class[] temp_interfaces = rds_class.getInterfaces();
        String  extension_type;
        boolean instance_found = false;

        // First check that the class implements the right interfaces
        // at this level.
        if(temp_interfaces.length != 0)
        {
          for(int i = 0; i < temp_interfaces.length; i++)
          {
            if(temp_interfaces[i].equals(reference))
            {
              // make an instance of it
              resolver = (URNResolverService)rds_class.newInstance();
              resolver.init();
              resolver.setResourceFactory(resource_factory);
              resolver_found = true;
            }
          }
        }

        // If we didn't find the right interface at the top level with the
        // last check then we recursively check the superclasses for the
        // same conditions.
        if(!resolver_found && (backgroundCheck(rds_class, reference)))
        {
          // make an instance of it
          resolver = (URNResolverService)rds_class.newInstance();
          resolver.init();
          resolver.setResourceFactory(resource_factory);
          resolver_found = true;
        }
      }
      catch(ClassNotFoundException cnfe)
      {
        // ignore and try the next one
      }
      catch(InstantiationException ie)
      {
        // ignore and try the next one
      }
      catch(IllegalAccessException iae)
      {
        // ignore and try the next one
      }
      catch(ConfigErrorException cfe)
      {
        resolver = null;
        System.err.println();
        System.err.println("Resolver class " + class_name +
                           " had an configuration error: ");
        System.err.println(cfe);
        System.err.println("Continuing.....");
      }
      catch(Throwable th)
      {
        // A last resort catch because something might have screwed
        // up in the class.forName() static initialisation
        resolver = null;
        System.err.println();
        System.err.println("There was an unexpected error loading " +
                           " the class " + class_name + ".");
        System.err.println("This is the error. We'll continue loading anyway");
        System.err.println(th);
        System.err.println("Continuing.....");
      }
    }

    // If we have a valid instance, register it with the hashmap and then
    // exit the method with the appropriate return value.
    if(resolver_found)
      rds_loaded.put(type, resolver);

    return resolver;
  }

  /**
   * Recursively check the super classes for the required reference interface
   * implementation.
   *
   * @param suspect The class to test for the reference
   * @param reference The class to check against.
   * @return true if this implements the required reference
   */
  private static boolean backgroundCheck(Class suspect, Class reference)
  {
    int j;
    Class parent = suspect.getSuperclass();

    // no point checking  java.lang.Object!
    if(parent == null)
      return false;

    Class[] temp_interfaces = parent.getInterfaces();

    if(temp_interfaces.length != 0)
    {
      for(j = 0; j < temp_interfaces.length; j++)
        if(temp_interfaces[j].equals(reference))
          return true;
    }

    // didn't find one so check the parent
    return(backgroundCheck(parent, reference));
  }

  /**
   * Sets an application's <CODE>URIResolverServiceFactory</CODE>. This method
   * can be called as many times as you like to set and un-set the factory.
   * Passing in a value of <CODE>null</CODE> clears the currently set factory.
   * <P>
   * The <CODE>URIResolverServiceFactory</CODE> instance is used to construct a
   * stream protocol handler from a protocol name.
   * <P>
   * If there is a security manager, this method first calls the security
   * manager's <CODE>checkSetFactory</CODE> method to ensure the operation is
   * allowed. This could result in a SecurityException.
   *
   * @param fac The desired factory
   * @exception SecurityException if the security manager exists and the
   *   set factory operation is not allowed.
   */
  static void setURIResolverServiceFactory(URIResolverServiceFactory fac)
  {
    // In the end, we may want to have a specific permission to check rather
    // using the default set factory as this allows many other loop holes into
    // the system.
    SecurityManager security = System.getSecurityManager();

    if (security != null)
      security.checkSetFactory();

    rds_factory = fac;
  }

  /**
   * Request the currently set factory.
   *
   * @return The currently set factory or <CODE>null</CODE> if none set
   */
  static URIResolverServiceFactory getURIResolverServiceFactory()
  {
    return rds_factory;
  }

  /**
   * Add a specific URN resolver to the system. If the resolver is already
   * registered, the request is ignored. Note that this is appended to the
   * list so registration order is important.
   *
   * @param resolver The resolver to add
   */
  static void addURNResolver(URNResolverService resolver)
  {
    if((resolver != null) && !local_resolvers.contains(resolver))
      local_resolvers.add(resolver);
  }

  /**
   * Add a specific URN resolver to the system. If the resolver is already
   * registered, the request is ignored. Note that this is appended to the
   * list so registration order is important.
   *
   * @param resolver The resolver to add
   */
  static void removeURNResolver(URNResolverService resolver)
  {
      local_resolvers.remove(resolver);
  }
}
