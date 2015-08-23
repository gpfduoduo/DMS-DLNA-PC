# DMS-DLNA-PC
基于Cling在PC下实现DLNA Server
最基本的DLNA的相关知识在此不进行介绍了，具体的看链接：     

[![DLNA官方网址]](http://www.dlna.org/)      

[![百度百科]](http://baike.baidu.com/link?url=vs-a_pwD6ZxcboWaOFAPHoVujPc7PA-zUhbk3NIM64RVXe6uO27pY-pBpbxsAcU6gLkMXaEvUEtONuzUXKFzOK)

## 项目说明
  1、此项目是在PC下（linux也是可以的，需要修改一下代码中的路径）基于Cling实现的DLNA DMS，cling的官方地址在最后的感谢中。    
  
  2、运行程序后，  DLNA中的DMC（例如：你的手机中安装了DLNA的客户端）与你的PC在同一个局域网下，就可以通过DMC来浏览PC中的
内容，当然也可以通过DMC将这些内容到你的大屏幕上，如电视的盒子，目前世面上所有的OTT盒子（如天猫魔盒、小米盒子、zte的盒子、华为的盒子等）几乎都支持DLNA。    

  3、项目中实现了文件夹的嵌套，即：你DMC中浏览到的第一级的目录为：Audios、Videos和Images，后续的目录浏览是根据你放置的媒体文件的目录和文件夹是一一对应的，    
  
  4、调用jNotify来实现监听文件夹变化的操作，如果pc下媒体资源文件夹内容发生了变化，dms中的内容也会进行响应的变化，而不需要重新启动程序。    
  
  5、通过DLNA你的PC的多媒体内容不需要通过HDMI等第三方的一条线就可以完成媒体的共享。
  
## 代码中需要注意的地方
  本工程并没有实现UI，因此在本工程中你的多媒体资源（音频、视频和图片）的路径是在代码中写死的，   
  
  因此这就需要你的PC或者linux中必须具有以下的路径，具体的如：
  ```JAVA
  private String localMusicPath = "E:\\Entertainment\\audio"; //你的音频路径，将你的音频文件放在这个地方就好
  private String localVideoPath = "E:\\Entertainment\\video";
  private String localPhotoPath = "E:\\Entertainment\\photo";
  ```
  如果你想将程序运行在linux中，也是需要修改次路径
  ```JAVA
  private String localMusicPath = "/data/dlna/Music";
  private String localVideoPath = "/data/dlna/Video";
  private String localPhotoPath = "/data/dlna/Photo"
  ```
## 工程启动方法
  在Eclipse中，右击StartDMS 选择run as java application，即可。
  
## 效果图
  pc下音频内容：
  ![DMS目录](https://github.com/gpfduoduo/DMS-DLNA-PC/blob/master/picture/PC_Music.png "pc中DMS的音乐目录")
   在我的手机中安装了DMC客户端，浏览DMS中的内容如下：
  ![DMC运行结果](https://github.com/gpfduoduo/DMS-DLNA-PC/blob/master/picture/demo.gif "DMC的运行结果")

## 感谢
  1、cling的官方地址
    [![Cling]](http://4thline.org/projects/cling/)

