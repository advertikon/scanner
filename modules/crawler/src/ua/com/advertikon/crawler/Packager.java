/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ua.com.advertikon.crawler;

import java.io.File;
import java.io.IOException;
import java.nio.file.CopyOption;
import java.nio.file.FileVisitOption;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Spliterator;
import java.util.concurrent.TimeUnit;
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
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 *
 * @author Advertikon
 */
public class Packager implements Runnable {
    public static final String TMP_DIR     = ".tmp_pckg/upload";
	public static final String STORAGE_DIR = "/var/www/html/crawler/";
    protected ArrayList<Path> mFiles;
    protected String mCode;
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
    }
    
    public void runIt() throws IOException, ParserConfigurationException, SAXException,
            TransformerException, TransformerConfigurationException, CrawlerException, InterruptedException {
		mVersion = guesVersion();

        createV23();
        makeVQMod();
        addReadme();
        addTranslate();
		addVersion();
//		phpLintAll(); // for vesion 2.3 - 3+
		createV20();
//		phpLintAll(); // for version 2.0 - 2.2
    }
    
    protected void createV23() throws IOException {
        Profiler.record( "Populate V23" );
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
        
        Profiler.record( "Populate V23" );
    }
    
    /**
     * Recursively deletes directory
     * @param path
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
    
    protected void addReadme() throws IOException {
        Profiler.record( "Readme" );
    
        for( Path path: mFiles ) {
            if ( path.getFileName().toString().equals( mCode + "_readme" ) ) {
                Files.copy( path, Paths.get( TMP_DIR ).getParent().resolve( Paths.get( "README.TXT" ) ) );
                break;
            }
        }
        
        Profiler.record( "Readme" );
    }
    
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
    
    protected boolean isOCMod( Path path ) {
        return path.toString().endsWith( "/system/" + mCode + ".ocmod.xml" );
    }
    
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
                    // TODO: add version
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

        StreamResult result2 = new StreamResult( Paths.get( packageRoot.toString(), mCode + ".ocmod.xml" ).toFile() );
        transformer.transform( ocmodSource, result2 );
        
        // VQMOD
        Path vqmodPath = Paths.get( packageRoot.toString(), mCode + ".vqmod.xml" );
        DOMSource vqmodSource = new DOMSource( vqmod );
        StreamResult result3 = new StreamResult( vqmodPath.toFile() );
        transformer.transform( vqmodSource, result3 );
        
        if ( true || hasXmlLinter() ) {
            xmlLint( indexPath );
            xmlLint( vqmodPath );
        }
    }
    
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
    
    protected void xmlLint( Path path ) throws IOException, CrawlerException, InterruptedException {
        String[] args = { "xmllint", path.toString() };
        Process p = Runtime.getRuntime().exec( args );
  
        if ( p.waitFor() != 0 ) {
            throw new CrawlerException( String.format( "File %s has invalid XML markup", path.toString() ) );
        }
    }
	
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
	
	protected void phpLintAll() throws IOException, CrawlerException, InterruptedException {
		Profiler.record( "PHP lint" );
//		mSyncedList = Collections.<Path>synchronizedList( (List<Path>) mFiles.clone() );
		mPhpLintError = null;
		ArrayList<Thread> threads = new ArrayList<>();
		List<Path> list = Files.walk( Paths.get( TMP_DIR ) ).collect( Collectors.toList() );
		mSyncedList = Collections.<Path>synchronizedList( (List<Path>) list );
		
		for( int i = 0; i < 10; i++ ) {
			Thread t = new Thread( this );
			threads.add( t );
			t.start();
		}
		
		for( Thread t: threads ) {
			t.join();
		}
		
		Profiler.record( "PHP lint" );
		
		if ( mPhpLintError != null ) {
			throw new CrawlerException( mPhpLintError );
		}
	}
	
	@Override
	public void run() {
		Path p = null;

		while( mPhpLintError == null && !mSyncedList.isEmpty() ) {
			try {
				p = mSyncedList.get( 0 );
				mSyncedList.remove( 0 );
				phpLint( p );

			} catch (IOException | CrawlerException | InterruptedException ex) {
				Logger.getLogger(Packager.class.getName()).log(Level.SEVERE, null, ex);
				mPhpLintError = String.format( "File %s has invalid PHP markup", p != null ? p.toString() : "" );
			}
		}
	}
	
	protected boolean doPhpLint( Path path ) {
        int index = path.toString().lastIndexOf( "." );
        
        if ( -1 == index ) {
            return false;
        }
        
        return path.toString().substring( index + 1 ).equals( "php" );
    }
    
    protected void phpLint( Path path ) throws IOException, CrawlerException, InterruptedException {
		if ( !doPhpLint( path ) ) {
			return;
		}

        String[] args = { "php", "-l", path.toString() };
        Process p = Runtime.getRuntime().exec( args );
        
        synchronized( p ) {
            p.wait();
        }
  
        if ( p.exitValue() != 0 ) {
            throw new CrawlerException( String.format( "File %s has invalid PHP markup", path.toString() ) );
        }
    }
    
    protected void addTranslate() throws IOException, CrawlerException {
        Translator translator = new Translator( mFiles, mCode );
        translator.run();
    }
	
	protected void createV20() throws IOException {
		Profiler.record( "Creating V20 package" );
		v20Visitor visitor = new v20Visitor();
		Files.walkFileTree( Paths.get( TMP_DIR ), visitor );
		Profiler.record( "Creating V20 package" );
	}
	
	protected void addVersion() throws IOException {
		Profiler.record( "Version" );
		VersionVisitor visitor = new VersionVisitor();
		Files.walkFileTree( Paths.get( TMP_DIR ), visitor );
		Profiler.record( "Versione" );
	}
	
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
//				Files.delete( file );

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
				
				for( String line: Files.readAllLines( file ) ) {
					if ( !found && line.contains( "*/" ) ) {
						return FileVisitResult.CONTINUE;
					}
					
					if ( !found && line.contains( "@version " ) ) {
						line = " * @version 1.1.1\n";
						found = true;
					}
					
					out.append( line ).append( "\n" );
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
}
