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
import hrm.utils.Element;
import hrm.view.JSPResolver;
import java.util.Set;

/**
 * Definition of a Callee where is responsible for performing action when it is dispatched by request.
 *
 * @author davis
 */
public abstract class CalleeContext implements JSPResolverListener {

        abstract public Set<Element> get_param_constraints();

        abstract public void add_params(Set<Attribute> attri);

        abstract public ReturnValue get_return_value();

        private JSPResolver m_resolver;

        @Override
        public void set_resolver(JSPResolver res) {
                m_resolver = res;
        }

        @Override
        public JSPResolver get_resolver() {
                return m_resolver;
        }
}
