package com.unclezs.utils;


import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.http.HttpUtil;
import info.monitorenter.cpdetector.io.CodepageDetectorProxy;
import info.monitorenter.cpdetector.io.JChardetFacade;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Comparator;

public class OsUtil {
    /**
     * 判断文件是否存在
     *
     * @param path 文件路径
     * @return 存在返回true
     */
    public static boolean checkFileExist(String path) {
        if (new File(path).exists())
            return true;
        return false;
    }

    //获取文件编码
    public static String codeFile(String path) throws IOException {
        CodepageDetectorProxy detector = CodepageDetectorProxy.getInstance();
        detector.add(JChardetFacade.getInstance());
        Charset encode = detector.detectCodepage(new File(path).toURI().toURL());
        return encode.name();
    }

    //按照字节写文件
    public static void writeFileByte(String path, InputStream is) throws IOException {
        File file = new File(path);
        if (!file.exists()) {
            file.getParentFile().mkdirs();
            file.createNewFile();
        }
        BufferedInputStream bis = new BufferedInputStream(is);
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
        byte[] b = new byte[1024];
        int len;
        while ((len = bis.read(b)) != -1) {
            bos.write(b, 0, len);
        }
        is.close();
    }

    // { 通用文本读取，写入
    // 优先使用这个读取文本，快点，变量大小可以调整一下以达到最好的速度
    public static String readText(String filePath, String inFileEnCoding) {
        // 为了线程安全，可以替换StringBuilder 为 StringBuffer
        StringBuilder retStr = new StringBuilder(174080);
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(filePath), inFileEnCoding));

            char[] chars = new char[4096]; // 这个大小不影响读取速度
            int length = 0;
            while ((length = br.read(chars)) > 0) {
                retStr.append(chars, 0, length);
            }
/*
			// 下面这个效率稍低，但可以控制换行符
			String line = null;
			while ((line = br.readLine()) != null) {
			retStr.append(line).append("\n");
			}
*/
            br.close();
        } catch (Exception e) {
            System.err.println(e.toString());
        }
        return retStr.toString();
    }

    // 写入指定编码，速度快点
    public static void writeText(String iStr, String filePath, String oFileEncoding) {
        boolean bAppend = false;
        try {
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filePath, bAppend), oFileEncoding));
            bw.write(iStr);
            bw.flush();
            bw.close();
        } catch (Exception e) {
            System.err.println(e.toString());
        }
    }

    //下载文件
    public static String uploadFile(String path, String uri, int size) {
        try {
            File file = FileUtil.file(path);
            /*将网络资源地址传给,即赋值给url*/
            URL url = new URL(uri);
            /*此为联系获得网络资源的固定格式用法，以便后面的in变量获得url截取网络资源的输入流*/
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setReadTimeout(10000);
            connection.setConnectTimeout(10000);
//            BufferedInputStream in = new BufferedInputStream(connection.getInputStream());
//            /*此处也可用BufferedInputStream与BufferedOutputStream*/
//            BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(path));
//            /*将参数savePath，即将截取的图片的存储在本地地址赋值给out输出流所指定的地址*/
//            byte[] buffer = new byte[size];
//            int count = 0;
//            /*将输入流以字节的形式读取并写入buffer中*/
//            while ((count = in.read(buffer)) > 0) {
//                out.write(buffer, 0, count);
//            }
//            out.close();/*后面三行为关闭输入输出流以及网络资源的固定格式*/
//            in.close();
            //返回内容是保存后的完整的URL
            /*网络资源截取并存储本地成功返回true*/
            IoUtil.copy(connection.getInputStream(), FileUtil.getOutputStream(file));
            connection.disconnect();
            return file.getAbsolutePath();

        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    //下载图片
    public static String uploadFile(String path, String uri) {
        return uploadFile(path, uri, 4 * 1024);
    }

    /**
     * 合并文件
     *
     * @param name     合并后的文件名字
     * @param path     保存路径
     * @param tempPath 要合并的文件目录
     * @param code     编码
     * @return 合并后的绝对路径
     */
    public static String mergeFiles(String name, String path, String tempPath, String code) {
        // 过滤出分块文件
        //分块目录
        File temPath = new File(tempPath);
        File[] files = temPath.listFiles(pathname -> {
            if (pathname.getName().split("\\-").length < 2) {
                return false;
            } else {
                return true;
            }
        });
        String fp = path + "/" + name.replaceAll("[^\\u4E00-\\u9FFF]", "");
        int x = 1;
        while (new File(fp + ".txt").exists()) {
            if (fp.matches(".+?[(].+?[)]")) {
                fp = fp.substring(0, fp.length() - 3) + "(" + x + ")";
            } else {
                fp = fp + "(" + x + ")";
            }
            x++;
        }
        fp = fp + ".txt";
        Arrays.sort(files, new FileSort());
        try (PrintWriter out = new PrintWriter(
                new OutputStreamWriter(new FileOutputStream(new File(fp)), code))) {
            for (File file : files) {
                System.out.println(file.getName());
                BufferedReader buf = new BufferedReader(
                        new InputStreamReader(new FileInputStream(file.getAbsolutePath()), code));
                String line;
                while ((line = buf.readLine()) != null) {
                    out.println(line);
                }
                out.println();
                buf.close();
            }
            //删除分块临时文件夹
            deleteDir(temPath);
            return path;
        } catch (Exception e) {
            throw new RuntimeException("文件合并失败");
        }
    }

    /**
     * 删除文件夹
     *
     * @param dir 文件夹
     * @return
     */
    public static boolean deleteDir(File dir) {
        if (dir.isDirectory()) {
            // 递归删除目录中的子目录下
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                if (!deleteDir(new File(dir, children[i]))) {
                    return false;
                }
            }
        } // 目录此时为空，可以删除
        boolean bDeleted = false;
        try {
            bDeleted = dir.delete();
        } catch (Exception e) {
            System.err.println(e.toString());
        }
        return bDeleted;
    }

    /**
     * 排序合并文件的序列
     */
    private static class FileSort implements Comparator<File> {
        @Override
        public int compare(File o1, File o2) {
            int o1index = Integer.parseInt(o1.getName().split("\\-")[0]);
            int o2index = Integer.parseInt(o2.getName().split("\\-")[0]);
            return o1index > o2index ? 1 : -1;
        }
    }

}
