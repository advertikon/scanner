/*
 * Class wich makes package
 */
package ua.com.advertikon.crawler;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveAction;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import javafx.scene.control.Spinner;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 *
 * @author Advertikon
 */
public class Packager {
    public static final String TMP_DIR     = ".tmp_pckg/upload";
	public static final String STORAGE_DIR = "/var/www/html/crawler/";
    protected ArrayList<Path> mFiles;
    protected String mCode;
	protected String mRealCode;
	protected String mPhpLintError;
	protected List<Path> mSyncedList;
	
	protected Spinner mVersionMajor;
	protected Spinner mVersionMinor;
	protected Spinner mVersionPatch;
	
	protected String mVersion;
    
    public Packager( ArrayList files, String code, Spinner versionMajor, Spinner versionMinor, Spinner versionPatch ) {
        mFiles = files;
        mCode = code;
		mVersionMajor = versionMajor;
		mVersionMinor = versionMinor;
		mVersionPatch = versionPatch;
		
		int pos = code.indexOf( '#' );
		
		if ( pos > 0 ) {
			mRealCode = code.substring( 0, pos );

		} else {
			mRealCode = code;
		}
    }
    
	/**
	 * Main method which does the magic
	 * @throws IOException
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws TransformerException
	 * @throws TransformerConfigurationException
	 * @throws CrawlerException
	 * @throws InterruptedException
	 * @throws Exception 
	 */
    public void runIt() throws IOException, ParserConfigurationException, SAXException,
            TransformerException, TransformerConfigurationException, CrawlerException,
			InterruptedException, Exception {
		mVersion = guesVersion();

        createV23();
        makeVQMod();
        addReadme();
        addTranslate();
		addVersion();
		phpLintAll(); // for vesion 2.3 - 3+
		zip( mCode + "-OC-23-3+-" + mVersion + ".ocmod.zip" );
		createV20();
		phpLintAll(); // for version 2.0 - 2.2
		zip( mCode + "-OC-20-22-" + mVersion + ".ocmod.zip" );
		
		System.out.println( ">>>>>>>>>>>>>>>>>>>>>>End<<<<<<<<<<<<<<<<<<<<<<" );
    }
    
	/**
	 * Creates package structure for OC2.3+ under {@link #STORAGE_DIR}
	 * @throws IOException 
	 */
    protected void createV23() throws IOException {
        Profiler.record( "Create V23" );
        Path tmp = Paths.get( TMP_DIR );
        cleanUpDir( tmp.getParent() );
 
        for( Path path: mFiles ) {
            if ( isOCMod( path ) ) {
                continue;
            }

            Path target = tmp.resolve( path );
            Files.createDirectories( target.getParent() );
            Files.copy( path, target, StandardCopyOption.REPLACE_EXISTING );
        }
        
        Profiler.record( "Create V23" );
    }
    
    /**
     * Recursively deletes directory
     * @param path Target folder
     * @throws IOException 
     */
    protected void cleanUpDir( Path path ) throws IOException {
        if ( !Files.exists( path ) ) {
            System.out.println( String.format( "File %s doesn't exist. Skip directory clean up", path.toAbsolutePath().toString() ) );
            return;
        }
 
        CleanVisitor visitor = new CleanVisitor();
        Files.walkFileTree( path, visitor );
    }
    
	/**
	 * Adds README file to package structure
	 * @throws IOException 
	 */
    protected void addReadme() throws IOException {
        Profiler.record( "Readme" );
    
        for( Path path: mFiles ) {
            if ( path.getFileName().toString().equals( mRealCode + "_readme" ) ) {
                Files.copy( path, Paths.get( TMP_DIR ).getParent().resolve( Paths.get( "README.TXT" ) ) );
                break;
            }
        }
        
        Profiler.record( "Readme" );
    }
    
