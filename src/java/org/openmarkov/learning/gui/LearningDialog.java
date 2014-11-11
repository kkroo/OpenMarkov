/*
* Copyright 2011 CISIAD, UNED, Spain
*
* Licensed under the European Union Public Licence, version 1.1 (EUPL)
*
* Unless required by applicable law, this code is distributed
* on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
*/

/*
 * LearningGUI.java
 *
 * Created on 5 de junio de 2008, 19:59
 */

package org.openmarkov.learning.gui;


import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.geom.Point2D;
import java.io.IOException;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.apache.commons.io.FilenameUtils;
import org.openmarkov.core.exception.ProbNodeNotFoundException;
import org.openmarkov.core.gui.dialog.io.DBReaderFileChooser;
import org.openmarkov.core.gui.dialog.io.DBWriterFileChooser;
import org.openmarkov.core.gui.dialog.io.NetsIO;
import org.openmarkov.core.gui.dialog.io.NetworkFileChooser;
import org.openmarkov.core.gui.loader.element.OpenMarkovLogoIcon;
import org.openmarkov.core.gui.localize.StringDatabase;
import org.openmarkov.core.gui.plugin.ToolPlugin;
import org.openmarkov.core.gui.window.MainPanel;
import org.openmarkov.core.gui.window.edition.NetworkPanel;
import org.openmarkov.core.io.database.CaseDatabase;
import org.openmarkov.core.io.database.CaseDatabaseReader;
import org.openmarkov.core.io.database.CaseDatabaseWriter;
import org.openmarkov.core.io.database.plugin.CaseDatabaseManager;
import org.openmarkov.core.model.graph.Graph;
import org.openmarkov.core.model.graph.Node;
import org.openmarkov.core.model.network.ProbNet;
import org.openmarkov.core.model.network.ProbNode;
import org.openmarkov.core.model.network.Variable;
import org.openmarkov.core.model.network.VariableType;
import org.openmarkov.learning.core.LearningManager;
import org.openmarkov.learning.core.algorithm.LearningAlgorithm;
import org.openmarkov.learning.core.exception.LatentVariablesException;
import org.openmarkov.learning.core.preprocess.Discretization;
import org.openmarkov.learning.core.preprocess.FilterDatabase;
import org.openmarkov.learning.core.preprocess.MissingValues;
import org.openmarkov.learning.core.util.ModelNetUse;
import org.openmarkov.learning.gui.interactive.InteractiveLearningDialog;

/** GUI to the learning option
 * @author joliva
 * @author manuel      
 * @author fjdiez          
 * @author ibermejo
 * @version 1.1
 * @since OpenMarkov 1.0 */
@SuppressWarnings("serial")
@ToolPlugin( name="Learning", command="Tools.Learning")
public class LearningDialog extends javax.swing.JDialog {
    
    private CaseDatabase database;
    
    private ProbNet modelNet;
    
    private boolean[] isNumeric;
    
    private static String databasePath = null;
    private static String databaseName = null;
    private String fileName = null;
    
    
    private JFrame parent;
    
    private AlgorithmConfigurationManager algorithmConfigurationManager;
    private CaseDatabaseManager caseDbManager;
    
    private AlgorithmParametersDialog optionsGUI;

    /**
     * String database 
     */
    protected StringDatabase stringDatabase = StringDatabase.getUniqueInstance ();    
    /**
     * 
     * Constructor for LearningGUI.
     * @param parent
     */
    public LearningDialog(JFrame parent) {
    	
    	super(parent,true);
    	this.parent = parent;
    	
    	algorithmConfigurationManager = new AlgorithmConfigurationManager (parent);
    	caseDbManager = new CaseDatabaseManager ();
    	
        initComponents();
        setIconImage(OpenMarkovLogoIcon.getUniqueInstance().
        		getOpenMarkovLogoIconImage16());
        varSelectionPanel.setLayout(new GridLayout(0,1,0,20));
        missingValuesPanel.setLayout(new GridLayout(0,1,0,20));
        discretizePanel.setLayout(new GridLayout(0,1,0,20));     
        numIntervalsPanel.setLayout(new GridLayout(0,1,0,20));
        
        try
        {
            UIManager.setLookAndFeel( UIManager.getSystemLookAndFeelClassName());
        }
        catch (ClassNotFoundException | InstantiationException | IllegalAccessException
                | UnsupportedLookAndFeelException e)
        {
            e.printStackTrace();
        }         

        setDefaultCloseOperation(HIDE_ON_CLOSE);
        setLocationRelativeTo(null);
        modelNetButtonGroup = new ButtonGroup();
        modelNetButtonGroup.add( fromFileRadioButton );
        modelNetButtonGroup.add( fromOpenMarkovRadioButton );
        modelNetButtonGroup.add( noModelNetRadioButton );
        variablesButtonGroup = new ButtonGroup();
        variablesButtonGroup.add(allVariablesRadioButton);
        variablesButtonGroup.add(selectedVariablesRadioButton);
        variablesButtonGroup.add(modelNetVariablesRadioButton);
        fromOpenMarkovRadioButton.setEnabled (MainPanel.getUniqueInstance().
                            getMainPanelListenerAssistant().
                            getCurrentNetworkPanel() != null);
        modelNetVariablesRadioButton.setEnabled (modelNet != null);
        
        if(databasePath != null)
        {
            caseFileTextPane.setText(databaseName);
            loadCaseFile (databasePath);
        }
        
        setVisible(true);
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    @SuppressWarnings({"unchecked", "rawtypes"})
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        databaseFileChooser = new DBReaderFileChooser (true);
        modelNetFileChooser = new NetworkFileChooser();
        savePreprocessFileChooser = new DBWriterFileChooser ();
        modelNetButtonGroup = new javax.swing.ButtonGroup();
        variablesButtonGroup = new javax.swing.ButtonGroup();
        LearningTypeButtonGroup = new javax.swing.ButtonGroup();
        generalTabbedPane = new javax.swing.JTabbedPane();
        generalPanel = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        caseFileTextPane = new javax.swing.JTextPane();
        loadCaseFileButton = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        AlgorithmLabel = new javax.swing.JLabel();
        algorithmComboBox = new javax.swing.JComboBox<String>();
        jPanel6 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        optionsTextArea = new javax.swing.JTextArea();
        optionsButton = new javax.swing.JButton();
        jPanel7 = new javax.swing.JPanel();
        InteractiveLearningRadioButton = new javax.swing.JRadioButton();
        automaticLearningRadioButton = new javax.swing.JRadioButton();
        modelNetPanel = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        noModelNetRadioButton = new javax.swing.JRadioButton();
        fromFileRadioButton = new javax.swing.JRadioButton();
        fromOpenMarkovRadioButton = new javax.swing.JRadioButton();
        jScrollPane3 = new javax.swing.JScrollPane();
        modelNetTextPane = new javax.swing.JTextPane();
        jLabel2 = new javax.swing.JLabel();
        loadModelNetButton = new javax.swing.JButton();
        jPanel4 = new javax.swing.JPanel();
        addLinkModelNet = new javax.swing.JCheckBox();
        deleteLinksModelNet = new javax.swing.JCheckBox();
        invertLinksModelNet = new javax.swing.JCheckBox();
        useNodePositionsCheckBox = new javax.swing.JCheckBox();
        startFromModelNetCheckBox = new javax.swing.JCheckBox();
        variablesPanel = new javax.swing.JPanel();
        jScrollPane4 = new javax.swing.JScrollPane();
        showVariablesPanel = new javax.swing.JPanel();
        missingValuesPanel = new javax.swing.JPanel();
        discretizePanel = new javax.swing.JPanel();
        numIntervalsPanel = new javax.swing.JPanel();
        varSelectionPanel = new javax.swing.JPanel();
        missingValuesComboBox = new javax.swing.JComboBox<String>();
        jLabel6 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        discretizeComboBox = new javax.swing.JComboBox<String>();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        numIntervalsSpinner = new JSpinner(new SpinnerNumberModel(2,
            2,20,1));
        jLabel10 = new javax.swing.JLabel ();
        numIntervalsCheckBox = new javax.swing.JCheckBox ();
        allVariablesRadioButton = new javax.swing.JRadioButton ();
        selectedVariablesRadioButton = new javax.swing.JRadioButton ();
        modelNetVariablesRadioButton = new javax.swing.JRadioButton ();
        selectDeselectCheckBox = new javax.swing.JCheckBox ();
        jPanel5 = new javax.swing.JPanel ();
        learnButton = new javax.swing.JButton ();
        cancelButton = new javax.swing.JButton ();
        savePreprocessButton = new javax.swing.JButton ();
        resetButton = new javax.swing.JButton ();
        
        setDefaultCloseOperation (javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle (stringDatabase.getString ("Learning.Title"));
        jPanel1.setBorder (javax.swing.BorderFactory.createTitledBorder (stringDatabase.getString ("Learning.CaseFile")));
        caseFileTextPane.setEditable (false);
        caseFileTextPane.setEnabled (false);
        jScrollPane2.setViewportView (caseFileTextPane);
        loadCaseFileButton.setText (stringDatabase.getString ("Learning.Open"));
        loadCaseFileButton.addActionListener (new java.awt.event.ActionListener ()
            {
                public void actionPerformed (java.awt.event.ActionEvent evt)
                {
                    loadCaseFileButtonActionPerformed (evt);
                }
            });

    jLabel1.setText(stringDatabase.getString ("Learning.CaseFile")); 

    org.jdesktop.layout.GroupLayout jPanel1Layout = new org.jdesktop.layout.GroupLayout(jPanel1);
    jPanel1.setLayout(jPanel1Layout);
    
    jPanel1Layout.setHorizontalGroup(
        jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
        .add(jPanel1Layout.createSequentialGroup()
            .addContainerGap()
            .add(jLabel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
            .add(jScrollPane2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 400, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
            .add(25, 25, 25)
            .add(loadCaseFileButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 90, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
            .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
    );
    jPanel1Layout.setVerticalGroup(
        jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
        .add(jPanel1Layout.createSequentialGroup()
            .addContainerGap()
            .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                .add(jLabel1)
                .add(jScrollPane2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 22, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(25, 25, 25)
                .add(loadCaseFileButton))
            .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
    );

    jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder(stringDatabase.getString("Learning.Algorithm"))); 
    jPanel3.setPreferredSize(new java.awt.Dimension(600, 203));

    AlgorithmLabel.setText(stringDatabase.getString("Learning.Algorithm")); 

    algorithmComboBox.setModel(new javax.swing.DefaultComboBoxModel(LearningManager.getAlgorithmNames().toArray ()));
    algorithmComboBox.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            algorithmComboBoxActionPerformed(evt);
        }
    });
    
    String selectedAlgorithm = (String) algorithmComboBox.getSelectedItem ();
    optionsGUI = algorithmConfigurationManager.getByName (selectedAlgorithm);
    optionsButton.setEnabled (optionsGUI != null);
    optionsTextArea.setText ((optionsGUI != null)? optionsGUI.getDescription () : "");

    org.jdesktop.layout.GroupLayout jPanel6Layout = new org.jdesktop.layout.GroupLayout(jPanel6);
    jPanel6.setLayout(jPanel6Layout);
    jPanel6Layout.setHorizontalGroup(
        jPanel6Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
        .add(0, 0, Short.MAX_VALUE)
    );
    jPanel6Layout.setVerticalGroup(
        jPanel6Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
        .add(0, 0, Short.MAX_VALUE)
    );

    optionsTextArea.setColumns(20);
    optionsTextArea.setRows(3);
    //TODO: Set default
    //optionsTextArea.setText(HillClimbingOptionsGUI.getUniqueInstance(parent).getDescription());
    jScrollPane1.setViewportView(optionsTextArea);

    optionsButton.setText(stringDatabase.getString("Learning.Options")); 
    optionsButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            optionsButtonActionPerformed(evt);
        }
    });

