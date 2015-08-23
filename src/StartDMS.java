import java.io.File;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URLEncoder;
import java.text.FieldPosition;
import java.util.Enumeration;
import java.util.Random;

import net.contentobjects.jnotify.JNotify;
import net.contentobjects.jnotify.JNotifyException;
import net.contentobjects.jnotify.JNotifyListener;

import org.teleal.cling.UpnpService;
import org.teleal.cling.UpnpServiceImpl;
import org.teleal.cling.model.ValidationException;
import org.teleal.cling.support.model.DIDLObject;
import org.teleal.cling.support.model.PersonWithRole;
import org.teleal.cling.support.model.Res;
import org.teleal.cling.support.model.WriteStatus;
import org.teleal.cling.support.model.container.Container;
import org.teleal.cling.support.model.item.ImageItem;
import org.teleal.cling.support.model.item.MusicTrack;
import org.teleal.cling.support.model.item.VideoItem;
import org.teleal.common.util.MimeType;
import dlna.http.server.MediaServer;
import dlna.http.server.ContentNode;
import dlna.http.server.ContentTree;


public class StartDMS {

	private MediaServer mediaServer = null;
	private UpnpService upnpService = null;
	//自定义媒体路径，写死 
	//pc下的写法
	private String localMusicPath = "E:\\Entertainment\\audio";
	private String localVideoPath = "E:\\Entertainment\\video";
	private String localPhotoPath = "E:\\Entertainment\\photo";
	
	Random random = new Random();
	
	//移植到linux（ubuntu）的写法，此程序已经验证
	/*private String localMusicPath = "/data/dlna/Music";
	private String localVideoPath = "/data/dlna/Video";
	private String localPhotoPath = "/data/dlna/Photo";*/

	public static void main(String args[]){
		System.out.println("----GPF---main function");
		StartDMS dmsInstanceDms = new StartDMS();
		dmsInstanceDms.start();

	}

	public void start() {
		upnpService = new UpnpServiceImpl();
		if (mediaServer == null) {
			try {
				mediaServer = new MediaServer(getLocalIpAddress());
				System.out.println("---GPF----local Address:" + getLocalIpAddress().getHostAddress());
			} catch (ValidationException e) {
				// TODO Auto-generated catch block
				System.out.println("----GPF----MediaServer initialize fail");
				e.printStackTrace();
			}

			upnpService.getRegistry().addDevice(mediaServer.getDevice());

			prepareMediaServer();

			/**
			 *  建立一个线程实时监测文件夹内文件是否变化
			 *  2013-8-8 测试可以，依赖的jar包：jnotify-0.94.jar
			 */
			new Thread() {
				public void run() {
					//System.setProperty("java.library.path", "F:\\android-player\\DMS-DLNA-PC\\libs");
					WatchFile();
				}
			}.start();

			/**
			 * 停止UPnP文档
			 */
			/*try {
				Thread.sleep(10 * 1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			upnpService.getRegistry().removeAllLocalDevices();
			*/
		}
	}

