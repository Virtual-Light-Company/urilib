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
 * A test harness class that loads a URN and then fetches the content of that
 * URN.
 * <P>
 * The first set of tests loads URNs with no extra directory qualification on
 * the element part. The tests are performed in the following order:
 * <UL>
 * <LI>text file
 * <LI>JPEG Image
 * <LI>PNG Image (using a content handler to do the work)
 * </UL>
 *
 * Following this, tests with the exact same files located in a directory,
 * and suitably nominated as such in the URN are performed. The following
 * table indicates the tests performed:
 * <P>
 * <TABLE BORDER=1>
 * <TR><TH>File Type  <TH>Simple URN <TH>Qualified URN</TR>
 * <TR><TD>text file  <TD>urn:vrml:umel:hello.txt <TD>urn:vrml:umel:/test/urn/hello.txt</TR>
 * <TR><TD>JPEG Image <TD>urn:vrml:umel:hello.jpg <TD>urn:vrml:umel:/test/urn/hello.jpg</TR>
 * <TR><TD>PNG Image  <TD>urn:vrml:umel:hello.png <TD>urn:vrml:umel:/test/urn/hello.png</TR>
 * </TABLE>
 *
 * These files may be located anywhere. It is suggested that for a full test of
 * URNs that the urn_bindings file be modified and run this test multiple times
 * to locate these files from both local directories and a web server.
 * <P>
 * No testing is performed with URLStreamHandlerFactories because no suitable
 * code has been found. Please contact the working group if you have feedback.
 *
 * @author  Justin Couch
 * @version 1.00 98/11/03
 */
public class URNTest
{
  private static final String TXT_SIMPLE_URN = "urn:vrml:umel:/hello.txt";
  private static final String JPG_SIMPLE_URN = "urn:vrml:umel:/hello.jpg";
  private static final String PNG_SIMPLE_URN = "urn:vrml:umel:/hello.png";

  // The following URNs are "long", meaning they specify a directory path leading
  // to the media element (whereas "simple" URNs above do not). The directory path
  // is relative to the resolved URN identifiers, meaning "urn:vrml:umel" is
  // first resolved to a specific location ("C:/urntests/media/", for example).
  // The remainder of the URN (i.e. "long/media/hello.jpg") specifies a directory
  // path and file relative to that location. The two are concatenated, and the
  // result is an abolute path to the element:
  //         C:/urntests/media/long/media/hello.jpg

  private static final String TXT_LONG_URN = "urn:vrml:umel:/long/xyz/hello.txt";
  private static final String JPG_LONG_URN = "urn:vrml:umel:/long/xyz/hello.jpg";
  private static final String PNG_LONG_URN = "urn:vrml:umel:/long/xyz/hello.png";

  private static final String CID_URN = "urn:cid:199606121851.1@mordred.gatech.edu";

  public static void checkGetURL(String urn)
  {
    System.out.println("LOADING URN:  " + urn);

    try
    {
      URN test_urn = new URN(urn);
      URL url = test_urn.getURL();

      if(url != null)
        System.out.println("TEST PASSED.  This test URN works:\n " + url.toExternalForm());
      else
        System.out.println("Could not resolve the URN");
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

  public static void checkGetURLList(String urn)
  {
    System.out.println("LOADING URN:  " + urn);

    try
    {
      URN test_urn = new URN(urn);
      URL[] url = test_urn.getURLList();

      if((url != null) && (url.length > 0))
      {
        System.out.println("TEST PASSED.  The URLs available are: ");
        for(int i = 0; i < url.length; i++)
          System.out.println("   " + i + ": " + url[i].toExternalForm());
      }
      else
        System.out.println("No URLs were found");
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

  public static void checkGetResource(String urn)
  {
    System.out.println("LOADING URN:  " + urn);

    try
    {
      URN test_urn = new URN(urn);
      ResourceConnection resc = test_urn.getResource();

      if(resc != null)
        System.out.println("TEST PASSED.  Connection available");
      else
        System.out.println("Could not resolve the URN");

      System.out.println("Content Type: " + resc.getContentType());

      if(resc.getContentType() != null)
        System.out.println("Contents: " + resc.getContent() + "....end");
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
/*
    System.out.print("Testing simple TEXT URN.....");
    checkGetURL(TXT_SIMPLE_URN);
    checkGetURLList(TXT_SIMPLE_URN);

    System.out.print("\nTesting simple JPEG URN.....");
    checkGetURL(JPG_SIMPLE_URN);

    System.out.print("\nTesting simple PNG URN......");
    checkGetURL(PNG_SIMPLE_URN);

    System.out.print("\nTesting expanded TEXT URN...");
    checkGetURL(TXT_LONG_URN);
    checkGetURLList(TXT_LONG_URN);

    System.out.print("\nTesting expanded JPEG URN...");
    checkGetURL(JPG_LONG_URN);

    System.out.print("\nTesting expanded PNG URN....");
    checkGetURL(PNG_LONG_URN);

    System.out.print("\nTesting CID URN....");
    checkGetURL(CID_URN);

    System.out.print("\nTesting resource....");
    checkGetResource("urn:vrml:umel:basic_text.txt");

    System.out.print("\nTesting resource....");
    checkGetResource("urn:vrml:umel:img_test.gif");

    System.out.print("\nTesting resource....");
    checkGetResource("urn:vrml:umel:img_test.png");

    System.out.print("\nTesting resource....");
    checkGetResource("urn:vrml:umel:img_test.tga");

    System.out.print("\nTesting resource....");
    checkGetResource("urn:vrml:umel:img_test.jpg");

*/
    String[] urn_list = {
//      "urn:x-ucb:brk00038835a.tiff",
      "urn:x-ucb:I0018235A.jpg ",
      "urn:x-ucb:brk00038836a_b.gif"
    };

    String[] comment_list = {
//      "resolves to http://sunsite.berkeley.edu/~jmcdonou/HOWE/brk00038835a.tiff",
      "resolves to http://sunsite.berkeley.edu/~jmcdonou/BREEN/figures/I0018235A.jpg",
      "resolves to http://sunsite.berkeley.edu/~jmcdonou/HOWE/brk00038836a_b.gif",
    };

    for(int i = 0; i < urn_list.length; i++)
    {
      System.out.println();
      System.out.println(comment_list[i]);

      checkGetURL(urn_list[i]);
    }

    System.out.print("\nTest exiting.");
    System.exit(0);
  }
}
