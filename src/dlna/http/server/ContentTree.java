package dlna.http.server;


import java.util.HashMap;

import org.omg.CosNaming._BindingIteratorImplBase;
import org.teleal.cling.support.model.WriteStatus;
import org.teleal.cling.support.model.container.Container;

public class ContentTree {
	
	public final static String ROOT_ID = "0";
	public final static String VIDEO_ID = "1";
	public final static String AUDIO_ID = "2";
	public final static String IMAGE_ID = "3";
	public final static String VIDEO_PREFIX = "video-item-";
	public final static String AUDIO_PREFIX = "audio-item-";
	public final static String IMAGE_PREFIX = "image-item-";
	
	private static HashMap<String, ContentNode> contentMap = new HashMap<String, ContentNode>();

//	private static ContentNode rootNode = createRootNode();//gpf

	public ContentTree() {};

	protected static ContentNode createRootNode() {
		// create root container
		System.out.println("----GPF----createRootNode");
		Container root = new Container();
		root.setId(ROOT_ID);
		root.setParentID("-1");
		root.setTitle("GPF MediaServer root directory");
		root.setCreator("GPF Media Server");
		root.setRestricted(true);
		root.setSearchable(true);
		root.setWriteStatus(WriteStatus.NOT_WRITABLE);
		root.setChildCount(0);
		ContentNode rootNode = new ContentNode(ROOT_ID, root);
		contentMap.put(ROOT_ID, rootNode);
		return rootNode;
	}
	
	public static ContentNode getRootNode() {
//		return rootNode;//gpf 
		return createRootNode();//gpf
	}
	
	public static ContentNode getNode(String id) {
		if( contentMap.containsKey(id)) {
			return contentMap.get(id);
		}
		return null;
	}
	
	public static boolean hasNode(String id) {
		return contentMap.containsKey(id);
	}
	
	public static void addNode(String ID, ContentNode Node) {
		contentMap.put(ID, Node);
	}
	/**
	 * GPF add 
	 * Clear all the content node in HashMap
	 */
	public static void deleteALL() {
		int size = contentMap.size();
		System.out.println("----GPF---contentMap Size = " + size);
		contentMap.clear();
	}
}
