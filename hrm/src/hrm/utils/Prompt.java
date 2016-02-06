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

import java.sql.SQLException;

/**
 * Log output.
 *
 * @author davis
 */
public class Prompt {

        // warning level
        public static final int NORMAL = 1;
        public static final int WARNING = 2;
        public static final int ERROR = 3;

        // coloring schemes
        public static final String ANSI_RESET = "\u001B[0m";
        public static final String ANSI_BLACK = "\u001B[30m";
        public static final String ANSI_RED = "\u001B[31m";
        public static final String ANSI_GREEN = "\u001B[32m";
        public static final String ANSI_YELLOW = "\u001B[33m";
        public static final String ANSI_BLUE = "\u001B[34m";
        public static final String ANSI_PURPLE = "\u001B[35m";
        public static final String ANSI_CYAN = "\u001B[36m";
        public static final String ANSI_WHITE = "\u001B[37m";

        public static void log(int level, String loc, String message) {
                String prefix;
                switch (level) {
                        case NORMAL:
                                prefix = ANSI_GREEN + "Normal:";
                                break;
                        case WARNING:
                                prefix = ANSI_YELLOW + "Warning: ";
                                break;
                        case ERROR:
                                prefix = ANSI_RED + "Error: ";
                                break;
                        default:
                                prefix = "";
                }
                if (loc != null) {
                        System.out.println(prefix + loc + " - " + message + ANSI_RESET);
                } else {
                        System.out.println(prefix + " - " + message + ANSI_RESET);
                }
        }

        public static void log_sql_ex(String loc, SQLException ex) {
                System.out.println(loc + " - SQLException: ");
                do {
                        System.out.println("\t" + loc + " - SQLState:" + ex.getSQLState());
                        System.out.println("\t" + loc + " - Error Code:" + ex.getErrorCode());
                        System.out.println("\t" + loc + " - Message:" + ex.getMessage());
                        Throwable t = ex.getCause();
                        while (t != null) {
                                System.out.println("\t" + loc + " - Cause:" + t);
                                t = t.getCause();
                        }
                        ex = ex.getNextException();
                } while (ex != null);
        }
}
