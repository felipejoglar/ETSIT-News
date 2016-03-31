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
package com.fjoglar.etsitnews.utils;

import java.util.ArrayList;
import java.util.List;

public class AttachmentsUtils {

    public static List<Attachment> extractAttachments(String attachments) {
        if (!(attachments.length() > 0)) {
            return null;
        }

        List<Attachment> attachmentList = new ArrayList<>();
        attachments = attachments.substring(0, attachments.length() - 3);
        String[] attachmentsParts = attachments.split("___");

        for (int i = 0; i < attachmentsParts.length; i = i + 2) {
            Attachment attachment = new Attachment(attachmentsParts[i], attachmentsParts[i + 1]);
            attachmentList.add(attachment);
        }

        return attachmentList;
    }

    public static class Attachment {
        String downloadLink;
        String title;
        FILE_TYPE fileType;

        public enum FILE_TYPE{
            FILE,
            IMAGE,
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

        @Override
        public String toString() {
            return this.getTitle() + " -----> " + getDownloadLink();
        }

        private FILE_TYPE setFileType(String title) {
            String extension = title.substring(title.lastIndexOf(".") + 1, title.length());

            if (extension.equalsIgnoreCase("doc")
                    || extension.equalsIgnoreCase("pdf")
                    || extension.equalsIgnoreCase("zip")) {
                return  FILE_TYPE.FILE;
            } else if (extension.equalsIgnoreCase("jpg")
                    || extension.equalsIgnoreCase("png")) {
                return FILE_TYPE.IMAGE;
            } else {
                return FILE_TYPE.LINK;
            }
        }
    }
}
