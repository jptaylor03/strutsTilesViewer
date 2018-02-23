/*
 * @(#)URLClassPath.java	1.4 05/11/17
 * 
 * Copyright 2005 Sun Microsystems, Inc. All rights reserved. 
 * Use is subject to license terms.
 * 
 */

package util.classloading;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.JarURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;
import java.net.URLStreamHandlerFactory;
import java.security.AccessControlException;
import java.security.Permission;
import java.security.cert.Certificate;
import java.util.ArrayList;
import java.util.EmptyStackException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.Stack;
import java.util.StringTokenizer;
import java.util.jar.Attributes;
import java.util.jar.Attributes.Name;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

import sun.misc.ExtensionDependency;
import sun.misc.InvalidJarIndexException;
import sun.misc.JarIndex;
import sun.misc.Resource;

/**
 * This class is used to maintain a search path of URLs for loading classes
 * and resources from both JAR files and directories.
 *
 * @author  David Connelly
 * @version 1.62, 03/09/00
 */
public class MyURLClassPath {
	final static String USER_AGENT_JAVA_VERSION = "UA-Java-Version";
	final static String JAVA_VERSION;

	static {
		JAVA_VERSION = (String) java.security.AccessController.doPrivileged(
				new sun.security.action.GetPropertyAction("java.version"));
	}

	/* The original search path of URLs. */
	private ArrayList path = new ArrayList();

	/* The stack of unopened URLs */
	private Stack urls = new Stack();

	/* The resulting search path of Loaders */
	private ArrayList loaders = new ArrayList();

	/* Map of each URL opened to its corresponding Loader */
	private HashMap lmap = new HashMap();

	/* The jar protocol handler to use when creating new URLs */
	private URLStreamHandler jarHandler;

	/**
	 * Creates a new URLClassPath for the given URLs. The URLs will be
	 * searched in the order specified for classes and resources. A URL
	 * ending with a '/' is assumed to refer to a directory. Otherwise,
	 * the URL is assumed to refer to a JAR file.
	 *
	 * @param urls the directory and JAR file URLs to search for classes
	 *        and resources
	 * @param factory the URLStreamHandlerFactory to use when creating new URLs
	 */
	public MyURLClassPath(URL[] urls, URLStreamHandlerFactory factory) {
		for (int i = 0; i < urls.length; i++) {
			path.add(urls[i]);
		}
		push(urls);
		if (factory != null) {
			jarHandler = factory.createURLStreamHandler("jar");
		}
	}

	public MyURLClassPath(URL[] urls) {
		this(urls, null);
	}

	/**
	 * Appends the specified URL to the search path of directory and JAR
	 * file URLs from which to load classes and resources.
	 */
	public void addURL(URL url) {
		synchronized (urls) {
			urls.add(0, url);
			path.add(url);
		}
	}

	/**
	 * Returns the original search path of URLs.
	 */
	public URL[] getURLs() {
		synchronized (urls) {
			return (URL[])path.toArray(new URL[path.size()]);
		}
	}

	/**
	 * Finds the first Resource on the URL search path which has the specified
	 * name. Returns null if no Resource could be found.
	 *
	 * @param name the name of the Resource
	 * @return the Resource, or null if not found
	 */
	public Resource getResource(String name, boolean check) {
		Loader loader;
		for (int i = 0; (loader = getLoader(i)) != null; i++) {
			Resource res = loader.getResource(name, check);
			if (res != null) {
				return res;
			}
		}
		return null;
	}

	public Resource getResource(String name) {
		return getResource(name, true);
	}

