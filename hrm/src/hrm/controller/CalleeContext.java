/*
 * Copyright (C) 2016 davis
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
package hrm.controller;

import hrm.utils.Attribute;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Definition of a Callee where is responsible for performing action when it is dispatched by request.
 *
 * @author davis
 */
public abstract class CalleeContext implements JSPResolverListener {

        /**
         * Main processing method.
         * @param action the action requested.
         * @return the results.
         * @throws java.lang.Exception
         */
        abstract public ReturnValue process(String action) throws Exception;

        private JSPResolver m_resolver;

        @Override
        public void set_resolver(JSPResolver res) {
                m_resolver = res;
        }

        @Override
        public JSPResolver get_resolver() {
                return m_resolver;
        }
        
        private Map<String, String[]>   m_params = new HashMap<>();
        private List<DataPart>          m_parts;
        private String                  m_incoming_uri = "";
        
        /**
         * Helper method to set all required parameters.
         * @param param one of the parameter to be requested.
         * @throws java.lang.Exception
         */
        public void require(Parameter param) throws Exception {
                String[] value = m_params.get(param.name());
                if (value == null) {
                        throw new Exception("param: " + param.name() + " is required");
                }
                param.assign(value);
        }
        
        public List<DataPart> require_data_streams() throws Exception {
                if (m_parts == null) {
                        throw new Exception("file_name&data_stream are required but not supplied");
                }
                return m_parts;
        }
        
        private class ReturnCleaner extends ReturnValue {
                private final ReturnValue m_ret;
                
                public ReturnCleaner(ReturnValue ret) {
                        m_ret = ret;
                }
                
                @Override
                public String get_redirected_page_uri() {
                        String uri = m_ret.get_redirected_page_uri();
                        if (uri == null) {
                                uri = m_incoming_uri.replace(".jspx", ".jsp");
                        }
                        return uri;
                }

                @Override
                public Set<Attribute> get_session_attribute() {
                        return m_ret.get_session_attribute();
                }

                @Override
                public Set<Attribute> get_requst_attribute() {
                        return m_ret.get_requst_attribute();
                }

                @Override
                public JSPResolver get_resolver() {
                        return m_ret.get_resolver();
                }
                
        }
        
        public ReturnValue get_return_value(Map<String, String[]> params, 
                                            List<DataPart> parts,
                                            String incoming_uri) throws Exception {
                m_params = params;
                m_incoming_uri = incoming_uri;
                m_parts = parts;
                String[] action = params.get("action");
                ReturnValue ret;
                try {
                        ret = process(action == null ? null : action[0]);
                } catch (Exception ex) {
                        throw new Exception(ex.getMessage() + ", Details: " + 
                                            "params: " + params + "\n" + 
                                            "parts: " + parts + "\n" + 
                                            "incoming_uri: " + incoming_uri);
                }
                return new ReturnCleaner(ret);
        }
}
