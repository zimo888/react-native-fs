package com.rnfs;

import java.io.*;

public class Copy {

  String startFilePath = null;
  String desFilePath = null;

  public Copy() {

  }

  /**
   *
   * 如果复制的是个文件夹，那么有个前提，那就是目的文件夹所在位置，不能在源文件夹的子目录中，
   * 如：如果源文件夹为D:/yingzi ,那么目的文件夹不能在D:/yingzi文件夹下的某个位置，因为这样
   * 会造成无限循环源文件夹下内容一直增加着，而目的文件夹下内容随着源文件夹的增加而增加，
   * windows下就不可以复制，不信你试试
   *
   * @param startFilePath
   *            = 原始文件路径
   * @param desFilePath
   *            = 目标文件路径
   * @return = 返回true，表示复制成功，返回false表示复制失败
   *
   */
  public boolean copy(String startFilePath, String desFilePath) {

    this.startFilePath = startFilePath;
    this.desFilePath = desFilePath;

    // 判断是否返回成功的变量
    boolean copyFinished = false;

    File startFile = new File(startFilePath);
    File desFile = new File(desFilePath);

    // 如果源文件是个文件
    if (startFile.isFile()) {

      copyFinished = this.copySingleFile(startFile, desFile);

      // 如果源文件是个文件夹，就需要递归复制
    } else {

      //如果目标文件夹是源文件夹的一个子目录的情况，拒绝复制，因为会造成无限循环
      if(desFilePath.startsWith(startFilePath)){
        System.out.println("error copy");
        return false;

      }else

        copyFinished = this.copyFolder(startFile, desFile);

    }


    return copyFinished;
  }

  /**
   * 此方法为复制单个文件，如果复制多个文件可以递归调用
   */
  private boolean copySingleFile(File startSingleFile, File desSingleFile) {

    boolean rightCopy = false;

    // -------从源文件中输入内存入byte中，在将byte写入目标文件--------------------
    FileInputStream singleFileInputStream = null;
    DataInputStream singleDataInputStream = null;
    FileOutputStream singleFileOutputStream = null;
    DataOutputStream singleDataOutputStream = null;

    try {

      singleFileInputStream = new FileInputStream(startSingleFile);

      singleDataInputStream = new DataInputStream(
              new BufferedInputStream(singleFileInputStream));

      singleFileOutputStream = new FileOutputStream(desSingleFile);

      singleDataOutputStream = new DataOutputStream(
              new BufferedOutputStream(singleFileOutputStream));

      byte[] b = new byte[1024];

      int len;
      while ((len = singleDataInputStream.read(b)) != -1) {

        singleDataOutputStream.write(b, 0, len);

      }
      //刷新缓冲区
      singleDataOutputStream.flush();

    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      try {

        if (singleDataInputStream != null)
          singleDataInputStream.close();
        if (singleDataOutputStream != null)
          singleDataOutputStream.close();

      } catch (Exception e) {
        e.printStackTrace();
      }
    }

    //判断源文件和目标文件大小是否相同，如果相同证明复制成功
    if (startSingleFile.length() == desSingleFile.length())
      rightCopy = true;
    else
      rightCopy = false;

    return rightCopy;

  }


  /**
   * 递归复制文件夹，因为文件夹下不止一个文件，里面可能有文件或文件夹，
   * 因此需要调用递归方法
   * @param startFolder  = 需要复制的文件夹
   * @param desFolder =  复制目的地的文件夹
   * @return = true 表示成功，false 表示失败
   */


  public boolean copyFolder(File startFolder, File desFolder) {

    boolean rightCopy = false;

    rightCopy = this.recursionCopy(startFolder, desFolder);

    return rightCopy;
  }

  /**
   * 复制文件夹函数，此函数是个递归，会复制文件夹下的所有文件
   *
   * @param recFileFolder
   *            = 需要拷贝的文件夹或子文件夹
   * @param recDesFolder
   *            = 拷贝的目的文件夹或子文件夹，
   * @return = true表示成功， false表示失败
   */
  private boolean recursionCopy(File recFileFolder, File recDesFolder) {

    File desFolder = recDesFolder;

    //因为目的文件夹或子文件夹不存在，需要创建
    desFolder.mkdir();

    //此为需要拷贝的文件夹下的所有文件列表（其中有文件和文件夹）
    File[] files = recFileFolder.listFiles();

    //如果是个空文件夹
    if(files == null || files.length==0) return true;


		/*
		 * 将文件夹下所有文件放入for循环，如果是文件，那么调用copySingleFile()拷贝文件，
		 * 如果是文件夹，那么递归调用此函数
		 *
		 */
    for (File thisFile : files) {

      // 如果此文件是个文件，那么直接调用单个文件复制命令复制文件
      if (thisFile.isFile()) {
        // 得到此文件的新位置地址
        String desContentFilePath = this.getSubFilePath(startFilePath,desFilePath, thisFile.getAbsolutePath());

        boolean rightCopy = this.copySingleFile(thisFile, new File(desContentFilePath));

        // 如果复制失败，就跳出循环停止复制
        if(!rightCopy) return false;

        // 如果是个文件夹
      } else {

				/*
				 * 此函数是为了得到目的文件夹的地址，
				 * 如：源文件夹为：D:/yingzi/text (其中text文件夹下有另一个文件夹 second :  D:/yingzi/text/second)
				 *    		目标位置为：E:/aa/text
				 *    那么此second文件夹在目标地址的位置就是 E:/aa/text/second
				 *
				 */
        String desContentFilePath = this.getSubFilePath(startFilePath,desFilePath, thisFile.getAbsolutePath());
        // 递归的调用此函数，确保函数都被复制完全
        boolean rightCopy = recursionCopy(thisFile, new File(desContentFilePath));
        if(!rightCopy) return false;
      }

    }
    return true;
  }

  /**
   * 此函数是为了得到目的文件夹的地址，
   * 如：源文件夹为：D:/yingzi/text (其中text文件夹下有另一个文件夹 second :  D:/yingzi/text/second)
   *    		目标位置为：E:/aa/text
   *    那么此second文件夹在目标地址的位置就是 E:/aa/text/second
   *    此方法中 startFolderPath = D:/yingzi/text (源文件夹) ；
   *   		 desFolderPath = E:/aa/text (目标位置)；
   *   		 currentFilePath = D:/yingzi/text/second(需要复制的子文件夹)
   *         返回值为： E:/aa/text/second
   */
  private String getSubFilePath(String startFolderPath, String desFolderPath,
                                String currentFilePath) {

    String currentDesFilePath = null;

    int i = startFolderPath.length();

    //int j = desFolderPath.lastIndexOf("/");

    //String subDirPath = startFolderPath.substring(0, i);
    //String subDesPath = desFolderPath.substring(0, j);

    currentDesFilePath = desFolderPath
            + currentFilePath.substring(i);

    return currentDesFilePath;

  }

}

