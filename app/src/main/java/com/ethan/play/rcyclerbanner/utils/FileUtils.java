package com.ethan.play.rcyclerbanner.utils;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;



import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

/**
 * 文件操作
 *
 * @author adkins.zhang
 */
@SuppressLint("NewApi")
public class FileUtils {
    public final static String FILE_EXTENSION_SEPARATOR = ".";

    public static boolean hasSDCard() {
        String status = Environment.getExternalStorageState();
        if (!status.equals(Environment.MEDIA_MOUNTED)) {
            return false;
        }
        return true;
    }

    public static String getRootFilePath() {
        if (hasSDCard()) {
            return Environment.getExternalStorageDirectory().getAbsolutePath() + "/";
        } else {
            return Environment.getDataDirectory().getAbsolutePath() + "/data/";
        }
    }

    /**
     * 读取文件
     *
     * @param filePath
     * @param charsetName
     * @return
     */
    public static String readFile(String filePath, String charsetName) {
        File file = new File(filePath);
        StringBuilder fileContent = new StringBuilder("");
        if (file == null || !file.isFile()) {
            return null;
        }

        BufferedReader reader = null;
        try {
            InputStreamReader is = new InputStreamReader(new FileInputStream(file), charsetName);
            reader = new BufferedReader(is);
            String line = null;
            while ((line = reader.readLine()) != null) {
                if (!fileContent.toString().equals("")) {
                    fileContent.append("\r\n");
                }
                fileContent.append(line);
            }
            reader.close();
            return fileContent == null ? "" : fileContent.toString();
        } catch (IOException e) {
            throw new RuntimeException("IOException occurred. ", e);
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    throw new RuntimeException("IOException occurred. ", e);
                }
            }
        }
    }

    /**
     * 移动指定文件夹内的全部文件
     *
     * @param from 要移动的文件目录
     * @param to   目标文件目录
     * @throws Exception
     */
    public static boolean moveDir(String from, String to) {
        try {
            File dir = new File(from);
            // 文件一览
            File[] files = dir.listFiles();
            if (files == null)
                return false;
            // 目标
            File moveDir = new File(to);
            if (!moveDir.exists()) {
                boolean mkResult = moveDir.mkdirs();
                if (!mkResult) {
                    return false;
                }
            }
            // 文件移动
            for (int i = 0; i < files.length; i++) {
                if (files[i].isDirectory()) {
                    if (moveDir(files[i].getPath(), to + File.separator + files[i].getName()))
                        // 成功，删除原文件
                        files[i].delete();
                    else return false;
                } else {
                    File moveFile = new File(moveDir.getPath() + File.separator
                            + files[i].getName());
                    // 目标文件夹下存在的话，删除
                    if (moveFile.exists()) {
                        moveFile.delete();
                    }
                    if (!files[i].renameTo(moveFile)) {
                        return false;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * 追加方式写文件
     *
     * @param filePath
     * @param content
     * @param append
     * @return
     */
    public static boolean writeFile(String filePath, String content, boolean append) {
        FileWriter fileWriter = null;
        try {
            makeDirs(filePath);
            fileWriter = new FileWriter(filePath, append);
            fileWriter.write(content);
            fileWriter.close();
            return true;
        } catch (IOException e) {
            throw new RuntimeException("IOException occurred. ", e);
        } finally {
            if (fileWriter != null) {
                try {
                    fileWriter.close();
                } catch (IOException e) {
                    throw new RuntimeException("IOException occurred. ", e);
                }
            }
        }
    }

    /**
     * 写文件
     *
     * @param filePath
     * @param stream
     * @return
     */
    public static boolean writeFile(String filePath, InputStream stream) {
        return writeFile(filePath, stream, false);
    }

    /**
     * 追加方式写流
     *
     * @param filePath
     * @param stream
     * @param append
     * @return
     */
    public static boolean writeFile(String filePath, InputStream stream, boolean append) {
        return writeFile(filePath != null ? new File(filePath) : null, stream, append);
    }

    /**
     * @param file
     * @param stream
     * @return
     */
    public static boolean writeFile(File file, InputStream stream) {
        return writeFile(file, stream, false);
    }

    /**
     * @param file
     * @param stream
     * @param append
     * @return
     */
    public static boolean writeFile(File file, InputStream stream, boolean append) {
        OutputStream o = null;
        try {
            makeDirs(file.getAbsolutePath());
            o = new FileOutputStream(file, append);
            byte data[] = new byte[1024];
            int length = -1;
            while ((length = stream.read(data)) != -1) {
                o.write(data, 0, length);
            }
            o.flush();
            return true;
        } catch (FileNotFoundException e) {
            throw new RuntimeException("FileNotFoundException occurred. ", e);
        } catch (IOException e) {
            throw new RuntimeException("IOException occurred. ", e);
        } finally {
            if (o != null) {
                try {
                    o.close();
                    stream.close();
                } catch (IOException e) {
                    throw new RuntimeException("IOException occurred. ", e);
                }
            }
        }
    }

    /**
     * 移动文件
     *
     * @param source      需要移动的文件的路径
     * @param destination 目标路径
     */
    public static boolean moveFile(String source, String destination) {
        return new File(source).renameTo(new File(destination));
    }

    /**
     * 复制文件
     *
     * @param sourceFilePath
     * @param destFilePath
     * @return
     */
    public static boolean copyFile(String sourceFilePath, String destFilePath) {
        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream(sourceFilePath);
        } catch (FileNotFoundException e) {
            throw new RuntimeException("FileNotFoundException occurred. ", e);
        }
        return writeFile(destFilePath, inputStream);
    }

    /**
     * 复制单个文件
     *
     * @param oldPath String 原文件路径 如：c:/fqf.txt
     * @param newPath String 复制后路径 如：f:/fqf.txt
     * @return boolean
     */
    public static boolean copyFile2(String oldPath, String newPath) {
        boolean isok = true;
        try {
            int bytesum = 0;
            int byteread = 0;
            File oldfile = new File(oldPath);
            if (oldfile.exists()) {
                InputStream inStream = new FileInputStream(oldPath);
                FileOutputStream fs = new FileOutputStream(newPath);
                byte[] buffer = new byte[1024];
                int length;
                while ((byteread = inStream.read(buffer)) != -1) {
                    bytesum += byteread;
                    fs.write(buffer, 0, byteread);
                }
                fs.flush();
                fs.close();
                inStream.close();
            } else {
                isok = false;
            }
        } catch (Exception e) {
            isok = false;
        }
        return isok;

    }

    /**
     * 复制整个文件夹内容
     *
     * @param oldPath String 原文件路径 如：c:/fqf
     * @param newPath String 复制后路径 如：f:/fqf/ff
     * @return boolean
     */
    public static boolean copyFolder(String oldPath, String newPath) {
        boolean isok = true;
        try {
            (new File(newPath)).mkdirs(); // 如果文件夹不存在 则建立新文件夹
            File a = new File(oldPath);
            String[] file = a.list();
            File temp = null;
            for (int i = 0; i < file.length; i++) {
                if (oldPath.endsWith(File.separator)) {
                    temp = new File(oldPath + file[i]);
                } else {
                    temp = new File(oldPath + File.separator + file[i]);
                }

                if (temp.isFile()) {
                    FileInputStream input = new FileInputStream(temp);
                    FileOutputStream output = new FileOutputStream(newPath + "/" + (temp.getName()).toString());
                    byte[] b = new byte[1024 * 5];
                    int len;
                    while ((len = input.read(b)) != -1) {
                        output.write(b, 0, len);
                    }
                    output.flush();
                    output.close();
                    input.close();
                }
                if (temp.isDirectory()) {// 如果是子文件夹
                    copyFolder(oldPath + "/" + file[i], newPath + "/" + file[i]);
                }
            }
        } catch (Exception e) {
            isok = false;
        }
        return isok;
    }

    public static File[] getFileList(String dirPath) {
        if (FileUtils.isFolderExist(dirPath)) {
            return new File(dirPath).listFiles();
        }
        return null;
    }

    public static List<String> readFileToList(String filePath, String charsetName) {
        File file = new File(filePath);
        List<String> fileContent = new ArrayList<String>();
        if (file == null || !file.isFile()) {
            return null;
        }

        BufferedReader reader = null;
        try {
            InputStreamReader is = new InputStreamReader(new FileInputStream(file), charsetName);
            reader = new BufferedReader(is);
            String line = null;
            while ((line = reader.readLine()) != null) {
                fileContent.add(line);
            }
            reader.close();
            return fileContent;
        } catch (IOException e) {
            throw new RuntimeException("IOException occurred. ", e);
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    throw new RuntimeException("IOException occurred. ", e);
                }
            }
        }
    }

    public static boolean renameFile(String odlerName, String newName) {
        File file = new File(odlerName);
        boolean isOk = false;
        if (file.exists()) {
            isOk = file.renameTo(new File(newName));
        }
        return isOk;
    }

    /**
     * 获取文件名，不包含后缀名
     *
     * @param filePath
     * @return
     */
    public static String getFileNameWithoutExtension(String filePath) {
        if (filePath.isEmpty()) {
            return filePath;
        }

        int extenPosi = filePath.lastIndexOf(FILE_EXTENSION_SEPARATOR);
        int filePosi = filePath.lastIndexOf(File.separator);
        if (filePosi == -1) {
            return (extenPosi == -1 ? filePath : filePath.substring(0, extenPosi));
        }
        if (extenPosi == -1) {
            return filePath.substring(filePosi + 1);
        }
        return (filePosi < extenPosi ? filePath.substring(filePosi + 1, extenPosi) : filePath.substring(filePosi + 1));
    }

    /**
     * 获取文件名，包含后缀名
     *
     * @param filePath
     * @return
     */
    public static String getFileName(String filePath) {
        if (filePath.isEmpty()) {
            return filePath;
        }

        int filePosi = filePath.lastIndexOf(File.separator);
        return (filePosi == -1) ? filePath : filePath.substring(filePosi + 1);
    }

    /**
     * 获取父路径
     *
     * @param filePath
     * @return
     */
    public static String getFolderName(String filePath) {

        if (filePath.isEmpty()) {
            return filePath;
        }

        int filePosi = filePath.lastIndexOf(File.separator);
        return (filePosi == -1) ? "" : filePath.substring(0, filePosi);
    }

    /**
     * 获取后缀名
     *
     * @param filePath
     * @return
     */
    public static String getFileExtension(String filePath) {
        if (StringUtils.isBlank(filePath)) {
            return filePath;
        }

        int extenPosi = filePath.lastIndexOf(FILE_EXTENSION_SEPARATOR);
        int filePosi = filePath.lastIndexOf(File.separator);
        if (extenPosi == -1) {
            return "";
        }
        return (filePosi >= extenPosi) ? "" : filePath.substring(extenPosi + 1);
    }

    /**
     * 创建完整路径
     *
     * @param filePath
     * @return
     */
    public static boolean makeDirs(String filePath) {
        String folderName = getFolderName(filePath);
        if (StringUtils.isEmpty(folderName)) {
            return false;
        }
        File folder = new File(folderName);
        return (folder.exists() && folder.isDirectory()) ? true : folder.mkdirs();
    }


    public static boolean createFile(String filePath) {
        if (StringUtils.isEmpty(filePath)) {
            return false;
        }
        File folder = new File(filePath);
        try {
            if (folder.exists()) {
                return true;
            } else {
                folder.createNewFile();
                return true;
            }
            // return (folder.exists()) ? true : folder.createNewFile();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            return false;
        }
    }

    /**
     * @param filePath
     * @return
     */
    public static boolean makeFolders(String filePath) {
        return makeDirs(filePath);
    }

    /**
     * 文件是否存在
     *
     * @param filePath
     * @return
     */
    public static boolean isFileExist(String filePath) {
        if (StringUtils.isBlank(filePath)) {
            return false;
        }

        File file = new File(filePath);
        return (file.exists() && file.isFile());
    }

    /**
     * 文件夹是否存在
     *
     * @param directoryPath
     * @return
     */
    public static boolean isFolderExist(String directoryPath) {
        if (StringUtils.isBlank(directoryPath)) {
            return false;
        }

        File dire = new File(directoryPath);
        return (dire.exists() && dire.isDirectory());
    }

    /**
     * 删除文件或者文件夹
     *
     * @param path
     * @return
     */
    public static boolean deleteFile(String path) {
        if (StringUtils.isBlank(path)) {
            return true;
        }

        File file = new File(path);
        if (!file.exists()) {
            return true;
        }
        if (file.isFile()) {
            return file.delete();
        }
        if (!file.isDirectory()) {
            return false;
        }
        File[] listFile = file.listFiles();
        if (listFile != null) {
            for (File f : listFile) {
                if (f.isFile()) {
                    f.delete();
                } else if (f.isDirectory()) {
                    deleteFile(f.getAbsolutePath());
                }
            }
        }
        return file.delete();
    }

    /**
     * 获取文件大小
     *
     * @param path
     * @return
     */
    public static long getFileSize(String path) {
        if (StringUtils.isBlank(path)) {
            return -1;
        }

        File file = new File(path);
        return (file.exists() && file.isFile() ? file.length() : -1);
    }

    public static void saveBitmap(String savePath, Bitmap bitmap) {
        makeDirs(savePath);
        File f = new File(savePath);
        if (f.exists()) {
            f.delete();
        }
        try {
            FileOutputStream out = new FileOutputStream(f);
            bitmap.compress(Bitmap.CompressFormat.PNG, 90, out);
            out.flush();
            out.close();
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * 将图片保存到本地
     *
     * @param path
     * @param fileName
     * @param bitmap
     */
    public static void saveBitmap(String path, String fileName, Bitmap bitmap) {
        saveBitmap(path + fileName, bitmap);
    }

    /**
     * 从SD卡获取图片
     */
    public static Bitmap getBitmapFormSDCard(String fileName) {
        Bitmap btn;
        btn = BitmapFactory.decodeFile(fileName);
        if (btn != null)
            return btn;
        return null;
    }

    // 写文件
    public static void writeSDFile(String fileName, String write_str) throws IOException {
        File file = new File(fileName);
        FileOutputStream fos = new FileOutputStream(file);
        byte[] bytes = write_str.getBytes();
        fos.write(bytes);
        fos.close();
    }

    public static long getFolderSize(String path) {
        if (StringUtils.isBlank(path))
            return 0;
        File dire = new File(path);
        return (dire.exists() && dire.isDirectory() ? getFolderSize(dire) : 0);
    }

    /**
     * 获取文件夹占用空间大小
     *
     * @param f
     * @return
     */
    public static String getFloderSize(File f) {
        long size = getFolderSize(f);
        DecimalFormat df = new DecimalFormat("###.00");
        String result = df.format((float) size / 1048576);
        if (result.indexOf(".") == 0)
            return "0" + result;
        else
            return result;
    }

    /***
     * 获取文件夹大小
     ***/
    public static long getFolderSize(File f) {
        try {
            long size = 0;
            File flist[] = f.listFiles();
            for (int i = 0; i < flist.length; i++) {
                if (flist[i].isDirectory()) {
                    size = size + getFolderSize(flist[i]);
                } else {
                    size = size + flist[i].length();
                }
            }
            return size;
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * 解压缩功能. 将zipFile文件解压到folderPath目录下.
     *
     * @throws Exception
     */
    public static int upZipFile(File zipFile, String folderPath) throws ZipException, IOException {
        // public static void upZipFile() throws Exception{
        ZipFile zfile = new ZipFile(zipFile);
        Enumeration zList = zfile.entries();
        ZipEntry ze = null;
        byte[] buf = new byte[1024];
        while (zList.hasMoreElements()) {
            ze = (ZipEntry) zList.nextElement();
            if (ze.isDirectory()) {
                String dirstr = folderPath + ze.getName();
                // dirstr.trim();
                dirstr = new String(dirstr.getBytes("8859_1"), "GB2312");
                File f = new File(dirstr);
                f.mkdir();
                continue;
            }
            OutputStream os = new BufferedOutputStream(new FileOutputStream(getRealFileName(folderPath, ze.getName())));
            InputStream is = new BufferedInputStream(zfile.getInputStream(ze));
            int readLen = 0;
            while ((readLen = is.read(buf, 0, 1024)) != -1) {
                os.write(buf, 0, readLen);
            }
            is.close();
            os.close();
        }
        zfile.close();
        return 0;
    }

    /**
     * 给定根目录，返回一个相对路径所对应的实际文件名.
     *
     * @param baseDir     指定根目录
     * @param absFileName 相对路径名，来自于ZipEntry中的name
     * @return java.io.File 实际的文件
     */
    public static File getRealFileName(String baseDir, String absFileName) {
        String[] dirs = absFileName.split("/");
        File ret = new File(baseDir);
        String substr = null;
        if (dirs.length > 1) {
            for (int i = 0; i < dirs.length - 1; i++) {
                substr = dirs[i];
                try {
                    substr = new String(substr.getBytes("8859_1"), "GB2312");

                } catch (UnsupportedEncodingException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                ret = new File(ret, substr);
            }
            if (!ret.exists())
                ret.mkdirs();
            substr = dirs[dirs.length - 1];
            try {
                // substr.trim();
                substr = new String(substr.getBytes("8859_1"), "GB2312");
            } catch (UnsupportedEncodingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            ret = new File(ret, substr);
            return ret;
        }
        return ret;
    }

    public static boolean checkApk(String fname) {
        if (fname.substring(fname.length() - 4).equalsIgnoreCase(".apk")) {
            return true;
        }
        return false;
    }

    public static boolean checkImage(String fileName) {
        int suffixIndex = fileName.lastIndexOf(".");
        if (suffixIndex != -1) {
            String d = fileName.substring(suffixIndex + 1);
            if (d.equalsIgnoreCase("png") || d.equalsIgnoreCase("jpeg") || d.equalsIgnoreCase("jpg")
                    || d.equalsIgnoreCase("bmp")) {
                return true;
            }
        }
        return false;
    }

    public static String getFileLastModifiedTime(String filePath) {
        String path = filePath.toString();
        File f = new File(path);
        SimpleDateFormat sfd = new SimpleDateFormat("yyyy/MM/dd");
        long time = f.lastModified();
        return sfd.format(time);
    }


    public static String getFileMD5(String filePath) {
        if (isFileExist(filePath)) {
            return getFileMD5(new File(filePath));
        }
        return null;
    }

    /**
     * 获取单个文件的MD5值！
     *
     * @param file
     * @return
     */

    public static String getFileMD5(File file) {
        if (!file.isFile()) {
            return "";
        }
        MessageDigest digest = null;
        FileInputStream in = null;
        byte buffer[] = new byte[1024];
        int len;
        try {
            digest = MessageDigest.getInstance("MD5");
            in = new FileInputStream(file);
            while ((len = in.read(buffer, 0, 1024)) != -1) {
                digest.update(buffer, 0, len);
            }
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
        BigInteger bigInt = new BigInteger(1, digest.digest());
        return bigInt.toString(16);
    }
}
