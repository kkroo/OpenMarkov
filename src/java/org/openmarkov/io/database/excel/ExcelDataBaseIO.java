/*
* Copyright 2011 CISIAD, UNED, Spain
*
* Licensed under the European Union Public Licence, version 1.1 (EUPL)
*
* Unless required by applicable law, this code is distributed
* on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
*/

package org.openmarkov.io.database.excel;


import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.openmarkov.core.io.database.CaseDatabase;
import org.openmarkov.core.io.database.CaseDatabaseReader;
import org.openmarkov.core.io.database.CaseDatabaseWriter;
import org.openmarkov.core.io.database.plugin.CaseDatabaseFormat;
import org.openmarkov.core.model.network.NodeType;
import org.openmarkov.core.model.network.ProbNet;
import org.openmarkov.core.model.network.ProbNode;
import org.openmarkov.core.model.network.State;
import org.openmarkov.core.model.network.Variable;
import org.openmarkov.core.model.network.VariableType;
import org.openmarkov.core.model.network.type.BayesianNetworkType;

/** This class contains some routines to load a database from a '.xls' file 
 * (the format used by Excel). The first line of the file has the names of
 * the variables.
 * @author joliva
 * @author manuel       
 * @author fjdiez               
 * @version 1.0
 * @since OpenMarkov 1.0 */
@CaseDatabaseFormat(extension = "xls", name = "Excel")
public class ExcelDataBaseIO implements CaseDatabaseReader, CaseDatabaseWriter {

    /**
     * Opens a database from a '.xls' file and creates a ProbNet
     * building the variables and states dinamically while reading, and 
     * returning the cases on the database.
     *
     * @param fileName <code>String</code> with the name of the 
     * file where the network is saved.
     * @return <code>int[][]</code> matrix with the cases in the database.
     * @throws IOException if the file does not exist or hte file format is not
     * correct.
     * @throws Exception if any other error occurs.
     */
    @SuppressWarnings({ "unchecked", "static-access" })
    public CaseDatabase load (String fileName)
        throws IOException
    {        
        HSSFRow row;
        HSSFCell cell; 
        int rows = 0, columns = 0, numVariables = 0, index = 0;
        int[][] cases= null;
        String stateName="";
        State state;
        List<String> variableNames=new ArrayList<String>();
        /* Each field stores an ArrayList with the StateNames of the 
         * corresponding variable */
        List<State>[] variableStates; 
        
        //create a POIFSFileSystem object to read the data
        POIFSFileSystem fs = new POIFSFileSystem(new FileInputStream(fileName));
        // create a workbook out of the input stream
        HSSFWorkbook wb = new HSSFWorkbook(fs);
        // get a reference to the worksheet
        HSSFSheet sheet = wb.getSheetAt(0);
        
        // number of rows and columns
        rows = sheet.getPhysicalNumberOfRows();
        if (rows == 0){
            throw(new IOException("Bad format file: Empty file."));
        }
        columns = sheet.getRow(0).getPhysicalNumberOfCells();
        numVariables = columns;
        cases=new int[rows-1][columns];
        variableStates = (ArrayList<State>[]) new ArrayList[columns];

        // the first row contains the names of the variables
        row=sheet.getRow(0);
        for (int i = 0; i < columns; i++){
            variableNames.add(row.getCell(i).getRichStringCellValue()
                    .getString());
            variableStates[i] = new ArrayList<State>();
        }
        
        // Gets a int[][] variable that represents the excel file.
        // The first coordinate is the variable index and the second
        // is the row index of the excel datasheet. 
        // A cell contains the StateName index corresponding to
        // the String in the excel file cell
        for (int i = 1; i < rows; i++){
            row = sheet.getRow(i);
            if(row != null){
                columns=row.getPhysicalNumberOfCells();
                for (int j=0; j<numVariables; j++){
                    cell = row.getCell(j); 
                    /* we can have a string or an integer. If we have an
                     absent value, cell is null*/
                    if ((cell == null) || (cell.getCellType()==cell.
                            CELL_TYPE_BLANK))
                        stateName = "?";
                    else if (cell.getCellType()==HSSFCell.CELL_TYPE_STRING){
                        if (cell.getRichStringCellValue().length()==0)
                            stateName = "?";
                        else
                            stateName = cell.getRichStringCellValue().
                            getString();
                    }
                    else if (cell.getCellType()==HSSFCell.CELL_TYPE_BOOLEAN) {
                    	stateName = Boolean.toString(cell.
                    			getBooleanCellValue());
                    } else 
                    	stateName = "" + (double) cell.
                			getNumericCellValue();
                    
                    state = new State(stateName);
                    index = variableStates[j].indexOf(state);
                    if (index==-1){
                        variableStates[j].add(state);
                        index = variableStates[j].indexOf(state);
                    }
                    cases[i-1][j]= index;         
                }
            }
        }
            
        /* Creation of the probNet. The probNet only contains variables but
         * no links. */
        ProbNet probNet = new ProbNet(BayesianNetworkType.getUniqueInstance ());
        HashMap<String, String> ioNet = new HashMap<String, String>();
        ioNet.put("Name", fileName);
        int i=0; 
        for (String variableName : variableNames){ 
        	HashMap<String, String> infoNode = new HashMap<String, 
                        String>();
            infoNode.put("Title", variableName);
            infoNode.put("NodeType", NodeType.CHANCE.name());
            infoNode.put("TypeOfVariable", VariableType.FINITE_STATES.name());
            infoNode.put("CoordinateX", new Integer(0).toString());
            infoNode.put("CoordinateY", new Integer(0).toString());
            infoNode.put("UseDefaultStates", new Boolean(false).toString());
            State[] aux = new State[1];
            Variable variable = new Variable(variableName, 
                    (State[])variableStates[i].toArray(aux));
            ProbNode probNode = probNet.addProbNode(variable, NodeType.CHANCE);
            probNode.additionalProperties = infoNode;
            infoNode.put("Name", variableName);
            i++;
        }
        probNet.additionalProperties = ioNet;
        
        return new CaseDatabase (probNet.getVariables (), cases) ;
    }
    
