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

package org.ietf.uri;

// Standard imports
import java.util.jar.*;

import java.io.IOException;
import java.security.cert.Certificate;
import java.net.MalformedURLException;

/**
 * Representation of a JAR resource.
 * <P>
 *
 * Presents a standardised interface to JAR files, regardless of their
 * location. The methods provided here model those in the
 * <CODE>java.net</CODE> version with one difference. The
 * <CODE>getJarFile</CODE> method is protected rather than public. If you need
 * access to the entire JAR file, then that should be by making the appropriate
 * URL with no entry name and accessing through the <CODE>getContent</CODE>
 * method. For this, the content type shall be <CODE>x-java/jar</CODE>
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
public abstract class JarResourceConnection extends ResourceConnection
{
  /**
   * The content type that is returned to represent a JAR file -
   * <CODE>x-java/jar</CODE>
   */
  public static final String JAR_CONTENT_TYPE = "x-java/jar";

  /** The file extension normally used on JAR files.: <CODE>.jar</CODE> */
  public static final String JAR_FILE_EXTENSION = "jar";

  /**
   * The connection to the JAR file if it has been initiated. This field is
   * not initialised by the base class. It should be set by the derived class
   * after the call to connect.
   */
  protected ResourceConnection jarFileResource = null;

  /** The URI to the JAR file itself */
  protected URI jarFileURI = null;

  /**
   * The name of the extry to fetch. If the connection is only to a JAR file
   * with no entry attributes, this will remain null
   */
  protected String jarEntryName = null;

  /** The file that we are making the queries on */
  protected JarFile jarFile = null;

  /**
   * The entry that we fetched. If there was no entry specified in the URL
   * then this will be null.
   */
  protected JarEntry jarEntry = null;

  /**
   * Static initializer to make sure that we have the jar content type
   * registered for when we need it.
   */
  static
  {
    addContentTypeToDefaultMap(JAR_CONTENT_TYPE, JAR_FILE_EXTENSION);
  }

  /**
   * Create an instance of this connection to the jar file at the nominated
   * location. This location itself may be another URL. If there is no JAR
   * entry to fetch then the name should be <CODE>null</CODE>.
   *
   * @param jarLocation The URI to establish the connection to
   * @param jarEntry The name of the entry to fetch
   * @exception MalformedURLException We stuffed up something in the filename
   */
  protected JarResourceConnection(URI jarLocation, String jarEntryPath)
    throws MalformedURLException
  {
    super(new URL("jar:" + jarLocation.toExternalForm() + "!/" + jarEntryPath));

    jarFileURI = jarLocation;

    // protection against zero length strings causing weirdness
    if((jarEntryPath != null) && (jarEntryPath.length() != 0))
      jarEntryName = jarEntryPath;
  }

  /**
   * Request the derived class to provide us with the parent JarFile that
   * this resource references. When this is called, it should automatically
   * connect to the source and retrieve the file. Note that caching and other
   * techniques are permitted. The underlying methods assume that this file
   * is accessible and makes no checks for connection state.
   *
   * @return The jar file pointed to by the location
   * @exception IOException There was an error reading the file
   */
  protected abstract JarFile getJarFile()
    throws IOException;


  /**
   * Return the list of attributes for the JAR entry. If the resource points
   * to the JAR file only, then this method returns <CODE>null</CODE>.
   *
   * @return The list of attributes
   * @exception IOException There was an error reading the file
   */
  public Attributes getAttributes()
    throws IOException
  {
    if(jarEntryName == null)
      return null;

    if(jarEntry == null)
      getJarEntry();

    Attributes ret_vals = null;

    if(jarEntry != null)
      ret_vals = jarEntry.getAttributes();

    return ret_vals;
  }

  /**
   * Get the certificates that describe this entry in the JAR file. If the
   * resource points to a Jar file, then this method returns <CODE>null</CODE>.
   *
   * @return The list of certificates
   * @exception IOException There was an error reading the file
   */
  public Certificate[] getCertificates()
    throws IOException
  {
    if(jarEntryName == null)
      return null;

    if(jarEntry == null)
      getJarEntry();

    Certificate[] ret_vals = null;

    if(jarEntry != null)
      ret_vals = jarEntry.getCertificates();

    return ret_vals;
  }

  /**
   * Get the name of the entry that is being fetched from this connection. If
   * the connection points to the file as a whole then this returns
   * <CODE>null</CODE>
   *
   * @return The name of the entry, if any
   */
  public String getEntryName()
  {
    return jarEntryName;
  }

  /**
   * Fetch the JAR entry as nominated by the resource. If the resource does not
   * name an entry, then it returns <CODE>null</CODE>
   *
   * @return A reference to the JAREntry or </CODE>null</CODE>
   * @exception IOException There was an error reading the file
   */
  public JarEntry getJarEntry()
    throws IOException
  {
    if(jarEntryName == null)
      return null;

    if(jarEntry == null)
    {
      if(jarFile == null)
        jarFile = getJarFile();

      jarEntry = jarFile.getJarEntry(jarEntryName);
    }

    return jarEntry;
  }

  /**
   * Get the URL to the actual JAR file. Note that <CODE>jar:</CODE> URLs are
   * not supposed to have anything except a URL as the location, we have
   * allowed the code to use URNs as well.
   */
  public URI getJarFileURI()
  {
    return jarFileURI;
  }

  /**
   * Get the main attributes of the Jar File. These are the attributes that
   * are contained in the manifest entry.
   *
   * @return a reference to the attributes of the manifest
   * @exception IOException There was an error reading the file
   */
  public Attributes getMainAttributes()
    throws IOException
  {
    Manifest man = getManifest();

    Attributes ret_val = null;

    if(man != null)  // always possible if the JAR file doesn't have one
      ret_val = man.getMainAttributes();

    return ret_val;
  }

  /**
   * Get the manifest from the JAR file.
   *
   * @return A reference to the manifest.
   * @exception IOException There was an error reading the file
   */
  public Manifest getManifest()
    throws IOException
  {
    if(jarFile == null)
      jarFile = getJarFile();

    return jarFile.getManifest();
  }
}
