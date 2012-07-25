package controller;

public class Test 
{
	public static void main(String[] args) 
	{
		//ImportDatei
		CSVData csv_file = new CSVData("C:/data.csv",";");
		csv_file.print();

	}
}
