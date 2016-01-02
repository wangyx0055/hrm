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
 * Indicate errors associated with manipulating a system form.
 * @author davis
 */
public class SystemFormException extends Exception {
        public enum Error {
                UnknownError,
                InitializationError,
                FetchingError,
                UpdateError,
                QueryError,
                StoringError,
                InvalidParameterError
        }
        
        private final String            m_message;
        private final String            m_stacktrace;
        private String                  m_extrainfo = "";
        
        SystemFormException(Error error_type) {
                switch (error_type) {
                        case InitializationError:
                                m_message = "SystemPreset Initialization Error!";
                                break;
                        case FetchingError:
                                m_message = "SystemPreset Fetching Error!";
                                break;
                        case UpdateError:
                                m_message = "SystemForm Update Error!";
                                break;
                        case QueryError:
                                m_message = "SystemForm qeury Error!";
                                break;
                        case StoringError:
                                m_message = "SystemForm Storing Error!";
                                break;
                        case InvalidParameterError:
                                m_message = "SystemForm Invalid Parameter(s) Error!";
                                break;
                        default:
                                m_message = "Unknown error!";
                }
                StringWriter sw = new StringWriter();
                PrintWriter pw = new PrintWriter(sw);
                this.printStackTrace(pw);
                m_stacktrace = sw.toString();
        }
        
        public SystemFormException add_extra_info(String info) {
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
