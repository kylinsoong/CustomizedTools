package com.customized.tools;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.log4j.Logger;

import com.customized.tools.smartanalyser.Constants;
import com.customized.tools.smartanalyser.GCLogAnalyser;
import com.customized.tools.smartanalyser.HeapDumpAnalyser;
import com.customized.tools.smartanalyser.IAnalyser;
import com.customized.tools.smartanalyser.JBossLogConfAnalyser;
import com.customized.tools.smartanalyser.SmartAnalyserException;
import com.customized.tools.smartanalyser.ThreadDumpAnalyser;
import com.customized.tools.smartanalyser.status.SmartAnalyserStatusException;
import com.customized.tools.startup.ToolsConsole;
import com.customized.tools.startup.ToolsProperties;

public class SmartAnalyser extends AbstractTools {
	
	private static final Logger logger = Logger.getLogger(SmartAnalyser.class);

	public SmartAnalyser(ToolsProperties props, ToolsConsole console) {
		super(props, console);
	}
	

	public void execute() throws Throwable {
		
		logger.info("Start SmartAnalyser...");
		
		int status = getAnalyerStatus();
		
		IAnalyser analyser = null;
		
		try {
			switch (status) {
			case 1:
				analyser = new JBossLogConfAnalyser(1, props, console);
				break;
			case 2:
				analyser = new GCLogAnalyser(2, props, console);
				break;
			case 3:
				analyser = new ThreadDumpAnalyser(3, props, console);
				break;
			case 4:
				analyser = new HeapDumpAnalyser(4, props, console);
				break;
			default:
				break;
			}
			
			if(analyser != null) {
				analyser.analyser();
			} else {
				throw new SmartAnalyserStatusException("get Analyer Status return a error status code");
			}
			
		} catch (Exception e) {

			SmartAnalyserException ex = new SmartAnalyserException("Analyser returned a unexpected Exception ", e);
			
			console.prompt(ex.getMessage());
			
			throw ex;
		}
	}

	private int getAnalyerStatus() throws Exception {
		
		validateImg(props.getProperty("analyser.img.folder", true));
		
		for(Iterator iterator = imgSet.iterator(); iterator.hasNext();) {
			String path =  (String) iterator.next();
			
			if(isJBossLogConf(path)) {
				return Constants.STATUS_JBOSS_LOG_CONF;
			} else if(isGClog(path)) {
				return Constants.STATUS_GC_LOG;
			} else if(isThreadDump(path)) {
				return Constants.STATUS_THREAD_DUMP;
			} else if(isHeapDump(path)) {
				return Constants.STATUS_HEAP_DUMP;
			}
		}
		
		throw new SmartAnalyserStatusException("Get analyser code error");
	}


	private boolean isJBossLogConf(String path) {
		return path.contains("boot.log") || path.contains("server.log");
	}


	private boolean isGClog(String path) {
		// TODO Auto-generated method stub
		return false;
	}


	private boolean isThreadDump(String path) {
		// TODO Auto-generated method stub
		return false;
	}


	private boolean isHeapDump(String path) {
		// TODO Auto-generated method stub
		return false;
	}


	private void validateImg(String imgPath) throws Exception {
		
		File file = new File(imgPath);

		if(!(file.exists())) {
			throw new SmartAnalyserStatusException("image folder does not exist");
		}
		
		if(!(file.isDirectory())) {
			throw new SmartAnalyserStatusException("image folder is not a folder");
		}
		
		unzipFile(file);
		
		addToSet(file);
	}

	Set<String> imgSet = new HashSet<String>();

	private void addToSet(File file) {
		
		for(File f : file.listFiles()) {
			if(invalidImg(f)) {
				imgSet.add(f.getAbsolutePath());
			}
		}
	}


	private boolean invalidImg(File f) {
		
		String path = f.getAbsolutePath();
		return !path.endsWith(".zip");
	}


	private void unzipFile(File file) throws Exception {
		for(File f : file.listFiles()){
			if(f.isDirectory()) {
				unzipFile(f);
			} else if(f.getAbsolutePath().endsWith(".zip")) {
				unzip(f);
			}
		}
	}


	private void unzip(File f) throws Exception {
		
		console.prompt("\n  unzip file " + f + "\n");
		
		byte[] buf = new byte[1024];
	    ZipInputStream zinstream = new ZipInputStream(new FileInputStream(f));
	    ZipEntry zentry = zinstream.getNextEntry();
	   logger.debug("Name of current Zip Entry : " + zentry);
	    while (zentry != null) {
	      String entryName = zentry.getName();
	      logger.debug("Name of  Zip Entry : " + entryName);
	      FileOutputStream outstream = new FileOutputStream(new File(f.getParentFile(), entryName));
	      int n;

	      while ((n = zinstream.read(buf, 0, 1024)) > -1) {
	        outstream.write(buf, 0, n);

	      }
	      logger.debug("Successfully Extracted File Name : " + entryName);
	      outstream.close();

	      zinstream.closeEntry();
	      zentry = zinstream.getNextEntry();
	    }
	    zinstream.close();
	}

}