	private void WatchFile() {
		String path = localPhotoPath;
		System.out.println("----GPF----WatchFile function path=" + path);
		int mask = JNotify.FILE_CREATED  | 
				JNotify.FILE_DELETED  | 
				JNotify.FILE_MODIFIED | 
				JNotify.FILE_RENAMED;
		boolean watchSubtree = true;
		int watchID = 0;
		try {
			watchID = JNotify.addWatch(path, mask, watchSubtree, new Listener());
		} catch (JNotifyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			Thread.sleep(1000 * 1000);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		// to remove watch the watch
		boolean res = false;
		try {
			res = JNotify.removeWatch(watchID);
		} catch (JNotifyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (!res) {
			// invalid watch ID specified.
		}

	}

	class Listener implements JNotifyListener {
		public void fileRenamed(int wd, String rootPath, String oldName,
				String newName) {
			System.out.println("----GPF----renamed " + rootPath + " : " + oldName + " -> " + newName);
			RePrepareMediaServer();
		}
		public void fileModified(int wd, String rootPath, String name) {
			System.out.println("----GPF----modified " + rootPath + " : " + name);
			RePrepareMediaServer();
		}
		public void fileDeleted(int wd, String rootPath, String name) {
			System.out.println("----GPF----deleted " + rootPath + " : " + name);
			RePrepareMediaServer();
		}
		public void fileCreated(int wd, String rootPath, String name) {
			System.out.println("----GPF----created " + rootPath + " : " + name);
			RePrepareMediaServer();
		}
	}
	
//	final ContentNode rootNode = ContentTree.getRootNode();
	//当媒体内容有变化时，重新建立-简单的修改，理论上修改了那些内容就变化那些内容
	public void RePrepareMediaServer() {
		// TODO Auto-generated method stub
		ContentTree.deleteALL();
		prepareMediaServer();
	}

	private  void prepareMediaServer() {
		// TODO Auto-generated method stub
		final ContentNode rootNode = ContentTree.getRootNode();
		new Thread() {
			public void run() {
				Container videoContainer = new Container();
				videoContainer.setClazz(new DIDLObject.Class("object.container"));
				videoContainer.setId(ContentTree.VIDEO_ID);
				videoContainer.setParentID(ContentTree.ROOT_ID);
				videoContainer.setTitle("Videos");
				videoContainer.setRestricted(true);
				videoContainer.setWriteStatus(WriteStatus.NOT_WRITABLE);
				videoContainer.setChildCount(0);

				rootNode.getContainer().addContainer(videoContainer);
				rootNode.getContainer().setChildCount(
						rootNode.getContainer().getChildCount() + 1);
				ContentTree.addNode(ContentTree.VIDEO_ID, new ContentNode(
						ContentTree.VIDEO_ID, videoContainer));
				//视频文件
				File videoPath = new File(localVideoPath);
				if (videoPath.isDirectory() == false) {
					System.out.println("----GPF----视频路径出错");
					return;
				}
				String[] videoList = videoPath.list();
				System.out.println("-----GPF---video file count:" + videoList.length);
				//该目录下不存在目录的嵌套
				for (int i = 0; i < videoList.length; i++) {
					String filename = localVideoPath +File.separator + videoList[i];
					System.out.println("-----GPF-----视频文件路径：" + filename);
					String title = videoList[i];
					String filePath = filename;
					long size = new File(filename).length();
					String id = random.nextInt() + title;

					String creator = null;
					String mimeType = null;
					String index = filename.substring(filename.lastIndexOf("."));
					System.out.println("----视频文件的后缀名：" + index);
					if (index.equals(".mp4")) {
						mimeType = "video/mp4";	
						System.out.println("----GPF---mp4视频格式");
					} else if (index.equals(".mkv")) {
						mimeType = "video/x-mkv";
						System.out.println("----GPF---mkv视频格式");
					} else if (index.equals(".rmvb")) {
						mimeType = "video/vnd.rn-realvideo";
						System.out.println("----GPF---rmvb视频格式");
					} else if (index.equals(".ts")) {
						mimeType = "video/MP2T";
						System.out.println("----GPF---ts视频格式");
					} else if (index.equals(".flv")) {
						mimeType = "video/x-flv";
						System.out.println("----GPF---flv视频格式");
					} else if (index.equals(".mov")) {
						mimeType = "video/quicktime";
						System.out.println("----GPF---mov视频格式");
					} else if (index.equals(".wmv")) {
						mimeType = "video/x-ms-wmv wmv";
						System.out.println("----GPF---wmv视频格式");
					} else if (index.equals(".avi")) {
						mimeType = "video/x-msvideo";
						System.out.println("----GPF---avi视频格式");
					} else if (index.equals(".mpg") || index.equals(".mpeg")) {
						mimeType = "video/mpeg";
						System.out.println("----GPF---mpg视频格式");
					} else {
						mimeType = "video/unknown";
						System.out.println("----GPF---未知视频格式");
					}

					Res res = new Res(new MimeType(mimeType.substring(0,
							mimeType.indexOf('/')), mimeType.substring(mimeType
									.indexOf('/') + 1)), size, "http://"
											+ mediaServer.getAddress() + "/" + URLEncoder.encode(id));

					System.out.println("----GPF---视频文件URL地址：" + "http://" + mediaServer.getAddress() + "/" + URLEncoder.encode(id));

					VideoItem videoItem = new VideoItem(id, ContentTree.VIDEO_ID, title, creator, res);
					videoContainer.addItem(videoItem);
					videoContainer.setChildCount(videoContainer.getChildCount() + 1);
					ContentTree.addNode(id, new ContentNode(id, videoItem, filePath));
					System.out.println("----GPF---added vodeo item " + title + " from " + filePath);
				}
			}
		}.start();


		new Thread() {
			public void run() {
				// 音频目录
				Container audioContainer = new Container(ContentTree.AUDIO_ID,
						ContentTree.ROOT_ID, "Audios", "GPF MediaServer",
						new DIDLObject.Class("object.container"), 0);
				audioContainer.setRestricted(true);
				audioContainer.setWriteStatus(WriteStatus.NOT_WRITABLE);
				rootNode.getContainer().addContainer(audioContainer);
				rootNode.getContainer().setChildCount(rootNode.getContainer().getChildCount() + 1);
				ContentTree.addNode(ContentTree.AUDIO_ID, new ContentNode(ContentTree.AUDIO_ID, audioContainer));

				//音频文件
				File musicFile = new File(localMusicPath);
				if (musicFile.isDirectory() == false) {
					System.out.println("----GPF----音乐路径出错");
					return;
				}

				String[] musicList = musicFile.list();
				System.out.println("-----GPF---music file count:" + musicList.length);
				//该目录下不存在目录的嵌套
				for (int i = 0; i < musicList.length; i++) {
					String filename = localMusicPath +File.separator + musicList[i];
					String title = musicList[i];
					addMusicFile(audioContainer, filename, title);
				}
			}
		}.start();



		new Thread() {
			public void run() {
				//图像目录
				Container imageContainer = new Container(ContentTree.IMAGE_ID,
						ContentTree.ROOT_ID, "Images", "GNaP MediaServer",
						new DIDLObject.Class("object.container"), 0);
				imageContainer.setRestricted(true);
				imageContainer.setWriteStatus(WriteStatus.NOT_WRITABLE);
				rootNode.getContainer().addContainer(imageContainer);
				rootNode.getContainer().setChildCount(
						rootNode.getContainer().getChildCount() + 1);
				ContentTree.addNode(ContentTree.IMAGE_ID, new ContentNode(ContentTree.IMAGE_ID, imageContainer));
				//图像文件
				File photoFile = new File(localPhotoPath);
				if (photoFile.isDirectory() == false) {
					System.out.println("----GPF----图像路径出错");
					return;
				}

				String[] photoList = photoFile.list();
				System.out.println("-----GPF---music file count:" + photoList.length);
				for (int i = 0; i < photoList.length; i++) {
					String filePath = localPhotoPath +File.separator + photoList[i];
					String titlename = photoList[i];
					System.out.println("----GPF----filePath=" + filePath);
					addImageFile(imageContainer, filePath, titlename);
				}
			}
		}.start();

	} 

	public void addMusicFile(Container audioContainer, String filePath, String title) {
		// TODO Auto-generated method stub
		File file = new File(filePath);
		if (file.isDirectory() == false) {
			String creator = null;
			String album = null;
			String id = random.nextInt() + title;
			String mimeType = null;
			String index = filePath.substring(filePath.lastIndexOf("."));
			System.out.println("----音频文件的后缀名：" + index);
			if (index.endsWith(".mp3")) {
				mimeType = "audio/mpeg";
				System.out.println("----GPF---mp3音频格式");
			} else if (index.equals(".wma")) {
				mimeType = "audio/x-ms-wma";
				System.out.println("----GPF---wma音频格式");
			} else {
				System.out.println("----GPF---未知音频格式");
			}

			long size = new File(filePath).length();
			System.out.println("---GPF---music info: title:" + title + ";createor:" + creator + ";filepath:" + filePath
					+ ";mimeType:" + mimeType +";size:" +size + ";id:" + id + ";album:" + album);

			Res res = new Res(new MimeType(mimeType.substring(0,
					mimeType.indexOf('/')), mimeType.substring(mimeType
							.indexOf('/') + 1)), size, "http://"
									+ mediaServer.getAddress() + "/" + URLEncoder.encode(id));

			System.out.println("音乐文件URL地址：" + "http://" + mediaServer.getAddress() + "/" + id);

			MusicTrack musicTrack = new MusicTrack(id, audioContainer.getParentID(), title, creator, album,
					new PersonWithRole(creator, "Performer"), res);
			audioContainer.addItem(musicTrack);
			audioContainer.setChildCount(audioContainer.getChildCount() + 1);
			ContentTree.addNode(id, new ContentNode(id, musicTrack,filePath));
		} else {
			Container subaudioContainer = new Container(title,
					audioContainer.getParentID(), title, "GPF MediaServer",
					new DIDLObject.Class("object.container"), 0);
			subaudioContainer.setRestricted(true);
			subaudioContainer.setWriteStatus(WriteStatus.NOT_WRITABLE);
			audioContainer.addContainer(subaudioContainer);
			audioContainer.setChildCount(audioContainer.getChildCount() + 1);
			ContentTree.addNode(subaudioContainer.getId(), new ContentNode(
					subaudioContainer.getId(), subaudioContainer));
			//嵌套
			String filePath2 = filePath;
			System.out.println("---GPF---filePath2=" + filePath2);
			File file2 = new File(filePath2);
			String[] file2List = file2.list();
			System.out.println("---GPF----file2List.length=" + file2List.length);
			for (int i = 0; i < file2List.length; i++) {
				String subfilePath = filePath2 +File.separator + file2List[i];
				String titlename = file2List[i];
				addMusicFile(subaudioContainer, subfilePath, titlename);
			}
		}
	}

	public void addImageFile(Container imageContainer, String filePath, String title) {
		File file = new File(filePath);
		if (file.isDirectory() == false) {
			String id = random.nextInt() + title;
			String creator = "unknown";
			System.out.println("---GPF---photo file path=" + filePath);
			long size = new File(filePath).length();

			String mimeType =null;
			String index = filePath.substring(filePath.lastIndexOf("."));
			System.out.println("----图像文件的后缀名：" + index);
			if (index.equals(".jpg")) {
				mimeType = "image/jpeg";
				System.out.println("---GPF----jpg图像格式");
			} else if (index.equals(".bmp")) {
				mimeType = "image/bmp";
				System.out.println("---GPF----bmp图像格式");
			} else if (index.equals(".png")) {
				mimeType = "image/png";
				System.out.println("---GPF----png图像格式");
			} else if (index.equals(".tiff") || index.equals("tif")) {
				mimeType = "image/tiff";
				System.out.println("---GPF----tif图像格式");
			} else if (index.equals(".gif")) {
				mimeType = "image/gif";
				System.out.println("---GPF----gif图像格式");
			} else {
				mimeType = "image/unknown";
				System.out.println("----GPF---未知图像格式");
			}

			Res res = new Res(new MimeType(mimeType.substring(0,
					mimeType.indexOf('/')), mimeType.substring(mimeType
							.indexOf('/') + 1)), size, "http://"
									+ mediaServer.getAddress() + "/" + URLEncoder.encode(id));

			ImageItem imageItem = new ImageItem(id, imageContainer.getParentID(),title, creator, res);
			imageContainer.addItem(imageItem);
			imageContainer.setChildCount(imageContainer.getChildCount() + 1);
			ContentTree.addNode(id, new ContentNode(id, imageItem, filePath));
			System.out.println("----GPF---added photo item " + title + " from " + filePath);
		} else {
			//如果是目录
			System.out.println("----GPF----是目录");
			Container subimageContainer = new Container(title,
					imageContainer.getParentID(), title, "GNaP MediaServer",
					new DIDLObject.Class("object.container"), 0);
			subimageContainer.setRestricted(true);
			subimageContainer.setWriteStatus(WriteStatus.NOT_WRITABLE);
			imageContainer.addContainer(subimageContainer);
			imageContainer.setChildCount(imageContainer.getChildCount() + 1);
			ContentTree.addNode(subimageContainer.getId(), new ContentNode(subimageContainer.getId(), subimageContainer));
			//嵌套
			String filePath2 = filePath;
			System.out.println("---GPF---filePath2=" + filePath2);
			File file2 = new File(filePath2);
			String[] file2List = file2.list();
			System.out.println("---GPF----file2List.length=" + file2List.length);
			for (int i = 0; i < file2List.length; i++) {
				String subfilePath = filePath2 +File.separator + file2List[i];
				String titlename = file2List[i];
				addImageFile(subimageContainer, subfilePath, titlename);
			}
		}
	}

	private Inet4Address getLocalIpAddress() {
		try {
			for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
				NetworkInterface intf = en.nextElement();
				for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
					InetAddress inetAddress = enumIpAddr.nextElement();
					if (!inetAddress.isLoopbackAddress()) {
						if (inetAddress instanceof Inet4Address) {
							System.out.println("GPF: inetAddress:" + inetAddress.getHostAddress().toString());
							return ((Inet4Address)inetAddress);
						}
					}
				}
			}
		} catch (SocketException ex) {
			System.out.println("----GPF-----getLocalIpAddress fail");
		}
		return null;
	}

}
