/*
 * Copyright (C) 2015 davis
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package hrm.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * Utility to help scan and open files in a directory.
 *
 * @author davis
 */
public class ResourceScanner {

        private static String m_context_path = "";
        
        public interface Filter {
                
                boolean is_accepted(File file);
        }

        /**
         * Set the base path to operate on.
         *
         * @param root the base path.
         */
        public static void init_context_path(String root) {
                m_context_path = root;
        }

        /**
         * This would open all files at the directory specified.
         *
         * @param path the directory to be scanned.
         * @param filter filter unwanted files.
         * @return InputStream representing all opened file at current
         * directory.
         * @throws java.io.FileNotFoundException
         */
        public static List<InputStream> open_external_files_at(String path, Filter filter) 
                throws FileNotFoundException {
                if (path == null) throw new FileNotFoundException("The path parameter is null");
                File at_current = new File(m_context_path + path);
                List<InputStream> ins = new LinkedList<>();
                for (File f : at_current.listFiles()) {
                        if (f.isFile() && (filter == null || filter.is_accepted(f))) {
                                Prompt.log(Prompt.NORMAL, "ResourceScanner.open_external_files_at", 
                                        "Openning file: " + f.getPath());
                                ins.add(new FileInputStream(f));
                        }
                }
                return ins;
        }

        /**
         * This would open all files recursively at the directory specified.
         *
         * @param path the root directory to be scanned.
         * @param filter filter unwanted files.
         * @return InputStream representing all opened file at current
         * directory.
         * @throws java.io.FileNotFoundException
         */
        public static List<InputStream> open_external_files_recursively_at(String path, Filter filter) 
                throws FileNotFoundException {
                if (path == null) throw new FileNotFoundException("The path parameter is null");
                File at_current = new File(m_context_path + path);
                
                Queue<File> dirs = new LinkedList<>();
                dirs.add(at_current);
                
                List<InputStream> ins = new LinkedList<>();
                
                while (!dirs.isEmpty()) {
                        File current = dirs.poll();
                        for (File f : current.listFiles()) {
                                if (f.isFile() && (filter == null || filter.is_accepted(f))) {
                                        Prompt.log(Prompt.NORMAL, "ResourceScanner.open_external_files_recursively_at", 
                                                "Openning file: " + f.getPath());
                                        ins.add(new FileInputStream(f));
                                } else if (f.isDirectory()) {
                                        dirs.add(f);
                                }
                        }
                }
                return ins;
        }

        /**
         * This would open a file with the file path specified.
         *
         * @param file the file.
         * @return An InputStream representing the opened file.
         * @throws FileNotFoundException
         */
        public static InputStream open_external_file(String file) throws FileNotFoundException {
                if (file == null) throw new FileNotFoundException("The file parameter is null");
                Prompt.log(Prompt.NORMAL, "ResourceScanner.open_external_file", 
                                        "Openning file: " + m_context_path + file);
                return new FileInputStream(m_context_path + file);
        }
}
