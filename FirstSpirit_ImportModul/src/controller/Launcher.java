package controller;

import java.awt.Component;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import view.GUI_Start;

import de.espirit.common.util.Listable;
import de.espirit.firstspirit.access.AdminService;
import de.espirit.firstspirit.access.Connection;
import de.espirit.firstspirit.access.ConnectionManager;
import de.espirit.firstspirit.access.ExceptionHandler;
import de.espirit.firstspirit.access.InvalidSessionException;
import de.espirit.firstspirit.access.MailService;
import de.espirit.firstspirit.access.MessageService;
import de.espirit.firstspirit.access.ServerConfiguration;
import de.espirit.firstspirit.access.ServiceNotFoundException;
import de.espirit.firstspirit.access.ServicesBroker;
import de.espirit.firstspirit.access.User;
import de.espirit.firstspirit.access.UserService;
import de.espirit.firstspirit.access.editor.value.DatasetContainer;
import de.espirit.firstspirit.access.project.Project;
import de.espirit.firstspirit.access.project.RemoteProjectConfiguration;
import de.espirit.firstspirit.access.search.SearchService;
import de.espirit.firstspirit.access.store.ElementDeletedException;
import de.espirit.firstspirit.access.store.IDProvider;
import de.espirit.firstspirit.access.store.LockException;
import de.espirit.firstspirit.access.store.Store;
import de.espirit.firstspirit.access.store.Store.Type;
import de.espirit.firstspirit.access.store.StoreElement;
import de.espirit.firstspirit.access.store.StoreElementFilter;
import de.espirit.firstspirit.access.store.StoreElementFolder;
import de.espirit.firstspirit.access.store.contentstore.Content2;
import de.espirit.firstspirit.access.store.contentstore.ContentFolder;
import de.espirit.firstspirit.access.store.contentstore.ContentStoreRoot;
import de.espirit.firstspirit.access.store.contentstore.Dataset;
import de.espirit.firstspirit.access.store.pagestore.Page;
import de.espirit.firstspirit.access.store.pagestore.PageFolder;
import de.espirit.firstspirit.access.store.templatestore.Schema;
import de.espirit.firstspirit.agency.SpecialistsBroker;
import de.espirit.firstspirit.agency.StoreAgent;
import de.espirit.firstspirit.client.migration.parser.Content;
import de.espirit.firstspirit.common.IOError;
import de.espirit.firstspirit.common.MaximumNumberOfSessionsExceededException;
import de.espirit.firstspirit.forms.FormField;
import de.espirit.firstspirit.server.authentication.AuthenticationException;
import de.espirit.or.Session;
import de.espirit.or.query.Select;
import de.espirit.or.schema.Entity;



