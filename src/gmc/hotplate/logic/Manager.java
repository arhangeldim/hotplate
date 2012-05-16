/* Copyright (c) 2012 Hotplate developers. All rights reserved.
 * Use of this source code is governed by a BSD-style license that can be
 * found in the LICENSE file.
 *
 * project: Hotplate
 */

package gmc.hotplate.logic;

import gmc.hotplate.R;
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
import android.widget.Button;
import android.widget.TextView;

public final class Manager {

    private static final String LOG_TAG = Manager.class.getName();
    private static Manager sInstance = null;
    private Recipe currentRecipe;
    private Activity activity;
    private List<TextView> cachedTextViews;
    private List<View> cachedViews;
    private List<Boolean> isCached = null;
    private List<Boolean> isTimerStarted = null;

    private Manager() {
        cachedViews = new ArrayList<View>();
        cachedTextViews = new ArrayList<TextView>();
    }

    public static synchronized Manager getInstance() {
        if (sInstance == null) {
            sInstance = new Manager();
        }
        return sInstance;
    }

    public TextView getCachedTextView(int position) {
        return cachedTextViews.get(position);
    }

    public Boolean isTimerStarted(int position) {
        return isTimerStarted.get(position);
    }

    public Boolean isAnyTimerStarted() {
        Boolean result = Boolean.FALSE;
        if (isTimerStarted == null) {
            return Boolean.FALSE;
        }
        for (Boolean b : isTimerStarted) {
            result |= b;
        }
        return result;
    }

    public void setTimerStarted(int position, Boolean state) {
        isTimerStarted.set(position, state);
    }

    public Recipe getCurrentRecipe() {
        return currentRecipe;
    }

    public void setCurrentRecipe(Recipe currentRecipe) {
        this.currentRecipe = currentRecipe;
    }

    public Activity getActivity() {
        return activity;
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    public List<TextView> getCachedTextViews() {
        return cachedTextViews;
    }

    public void setCachedTextViews(List<TextView> cachedTextViews) {
        this.cachedTextViews = cachedTextViews;
    }

    public List<View> getCachedViews() {
        return cachedViews;
    }

    public void setCachedViews(List<View> cachedViews) {
        this.cachedViews = cachedViews;
    }

    public Button getButton(int position) {
        return (Button) cachedViews.get(position).findViewById(R.id.btnTimerControl);
    }

    public List<Boolean> getIsCached() {
        return isCached;
    }

    public void setIsCached(List<Boolean> isCached) {
        this.isCached = isCached;
    }

    public List<Boolean> getIsTimerStarted() {
        return isTimerStarted;
    }

    public void setIsTimerStarted(List<Boolean> isTimerStarted) {
        this.isTimerStarted = isTimerStarted;
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
