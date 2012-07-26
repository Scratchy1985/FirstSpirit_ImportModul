package controller;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.espirit.or.schema.Entity;

public class CSVData 
{
	//************************************************************************************************
	//VARIABLEN TESTTTTTTTTT
	//************************************************************************************************
	private String path="";
	private String cutter;
	private List<String> columnList = new ArrayList<String>();
	private List<String[]> contentList = new ArrayList<String[]>();
	
	
	//************************************************************************************************
	//KONSTRUKTOR
	//************************************************************************************************
	public CSVData (String path, String cutter)
	{
		this.path = path;
		this.cutter = cutter;
		this.getImportData();
	}
	
	//************************************************************************************************
	//METHODEN
	//************************************************************************************************
	//Getter and Setter
	public String getPath() 
	{
		return path;
	}

	public void setPath(String path) 
	{
		this.path = path;
	}

	public String getCutter() 
	{
		return cutter;
	}

	public void setCutter(String cutter) 
	{
		this.cutter = cutter;
	}

	public List<String> getColumnList() 
	{
		return columnList;
	}

	public void setColumnList(List<String> columnList) 
	{
		this.columnList = columnList;
	}

	public List<String[]> getContentList() 
	{
		return contentList;
	}

	public void setContentList(List<String[]> contentList) 
	{
		this.contentList = contentList;
	}
	

	//Methode: Alle Daten aus Datei in der Map ablegen
	private boolean getImportData ()
	{
		try 
		{
			BufferedReader br_data = new BufferedReader(new InputStreamReader(new FileInputStream(this.path)));
			String line="";
			//Put Topics in List
			for (String column : br_data.readLine().split(this.cutter))
			{
				this.columnList.add(column);
			}

			//Content
			while ((line = br_data.readLine()) != null)
			{
				if (!line.isEmpty())
				{
					String [] data = line.split(this.cutter);
					this.contentList.add(data);
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

	
	//Print Import Data
	public void print()
	{
		System.out.println();
		System.out.println();
		System.out.println("Ausgabe der Daten aus der Import Datei:");
		System.out.println("*******************************************");
		//Print Topics
		for (String column : this.columnList)
		{
			System.out.printf("%-30s", column);
		}
		System.out.println();
		System.out.println("-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------");
	
		//Print Content
		for (String[] content : this.contentList)
		{
			for (int i=0;i<content.length;i++)
			{
				System.out.printf("%-30s", content[i]);
			}
			System.out.println();
		}
	}
	

}
