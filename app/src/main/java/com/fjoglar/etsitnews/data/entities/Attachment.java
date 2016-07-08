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

public class Attachment {

    String downloadLink;
    String title;
    FILE_TYPE fileType;

    public enum FILE_TYPE {
        FILE,
        IMAGE,
        FOLDER,
        LINK
    }

    public Attachment(String downloadLink, String title) {
        this.downloadLink = downloadLink;
        this.title = title;
        this.fileType = setFileType(title);
    }

    public String getTitle() {
        return title;
    }

    public String getDownloadLink() {
        return downloadLink;
    }

    public FILE_TYPE getFileType() {
        return fileType;
    }

    private FILE_TYPE setFileType(String title) {
        String extension = title.substring(title.lastIndexOf(".") + 1, title.length());

        if (extension.equalsIgnoreCase("doc")
                || extension.equalsIgnoreCase("docx")
                || extension.equalsIgnoreCase("pdf")) {
            return FILE_TYPE.FILE;
        } else if (extension.equalsIgnoreCase("jpg")
                || extension.equalsIgnoreCase("png")) {
            return FILE_TYPE.IMAGE;
        } else if (extension.equalsIgnoreCase("zip")) {
            return FILE_TYPE.FOLDER;
        } else {
            return FILE_TYPE.LINK;
        }
    }

}