    org.jdesktop.layout.GroupLayout jPanel3Layout = new org.jdesktop.layout.GroupLayout(jPanel3);
    jPanel3.setLayout(jPanel3Layout);
    jPanel3Layout.setHorizontalGroup(
        jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
        .add(jPanel3Layout.createSequentialGroup()
            .addContainerGap()
            .add(jPanel6, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        .add(jPanel3Layout.createSequentialGroup()
            .add(33, 33, 33)
            .add(jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                .add(jPanel3Layout.createSequentialGroup()
                    .add(AlgorithmLabel)
                    .add(40, 40, 40)
                    .add(algorithmComboBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 369, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(0, 0, Short.MAX_VALUE))
                .add(jScrollPane1))
            .add(18, 18, 18)
            .add(optionsButton)
            .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
    );
    jPanel3Layout.setVerticalGroup(
        jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
        .add(jPanel3Layout.createSequentialGroup()
            .add(jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                .add(AlgorithmLabel)
                .add(algorithmComboBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
            .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
            .add(jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                .add(optionsButton)
                .add(jScrollPane1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
            .add(22, 22, 22)
            .add(jPanel6, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addContainerGap())
    );

    jPanel7.setBorder(javax.swing.BorderFactory.createTitledBorder(stringDatabase.getString("Learning.LearningType"))); 

    LearningTypeButtonGroup.add(InteractiveLearningRadioButton);
    InteractiveLearningRadioButton.setText(stringDatabase.getString("Learning.Interactive")); 

    LearningTypeButtonGroup.add(automaticLearningRadioButton);
    automaticLearningRadioButton.setText(stringDatabase.getString("Learning.Automatic")); 
    InteractiveLearningRadioButton.setSelected(true);

    org.jdesktop.layout.GroupLayout jPanel7Layout = new org.jdesktop.layout.GroupLayout(jPanel7);
    jPanel7.setLayout(jPanel7Layout);
    jPanel7Layout.setHorizontalGroup(
        jPanel7Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
        .add(jPanel7Layout.createSequentialGroup()
            .add(33, 33, 33)
            .add(jPanel7Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                .add(automaticLearningRadioButton)
                .add(InteractiveLearningRadioButton))
            .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
    );
    jPanel7Layout.setVerticalGroup(
        jPanel7Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
        .add(jPanel7Layout.createSequentialGroup()
            .add(InteractiveLearningRadioButton)
            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
            .add(automaticLearningRadioButton)
            .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
    );

    org.jdesktop.layout.GroupLayout generalPanelLayout = new org.jdesktop.layout.GroupLayout(generalPanel);
    generalPanel.setLayout(generalPanelLayout);
    generalPanelLayout.setHorizontalGroup(
        generalPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
        .add(generalPanelLayout.createSequentialGroup()
            .addContainerGap()
            .add(generalPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                .add(jPanel3, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .add(jPanel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .add(jPanel7, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addContainerGap ())
    );
    generalPanelLayout.setVerticalGroup(
        generalPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
        .add(generalPanelLayout.createSequentialGroup()
            .addContainerGap()
            .add(jPanel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
            .add(jPanel3, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 121, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .add(jPanel7, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
            .add(50, 50, 50))
    );

    generalTabbedPane.addTab(stringDatabase.getString("Learning.General"), generalPanel); 

    jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(stringDatabase.getString("Learning.ChooseModelNet"))); 

    noModelNetRadioButton.setSelected(true);
    noModelNetRadioButton.setText(stringDatabase.getString("Learning.DontUseModelNet")); 
    noModelNetRadioButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            noModelNetRadioButtonActionPerformed(evt);
        }
    });

    fromFileRadioButton.setText(stringDatabase.getString("Learning.LoadModelNetFromFile")); 
    fromFileRadioButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            fromFileRadioButtonActionPerformed(evt);
        }
    });

    fromOpenMarkovRadioButton.setText(stringDatabase.getString("Learning.TakeOpenModelNet")); 
    fromOpenMarkovRadioButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            fromOpenMarkovRadioButtonActionPerformed(evt);
        }
    });

    modelNetTextPane.setEditable(false);
    modelNetTextPane.setEnabled(false);
    jScrollPane3.setViewportView(modelNetTextPane);

    jLabel2.setText(stringDatabase.getString("Learning.ModelNet")+":"); 

    loadModelNetButton.setText(stringDatabase.getString("Learning.Open")); 
    loadModelNetButton.setEnabled(false);
    loadModelNetButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            loadModelNetButtonActionPerformed(evt);
        }
    });

    org.jdesktop.layout.GroupLayout jPanel2Layout = new org.jdesktop.layout.GroupLayout(jPanel2);
    jPanel2.setLayout(jPanel2Layout);
    jPanel2Layout.setHorizontalGroup(
        jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
        .add(jPanel2Layout.createSequentialGroup()
            .addContainerGap()
            .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                .add(jPanel2Layout.createSequentialGroup()
                    .add(jLabel2)
                    .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                    .add(jScrollPane3)
                    .add(22, 22, 22))
                .add(jPanel2Layout.createSequentialGroup()
                    .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                        .add(jPanel2Layout.createSequentialGroup()
                            .add(fromFileRadioButton)
                            .add(50, 50, 50)
                            .add(loadModelNetButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 88, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .add(noModelNetRadioButton)
                        .add(fromOpenMarkovRadioButton))
                    .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
    );
    jPanel2Layout.setVerticalGroup(
        jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
        .add(jPanel2Layout.createSequentialGroup()
            .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                .add(jPanel2Layout.createSequentialGroup()
                    .add(noModelNetRadioButton)
                    .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                    .add(fromOpenMarkovRadioButton)
                    .add(2, 2, 2)
                    .add(fromFileRadioButton))
                .add(loadModelNetButton))
            .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
            .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                .add(jLabel2)
                .add(jScrollPane3, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 22, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
            .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
    );

    jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder(stringDatabase.getString("Learning.ModelNetUse"))); 

    addLinkModelNet.setText(stringDatabase.getString("Learning.AllowLinkAddition")); 
    addLinkModelNet.setEnabled(false);

    deleteLinksModelNet.setText(stringDatabase.getString("Learning.AllowLinkRemoval")); 
    deleteLinksModelNet.setEnabled(false);

    invertLinksModelNet.setText(stringDatabase.getString("Learning.AllowLinkInversion")); 
    invertLinksModelNet.setEnabled(false);

    useNodePositionsCheckBox.setText(stringDatabase.getString("Learning.ModelNetUseOnlyPositions"));
    useNodePositionsCheckBox.setEnabled(false);  

    startFromModelNetCheckBox.setText(stringDatabase.getString("Learning.StartFromModelNet"));
    startFromModelNetCheckBox.setEnabled(false);
    startFromModelNetCheckBox.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            startFromModelNetCheckBoxActionPerformed(evt);
        }
    });    
    

    org.jdesktop.layout.GroupLayout jPanel4Layout = new org.jdesktop.layout.GroupLayout(jPanel4);
    jPanel4.setLayout(jPanel4Layout);
    jPanel4Layout.setHorizontalGroup(
        jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
        .add(jPanel4Layout.createSequentialGroup()
            .add(jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                .add(jPanel4Layout.createSequentialGroup()
                    .add(19, 19, 19)
                    .add(jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                        .add(useNodePositionsCheckBox)
                        .add(startFromModelNetCheckBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 244, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                .add(jPanel4Layout.createSequentialGroup()
                    .add(59, 59, 59)
                    .add(jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                        .add(deleteLinksModelNet)
                        .add(invertLinksModelNet)
                        .add(addLinkModelNet))))
            .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
    );
    jPanel4Layout.setVerticalGroup(
        jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
        .add(jPanel4Layout.createSequentialGroup()
            .add(useNodePositionsCheckBox)
            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
            .add(startFromModelNetCheckBox)
            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
            .add(addLinkModelNet)
            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
            .add(deleteLinksModelNet)
            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
            .add(invertLinksModelNet)
            .addContainerGap(17, Short.MAX_VALUE))
    );

    deleteLinksModelNet.getAccessibleContext().setAccessibleName("deleteCheckBox"); 

    org.jdesktop.layout.GroupLayout modelNetPanelLayout = new org.jdesktop.layout.GroupLayout(modelNetPanel);
    modelNetPanel.setLayout(modelNetPanelLayout);
    modelNetPanelLayout.setHorizontalGroup(
        modelNetPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
        .add(modelNetPanelLayout.createSequentialGroup()
            .addContainerGap()
            .add(modelNetPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                .add(jPanel2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .add(jPanel4, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addContainerGap())
    );
    modelNetPanelLayout.setVerticalGroup(
        modelNetPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
        .add(modelNetPanelLayout.createSequentialGroup()
            .addContainerGap()
            .add(jPanel2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
            .add(jPanel4, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .add(16, 16, 16))
    );

    generalTabbedPane.addTab(stringDatabase.getString("Learning.ModelNet"), modelNetPanel); 

    jScrollPane4.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

    showVariablesPanel.setLayout(new java.awt.GridBagLayout());

    org.jdesktop.layout.GroupLayout missingValuesPanelLayout = new org.jdesktop.layout.GroupLayout(missingValuesPanel);
    missingValuesPanel.setLayout(missingValuesPanelLayout);
    missingValuesPanelLayout.setHorizontalGroup(
        missingValuesPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
        .add(0, 200, Short.MAX_VALUE)
    );
    missingValuesPanelLayout.setVerticalGroup(
        missingValuesPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
        .add(0, 232, Short.MAX_VALUE)
    );

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 0;
    gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
    gridBagConstraints.weighty = 1.0;
    gridBagConstraints.insets = new java.awt.Insets(4, 0, 4, 8);
    showVariablesPanel.add(missingValuesPanel, gridBagConstraints);

    org.jdesktop.layout.GroupLayout discretizePanelLayout = new org.jdesktop.layout.GroupLayout(discretizePanel);
    discretizePanel.setLayout(discretizePanelLayout);
    discretizePanelLayout.setHorizontalGroup(
        discretizePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
        .add(0, 150, Short.MAX_VALUE)
    );
    discretizePanelLayout.setVerticalGroup(
        discretizePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
        .add(0, 232, Short.MAX_VALUE)
    );

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 2;
    gridBagConstraints.gridy = 0;
    gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
    gridBagConstraints.weighty = 1.0;
    gridBagConstraints.insets = new java.awt.Insets(4, 0, 4, 8);
    showVariablesPanel.add(discretizePanel, gridBagConstraints);

    org.jdesktop.layout.GroupLayout numIntervalsPanelLayout = new org.jdesktop.layout.GroupLayout(numIntervalsPanel);
    numIntervalsPanel.setLayout(numIntervalsPanelLayout);
    numIntervalsPanelLayout.setHorizontalGroup(
        numIntervalsPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
        .add(0, 50, Short.MAX_VALUE)
    );
    numIntervalsPanelLayout.setVerticalGroup(
        numIntervalsPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
        .add(0, 232, Short.MAX_VALUE)
    );

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 3;
    gridBagConstraints.gridy = 0;
    gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
    gridBagConstraints.weighty = 1.0;
    gridBagConstraints.insets = new java.awt.Insets(4, 0, 4, 0);
    showVariablesPanel.add(numIntervalsPanel, gridBagConstraints);

    org.jdesktop.layout.GroupLayout varSelectionPanelLayout = new org.jdesktop.layout.GroupLayout(varSelectionPanel);
    varSelectionPanel.setLayout(varSelectionPanelLayout);
    varSelectionPanelLayout.setHorizontalGroup(
        varSelectionPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
        .add(0, 150, Short.MAX_VALUE)
    );
    varSelectionPanelLayout.setVerticalGroup(
        varSelectionPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
        .add(0, 232, Short.MAX_VALUE)
    );

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 0;
    gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
    gridBagConstraints.insets = new java.awt.Insets(4, 0, 4, 8);
    showVariablesPanel.add(varSelectionPanel, gridBagConstraints);

    jScrollPane4.setViewportView(showVariablesPanel);

    //ausentValuesComboBox.setModel(new javax.swing.DefaultComboBoxModel(AbsentValues.getOptions()));
    missingValuesComboBox.addItem(stringDatabase.getString("Learning.DefaultOption"));
    missingValuesComboBox.addItem(stringDatabase.getString("Learning.MissingValues.KeepMissing"));
    missingValuesComboBox.addItem(stringDatabase.getString("Learning.MissingValues.Eliminate"));
    //ausentValuesComboBox.insertItemAt(AbsentValues.defaultOption, 0);
    missingValuesComboBox.setSelectedItem(0);
    missingValuesComboBox.setPreferredSize(new java.awt.Dimension(27, 20));
    missingValuesComboBox.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            missingValuesComboBoxActionPerformed(evt);
        }
    });

    jLabel6.setText(stringDatabase.getString("Learning.TreatAbsentValues")); 

    jLabel8.setText(stringDatabase.getString("Learning.Discretize")); 

    //discretizeComboBox.setModel(new javax.swing.DefaultComboBoxModel(Discretization.getOptions()));
    discretizeComboBox.addItem(stringDatabase.getString("Learning.DefaultOption"));
    discretizeComboBox.addItem(stringDatabase.getString("Learning.Discretize.NoDiscretize"));
    discretizeComboBox.addItem(stringDatabase.getString("Learning.Discretize.SameFreq"));
    discretizeComboBox.addItem(stringDatabase.getString("Learning.Discretize.SameWidth"));
    discretizeComboBox.setPreferredSize(new java.awt.Dimension(27, 20));
    discretizeComboBox.setSelectedItem(0);
    discretizeComboBox.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            discretizeComboBoxActionPerformed(evt);
        }
    });

    jLabel3.setText(stringDatabase.getString("Learning.Preprocessing")); 

    jLabel4.setText(stringDatabase.getString("Learning.AbsentValues"));   
    jLabel4.setHorizontalAlignment(JLabel.CENTER);

    jLabel5.setText(stringDatabase.getString("Learning.Discretization")); 
    jLabel5.setHorizontalAlignment(JLabel.CENTER);

    jLabel9.setText(stringDatabase.getString("Learning.IntervalCount"));
    jLabel9.setHorizontalAlignment(JLabel.CENTER);

    numIntervalsSpinner.setEnabled(false);
    numIntervalsSpinner.addChangeListener(new javax.swing.event.ChangeListener() {
        public void stateChanged(javax.swing.event.ChangeEvent evt) {
            numIntervalsSpinnerStateChanged(evt);
        }
    });

    jLabel10.setText(stringDatabase.getString("Learning.Intervals")); 

    numIntervalsCheckBox.setText(stringDatabase.getString("Learning.SameIntervalNumber")); 
    numIntervalsCheckBox.setContentAreaFilled(false);
    numIntervalsCheckBox.setMargin(new java.awt.Insets(2, 2, 2, 0));
    numIntervalsCheckBox.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            numIntervalsCheckBoxActionPerformed(evt);
        }
    });

    allVariablesRadioButton.setSelected(true);
    allVariablesRadioButton.setText(stringDatabase.getString("Learning.UseAllVariables")); 
    allVariablesRadioButton.setEnabled(false);
    allVariablesRadioButton.setPreferredSize(new java.awt.Dimension(93, 20));
    allVariablesRadioButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            allVariablesRadioButtonActionPerformed(evt);
        }
    });

    selectedVariablesRadioButton.setText(stringDatabase.getString("Learning.UseSelectedVariables")); 
    selectedVariablesRadioButton.setEnabled(false);
    selectedVariablesRadioButton.setPreferredSize(new java.awt.Dimension(93, 20));
    selectedVariablesRadioButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            selectedVariablesRadioButtonActionPerformed(evt);
        }
    });

    modelNetVariablesRadioButton.setText(stringDatabase.getString("Learning.UseModelNetVariables")); 
    modelNetVariablesRadioButton.setEnabled(false);
    modelNetVariablesRadioButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            modelNetVariablesRadioButtonActionPerformed(evt);
        }
    });

    selectDeselectCheckBox.setText(stringDatabase.getString("Learning.SelectAllVariables")); 
    selectDeselectCheckBox.setEnabled(false);
    selectDeselectCheckBox.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            selectDeselectCheckBoxActionPerformed(evt);
        }
    });

    org.jdesktop.layout.GroupLayout variablesPanelLayout = new org.jdesktop.layout.GroupLayout(variablesPanel);
    variablesPanel.setLayout(variablesPanelLayout);
    variablesPanelLayout.setHorizontalGroup(
        variablesPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
        .add(variablesPanelLayout.createSequentialGroup()
            .add(variablesPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                .add(variablesPanelLayout.createSequentialGroup()
                    .addContainerGap()
                    .add(variablesPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                        .add(allVariablesRadioButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 140, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(variablesPanelLayout.createSequentialGroup()
                            .add(selectedVariablesRadioButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 192, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(28, 28, 28)
                            .add(selectDeselectCheckBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 251, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .add(variablesPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                             .add(variablesPanelLayout.createSequentialGroup()
                                  .add(variablesPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                       .add(jLabel8)
                                       .add(jLabel6))
                                 .add(20, 20, 20)
                                 .add(variablesPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                      .add(discretizeComboBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 278, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                      .add(missingValuesComboBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 278, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                                 .add(50, 50, 50)
                                 .add(variablesPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                                        .add(numIntervalsCheckBox)
                                        .add(variablesPanelLayout.createSequentialGroup()
                                            .add(5, 5, 5)
                                            .add(jLabel10)
                                            .add(10, 10, 10)
                                            .add(numIntervalsSpinner, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 48, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))))
                                 .add(variablesPanelLayout.createSequentialGroup()
                                    .add(21, 21, 21)
                                    .add(jLabel3, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 102, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                    .add(120, 120, 120)
                                    .add(jLabel4, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 90, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                    .add(120, 120, 120)
                                    .add(jLabel5, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 99, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                    .add(30, 30, 30)
                                    .add(jLabel9, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 121, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                    .add(21, 21, 21))
                            .add(22, 22, 22)))
                .add(org.jdesktop.layout.GroupLayout.TRAILING, variablesPanelLayout.createSequentialGroup()
                    .add(10, 10, 10)
                    .add(modelNetVariablesRadioButton, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .add(variablesPanelLayout.createSequentialGroup()
                    .add(jScrollPane4, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 700, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(0, 0, Short.MAX_VALUE)))
            .addContainerGap())
    );
    variablesPanelLayout.setVerticalGroup(
        variablesPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
        .add(org.jdesktop.layout.GroupLayout.TRAILING, variablesPanelLayout.createSequentialGroup()
            .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .add(variablesPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                .add(org.jdesktop.layout.GroupLayout.TRAILING, variablesPanelLayout.createSequentialGroup()
                    .add(variablesPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                        .add(discretizeComboBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(jLabel8))
                    .add(4, 4, 4)
                    .add(variablesPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                        .add(jLabel6)
                        .add(missingValuesComboBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(numIntervalsSpinner, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                .add(org.jdesktop.layout.GroupLayout.TRAILING, variablesPanelLayout.createSequentialGroup()
                    .add(numIntervalsCheckBox)
                    .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                    .add(jLabel10)))
            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
            .add(allVariablesRadioButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
            .add(modelNetVariablesRadioButton)
            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
            .add(variablesPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                .add(selectedVariablesRadioButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(selectDeselectCheckBox))
            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
            .add(variablesPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                .add(jLabel4)
                .add(jLabel3)
                .add(jLabel9)
                .add(jLabel5))
            .add(5, 5, 5)
            .add(jScrollPane4, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 180, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
            .add(52, 52, 52))
    );

    generalTabbedPane.addTab("   "+stringDatabase.getString("Learning.Preprocessing")+ "   ", variablesPanel); 

    learnButton.setText(stringDatabase.getString("Learning.LearnNet")); 
    learnButton.setEnabled(false);
    learnButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            learnButtonActionPerformed(evt);
        }
    });

    cancelButton.setText(stringDatabase.getString("Learning.Cancel")); 
    cancelButton.setPreferredSize(new java.awt.Dimension(99, 23));
    cancelButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            cancelButtonActionPerformed(evt);
        }
    });

    savePreprocessButton.setText(stringDatabase.getString("Learning.SaveDatabase")); 
    savePreprocessButton.setEnabled(false);
    savePreprocessButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            savePreprocessButtonActionPerformed(evt);
        }
    });

    resetButton.setText(stringDatabase.getString("Learning.InitialValues")); 
    resetButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            resetButtonActionPerformed(evt);
        }
    });

    org.jdesktop.layout.GroupLayout jPanel5Layout = new org.jdesktop.layout.GroupLayout(jPanel5);
    jPanel5.setLayout(jPanel5Layout);
    jPanel5Layout.setHorizontalGroup(
        jPanel5Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
        .add(jPanel5Layout.createSequentialGroup()
            .addContainerGap()
            .add(learnButton)
            .add(14, 14, 14)
            .add(savePreprocessButton)
            .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
            .add(resetButton)
            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 12, Short.MAX_VALUE)
            .add(cancelButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 90, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
            .addContainerGap())
    );
    jPanel5Layout.setVerticalGroup(
        jPanel5Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
        .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel5Layout.createSequentialGroup()
            .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .add(jPanel5Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                .add(learnButton)
                .add(cancelButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(resetButton)
                .add(savePreprocessButton))
            .addContainerGap())
    );

    org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
    getContentPane().setLayout(layout);
    layout.setHorizontalGroup(
        layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
        .add(layout.createSequentialGroup()
            .addContainerGap()
            .add(generalTabbedPane, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 600, Short.MAX_VALUE))
        .add(layout.createSequentialGroup()
            .add(170, 170, 170)
            .add(jPanel5, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
            .add(0, 0, Short.MAX_VALUE))
    );
    layout.setVerticalGroup(
        layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
        .add(layout.createSequentialGroup()
            .add(generalTabbedPane, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 359, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
            .add(jPanel5, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
            .addContainerGap())
    );

    pack();
    }// </editor-fold>//GEN-END:initComponents

    @SuppressWarnings("unchecked")
    private void missingValuesComboBoxActionPerformed(
    		java.awt.event.ActionEvent evt) {
    	//GEN-FIRST:event_ausentValuesComboBoxActionPerformed
        String selected = (String) missingValuesComboBox.getSelectedItem();
        
        for( Component component : missingValuesPanel.getComponents() ){
            if (missingValuesComboBox.getSelectedIndex()==0)
                ((JComboBox<String>) component).setEnabled(true);
            else{
                ((JComboBox<String>) component).setSelectedItem(selected);
                ((JComboBox<String>) component).setEnabled(false);
            }
        }
    }//GEN-LAST:event_ausentValuesComboBoxActionPerformed

    private void loadCaseFileButtonActionPerformed(
    		java.awt.event.ActionEvent evt) {
    	//GEN-FIRST:event_loadCaseFileButtonActionPerformed
        databaseFileChooser.setDialogTitle( stringDatabase
                                            .getString( "Learning.OpenDatabase" ) );         
        if(databaseFileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION){
            databasePath = databaseFileChooser.getSelectedFile ().getAbsolutePath (); 
            databaseName = databaseFileChooser.getSelectedFile().getName();
            caseFileTextPane.setText(databaseName);
            loadCaseFile(databasePath);
        }
    }//GEN-LAST:event_loadCaseFileButtonActionPerformed
    
    private void loadCaseFile (String path)
    {
        if ((path != null) && (!path.equals ("")))
        {
            CaseDatabaseReader reader = caseDbManager.getReader (FilenameUtils.getExtension (path));
            if (reader == null)
            {
                JOptionPane.showMessageDialog (null,
                                               stringDatabase.getString ("Learning.IncorrectCaseDatabaseFileFormat"),
                                               stringDatabase.getString ("ErrorWindow.Title.Label"),
                                               JOptionPane.ERROR_MESSAGE);
                databaseFileChooser.setSelectedFile (null);
                caseFileTextPane.setText (null);
                learnButton.setEnabled (false);
                allVariablesRadioButton.setEnabled (false);
                selectedVariablesRadioButton.setEnabled (false);
                modelNetVariablesRadioButton.setEnabled (false);
                selectDeselectCheckBox.setEnabled (false);
            }
            else
            {
                try
                {
                    // Load the database
                    if (databasePath != null)
                    {
                        database = reader.load (databasePath);
                        updateVariableSelectionPanel ();
                        learnButton.setEnabled (true);
                        savePreprocessButton.setEnabled (true);
                        numIntervalsCheckBox.setSelected (false);
                        numIntervalsSpinner.setEnabled (false);
                        modelNetVariablesRadioButton.setEnabled (modelNet != null);
                        if (modelNet == null)
                        {
                            allVariablesRadioButton.setSelected (true);
                        }
                    }
                }
                catch (IOException ex)
                {
                    Logger.getLogger (LearningDialog.class.getName ()).log (Level.SEVERE, null, ex);
                }
            }
        }
        else learnButton.setEnabled (false);
    }

    private void loadModelNetButtonActionPerformed(
    		java.awt.event.ActionEvent evt){
    	//GEN-FIRST:event_loadModelNetButtonActionPerformed
        if(fromFileRadioButton.isSelected()){
        	
        	String modelNetFilePath = requestNetworkFileToOpen();
        	
            if(fileName != null){
                if(!isSupportedNetFormat(fileName)){
                    JOptionPane.showMessageDialog (null,
                                                   stringDatabase.getString ("Learning.IncorrectFileFormat"),
                                                   stringDatabase.getString ("ErrorWindow.Title.Label"),
                                                   JOptionPane.ERROR_MESSAGE);
                    modelNetFileChooser.setSelectedFile(null);
                    modelNetTextPane.setText(null);
                    addLinkModelNet.setEnabled(false);
                    deleteLinksModelNet.setEnabled(false);
                    invertLinksModelNet.setEnabled(false);
                    modelNet = null;
                }
                else{
                	try{
                		modelNetTextPane.setText(fileName);
                		modelNet = NetsIO.openNetworkFile(modelNetFilePath).getProbNet ();
                		modelNetSelected ();
                	} catch (Exception e){
                        JOptionPane.showMessageDialog (null,
                                                       stringDatabase.getString ("Learning.UnableToLoadModelNet"),
                                                       stringDatabase.getString ("ErrorWindow.Title.Label"),
    							JOptionPane.ERROR_MESSAGE);
                		e.printStackTrace();
                	}
                }  
            }
        }
    }//GEN-LAST:event_loadModelNetButtonActionPerformed
    
    /**
	 * It asks the user to choose a file by means of a open-file dialog box.
	 * 
	 * @return complete path of the file, or null if the user selects cancel.
	 */
	private String requestNetworkFileToOpen() {

	    String filePath = null;
		modelNetFileChooser.setDialogTitle( stringDatabase.getString( "OpenNetwork.Title.Label" ) ); 
		if(modelNetFileChooser.showOpenDialog(this.parent) == JFileChooser.APPROVE_OPTION)
		{
		    filePath = modelNetFileChooser.getSelectedFile().getAbsolutePath();
		    fileName = modelNetFileChooser.getSelectedFile().getName();
		}else
		{
		    filePath = null;
		    fileName = null;
		}

		return filePath;

	}

    @SuppressWarnings("unchecked")
    private void discretizeComboBoxActionPerformed(
    		java.awt.event.ActionEvent evt) {
    	//GEN-FIRST:event_discretizeComboBoxActionPerformed
        String selected = (String) discretizeComboBox.getSelectedItem();
        
        int i = 0;
        for (Component component : discretizePanel.getComponents ())
        {
            JComboBox<String> variableDiscretizeComboBox = ((JComboBox<String>) component); 
            variableDiscretizeComboBox.setEnabled (discretizeComboBox.getSelectedIndex () == 0);
            if (discretizeComboBox.getSelectedIndex () >= 0) 
            {
                variableDiscretizeComboBox.setSelectedItem ((isNumeric[i])?selected:0);
                variableDiscretizeComboBox.setEnabled (isNumeric[i] && discretizeComboBox.getSelectedIndex () == 0);
            }
            ++i;
        }
    }//GEN-LAST:event_discretizeComboBoxActionPerformed

    private void savePreprocessButtonActionPerformed (java.awt.event.ActionEvent evt)
    {
        // GEN-FIRST:event_savePreprocessButtonActionPerformed
        savePreprocessFileChooser.setDialogTitle (stringDatabase.getString( "Learning.SaveDatabase" ));
        if (savePreprocessFileChooser.showSaveDialog (this) == JFileChooser.APPROVE_OPTION)
        {
            String path = savePreprocessFileChooser.getSelectedFile ().getAbsolutePath ();
            if ((path != null) && (!path.equals ("")))
            {
                CaseDatabaseWriter writer = caseDbManager.getWriter (FilenameUtils.getExtension (path));
                if (writer == null)
                {
                    JOptionPane.showMessageDialog (null,
                                                   stringDatabase.getString ("Learning.IncorrectCaseDatabaseFileFormat"),
                                                   stringDatabase.getString ("ErrorWindow.Title.Label"),
                                                   JOptionPane.ERROR_MESSAGE);
                }
                else
                {
                    try
                    {
                        CaseDatabase preprocessedDb = MissingValues.process (database,
                                                                                getSelectedMissingValuesOptions());

                        CaseDatabase discretizedDB = Discretization.process (preprocessedDb,
                                                                             getSelectedDiscretizeOptions (),
                                                                             getSelectedNumIntervals (),
                                                                             modelNet);
                        writer.save (path, discretizedDB);
                    }
                    catch (Exception ex)
                    {
                        Logger.getLogger (LearningDialog.class.getName ()).log (Level.SEVERE, null, ex);
                    }
                }
            }
        }
    }// GEN-LAST:event_savePreprocessButtonActionPerformed

    private Map<String, Integer> getSelectedNumIntervals ()
    {
        Map<String, Integer> selectedNumIntervals = new HashMap<>();
        
        for (int i= 0; i < varSelectionPanel.getComponents().length; ++i) {
            Component comp = varSelectionPanel.getComponents()[i];
            if (((JCheckBox) comp).isSelected()) {
                String variableName = ((JCheckBox) comp).getText();
                int numIntervals = (Integer)((JSpinner)numIntervalsPanel.getComponents()[i]).getValue();
                selectedNumIntervals.put(variableName, numIntervals);
            }
        }        
        return selectedNumIntervals; 
    }

    private Map<String, Discretization.Option> getSelectedDiscretizeOptions ()
    {
        Map<String, Discretization.Option> selectedDiscretizeOptions = new HashMap<>();
        
        for (int i= 0; i < varSelectionPanel.getComponents().length; ++i) {
            Component comp = varSelectionPanel.getComponents()[i];
            if (((JCheckBox) comp).isSelected()) {
                String variableName = ((JCheckBox) comp).getText();
                @SuppressWarnings("unchecked")
                int selectedIndex = ((JComboBox<String>)discretizePanel.getComponents()[i]).getSelectedIndex ();
                Discretization.Option discretizationOption = Discretization.Option.values ()[selectedIndex];
                selectedDiscretizeOptions.put(variableName, discretizationOption);
            }
        }        
        return selectedDiscretizeOptions; 
    }

    private Map<String, MissingValues.Option> getSelectedMissingValuesOptions ()
    {
        Map<String, MissingValues.Option> selectedPreprocessOptions = new HashMap<>();
        
        for (int i= 0; i < varSelectionPanel.getComponents().length; ++i) {
            Component comp = varSelectionPanel.getComponents()[i];
            if (((JCheckBox) comp).isSelected()) {
                String variableName = ((JCheckBox) comp).getText();
                @SuppressWarnings("unchecked")
                int selectedIndex = ((JComboBox<String>)missingValuesPanel.getComponents()[i]).getSelectedIndex ();
                MissingValues.Option  missingValuesOption = MissingValues.Option.values ()[selectedIndex];             
                
                selectedPreprocessOptions.put(variableName, missingValuesOption);
            }
        }        
        return selectedPreprocessOptions;
    }

    private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelButtonActionPerformed
        this.setVisible(false);
    }//GEN-LAST:event_cancelButtonActionPerformed

    private void learnButtonActionPerformed (java.awt.event.ActionEvent evt)
    {// GEN-FIRST:event_learnButtonActionPerformed
        ModelNetUse modelNetUse = null;
        if ((databasePath == null) || (databasePath.equals ("")))
        {
            JOptionPane.showMessageDialog (null,
                                           stringDatabase.getString ("Learning.MustLoadACaseDatabase"),
                                           stringDatabase.getString ("ErrorWindow.Title.Label"),
                                           JOptionPane.ERROR_MESSAGE);
            return;
        }
        List<Variable> selectedVariables = getSelectedVariables ();
        if (selectedVariables.isEmpty ())
        {
            JOptionPane.showMessageDialog (null,
                                           stringDatabase.getString ("Learning.MustChooseVariables"),
                                           stringDatabase.getString ("ErrorWindow.Title.Label"),
                                           JOptionPane.ERROR_MESSAGE);
            return;
        }
        CaseDatabase preprocessedDatabase = null;
        CaseDatabase discretizedDB = null;
        try
        {
            preprocessedDatabase = FilterDatabase.filter(database, selectedVariables);
            preprocessedDatabase = MissingValues.process (preprocessedDatabase, getSelectedMissingValuesOptions ());
            discretizedDB = Discretization.process (preprocessedDatabase,
                                                                 getSelectedDiscretizeOptions (),
                                                                 getSelectedNumIntervals (),
                                                                 modelNet);
        }
        catch (Exception e)
        {
            JOptionPane.showMessageDialog (null,
                                           stringDatabase.getString ("Learning.ErrorPreprocessing"),
                                           stringDatabase.getString ("ErrorWindow.Title.Label"),
                                           JOptionPane.ERROR_MESSAGE);
            e.printStackTrace ();
            discretizedDB = null;
        }
        if (discretizedDB != null)
        {
            if (modelNet != null)
            {
                modelNetUse = new ModelNetUse (true, useNodePositionsCheckBox.isSelected (),
                							   startFromModelNetCheckBox.isSelected(),
                                               addLinkModelNet.isSelected (),
                                               deleteLinksModelNet.isSelected (),
                                               invertLinksModelNet.isSelected ());
            }
            try
            {
                // Initialize learningManager
                LearningManager learningManager = new LearningManager (discretizedDB,
                                                                       algorithmComboBox.getSelectedItem ().toString (),
                                                                       modelNet, modelNetUse);
                LearningAlgorithm learningAlgorithm = null;
                
                if(optionsGUI != null)
                {
                    learningAlgorithm = optionsGUI.getInstance (learningManager.getLearnedNet (), discretizedDB);
                }else
                {
                    learningAlgorithm = learningManager.getAlgorithmInstance (algorithmComboBox.getSelectedItem ().toString ());
                }
                
                if(learningAlgorithm == null)
                {
                    throw new InvalidParameterException("Unable to instance learning algorithm " + algorithmComboBox.getSelectedItem ().toString ());
                }
                
                /* Get current time */
                long start = System.currentTimeMillis ();
                
                learningManager.init (learningAlgorithm);
                
                if (automaticLearningRadioButton.isSelected ())
                {
                    learningManager.learn();
                    /* Get elapsed time in milliseconds */
                    long elapsedTimeMillis = System.currentTimeMillis() - start;
                    System.out.print(stringDatabase.getString("Learning.LearningFinished")
                            + calculateTime(elapsedTimeMillis)
                            + "\n");
                    if (modelNetUse == null || !modelNetUse.isUseModelNet()) {
                        // Place nodes in a sensible way
                        placeNodesInLearnedNet(learningManager.getLearnedNet());
                    }
                }
                else
                {
                    // INTERACTIVE LEARNING
                    InteractiveLearningDialog interactiveLearningGUI = new InteractiveLearningDialog (
                                                                                                this.parent,
                                                                                                false,
                                                                                                learningManager);
                    if( (modelNetUse == null) || (!modelNetUse.isUseNodePositions()) )
                    {
                        placeNodesInCircle (learningManager.getLearnedNet ());
                    }
                    interactiveLearningGUI.setVisible (true);
                }
                ProbNet probNet = learningManager.getLearnedNet ();
                setProperName (probNet);
                NetworkPanel networkPanel = MainPanel.getUniqueInstance ().getMainPanelListenerAssistant ().createNewFrame (probNet);
                probNet.getPNESupport ().addUndoableEditListener (networkPanel);
                this.setVisible (false);
            }
            catch (LatentVariablesException e1)
            {
                JOptionPane.showMessageDialog (null,
                                               stringDatabase.getString ("Learning.Error.LatentVariables") + " :" + e1.getLatentVariables (),
                                               stringDatabase.getString ("ErrorWindow.Title.Label"),
                                               JOptionPane.ERROR_MESSAGE);
            }
            catch (Exception e)
            {
                JOptionPane.showMessageDialog (null,
                                               stringDatabase.getString ("Learning.Error") + ":" + e.getMessage (),
                                               stringDatabase.getString ("ErrorWindow.Title.Label"),
                                               JOptionPane.ERROR_MESSAGE);
                e.printStackTrace ();
            }catch (OutOfMemoryError e1) {
                JOptionPane.showMessageDialog (null,
                        stringDatabase.getString ("Learning.Error.OutOfMemory"),
                        stringDatabase.getString ("ErrorWindow.Title.Label"),
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }// GEN-LAST:event_learnButtonActionPerformed
        
    /* This function gives a proper name to the learned net.
     * It avoids repeating names of other opened networks in OpenMarkov.
     */
    private void setProperName(ProbNet probNet){
    	String name = databasePath.substring(databasePath.lastIndexOf('\\') + 1,
                databasePath.lastIndexOf('.')) + stringDatabase.getString("Learning.NetSuffix"); 
    	probNet.setName(name);
    }
    
    private void numIntervalsCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_numIntervalsCheckBoxActionPerformed
            int i = 0;
            
            if (numIntervalsCheckBox.isSelected()){
                numIntervalsSpinner.setEnabled(true);
                Integer selected = (Integer) numIntervalsSpinner.getValue();

                for( Component component : numIntervalsPanel.getComponents() ){
                    ((JSpinner) component).setValue(selected);
                    ((JSpinner) component).setEnabled(false);
                }
            }
            else{
                numIntervalsSpinner.setEnabled(false);
                for( Component component : numIntervalsPanel.getComponents() ){
                    if(isNumeric[i])
                        ((JSpinner) component).setEnabled(true);
                    i++;
                }
            }
    }//GEN-LAST:event_numIntervalsCheckBoxActionPerformed
    
    private void startFromModelNetCheckBoxActionPerformed (java.awt.event.ActionEvent evt)
    {
    	if ( startFromModelNetCheckBox.isSelected() )
    	{
	        addLinkModelNet.setEnabled(true);
	        addLinkModelNet.setSelected(true);
	        deleteLinksModelNet.setEnabled(true);
	        invertLinksModelNet.setEnabled(true);
    	}
    	else
    	{
    		addLinkModelNet.setEnabled(false);
	        addLinkModelNet.setSelected(false);
	        deleteLinksModelNet.setEnabled(false);
	        invertLinksModelNet.setEnabled(false);
    	}
    }       
        private void numIntervalsSpinnerStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_numIntervalsSpinnerStateChanged
            Integer selected = (Integer) numIntervalsSpinner.getValue();

            for( Component component : numIntervalsPanel.getComponents() ){
                ((JSpinner) component).setValue(selected);
            }
        }//GEN-LAST:event_numIntervalsSpinnerStateChanged

        private void resetButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_resetButtonActionPerformed
            databasePath = null;
            databaseName = null;
            modelNet = null;
            caseFileTextPane.setText(null);
            modelNetTextPane.setText(null);
            noModelNetRadioButton.setEnabled(true);
            resetVariablePanel();
        }//GEN-LAST:event_resetButtonActionPerformed

        private void noModelNetRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_noModelNetRadioButtonActionPerformed
            modelNet = null;
            modelNetTextPane.setText(null);
            loadModelNetButton.setEnabled(false);
            useNodePositionsCheckBox.setSelected(false);
            useNodePositionsCheckBox.setEnabled(false);
            startFromModelNetCheckBox.setEnabled(false);
            startFromModelNetCheckBox.setSelected(false);
            addLinkModelNet.setSelected(false);
            deleteLinksModelNet.setSelected(false);
            invertLinksModelNet.setSelected(false);
            addLinkModelNet.setEnabled(false);
            deleteLinksModelNet.setEnabled(false);
            invertLinksModelNet.setEnabled(false); 
            allVariablesRadioButton.setSelected(true);
        }//GEN-LAST:event_noModelNetRadioButtonActionPerformed

        private void fromOpenMarkovRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fromOpenMarkovRadioButtonActionPerformed
            try{
                    modelNet = MainPanel.getUniqueInstance().
                            getMainPanelListenerAssistant().
                            getCurrentNetworkPanel().getProbNet();
                    modelNetTextPane.setText(null);
                    loadModelNetButton.setEnabled(false);
                    modelNetSelected();
            } catch (Exception e){
                noModelNetRadioButton.setSelected(true);
                JOptionPane.showMessageDialog(
    					null, stringDatabase.getString("Learning.NoOpenNet"), 
    					stringDatabase.getString("ErrorWindow.Title.Label"), 
    					JOptionPane.ERROR_MESSAGE);
            }

        }//GEN-LAST:event_fromOpenMarkovRadioButtonActionPerformed

        private void modelNetSelected()
        {
            modelNetVariablesRadioButton.setSelected(true);
            useNodePositionsCheckBox.setEnabled(true);
            useNodePositionsCheckBox.setSelected(true);
            startFromModelNetCheckBox.setEnabled(true);
            
            updateVariableSelectionPanel ();
            
            discretizeComboBox.addItem(stringDatabase.getString("Learning.Discretize.ModelNet"));
            discretizeComboBox.setSelectedItem (stringDatabase.getString("Learning.Discretize.ModelNet"));
        }

        
        private void fromFileRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fromFileRadioButtonActionPerformed
            loadModelNetButton.setEnabled(true);
        }//GEN-LAST:event_fromFileRadioButtonActionPerformed

        private void allVariablesRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_allVariablesRadioButtonActionPerformed
            selectDeselectCheckBox.setSelected(false);
            selectDeselectCheckBox.setEnabled(false);
            for (Component comp : varSelectionPanel.getComponents()){
                ((JCheckBox) comp).setSelected(true);
                ((JCheckBox) comp).setEnabled(false);
            }
        }//GEN-LAST:event_allVariablesRadioButtonActionPerformed

        private void selectedVariablesRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_selectedVariablesRadioButtonActionPerformed
            selectDeselectCheckBox.setEnabled(true);
            for (Component comp : varSelectionPanel.getComponents()){
                ((JCheckBox) comp).setEnabled(true);
            }
        }//GEN-LAST:event_selectedVariablesRadioButtonActionPerformed

        private void modelNetVariablesRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_modelNetVariablesRadioButtonActionPerformed
            if (modelNet == null){
            	JOptionPane.showMessageDialog(
						null, stringDatabase.getString("Learning.NoModelNet"), 
						stringDatabase.
							getString("ErrorWindow.Title.Label"), 
						JOptionPane.ERROR_MESSAGE);
                allVariablesRadioButton.setSelected(true);
                return;
            }

            selectDeselectCheckBox.setSelected(false);
            selectDeselectCheckBox.setEnabled(false);

            updateVariableSelectionPanel();
      }//GEN-LAST:event_modelNetVariablesRadioButtonActionPerformed

        private void selectDeselectCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_selectDeselectCheckBoxActionPerformed
            boolean value = selectDeselectCheckBox.isSelected();
            for (Component comp : varSelectionPanel.getComponents()){
                ((JCheckBox) comp).setSelected(value);
            }

        }//GEN-LAST:event_selectDeselectCheckBoxActionPerformed

    private void optionsButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_optionsButtonActionPerformed
            optionsGUI.setVisible (true);
            optionsTextArea.setText (optionsGUI.getDescription ());
        }//GEN-LAST:event_optionsButtonActionPerformed
    
    private void algorithmComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_algorithmComboBoxActionPerformed
        String selectedAlgorithm = (String) algorithmComboBox.getSelectedItem();
        optionsGUI = algorithmConfigurationManager.getByName (selectedAlgorithm);
        optionsButton.setEnabled (optionsGUI != null);
        optionsTextArea.setText ((optionsGUI != null)? optionsGUI.getDescription () : "");
    }//GEN-LAST:event_algorithmComboBoxActionPerformed
    
    private void resetVariablePanel(){
        discretizeComboBox.setSelectedIndex(0);
        missingValuesComboBox.setSelectedIndex(0);
        numIntervalsCheckBox.setSelected(false);
        allVariablesRadioButton.setSelected(true);
        numIntervalsSpinner.setValue(new Integer(2));
        numIntervalsSpinner.setEnabled(false);
        varSelectionPanel.removeAll();
        missingValuesPanel.removeAll();
        discretizePanel.removeAll();
        numIntervalsPanel.removeAll();
    }
    
    private void updateVariableSelectionPanel(){
        
        if(database != null)
        {
            varSelectionPanel.removeAll();
            missingValuesPanel.removeAll();
            discretizePanel.removeAll();
            numIntervalsPanel.removeAll();
            int i = 0;
            
            allVariablesRadioButton.setEnabled(true);
            selectedVariablesRadioButton.setEnabled(true);
            
            isNumeric = new boolean[database.getVariables().size ()];

            for(Variable var : database.getVariables()){
                /*JComboBox preprocessOptions = new JComboBox(new javax.swing.
                        DefaultComboBoxModel(AbsentValues.getOptions()));*/
            	JComboBox<String> preprocessOptions = new JComboBox<String>();
            	preprocessOptions.addItem(stringDatabase.getString("Learning.MissingValues.KeepMissing"));
            	preprocessOptions.addItem(stringDatabase.getString("Learning.MissingValues.Eliminate"));
            	preprocessOptions.setSelectedItem(0);
            	preprocessOptions.setPreferredSize(new Dimension(225,18));
                JCheckBox varSelect = new JCheckBox(var.getName());
                varSelect.setPreferredSize(new Dimension(175,18));
                varSelect.setEnabled (false);
                boolean select = true;
                if(modelNetVariablesRadioButton.isSelected ())
                {
                    select = modelNet.containsVariable (var.getName());
                }
                varSelect.setSelected(select);
                /*JComboBox discretizeOptions = new JComboBox(new javax.swing.
                        DefaultComboBoxModel(Discretization.getOptions()));*/
                JComboBox<String> discretizeOptions = new JComboBox<String>(new javax.swing.DefaultComboBoxModel<String>());
                discretizeOptions.addItem(stringDatabase.getString("Learning.Discretize.NoDiscretize"));
                discretizeOptions.addItem(stringDatabase.getString("Learning.Discretize.SameFreq"));
                discretizeOptions.addItem(stringDatabase.getString("Learning.Discretize.SameWidth"));
                if(modelNet != null)
                {
                    discretizeOptions.addItem(stringDatabase.getString("Learning.Discretize.ModelNet"));
                }
                discretizeOptions.setSelectedItem(0);
                discretizeOptions.setPreferredSize(new Dimension(175,18));
                discretizeOptions.addItemListener (new ItemListener()
                    {
                        @SuppressWarnings("unchecked")
						@Override
                        public void itemStateChanged (ItemEvent arg0)
                        {
                            int i = 0;
                            for(Component comboBox : discretizePanel.getComponents ())
                            {
                                if(comboBox.equals (arg0.getSource ()))
                                {
                                    numIntervalsPanel.getComponent (i).setEnabled (isNumeric[i]
                                                                                           && ((JComboBox<String>) comboBox).getSelectedIndex () > 0
                                                                                           && !numIntervalsCheckBox.isSelected ());
                                }
                                ++i;
                            }
                        }
                    });
                JSpinner numIntervals = new JSpinner(new SpinnerNumberModel(2, 2, 20, 1));
                numIntervals.setPreferredSize(new Dimension(50,18));
                isNumeric[i] = Discretization.isNumeric (var);
                discretizeOptions.setEnabled(isNumeric[i] && discretizeComboBox.getSelectedIndex () == 0);
                /* If there is a model net and the variable is in the model net
                 * and is discretized, then the default option should be 'As in model'*/
               if (modelNet != null) {
                	try {
    					VariableType variableTypeInModel = modelNet.getProbNode(
    							var.getName()).getVariable().getVariableType();
    					if (variableTypeInModel == VariableType.DISCRETIZED) {
    						discretizeOptions.setSelectedItem(stringDatabase.getString("Learning.Discretize.ModelNet"));
    					}
    				} catch (ProbNodeNotFoundException e) {
    				}
                }
               numIntervals.setEnabled(isNumeric[i] && discretizeOptions.getSelectedIndex () > 1);
                
                varSelectionPanel.add(varSelect);
                missingValuesPanel.add(preprocessOptions);
                discretizePanel.add(discretizeOptions); 
                numIntervalsPanel.add(numIntervals);
                i++;
            }
            
            varSelectionPanel.revalidate();
            missingValuesPanel.revalidate();
            discretizePanel.revalidate();
            numIntervalsPanel.revalidate();
        }
    }
    
    private List<Variable> getSelectedVariables() {
        List<Variable> variables = new ArrayList<Variable>();
        
        for (Component comp : varSelectionPanel.getComponents()) {
            if (((JCheckBox) comp).isSelected()) {
                variables.add (database.getVariable (((JCheckBox) comp).getText ()));
            }
        }
        return variables;
    }

    /**
     * 
     * @param path
     * @return whether a file format is supported or not
     */
    private static boolean isSupportedNetFormat(String path){
        return (FilenameUtils.getExtension (path).toLowerCase ().equals ("elv")
                || FilenameUtils.getExtension (path).toLowerCase ().equals ("xml")
                || FilenameUtils.getExtension (path).toLowerCase ().equals ("pgmx"));
    }
    
    /**This function returns a <code>String</code> that represents the given 
     * elapsed time in the format: minutes' seconds'' milliseconds ms.
     * 
     * @param elapsedTimeMillis long with the elapsed time.
     * @return <code>String</code> that represents the given time.
     */
    private static String calculateTime(long elapsedTimeMillis){
        
        StringBuffer timeString = new StringBuffer();
        int minutes, seconds;
        
        minutes = (int) (elapsedTimeMillis / 60000);
        elapsedTimeMillis -= minutes * 60000;
        seconds = (int) (elapsedTimeMillis / 1000);
        elapsedTimeMillis -= seconds * 1000;
        
        timeString.append(minutes + "' " + seconds + "\" " + elapsedTimeMillis   
                + " ms."); 
        
        return timeString.toString();
    }    
    
    /**
     * Places the nodes in a sensible way instead of putting them all in the same point 
     * @param learnedNet
     */
    private void placeNodesInLearnedNet (ProbNet learnedNet)
    {
        double top =  0.0;
        double bottom =  600.0;
        double left =  100.0;
        double right =  800.0;
        
        Graph graph = learnedNet.getGraph ().copy ();
        List<List<Node>> nodesInLevels = new ArrayList<List<Node>>(); 
        while (!graph.getNodes ().isEmpty ())
        {
            // Look for the leaves
            List<Node> leaves = new ArrayList<> ();
            for(Node node : graph.getNodes ())
            {
                if(node.getChildren ().isEmpty ())
                {
                    leaves.add (node);
                }
            }
            for(Node leave : leaves)
            {
                graph.removeNode (leave);
            }            
            nodesInLevels.add (leaves);
        }
        
        double verticalStep  = (bottom - top) / nodesInLevels.size ();
        double currentY = bottom;
        for(List<Node> nodes : nodesInLevels)
        {
            double currentX = left;
            double horizontalStep = (right - left) / nodes.size ();
            for(Node node : nodes)
            {
                Node realNode = null;
                try
                {
                    realNode = learnedNet.getProbNode (((ProbNode)node.getObject ()).getName ()).getNode ();
                    realNode.setCoordinateX (currentX);
                    realNode.setCoordinateY (currentY);
                }
                catch (ProbNodeNotFoundException e)
                {
                }
                currentX += horizontalStep;
            }
            currentY -= verticalStep;
        }
        
    }        
    
    private void placeNodesInCircle (ProbNet probNet)
    {
        List<ProbNode> nodes = probNet.getProbNodes ();
        double radius = 250 + nodes.size () * 2;
        double margin = 100;
        Point2D center = new Point2D.Double(radius + margin, radius + margin); 
        for(int i=0; i < nodes.size (); ++i)
        {
            double rad = 2 * Math.PI * i/(double)nodes.size ();
            
            nodes.get (i).getNode ().setCoordinateX (center.getX ()+ Math.sin (rad) * radius);
            nodes.get (i).getNode ().setCoordinateY (center.getY ()- Math.cos (rad) * radius);
        }
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel AlgorithmLabel;
    private javax.swing.JRadioButton InteractiveLearningRadioButton;
    private javax.swing.ButtonGroup LearningTypeButtonGroup;
    private static javax.swing.JCheckBox addLinkModelNet;
    private javax.swing.JComboBox<String> algorithmComboBox;
    private static javax.swing.JRadioButton allVariablesRadioButton;
    private javax.swing.JRadioButton automaticLearningRadioButton;
    private static javax.swing.JButton cancelButton;
    private static javax.swing.JFileChooser databaseFileChooser;
    private static javax.swing.JTextPane caseFileTextPane;
    private static javax.swing.JCheckBox deleteLinksModelNet;
    private static javax.swing.JComboBox<String> discretizeComboBox;
    private static javax.swing.JPanel discretizePanel;
    private static javax.swing.JRadioButton fromFileRadioButton;
    private static javax.swing.JRadioButton fromOpenMarkovRadioButton;
    private javax.swing.JPanel generalPanel;
    private javax.swing.JTabbedPane generalTabbedPane;
    private static javax.swing.JCheckBox invertLinksModelNet;
    private javax.swing.JLabel jLabel1;
    private static javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JCheckBox useNodePositionsCheckBox;
    private static javax.swing.JButton learnButton;
    private javax.swing.JButton loadCaseFileButton;
    private static javax.swing.JButton loadModelNetButton;
    private static javax.swing.JComboBox<String> missingValuesComboBox;
    private static javax.swing.JPanel missingValuesPanel;
    private javax.swing.ButtonGroup modelNetButtonGroup;
    private static NetworkFileChooser modelNetFileChooser;
    private javax.swing.JPanel modelNetPanel;
    private static javax.swing.JTextPane modelNetTextPane;
    private static javax.swing.JRadioButton modelNetVariablesRadioButton;
    private static javax.swing.JRadioButton noModelNetRadioButton;
    private static javax.swing.JCheckBox numIntervalsCheckBox;
    private static javax.swing.JPanel numIntervalsPanel;
    private static javax.swing.JSpinner numIntervalsSpinner;
    private javax.swing.JButton optionsButton;
    private javax.swing.JTextArea optionsTextArea;
    private static javax.swing.JButton resetButton;
    private static javax.swing.JButton savePreprocessButton;
    private static javax.swing.JFileChooser savePreprocessFileChooser;
    private static javax.swing.JCheckBox selectDeselectCheckBox;
    private static javax.swing.JRadioButton selectedVariablesRadioButton;
    private javax.swing.JPanel showVariablesPanel;
    private javax.swing.JCheckBox startFromModelNetCheckBox;
    private static javax.swing.JPanel varSelectionPanel;
    private javax.swing.ButtonGroup variablesButtonGroup;
    private javax.swing.JPanel variablesPanel;
    // End of variables declaration//GEN-END:variables
    
}