	/**
	 * Finds all resources on the URL search path with the given name.
	 * Returns an enumeration of the Resource objects.
	 *
	 * @param name the resource name
	 * @return an Enumeration of all the resources having the specified name
	 */
	public Enumeration getResources(final String name, 
			final boolean check) {
		return new Enumeration() {
			private int index = 0;
			private Resource res = null;

			private boolean next() {
				if (res != null) {
					return true;
				} else {
					Loader loader;
					while ((loader = getLoader(index++)) != null) {
						res = loader.getResource(name, check);
						if (res != null) {
							return true;
						}
					}
					return false;
				}
			}

			public boolean hasMoreElements() {
				return next();
			}

			public Object nextElement() {
				if (!next()) {
					throw new NoSuchElementException();
				}
				Resource r = res;
				res = null;
				return r;
			}
		};
	}	 

	public Enumeration getResources(final String name) {
		return getResources(name, true);
	}

	/*
	 * Returns the Loader at the specified position in the URL search
	 * path. The URLs are opened and expanded as needed. Returns null
	 * if the specified index is out of range.
	 */
	private synchronized Loader getLoader(int index) {
		// Expand URL search path until the request can be satisfied
		// or the URL stack is empty.
		while (loaders.size() < index + 1) {
			// Pop the next URL from the URL stack
			URL url;
			try {
				synchronized (urls) {
					url = (URL)urls.pop();
				}
			} catch (EmptyStackException e) {
				return null;
			}
			// Skip this URL if it already has a Loader. (Loader
			// may be null in the case where URL has not been opened
			// but is referenced by a JAR index.)
			if (lmap.containsKey(url)) {
				continue;
			}
			// Otherwise, create a new Loader for the URL.
			Loader loader;
			try {
				loader = getLoader(url);
				// If the loader defines a local class path then add the
				// URLs to the list of URLs to be opened.
				URL[] urls = loader.getClassPath();
				if (urls != null) {
					push(urls);
				}
			} catch (IOException e) {
				// Silently ignore for now...
				continue;
			}
			// Finally, add the Loader to the search path.
			loaders.add(loader);
			lmap.put(url, loader);
		}
		return (Loader)loaders.get(index);
	}

	/*
	 * Returns the Loader for the specified base URL.
	 */
	private Loader getLoader(final URL url) throws IOException {
		try {
			return (Loader)java.security.AccessController.doPrivileged
			(new java.security.PrivilegedExceptionAction() {
				public Object run() throws IOException {
					String file = url.getFile();
					if (file != null && file.endsWith("/")) {
						if ("file".equals(url.getProtocol())) {
							return new FileLoader(url);
						} else {
							return new Loader(url);
						}
					} else {
						return new JarLoader(url, jarHandler, lmap);
					}
				}
			});
		} catch (java.security.PrivilegedActionException pae) {
			throw (IOException)pae.getException();
		}
	}

	/*
	 * Pushes the specified URLs onto the list of unopened URLs.
	 */
	private void push(URL[] us) {
		synchronized (urls) {
			for (int i = us.length - 1; i >= 0; --i) {
				urls.push(us[i]);
			}
		}
	}

	/**
	 * Convert class path specification into an array of file URLs.
	 */
	public static URL[] pathToURLs(String path) {
		StringTokenizer st = new StringTokenizer(path, File.pathSeparator);
		URL[] urls = new URL[st.countTokens()];
		int count = 0;
		while (st.hasMoreTokens()) {
			try {
				File nonCanonFile = new File(st.nextToken());
				urls[count++] = nonCanonFile.toURL();

				// try to canonicalize the filename
				urls[count - 1] =
					new File(nonCanonFile.getCanonicalPath()).toURL();
			} catch (IOException ioe) {
				// use the non-canonicalized filename
			}
		}
		if (urls.length != count) {
			URL[] tmp = new URL[count];
			System.arraycopy(urls, 0, tmp, 0, count);
			urls = tmp;
		}
		return urls;
	}

	/*
	 * Check whether the resource URL should be returned.
	 * Return null on security check failure.
	 * Called by java.net.URLClassLoader.
	 */
	public URL checkURL(URL url) {
		try {
			check(url);
		} catch (Exception e) {
			return null;
		}

		return url;
	}

