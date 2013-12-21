

import javax.swing.*;
import javax.swing.table.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.border.TitledBorder;
import javax.swing.event.*;
import javax.swing.text.Document;

import java.awt.*;
import java.awt.event.*;

import java.sql.*;


public class DatabaseFrame extends JFrame {
	
	
	static String account = "ccs108adchang1"; // replace with your account static String password = "..."; // replace with your password static String server = "mysql-user.stanford.edu";
	static String database = "c_cs108_adchang1"; // replace with your db
	static String sourcefile = "~/metropolises.sql";   //source file 
	static String tableName = "metropolises";
	static String password = "ceemeiru";
	
	
	
	public class customTableModel extends AbstractTableModel{
		int rows;
		int cols;
		ResultSet data;
		ResultSetMetaData rsmd;
		String[] columnNames = {"Metropolis","Continent","Population"};
		
		
		/**
		 * Default constructor.  Calls the superclass constructor (AbstractTableModel)
		 * with no arguments.
		 * 
		 */
		public customTableModel(){
			super();
		}
		
		/**
		 * Constructor that takes in a ResultSet as the argument
		 */
		public customTableModel(ResultSet rs){
			try{
				data = rs;
				rsmd = data.getMetaData();
				data.last();   //move to last row to get count of total number of rows
				rows = data.getRow();   //note the row #s start at 1, not 0, for ResultSets
				data.beforeFirst();   //resets  the cursor to before the first row.
				cols = rsmd.getColumnCount();
				
			}
			catch(SQLException e){
				return;
			}
		}
		
		/**
		 * Returns the number of columns in the model. A JTable uses this method to determine how many columns it should create and display by default.
		 * @return the number of columns in the model (int)
		 */
		public int getColumnCount(){
			return cols;
		}
		
		/**
		 * Returns the number of rows in the model. A JTable uses this method to determine how many rows it should display. This method should be quick, as it is called frequently during rendering.	 * @return the number of columns in the model (int)
		 * @return the number of rows in the model (int)
		 */
		public int getRowCount(){
			return rows;		
		}
	
		/**
		 * Returns the value for the cell at columnIndex and rowIndex.
		 * @param row- the row whose value is to be queried
		 * @param col - the column whose value is to be queried		
		 * @return the value Object at the specified cell
		 */
		public Object getValueAt(int row, int col){
	
			try{	
				int modColNum = col+1;   //because ResultSet columns start at index 1
				for(int count=0;count <= row; count++){
						data.next();				
				}
				String retrieved =data.getString(modColNum);
				data.beforeFirst();       //make sure to move the cursor back to top for subsequent queries!		
				return retrieved;				
			}
			catch(SQLException e){	
			}
			String test = "";
			return test;
		}
		
		/**
		 * Returns the name of the column at columnIndex. This is used to initialize the table's column header name. Note: this name does not need to be unique; two columns in a table can have the same name.
		 * @param column- the index of the column
		 * @return the name of the column
		 */
		@Override
		public String getColumnName(int column) {
		    return columnNames[column];
		}
		

	}
	
	JPanel topPanel;					//declare all the components you will be using
	JPanel rightPanel;
	JPanel searchOptions;
	JButton add;
	JButton search;
	JLabel soln;
	JTextField metro;
	JLabel metroLabel;
	JTextField cont;
	JLabel contLabel;
	JTextField pop;
	JLabel popLabel;
	JTable customTable;
	JScrollPane scrollpane;
	customTableModel custmod;
	
	
	String[] popCompChoices = {"Population Greater Than", "Population Less Than"};;
	JComboBox popComp = new JComboBox(popCompChoices);
	String[] searchExactChoices = {"Exact Match", "Partial Match"};
	JComboBox searchExact = new JComboBox(searchExactChoices);
	
	Statement stmt;
	Connection con;
	
