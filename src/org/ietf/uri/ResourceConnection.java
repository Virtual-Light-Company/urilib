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

// standard imports
import java.util.*;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;
import java.net.UnknownServiceException;

// Application specific imports
import org.ietf.uri.event.*;

/**
 * Representation of a connection to a net based resource.
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
public abstract class ResourceConnection
{
  private static final byte[] EMPTY_HEADER = new byte[0];

  /** The URI that this resource connection represents. */
  protected URI uri;

  /** flag indicating that this connection is made */
  protected boolean connected = false;

  /** The string representation of this class */
  private String name_string = null;

  /** The listener/multicaster for sending events */
  private ProgressListener progress_listener = null;

  /**
   * Create an instance of this connection.
   *
   * @param uri The URI to establish the connection to
   */
  protected ResourceConnection(URI uri)
  {
    this.uri = uri;
  }

  /**
   * Send a notification to the listeners that the connection has been
   * established.
   *
   * @param msg A message to accompany the event. May be <CODE>null</CODE>
   */
  protected void notifyConnectionEstablished(String msg)
  {
    if(progress_listener != null)
    {
      ProgressEvent evt =
        new ProgressEvent(this,
                          ProgressEvent.CONNECTION_ESTABLISHED,
                          msg,
                          -1);

      progress_listener.connectionEstablished(evt);
    }
  }

  /**
   * Send a notification to the listeners that handshaking is in progress
   * between the client and server or that header information is currently
   * being processed.
   * <P>
   * This method may be called multiple times in the life of a connection.
   * For example, for a HTTP redirect, this would be called once with the
   * initial connection, once when the redirect takes place and then again
   * when the new headers are downloaded. Make sure that the message
   * reflects this as the UI displaying this information may not be terribly
   * informative.
   *
   * @param msg A message to accompany the event. May be <CODE>null</CODE>
   */
  protected void notifyHandshakeInProgress(String msg)
  {
    if(progress_listener != null)
    {
      ProgressEvent evt =
        new ProgressEvent(this,
                          ProgressEvent.HANDSHAKE_IN_PROGRESS,
                          msg,
                          -1);

      progress_listener.handshakeInProgress(evt);
    }
  }

  /**
   * A package level method to send update events to the registered listener
   * of this connection. Obviously we don't want the user calling this directly
   * because of the problems. Normally, this should only be called by the
   * content handler implementation. No protection is given for multiple
   * simultaneous calls.
   *
   * @param evt The event to send out
   */
  void sendStartEvent(ProgressEvent evt)
  {
    if(progress_listener != null)
      progress_listener.downloadStarted(evt);
  }

  /**
   * A package level method to send update events to the registered listener
   * of this connection. Obviously we don't want the user calling this directly
   * because of the problems. Normally, this should only be called by the
   * content handler implementation. No protection is given for multiple
   * simultaneous calls.
   *
   * @param evt The event to send out
   */
  void sendUpdateEvent(ProgressEvent evt)
  {
    if(progress_listener != null)
      progress_listener.downloadUpdate(evt);
  }

  /**
   * A package level method to send update events to the registered listener
   * of this connection. Obviously we don't want the user calling this directly
   * because of the problems. Normally, this should only be called by the
   * content handler implementation. No protection is given for multiple
   * simultaneous calls.
   *
   * @param evt The event to send out
   */
  void sendErrorEvent(ProgressEvent evt)
  {
    if(progress_listener != null)
      progress_listener.downloadError(evt);
  }

  /**
   * A package level method to send update events to the registered listener
   * of this connection. Obviously we don't want the user calling this directly
   * because of the problems. Normally, this should only be called by the
   * content handler implementation. No protection is given for multiple
   * simultaneous calls.
   *
   * @param evt The event to send out
   */
  void sendFinishEvent(ProgressEvent evt)
  {
    if(progress_listener != null)
      progress_listener.downloadEnded(evt);
  }

  /**
   * Get the input stream for this. Throws an UnknownServiceExeception if
   * there is no stream available.
   *
   * @return The stream
   */
  public InputStream getInputStream()
    throws IOException
  {
    throw new UnknownServiceException("protocol doesn't support input");
  }

  /**
   * Get the output stream for this. Throws an UnknownServiceExeception if
   * there is no stream available.
   *
   * @return The stream
   */
  public OutputStream getOutputStream()
    throws IOException
  {
    throw new UnknownServiceException("protocol doesn't support output");
  }

  /**
   * Get the content held by this resource connection. The content type
   * will be the content of the currently available object. For example, it
   * may be possible that a stream will be able to produce more than one
   * object during its lifetime.
   *
   * @return The object read from the stream
   * @exception IOException An error while reading the stream
   */
  public Object getContent()
    throws IOException
  {
    InputStream stream = getInputStream();

    String content_type = getContentType();
    ContentHandler handler = getContentHandler(content_type);

    Object content = null;

    if(handler != null)
      content = handler.getContent(this);
    return content;
  }

  /**
   * Get the content type of the resource that this stream points to.
   * Returns a standard MIME type string. If the content type is not known then
   * <CODE>unknown/unknown</CODE> is returned (the default implementation).
   * Thie method should be overridden by all implementations.
   *
   * @return The content type of this resource
   */
  public String getContentType()
  {
    return "unknown/unknown";
  }

  /**
   * Connect to the named resource if not already connected.
   *
   * @exception An error occurred during the connection process
   */
  public abstract void connect()
    throws IOException;

  /**
   * Get the length of the content that is to follow on the stream. If the
   * length is unknown then -1 is returned. This method returns -1 by default
   * and should be overridden by implementing classes.
   *
   * @return The length of the content in bytes or -1
   */
  public int getContentLength()
  {
    return -1;
  }

  /**
   * Get the encoding type of the content. Allows for dealing with
   * multi-lingual content. If it cannot be determined then <CODE>null</CODE>
   * is returned (which is the default implementation).
   *
   * @return The content type
   */
  public String getContentEncoding()
  {
    return null;
  }

  /**
   * Get the URI that this connection was established for
   */
  public URI getURI()
  {
    return uri;
  }

  /**
   * Get the nth header field from this connection. If the key name is not
   * understood or does not have a value, null will be returned. This is the
   * default behaviour and should be overrridden.
   *
   * @param n The index of the field to fetch
   * @return The header field value formated as a string
   */
  public String getHeaderField(int n)
  {
    return null;
  }

  /**
   * Get the named header field from this connection. If the key name is not
   * understood or does not have a value, null will be returned. This is the
   * default behaviour and should be overrridden.
   *
   * @param n The index of the field to fetch
   * @return The header field value formated as a string
   */
  public String getHeaderField(String name)
  {
    return null;
  }

  /**
   * Get the nth header field from this connection in its raw form as a
   * series of bytes. If the key is not known then the value returned is a
   * zero length array. This is the default behaviour and should be overridden
   * where necessary.
   *
   * @param n The index of the field to fetch
   * @return The header field value formated as a string
   */
  public byte[] getRawHeaderField(int n)
  {
    return EMPTY_HEADER;
  }

  /**
   * Get the named header field from this connection in its raw form as a
   * series of bytes. If the key is not known then the value returned is a
   * zero length array. This is the default behaviour and should be overridden
   * where necessary.
   *
   * @param n The index of the field to fetch
   * @return The header field value formated as a string
   */
  public byte[] getRawHeaderField(String name)
  {
    return EMPTY_HEADER;
  }

  /**
   * Get the key used by the nth header field from this connection. If the key
   * is not known or index is invalid, null is returned. The default
   * implementation always returns null and should be overridden.
   *
   * @param n The index of the key to fetch
   * @return The header field key value formated as a string
   */
  public String getHeaderFieldKey(int n)
  {
    return null;
  }

  /**
   * Get the time that this object was last modified. This information may
   * come from protocol headers or any other means
   * <P>
   * The time is in The result is the number of seconds since January 1, 1970
   * GMT. The default implementation returns 0.
   *
   * @return The time or 0 if unknown
   */
  public long getLastModified()
  {
    return 0;
  }

  /**
   * Insert a content type into the default mappings. This is to ensure that
   * a content handler is correctly determined for some stream types where
   * it is not provided by the underlying resource stream.
   *
   * @param mimetype The mime type to add
   * @param ext The file extension used for this mapping
   */
  protected static void addContentTypeToDefaultMap(String mimetype, String ext)
  {
    ResourceManager.addContentTypeToDefaultMap(mimetype, ext);
  }

  /**
   * Guess the content type from the filename. Uses the currently loaded
   * filename maps for deciding what to return. The filename only needs to
   * be the raw filename. It doesn not need to be fully qualified. The
   * extension is determined by starting at the end of the string and looking
   * backwards for the first instance of '.'. All of the characters in
   * between are used as the extension. If no type can be determined,
   * <CODE>null</CODE> is returned.
   *
   * @param filename The name of the fileA
   * @return The name of the content type or <CODE>null</CODE>
   */
  public static String findContentType(String filename)
  {
    return ResourceManager.getMIMEType(filename);
  }

  /**
   * Take an educated stab at the extension a file should have for the given
   * MIME type. Although there are many possible extensions for a given MIME
   * type, we take whatever the filename maps tell us. They are responsible for
   * providing the correct mapping. Assumes that a valid mime type is given.
   * If the value is <CODE>null</CODE> then the results are indeterminate,
   * while an unknown or invalid type specification will return
   * <CODE>null</CODE>.
   *
   * @param mimetype The mime type to compare against.
   */
  public static String findFileExtension(String mimetype)
  {
    return ResourceManager.getFileExtension(mimetype);
  }
  
  /**
   * Fetch the content handler for the given mime type. First check the factory
   * (if set), then the list of provided packages. It then checks the default
   * package. If none can be found then it attempts to try the default packages
   * that come as part of the standard java setups. It places a wrapper around
   * these to make it conform with our local stuff.
   *
   * @param contentType The desired MIMType
   * @return The appropriate content handler or null if none can be found
   */
  protected ContentHandler getContentHandler(String contentType)
  {
    return ResourceManager.getContentHandler(contentType);
  }

  /**
   * Add a progress listener to this resource connection. This will be given
   * events as you read content from the connection.
   *
   * @param l The listener to be added.
   */
  public void addProgressListener(ProgressListener l)
  {
    progress_listener = ProgressListenerMulticaster.add(progress_listener, l);
  }

  /**
   * Remove a progress listener from this resource connection. This will be
   * will no longer receive events.
   *
   * @param l The listener to be added.
   */
  public void removeProgressListener(ProgressListener l)
  {
    progress_listener =
      ProgressListenerMulticaster.remove(progress_listener, l);
  }

  /**
   * Print out a string representation of the class. Uses the form:
   * <PRE>
   *   Resource Connection: [ class.getName() ]
   * </PRE>
   *
   * @return A string representation of the class
   */
  public String toString()
  {
    if(name_string == null)
    {
      StringBuffer buffer = new StringBuffer("Resource Connection: [ ");
      buffer.append(getClass().getName());
      buffer.append(" ]");

      name_string = buffer.toString();
    }

    return name_string;
  }
}