	/*
	 * Check whether the resource URL should be returned.
	 * Throw exception on failure.
	 * Called internally within this file.
	 */
	static void check(URL url) throws IOException {
		SecurityManager security = System.getSecurityManager();
		if (security != null) {
			Permission perm = url.openConnection().getPermission();
			if (perm != null) {
				try {
					security.checkPermission(perm);
				} catch (SecurityException se) {
					// fallback to checkRead/checkConnect for pre 1.2
					// security managers
					if ((perm instanceof java.io.FilePermission) &&
							perm.getActions().indexOf("read") != -1) {
						security.checkRead(perm.getName());
					} else if ((perm instanceof 
							java.net.SocketPermission) &&
							perm.getActions().indexOf("connect") != -1) {
						security.checkConnect(url.getHost(),
								url.getPort());
					} else {
						throw se;
					}
				}
			}
		}
	}

	/**
	 * Inner class used to represent a loader of resources and classes
	 * from a base URL.
	 */
	private static class Loader {
		private final URL base;

		/*
		 * Creates a new Loader for the specified URL.
		 */
		Loader(URL url) {
			base = url;
		}

		/*
		 * Returns the base URL for this Loader.
		 */
		URL getBaseURL() {
			return base;
		}

		Resource getResource(final String name, boolean check) {
			final URL url;
			try {
				url = new URL(base, name);
			} catch (MalformedURLException e) {
				throw new IllegalArgumentException("name");
			}
			final URLConnection uc;
			try {
				if (check) {
					MyURLClassPath.check(url);
				}

				// check to see if it exists
				//
				uc = url.openConnection();
				MyURLClassLoader.disableUrlConnectionCaching(uc);

				// our best guess for the other cases
				InputStream is = url.openStream();
				is.close();

			} catch (Exception e) {
				return null;
			}
			return new Resource() {
				public String getName() { return name; }
				public URL getURL() { return url; }
				public URL getCodeSourceURL() { return base; }
				public InputStream getInputStream() throws IOException {
					return uc.getInputStream();
				}
				public int getContentLength() throws IOException {
					return uc.getContentLength();
				}
			};
		}

		/*
		 * Returns the Resource for the specified name, or null if not
		 * found or the caller does not have the permission to get the
		 * resource.
		 */
		Resource getResource(final String name) {
			return getResource(name, true);
		}

		/*
		 * Returns the local class path for this loader, or null if none.
		 */
		URL[] getClassPath() throws IOException {
			return null;
		}
	}

	/*
	 * Inner class used to represent a Loader of resources from a JAR URL.
	 */
	private static class JarLoader extends Loader {
		private JarFile jar;
		private URL csu;
		private JarIndex index;
		private URLStreamHandler handler;
		private HashMap lmap;

		/*
		 * Creates a new JarLoader for the specified URL referring to
		 * a JAR file.
		 */
		JarLoader(URL url, URLStreamHandler jarHandler, HashMap loaderMap) 
		throws IOException 
		{
			super(new URL("jar", "", -1, url + "!/", jarHandler));
			jar = getJarFile(url);
			index = JarIndex.getJarIndex(jar);
			csu = url;
			handler = jarHandler;
			lmap = loaderMap;
			if (index != null) {
				String[] jarfiles = index.getJarFiles();
				// Add all the dependent URLs to the lmap so that loaders
				// will not be created for them by URLClassPath.getLoader(int)
				// if the same URL occurs later on the main class path.  We set 
				// Loader to null here to avoid creating a Loader for each 
				// URL until we actually need to try to load something from them.
				for(int i = 0; i < jarfiles.length; i++) {
					try {
						URL jarURL = new URL(csu, jarfiles[i]);
						lmap.put(jarURL, null);
					} catch (MalformedURLException e) {
						continue;
					}
				}
			}
		}

