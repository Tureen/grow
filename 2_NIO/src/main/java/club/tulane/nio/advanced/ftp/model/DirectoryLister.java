/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package club.tulane.nio.advanced.ftp.model;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * <strong>Internal class, do not use directly.</strong>
 *
 * This class prints file listing.
 *
 * @author <a href="http://mina.apache.org">Apache MINA Project</a>
 */
public class DirectoryLister {

    private String traverseFiles(final List<? extends FileView> files, final LISTFileFormatter formater) {
        StringBuilder sb = new StringBuilder();

        sb.append(traverseFiles(files, formater, true));
        sb.append(traverseFiles(files, formater, false));

        return sb.toString();
    }

    private String traverseFiles(final List<? extends FileView> files, final LISTFileFormatter formater,
            boolean matchDirs) {
        StringBuilder sb = new StringBuilder();
        for (FileView file : files) {
            if (file == null) {
                continue;
            }

            if (file.isDirectory() == matchDirs) {
                sb.append(formater.format(file));
            }
        }

        return sb.toString();
    }

    public String listFiles(final ListArgument argument,
            final FileSystemView fileSystemView, final LISTFileFormatter formater)
            throws IOException {

        StringBuilder sb = new StringBuilder();

        // get all the file objects
        List<? extends FileView> files = listFiles(fileSystemView, argument.getFile());
        if (files != null) {

            sb.append(traverseFiles(files, formater));
        }

        return sb.toString();
    }

    /**
     * Get the file list. Files will be listed in alphabetlical order.
     */
    private List<? extends FileView> listFiles(FileSystemView fileSystemView, String file) {
        List <? extends FileView> files = null;
        try {
        	FileView virtualFile = fileSystemView.getFile(file);
            if (virtualFile.isFile()) {
                List<FileView> auxFiles = new ArrayList<FileView>();
                auxFiles.add(virtualFile);
                files = auxFiles;
            } else {
                files = virtualFile.listFiles();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return files;
    }
}
