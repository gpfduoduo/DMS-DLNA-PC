# DMS-DLNA-PC
基于Cling在PC下实现DLNA Server

## 项目说明
  此项目是在PC下（linux也是可以的，需要修改一下代码中的路径）基于Cling实现的DLNA DMS，允许程序后，
  DLNA中的DMC（例如：你的手机中安装了DLNA的客户端）与你的PC在同一个局域网下，就可以通过DMC来浏览PC中的
  内容，当然也可以通过DMC将这些内容到你的大屏幕上，如电视的盒子，目前世面上所有的OTT盒子
  （如天猫魔盒、小米盒子等）几乎都支持DLNA。
  
## 代码中需要注意的地方
  本工程并没有实现UI，因此在本工程中你的多媒体资源（音频、视频和图片）的路径是在代码中写死的，如：
  ```JAVA
  private String localMusicPath = "E:\\Entertainment\\audio"; //你的音频路径，将你的音频文件放在这个地方就好
	private String localVideoPath = "E:\\Entertainment\\video";
	private String localPhotoPath = "E:\\Entertainment\\photo";
  ```
  
## 工程启动方法
  在Eclipse中，右击StartDMS 选择run as java application，即可。
  
## 