	/**
	 * Puts modification files into the root of package structure
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 * @throws TransformerException
	 * @throws TransformerConfigurationException
	 * @throws CrawlerException
	 * @throws InterruptedException 
	 */
    private void makeVQMod() throws ParserConfigurationException, SAXException,
            IOException, TransformerException, TransformerConfigurationException, CrawlerException, InterruptedException {
        Profiler.record( "VQMOD" );
        for( Path path: mFiles ) {
            if( !isOCMod( path ) ) {
                continue;
            } 
            
            vqmoFromOcmod( path );
        }
        
       Profiler.record( "VQMOD" );
    }
    
	/**
	 * Checks if file is OCMOD file
	 * @param path Target file
	 * @return 
	 */
    protected boolean isOCMod( Path path ) {
        return path.toString().endsWith( "/system/" + mRealCode + ".ocmod.xml" );
    }
    
	/**
	 * Creates VQMOD from OCMOD, XML lint them and put into the root of
	 * package structure
	 * @param path OCMOD file
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 * @throws TransformerConfigurationException
	 * @throws TransformerException
	 * @throws CrawlerException
	 * @throws InterruptedException 
	 */
    protected void vqmoFromOcmod( Path path ) throws ParserConfigurationException,
            SAXException, IOException, TransformerConfigurationException, TransformerException, CrawlerException, InterruptedException {

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document ocmod = builder.parse( path.toFile() );
        Document vqmod = builder.newDocument();
        Element vqmodRoot = vqmod.createElement( "modification" );
        vqmod.appendChild( vqmodRoot );
        Element ocmodRoot = ocmod.getDocumentElement();
        NodeList children = ocmodRoot.getChildNodes();
        Element tmp;
        
        for( int i = 0; i < children.getLength(); i++ ) {
            if ( !( children.item( i ) instanceof Element ) ) {
                continue;
            }

            Element child = (Element) children.item( i );

            switch ( child.getTagName() ) {
                case "file":
                    tmp = fixVQMODFile( ( Element )vqmod.adoptNode( child.cloneNode( true  ) ) );
                    vqmodRoot.appendChild( tmp );
                    break;
                case "name":
                    tmp = vqmod.createElement( "id" );
                    tmp.setTextContent( child.getTextContent() );
                    vqmodRoot.appendChild( tmp );
                    break;
                case "link":
                case "code":
                    break;
                case "version":
                    child.setTextContent( mVersion );
                    default:
                        vqmodRoot.appendChild( vqmod.adoptNode( child.cloneNode(true) ) );
                    break;
            }
        }
        
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        Path packageRoot = Paths.get( TMP_DIR ).getParent();
        
        // OCMOD for extension installer and for FTP installation
        DOMSource ocmodSource = new DOMSource( ocmod );
        
        Path indexPath = Paths.get( packageRoot.toString(), "install.xml" );
        StreamResult result1 = new StreamResult( indexPath.toFile() );
        transformer.transform( ocmodSource, result1 );

        StreamResult result2 = new StreamResult( Paths.get( packageRoot.toString(), mRealCode + ".ocmod.xml" ).toFile() );
        transformer.transform( ocmodSource, result2 );
        
        // VQMOD
        Path vqmodPath = Paths.get( packageRoot.toString(), mRealCode + ".vqmod.xml" );
        DOMSource vqmodSource = new DOMSource( vqmod );
        StreamResult result3 = new StreamResult( vqmodPath.toFile() );
        transformer.transform( vqmodSource, result3 );
        
        if ( hasXmlLinter() ) {
            xmlLint( indexPath );
            xmlLint( vqmodPath );
        }
    }
    
	/**
	 * Fixes differences between OCMOD and VQMOD <b>file</b> entry
	 * @param element File XML Element of VQMOD file
	 * @return 
	 */
    protected Element fixVQMODFile( Element element ) {
        // path => name
        element.setAttribute( "name", element.getAttribute( "path" ) );
        element.removeAttributeNode( element.getAttributeNode( "path" ) );
        
        // Position attr from add to search
        Element search = (Element) element.getElementsByTagName( "search" ).item( 0 );
        Element add    = (Element) element.getElementsByTagName( "add" ).item( 0 );
        
        if ( add.hasAttribute( "position" ) ) {
            search.setAttribute( "position", add.getAttribute( "position" ) );
            add.removeAttributeNode( add.getAttributeNode( "position" ) );
        }
        
        return element;
    }
    
