/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ua.com.advertikon.crawler;

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
import java.util.logging.Level;
import java.util.logging.Logger;
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
public class Packager {
    public static final String TMP_DIR = ".tmp_pckg/upload";
    protected ArrayList<Path> mFiles;
    protected String mCode;
    
    public Packager( ArrayList files, String code ) {
        mFiles = files;
        mCode = code;
    }
    
    public void run() throws IOException, ParserConfigurationException, SAXException,
            TransformerException, TransformerConfigurationException, CrawlerException, InterruptedException {
        createV2();
        makeVQMod();
        addReadme();
        addTranslate();
    }
    
    protected void createV2() throws IOException {
        Profiler.record( "Populate V2" );
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
        
        Profiler.record( "Populate V2" );
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
        
        Path indexPath = Paths.get( packageRoot.toString(), "index.xml" );
        StreamResult result1 = new StreamResult( indexPath.toFile() );
        transformer.transform( ocmodSource, result1 );

        StreamResult result2 = new StreamResult( Paths.get( packageRoot.toString(), mCode + ".ocmod.xml" ).toFile() );
        transformer.transform( ocmodSource, result2 );
        
        // VQMOD
        Path vqmodPath = Paths.get( packageRoot.toString(), mCode + ".vqmod.xml" );
        DOMSource vqmodSource = new DOMSource( vqmod );
        StreamResult result3 = new StreamResult( vqmodPath.toFile() );
        transformer.transform( vqmodSource, result3 );
        
        if ( hasXmlLinter() ) {
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
    
    protected boolean hasXmlLinter() {
        Process p = null;
        try {
            p = Runtime.getRuntime().exec( "which xmllint" );
            
            synchronized ( p ) {
                p.wait();
            }
  
        } catch (IOException ex) {
            Logger.getLogger( Packager.class.getName()).log(Level.SEVERE, null, ex );
            return false;
        } catch (InterruptedException ex) {
            Logger.getLogger(Packager.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return p != null && p.exitValue() == 0;
    }
    
    protected void xmlLint( Path path ) throws IOException, CrawlerException, InterruptedException {
        String[] args = { "xmllint", path.toString() };
        Process p = Runtime.getRuntime().exec( args );
        
        synchronized( p ) {
            p.wait();
        }
  
        if ( p.exitValue() != 0 ) {
            throw new CrawlerException( String.format( "File %s has invalid XML markup", path.toString() ) );
        }
    }
    
    protected void addTranslate() throws IOException, CrawlerException {
        Translator translator = new Translator( mFiles, mCode );
        translator.run();
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
}
