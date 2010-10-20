/**
 * Object Search Framework
 *
 * Copyright (C) 2010 Julian Klas
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

package com.jklas.search.engine.stemming.snowball;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;

public class TestApp {
    private static void usage()
    {
        System.err.println("Usage: TestApp <algorithm> <input file> [-o <output file>]");
    }

    public static void main(String [] args) throws Throwable {
	if (args.length < 2) {
            usage();
            return;
        }

	Class<?> stemClass = Class.forName("org.tartarus.snowball.ext." +
					args[0] + "Stemmer");
        SnowballStemmer stemmer = (SnowballStemmer) stemClass.newInstance();

	Reader reader;
	reader = new InputStreamReader(new FileInputStream(args[1]));
	reader = new BufferedReader(reader);

	StringBuffer input = new StringBuffer();

        OutputStream outstream;

	if (args.length > 2) {
            if (args.length >= 4 && args[2].equals("-o")) {
                outstream = new FileOutputStream(args[3]);
            } else {
                usage();
                return;
            }
	} else {
	    outstream = System.out;
	}
	Writer output = new OutputStreamWriter(outstream);
	output = new BufferedWriter(output);

	int repeat = 1;
	if (args.length > 4) {
	    repeat = Integer.parseInt(args[4]);
	}

	int character;
	while ((character = reader.read()) != -1) {
	    char ch = (char) character;
	    if (Character.isWhitespace((char) ch)) {
		if (input.length() > 0) {
		    stemmer.setCurrent(input.toString());
		    for (int i = repeat; i != 0; i--) {
			stemmer.internalStemming();
		    }
		    output.write(stemmer.getCurrent());
		    output.write('\n');
		    input.delete(0, input.length());
		}
	    } else {
		input.append(Character.toLowerCase(ch));
	    }
	}
	output.flush();
    }
}
