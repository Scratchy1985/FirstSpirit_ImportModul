package controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.espirit.firstspirit.access.store.IDProvider;
import de.espirit.firstspirit.access.store.Store;
import de.espirit.firstspirit.access.store.StoreElement;
import de.espirit.firstspirit.access.store.contentstore.Content2;
import de.espirit.firstspirit.access.store.templatestore.Schema;
import de.espirit.or.Session;
import de.espirit.or.query.Select;
import de.espirit.or.schema.Entity;

public class DatenImport 
{
	//************************************************************************************************
	//VARIABLEN
	//************************************************************************************************
	private Store store;
	private Map<String,String> contenstoreRootChilds = new HashMap<String,String>();
	private Content2 content2;
	private Schema schema;
	private Session session;
	private List<String> columnList = new ArrayList<String>();
	private List<Entity> contentList;

	//************************************************************************************************
	//KONSTRUKTOR
	//************************************************************************************************
	public DatenImport (Store store)
	{
		this.store = store;
	}
	
	
	//************************************************************************************************
	//METHODEN
	//************************************************************************************************
	//Get und Set Methoden
	public  Map<String, String> get_contenstoreRootChilds() 
	{
		return this.contenstoreRootChilds;
	}
	
	public Content2 get_content2() 
	{
		return this.content2;
	}

	public Schema getSchema() 
	{
		return this.schema;
	}

	public Session getSession() 
	{
		return this.session;
	}

	public List<String> getColumns() 
	{
		return this.columnList;
	}
	
	public Store getStore() 
	{
		return store;
	}

	public void setStore(Store store) 
	{
		this.store = store;
	}

	public Map<String, String> getContenstoreRootChilds() 
	{
		return contenstoreRootChilds;
	}

	public void setContenstoreRootChilds(Map<String, String> contenstoreRootChilds) 
	{
		this.contenstoreRootChilds = contenstoreRootChilds;
	}

	public Content2 getContent2() 
	{
		return content2;
	}

	public void setContent2(Content2 content2) 
	{
		this.content2 = content2;
	}

	public void setSchema(Schema schema) 
	{
		this.schema = schema;
	}

	public void setSession(Session session) 
	{
		this.session = session;
	}

	public void setColumns(List<String> columns) 
	{
		this.columnList = columns;
	}


	//Get alle Tabellen
	public void getAllTables()
	{
		//Auslistung alle Tabellen im ContentStore --> WICHTIG: Es werden 5 Ebenen unterstützt !!!!!!
		for (StoreElement st1 : store.getChildren())
		{
	    	if (!st1.getName().contains("de.espirit."))
			{
	    		if (st1.getElementType().toString().equals("ContentFolder"))
	    		{
	    			//Zweite Ebene
	    			for (StoreElement st2 : st1.getChildren())
	    			{
	    				if (st2.getElementType().toString().equals("ContentFolder"))
	    	    		{
	    					//Dritte Ebene
	    	    			for (StoreElement st3 : st2.getChildren())
	    	    			{
	    	    				if (st3.getElementType().toString().equals("ContentFolder"))
	    	    	    		{
	    	    					//Vierte Ebene
	    	    	    			for (StoreElement st4 : st3.getChildren())
	    	    	    			{
	    	    	    				if (st4.getElementType().toString().equals("ContentFolder"))
	    	    	    	    		{
	    	    	    					//Fünfte Ebene
	    	    	    	    			for (StoreElement st5 : st4.getChildren())
	    	    	    	    			{
	    	    	    	    				if (!st5.getElementType().toString().equals("ContentFolder"))
	    	    	    	    	    		{
	    	    	    	    					contenstoreRootChilds.put(st5.getName(), st5.getParent().getName());
	    	    	    	    	    		}
	    	    	    	    			}
	    	    	    	    		}
	    	    	    				else
	    	    	    				{
	    	    	    					if (!st4.getName().contains("de.espirit."))
	    	    	    					{
	    	    	    						contenstoreRootChilds.put(st4.getName(), st4.getParent().getName());
	    	    	    					}
	    	    	    				}
	    	    	    			}
	    	    	    		}
	    	    				else
	    	    				{
	    	    					if (!st3.getName().contains("de.espirit."))
	    	    					{
	    	    						contenstoreRootChilds.put(st3.getName(), st3.getParent().getName());
	    	    					}
	    	    				}
	    	    			}
	    	    		}
	    				else
	    				{
	    					if (!st2.getName().contains("de.espirit."))
	    					{
	    						contenstoreRootChilds.put(st2.getName(), st2.getParent().getName());
	    					}
	    				}
	    			}
	    		}
	    		else
	    		{
	    			if (!st1.getName().contains("de.espirit."))
	    			{
	    				contenstoreRootChilds.put(st1.getName(), st1.getParent().getName());
	    			}
	    		}
			}
		}
	}
	
	//Get All Columns from Table
	public void fillColumList (String tab)
	{
		this.setContent2((Content2) this.store.getStoreElement(tab, IDProvider.UidType.CONTENTSTORE));
		this.setSchema(this.content2.getSchema());
		this.setSession(this.schema.getSession());
		StoreElement table =  this.store.getStoreElement(tab, IDProvider.UidType.CONTENTSTORE);
		
		//Inhaltsabfrage
		Select select_all = this.session.createSelect(table.getName());
		this.contentList = this.session.executeQuery(select_all);
		
		for (String topic : contentList.get(0).getAttributeNames())
		{
			if (!topic.contains("changed by") && !topic.contains("fs_id") && !topic.contains("released by") && !topic.contains("wf id") && !topic.contains("wf col"))
			{
				this.columnList.add(topic);
			}	
		}
	}

	//Ausgabe Content
	public void printContent(String tab)
	{
		//Print Topics
		for (String topic : this.columnList)
		{
			if (!topic.contains("changed by") && !topic.contains("fs_id") && !topic.contains("released by") && !topic.contains("wf id") && !topic.contains("wf col"))
			{
				System.out.printf("%-30s", topic);
			}	
		}
		System.out.println();
		System.out.println("--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------");
		
		
		//Print Content
		for (Entity e : this.contentList)
		{
			for (int i=0;i<this.columnList.size();i++)
			{
				System.out.printf("%-30s", e.getValue(this.columnList.get(i)));
				if ((i+1) == this.columnList.size())
				{
					System.out.println();
				}
			}
		}
	}
	
	
	//Ausgabe der Tabellen
	public void printTab()
	{
		System.out.println();
		System.out.println("Zur Verfügung stehende Tabellen:");
		System.out.println("*********************************");
		System.out.printf("%-10s %-35s %-35s %n", "Nr.","Name","Parent");
		System.out.println("---------------------------------------------------");
		
		Set<String> keyList = this.contenstoreRootChilds.keySet();
		int i=1;
		for (String eintrag : keyList)
		{
			System.out.printf("%-10s %-35s %-35s %n",i,eintrag ,this.contenstoreRootChilds.get(eintrag));
			i++;
		}
		System.out.println();
		System.out.println();
	}
}
