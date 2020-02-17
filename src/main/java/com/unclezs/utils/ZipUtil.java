package com.unclezs.utils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.zip.CRC32;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ZipUtil {
	private FileOutputStream fos;
	protected ZipOutputStream zos;

	public ZipUtil(File oZipFile) {
		try {
			fos = new FileOutputStream(oZipFile);
		} catch (Exception e) {
			System.err.println(e.toString());
		}
		zos = new ZipOutputStream(fos);
	}

	public void putTextFile(String content, String filename) {
		putTextFile(content, "UTF-8", filename);
	}

	public void putTextFile(String content, String outEncoding, String filename) {
		try {
			putBinFile(content.getBytes(outEncoding), filename, false);
		} catch (Exception e) {
			System.err.println(e.toString());
		}
	}

	public void setLevel(int level){  // 压缩级别: 0-9
		zos.setLevel(level);
	}

	public void putBinFile(byte[] b, String filename, boolean isStored) {
		ZipEntry entry = new ZipEntry(filename);
		if ( isStored ) { // 仅存储，不压缩
			entry.setMethod(ZipEntry.STORED);
			entry.setCompressedSize(b.length);
			CRC32 crc = new CRC32();
			crc.update(b);
			entry.setCrc(crc.getValue());
		}
		try {
			zos.putNextEntry(entry);
			zos.write(b, 0, b.length);
			zos.closeEntry();
		} catch (Exception e) {
			System.err.println(e.toString());
		}
	}

	public void putBinFile(File inFile, String filename) {  // 将文件写入zip
		ZipEntry entry = new ZipEntry(filename);
		try {
			zos.putNextEntry(entry);
			byte buffer[] = new byte[1048576]; // 1M
			BufferedInputStream in = new BufferedInputStream(new FileInputStream(inFile), 1048576);
			int realLength;
			while ((realLength = in.read(buffer)) != -1)
				zos.write(buffer, 0, realLength);
			in.close();
			zos.flush();
			zos.closeEntry();
		} catch (Exception e) {
			System.err.println(e.toString());
		}
	}

	public void close() {
		try {
			zos.close();
			fos.close();
		} catch (Exception e) {
			System.err.println(e.toString());
		}
	}

	public static void main(String[] args) {
		long sTime = System.currentTimeMillis();

		ZipUtil zip = new ZipUtil(new File("C:\\xx.zip"));
		zip.putBinFile("application/epub+zip".getBytes(), "mimetype", true); // epub规范，第一个文件必须为stored
		zip.setLevel(9);
		zip.putTextFile("<html>\n</html>\n", "index.html");
		zip.close();

		System.out.println("Time=" + (System.currentTimeMillis() - sTime));
	}

}
