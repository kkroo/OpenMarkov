/*
 * Copyright 2011 CISIAD, UNED, Spain
 *
 * Licensed under the European Union Public Licence, version 1.1 (EUPL)
 *
 * Unless required by applicable law, this code is distributed
 * on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
 */

package org.openmarkov.core.gui.menutoolbar.menu;

import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.Set;

import javax.help.CSH;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

import org.openmarkov.core.gui.component.LastRecentFilesMenuItem;
import org.openmarkov.core.gui.configuration.LastOpenFiles;
import org.openmarkov.core.gui.dialog.HelpViewer;
import org.openmarkov.core.gui.loader.element.IconLoader;
import org.openmarkov.core.gui.localize.LocalizedCheckBoxMenuItem;
import org.openmarkov.core.gui.localize.LocalizedMenuItem;
import org.openmarkov.core.gui.localize.MenuLocalizer;
import org.openmarkov.core.gui.menutoolbar.common.ActionCommands;
import org.openmarkov.core.gui.menutoolbar.common.MenuItemNames;
import org.openmarkov.core.gui.menutoolbar.common.MenuToolBarBasic;
import org.openmarkov.core.gui.menutoolbar.common.MenuToolBarBasicImpl;
import org.openmarkov.core.gui.menutoolbar.common.ZoomMenuToolBar;
import org.openmarkov.core.gui.menutoolbar.plugin.ToolbarManager;
import org.openmarkov.core.gui.plugin.ToolPluginManager;
import org.openmarkov.core.gui.window.MainPanel;

/**
 * Class that manages the main menubar. It configures the default main menubar
 * and chages it according to the state of the application.
 * 
 * @author jmendoza
 * @version 1.0 jmendoza
 * @version 1.1 jlgozalo Add Help menus, HelpViewer, suppress from view the Menu
 *          Nodes View and set all the names for the different menus and menu
 *          items to ensure i18N Add getLastOpenFiles() method to the menu
 * @version 1.2 asaez Add Inference menus
 */
public class MainMenu extends JMenuBar implements MenuToolBarBasic, ZoomMenuToolBar {

    /**
     * Static field for serializable class.
     */
    private static final long   serialVersionUID                          = 8267763502728836096L;

    /**
     * Object that represents the menu 'File'.
     */
    private JMenu               fileMenu                                  = null;

    /**
     * Object that represents the item 'File - New'.
     */
    private JMenuItem           fileNewMenuItem                           = null;

    /**
     * Object that represents the item 'File - Open'.
     */
    private JMenuItem           fileOpenMenuItem                          = null;

    /**
     * Object that represents the item 'File - Save'.
     */
    private JMenuItem           fileSaveMenuItem                          = null;

    /**
     * Object that represents the item 'File - Save and Reopen'.
     */
    private JMenuItem           fileSaveOpenMenuItem                      = null;

    /**
     * Object that represents the item 'File - Save as'.
     */
    private JMenuItem           fileSaveAsMenuItem                        = null;

    /**
     * Object that represents the item 'File - Close'.
     */
    private JMenuItem           fileCloseMenuItem                         = null;

    /**
     * Object that represents the item 'File - Load Evidence'.
     */
    private JMenuItem           fileLoadEvidenceMenuItem                  = null;

    /**
     * Object that represents the item 'File - Save Evidence'.
     */
    private JMenuItem           fileSaveEvidenceMenuItem                  = null;

    /**
     * Object that represents the item 'File - Network additionalProperties'.
     */
    private JMenuItem           fileNetworkPropertiesMenuItem             = null;

    /**
     * Object that represents the item 'File - Exit'.
     */
    private JMenuItem           fileExitMenuItem                          = null;

    /**
     * Object that represents the menu 'Edit'.
     */
    private JMenu               editMenu                                  = null;

    /**
     * Object that represents the item 'Edit - Undo'.
     */
    private JMenuItem           editUndoMenuItem                          = null;

    /**
     * Object that represents the item 'Edit - Redo'.
     */
    private JMenuItem           editRedoMenuItem                          = null;

    /**
     * Object that represents the item 'Edit - Cut'.
     */
    private JMenuItem           editCutMenuItem                           = null;

    /**
     * Object that represents the item 'Edit - Copy'.
     */
    private JMenuItem           editCopyMenuItem                          = null;

    /**
     * Object that represents the item 'Edit - Paste'.
     */
    private JMenuItem           editPasteMenuItem                         = null;

    /**
     * Object that represents the item 'Edit - Remove'.
     */
    private JMenuItem           editRemoveMenuItem                        = null;

    /**
     * Object that represents the item 'Edit - Select all'.
     */
    private JMenuItem           editSelectAllMenuItem                     = null;

    /**
     * Object that represents the item 'Edit - Object selection'.
     */
    private JCheckBoxMenuItem   editObjectSelectionMenuItem               = null;

    /**
     * Object that represents the item 'Edit - Chance nodes creation'.
     */
    private JCheckBoxMenuItem   editChanceCreationMenuItem                = null;

    /**
     * Object that represents the item 'Edit - Decision nodes creation'.
     */
    private JCheckBoxMenuItem   editDecisionCreationMenuItem              = null;

    /**
     * Object that represents the item 'Edit - Utility nodes creation'.
     */
    private JCheckBoxMenuItem   editUtilityCreationMenuItem               = null;

    /**
     * Object that represents the item 'Edit - Links creation'.
     */
    private JCheckBoxMenuItem   editLinkCreationMenuItem                  = null;

    /**
     * Object that represents the item 'Edit - Instance creation'.
     */
    private JCheckBoxMenuItem   editInstanceCreationMenuItem              = null;

    /**
     * Object used to make autoexclusive the different select options.
     */
    private ButtonGroup         groupEditOptions                          = new ButtonGroup();

    /**
     * Object that represents the item 'Edit - Node additionalProperties'.
     */
    private JMenuItem           editNodePropertiesMenuItem                = null;

    /**
     * Object that represents the item 'Edit - Node Relation Table'.
     */
    private JMenuItem           editRelationMenuItem                      = null;

    /**
     * Object that represents the item 'Edit - Link additionalProperties'.
     */
    private JMenuItem           editLinkPropertiesMenuItem                = null;

    /**
     * Object that represents the item 'Edit - Switch to Inference mode'.
     */
    private JMenuItem           editSwitchToInferenceModeMenuItem         = null;

    /**
     * Object that represents the menu 'Inference'.
     */
    private JMenu               inferenceMenu                             = null;

    /**
     * Object that represents the item 'Inference - Switch to Edition mode'.
     */
    private JMenuItem           inferenceSwitchToEditionModeMenuItem      = null;

    /**
     * Object that represents the item 'Inference - Inference Options'.
     */
    private JMenuItem           inferenceOptionsMenuItem                  = null;

    /**
     * Object that represents the item 'Inference - Create New Evidence Case'.
     */
    private JMenuItem           inferenceCreateNewEvidenceCaseMenuItem    = null;

    /**
     * Object that represents the item 'Inference - Go To First Evidence Case'.
     */
    private JMenuItem           inferenceGoToFirstEvidenceCaseMenuItem    = null;

    /**
     * Object that represents the item 'Inference - Go To Previous Evidence
     * Case'.
     */
    private JMenuItem           inferenceGoToPreviousEvidenceCaseMenuItem = null;

    /**
     * Object that represents the item 'Inference - Go To Next Evidence Case'.
     */
    private JMenuItem           inferenceGoToNextEvidenceCaseMenuItem     = null;

    /**
     * Object that represents the item 'Inference - Go To Last Evidence Case'.
     */
    private JMenuItem           inferenceGoToLastEvidenceCaseMenuItem     = null;

    /**
     * Object that represents the item 'Inference - Clear Out All Evidence
     * Cases'.
     */
    private JMenuItem           inferenceClearEvidenceCasesMenuItem       = null;

    /**
     * Object that represents the item 'Inference - PropagateEvidence'.
     */
    private JMenuItem           inferencePropagateEvidenceMenuItem        = null;

    /**
     * Object that represents the item 'Inference - ExpandNode'.
     */
    private JMenuItem           inferenceExpandNodeMenuItem               = null;

    /**
     * Object that represents the item 'Inference - ContractNode'.
     */
    private JMenuItem           inferenceContractNodeMenuItem             = null;

    /**
     * Object that represents the item 'Inference - RemoveAllFindings'.
     */
    private JMenuItem           inferenceRemoveAllFindingsMenuItem        = null;

    /**
     * Object that represents the menu 'View'.
     */
    private JMenu               viewMenu                                  = null;

    /**
     * Object that represents the menu 'View - Nodes'.
     */
    private JMenu               viewNodesMenu                             = null;

    /**
     * Object that represents the item 'View - Nodes - ByName'.
     */
    private JCheckBoxMenuItem   viewNodesByNameMenuItem                   = null;

    /**
     * Object that represents the item 'View - Nodes - ByTitle'.
     */
    private JCheckBoxMenuItem   viewNodesByTitleMenuItem                  = null;

    /**
     * Object used to make autoexclusive the options 'ByName' and 'ByTitle'.
     */
    private ButtonGroup         groupByNameByTitle                        = new ButtonGroup();

    /**
     * Object that represents the menu 'View - Zoom'.
     */
    private JMenu               viewZoomMenu                              = null;

    /**
     * Object that represents the menu 'View - Toolbars'.
     */
    private JMenu               viewToolbarsMenu                          = null;

    /**
     * Object that represents the item 'View - Zoom - Zoom in'.
     */
    private JMenuItem           viewZoomInMenuItem                        = null;

    /**
     * Object that represents the item 'View - Zoom - Zoom out'.
     */
    private JMenuItem           viewZoomOutMenuItem                       = null;

    /**
     * Object that represents the item 'View - Zoom - 500%'.
     */
    private JCheckBoxMenuItem   viewZoom500MenuItem                       = null;

    /**
     * Object that represents the item 'View - Zoom - 200%'.
     */
    private JCheckBoxMenuItem   viewZoom200MenuItem                       = null;