		private JarFile getJarFile(URL url) throws IOException {
			// Optimize case where url refers to a local jar file
			if ("file".equals(url.getProtocol())) {
				String path = url.getFile().replace('/', File.separatorChar);
				File file = new File(path);
				if (!file.exists()) {
					throw new FileNotFoundException(path);
				}
				return new JarFile(path);
			}
			URLConnection uc = getBaseURL().openConnection();
			MyURLClassLoader.disableUrlConnectionCaching(uc);
			uc.setRequestProperty(USER_AGENT_JAVA_VERSION, JAVA_VERSION);
			return ((JarURLConnection)uc).getJarFile();
		}

		/*
		 * Returns the index of this JarLoader if it exists.
		 */
		JarIndex getIndex() {
			return index;
		}

		/*
		 * Returns the JAR Resource for the specified name.
		 */
		Resource getResource(final String name, boolean check) {
			final JarEntry entry = jar.getJarEntry(name);
			Resource res;

			if (entry != null) {
				final URL url;
				try {
					url = new URL(getBaseURL(), name);
					if (check) {
						MyURLClassPath.check(url);
					}
				} catch (MalformedURLException e) {
					return null;
					// throw new IllegalArgumentException("name");
				} catch (IOException e) {
					return null;
				} catch (AccessControlException e) {
					return null;
				}

				return new Resource() {
					public String getName() { return name; }
					public URL getURL() { return url; }
					public URL getCodeSourceURL() { return csu; }
					public InputStream getInputStream() throws IOException
					{ return jar.getInputStream(entry); }
					public int getContentLength()
					{ return (int)entry.getSize(); }
					public Manifest getManifest() throws IOException
					{ return jar.getManifest(); };
					public Certificate[] getCertificates()
					{ return entry.getCertificates(); };
				};
			} else if (index != null) {
				LinkedList jarFiles;

				if ((jarFiles = index.get(name)) == null) {
					/* not found in index */
					return null;
				}

				/* loop through the mapped jar file list */
				Iterator itr = jarFiles.iterator();
				while(itr.hasNext()) {
					String jar = (String)itr.next();
					Loader newLoader;

					try{
						final URL url = new URL(csu, jar);
						if ((newLoader = (Loader)lmap.get(url)) == null) {
							/* no loader has been set up for this jar file before */
							newLoader = (Loader)java.security.AccessController.doPrivileged
							(new java.security.PrivilegedExceptionAction() {
								public Object run() throws IOException {
									return new JarLoader(url, handler, lmap);
								}
							});

							/* this newly opened jar file has its own index,
							 * merge it into the parent's index, taking into
							 * account the relative path.
							 */
							JarIndex newIndex = ((JarLoader)newLoader).getIndex();
							if(newIndex != null) {
								int pos = jar.lastIndexOf("/");
								newIndex.merge(this.index, (pos == -1 ? 
										null : jar.substring(0, pos + 1)));
							}

							/* put it in the global hashtable */
							lmap.put(url, newLoader);
						}
					} catch (java.security.PrivilegedActionException pae) {
						continue;
					} catch (MalformedURLException e) {
						continue;
					}

					/* if newLoader is myself, I have already searched, 
					 * then go to the next loader
					 */
					if (newLoader == this ) {
						continue;
					}

					if((res = newLoader.getResource(name, check)) != null) {
						return res;
					}
				}

				/* the mapping is wrong */
				throw new InvalidJarIndexException("Invalid index!");
			}
			return null;
		}


		/*
		 * Returns the JAR file local class path, or null if none.
		 */
		URL[] getClassPath() throws IOException {
			if (index != null) {
				return null;
			}

			parseExtensionsDependencies();
			Manifest man = jar.getManifest();
			if (man != null) {
				Attributes attr = man.getMainAttributes();
				if (attr != null) {
					String value = attr.getValue(Name.CLASS_PATH);
					if (value != null) {
						return parseClassPath(csu, value);
					}
				}
			}
			return null;
		}

