/*
 ***************************************************************************
 * Copyright 2003-2005 Luca Passani, passani at eunet.no                   *
 * Distributed under the Mozilla Public License                            *
 *   http://www.mozilla.org/NPL/MPL-1.1.txt                                *
 ***************************************************************************
 *   $Author: passani $
 *   $Header: /cvsroot/wurfl/tools/java/wurflapi-xom/antbuild/src/net/sourceforge/wurfl/wurflapi/ObjectsManager.java,v 1.3 2005/02/13 15:11:39 passani Exp $
 */

package de.schildbach.wurflapi;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashSet;
import java.util.Properties;

/**
 * @author <b>Luca Passani</b>, passani at eunet dot no
 * <br><br>
 * Rather than initializing a new CapabilityMatrix and UAManager
 * each time you need one, you should request ObjectsManager to give 
 * you the instance of an existing one.<br>
 * The advantage of this approach are huge in terms of
 * performance and memory usage, particularly in the contaxt of
 * web applications.
 * 
 */
public class ObjectsManager {
    
    private static Object lock = new Object();
    private static Wurfl wurflInstance = null;
    private static CapabilityMatrix capabilityMatrixInstance = null;
    private static UAManager UAManagerInstance = null;
    private static ListManager ListManagerInstance = null;
    
    /**
     * You are not allowed to manipulate a Wurfl object directly through
     * the published API. This method is left public because it gives you a chance
     * to initialize the WURFL with a <code>wurfl.xml</code> file located in
     * a place where the library does not look at by default.
     * 
     */

    public static Wurfl getWurflInstance(String parameter, String patch) {
	synchronized (lock) {
	    if (wurflInstance==null)
            	wurflInstance = new Wurfl (parameter,patch);
	    return wurflInstance;
	}
    }

    public static Wurfl getWurflInstance(String parameter) {
	synchronized (lock) {
	    if (wurflInstance==null)
            	wurflInstance = new Wurfl (parameter);
	    return wurflInstance;
	}
    }
    
    
    static Wurfl getWurflInstance() {
	synchronized (lock) {
	    if (wurflInstance==null) {
		String param = getWurflFileLocation();	
		wurflInstance = new Wurfl (param);
	    }
            return wurflInstance;
	}
    }
    /*
     LUCA: NOBODY REALLY USED THIS. IT'S CONFUSING TOO. REMOVED
    
     * <b>Experimental!!!</b>
     * This method tells the library to fetch the WURFL off the wurfl
     * website (http://wurfl.sourceforge.net/wurfl.xml).<br>
     * wurfl.xml is a few hundreds kb, so this is not going to be snappy
     * and you are much better off saving a copy of wurfl.xml on
     * your local file system.
     *
    
    public static void wurflWebInit() {
	synchronized (lock) {
	    if (wurflInstance==null)
            	wurflInstance = new Wurfl ("http://wurfl.sourceforge.net/wurfl.xml");
	}
    }

    */
    
    private static String getWurflFileLocation() {
	String param,param2;
	FileInputStream fis;
	
	//have a look at the wurfl.properties file. It may exist
	Properties locations = new Properties(); 
	if (fileExists("wurfl.properties")) {
	    System.out.println("wurfl.properties file found. Lemme have a look...");
	    try {
		fis = new FileInputStream("wurfl.properties"); 
		locations.load(fis);
		fis.close();
	    } 
	    catch (IOException ioe) {
		System.err.println("problems with wurfl.properties");
		ioe.printStackTrace();
		throw new WurflException("Problems with wurfl.properties");
	    } 
	    
	    param = locations.getProperty("wurflpath");
	    if (param == null) {
		System.out.println("Expected wurflpath property not found in wurfl.properties file");
		param = "";
	    }
	    if (param.indexOf("file://") != -1) {
		param2 = param.substring(7,param.length());
	    } else {
		param2 = param;	
	    }
	    //param 2 is used to get rid of 'file://'
	    if (fileExists(param) || fileExists(param2)) {
		System.out.println("using "+param+" file found in wurfl.properties");
		return param;
	    } else {
		System.out.println("file '"+param + "' (found in wurfl.properties)  does not exist");	
	    }
	}
	
	
	System.out.println("Last try. Looking for wurfl.xml in temp directory");
	if (System.getProperty("os.name").indexOf("Windows") != -1) {
	    param = "C:\\temp\\wurfl.xml";
	    if (fileExists(param)) {
		System.out.println(param+" found! I'll use this");
		return param;
	    }
	} else {
	    param = "/tmp/wurfl.xml";	
	    if (fileExists(param)) {
		System.out.println(param+" found! I'll use this");
                return param;
	    }
	}

	String sys_prop = System.getProperty("wurflpath");
	if (sys_prop != null) {
		System.out.println(sys_prop+" found! I'll use this");	    
		return sys_prop;
	}
	
	//no wurfl found!
	System.out.println("WURFL not found anywhere");
	System.out.println("You have 3 possibilities:");
	System.out.println("- define wurfl.properties in the same directory");
	System.out.println("  as your application and provide the wurfl.xml path");
	System.out.println("  ex: wurflpath = file://C:\\projects\\wurfl\\resources\\wurfl.xml");
	System.out.println("");
	System.out.println("- place wurfl.xml in either C:\\temp (Windows) or /tmp (Unix)");
	System.out.println("");
	System.out.println("In a servlet environment, initFromWebApplication() can be used to initialize");
	System.out.println("using the wurfl at /WEB-INF/wurfl.xml");
	System.out.println("");

	System.out.println("- the API will also look at the 'wurflpath' System property");
	System.out.println("");
	
	throw new WurflException("Cannot find WURFL file (wurfl.xml)");
    }
    
