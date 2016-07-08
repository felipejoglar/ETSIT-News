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

import com.fjoglar.etsitnews.data.entities.Attachment;

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

}