		/*
		 * parse the standard extension dependencies
		 */
		private void  parseExtensionsDependencies() throws IOException {
			ExtensionDependency.checkExtensionsDependencies(jar);
		}

		/*
		 * Parses value of the Class-Path manifest attribute and returns
		 * an array of URLs relative to the specified base URL.
		 */
		private URL[] parseClassPath(URL base, String value)
		throws MalformedURLException
		{
			StringTokenizer st = new StringTokenizer(value);
			URL[] urls = new URL[st.countTokens()];
			int i = 0;
			while (st.hasMoreTokens()) {
				String path = st.nextToken();
				urls[i] = new URL(base, path);
				i++;
			}
			return urls;
		}
	}

	/*
	 * Inner class used to represent a loader of classes and resources
	 * from a file URL that refers to a directory.
	 */
	private static class FileLoader extends Loader {
		private File dir;

		FileLoader(URL url) throws IOException {
			super(url);
			if (!"file".equals(url.getProtocol())) {
				throw new IllegalArgumentException("url");
			}
			dir = new File(url.getFile().replace('/', File.separatorChar));
		}

		Resource getResource(final String name, boolean check) {
			final URL url;
			try {
				url = new URL(getBaseURL(), name);

				if (url.getFile().startsWith(getBaseURL().getFile()) == false) {
					// requested resource had ../..'s in path
					return null;
				}

				if (check)
					MyURLClassPath.check(url);
				final File file =
					new File(dir, name.replace('/', File.separatorChar));
				if (file.exists()) {
					return new Resource() {
						public String getName() { return name; };
						public URL getURL() { return url; };
						public URL getCodeSourceURL() { return getBaseURL(); };
						public InputStream getInputStream() throws IOException
						{ return new FileInputStream(file); };
						public int getContentLength() throws IOException
						{ return (int)file.length(); };
					};
				}
			} catch (Exception e) {
				return null;
			}
			return null;
		}
	}
	
	/**
	 * Simulate findResource() method by deferring to {@link #getResource(String)} method.
	 * <p>
	 *  NOTE: This method was added after overriding <code>sun.misc.URLClassPath</code>.
	 * </p>
	 * 
	 * @param name String containing the name of the resource.
	 * @return the Resource, or null if not found.
	 * @see #getResource(String)
	 */
	public Resource findResource(String name) {
		return this.getResource(name);
	}
	/**
	 * Simulate findResource() method by deferring to {@link #getResource(String, boolean)} method.
	 * <p>
	 *  NOTE: This method was added after overriding <code>sun.misc.URLClassPath</code>.
	 * </p>
	 * 
	 * @param name String containing the name of the resource.
	 * @param check Boolean
	 * @return the Resource, or null if not found.
	 * @see #getResource(String, boolean)
	 */
	public Resource findResource(String name, boolean check) {
		return this.getResource(name, check);
	}
	/**
	 * Simulate findResources() method by deferring to {@link #getResources(String)} method.
	 * <p>
	 *  NOTE: This method was added after overriding <code>sun.misc.URLClassPath</code>.
	 * </p>
	 * 
	 * @param name String containing the resource name.
	 * @return an Enumeration of all the resources having the specified name.
	 * @see #getResources(String)
	 */
	public Enumeration findResources(String name) {
		return this.getResources(name);
	}
	/**
	 * Simulate findResources() method by deferring to {@link #getResources(String, boolean)} method.
	 * <p>
	 *  NOTE: This method was added after overriding <code>sun.misc.URLClassPath</code>.
	 * </p>
	 * 
	 * @param name String containing the resource name.
	 * @param check Boolean
	 * @return an Enumeration of all the resources having the specified name.
	 * @see #getResources(String, boolean)
	 */
	public Enumeration findResources(String name, boolean check) {
		return this.getResources(name, check);
	}

}