    //tiny utility to see if file exists 
    private static boolean fileExists(String path) {
	
	File file = new File(path);
	return file.exists ();
    }
    
    /** 
     * Use this method to retrieve the existing instance of the CapabilityMatrix
     * (or get one initialized for you). Similar to a Singleton in a way.
     */
    
    public static CapabilityMatrix getCapabilityMatrixInstance() {
	synchronized (lock) {
	    if (wurflInstance==null)
            	wurflInstance = new Wurfl (getWurflFileLocation());
	    if (capabilityMatrixInstance==null)
		capabilityMatrixInstance = new CapabilityMatrix(wurflInstance);
	    return capabilityMatrixInstance;
	}
    }
    
    /** 
     * Use this method to retrieve the existing instance of the UAManager
     * (or get one initialized for you). 
     */
    
    public static UAManager getUAManagerInstance() {
	synchronized (lock) {
	    	if (wurflInstance==null)
		    wurflInstance = new Wurfl (getWurflFileLocation());
		if (UAManagerInstance==null)
		    UAManagerInstance = new UAManager(wurflInstance);
            	return UAManagerInstance;
	}
    }

    
    /** 
     * Use this method to retrieve the existing instance of the ListManager
     * (or get one initialized for you). 
     */
    
    public static ListManager getListManagerInstance() {
	synchronized (lock) {
	    if (wurflInstance==null)
		wurflInstance = new Wurfl (getWurflFileLocation());
	    if (ListManagerInstance==null)
		ListManagerInstance = new ListManager(wurflInstance);
	    return ListManagerInstance;
	}
    }
    

    /** 
     * Use this method to understand if the WURFL is already initialized
     * or not
     */
    
    public static boolean isWurflInitialized() {
	synchronized (lock) {
	    if (wurflInstance==null) {
		return false;
	    } else {
		return true;
	    }
	}
    }
  
    /** 
     * Get an XMLized version of the WURFL (WURFL+patch Object Model turned into an XML file)
     */
    
    public static String getWURFLAsXML() {
	synchronized (lock) {
	    if (wurflInstance==null) {
		return "";
	    } else {
		return wurflInstance.toXML();
	    }
	}
    }

    /** 
     * Use this method to initialize if you have your WURFL in unusual places
     */
    
    public static void initFromWebApplication(String path) {
	
	synchronized (lock) {
	    if (wurflInstance==null) {
		File file = new File(path);
		if (file.exists()) {
		    wurflInstance = new Wurfl(path);
		    System.out.println("Initializing web-app with "+path);
		} else {
		    System.out.println("WARNING: initialization failed. Could not find a "+
				       "WURFL file at "+path);
		}
	    }
	}
    }

    /** 
     * Use this method to initialize if you have your WURFL in unusual places
     */
    
    public static void initFromWebApplication(String path, String pathToPatch) {
	
	synchronized (lock) {
	    if (wurflInstance==null) {
		File file = new File(path);
		if (file.exists()) {
		    File file2 = new File(pathToPatch);
		    if (file2.exists()) {
			wurflInstance = new Wurfl(path,pathToPatch);
			System.out.println("Initializing web-app with "+ path +
					   " and "+ pathToPatch);
		    } else{ 
			wurflInstance = new Wurfl(path);
			System.out.println("Initializing web-app with "+path);
			//System.out.println("WARNING: patch file not found at "+pathToPatch);
		    }
		} else {
		    System.out.println("WARNING: initialization failed. Could not find a "+
				       "WURFL file at "+path);
		}
	    }
	}
    }


    /** 
     * This method lets you initialize the WURFL by providing
     * an object which knows how to get to the input streams
     */
    
    public static void initMyWay(WurflSource ws) {
	
	synchronized (lock) {
	    if (wurflInstance==null) {

		InputStream in1 = ws.getWurflInputStream();
		InputStream in2 = ws.getWurflPatchInputStream();
		if (in1 != null) {
		    System.out.println("Initializing web-app from stream with InputStream.");
		    wurflInstance = new Wurfl(in1,in2);			
		} else {
			System.out.println("initMyWay(WurflSource): "+
					   "\nCannot initialize Wurfl. InputStream is empty!");
		}
		
	    } else {
		System.out.println("WARNING: initMyWay() failed. Wurfl was already initialized ");
	    }
	}
    }

    public static void getFilteredWurfl(HashSet capaList, OutputStream out) {

	    if (wurflInstance==null) {
		return;
	    } else {
		wurflInstance.filterCapabilities(capaList, out);
	    }
    }

    /** 
     * Use this method to force the library to reload the WURFL again
     */
    
    public static void resetWurfl() {
	synchronized (lock) {
	    wurflInstance = null; 
	    capabilityMatrixInstance = null;
	    UAManagerInstance = null;
	    ListManagerInstance = null;
	}
	System.gc();
    }

      
        

}
