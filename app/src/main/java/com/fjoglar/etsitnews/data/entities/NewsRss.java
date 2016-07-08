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
package com.fjoglar.etsitnews.data.entities;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

/**
 * This class holds the RSS from www.etsit.uva.es.
 * It is made to work with retrofit and simpleXML converter.
 *
 * More info on simple XML serialization:
 * http://simple.sourceforge.net/home.php
 */
@Root(name = "rss")
public class NewsRss {

    @Attribute(name = "version")
    private String version;

    @Element(name = "channel")
    private NewsChannel newsChannel;

    public NewsChannel getChannel() {
        return newsChannel;
    }
}
