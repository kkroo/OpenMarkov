/*
 * Copyright 2011 CISIAD, UNED, Spain Licensed under the European Union Public
 * Licence, version 1.1 (EUPL) Unless required by applicable law, this code is
 * distributed on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
 */

package org.openmarkov.core.gui.loader.element;

import java.net.URL;
import java.util.MissingResourceException;

import javax.swing.ImageIcon;

import org.openmarkov.core.gui.localize.StringDatabase;

/**
 * This class is used to load icons from a folder.
 * @author jmendoza
 * @version 1.0
 * @author jlgozalo
 * @version 1.1 jlgozalo 20/08 add Infinite Positive and Negative icons
 */
public class IconLoader
{
    /**
     * Folder where icons are saved.
     */
    // TODO must be externalize in a property
    private static final String RESOURCE_ICONS_PATH                       = "icons/";
    /**
     * Name of the 'new' enabled icon.
     */
    public static final String  ICON_NEW_ENABLED                          = "new.gif";
    /**
     * Name of the 'open' enabled icon.
     */
    public static final String  ICON_OPEN_ENABLED                         = "open.gif";
    /**
     * Name of the 'save' enabled icon.
     */
    public static final String  ICON_SAVE_ENABLED                         = "save.gif";
    /**
     * Name of the 'close' enabled icon.
     */
    public static final String  ICON_CLOSE_ENABLED                        = "close.gif";
    /**
     * Name of the 'undo' enabled icon.
     */
    public static final String  ICON_UNDO_ENABLED                         = "undo.gif";
    /**
     * Name of the 'redo' enabled icon.
     */
    public static final String  ICON_REDO_ENABLED                         = "redo.gif";
    /**
     * Name of the 'redo' enabled icon.
     */
    public static final String  ICON_ACCEPT_ENABLED                       = "green_ok.gif";
    /**
     * Name of the 'redo' enabled icon.
     */
    public static final String  ICON_APPLY_ENABLED                        = "green_apply.gif";
    /**
     * Name of the 'object selection' enabled icon.
     */
    public static final String  ICON_SELECTION_ENABLED                    = "selection.gif";
    /**
     * Name of the 'chance node creation' enabled icon.
     */
    public static final String  ICON_CHANCE_ENABLED                       = "chance.gif";
    /**
     * Name of the 'decision node creation' enabled icon.
     */
    public static final String  ICON_DECISION_ENABLED                     = "decision.gif";
    /**
     * Name of the 'utility node creation' enabled icon.
     */
    public static final String  ICON_UTILITY_ENABLED                      = "utility.gif";
    /**
     * Name of the 'link creation' enabled icon.
     */
    public static final String  ICON_LINK_ENABLED                         = "link.gif";
    /**
     * Name of the 'zoom in' enabled icon.
     */
    public static final String  ICON_ZOOM_IN_ENABLED                      = "zoomin.gif";
    /**
     * Name of the 'zoom out' enabled icon.
     */
    public static final String  ICON_ZOOM_OUT_ENABLED                     = "zoomout.gif";
    /**
     * Name of the 'cut' enabled icon.
     */
    public static final String  ICON_CUT_ENABLED                          = "cut.gif";
    /**
     * Name of the 'copy' enabled icon.
     */
    public static final String  ICON_COPY_ENABLED                         = "copy.gif";
    /**
     * Name of the 'paste' enabled icon.
     */
    public static final String  ICON_PASTE_ENABLED                        = "paste.gif";
    /**
     * Name of the 'remove' enabled icon.
     */
    public static final String  ICON_REMOVE_ENABLED                       = "remove.gif";
    /**
     * Name of the 'arrow up' enabled icon.
     */
    public static final String  ICON_ARROW_UP_ENABLED                     = "arrowUp.gif";
    /**
     * Name of the 'arrow down' enabled icon.
     */
    public static final String  ICON_ARROW_DOWN_ENABLED                   = "arrowDown.gif";
    /**
     * Name of the 'plus' enabled icon.
     */
    public static final String  ICON_PLUS_ENABLED                         = "plus.gif";
    /**
     * Name of the 'minus' enabled icon.
     */
    public static final String  ICON_MINUS_ENABLED                        = "minus.gif";
    /**
     * Name of the 'infinite positive' enabled icon.
     */
    public static final String  ICON_INFINITE_POSITIVE_ENABLED            = "positiveInfinite.gif";
    /**
     * Name of the 'infinite negative' enabled icon.
     */
    public static final String  ICON_INFINITE_NEGATIVE_ENABLED            = "negativeInfinite.gif";
    /**
     * Name of the OpenMarkov Logo 16 icon.
     */
    public static final String  OPENMARKOV_LOGO_ICON_16                   = "OM_16p4.png";
    /**
     * Name of the OpenMarkov Logo 32 icon.
     */
    public static final String  OPENMARKOV_LOGO_ICON_32                   = "C2_32.ico";
    /**
     * Name of the OpenMarkov Logo 64 icon.
     */
    public static final String  OPENMARKOV_LOGO_ICON_64                   = "C2_64.ico";
    /**
     * Name of the 'edition mode' enabled icon.
     */
    public static final String  ICON_EDITION_MODE_ENABLED                 = "edition_mode.png";
    /**
     * Name of the 'inference mode' enabled icon.
     */
    public static final String  ICON_INFERENCE_MODE_ENABLED               = "inference_mode.png";
    /**
     * Name of the 'Create New Evidence Case' enabled icon.
     */
    public static final String  ICON_CREATE_NEW_EVIDENCE_CASE_ENABLED     = "createNewCase.png";
    /**
     * Name of the 'Go To First Evidence Case' enabled icon.
     */
    public static final String  ICON_GO_TO_FIRST_EVIDENCE_CASE_ENABLED    = "goFirst.png";
    /**
     * Name of the 'Go To Previous Evidence Case' enabled icon.
     */
    public static final String  ICON_GO_TO_PREVIOUS_EVIDENCE_CASE_ENABLED = "goPrevious.png";
    /**
     * Name of the 'Go To Next Evidence Case' enabled icon.
     */
    public static final String  ICON_GO_TO_NEXT_EVIDENCE_CASE_ENABLED     = "goNext.png";
    /**
     * Name of the 'Go To Last Evidence Case' enabled icon.
     */
    public static final String  ICON_GO_TO_LAST_EVIDENCE_CASE_ENABLED     = "goLast.png";
    /**
     * Name of the 'Clear Out All Evidence Cases' enabled icon.
     */
    public static final String  ICON_CLEAR_OUT_ALL_EVIDENCE_CASES_ENABLED = "clearOutAllCases.png";
    /**
     * Name of the 'propagate evidence' enabled icon.
     */
    public static final String  ICON_PROPAGATE_EVIDENCE_ENABLED           = "propagate_evidence.png";
    /**
     * Name of the 'Uncertainty' enabled icon.
     */
    public static final String  ICON_UNCERTAINTY                          = "uncertainty2.png";
    /**
     * Name of the 'decision tree' enabled icon.
     */
    public static final String  ICON_DECISION_TREE                        = "dectree.gif";

    /**
     * This method loads an icon resource.
     * @param iconName name of the icon to load.
     * @return a reference to the icon resource.
     * @throws MissingResourceException if the resource doesn't exist.
     */
    public ImageIcon load (String iconName)
        throws MissingResourceException
    {
        URL icon = getClass ().getClassLoader ().getResource (RESOURCE_ICONS_PATH + iconName);
        if (icon == null)
        {
            throw new MissingResourceException (
                                                StringDatabase.getUniqueInstance ().getString ("IconResourceNotExists.Text.Label")
                                                        + " " + RESOURCE_ICONS_PATH + iconName,
                                                getClass ().getName (), iconName);
        }
        return new ImageIcon (icon);
    }
}
