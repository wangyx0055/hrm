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

/**
 * Represents one type of ResponseData that describe the status of certain business element.
 * The status of each element can be used to affect the state of page elements once 
 * this descriptor is passed to the client and if that client put those page elements
 * under control.
 * 
 * @author davis
 */
public class ElementDescriptor implements ResponseData {
        
        public ElementDescriptor() {
        }
}
