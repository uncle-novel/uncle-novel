package com.unclezs.utils;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.system.SystemUtil;
import com.unclezs.constrant.Charsets;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.BitSet;

/**
 * 文件工具
 *
 * @author uncle
 * @date 2020/4/22 22:53
 */
public class FileUtil {
    private static final int BYTE_SIZE = 8;
    private static final String CODE_UTF8 = Charsets.UTF8;
    private static final String CODE_UTF8_BOM = "UTF-8_BOM";
    private static final String CODE_GBK = Charsets.GBK;
    private static final String SEPARATOR = "/";

    /**
     * 是否以 / 结尾
     * 不以/结尾就加上/
     *
     * @param s /
     * @return /
     */
    public static String ensureEndingSlash(String s) {
        if (s != null && !s.endsWith(FileUtil.SEPARATOR)) {
            s += "/";
        }
        return s;
    }

    /**
     * 获取文件路径  换 \为 / 并且末尾必带/
     *
     * @param file /
     * @return /
     */
    public static String getPath(File file) {
        String path = file.getAbsolutePath().replace("\\\\", "/").replace("\\", "/");
        if (!path.endsWith(SEPARATOR)) {
            path = path.concat(SEPARATOR);
        }
        return path;
    }

    public static String getPath(String path) {
        path = path.replace("\\\\", "/").replace("\\", "/");
        if (!path.endsWith(SEPARATOR)) {
            path = path.concat(SEPARATOR);
        }
        return path;
    }

    /**
     * 当前文件夹下的file
     *
     * @param path 相对于当前文件夹
     * @return /
     */
    public static File currentDirFile(String path) {
        return cn.hutool.core.io.FileUtil.file(getPath(getCurrentDir() + path));
    }

    /**
     * 获取当前文件夹
     *
     * @return /
     */
    public static String getCurrentDir() {
        return getPath(SystemUtil.getUserInfo().getCurrentDir());
    }

    /**
     * 判断是否为图片
     *
     * @param imageFile /
     * @return /
     */
    public static boolean isImage(String imageFile) {
        String mimeType = cn.hutool.core.io.FileUtil.getMimeType(imageFile);
        if (mimeType == null) {
            return false;
        }
        String type = mimeType.split("/")[0];
        return "image".equals(type);
    }

    /**
     * 检测文件是否存在 存在则自动重命名
     *
     * @param path /
     * @return /
     */
    static File checkExistAndRename(String path, boolean rename) {
        File target = cn.hutool.core.io.FileUtil.file(path);
        return checkExistAndRename(target, rename);
    }

    /**
     * 检测文件是否存在 存在则自动重命名
     *
     * @param target /
     * @return /
     */
    public static File checkExistAndRename(File target, boolean rename) {
        if (target.exists() && rename) {
            String name = cn.hutool.core.io.FileUtil.mainName(target);
            String ext = cn.hutool.core.io.FileUtil.extName(target);
            //没有后缀的
            if (StrUtil.isBlank(ext)) {
                return checkExistAndRename(cn.hutool.core.io.FileUtil.file(target.getParent(),
                    String.format("%s_%s/", name, RandomUtil.randomString(3))), true);
            }
            return checkExistAndRename(cn.hutool.core.io.FileUtil.file(target.getParent(),
                String.format("%s_%s.%s", name, RandomUtil.randomString(3), ext)), true);
        } else {
            return target;
        }
    }


    /**
     * 文件大小
     *
     * @param file /
     * @return /
     */
    public static String size(File file) {
        if (!file.exists()) {
            return "0B";
        }
        return size(cn.hutool.core.io.FileUtil.size(file));
    }

    /**
     * 文件大小
     *
     * @param size /
     * @return xx M
     */
    public static String size(Long size) {
        BigDecimal fileSize = new BigDecimal(size);
        BigDecimal param = new BigDecimal(1024);
        int count = 0;
        while (fileSize.compareTo(param) > 0 && count < 5) {
            fileSize = fileSize.divide(param, RoundingMode.HALF_UP);
            count++;
        }
        DecimalFormat df = new DecimalFormat("#.##");
        String result = df.format(fileSize) + "";
        switch (count) {
            case 0:
                result += "B";
                break;
            case 1:
                result += "KB";
                break;
            case 2:
                result += "MB";
                break;
            case 3:
                result += "GB";
                break;
            case 4:
                result += "TB";
                break;
            case 5:
                result += "PB";
                break;
            default:
                break;
        }
        return result;
    }

