/*
 * Copyright (c) 2012 Hotplate developers. All rights reserved.
 * Use of this source code is governed by a BSD-style license that can be
 * found in the LICENSE file.
 *
 * project: Hotplate
 */

package gmc.hotplate.logic;

import gmc.hotplate.entities.Recipe;
import gmc.hotplate.entities.Step;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import android.app.Activity;
import android.util.Log;
import android.view.View;

public final class RecipeManager {
    private static final String LOG_TAG = RecipeManager.class.getName();
    private static RecipeManager sInstance = null;
    private Recipe currentRecipe;
    private Activity currentActivity;
    private List<Boolean> isCreated;
    private List<View> views;

    private RecipeManager() {
        views = new ArrayList<View>();
        isCreated = new ArrayList<Boolean>();
    }

    public static synchronized RecipeManager getInstance() {
        if (sInstance == null) {
            sInstance = new RecipeManager();
        }
        return sInstance;
    }

    
    
    public List<Boolean> getIsCreated() {
        return isCreated;
    }

    public void setIsCreated(List<Boolean> isCreated) {
        this.isCreated = isCreated;
    }

    public List<View> getViews() {
        return views;
    }

    public void setViews(List<View> views) {
        this.views = views;
    }

    public Recipe getCurrentRecipe() {
        return currentRecipe;
    }

    public void setCurrentRecipe(Recipe currentRecipe) {
        this.currentRecipe = currentRecipe;
    }

    public Activity getCurrentActivity() {
        return currentActivity;
    }

    public void setCurrentActivity(Activity currentActivity) {
        this.currentActivity = currentActivity;
    }

    public List<Step> parseSteps(String xmlString) throws ParserConfigurationException,
            SAXException, IOException {
        Document doc = getDocument(xmlString);
        Element element = doc.getDocumentElement();
        NodeList stepNodes = element.getElementsByTagName("step");
        List<Step> stepList = new ArrayList<Step>();
        for (int i = 0; i < stepNodes.getLength(); i++) {
            Node stepNode = stepNodes.item(i);
            Step step = new Step();
            NodeList properties = stepNode.getChildNodes();
            for (int j = 0; j < properties.getLength(); j++) {
                Node current = properties.item(j);
                if (current.getNodeName().equals("#text")) {
                    continue;
                } else if (current.getNodeName().equals("id")) {
                    step.setId(Integer.parseInt(current.getTextContent().trim()));
                } else if (current.getNodeName().equals("name")) {
                    step.setName(current.getTextContent());
                } else if (current.getNodeName().equals("description")) {
                    step.setDescription(current.getTextContent());
                } else if (current.getNodeName().equals("time")) {
                    step.setTime(Integer.parseInt(current.getTextContent().trim()));
                } else {
                    Log.d(LOG_TAG, "Xml Parsing. Undefining node: " + current.getNodeName());
                }
            }
            stepList.add(step);
        }
        return stepList;
    }

    private Document getDocument(String xmlString) throws ParserConfigurationException,
            SAXException, IOException {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setIgnoringElementContentWhitespace(true);
        DocumentBuilder builder = dbf.newDocumentBuilder();
        return builder.parse(new InputSource(new StringReader(xmlString)));
    }

}
