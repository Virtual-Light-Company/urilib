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

import org.ietf.uri.*;

import java.net.URLConnection;
import java.io.IOException;

/**
 * A test harness class that loads a URL and then fetches the content of that
 * URL.
 * <P>
 * Mainly used to test new content types and resource connection types.
 * @author  Justin Couch
 * @version 1.00 98/11/03
 */
public class URLTest
{
  private static final String TXT_URL = "/~couch/hello.txt";
  private static final String JPG_URL = "/~couch/hello.jpg";
  private static final String PNG_URL = "/~couch/hello.png";
  private static final String JAR_URL = "/~couch/test.jar";

  private static final String HTTP_SERVER = "http://www.ccis.adisys.com.au";
  private static final String FILE_DIR = "file:///H:/public_html";

  public static void checkGetResource(String url)
  {
    System.out.println("LOADING URL:  " + url);

    try
    {
      URL test_url = new URL(url);
      ResourceConnection resc = test_url.getResource();

      if(resc != null)
        System.out.println("TEST PASSED.  Connection available");
      else
      {
        System.out.println("Could not resolve the resource");
        return;
      }

      System.out.println("Connection type is\n  " + resc);
      String content_type = resc.getContentType();
      System.out.println("Content Type: " + content_type);
      if(resc instanceof HttpResourceConnection)
      {
        HttpResourceConnection http_res = (HttpResourceConnection)resc;
        System.out.println("Response code is " + http_res.getResponseCode());
      }

      if(content_type != null)
      {
        if(content_type.equals("unknown/unknown"))
          System.out.println("Contents unknown type ");
        else
          System.out.println("Contents: " + resc.getContent() + "....end");
      }
    }
    catch(MalformedURNException mue)
    {
      System.out.println(mue);
    }
    catch(UnsupportedServiceException use)
    {
      // Doh!
      System.out.println("Service is not supported : " + use);
    }
    catch(IOException ioe)
    {
      System.out.println("Some io error " + ioe);
    }
  }

  public static void main(String[] args)
  {
    // JAR handling strings
    String[] jar_urls = {
      "jar:file:///h:/public_html/test.jar!/",
      "jar:file:///h:/public_html/test.jar!/hello.txt",
      "jar:file:///h:/public_html/test.jar!/hello.jpg"
    };

    int i;

    URI.setFileNameMap(new TestFileMap());

//    System.out.print("\nTesting resource....");
//    checkGetResource(HTTP_SERVER + TXT_URL);

//    System.out.print("\nTesting resource....");
//    checkGetResource(FILE_DIR + "/test.jar");

    for(i = 0; i < jar_urls.length; i++)
    {
      System.out.print("\nTesting resource....");
      checkGetResource(jar_urls[i]);
    }

//    System.out.print("\nTesting resource....");
//    checkGetResource(HTTP_SERVER + JPG_URL);

    System.out.print("\nTest exiting.");
    System.exit(0);
  }
}