    /**
     * 通过文件全名称获取编码集名称
     *
     * @param fullFileName /
     * @param ignoreBom    /
     * @return /
     * @throws Exception /
     */
    public static String getEncode(String fullFileName, boolean ignoreBom) throws Exception {
        BufferedInputStream bis = new BufferedInputStream(new FileInputStream(fullFileName));
        return getEncode(bis, ignoreBom);
    }

    /**
     * 通过文件缓存流获取编码集名称，文件流必须为未曾
     *
     * @param bis       /
     * @param ignoreBom 是否忽略utf-8 bom
     * @return /
     * @throws Exception /
     */
    public static String getEncode(BufferedInputStream bis, boolean ignoreBom) throws Exception {
        bis.mark(0);
        String encodeType;
        byte[] head = new byte[3];
        int read = bis.read(head);
        if (head[0] == -1 && head[1] == -2) {
            encodeType = "UTF-16";
        } else if (head[0] == -2 && head[1] == -1) {
            encodeType = "Unicode";
        } else if (head[0] == -17 && head[1] == -69 && head[2] == -65) {
            if (ignoreBom) {
                encodeType = CODE_UTF8;
            } else {
                encodeType = CODE_UTF8_BOM;
            }
        } else if (utf8(bis)) {
            encodeType = CODE_UTF8;
        } else {
            encodeType = CODE_GBK;
        }
        return encodeType;
    }

    /**
     * 是否是无BOM的UTF8格式，不判断常规场景，只区分无BOM UTF8和GBK
     *
     * @param bis /
     * @return /
     */
    private static boolean utf8(BufferedInputStream bis) throws Exception {
        bis.reset();

        //读取第一个字节
        int code = bis.read();
        do {
            BitSet bitSet = convert2BitSet(code);
            //判断是否为单字节
            if (bitSet.get(0)) {
                if (!checkMultiByte(bis, bitSet)) {
                    return false;
                }
            }
            code = bis.read();
        } while (code != -1);
        return true;
    }

    /**
     * 检测多字节，判断是否为utf8，已经读取了一个字节
     *
     * @param bis    /
     * @param bitSet /
     * @return /
     */
    private static boolean checkMultiByte(BufferedInputStream bis, BitSet bitSet) throws Exception {
        int count = getCountOfSequential(bitSet);
        byte[] bytes = new byte[count - 1];
        bis.read(bytes);
        for (byte b : bytes) {
            if (!checkUtf8Byte(b)) {
                return false;
            }
        }
        return true;
    }

    /**
     * 检测单字节，判断是否为utf8
     *
     * @param b /
     * @return /
     */
    private static boolean checkUtf8Byte(byte b) {
        BitSet bitSet = convert2BitSet(b);
        return bitSet.get(0) && !bitSet.get(1);
    }

    /**
     * 检测bitSet中从开始有多少个连续的1
     *
     * @param bitSet /
     * @return /
     */
    private static int getCountOfSequential(BitSet bitSet) {
        int count = 0;
        for (int i = 0; i < BYTE_SIZE; i++) {
            if (bitSet.get(i)) {
                count++;
            } else {
                break;
            }
        }
        return count;
    }


    /**
     * 将整形转为BitSet
     *
     * @param code /
     * @return /
     */
    private static BitSet convert2BitSet(int code) {
        BitSet bitSet = new BitSet(BYTE_SIZE);

        for (int i = 0; i < BYTE_SIZE; i++) {
            int tmp3 = code >> (BYTE_SIZE - i - 1);
            int tmp2 = 0x1 & tmp3;
            if (tmp2 == 1) {
                bitSet.set(i);
            }
        }
        return bitSet;
    }

    /**
     * 强制删除文件
     *
     * @param s /
     */
    public static void deleteForce(String s) {
        deleteForce(new File(s));
    }

    /**
     * 强制删除文件
     *
     * @param file /
     */
    public static void deleteForce(File file) {
        if (file.isFile() && file.exists()) {
            boolean result = file.delete();
            int tryCount = 0;
            while (!result && tryCount++ < 10) {
                System.gc();
                result = file.delete();
            }
        }
    }
}
