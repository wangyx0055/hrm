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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;

/**
 * Extract lines from an ASCII stream.
 * @author davis
 */
public class AsciiStream {
        
        private static final int MAX_BUFFERING = 16384;
        
        public static String extract(InputStream source) throws IOException {
                StringWriter writer = new StringWriter();
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(source))) {
			char[] buffer = new char[MAX_BUFFERING];
			int n_bytes = reader.read(buffer, 0, buffer.length);
			while (n_bytes != -1) {
				writer.write(buffer, 0, n_bytes);
				n_bytes = reader.read(buffer, 0, buffer.length);
			}
		}
		return writer.toString();
        }
}