    /**
     * Object that represents the item 'View - Zoom - 150%'.
     */
    private JCheckBoxMenuItem   viewZoom150MenuItem                       = null;

    /**
     * Object that represents the item 'View - Zoom - 100%'.
     */
    private JCheckBoxMenuItem   viewZoom100MenuItem                       = null;

    /**
     * Object that represents the item 'View - Zoom - 75%'.
     */
    private JCheckBoxMenuItem   viewZoom75MenuItem                        = null;

    /**
     * Object that represents the item 'View - Zoom - 50%'.
     */
    private JCheckBoxMenuItem   viewZoom50MenuItem                        = null;

    /**
     * Object that represents the item 'View - Zoom - 25%'.
     */
    private JCheckBoxMenuItem   viewZoom25MenuItem                        = null;

    /**
     * Object that represents the item 'View - Zoom - 10%'.
     */
    private JCheckBoxMenuItem   viewZoom10MenuItem                        = null;

    /**
     * Object that represents the item 'View - Zoom - Other'.
     */
    private JCheckBoxMenuItem   viewZoomOtherMenuItem                     = null;

    /**
     * Object used to make autoexclusive the zoom values.
     */
    private ButtonGroup         groupZoom                                 = new ButtonGroup();

    /**
     * Object that represents the item 'View - Message window'.
     */
    private JMenuItem           viewMessageWindowMenuItem                 = null;

    /**
     * Object that represents the menu 'Tools'.
     */
    private JMenu               toolsMenu                                 = null;

    /**
     * Object that represents the item 'Tools - Configuration'.
     */
    private JMenuItem           toolsConfigurationMenuItem                = null;

    /**
     * Object that represents the item 'Tools - CostEffectiveness analysis'.
     */
    private JMenuItem           toolsDeterministicCEMenuItem              = null;

    /**
     * Object that represents the item 'Tools - CostEffectiveness analysis'.
     */
    private JMenuItem           toolsProbabilisticCEMenuItem              = null;

    /**
     * Object that represents the menu 'Options'.
     */
    // private JMenu optionsMenu = null; //FOR FUTURE USE

    /**
     * Object that represents the menu 'Help'.
     */
    private JMenu               helpMenu                                  = null;

    /**
     * Object that represents the item 'Help - Help'.
     */
    private JMenuItem           helpOpenHelpMenuItem                      = null;

    /**
     * Object that represents the item 'Help - ChangeLanguage'.
     */
    private JMenuItem           helpOpenChangeLanguageMenuItem            = null;

    /**
     * Object that represents the item 'Help - About'.
     */
    private JMenuItem           helpOpenAboutMenuItem                     = null;

    /**
     * Object that is filled the MDI class.
     */
    private JMenu               menuMDI                                   = null;

    /**
     * Set of menu items and their default texts.
     */
    HashMap<JComponent, String> defaultText                               = new HashMap<JComponent, String>();

    /**
     * Object that listen to the user's actions.
     */
    private ActionListener      listener;

    /**
     * Menu option for testing
     */
    private JMenuItem           editTestMenuItem                          = null;

    private JMenu               toolsCostEffectivenessMenuItem;

    // private HashMap<JComponent, String> dynamicActions = new
    // HashMap<JComponent, String>();

    /**
     * last open file index
     */
    // private int lastOpenFileIndex = 0;

    /**
     * Creates a new instance.
     * 
     * @param newListener
     *            listener of the user's actions.
     */
    public MainMenu(ActionListener newListener) {

        listener = newListener;
        initialize();

    }

    /**
     * This method initializes the instance.
     */
    private void initialize() {

        add(getFileMenu());
        add(getEditMenu());
        add(getInferenceMenu());
        add(getViewMenu());
        add(getMenuMDI());
        add(getToolsMenu());
        // add(getOptionsMenu()); //FOR FUTURE USE
        add(getHelpingMenu());

    }

    /**
     * This method initializes fileMenu.
     * 
     * @return a new File menu.
     */
    private JMenu getFileMenu() {

        if (fileMenu == null) {
            fileMenu = new JMenu();
            fileMenu.setName(MenuItemNames.FILE_MENU);
            fileMenu.setText(MenuLocalizer.getLabel(MenuItemNames.FILE_MENU));
            fileMenu.setMnemonic(MenuLocalizer.getMnemonic(MenuItemNames.FILE_MENU).charAt(0));
            getBasicFileMenu();
            getLastOpenFiles();

        }

        return fileMenu;

    }

    /**
     * get all the menu items for the basic File Menu (without Last Open Files)
     */
    private void getBasicFileMenu() {
        fileMenu.add(getFileNewMenuItem());
        fileMenu.add(getFileOpenMenuItem());
        fileMenu.addSeparator();
        fileMenu.add(getFileSaveMenuItem());
        fileMenu.add(getFileSaveOpenMenuItem());
        fileMenu.add(getFileSaveAsMenuItem());
        fileMenu.addSeparator();
        fileMenu.add(getFileCloseMenuItem());
        fileMenu.addSeparator();
        fileMenu.add(getFileLoadEvidenceMenuItem());
        // fileMenu.add(getFileSaveEvidenceMenuItem ());
        fileMenu.addSeparator();
        fileMenu.add(getFileNetworkPropertiesMenuItem());
        fileMenu.addSeparator();
        fileMenu.add(getFileExitMenuItem());
    }

    /**
     * This method initializes fileNewMenuItem.
     * 
     * @return a new item 'File - New'.
     */
    private JMenuItem getFileNewMenuItem() {

        if (fileNewMenuItem == null) {
            fileNewMenuItem = new LocalizedMenuItem(MenuItemNames.FILE_NEW_MENUITEM,
                    ActionCommands.NEW_NETWORK,
                    IconLoader.ICON_NEW_ENABLED,
                    KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_DOWN_MASK));
            fileNewMenuItem.addActionListener(listener);
        }

