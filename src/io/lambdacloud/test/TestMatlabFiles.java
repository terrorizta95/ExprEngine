package io.lambdacloud.test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import io.lambdacloud.MatlabEngine;

public class TestMatlabFiles {

	public static void main(String[] args) {
		//compile("./test_matlab/spdiags.m");
		//compile("./test_matlab/matlab/elmat/flipdim.m");
		//compile("./test_matlab/matlab/elmat/ndgrid.m");
		//TODO compile("./test_matlab/matlab/elmat/gallery.m");
		//compile("./test_matlab/matlab/elmat");
		compile("./test_matlab/matlab/elmat-test/blkdiag.m");
	}
	
	/**
	 * 
	 * @param path folder or file
	 */
	public static void compile(String path) {
		File folder = new File(path);
		File[] listOfFiles = null;
		if(folder.isDirectory())
			listOfFiles = folder.listFiles();
		else
			listOfFiles = new File[] {folder};
		if(listOfFiles.length == 0) {
			System.out.println("Nothing to compile.");
			return;
		}
		for (int i = 0; i < listOfFiles.length; i++) {
			if (listOfFiles[i].isFile()) {
				System.out.println("File " + listOfFiles[i].getName());
				try {
					StringBuilder sb = new StringBuilder();
					BufferedReader br = new BufferedReader(new FileReader(listOfFiles[i]));
					String line = null;
					while ((line = br.readLine()) != null) {
						sb.append(line).append("\n");
					}
					br.close();
					
					MatlabEngine.exec(sb.toString());
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else if (listOfFiles[i].isDirectory()) {
				System.out.println("Directory " + listOfFiles[i].getName());
			} else {
				System.out.println(listOfFiles[i]+" is not a file or directory!");
			}
		}

	}

}