	/**
	 * Checks if system has xmllinter installed
	 * @return
	 * @throws InterruptedException 
	 */
    protected boolean hasXmlLinter() throws InterruptedException {
        Process p;
        try {
            p = Runtime.getRuntime().exec( "which xmllint" );
  
        } catch (IOException ex) {
            Logger.getLogger( Packager.class.getName()).log(Level.SEVERE, null, ex );
            return false;
        }
        
        return p != null && p.waitFor() == 0;
    }
    
	/**
	 * Runs xmllinter on target file
	 * @param path Target file
	 * @throws IOException
	 * @throws CrawlerException
	 * @throws InterruptedException 
	 */
    protected void xmlLint( Path path ) throws IOException, CrawlerException, InterruptedException {
		Profiler.record( "XML Lint" );
        String[] args = { "xmllint", path.toString() };
        Process p = Runtime.getRuntime().exec( args );
		Profiler.record( "XML Lint" );
  
        if ( p.waitFor() != 0 ) {
            throw new CrawlerException( String.format( "File %s has invalid XML markup", path.toString() ) );
        }
    }
	
	/**
	 * Checks if system has PHP installed
	 * @return
	 * @throws InterruptedException 
	 */
	protected boolean hasPHPLinter() throws InterruptedException {
        Process p;

        try {
            p = Runtime.getRuntime().exec( "which php" );
  
        } catch (IOException ex) {
            Logger.getLogger( Packager.class.getName()).log(Level.SEVERE, null, ex );
            return false;
        }
        
        return p != null && p.waitFor() == 0;
    }
	
	/**
	 * Runs php linter on all the files under {@link #TMP_DIR}
	 * @throws IOException
	 * @throws CrawlerException
	 * @throws InterruptedException 
	 */
	protected void phpLintAll() throws IOException, CrawlerException, InterruptedException {
		Profiler.record( "PHP lint" );

		mPhpLintError = null;
		ArrayList<Thread> threads = new ArrayList<>();
		List<Path> list = Files.walk( Paths.get( TMP_DIR ) ).filter( p -> p.toString().endsWith( ".php" ) ).collect( Collectors.toList() );
		ForkJoinPool.commonPool().invoke( new PHPLinterThread( list ) );
		
		Profiler.record( "PHP lint" );
		
		if ( mPhpLintError != null ) {
			throw new CrawlerException( mPhpLintError );
		}
	}
    
	/**
	 * Runs php linter on target file
	 * @param path Target file
	 * @throws IOException
	 * @throws CrawlerException
	 * @throws InterruptedException 
	 */
    protected void phpLint( Path path ) throws IOException, CrawlerException, InterruptedException {
        String[] args = { "php", "-l", path.toString() };
        Process p = Runtime.getRuntime().exec( args );
  
        if ( p.waitFor() != 0 ) {
            throw new CrawlerException( String.format( "File %s has invalid PHP markup", path.toString() ) );
        }
    }
    
	/**
	 * Adds translate files
	 * @throws IOException
	 * @throws CrawlerException 
	 */
    protected void addTranslate() throws IOException, CrawlerException {
        Translator translator = new Translator( mFiles, mRealCode );
        translator.run();
    }
	
	/**
	 * Creates OC 2.0-2.2 file structure under {@link #TMP_DIR}
	 * @throws IOException 
	 */
	protected void createV20() throws IOException {
		Profiler.record( "Creating V20 package" );
		v20Visitor visitor = new v20Visitor();
		Files.walkFileTree( Paths.get( TMP_DIR ), visitor );
		Profiler.record( "Creating V20 package" );
	}
	