public class Launcher extends JFrame
{
	//Import File Path -->> HIER HABE ICH AUF DEM PC SCRATCHY WAS HINZUGEFÜGT
	private static String _path = "";
	//Connect
	private static Connection _connection;
	//Project
	private static Project _project;
	//Store
	private static Store _store;
	private static Content2 _content2;
	private static Schema _schema;
	private static Session _session;
	private static Map<Integer, String[]> _newData = new HashMap<Integer,String[]>();
	//Import in DB/Tabellen
	private static List<String> _refTable = new ArrayList<String>();
	
	
	public static void main(String[] args) 
	{
		//Login FirstSpirit Server <-- FÄLLT WEG
		connect("localhost",8000,1,"Admin","Admin");
		_project = getProject("Test");
		
		//Abfrage welche Import-Jobs in der Konfiguration erlaubt wurden --> GUI mit DropDown Menü zum Auswahl des Import-Jobs
			//Start GUI -> Logo Nionex und FirstSpirit + Überschrift
		
		
		
		//*************************************************************************************************************************
		//DB Import gewählt
		//*************************************************************************************************************************
		
		
		try 
		{
			//XML Datei
	        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
	        
	        // Leerzeichen werden entfernt
	        factory.setIgnoringElementContentWhitespace(true);

	        // bevor ein 'Document' erstellt werden kann wird ein 'DocumentBuilder' benötigt
	        DocumentBuilder builder= factory.newDocumentBuilder();
	        
			// Speicherort der XML-Datei
	        File file = new File("src/xml/import_job_tab_location.xml");
	        Document document = builder.parse(file);
	        
	        
			String st_job_name = document.getDocumentElement().getElementsByTagName("name").item(0).getTextContent().trim();
			String st_source = document.getDocumentElement().getElementsByTagName("source-url").item(0).getTextContent().trim();
	        String st_dbs = document.getDocumentElement().getElementsByTagName("dbs").item(0).getTextContent().trim();
	        String st_table = document.getDocumentElement().getElementsByTagName("table").item(0).getTextContent().trim();
	        
	        //Get Table of DBS
	        Content2 _content2Country = (Content2) _store.getStoreElement(st_table, IDProvider.UidType.CONTENTSTORE);
	        Schema _schemaCountry = _content2Country.getSchema();
			Session _sessionCountry = _schemaCountry.getSession();
	    	
			//Select all from Table
			Select select_all = _sessionCountry.createSelect(st_table);
			List<Entity> _list = _sessionCountry.executeQuery(select_all);
			
			//Print all Entries from table
			System.out.println("Spalten in location");
			System.out.println("*****************************************");
			
			String allEntriesCountry = "";
			for (Entity e : _list)
			{
				Collection<String> col_allAttNames = e.getAttributeNames();
				for (String at : col_allAttNames)
				{				
					//Von FirstSpirit automatisch generierte Spalten --> Fehler bei getClass() --> daher die Abfrage an dieser Stelle
					if (!at.toString().contains("released by") && !at.toString().contains("fs_id") && !at.toString().contains("wf col") && !at.toString().contains("wf id") && !at.toString().contains("changed by"))
					{
						//Check ob eine Referenz auf eine andere Tabelle vorliegt
						if (e.getValue(at).getClass().toString().contains("de.espirit."))
						{
							String column = at.toString();
							Entity en = (Entity) e.getValue(at);
							for (String att : en.getAttributeNames())
							{
								if (att.equals(column))
								{
									System.out.println(en +"            Das ist eine Referenz auf eine Weiter Tabelle! Bitte erst alle zu importierenden Daten in diese Tabelle importieren.");
									//Hier evtl. eine Liste füllen, die alle Referenztabellen enthält. Diese Werden dann hintereinander ausgegeben
								}
							}
						}
						else
						{
							System.out.println(at);
						}
					}
					
				}
				break;
			}
	        
	        
		} 
		catch (ParserConfigurationException e) 
		{
			e.printStackTrace();
		} 
		catch (SAXException e) 
		{
			e.printStackTrace();
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}

        

		
		
    	//*************************************************************************************************************************
    	//*************************************************************************************************************************
	}
	
	//Fällt komplett ab hier weg, da die Verbindung als Modul schon vorhanden ist
	//Connect
	public static void connect (String host, int port, int mode, String user, String password)
	{
		_connection = ConnectionManager.getConnection(host,port,mode,user,password);
		try 
		{
			_connection.connect();
		} 
		catch (IOError e)
		{
			JOptionPane.showMessageDialog(null, "Error: IO Error \nPlease check your inputs for ip and port");
			System.out.println(e.getMessage());
		}
		catch (MaximumNumberOfSessionsExceededException e) 
		{
			JOptionPane.showMessageDialog(null, "Error: MaximumNumberOfSessionsExceededException");
			System.out.println(e.getMessage());
		} 
		catch (IOException e) 
		{
			JOptionPane.showMessageDialog(null, "Error: Input Output Exception \nPlease check your login inputs");
			System.out.println(e.getMessage());
		} 
		catch (AuthenticationException e)
		{
			JOptionPane.showMessageDialog(null, "Error: Authentication Exception \nPlease check your inputs for user and password");
			System.out.println(e.getMessage());
		}
	}
	
	//Get Connection
	public static Connection getConnection ()
	{
		return _connection;
	}
	
	//closeConnection
	public static void closeConnection ()
	{
		try 
		{
			_connection.close();
		} 
		catch (IOException e1) 
		{
			e1.printStackTrace();
		}
	}
	
	
	//**************************************************************************************************************
	//Connection / Project / getData / import Data / print
	//**************************************************************************************************************
		
	//getProject
	public static Project getProject (String projectName)
	{
		return Launcher.getConnection().getProjectByName(projectName);
	}
		
	//getImportData
	public static boolean getImportData (String filePath, String trennzeichen)
	{
		try 
		{
			BufferedReader br_data = new BufferedReader(new InputStreamReader(new FileInputStream(filePath)));
			String line="";
			int i = 0;
			while ((line = br_data.readLine()) != null)
			{
				if (!line.isEmpty())
				{
					String [] location = line.split(trennzeichen);
					_newData.put(i, location);
					i++;
				}
				else
				{
					break;
				}
			}
				
			return true;
		} 
		catch (UnsupportedEncodingException e) 
		{
			e.printStackTrace();
			return false;
		} 
		catch (FileNotFoundException e) 
		{
			e.printStackTrace();
			return false;
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
			return false;
		}
	}
}
