package com.rnfs;

import android.content.Context;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class CopyAssets {
  public static final String TAG = "CopyAssets";
  /**
   * 从assets目录下拷贝文件
   *
   * @param context
   *            上下文
   * @param assetsFilePath
   *            文件的路径名如：SBClock/0001cuteowl/cuteowl_dot.png
   * @param targetFileFullPath
   *            目标文件路径如：/sdcard/SBClock/0001cuteowl/cuteowl_dot.png
   */
  public static void copyFileFromAssets(Context context, String assetsFilePath, String targetFileFullPath) {
    Log.d("copyFileFromAssets", "copyFileFromAssets ");
    InputStream assestsFileImputStream;
    try {
      assestsFileImputStream = context.getAssets().open(assetsFilePath);
      copyFile(assestsFileImputStream, targetFileFullPath);
    } catch (IOException e) {
      Log.d(TAG, "copyFileFromAssets " + "IOException-" + e.getMessage());
      e.printStackTrace();
    }
  }


  public static void copyFile(InputStream assestsFileImputStream,String targetFileFullPath) throws IOException
  {

    OutputStream myOutput = new FileOutputStream(targetFileFullPath);
    byte[] buffer = new byte[1024];
    int length = assestsFileImputStream.read(buffer);
    while(length > 0)
    {
      myOutput.write(buffer, 0, length);
      length = assestsFileImputStream.read(buffer);
    }

    myOutput.flush();
    assestsFileImputStream.close();
    myOutput.close();
  }


  private static boolean isFileByName(String string) {
    if (string.contains(".")) {
      return true;
    }
    return false;
  }


  /**
   * 从assets目录下拷贝整个文件夹，不管是文件夹还是文件都能拷贝
   *
   * @param context
   *            上下文
   * @param rootDirFullPath
   *            文件目录，要拷贝的目录如assets目录下有一个SBClock文件夹：SBClock
   * @param targetDirFullPath
   *            目标文件夹位置如：/sdcrad/SBClock
   */
  public static void copyFolderFromAssets(Context context, String rootDirFullPath, String targetDirFullPath) {
    Log.d(TAG, "copyFolderFromAssets " + "rootDirFullPath-" + rootDirFullPath + " targetDirFullPath-" + targetDirFullPath);
    try {
      String[] listFiles = context.getAssets().list(rootDirFullPath);// 遍历该目录下的文件和文件夹
      for (String string : listFiles) {// 看起子目录是文件还是文件夹，这里只好用.做区分了
        Log.d(TAG, "name-" + rootDirFullPath + "/" + string);
        if (isFileByName(string)) {// 文件
          File parentParent = new File(targetDirFullPath);
          if(!parentParent.isDirectory()){
            parentParent.delete();
          }
          if(!parentParent.exists()){
            parentParent.mkdirs();
          }

          File parent = new File(targetDirFullPath + "/" + rootDirFullPath);
          if(!parent.exists()){
            parent.mkdirs();
          }
          if(!parent.isDirectory()){
            parent.delete();
          }
          File targetFile = new File(targetDirFullPath + "/" + rootDirFullPath, string);
          targetFile.createNewFile();
          copyFileFromAssets(context, rootDirFullPath + "/" + string, targetDirFullPath + "/" + rootDirFullPath + "/" + string);
        } else {// 文件夹
          String childRootDirFullPath = rootDirFullPath + "/" + string;
          String childTargetDirFullPath = targetDirFullPath + "/" + string;
          new File(childTargetDirFullPath).mkdirs();
          copyFolderFromAssets(context, childRootDirFullPath, childTargetDirFullPath);
        }
      }
    } catch (IOException e) {
      Log.d(TAG, "copyFolderFromAssets " + "IOException-" + e.getMessage());
      Log.d(TAG, "copyFolderFromAssets " + "IOException-" + e.getLocalizedMessage());
      e.printStackTrace();
    }
  }

}

