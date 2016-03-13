/*
 * Copyright (C) 2016 Felipe Joglar Santos
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.fjoglar.etsitnews.model.entities;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

import java.io.Serializable;

/**
 * This class holds the items in RSS Channel from www.etsit.uva.es.
 * It is made to work with retrofit and simpleXML converter.
 *
 * More info on simple XML serialization:
 * http://simple.sourceforge.net/home.php
 */

@Root(name = "item",
        strict = false)
public class NewsItem implements Serializable{

    @Element(name = "title")
    private String title;

    @Element(name = "link")
    private String link;

    @Element(name = "description", required = false)
    private String description;

    @Element(name = "category")
    private String category;

    @Element(name = "pubDate")
    private String pubDate;

    private int formattedPubDate;

    public String getTitle() {
        return title;
    }

    public String getLink() {
        return link;
    }

    public String getDescription() {
        return description;
    }

    public String getCategory() {
        return category;
    }

    public String getPubDate() {
        return pubDate;
    }

    public int getFormattedPubDate() {
        return formattedPubDate;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setPubDate(String pubDate) {
        this.pubDate = pubDate;
    }

    public void setFormattedPubDate(int formattedPubDate) {
        this.formattedPubDate = formattedPubDate;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append(this.getTitle() + "\n");
        stringBuilder.append(this.getPubDate() + "\n");
        stringBuilder.append(this.getDescription() + "\n");
        stringBuilder.append(this.getCategory() + "\n");
        stringBuilder.append(this.getLink());

        return stringBuilder.toString();
    }
}