        return fileNewMenuItem;

    }

    /**
     * This method initializes fileOpenMenuItem.
     * 
     * @return a new item 'File - Open'.
     */
    private JMenuItem getFileOpenMenuItem() {

        if (fileOpenMenuItem == null) {
            fileOpenMenuItem = new LocalizedMenuItem(MenuItemNames.FILE_OPEN_MENUITEM,
                    ActionCommands.OPEN_NETWORK,
                    IconLoader.ICON_OPEN_ENABLED,
                    KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_DOWN_MASK));
            fileOpenMenuItem.addActionListener(listener);
        }

        return fileOpenMenuItem;

    }

    /**
     * This method initializes fileSaveMenuItem.
     * 
     * @return a new item 'File - Save'.
     */
    private JMenuItem getFileSaveMenuItem() {

        if (fileSaveMenuItem == null) {
            fileSaveMenuItem = new LocalizedMenuItem(MenuItemNames.FILE_SAVE_MENUITEM,
                    ActionCommands.SAVE_NETWORK,
                    IconLoader.ICON_SAVE_ENABLED,
                    KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK));
            fileSaveMenuItem.addActionListener(listener);
        }

        return fileSaveMenuItem;

    }

    /**
     * This method initializes fileSaveMenuItem.
     * 
     * @return a new item 'File - Save'.
     */
    private JMenuItem getFileSaveOpenMenuItem() {

        if (fileSaveOpenMenuItem == null) {
            fileSaveOpenMenuItem = new LocalizedMenuItem(MenuItemNames.FILE_SAVE_OPEN_MENUITEM,
                    ActionCommands.SAVE_OPEN_NETWORK,
                    IconLoader.ICON_SAVE_ENABLED,
                    KeyStroke.getKeyStroke(KeyEvent.VK_W, InputEvent.CTRL_DOWN_MASK));
            fileSaveOpenMenuItem.addActionListener(listener);
        }

        return fileSaveOpenMenuItem;

    }

    /**
     * This method initializes fileSaveAsMenuItem.
     * 
     * @return a new item 'File - Save as'.
     */
    private JMenuItem getFileSaveAsMenuItem() {

        if (fileSaveAsMenuItem == null) {
            fileSaveAsMenuItem = new LocalizedMenuItem(MenuItemNames.FILE_SAVEAS_MENUITEM,
                    ActionCommands.SAVEAS_NETWORK);
            fileSaveAsMenuItem.addActionListener(listener);
        }

        return fileSaveAsMenuItem;

    }

    /**
     * This method initializes fileCloseMenuItem.
     * 
     * @return a new item 'File - Close'.
     */
    private JMenuItem getFileCloseMenuItem() {

        if (fileCloseMenuItem == null) {
            fileCloseMenuItem = new LocalizedMenuItem(MenuItemNames.FILE_CLOSE_MENUITEM,
                    ActionCommands.CLOSE_NETWORK,
                    IconLoader.ICON_CLOSE_ENABLED);
            fileCloseMenuItem.addActionListener(listener);
        }

        return fileCloseMenuItem;

    }

    /**
     * This method initializes fileLoadEvidenceMenuItem.
     * 
     * @return a new item 'File - Load Evidence'.
     */
    private JMenuItem getFileLoadEvidenceMenuItem() {

        if (fileLoadEvidenceMenuItem == null) {
            fileLoadEvidenceMenuItem = new LocalizedMenuItem(MenuItemNames.FILE_LOAD_EVIDENCE_MENUITEM,
                    ActionCommands.LOAD_EVIDENCE);
            fileLoadEvidenceMenuItem.addActionListener(listener);
        }

        return fileLoadEvidenceMenuItem;

    }

    /**
     * This method initializes fileSaveEvidenceMenuItem.
     * 
     * @return a new item 'File - Save Evidence'.
     */
    private JMenuItem getFileSaveEvidenceMenuItem() {

        if (fileSaveEvidenceMenuItem == null) {
            fileSaveEvidenceMenuItem = new LocalizedMenuItem(MenuItemNames.FILE_SAVE_EVIDENCE_MENUITEM,
                    ActionCommands.SAVE_EVIDENCE);
            fileSaveEvidenceMenuItem.addActionListener(listener);
        }

        return fileSaveEvidenceMenuItem;

    }

    /**
     * This method initializes fileNetworkPropertiesMenuItem.
     * 
     * @return a new item 'File - Network additionalProperties'.
     */
    private JMenuItem getFileNetworkPropertiesMenuItem() {

        if (fileNetworkPropertiesMenuItem == null) {
            fileNetworkPropertiesMenuItem = new LocalizedMenuItem(MenuItemNames.FILE_NETWORKPROPERTIES_MENUITEM,
                    ActionCommands.NETWORK_PROPERTIES);
            fileNetworkPropertiesMenuItem.addActionListener(listener);
        }

        return fileNetworkPropertiesMenuItem;

    }

    /**
     * This method initializes fileExitMenuItem.
     * 
     * @return a new item 'File - Exit'.
     */
    private JMenuItem getFileExitMenuItem() {

        if (fileExitMenuItem == null) {
            fileExitMenuItem = new LocalizedMenuItem(MenuItemNames.FILE_EXIT_MENUITEM,
                    ActionCommands.EXIT_APPLICATION);
            fileExitMenuItem.addActionListener(listener);
        }

        return fileExitMenuItem;

    }

    /**
     * This method retrieves the LastOpenFiles and show them in the File Menu
     * 
     * @return a new set of items
     */
    private void getLastOpenFiles() {

        int index, lastIndex;
        LastRecentFilesMenuItem item = null;
        LastOpenFiles lastOpenFiles = new LastOpenFiles();

        if (lastOpenFiles.existLastOpenFiles()) {

            fileMenu.addSeparator();
            lastIndex = lastOpenFiles.getOldestOpenFileIndex();
            // lastOpenFileIndex = lastIndex;
            for (index = 1; index <= lastIndex; index++) {
                item = new LastRecentFilesMenuItem();
                item.setName("lastRecentFilesMenuItem" + index);
                item.setText(index + " - " + lastOpenFiles.getFileNameAt(index));
                switch (index) {
                case 1:
                    item.setActionCommand(ActionCommands.OPEN_LAST_1_FILE);
                    break;
                case 2:
                    item.setActionCommand(ActionCommands.OPEN_LAST_2_FILE);
                    break;
                case 3:
                    item.setActionCommand(ActionCommands.OPEN_LAST_3_FILE);
                    break;
                case 4:
                    item.setActionCommand(ActionCommands.OPEN_LAST_4_FILE);
                    break;
                case 5:
                    item.setActionCommand(ActionCommands.OPEN_LAST_5_FILE);
                    break;
                default:

                }
                item.addActionListener(listener);
                fileMenu.add(item);
            }
        }

    }

    /**
     * This method reset the LastOpenFiles set of items in the File Menu
     * 
     * @return a new set of items
     */
    public void rechargeLastOpenFiles() {
        fileMenu.removeAll();
        getBasicFileMenu();
        getLastOpenFiles();
        fileMenu.repaint();
    }

    /**
     * This method initializes editMenu.
     * 
     * @return a new Edit menu.
     */
    private JMenu getEditMenu() {

        if (editMenu == null) {
            editMenu = new JMenu();
            editMenu.setName(MenuItemNames.EDIT_MENU);
            editMenu.setText(MenuLocalizer.getLabel(MenuItemNames.EDIT_MENU));
            editMenu.setMnemonic(MenuLocalizer.getMnemonic(MenuItemNames.EDIT_MENU).charAt(0));
            editMenu.add(getEditCutMenuItem());
            editMenu.add(getEditCopyMenuItem());
            editMenu.add(getEditPasteMenuItem());
            editMenu.add(getEditRemoveMenuItem());
            editMenu.addSeparator();
            editMenu.add(getEditUndoMenuItem());
            editMenu.add(getEditRedoMenuItem());
            editMenu.addSeparator();
            editMenu.add(getEditSelectAllMenuItem());
            editMenu.addSeparator();
            editMenu.add(getEditObjectSelectionMenuItem());
            editMenu.add(getEditChanceCreationMenuItem());
            editMenu.add(getEditDecisionCreationMenuItem());
            editMenu.add(getEditUtilityCreationMenuItem());
            editMenu.add(getEditLinkCreationMenuItem());
            editMenu.addSeparator();
            editMenu.add(getEditNodePropertiesMenuItem());
            editMenu.add(getEditRelationMenuItem());
            // Menu option for test
            editMenu.add(getTestMenuItem());
            editMenu.addSeparator();
            editMenu.add(getEditSwitchToInferenceModeMenuItem());

            /*
             * This item must be added to the menu when is active the
             * possibility of editing the additionalProperties of a link in
             * future versions.
             */
            // editMenu.add(getEditLinkPropertiesMenuItem());
            getEditLinkPropertiesMenuItem();
        }

        return editMenu;

    }

    /**
     * This method initializes editCutMenuItem.
     * 
     * @return a new item 'Edit - Cut'.
     */
    private JMenuItem getEditCutMenuItem() {

        if (editCutMenuItem == null) {
            editCutMenuItem = new LocalizedMenuItem(MenuItemNames.EDIT_CUT_MENUITEM,
                    ActionCommands.CLIPBOARD_CUT,
                    IconLoader.ICON_CUT_ENABLED,
                    KeyStroke.getKeyStroke(KeyEvent.VK_X, InputEvent.CTRL_DOWN_MASK));
            editCutMenuItem.addActionListener(listener);
        }

        return editCutMenuItem;

    }

    /**
     * This method initializes editCopyMenuItem.
     * 
     * @return a new item 'Edit - Copy'.
     */
    private JMenuItem getEditCopyMenuItem() {

        if (editCopyMenuItem == null) {
            editCopyMenuItem = new LocalizedMenuItem(MenuItemNames.EDIT_COPY_MENUITEM,
                    ActionCommands.CLIPBOARD_COPY,
                    IconLoader.ICON_COPY_ENABLED,
                    KeyStroke.getKeyStroke(KeyEvent.VK_C, InputEvent.CTRL_DOWN_MASK));
            editCopyMenuItem.addActionListener(listener);
        }

        return editCopyMenuItem;

    }

    /**
     * This method initializes editPasteMenuItem.
     * 
     * @return a new item 'Edit - Paste'.
     */
    private JMenuItem getEditPasteMenuItem() {

        if (editPasteMenuItem == null) {
            editPasteMenuItem = new LocalizedMenuItem(MenuItemNames.EDIT_PASTE_MENUITEM,
                    ActionCommands.CLIPBOARD_PASTE,
                    IconLoader.ICON_PASTE_ENABLED,
                    KeyStroke.getKeyStroke(KeyEvent.VK_V, InputEvent.CTRL_DOWN_MASK));
            editPasteMenuItem.addActionListener(listener);
        }

        return editPasteMenuItem;

    }

    /**
     * This method initializes editRemoveMenuItem.
     * 
     * @return a new item 'Edit - Remove'.
     */
    private JMenuItem getEditRemoveMenuItem() {

        if (editRemoveMenuItem == null) {
            editRemoveMenuItem = new LocalizedMenuItem(MenuItemNames.EDIT_REMOVE_MENUITEM,
                    ActionCommands.OBJECT_REMOVAL,
                    IconLoader.ICON_REMOVE_ENABLED,
                    KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0));
            editRemoveMenuItem.addActionListener(listener);
        }

        return editRemoveMenuItem;

    }

    /**
     * This method initializes editUndoMenuItem.
     * 
     * @return a new item 'Edit - Undo'.
     */
    private JMenuItem getEditUndoMenuItem() {

        if (editUndoMenuItem == null) {
            editUndoMenuItem = new LocalizedMenuItem(MenuItemNames.EDIT_UNDO_MENUITEM,
                    ActionCommands.UNDO,
                    IconLoader.ICON_UNDO_ENABLED,
                    KeyStroke.getKeyStroke(KeyEvent.VK_Z, InputEvent.CTRL_DOWN_MASK));
            editUndoMenuItem.addActionListener(listener);
        }

        return editUndoMenuItem;

    }

    /**
     * This method initializes editRedoMenuItem.
     * 
     * @return a new item 'Edit - Redo'.
     */
    private JMenuItem getEditRedoMenuItem() {

        if (editRedoMenuItem == null) {
            editRedoMenuItem = new LocalizedMenuItem(MenuItemNames.EDIT_REDO_MENUITEM,
                    ActionCommands.REDO,
                    IconLoader.ICON_REDO_ENABLED,
                    KeyStroke.getKeyStroke(KeyEvent.VK_Y, InputEvent.CTRL_DOWN_MASK));
            editRedoMenuItem.addActionListener(listener);
        }

        return editRedoMenuItem;

    }

    /**
     * This method initializes editSelectAllMenuItem.
     * 
     * @return a new item 'Edit - Select all'.
     */
    private JMenuItem getEditSelectAllMenuItem() {

        if (editSelectAllMenuItem == null) {
            editSelectAllMenuItem = new LocalizedMenuItem(MenuItemNames.EDIT_SELECTALL_MENUITEM,
                    ActionCommands.SELECT_ALL);
            editSelectAllMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_E,
                    InputEvent.CTRL_DOWN_MASK));
            editSelectAllMenuItem.addActionListener(listener);
        }

        return editSelectAllMenuItem;

    }

    /**
     * This method initializes editObjectSelectionMenuItem.
     * 
     * @return a new item 'Edit - Object selection'.
     */
    private JCheckBoxMenuItem getEditObjectSelectionMenuItem() {

        if (editObjectSelectionMenuItem == null) {
            editObjectSelectionMenuItem = new LocalizedCheckBoxMenuItem(MenuItemNames.EDIT_MODE_SELECTION_MENUITEM,
                    ActionCommands.OBJECT_SELECTION,
                    IconLoader.ICON_SELECTION_ENABLED);
            editObjectSelectionMenuItem.addActionListener(listener);
            groupEditOptions.add(editObjectSelectionMenuItem);
        }

        return editObjectSelectionMenuItem;

    }

    /**
     * This method initializes editChanceCreationMenuItem.
     * 
     * @return a new item 'Edit - Chance nodes creation'.
     */
    private JCheckBoxMenuItem getEditChanceCreationMenuItem() {

        if (editChanceCreationMenuItem == null) {
            editChanceCreationMenuItem = new LocalizedCheckBoxMenuItem(MenuItemNames.EDIT_MODE_CHANCE_MENUITEM,
                    ActionCommands.CHANCE_CREATION,
                    IconLoader.ICON_CHANCE_ENABLED);
            editChanceCreationMenuItem.addActionListener(listener);
            groupEditOptions.add(editChanceCreationMenuItem);
        }

        return editChanceCreationMenuItem;

    }

    /**
     * This method initializes editDecisionCreationMenuItem.
     * 
     * @return a new item 'Edit - Decision nodes creation'.
     */
    private JCheckBoxMenuItem getEditDecisionCreationMenuItem() {

        if (editDecisionCreationMenuItem == null) {
            editDecisionCreationMenuItem = new LocalizedCheckBoxMenuItem(MenuItemNames.EDIT_MODE_DECISION_MENUITEM,
                    ActionCommands.DECISION_CREATION,
                    IconLoader.ICON_DECISION_ENABLED);
            editDecisionCreationMenuItem.addActionListener(listener);
            groupEditOptions.add(editDecisionCreationMenuItem);
        }

        return editDecisionCreationMenuItem;

    }

    /**
     * This method initializes editUtilityCreationMenuItem.
     * 
     * @return a new item 'Edit - Utility nodes creation'.
     */
    private JCheckBoxMenuItem getEditUtilityCreationMenuItem() {

        if (editUtilityCreationMenuItem == null) {
            editUtilityCreationMenuItem = new LocalizedCheckBoxMenuItem(MenuItemNames.EDIT_MODE_UTILITY_MENUITEM,
                    ActionCommands.UTILITY_CREATION,
                    IconLoader.ICON_UTILITY_ENABLED);
            editUtilityCreationMenuItem.addActionListener(listener);
            groupEditOptions.add(editUtilityCreationMenuItem);
        }

        return editUtilityCreationMenuItem;

    }

    /**
     * This method initializes editLinkCreationMenuItem.
     * 
     * @return a new item 'Edit - Links creation'.
     */
    private JCheckBoxMenuItem getEditLinkCreationMenuItem() {

        if (editLinkCreationMenuItem == null) {
            editLinkCreationMenuItem = new LocalizedCheckBoxMenuItem(MenuItemNames.EDIT_MODE_LINK_MENUITEM,
                    ActionCommands.LINK_CREATION,
                    IconLoader.ICON_LINK_ENABLED);
            editLinkCreationMenuItem.addActionListener(listener);
            groupEditOptions.add(editLinkCreationMenuItem);
        }

        return editLinkCreationMenuItem;

    }

    /**
     * This method initializes editNodePropertiesMenuItem.
     * 
     * @return a new item 'Edit - Node additionalProperties'.
     */
    private JMenuItem getEditNodePropertiesMenuItem() {

        if (editNodePropertiesMenuItem == null) {
            editNodePropertiesMenuItem = new LocalizedMenuItem(MenuItemNames.EDIT_NODEPROPERTIES_MENUITEM,
                    ActionCommands.NODE_PROPERTIES);
            editNodePropertiesMenuItem.addActionListener(listener);
        }

        return editNodePropertiesMenuItem;

    }

    /**
     * This method initializes editNodeRelationMenuItem.
     * 
     * @return a new item 'Edit - Node Relation Table'.
     */
    private JMenuItem getEditRelationMenuItem() {

        if (editRelationMenuItem == null) {
            editRelationMenuItem = new LocalizedMenuItem(MenuItemNames.EDIT_NODERELATION_MENUITEM,
                    ActionCommands.EDIT_POTENTIAL);
            editRelationMenuItem.addActionListener(listener);
        }

        return editRelationMenuItem;

    }

    /**
     * This method initializes editTestMenuItem.
     * 
     * @return a new item 'Edit - Node Relation Table'.
     */
    private JMenuItem getTestMenuItem() {

        if (editTestMenuItem == null) {
            editTestMenuItem = new LocalizedMenuItem(MenuItemNames.EDIT_NODETEST_MENUITEM,
                    ActionCommands.TEST);
            editTestMenuItem.addActionListener(listener);
        }

        return editTestMenuItem;

    }

    /**
     * This method initializes editLinkPropertiesMenuItem.
     * 
     * @return a new item 'Edit - Link additionalProperties'.
     */
    private JMenuItem getEditLinkPropertiesMenuItem() {

        if (editLinkPropertiesMenuItem == null) {
            editLinkPropertiesMenuItem = new LocalizedMenuItem(MenuItemNames.EDIT_LINKPROPERTIES_MENUITEM,
                    ActionCommands.LINK_PROPERTIES);
            editLinkPropertiesMenuItem.addActionListener(listener);
        }

        return editLinkPropertiesMenuItem;

    }

    /**
     * This method initializes editSwitchToInferenceModeMenuItem.
     * 
     * @return a new item 'Edit - Switch to Inference mode'.
     */
    private JMenuItem getEditSwitchToInferenceModeMenuItem() {
        if (editSwitchToInferenceModeMenuItem == null) {
            editSwitchToInferenceModeMenuItem = new LocalizedMenuItem(MenuItemNames.EDIT_SWITCH_TO_INFERENCE_MODE_MENUITEM,
                    ActionCommands.CHANGE_TO_INFERENCE_MODE,
                    IconLoader.ICON_INFERENCE_MODE_ENABLED,
                    KeyStroke.getKeyStroke(KeyEvent.VK_I, InputEvent.CTRL_DOWN_MASK));
            editSwitchToInferenceModeMenuItem.addActionListener(listener);
        }
        return editSwitchToInferenceModeMenuItem;
    }

    /**
     * This method initializes inferenceMenu.
     * 
     * @return a new Inference menu.
     */
    private JMenu getInferenceMenu() {
        if (inferenceMenu == null) {
            inferenceMenu = new JMenu();
            inferenceMenu.setName(MenuItemNames.INFERENCE_MENU);
            inferenceMenu.setText(MenuLocalizer.getLabel(MenuItemNames.INFERENCE_MENU));
            inferenceMenu.setMnemonic(MenuLocalizer.getMnemonic(MenuItemNames.INFERENCE_MENU).charAt(0));
            inferenceMenu.add(getInferenceSwitchToEditionModeMenuItem());
            inferenceMenu.addSeparator();
            inferenceMenu.add(getInferenceOptionsMenuItem());
            inferenceMenu.addSeparator();
            inferenceMenu.add(getInferenceCreateNewEvidenceCaseMenuItem());
            inferenceMenu.add(getInferenceClearOutAllEvidenceCasesMenuItem());
            inferenceMenu.addSeparator();
            inferenceMenu.add(getInferenceGoToFirstEvidenceCaseMenuItem());
            inferenceMenu.add(getInferenceGoToPreviousEvidenceCaseMenuItem());
            inferenceMenu.add(getInferenceGoToNextEvidenceCaseMenuItem());
            inferenceMenu.add(getInferenceGoToLastEvidenceCaseMenuItem());
            inferenceMenu.addSeparator();
            inferenceMenu.add(getInferenceExpandNodeMenuItem());
            inferenceMenu.add(getInferenceContractNodeMenuItem());
            inferenceMenu.addSeparator();
            inferenceMenu.add(getInferenceRemoveAllFindingsMenuItem());
        }
        return inferenceMenu;
    }

    /**
     * This method adds the item 'Propagate Now' on the inference menu.
     */
    public void addPropagateNowItem() {
        if (inferenceMenu != null) {
            inferenceMenu.removeAll();
            inferenceMenu.add(getInferenceSwitchToEditionModeMenuItem());
            inferenceMenu.addSeparator();
            inferenceMenu.add(getInferenceOptionsMenuItem());
            inferenceMenu.addSeparator();
            inferenceMenu.add(getInferenceCreateNewEvidenceCaseMenuItem());
            inferenceMenu.add(getInferenceClearOutAllEvidenceCasesMenuItem());
            inferenceMenu.addSeparator();
            inferenceMenu.add(getInferenceGoToFirstEvidenceCaseMenuItem());
            inferenceMenu.add(getInferenceGoToPreviousEvidenceCaseMenuItem());
            inferenceMenu.add(getInferenceGoToNextEvidenceCaseMenuItem());
            inferenceMenu.add(getInferenceGoToLastEvidenceCaseMenuItem());
            inferenceMenu.addSeparator();
            inferenceMenu.add(getInferencePropagateEvidenceMenuItem());
            inferenceMenu.addSeparator();
            inferenceMenu.add(getInferenceExpandNodeMenuItem());
            inferenceMenu.add(getInferenceContractNodeMenuItem());
            inferenceMenu.addSeparator();
            inferenceMenu.add(getInferenceRemoveAllFindingsMenuItem());
        }
    }

    /**
     * This method removes the item 'Propagate Now' from the inference menu.
     */
    public void removePropagateNowItem() {
        if (inferenceMenu != null) {
            inferenceMenu.removeAll();
            inferenceMenu.add(getInferenceSwitchToEditionModeMenuItem());
            inferenceMenu.addSeparator();
            inferenceMenu.add(getInferenceOptionsMenuItem());
            inferenceMenu.addSeparator();
            inferenceMenu.add(getInferenceCreateNewEvidenceCaseMenuItem());
            inferenceMenu.add(getInferenceClearOutAllEvidenceCasesMenuItem());
            inferenceMenu.addSeparator();
            inferenceMenu.add(getInferenceGoToFirstEvidenceCaseMenuItem());
            inferenceMenu.add(getInferenceGoToPreviousEvidenceCaseMenuItem());
            inferenceMenu.add(getInferenceGoToNextEvidenceCaseMenuItem());
            inferenceMenu.add(getInferenceGoToLastEvidenceCaseMenuItem());
            inferenceMenu.addSeparator();
            inferenceMenu.add(getInferenceExpandNodeMenuItem());
            inferenceMenu.add(getInferenceContractNodeMenuItem());
            inferenceMenu.addSeparator();
            inferenceMenu.add(getInferenceRemoveAllFindingsMenuItem());
        }
    }

    /**
     * This method initializes inferenceSwitchToEditionModeMenuItem.
     * 
     * @return a new item 'Inference - Switch to Edition mode'.
     */
    private JMenuItem getInferenceSwitchToEditionModeMenuItem() {
        if (inferenceSwitchToEditionModeMenuItem == null) {
            inferenceSwitchToEditionModeMenuItem = new LocalizedMenuItem(MenuItemNames.INFERENCE_SWITCH_TO_EDITION_MODE_MENUITEM,
                    ActionCommands.CHANGE_TO_EDITION_MODE,
                    IconLoader.ICON_EDITION_MODE_ENABLED,
                    KeyStroke.getKeyStroke(KeyEvent.VK_I, InputEvent.CTRL_DOWN_MASK));
            inferenceSwitchToEditionModeMenuItem.addActionListener(listener);
        }
        return inferenceSwitchToEditionModeMenuItem;
    }

    /**
     * This method initializes inferenceOptionsMenuItem.
     * 
     * @return a new item 'Inference - Inference Options'.
     */
    private JMenuItem getInferenceOptionsMenuItem() {
        if (inferenceOptionsMenuItem == null) {
            inferenceOptionsMenuItem = new LocalizedMenuItem(MenuItemNames.INFERENCE_OPTIONS_MENUITEM,
                    ActionCommands.INFERENCE_OPTIONS);
            inferenceOptionsMenuItem.addActionListener(listener);
        }
        return inferenceOptionsMenuItem;
    }

    /**
     * This method initializes inferenceCreateNewEvidenceCaseMenuItem.
     * 
     * @return a new item 'Inference - Create New Evidence Case'.
     */
    private JMenuItem getInferenceCreateNewEvidenceCaseMenuItem() {
        if (inferenceCreateNewEvidenceCaseMenuItem == null) {
            inferenceCreateNewEvidenceCaseMenuItem = new LocalizedMenuItem(MenuItemNames.INFERENCE_CREATE_NEW_EVIDENCE_CASE_MENUITEM,
                    ActionCommands.CREATE_NEW_EVIDENCE_CASE,
                    IconLoader.ICON_CREATE_NEW_EVIDENCE_CASE_ENABLED);
            inferenceCreateNewEvidenceCaseMenuItem.addActionListener(listener);
        }
        return inferenceCreateNewEvidenceCaseMenuItem;
    }

    /**
     * This method initializes inferenceGoToFirstEvidenceCaseMenuItem.
     * 
     * @return a new item 'Inference - Go To First Evidence Case'.
     */
    private JMenuItem getInferenceGoToFirstEvidenceCaseMenuItem() {
        if (inferenceGoToFirstEvidenceCaseMenuItem == null) {
            inferenceGoToFirstEvidenceCaseMenuItem = new LocalizedMenuItem(MenuItemNames.INFERENCE_GO_TO_FIRST_EVIDENCE_CASE_MENUITEM,
                    ActionCommands.GO_TO_FIRST_EVIDENCE_CASE,
                    IconLoader.ICON_GO_TO_FIRST_EVIDENCE_CASE_ENABLED);
            inferenceGoToFirstEvidenceCaseMenuItem.addActionListener(listener);
        }
        return inferenceGoToFirstEvidenceCaseMenuItem;
    }

    /**
     * This method initializes inferenceGoToPreviousEvidenceCaseMenuItem.
     * 
     * @return a new item 'Inference - Go To Previous Evidence Case'.
     */
    private JMenuItem getInferenceGoToPreviousEvidenceCaseMenuItem() {
        if (inferenceGoToPreviousEvidenceCaseMenuItem == null) {
            inferenceGoToPreviousEvidenceCaseMenuItem = new LocalizedMenuItem(MenuItemNames.INFERENCE_GO_TO_PREVIOUS_EVIDENCE_CASE_MENUITEM,
                    ActionCommands.GO_TO_PREVIOUS_EVIDENCE_CASE,
                    IconLoader.ICON_GO_TO_PREVIOUS_EVIDENCE_CASE_ENABLED);
            inferenceGoToPreviousEvidenceCaseMenuItem.addActionListener(listener);
        }
        return inferenceGoToPreviousEvidenceCaseMenuItem;
    }

    /**
     * This method initializes inferenceGoToNextEvidenceCaseMenuItem.
     * 
     * @return a new item 'Inference - Go To Next Evidence Case'.
     */
    private JMenuItem getInferenceGoToNextEvidenceCaseMenuItem() {
        if (inferenceGoToNextEvidenceCaseMenuItem == null) {
            inferenceGoToNextEvidenceCaseMenuItem = new LocalizedMenuItem(MenuItemNames.INFERENCE_GO_TO_NEXT_EVIDENCE_CASE_MENUITEM,
                    ActionCommands.GO_TO_NEXT_EVIDENCE_CASE,
                    IconLoader.ICON_GO_TO_NEXT_EVIDENCE_CASE_ENABLED);
            inferenceGoToNextEvidenceCaseMenuItem.addActionListener(listener);
        }
        return inferenceGoToNextEvidenceCaseMenuItem;
    }

    /**
     * This method initializes inferenceGoToLastEvidenceCaseMenuItem.
     * 
     * @return a new item 'Inference - Go To Last Evidence Case'.
     */
    private JMenuItem getInferenceGoToLastEvidenceCaseMenuItem() {
        if (inferenceGoToLastEvidenceCaseMenuItem == null) {
            inferenceGoToLastEvidenceCaseMenuItem = new LocalizedMenuItem(MenuItemNames.INFERENCE_GO_TO_LAST_EVIDENCE_CASE_MENUITEM,
                    ActionCommands.GO_TO_LAST_EVIDENCE_CASE,
                    IconLoader.ICON_GO_TO_LAST_EVIDENCE_CASE_ENABLED);
            inferenceGoToLastEvidenceCaseMenuItem.addActionListener(listener);
        }
        return inferenceGoToLastEvidenceCaseMenuItem;
    }

    /**
     * This method initializes inferenceClearOutAllEvidenceCasesMenuItem.
     * 
     * @return a new item 'Inference - Clear Out All Evidence Cases'.
     */
    private JMenuItem getInferenceClearOutAllEvidenceCasesMenuItem() {
        if (inferenceClearEvidenceCasesMenuItem == null) {
            inferenceClearEvidenceCasesMenuItem = new LocalizedMenuItem(MenuItemNames.INFERENCE_CLEAR_OUT_ALL_EVIDENCE_CASES_MENUITEM,
                    ActionCommands.CLEAR_OUT_ALL_EVIDENCE_CASES,
                    IconLoader.ICON_CLEAR_OUT_ALL_EVIDENCE_CASES_ENABLED);
            inferenceClearEvidenceCasesMenuItem.addActionListener(listener);
        }
        return inferenceClearEvidenceCasesMenuItem;
    }

    /**
     * This method initializes inferencePropagateEvidenceMenuItem.
     * 
     * @return a new item 'Inference - Switch to Edition mode'.
     */
    private JMenuItem getInferencePropagateEvidenceMenuItem() {
        if (inferencePropagateEvidenceMenuItem == null) {
            inferencePropagateEvidenceMenuItem = new LocalizedMenuItem(MenuItemNames.INFERENCE_PROPAGATE_EVIDENCE_MENUITEM,
                    ActionCommands.PROPAGATE_EVIDENCE,
                    IconLoader.ICON_PROPAGATE_EVIDENCE_ENABLED,
                    KeyStroke.getKeyStroke(KeyEvent.VK_F, InputEvent.CTRL_DOWN_MASK));
            inferencePropagateEvidenceMenuItem.addActionListener(listener);
        }
        return inferencePropagateEvidenceMenuItem;
    }

    /**
     * This method initializes inferenceExpandNodeMenuItem.
     * 
     * @return a new item 'Inference - ExpandNode'.
     */
    private JMenuItem getInferenceExpandNodeMenuItem() {
        if (inferenceExpandNodeMenuItem == null) {
            inferenceExpandNodeMenuItem = new LocalizedMenuItem(MenuItemNames.INFERENCE_EXPAND_NODE_MENUITEM,
                    ActionCommands.NODE_EXPANSION);
            inferenceExpandNodeMenuItem.addActionListener(listener);
        }
        return inferenceExpandNodeMenuItem;
    }

    /**
     * This method initializes inferenceContractNodeMenuItem.
     * 
     * @return a new item 'Inference - ContractNode'.
     */
    private JMenuItem getInferenceContractNodeMenuItem() {
        if (inferenceContractNodeMenuItem == null) {
            inferenceContractNodeMenuItem = new LocalizedMenuItem(MenuItemNames.INFERENCE_CONTRACT_NODE_MENUITEM,
                    ActionCommands.NODE_CONTRACTION);
            inferenceContractNodeMenuItem.addActionListener(listener);
        }
        return inferenceContractNodeMenuItem;
    }

    /**
     * This method initializes inferenceRemoveAllFindingsMenuItem.
     * 
     * @return a new item 'Inference - RemoveAllFindings'.
     */
    private JMenuItem getInferenceRemoveAllFindingsMenuItem() {
        if (inferenceRemoveAllFindingsMenuItem == null) {
            inferenceRemoveAllFindingsMenuItem = new LocalizedMenuItem(MenuItemNames.INFERENCE_REMOVE_ALL_FINDINGS_MENUITEM,
                    ActionCommands.NODE_REMOVE_ALL_FINDINGS);
            inferenceRemoveAllFindingsMenuItem.addActionListener(listener);
        }
        return inferenceRemoveAllFindingsMenuItem;
    }

    /**
     * This method initializes viewMenu.
     * 
     * @return a new menu 'View'.
     */
    private JMenu getViewMenu() {

        if (viewMenu == null) {
            viewMenu = new JMenu();
            viewMenu.setName(MenuItemNames.VIEW_MENU);
            viewMenu.setText(MenuLocalizer.getLabel(MenuItemNames.VIEW_MENU));
            viewMenu.setMnemonic(MenuLocalizer.getMnemonic(MenuItemNames.VIEW_MENU).charAt(0));
            // viewMenu.add(getViewNodesMenu()); 29/03/2009 - jlgozalo- Not
            // required in OpenMarkov
            viewMenu.add(getViewToolbarsMenu());
            viewMenu.add(getViewZoomMenu());
            viewMenu.addSeparator();
            viewMenu.add(getViewMessageWindowMenuItem());
        }

        return viewMenu;

    }

    /**
     * This method initializes viewNodesMenu.
     * 
     * @return a new menu 'View - Nodes'.
     */
    private JMenu getViewNodesMenu() {

        if (viewNodesMenu == null) {
            viewNodesMenu = new JMenu();
            viewNodesMenu.setName(MenuItemNames.VIEW_NODES_MENU);
            viewNodesMenu.setText(MenuLocalizer.getLabel(MenuItemNames.VIEW_NODES_MENU));
            viewNodesMenu.setMnemonic(MenuLocalizer.getMnemonic(MenuItemNames.VIEW_NODES_MENU).charAt(0));
            viewNodesMenu.add(getViewNodesByNameMenuItem());
            viewNodesMenu.add(getViewNodesByTitleMenuItem());
        }

        return viewNodesMenu;

    }

    /**
     * This method initializes viewNodesByNameMenuItem.
     * 
     * @return a new item 'View - Nodes - ByName'.
     */
    private JCheckBoxMenuItem getViewNodesByNameMenuItem() {

        if (viewNodesByNameMenuItem == null) {
            viewNodesByNameMenuItem = new LocalizedCheckBoxMenuItem(MenuItemNames.VIEW_NODES_BYNAME_MENUITEM,
                    ActionCommands.BYNAME_NODES);
            viewNodesByNameMenuItem.addActionListener(listener);
            groupByNameByTitle.add(viewNodesByNameMenuItem);
        }

        return viewNodesByNameMenuItem;

    }

    /**
     * This method initializes viewNodesByTitleMenuItem.
     * 
     * @return a new item 'View - Nodes - ByTitle'.
     */
    private JCheckBoxMenuItem getViewNodesByTitleMenuItem() {

        if (viewNodesByTitleMenuItem == null) {
            viewNodesByTitleMenuItem = new LocalizedCheckBoxMenuItem(MenuItemNames.VIEW_NODES_BYTITLE_MENUITEM,
                    ActionCommands.BYTITLE_NODES);
            viewNodesByTitleMenuItem.addActionListener(listener);
            groupByNameByTitle.add(viewNodesByTitleMenuItem);
        }

        return viewNodesByTitleMenuItem;

    }

    /**
     * This method initializes viewZoomMenu.
     * 
     * @return a new menu 'View - Zoom'.
     */
    private JMenu getViewZoomMenu() {

        if (viewZoomMenu == null) {
            viewZoomMenu = new JMenu();
            viewZoomMenu.setName(MenuItemNames.VIEW_ZOOM_MENU);
            viewZoomMenu.setText(MenuLocalizer.getLabel(MenuItemNames.VIEW_ZOOM_MENU));
            viewZoomMenu.setMnemonic(MenuLocalizer.getMnemonic(MenuItemNames.VIEW_ZOOM_MENU).charAt(0));
            viewZoomMenu.add(getViewZoomInMenuItem());
            viewZoomMenu.add(getViewZoomOutMenuItem());
            viewZoomMenu.addSeparator();
            viewZoomMenu.add(getViewZoom500MenuItem());
            viewZoomMenu.add(getViewZoom200MenuItem());
            viewZoomMenu.add(getViewZoom150MenuItem());
            viewZoomMenu.add(getViewZoom100MenuItem());
            viewZoomMenu.add(getViewZoom75MenuItem());
            viewZoomMenu.add(getViewZoom50MenuItem());
            viewZoomMenu.add(getViewZoom25MenuItem());
            viewZoomMenu.add(getViewZoom10MenuItem());
            viewZoomMenu.addSeparator();
            viewZoomMenu.add(getViewZoomOtherMenuItem());
        }

        return viewZoomMenu;

    }

    /**
     * This method initializes viewToolbarsMenu.
     * 
     * @return a new menu 'View - Toolbars'.
     */
    private JMenu getViewToolbarsMenu() {

        if (viewToolbarsMenu == null) {
            viewToolbarsMenu = new JMenu();
            viewToolbarsMenu.setName(MenuItemNames.VIEW_TOOLBARS_MENU);
            viewToolbarsMenu.setText(MenuLocalizer.getLabel(MenuItemNames.VIEW_TOOLBARS_MENU));
            viewToolbarsMenu.setMnemonic(MenuLocalizer.getMnemonic(MenuItemNames.VIEW_TOOLBARS_MENU).charAt(0));
            ToolbarManager toolbarManager = MainPanel.getUniqueInstance().getToolbarManager();
            Set<String> toolbarNames = toolbarManager.getToolbarNames();
            if (!toolbarNames.isEmpty()) {
                for (String toolbarName : toolbarManager.getToolbarNames()) {
                    JMenuItem menuItem = new LocalizedMenuItem(MenuItemNames.VIEW_TOOLBARS_MENU
                            + "."
                            + toolbarName, ActionCommands.VIEW_TOOLBARS + "." + toolbarName);
                    menuItem.addActionListener(listener);
                    viewToolbarsMenu.add(menuItem);
                }
            } else {
                JMenuItem emptyMenuItem = new JMenuItem("(empty)");
                emptyMenuItem.setEnabled(false);
                viewToolbarsMenu.add(emptyMenuItem);
            }
        }

        return viewToolbarsMenu;

    }

    /**
     * This method initializes viewZoomInMenuItem.
     * 
     * @return a new item 'View - Zoom - Zoom in.
     */
    private JMenuItem getViewZoomInMenuItem() {

        if (viewZoomInMenuItem == null) {
            viewZoomInMenuItem = new LocalizedMenuItem(MenuItemNames.VIEW_ZOOM_IN_MENUITEM,
                    ActionCommands.ZOOM_IN,
                    IconLoader.ICON_ZOOM_IN_ENABLED);
            viewZoomInMenuItem.addActionListener(listener);
        }

        return viewZoomInMenuItem;

    }

    /**
     * This method initializes viewZoomOutMenuItem.
     * 
     * @return a new item 'View - Zoom - Zoom out.
     */
    private JMenuItem getViewZoomOutMenuItem() {

        if (viewZoomOutMenuItem == null) {
            viewZoomOutMenuItem = new LocalizedMenuItem(MenuItemNames.VIEW_ZOOM_OUT_MENUITEM,
                    ActionCommands.ZOOM_OUT,
                    IconLoader.ICON_ZOOM_OUT_ENABLED);
            viewZoomOutMenuItem.addActionListener(listener);
        }

        return viewZoomOutMenuItem;

    }

    /**
     * This method initializes viewZoom500MenuItem
     * 
     * @return a new item 'View - Zoom - 500%'.
     */
    private JCheckBoxMenuItem getViewZoom500MenuItem() {

        if (viewZoom500MenuItem == null) {
            viewZoom500MenuItem = new LocalizedCheckBoxMenuItem(MenuItemNames.VIEW_ZOOM_500_MENUITEM,
                    ActionCommands.getZoomActionCommandValue(5));
            viewZoom500MenuItem.addActionListener(listener);
            groupZoom.add(viewZoom500MenuItem);
        }

        return viewZoom500MenuItem;

    }

    /**
     * This method initializes viewZoom200MenuItem
     * 
     * @return a new item 'View - Zoom - 200%'.
     */
    private JCheckBoxMenuItem getViewZoom200MenuItem() {

        if (viewZoom200MenuItem == null) {
            viewZoom200MenuItem = new LocalizedCheckBoxMenuItem(MenuItemNames.VIEW_ZOOM_200_MENUITEM,
                    ActionCommands.getZoomActionCommandValue(2));
            viewZoom200MenuItem.addActionListener(listener);
            groupZoom.add(viewZoom200MenuItem);
        }

        return viewZoom200MenuItem;

    }

    /**
     * This method initializes viewZoom150MenuItem.
     * 
     * @return a new item 'View - Zoom - 150%'.
     */
    private JCheckBoxMenuItem getViewZoom150MenuItem() {

        if (viewZoom150MenuItem == null) {
            viewZoom150MenuItem = new LocalizedCheckBoxMenuItem(MenuItemNames.VIEW_ZOOM_150_MENUITEM,
                    ActionCommands.getZoomActionCommandValue(1.5));
            viewZoom150MenuItem.addActionListener(listener);
            groupZoom.add(viewZoom150MenuItem);
        }

        return viewZoom150MenuItem;

    }

    /**
     * This method initializes viewZoom100MenuItem.
     * 
     * @return a new item 'View - Zoom - 100%'.
     */
    private JCheckBoxMenuItem getViewZoom100MenuItem() {

        if (viewZoom100MenuItem == null) {
            viewZoom100MenuItem = new LocalizedCheckBoxMenuItem(MenuItemNames.VIEW_ZOOM_100_MENUITEM,
                    ActionCommands.getZoomActionCommandValue(1));
            viewZoom100MenuItem.addActionListener(listener);
            groupZoom.add(viewZoom100MenuItem);
            viewZoom100MenuItem.setSelected(true);
        }

        return viewZoom100MenuItem;

    }

    /**
     * This method initializes viewZoom75MenuItem
     * 
     * @return a new item 'View - Zoom - 75%'.
     */
    private JCheckBoxMenuItem getViewZoom75MenuItem() {

        if (viewZoom75MenuItem == null) {
            viewZoom75MenuItem = new LocalizedCheckBoxMenuItem(MenuItemNames.VIEW_ZOOM_75_MENUITEM,
                    ActionCommands.getZoomActionCommandValue(0.75));
            viewZoom75MenuItem.addActionListener(listener);
            groupZoom.add(viewZoom75MenuItem);
        }

        return viewZoom75MenuItem;

    }

    /**
     * This method initializes viewZoom50MenuItem
     * 
     * @return a new item 'View - Zoom - 50%'.
     */
    private JCheckBoxMenuItem getViewZoom50MenuItem() {

        if (viewZoom50MenuItem == null) {
            viewZoom50MenuItem = new LocalizedCheckBoxMenuItem(MenuItemNames.VIEW_ZOOM_50_MENUITEM,
                    ActionCommands.getZoomActionCommandValue(0.5));
            viewZoom50MenuItem.addActionListener(listener);
            groupZoom.add(viewZoom50MenuItem);
        }

        return viewZoom50MenuItem;

    }

    /**
     * This method initializes viewZoom25MenuItem.
     * 
     * @return a new item 'View - Zoom - 25%'.
     */
    private JCheckBoxMenuItem getViewZoom25MenuItem() {

        if (viewZoom25MenuItem == null) {
            viewZoom25MenuItem = new LocalizedCheckBoxMenuItem(MenuItemNames.VIEW_ZOOM_25_MENUITEM,
                    ActionCommands.getZoomActionCommandValue(0.25));
            viewZoom25MenuItem.addActionListener(listener);
            groupZoom.add(viewZoom25MenuItem);
        }

        return viewZoom25MenuItem;

    }

    /**
     * This method initializes viewZoom10MenuItem.
     * 
     * @return a new item 'View - Zoom - 10%'.
     */
    private JCheckBoxMenuItem getViewZoom10MenuItem() {

        if (viewZoom10MenuItem == null) {
            viewZoom10MenuItem = new LocalizedCheckBoxMenuItem(MenuItemNames.VIEW_ZOOM_10_MENUITEM,
                    ActionCommands.getZoomActionCommandValue(0.1));
            viewZoom10MenuItem.addActionListener(listener);
            groupZoom.add(viewZoom10MenuItem);
        }

        return viewZoom10MenuItem;

    }

    /**
     * This method initializes viewZoomOtherMenuItem.
     * 
     * @return a new item 'View - Zoom - Other'.
     */
    private JCheckBoxMenuItem getViewZoomOtherMenuItem() {

        if (viewZoomOtherMenuItem == null) {
            viewZoomOtherMenuItem = new LocalizedCheckBoxMenuItem(MenuItemNames.VIEW_ZOOM_OTHER_MENUITEM,
                    ActionCommands.ZOOM_OTHER,
                    true);
            viewZoomOtherMenuItem.addActionListener(listener);
            groupZoom.add(viewZoomOtherMenuItem);
        }

        return viewZoomOtherMenuItem;

    }

    /**
     * This method initializes viewMessageWindowMenuItem.
     * 
     * @return a new item 'View - Message item'.
     */
    private JMenuItem getViewMessageWindowMenuItem() {

        if (viewMessageWindowMenuItem == null) {
            viewMessageWindowMenuItem = new LocalizedMenuItem(MenuItemNames.VIEW_MESSAGEWINDOW_MENUITEM,
                    ActionCommands.MESSAGE_WINDOW);
            viewMessageWindowMenuItem.addActionListener(listener);
        }

        return viewMessageWindowMenuItem;

    }

    /**
     * This method initializes toolsMenu.
     * 
     * @return a new File menu.
     */
    private JMenu getToolsMenu() {

        if (toolsMenu == null) {
            toolsMenu = new JMenu();
            toolsMenu.setName(MenuItemNames.TOOLS_MENU);
            toolsMenu.setText(MenuLocalizer.getLabel(MenuItemNames.TOOLS_MENU));
            toolsMenu.setMnemonic(MenuLocalizer.getMnemonic(MenuItemNames.TOOLS_MENU).charAt(0));
            ToolPluginManager toolsMenuManager = ToolPluginManager.getInstance();
            for (JMenuItem menuItem : toolsMenuManager.getMenuItems()) {
                menuItem.addActionListener(listener);
                toolsMenu.add(menuItem);
            }
            toolsMenu.addSeparator();
            toolsMenu.add(getToolsCostEffectivenessMenuItem());
            toolsMenu.addSeparator();
            toolsMenu.add(getToolsConfigurationMenuItem());
        }

        return toolsMenu;

    }

    private JMenuItem getToolsSensitivityAnalysis() {
        if (toolsProbabilisticCEMenuItem == null) {
            toolsProbabilisticCEMenuItem = new LocalizedMenuItem(MenuItemNames.SENSITIVITYANALYSIS_MENUITEM,
                    ActionCommands.SENSITIVITY_ANALYSIS);
            toolsProbabilisticCEMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P,
                    InputEvent.CTRL_DOWN_MASK));
            toolsProbabilisticCEMenuItem.addActionListener(listener);
            toolsProbabilisticCEMenuItem.setEnabled(false);
        }

        return toolsProbabilisticCEMenuItem;
    }

    private JMenuItem getToolsCostEffectivenessDeterministicMenuItem() {
        if (toolsDeterministicCEMenuItem == null) {
            toolsDeterministicCEMenuItem = new LocalizedMenuItem(MenuItemNames.COSTEFFECTIVENESSDETERMINISTIC_MENUITEM,
                    ActionCommands.COST_EFFECTIVENESS_DETERMINISTIC);
            toolsDeterministicCEMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_T,
                    InputEvent.CTRL_DOWN_MASK));
            toolsDeterministicCEMenuItem.addActionListener(listener);
            toolsDeterministicCEMenuItem.setEnabled(false);
        }

        return toolsDeterministicCEMenuItem;
    }

    private JMenuItem getToolsCostEffectivenessMenuItem() {
        if (toolsCostEffectivenessMenuItem == null) {
            toolsCostEffectivenessMenuItem = new JMenu();
            toolsCostEffectivenessMenuItem.setName(MenuItemNames.COSTEFFECTIVENESS_SUBMENU);
            toolsCostEffectivenessMenuItem.setText(MenuLocalizer.getLabel(MenuItemNames.COSTEFFECTIVENESS_SUBMENU));
            toolsCostEffectivenessMenuItem.add(getToolsCostEffectivenessDeterministicMenuItem());
            toolsCostEffectivenessMenuItem.add(getToolsSensitivityAnalysis());

        }

        return toolsCostEffectivenessMenuItem;
    }

    /**
     * This method initializes toolsConfigurationMenuItem.
     * 
     * @return a new item 'Tools - Configuration'.
     */
    private JMenuItem getToolsConfigurationMenuItem() {

        if (toolsConfigurationMenuItem == null) {
            toolsConfigurationMenuItem = new LocalizedMenuItem(MenuItemNames.CONFIGURATION_MENUITEM,
                    ActionCommands.CONFIGURATION);
            toolsConfigurationMenuItem.addActionListener(listener);
        }

        return toolsConfigurationMenuItem;

    }

    /**
     * This method initializes optionsMenu.
     * 
     * @return a new Options menu.
     */
    /*
     * private JMenu getOptionsMenu() { //FOR FUTURE USE if (optionsMenu ==
     * null) { optionsMenu = new JMenu(); optionsMenu.setName(OPTIONS_MENU);
     * optionsMenu.setText(stringResource.getString(OPTIONS_MENU +
     * LABEL_SUFFIX)); optionsMenu.setMnemonic(stringResource.getString(
     * OPTIONS_MENU + MNEMONIC_SUFFIX).charAt(0)); } return optionsMenu; }
     */// FOR FUTURE USE

    /**
     * This method initializes helpMenu
     * 
     * @return a new Help menu.
     */
    private JMenu getHelpingMenu() {

        if (helpMenu == null) {
            helpMenu = new JMenu();
            helpMenu.setName(MenuItemNames.HELP_MENU);
            helpMenu.setText(MenuLocalizer.getLabel(MenuItemNames.HELP_MENU));
            helpMenu.setMnemonic(MenuLocalizer.getMnemonic(MenuItemNames.HELP_MENU).charAt(0));
            // helpMenu.add(getHelpOpenHelpItem());
            helpMenu.addSeparator();
            helpMenu.add(getHelpOpenChangeLanguageItem());
            helpMenu.addSeparator();
            helpMenu.add(getHelpOpenAboutItem());
        }

        return helpMenu;

    }

    /**
     * This methods initializes openHelpMenuItem
     * 
     * @return a new item 'Help - Help'
     */
    private JMenuItem getHelpOpenHelpItem() {

        if (helpOpenHelpMenuItem == null) {
            helpOpenHelpMenuItem = new LocalizedMenuItem(MenuItemNames.HELP_HELP_MENUITEM,
                    ActionCommands.HELP_HELP);
            HelpViewer helpVw = HelpViewer.getUniqueInstance();
            ActionListener helper = new CSH.DisplayHelpFromSource(helpVw.getHb());
            helpOpenHelpMenuItem.addActionListener(helper);

            helpOpenHelpMenuItem.addActionListener(listener);

        }

        return helpOpenHelpMenuItem;

    }

    /**
     * This methods initializes openChangeLanguageMenuItem
     * 
     * @return a new item 'Help - ChangeLanguage'
     */
    private JMenuItem getHelpOpenChangeLanguageItem() {

        if (helpOpenChangeLanguageMenuItem == null) {
            helpOpenChangeLanguageMenuItem = new LocalizedMenuItem(MenuItemNames.HELP_CHANGELANGUAGE_MENUITEM,
                    ActionCommands.HELP_CHANGE_LANGUAGE);
            helpOpenChangeLanguageMenuItem.addActionListener(listener);
        }

        return helpOpenChangeLanguageMenuItem;

    }

    /**
     * This methods initializes openAboutMenuItem
     * 
     * @return a new item 'Help - About'
     */
    private JMenuItem getHelpOpenAboutItem() {

        if (helpOpenAboutMenuItem == null) {
            helpOpenAboutMenuItem = new LocalizedMenuItem(MenuItemNames.HELP_ABOUT_MENUITEM,
                    ActionCommands.HELP_ABOUT);
            helpOpenAboutMenuItem.addActionListener(listener);
        }

        return helpOpenAboutMenuItem;

    }

    /**
     * This method initializes menuMDI.
     * 
     * @return a new menu dependent of the MDI.
     */
    public JMenu getMenuMDI() {

        if (menuMDI == null) {
            menuMDI = new JMenu();
            menuMDI.setName("MDIMenu");
        }

        return menuMDI;

    }

    /**
     * Checks the checkbox menu item corresponding to the zoom value. If the
     * value isn't any of the prefixed zoom values, then checks the last menu
     * item and modifies its string so that the zoom value appears in the text
     * of the menu item.
     * 
     * @param value
     *            zoom value.
     */
    public void setZoom(double value) {

        if (value == 5) {
            viewZoom500MenuItem.setSelected(true);
        } else if (value == 2) {
            viewZoom200MenuItem.setSelected(true);
        } else if (value == 1.5) {
            viewZoom150MenuItem.setSelected(true);
        } else if (value == 1) {
            viewZoom100MenuItem.setSelected(true);
        } else if (value == 0.75) {
            viewZoom75MenuItem.setSelected(true);
        } else if (value == 0.5) {
            viewZoom50MenuItem.setSelected(true);
        } else if (value == 0.25) {
            viewZoom25MenuItem.setSelected(true);
        } else if (value == 0.1) {
            viewZoom10MenuItem.setSelected(true);
        } else {
            viewZoomOtherMenuItem.setSelected(true);
        }
        viewZoomOtherMenuItem.setText(MenuLocalizer.getLabel(MenuItemNames.VIEW_ZOOM_OTHER_MENUITEM)
                + " ("
                + (int) Math.round(value * 100)
                + "%)...");
    }

    /**
     * Returns the component that correspond to an action command.
     * 
     * @param actionCommand
     *            action command that identifies the component.
     * @return a components identified by the action command.
     */
    private JComponent getJComponentActionCommand(String actionCommand) {

        JComponent component = null;

        if (actionCommand.equals(ActionCommands.NEW_NETWORK)) {
            component = fileNewMenuItem;
        } else if (actionCommand.equals(ActionCommands.OPEN_NETWORK)) {
            component = fileOpenMenuItem;
        } else if (actionCommand.equals(ActionCommands.SAVE_NETWORK)) {
            component = fileSaveMenuItem;
        } else if (actionCommand.equals(ActionCommands.SAVEAS_NETWORK)) {
            component = fileSaveAsMenuItem;
        } else if (actionCommand.equals(ActionCommands.SAVE_OPEN_NETWORK)) {
            component = fileSaveOpenMenuItem;
        } else if (actionCommand.equals(ActionCommands.CLOSE_NETWORK)) {
            component = fileCloseMenuItem;
        } else if (actionCommand.equals(ActionCommands.LOAD_EVIDENCE)) {
            component = fileLoadEvidenceMenuItem;
        } else if (actionCommand.equals(ActionCommands.SAVE_EVIDENCE)) {
            component = fileSaveEvidenceMenuItem;
        } else if (actionCommand.equals(ActionCommands.NETWORK_PROPERTIES)) {
            component = fileNetworkPropertiesMenuItem;
        } else if (actionCommand.equals(ActionCommands.EXIT_APPLICATION)) {
            component = fileExitMenuItem;
        } else if (actionCommand.equals(ActionCommands.CLIPBOARD_CUT)) {
            component = editCutMenuItem;
        } else if (actionCommand.equals(ActionCommands.CLIPBOARD_COPY)) {
            component = editCopyMenuItem;
        } else if (actionCommand.equals(ActionCommands.CLIPBOARD_PASTE)) {
            component = editPasteMenuItem;
        } else if (actionCommand.equals(ActionCommands.OBJECT_REMOVAL)) {
            component = editRemoveMenuItem;
        } else if (actionCommand.equals(ActionCommands.UNDO)) {
            component = editUndoMenuItem;
        } else if (actionCommand.equals(ActionCommands.REDO)) {
            component = editRedoMenuItem;
        } else if (actionCommand.equals(ActionCommands.SELECT_ALL)) {
            component = editSelectAllMenuItem;
        } else if (actionCommand.equals(ActionCommands.OBJECT_SELECTION)) {
            component = editObjectSelectionMenuItem;
        } else if (actionCommand.equals(ActionCommands.CHANCE_CREATION)) {
            component = editChanceCreationMenuItem;
        } else if (actionCommand.equals(ActionCommands.DECISION_CREATION)) {
            component = editDecisionCreationMenuItem;
        } else if (actionCommand.equals(ActionCommands.UTILITY_CREATION)) {
            component = editUtilityCreationMenuItem;
        } else if (actionCommand.equals(ActionCommands.LINK_CREATION)) {
            component = editLinkCreationMenuItem;
        } else if (actionCommand.equals(ActionCommands.NODE_PROPERTIES)) {
            component = editNodePropertiesMenuItem;
        } else if (actionCommand.equals(ActionCommands.EDIT_POTENTIAL)) {
            component = editRelationMenuItem;
        } else if (actionCommand.equals(ActionCommands.LINK_PROPERTIES)) {
            component = editLinkPropertiesMenuItem;
        } else if (actionCommand.equals(ActionCommands.CHANGE_TO_INFERENCE_MODE)) {
            component = editSwitchToInferenceModeMenuItem;
        } else if (actionCommand.equals(ActionCommands.CHANGE_TO_EDITION_MODE)) {
            component = inferenceSwitchToEditionModeMenuItem;
        } else if (actionCommand.equals(ActionCommands.INFERENCE_OPTIONS)) {
            component = inferenceOptionsMenuItem;
        } else if (actionCommand.equals(ActionCommands.CREATE_NEW_EVIDENCE_CASE)) {
            component = inferenceCreateNewEvidenceCaseMenuItem;
        } else if (actionCommand.equals(ActionCommands.GO_TO_FIRST_EVIDENCE_CASE)) {
            component = inferenceGoToFirstEvidenceCaseMenuItem;
        } else if (actionCommand.equals(ActionCommands.GO_TO_PREVIOUS_EVIDENCE_CASE)) {
            component = inferenceGoToPreviousEvidenceCaseMenuItem;
        } else if (actionCommand.equals(ActionCommands.GO_TO_NEXT_EVIDENCE_CASE)) {
            component = inferenceGoToNextEvidenceCaseMenuItem;
        } else if (actionCommand.equals(ActionCommands.GO_TO_LAST_EVIDENCE_CASE)) {
            component = inferenceGoToLastEvidenceCaseMenuItem;
        } else if (actionCommand.equals(ActionCommands.CLEAR_OUT_ALL_EVIDENCE_CASES)) {
            component = inferenceClearEvidenceCasesMenuItem;
        } else if (actionCommand.equals(ActionCommands.PROPAGATE_EVIDENCE)) {
            component = inferencePropagateEvidenceMenuItem;
        } else if (actionCommand.equals(ActionCommands.NODE_EXPANSION)) {
            component = inferenceExpandNodeMenuItem;
        } else if (actionCommand.equals(ActionCommands.NODE_CONTRACTION)) {
            component = inferenceContractNodeMenuItem;
        } else if (actionCommand.equals(ActionCommands.NODE_REMOVE_ALL_FINDINGS)) {
            component = inferenceRemoveAllFindingsMenuItem;
        } else if (actionCommand.equals(ActionCommands.BYTITLE_NODES)) {
            component = viewNodesByTitleMenuItem;
        } else if (actionCommand.equals(ActionCommands.BYNAME_NODES)) {
            component = viewNodesByNameMenuItem;
        } else if (actionCommand.equals(ActionCommands.ZOOM_IN)) {
            component = viewZoomInMenuItem;
        } else if (actionCommand.equals(ActionCommands.ZOOM_OUT)) {
            component = viewZoomOutMenuItem;
        } else if (actionCommand.equals(ActionCommands.ZOOM_OTHER)) {
            component = viewZoomOtherMenuItem;
        } else if (actionCommand.equals(ActionCommands.ZOOM)) {
            component = viewZoomMenu;
        } else if (actionCommand.equals(ActionCommands.NODES)) {
            component = viewNodesMenu;
        } else if (actionCommand.equals(ActionCommands.COST_EFFECTIVENESS_DETERMINISTIC)) {
            component = toolsDeterministicCEMenuItem;
        } else if (actionCommand.equals(ActionCommands.SENSITIVITY_ANALYSIS)) {
            component = toolsProbabilisticCEMenuItem;
        }

        return component;

    }

    /**
     * Enables or disabled an option identified by an action command.
     * 
     * @param actionCommand
     *            action command that identifies the option.
     * @param b
     *            true to enable the option, false to disable.
     */
    public void setOptionEnabled(String actionCommand, boolean b) {

        MenuToolBarBasicImpl.setOptionEnabled(getJComponentActionCommand(actionCommand), b);

    }

    /**
     * Selects or unselects an option identified by an action command. Only
     * selects or unselects the components that are AbstractButton.
     * 
     * @param actionCommand
     *            action command that identifies the option.
     * @param b
     *            true to select the option, false to unselect.
     */
    public void setOptionSelected(String actionCommand, boolean b) {

        MenuToolBarBasicImpl.setOptionSelected(getJComponentActionCommand(actionCommand), b);

    }

    /**
     * Adds a text to the label of an option identified by an action command.
     * Only adds a text to the components that are AbstractButton.
     * 
     * @param actionCommand
     *            action command that identifies the option.
     * @param text
     *            text to add to the label of the options. If null, nothing is
     *            added.
     */
    public void addOptionText(String actionCommand, String text) {

        JComponent component = getJComponentActionCommand(actionCommand);
        MenuToolBarBasicImpl.addOptionText(component, defaultText.get(component), text);

    }

    /**
     * Changes the text of menu item
     * 
     * @param actionCommand
     *            action command that identifies the option.
     * @param text
     *            text to set to the label.
     */
    public void setText(String actionCommand, String text) {

        JComponent component = getJComponentActionCommand(actionCommand);
        MenuToolBarBasicImpl.setText(component, text);

    }
}