	/**
	 * Adds current version to files
	 * @throws IOException 
	 */
	protected void addVersion() throws IOException {
		Profiler.record( "Version" );
		VersionVisitor visitor = new VersionVisitor();
		Files.walkFileTree( Paths.get( TMP_DIR ), visitor );
		Profiler.record( "Version" );
	}
	
	/**
	 * Returns package version based on configuration version
	 * and latest saved package
	 * @return
	 * @throws IOException 
	 */
	protected String guesVersion() throws IOException {
		Path storage = Paths.get( STORAGE_DIR, mCode );
		Pattern pattern = Pattern.compile( ".*(\\d+)\\.(\\d+)\\.(\\d+)\\.ocmod\\.zip$" );
		ArrayList<PackageVersion> versions = new ArrayList<>();
		PackageVersion configVersion = new PackageVersion( (Integer)mVersionMajor.getValue(),
				(Integer)mVersionMinor.getValue(), (Integer)mVersionPatch.getValue() );
		
		// Looking for existing packages
		if ( Files.exists( storage ) ) {
			Files.list( storage ).forEach( path -> {
				Matcher m = pattern.matcher( path.toString() );
				if( m.matches()) {
					versions.add( new PackageVersion(
						Integer.parseInt(m.group( 1 ) ),
						Integer.parseInt(m.group( 2 ) ),
						Integer.parseInt(m.group( 3 ) )
					) );
				}
			} );
		}
		
		// There is no saved packages - use config values
		if ( versions.isEmpty() ) {
			return configVersion.toString();
		}
		
		// Get latest version
		versions.sort( ( a, b ) -> a.compareTo( b ) );
		PackageVersion latest = versions.get( versions.size() - 1 );
		
		// Config wins
		if ( latest.compareTo( configVersion ) < 0 ) {
			return configVersion.toString();
		}
		
		// Set next version
		latest.increment();
		// Update config with new values
		mVersionMajor.getValueFactory().setValue( latest.getMajor() );
		mVersionMinor.getValueFactory().setValue( latest.getMinor() );
		mVersionPatch.getValueFactory().setValue( latest.getPatch() );
		
		return latest.toString();
	}
	
	/**
	 * Zips package and places it under {@link #STORAGE_DIR}
	 * @param fileName Zip filename
	 * @throws Exception 
	 */
	protected void zip( String fileName ) throws Exception {
		Zipper zip = new Zipper( Paths.get( TMP_DIR ).getParent(), Paths.get( TMP_DIR ).getParent() );
		zip.save( STORAGE_DIR + mCode + "/" + fileName );
	}
    
    /**
     * Visitor class for cleanUpDir method
     */
    class CleanVisitor implements FileVisitor<Path> {

        @Override
        public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
            return FileVisitResult.CONTINUE;
        }

