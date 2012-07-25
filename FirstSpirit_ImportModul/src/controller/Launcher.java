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
import java.util.Scanner;
import java.util.Set;

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
import de.espirit.firstspirit.common.GlobalSystemInformations.SysinfoShellResult;
import de.espirit.firstspirit.common.IOError;
import de.espirit.firstspirit.common.MaximumNumberOfSessionsExceededException;
import de.espirit.firstspirit.forms.FormField;
import de.espirit.firstspirit.server.authentication.AuthenticationException;
import de.espirit.or.Session;
import de.espirit.or.query.Select;
import de.espirit.or.schema.Entity;



public class Launcher  
{
	//InputStream --> FÄLLT NACH GUI WEG
	private static String _eingabe = "";
	private static Scanner scanner = new Scanner(System.in);
	private static String _jobWahl = "";
	//Connect
	private static Connection _connection;
	//Project
	private static Project _project;
	//Store
	private static Store _store;
	//Mapping
	private static Map<String,String> _mapping= new HashMap<String,String>();
	
	
	//****************************************************************************************************************************
	//***MAIN*********************************************************************************************************************
	//****************************************************************************************************************************
	public static void main(String[] args) 
	{
		//Login FirstSpirit Server 
		connect("localhost",8000,1,"Admin","Admin");
		//Wahl des Projekts
		_project = getProject("ImportProjekt");
		
		System.out.println("Verbindung zum Projekt "+_project.getName()+" hergestellt...");
		System.out.println();
		
		//Ausgabe möglicher Jobs
		System.out.println("Mögliche Jobs:");
		System.out.println("******************");
		System.out.println("1  Daten-Import");
		System.out.println("2  Seiten-Import");
		
		//InputStream Jobwahl
		_jobWahl = inputStreamJob();
		
		//Check der Jobwahl 
		if (_jobWahl.equals("1")) //--> Daten-Import 
		{
			System.out.println();
			//Store erstellen
			_store = _project.getUserService().getStore(Store.Type.CONTENTSTORE, false);
			DatenImport di = new DatenImport(_store);
			di.getAllTables();
			di.printTab();
			
			//Eingabe der Ziel Tabelle
			inputStreamTable(di);
			
			//ImportDatei --> Ermittlung der Daten und Ausgabe
			CSVData csv_file = new CSVData("C:/data.csv",";");
			csv_file.print();
			
			//Mapping Spalten in Tabelle --> Felder in CSV
			System.out.println();
			System.out.println("Mapping Tabelle/CSV-Datei:");
			System.out.println("***************************");
			inputStreamMapping(di,csv_file);
		}
		
		//Close Connection
		try 
		{
			_connection.close();
			System.out.println();
			System.out.println("Verbindung zum Server wurde getrennt.");
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
	}
	//****************************************************************************************************************************
	//***MAIN ENDE****************************************************************************************************************
	//****************************************************************************************************************************
	
	//InputStreamMapping 
	public static void inputStreamMapping(DatenImport di, CSVData csv_file)
	{
		List<String> columnListCSV = csv_file.getColumnList();
		
		boolean error = false;
		while (true)
		{
			for (String column : di.getColumns())
			{
				boolean check = false;
				while (check == false)
				{
					System.out.println();
					System.out.println("Mapping für Spalte "+column+ " festlegen:");
					_eingabe = scanner.nextLine();
					if (columnListCSV.contains(_eingabe))
					{
						_mapping.put(column, _eingabe);
						check=true;
					}
				}
			}
			break;
		}
		
		//Ausgabe des Mappings
		System.out.println();
		System.out.println();
		System.out.println("Folgendes Mapping wurde festgelegt:");
		System.out.println("************************************");
		
		for (String column : di.getColumns())
		{
			System.out.printf("%-30s %-30s", column, _mapping.get(column));
			System.out.println();
		}
		
		//Prüfen ob Import gestartet werden soll
		System.out.println();
		System.out.println();
		System.out.println();
		while (!_eingabe.equals("ja") && !_eingabe.equals("nein"))
		{
			System.out.println("Wollen Sie den Import Starten (ja/nein)?");
			_eingabe = scanner.nextLine();
		}
		
		//Check ja oder nein
		if (_eingabe.equals("ja"))
		{
			System.out.println("Import wird gestartet....");
		}
		else
		{
			
		}
		
	}
	
	//InputStreamJob
	public static String inputStreamJob()
	{
		String job = "";
		boolean error = false;
		while (true)
		{
			if (error == false)
			{
				System.out.println();
				System.out.println("Bitte Import-Job-Nr. eingeben: ");
			}
			else
			{
				System.out.println("Fehler! Bitte Job-Nr. nochmals eingeben: ");
			}
			
			_eingabe = scanner.nextLine();
			System.out.println();
			
			if (_eingabe.equals("1")) 
			{
				job = "1";
				break;
			}
			else if (_eingabe.equals("2"))
			{
				System.out.println("Fehler: Noch nicht verfügbar.");
				error = true;
			}
			else if (_eingabe.equals("exit") || _eingabe.equals("Exit"))
			{
				break;
			}
			else
			{
				error = true;
			}
		}
		return job;
	}
	
	
	//ImputStreamTable
	public static void inputStreamTable(DatenImport di)
	{
		boolean error = false;
		while (true)
		{
			if (error == false)
			{
				System.out.println("Bitte Ziel Tabellennamen zum Import eingeben: ");
			}
			else
			{
				System.out.println("Fehler! Bitte Tabellenname nochmals eingeben: ");
			}
			
			_eingabe = scanner.nextLine();
			System.out.println();
			
			if (_store.getStoreElement(_eingabe, IDProvider.UidType.CONTENTSTORE) != null && !_store.getStoreElement(_eingabe, IDProvider.UidType.CONTENTSTORE).getName().contains("de.espirit.")) 
			{
				System.out.println();
				//Schema + Session erstellen
				di.fillColumList(_eingabe);
				di.printContent(_eingabe);
				break;
			}
			else if (_eingabe.equals("exit") || _eingabe.equals("Exit"))
			{
				break;
			}
			else
			{
				error = true;
			}

		}
	}
	
	//************************************************************************************************
	//CONNECTION --> FÄLLT WEG BEI GUI
	//************************************************************************************************
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
	//************************************************************************************************
	//************************************************************************************************
	
	//getProject
	public static Project getProject (String projectName)
	{
		return Launcher.getConnection().getProjectByName(projectName);
	}
}
