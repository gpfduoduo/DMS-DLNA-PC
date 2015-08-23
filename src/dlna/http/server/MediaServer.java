package dlna.http.server;

import java.io.IOException;
import java.net.InetAddress;
import org.teleal.cling.binding.annotations.AnnotationLocalServiceBinder;
import org.teleal.cling.model.DefaultServiceManager;
import org.teleal.cling.model.ValidationException;
import org.teleal.cling.model.meta.DeviceDetails;
import org.teleal.cling.model.meta.DeviceIdentity;
import org.teleal.cling.model.meta.LocalDevice;
import org.teleal.cling.model.meta.LocalService;
import org.teleal.cling.model.meta.ManufacturerDetails;
import org.teleal.cling.model.meta.ModelDetails;
import org.teleal.cling.model.types.DeviceType;
import org.teleal.cling.model.types.UDADeviceType;
import org.teleal.cling.model.types.UDN;



public class MediaServer {

	private UDN udn = UDN.uniqueSystemIdentifier("MediaServer");
	private LocalDevice localDevice; 

	private final static String deviceType = "MediaServer";
	private final static int version = 1;
	private final static String LOGTAG = "MediaServer";
	private final static int port = 8192;
	private static InetAddress localAddress;

	public MediaServer(InetAddress localAddress) throws ValidationException {
		System.out.println("-----GPF----MediaServer Construction function");
		DeviceType type = new UDADeviceType(deviceType, version);
		DeviceDetails details = new DeviceDetails("DLNA DMS", new ManufacturerDetails("DLNA"),
				new ModelDetails("DLNA", "MediaServer for DLNA", "v1"));
		LocalService service = new AnnotationLocalServiceBinder().read(ContentDirectoryService.class);
		service.setManager(new DefaultServiceManager<ContentDirectoryService>(service, 
				ContentDirectoryService.class));
		localDevice = new LocalDevice(new DeviceIdentity(udn), type, details, service);
		this.localAddress = localAddress;

		System.out.println("MediaServer device created: ");
		System.out.println("---GPF----friendly name: " + details.getFriendlyName());
		System.out.println("---GPF----manufacturer: "
				+ details.getManufacturerDetails().getManufacturer());
		System.out.println("---GPF----model: " + details.getModelDetails().getModelName());
		
		//start http server
		try {
			new HttpServer(port);
		}
		catch (IOException ioe )
		{
			System.err.println( "Couldn't start server:\n" + ioe );
			System.exit( -1 );
		}

	}

	public LocalDevice getDevice() {
		return localDevice;
	}

	public String getAddress() {
		return localAddress.getHostAddress() + ":" + port;
	}

}