        @Override
        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
            Files.delete( file );
            return FileVisitResult.CONTINUE;
        }

        @Override
        public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
            throw exc;
        }

        @Override
        public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
            Files.delete( dir );
            return FileVisitResult.CONTINUE;
        }
    }
	
    /**
     * Visitor class for createV20 method
     */
    class v20Visitor implements FileVisitor<Path> {

        @Override
        public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
            return FileVisitResult.CONTINUE;
        }

        @Override
        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
			String oldName = file.toString();

            if ( oldName.contains( "/en-gb/extension/" ) ) {
				Path newName = Paths.get( oldName.replace( "/en-gb/extension/", "/english/" ) );
				Files.createDirectories( newName.getParent() );
				Files.move( file, newName );

			} else if ( oldName.contains( "/extension/" ) ) {
				Path newName = Paths.get( oldName.replace( "/extension/", "/" ) );
				Files.createDirectories( newName.getParent() );
				
				if ( oldName.contains( "/controller/" ) || oldName.contains( "/model/" ) ) {
					String content = new String( Files.readAllBytes( file ) )
							.replaceFirst( "(Controller|Model)Extension", "$1" );
					Files.write( newName, content.getBytes() );
					Files.delete( file );

				} else {
					Files.move( file, newName );
				}
			}

            return FileVisitResult.CONTINUE;
        }

        @Override
        public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
            throw exc;
        }

        @Override
        public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
			if ( Files.list( dir ).count() == 0 ) {
				Files.delete( dir );
			}

            return FileVisitResult.CONTINUE;
        }
    }

    /**
     * Visitor class for addVerson method
     */
    class VersionVisitor implements FileVisitor<Path> {

        @Override
        public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
            return FileVisitResult.CONTINUE;
        }

        @Override
        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
			if ( file.toString().endsWith( ".php" ) ) {
				StringBuilder out = new StringBuilder();
				boolean found = false;
				
				try {
					for( String line: Files.readAllLines( file ) ) {
						if ( !found && line.contains( "*/" ) ) {
							return FileVisitResult.CONTINUE;
						}
						
						if ( !found && line.contains( "@version " ) ) {
							line = String.format( " * @version %s", mVersion );
							found = true;
						}
						
						out.append( line ).append( "\n" );
					}

				} catch (IOException ex) {
					Logger.getLogger(Packager.class.getName()).log(Level.SEVERE, null, ex);
					throw new IOException( ex.toString() + ": " + file.toString() );
				}
				
				Files.write( file, out.toString().getBytes() );
			}

            return FileVisitResult.CONTINUE;
        }

        @Override
        public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
            throw exc;
        }

        @Override
        public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
            return FileVisitResult.CONTINUE;
        }
    }
	
	/**
	 * Class that represents package version
	 */
	class PackageVersion {
		protected int mMajor = 0;
		protected int mMinor = 0;
		protected int mPatch = 0;

		public PackageVersion( int major, int minor, int patch ) {
			mMajor = major;
			mMinor = minor;
			mPatch = patch;
		}
		
		public int getMajor() {
			return mMajor;
		}
		
		public int getMinor() {
			return mMinor;
		}
		
		public int getPatch() {
			return mPatch;
		}
		
		public int compareTo( PackageVersion other ) {
			if ( mMajor != other.getMajor() ) {
				return mMajor - other.getMajor();
			}
			
			if ( mMinor != other.getMinor() ) {
				return mMinor - other.getMinor();
			}
			
			if ( mPatch != other.getPatch() ) {
				return mPatch - other.getPatch();
			}
			
			return 0;
		}
		
		public void increment() {
			mPatch++;
		}
		
		@Override
		public String toString() {
			return String.format( "%d.%d.%d", mMajor, mMinor, mPatch );
		}
	}
	
	/**
	 * PHP linter thread
	 */
	class PHPLinterThread extends RecursiveAction {
		private final List<Path> mFiles;
		private static final int THRESHOLD = 10;

		public PHPLinterThread( List files ) {
			mFiles = files;
		}

		@Override
		protected void compute() {
			if ( mPhpLintError != null ) {
				return;
			}

			if ( mFiles.size() > THRESHOLD ) {
				ForkJoinTask.invokeAll( createSubtasks() );

			} else {
				try {
					processing();

				} catch (IOException | InterruptedException | CrawlerException ex) {
					Logger.getLogger(Packager.class.getName()).log(Level.SEVERE, null, ex);
					mPhpLintError = ex.toString();
				}
			}
		}

		private List<PHPLinterThread> createSubtasks() {
			List<PHPLinterThread> subtasks = new ArrayList<>();

			List partOne = mFiles.subList( 0, mFiles.size() / 2 );
			List partTwo = mFiles.subList( mFiles.size() / 2, mFiles.size() );

			subtasks.add( new PHPLinterThread(partOne ) );
			subtasks.add( new PHPLinterThread( partTwo ) );

			return subtasks;
		}

		private void processing() throws IOException, CrawlerException, InterruptedException {
			for( Path p: mFiles ) {
				phpLint( p );
			}
		}
	}
}