	public DatabaseFrame(){
		super("Metropolis Viewer");   //uses the superclass's constructor
		
		
		
		
		try{
			Class.forName("com.mysql.jdbc.Driver"); 
			con = DriverManager.getConnection( "jdbc:mysql://mysql-user.stanford.edu", account ,password);
			stmt = con.createStatement();
			stmt.executeQuery("USE " + database);
		}
		catch (SQLException e) {
	//		System.out.println("SQL Exception while trying to set up the JDBC connection");
	//		e.printStackTrace();
			return;
		}
		catch (ClassNotFoundException e) {
	//		System.out.println("Class Not Found Exception while trying to set up the JDBC connection");
	//		e.printStackTrace();
			return;
		}
		

		
		topPanel = new JPanel();
		rightPanel = new JPanel();
		searchOptions = new JPanel();
		searchOptions.setLayout(new FlowLayout());
		searchOptions.setPreferredSize(new Dimension(300,300));
		add = new JButton("Add");
		search = new JButton("Search");
		metroLabel = new JLabel("Metropolis:");
		contLabel = new JLabel("Continent:");
		popLabel = new JLabel("Population:");
		metro = new JTextField(20);
		cont = new JTextField(20);
		pop = new JTextField(20);
		popComp.setPreferredSize(new Dimension(250,30));
		searchExact.setPreferredSize(new Dimension(250,30));
		
		
		custmod = new customTableModel();
		customTable = new JTable(custmod);
		customTable.setShowGrid(true);
		scrollpane = new JScrollPane(customTable);
		
		
		

		this.setLayout(new BorderLayout(4,4));
		
		rightPanel.setLayout(new BoxLayout(rightPanel,BoxLayout.Y_AXIS));
		rightPanel.setPreferredSize(new Dimension(320,0));
		
		searchOptions.setBorder(new TitledBorder("Search Options"));
		searchOptions.add(popComp);
		searchOptions.add(searchExact);
		topPanel.add(metroLabel);
		topPanel.add(metro);
		topPanel.add(contLabel);
		topPanel.add(cont);
		topPanel.add(popLabel);
		topPanel.add(pop);
		rightPanel.setLayout(new BoxLayout(rightPanel,BoxLayout.Y_AXIS));
		rightPanel.add(add);
		rightPanel.add(search);
		rightPanel.add(searchOptions);
		this.add(topPanel, BorderLayout.NORTH);
		this.add(rightPanel,BorderLayout.EAST);
		this.add(scrollpane,BorderLayout.CENTER);
		
		
		add.addActionListener( 
				new ActionListener() {
					public void actionPerformed(ActionEvent addPress){
						String metroText;
						String continentText;
						String popText;			
						metroText = metro.getText();
						continentText = cont.getText();
						popText = pop.getText();
						addEntry(metroText,continentText,popText,stmt,customTable);
					}
				}		
		);
		
		
		search.addActionListener( 
				new ActionListener() {
					public void actionPerformed(ActionEvent searchPress){
						String metroText;
						String continentText;
						String popText;			
						metroText = metro.getText();
						continentText = cont.getText();
						popText = pop.getText();
						String popCompResult = (String)popComp.getSelectedItem();
						String searchExactResult = (String)searchExact.getSelectedItem();
						searchDatabase(metroText,continentText,popText,popCompResult,searchExactResult, stmt,customTable);
					}
				}		
		);
		
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		pack();
		setVisible(true);
	
	}  // end constructor
	
	public void addEntry(String city, String continent, String population, Statement stmt, JTable table){
		if((continent.length()==0)||(population.length()==0)||(continent.length()==0)){
		//	System.out.println("There are empty fields when trying to add data! Please fill them out.");  //check to make sure no missing fields
			return;
		}
		try{
			customTableModel temp = (customTableModel)(table.getModel());
			stmt.executeUpdate("INSERT INTO "+tableName+" VALUES(\""+city+"\",\""+continent+"\",\""+population+"\")");
			searchDatabase(city,continent,"","Population Greater Than","Exact Match",stmt,customTable);
		}
		catch (SQLException e) {
		//	System.out.println("SQL Exception while trying to add");
		//	e.printStackTrace();
			return;
		}
		
	}
	
	//search function takes into account the 2 pulldowns, plus whether text boxes are blank. 
	public void searchDatabase(String city, String continent, String population,Object compare,Object search, Statement stmt,JTable table){
		String compareType = (String)compare;
		String searchType = (String)search;
		long popLong;
		if(continent.length()==0) continent = "%";   //if blank textboxes when searching, use wildcard to indicate don't care
		if(city.length()==0) city = "%";
		if(population.length()==0) population = "0";   //case where pop textbox is blank
		else{
			try{    //check if the population field was a valid number, throw error if not
				popLong = Long.parseLong(population);   //otherwise, get the number out of it
			}
			catch(NumberFormatException n){
		//		System.out.println("Invalid population number.  Please use only digits.");
				return;
			}
		}
		
	
		ResultSet rs;	
		try{
			if(compareType=="Population Greater Than"){
				if(searchType=="Exact Match"){							
					rs = stmt.executeQuery("SELECT * FROM "+tableName+" WHERE population > "+population+" AND metropolis LIKE \""+city+"\" AND continent LIKE \""+continent+"\";");		
				}
				else{  //search type is partial match					
					rs = stmt.executeQuery("SELECT * FROM "+tableName+" WHERE population > "+population+" AND metropolis LIKE \"%"+city+"%\" AND continent LIKE \"%"+continent+"%\";");			
				}	
			}
			else{   //population type is less than
				if(searchType=="Exact Match"){							
					rs = stmt.executeQuery("SELECT * FROM "+tableName+" WHERE population < "+population+" AND metropolis LIKE \""+city+"\" AND continent LIKE \""+continent+"\";");		
				}
				else{  //search type is partial match					
					rs = stmt.executeQuery("SELECT * FROM "+tableName+" WHERE population < "+population+" AND metropolis LIKE \"%"+city+"%\" AND continent LIKE \"%"+continent+"%\";");			
				}	
			}
			
		//Results have been stored in rs at this point

			
			customTableModel temp = new customTableModel(rs);  //creates new model using this data
			customTable.setModel(temp);    //updates the Jtable's model to this new one	
			temp.fireTableDataChanged();		//tell the model to redraw
		
		}
		catch (SQLException e) {
	//		System.out.println("SQL Exception while trying to search");
	//		e.printStackTrace();
			return;
		}
		
	}
	
	
	public static void main(String[] args) { 

		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception ignored) { }
		
		DatabaseFrame frame = new DatabaseFrame();
				
	} //end main
}