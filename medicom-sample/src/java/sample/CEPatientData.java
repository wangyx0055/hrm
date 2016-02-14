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
package sample;

import hrm.controller.CalleeContext;
import hrm.controller.ReturnValue;
import java.awt.event.ActionEvent;

/**
 *
 * @author davis
 */
public class CEPatientData extends CalleeContext {

        public CEPatientData() {
        }

        @Override
        public ReturnValue process(String action) {
                switch (action) {
                        case "upload": {
                                return new ReturnValue() {};
                        }
                        default:
                                return new ReturnValue() {};
                }
        }

        @Override
        public void actionPerformed(ActionEvent e) {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
}