    /** This method writes an Excel database file
     * @param fileName <code>String</code> path to the resulting .xls file
     * @param probNet <code>ProbNet</code> contains the list of variables
     * @param cases <code>int[][]</code> examples to save in the database
     * @throws IOException 
     * @throws java.lang.Exception
     */
    public void save (String fileName, CaseDatabase database)
        throws IOException
    {        
        String data;
        double numericData = 0;
        int numCell = 0;
        
        // create a workbook
        HSSFWorkbook wb = new HSSFWorkbook();
        // create a sheet
        HSSFSheet sheet = wb.createSheet("new sheet");
        // Create a row to put the names of the attributes.
        HSSFRow row = sheet.createRow((short)0);
        
        /* Attributes */
        for (Variable variable : database.getVariables ()){ 
            String variableName = variable.getName();
            // Create a cell and put a value in it.
            HSSFCell cell = row.createCell(numCell, HSSFCell.
                    CELL_TYPE_STRING);
            cell.setCellValue(new HSSFRichTextString(variableName));
            numCell++;
        }
        
        /* Cases */
        int[][] cases = database.getCases ();
        for (int i = 0; i < cases.length; i++){
            row = sheet.createRow((short)i+1);
            for (int j = 0; j < cases[i].length; j++){
                // Create a cell and put a value in it.
                HSSFCell cell = row.createCell(j);
                data = database.getVariables().get(j).getStates()[cases[i][j]].getName();
                if(data.equals("?"))
                    continue;
                try{
                    numericData = Double.parseDouble(data);
                }
                catch (NumberFormatException e){
                    cell.setCellValue(new HSSFRichTextString(data));
                    continue;
                }
                cell.setCellValue(numericData);
            }
        }
            
        // Write the output to a file
        FileOutputStream fileOut = new FileOutputStream(fileName);
        wb.write(fileOut);
        fileOut.close();
    }
}
