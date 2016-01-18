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
package hrm.model;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * Abnormal conditions produced by DBFormModule.
 * @author davis
 */
public class DataComponentException extends Exception {
        public enum Error {
                InvalidComponent,
                CreationForbidden,
                Unknown,
                Loading,
                Storing
        }
        
        private final String            m_message;
        private final String            m_stacktrace;
        private String                  m_extrainfo = "";
        
        DataComponentException(Error error_type) {
                switch (error_type) {
                        case InvalidComponent:
                                m_message = "DataComponent Invalid Component Type Error!";
                                break;
                        case CreationForbidden:
                                m_message = "DataComponent Creation of Such Component Is Forbidden!";
                                break;
                        case Loading:
                                m_message = "DataComponent Loading Error!";
                                break;
                        case Storing:
                                m_message = "DataComponent Storing Error!";
                                break;
                        default:
                                m_message = "DataComponent Unknown error!";
                }
                StringWriter sw = new StringWriter();
                PrintWriter pw = new PrintWriter(sw);
                this.printStackTrace(pw);
                m_stacktrace = sw.toString();
        }
        
        public DataComponentException add_extra_info(String info) {
                m_extrainfo += info + ";";
                return this;
        }
        
        @Override
        public String getMessage() {
                return toString();
        }
        
        @Override
        public String toString() {
                return m_message + ", details: " + m_extrainfo + "\n" + m_stacktrace;
        }
}
