import java.net.URL;
import java.io.InputStream;
import java.util.Enumeration;
import org.ietf.uri.RDSManager;
import org.ietf.uri.URIResolverService;

public class BasicTest
{
    public static void main(String[] argv)
    {
        ClassLoader cl = ClassLoader.getSystemClassLoader();
        
        System.out.println("cl.getResource()");

        URL url = cl.getResource("urn.conf");

        if(url == null)
            System.out.println("Couldn't find urn.conf");
        else
            System.out.println("URL is " + url.toExternalForm());

        // Fetching resource
        System.out.println("cl.getSystemResource())");
        url = ClassLoader.getSystemResource("urn.conf");

        if(url == null)
            System.out.println("Couldn't find urn.conf");
        else
            System.out.println("URL is " + url.toExternalForm());

        System.out.println("system resource as stream");
        InputStream is = ClassLoader.getSystemResourceAsStream("urn.conf");
         
        if(is == null)
            System.out.println("Couldn't find urn.conf stream");

        // RDSManager.getFirstResolver("x-rbuzz", URIResolverService.I2L);
        RDSManager.getAllResolvers("x-rbuzz", URIResolverService.I2L);

        System.out.println("All resolvers are:");
        Enumeration en = RDSManager.listResolverTypesOrder();
        
        for(int i = 0; en.hasMoreElements(); i++)
          System.out.println("  " + i + ": " + en.nextElement());
    }
}
