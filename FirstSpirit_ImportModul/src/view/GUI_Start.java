package view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Event;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.Toolkit;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import controller.Launcher;

import de.espirit.common.util.Listable;
import de.espirit.firstspirit.access.Connection;
import de.espirit.firstspirit.access.ConnectionManager;
import de.espirit.firstspirit.access.project.Project;
import de.espirit.firstspirit.access.store.IDProvider;
import de.espirit.firstspirit.access.store.Store;
import de.espirit.firstspirit.access.store.StoreElement;
import de.espirit.firstspirit.access.store.contentstore.Content2;
import de.espirit.firstspirit.access.store.templatestore.Schema;
import de.espirit.firstspirit.common.IOError;
import de.espirit.firstspirit.common.MaximumNumberOfSessionsExceededException;
import de.espirit.firstspirit.server.authentication.AuthenticationException;
import de.espirit.or.SchemaException;
import de.espirit.or.Session;
import de.espirit.or.query.Select;
import de.espirit.or.schema.Entity;

import java.awt.event.ActionEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class GUI_Start extends JFrame implements ActionListener
{
	
	public GUI_Start() 
	{	
		super("FirstSpirit: Import Modul");
		this.addWindowListener(new WindowAdapter()
        {
        	  public void windowClosing(WindowEvent we)
        	  {
        		  System.exit(0);
        	  }
        });
        
        Dimension d_min = new Dimension (500,400);
        Dimension d_max = new Dimension (400,400);
        
        this.setMinimumSize(d_min);
        this.setMaximumSize(d_max);
        this.setResizable(true);
        
        this.pack();
        this.setVisible(true);
	}

	//**************************************************************************************************************
	//ActionListener
	//**************************************************************************************************************
	@Override
	public void actionPerformed(ActionEvent event) 
	{
		//Ne nachdem welcher Job gewählt wurde, wird dieses Panel visibile = true gesetzt
	}
	
}
