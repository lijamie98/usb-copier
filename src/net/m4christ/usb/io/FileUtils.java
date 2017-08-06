package net.m4christ.usb.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class FileUtils {
	public FileUtils() {
	}

	public static long getTotalSize(File current) {
		if (current.isDirectory()) {
			File[] files = current.listFiles();

			long totalSize = 0L;
			for (File file : files) {
				if (file.isDirectory()) {
					totalSize += getTotalSize(file);
				} else {
					totalSize += file.length();
				}
			}
			return totalSize;
		}
		return current.length();
	}

	public static String readableFileSize(long size) {
		if (size <= 0L) {
			return "0";
		}
		String[] units = { "B", "kB", "MB", "GB", "TB" };
		int digitGroups = (int) (Math.log10(size) / Math.log10(1024.0D));
		return new DecimalFormat("#,##0.#").format(size
				/ Math.pow(1024.0D, digitGroups))
				+ " " + units[digitGroups];
	}

	public static void copyFile(File sourceFile, File destFile)
			throws IOException {
		if (!destFile.exists()) {
			destFile.createNewFile();
		}
		FileChannel source = null;
		FileChannel destination = null;
		try {
			source = new FileInputStream(sourceFile).getChannel();
			destination = new FileOutputStream(destFile).getChannel();
			destination.transferFrom(source, 0L, source.size());
		} finally {
			if (source != null) {
				source.close();
			}
			if (destination != null) {
				destination.close();
			}
		}
	}

	/**
	 * Search all the files under the folder. 
	 * @param folder
	 * @param includeFolder if true, the folders are also added as File objects.
	 * @return
	 */
	public static List<File> searchFiles(File folder, boolean includeFolder) {
		List<File> files = new ArrayList<File>();
		_searchFiles(files, folder, includeFolder);
		return files;
	}

	private static void _searchFiles(List<File> result, File folder,
			boolean includeFolder) {
		for (File f : folder.listFiles()) {
			if (f.isDirectory()) {
				_searchFiles(result, f, includeFolder);
				if (includeFolder) {
					result.add(f);
				}
			} else if (f.isFile()) {
				result.add(f);
			}
		}
	}
}
