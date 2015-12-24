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

/**
 * Log output.
 *
 * @author davis
 */
public class Prompt {

        public static final int NORMAL = 1;
        public static final int WARNING = 2;
        public static final int ERROR = 3;

        public static void log(int level, String loc, String message) {
                String prefix;
                switch (level) {
                        case NORMAL:
                                prefix = "Normal:";
                                break;
                        case WARNING:
                                prefix = "Warning: ";
                                break;
                        case ERROR:
                                prefix = "Error: ";
                                break;
                        default:
                                prefix = "";
                }
                if (loc != null)  System.out.println(prefix + loc + " - " + message);
                else              System.out.println(prefix + " - " + message);
        }
}
